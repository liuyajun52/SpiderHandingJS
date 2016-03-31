/**
 * 
 */
package cn.blacklighting.sevice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import cn.blacklighting.Main;
import cn.blacklighting.conf.SpiderHttpHeaderConf;
import cn.blacklighting.sevice.ProxyService.ProxyGetExcepction;

/**
 * @author Yajun Liu
 *
 */
public class PageDownService {
	// Vector<String> urlList;
	Reader urlFileReader;
	int threadPoolSize = 30;
	LinkedBlockingQueue<String> urlQueue;
	boolean stopThead = false;
	ExecutorService threadPool;
	ExecutorService readThreadPool;
	ProxyService pool;
	final static int BUFFER_SIZE = 102400 * 8; // 缓存大小为100KB
	OutPutHtml outer = null;
	Pattern charsetPatt=Pattern.compile("<meta[^>]*charset ?= ?([a-z0-9\\-]+)[^>]*>",Pattern.CASE_INSENSITIVE);

	public PageDownService(Reader urlFileReader, OutPutHtml outer) {
		this.urlFileReader = urlFileReader;
		this.outer = outer;
		urlQueue = new LinkedBlockingQueue<String>(threadPoolSize * 5);
		threadPool = Executors.newFixedThreadPool(threadPoolSize,
				new SpiderThreadFactory());
		readThreadPool = Executors.newFixedThreadPool(1, new ReadUrlThreadFactory());
		if (Main.needProxy)
			pool = ProxyService.getInstance();
	}

	public PageDownService(Reader urlFileReader, OutPutHtml outer,
			int threadPoolSize) {
		this.urlFileReader = urlFileReader;
		this.threadPoolSize = threadPoolSize;
		this.urlFileReader = urlFileReader;
		this.outer = outer;
		urlQueue = new LinkedBlockingQueue<String>(threadPoolSize * 5);
		threadPool = Executors.newFixedThreadPool(threadPoolSize,
				new SpiderThreadFactory());
		readThreadPool = Executors.newFixedThreadPool(1, new ReadUrlThreadFactory());
		if (Main.needProxy)
			pool = ProxyService.getInstance();

	}

	public void startSpider() {
		if (Main.needProxy)
			pool.startService();
		readThreadPool.execute(new ReadUrlThread());
		for (int i = 0; i < threadPoolSize; i++) {
			SpiderThread t = new SpiderThread();
			t.setThreadNum(i + 1);
			threadPool.execute(t);
		}
	}

	class ReadUrlThread implements Runnable {

		public void run() {

			String url = null;
			BufferedReader reader = new BufferedReader(urlFileReader);
			try {
				while ((url = reader.readLine()) != null) {
					// 注意这里不能把队列填满，因为如果把队列填满，发生所有消费线程失败回放url，会造成死锁
						while (urlQueue.size() >= 3 * threadPoolSize){
							Thread.sleep(500);
						}
						urlQueue.put(url);
				}

				while (!urlQueue.isEmpty()) {
					Thread.sleep(1000);
				}
				stopThead = true;
				threadPool.shutdown();
				FileWriteService.closeOut();
				if(Main.needProxy){
					ProxyService.getInstance().stopGetProxyService();
				}
				readThreadPool.shutdown();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	class SpiderThread implements Runnable {
		int threadNum;
		public void run() {
			String url = null;
			HttpHost p = null;
			InputStream input;
			CloseableHttpResponse response = null;
			CloseableHttpClient httpclient = HttpClients.createDefault();
			while ((!stopThead) || (!urlQueue.isEmpty())) {
				try {
					url = urlQueue.poll(1, TimeUnit.MINUTES);

//					System.out.println("get Url "+url);
					if (url == null) {
						continue;
					}

					if (Main.needProxy) {
						try {				
//							System.out.println("wait proxy "+pool.getProxyRemainCount()+" "+urlQueue.size());
							p = pool.getProxy();
//							System.out.println("get proxy");
						} catch (ProxyGetExcepction e) {
							System.err.println("Spider Thread " + threadNum
									+ " getProxy Fail!!!");
							throw e;
						}
					}

					HttpHost target = new HttpHost(url);
					RequestConfig config = null;
					Builder builder = RequestConfig.custom().setConnectionRequestTimeout(3000).setSocketTimeout(3000).setConnectTimeout(1000);
					
					builder.setProxy(new HttpHost("localhost",8888));
					
					if (Main.needProxy) {
						config = builder.setProxy(p).build();
					} else {
						config = builder.build();
					}
					HttpGet request = new HttpGet(url);
					request.addHeader("Accept", SpiderHttpHeaderConf
							.getInstance().getHeaderByKey("Accept"));
					request.addHeader("Connection", SpiderHttpHeaderConf
							.getInstance().getHeaderByKey("Connection"));
					request.addHeader("User-Agent", SpiderHttpHeaderConf
							.getInstance().getHeaderByKey("User-Agent"));
					request.addHeader("Accept-Encoding", SpiderHttpHeaderConf
							.getInstance().getHeaderByKey("Accept-Encoding"));
					request.addHeader("Accept-Language", SpiderHttpHeaderConf
							.getInstance().getHeaderByKey("Accept-Language"));
					request.setConfig(config);
					if (Main.needProxy){
						response = httpclient.execute(target, request);
					}else{
						response = httpclient.execute(request);
					}
					int reCode=response.getStatusLine().getStatusCode();
					if (reCode == 200) {

						HttpEntity entity = response.getEntity();
						
						if (entity == null) {
							throw new ClientProtocolException(
									"Response contains no content");
						}
//						Header[] allHeaders = response.getAllHeaders();
						// 编码识别
						String charsetStr = null;
						boolean useDefaultEncoing=true;
						Header charsetHeader = entity.getContentEncoding();
						if (charsetHeader != null) {
							charsetStr = charsetHeader.getValue();
						}
						
						ContentType cType=ContentType.get(entity);
						Charset charset = cType.getCharset();
						if(charset!=null){
							charsetStr=charset.name();
						}
						//默认编码
						if (charsetStr == null) {
							charsetStr = "utf-8";
						}else{
							useDefaultEncoing=false;
						}

						input = entity.getContent();
						byte[] htmlBytes=IOUtils.toByteArray(input);
						String html = new String(htmlBytes,charsetStr);
						//如果使用默认编码，在html文档中寻找编码信息，如果找到重新解码
						if(useDefaultEncoing){
							Matcher matcher = charsetPatt.matcher(html);
							if(matcher.find()){
								charsetStr=matcher.group(1);
								html=new String(htmlBytes,charsetStr);
							}
						}
						
						html = html.replace('\n', ' ').replace('\t', ' ')
								.replace('\r', ' ');
						if (Main.needProxy) {
							pool.reputProxy(p);
						}
						if(!html.trim().isEmpty()){
//							System.out.println("success url "+url);
							outer.writeHtml(url, html);
						}else{
							throw new Exception(url+" empty html!!!");
						}
					} 
				} catch (Exception e) {
					// e.printStackTrace();
					try {
						urlQueue.put(url);
					} catch (InterruptedException e1) {
					}
				}finally{
					try {
						if(response!=null){
							response.close();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		public void setThreadNum(int threadNum) {
			this.threadNum = threadNum;
		}

	}

	class SpiderThreadFactory implements ThreadFactory {

		public Thread newThread(Runnable r) {
			return new Thread(new SpiderThread());
		}

	}
	
	class ReadUrlThreadFactory implements ThreadFactory{

		public Thread newThread(Runnable r) {
			return new Thread(new ReadUrlThread());
		}
		
	}

	public interface OutPutHtml {
		void writeHtml(String url, String html);
	}

}
