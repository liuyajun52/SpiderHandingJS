/**
 * 
 */
package cn.blacklighting.sevice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import cn.blacklighting.Main;

/**
 * 代理池服务 <br/>
 * Create Time :2015年5月9日/下午4:09:28<br/>
 * Last Modified Time:2015年5月9日/下午4:09:28<br/>

 * @author Yajun Liu <br/>
 */
public class ProxyService {

	public static int CHECK_PROXY_THEAD_SUM = 20;
	public static final String CHECK_PROXY_URL = "http://www.baidu.com";
	/**
	 * 服务器返回的代理队列，需要验证
	 */
	LinkedBlockingQueue<HttpHost> proxyQueue;
	/**
	 * 验证过的代理队列
	 */
	LinkedBlockingQueue<HttpHost> proxyCanUseQueue;


	private static ProxyService pool = null;

	boolean serviceStoped = false;
	GetNewProxyThread newProxyThread;

	ExecutorService checkProxyThreadPool;
	ExecutorService newProxyThreadPool;
	ExecutorService reputProxyThreadPool;
	
	File cacheFile=null;
	OutputStreamWriter cacheWrite=null;
	
	HashSet<String> cacheSet=null;

	private ProxyService() {
		CHECK_PROXY_THEAD_SUM = Main.THRED_POOL_SIZE / 4 > 20 ? Main.THRED_POOL_SIZE / 4
				: 20;
		proxyQueue = new LinkedBlockingQueue<HttpHost>(
				CHECK_PROXY_THEAD_SUM * (Main.THRED_POOL_SIZE+CHECK_PROXY_THEAD_SUM)*10);
		proxyCanUseQueue = new LinkedBlockingQueue<HttpHost>(
				CHECK_PROXY_THEAD_SUM * (Main.THRED_POOL_SIZE+CHECK_PROXY_THEAD_SUM)*4);
		
		checkProxyThreadPool = Executors.newFixedThreadPool(
				CHECK_PROXY_THEAD_SUM, new ProxyCheckThreadFactory());
		newProxyThreadPool = Executors.newFixedThreadPool(1,
				new GetNewProxyThreadFactory());
		reputProxyThreadPool= Executors.newFixedThreadPool(CHECK_PROXY_THEAD_SUM/4>4?CHECK_PROXY_THEAD_SUM/4:4);
		
		cacheFile=new File("cache.txt");
		cacheSet=new HashSet<String>();
		try {
			if((!cacheFile.exists())&&(!cacheFile.createNewFile())){
				System.err.println("can not create proxy File!!!");
			}else{
				cacheWrite=new FileWriter(cacheFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ProxyService getInstance() {
		if (pool == null) {
			pool = new ProxyService();
		}
		return pool;
	}

	public void startService() {
		newProxyThread = new GetNewProxyThread();
		newProxyThreadPool.execute(newProxyThread);
		for (int i = 0; i < CHECK_PROXY_THEAD_SUM; i++) {
			checkProxyThreadPool.execute(new ProxyCheckThread());
		}
	}

	/**
	 * 获取代理
	 * 
	 * @return
	 * @throws ProxyGetExcepction
	 */
	public HttpHost getProxy() throws ProxyGetExcepction {
		HttpHost p = null;

		try {
			p = proxyCanUseQueue.poll(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (p == null) {
			throw new ProxyGetExcepction(
					"Can not get Proxy from ProxyService!!!");
		}
		return p;
	}

	/**
	 * 使用代理成功后放回代理池
	 * 
	 * @param p
	 */
	public void reputProxy(final HttpHost p) {	
		reputProxyThreadPool.execute(new Runnable() {
			public void run() {
				try {
					proxyCanUseQueue.offer(p, 30, TimeUnit.MINUTES);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * 获取代理池中可用代理的数目
	 * @return
	 */
	public int getProxyRemainCount(){
		return proxyCanUseQueue.size();
	}

	public void stopGetProxyService() {
		this.serviceStoped = true;
		newProxyThreadPool.shutdown();
		checkProxyThreadPool.shutdown();
	}

	/**
	 * 从服务器获取代理的线程 <br/>
	 * Create Time :2015年5月9日/下午12:16:36<br/>
	 * Last Modified Time:2015年5月9日/下午12:16:36<br/>
	 * Last Modified By :liuyajun01
	 * 
	 * @version
	 * @author liuyajun01 liuyajun01@baidu.com<br/>
	 * @Copyright 2015 Baidu Inc. All rights reserved. <br/>
	 */
	class GetNewProxyThread extends Thread {

		@Override
		public void run() {
			super.run();
			while (!serviceStoped) {
				int reProxySum = 0;
				try {
					while(proxyCanUseQueue.size()>Main.THRED_POOL_SIZE/2){
						sleep(10*1000);
					}
					URL proxyUrl = new URL(
							"http://51httpdaili.com/api.asp?dd=847568011050828&tqsl=1000&ports=18186&ports=8080&ports=9999&ports=8088&ports=80&qt=1&cf=1");
					HttpURLConnection connection = (HttpURLConnection) proxyUrl
							.openConnection();
					connection.connect();

					int responseCode = connection.getResponseCode();

					if (responseCode >= 200 && responseCode < 300) {
						BufferedReader in = new BufferedReader(
								new InputStreamReader(
										connection.getInputStream()));
						String line;
						while ((line = in.readLine()) != null) {
							String[] pArr = line.split(":");
							if (pArr.length == 2) {
								if(cacheWrite!=null){
									cacheWrite.append(line+"\n");
								}
								HttpHost p = new HttpHost(pArr[0],
										Integer.parseInt(pArr[1]));
								proxyQueue.put(p);
								reProxySum++;
							}
						}
						if(cacheWrite!=null){
							cacheWrite.flush();
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(5 * 1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {

					if (reProxySum == 0 && proxyCanUseQueue.size()<30) {
						System.err.println("Load proxy Fail !!! sleep 10 seconds");
						Thread.sleep(10 * 1000);
						if (cacheFile.exists()) {
							BufferedReader reader = new BufferedReader(
									new InputStreamReader(new FileInputStream(
											cacheFile)));
							String line;
							while ((line = reader.readLine()) != null) {
								cacheSet.add(line);
							}
							cacheFile.delete();
							cacheFile.createNewFile();
							cacheWrite=new FileWriter(cacheFile);
							for(String l:cacheSet){
								cacheWrite.write(l+"\n");
								String[] pArr = l.split(":");
								if (pArr.length == 2) {
									HttpHost p = new HttpHost(pArr[0],
											Integer.parseInt(pArr[1]));
									proxyQueue.put(p);
									reProxySum++;
								}
							}
							cacheWrite.flush();
							reader.close();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

	}

	/**
	 * 代理验证线程 <br/>
	 * Create Time :2015年5月9日/下午12:17:00<br/>
	 * Last Modified Time:2015年5月9日/下午12:17:00<br/>
	 * Last Modified By :liuyajun01
	 * 
	 * @version
	 * @author liuyajun01 liuyajun01@baidu.com<br/>
	 * @Copyright 2015 Baidu Inc. All rights reserved. <br/>
	 */
	class ProxyCheckThread implements Runnable {
		CloseableHttpResponse response = null;
		CloseableHttpClient httpclient = null;

		public ProxyCheckThread() {
			httpclient = HttpClients.createDefault();
		}

		public void run() {
			while (!serviceStoped) {
				try {
					HttpHost p = proxyQueue.take();
					HttpHost target = new HttpHost(CHECK_PROXY_URL);
					RequestConfig config = RequestConfig.custom().setProxy(p)
							.build();
					HttpGet request = new HttpGet(CHECK_PROXY_URL);

					request.setConfig(config);
					response = httpclient.execute(target, request);
					proxyCanUseQueue.put(p);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
				} catch (IOException e) {
				}
			}
		}

	}

	class GetNewProxyThreadFactory implements ThreadFactory {

		public Thread newThread(Runnable r) {
			return new Thread(new GetNewProxyThread());
		}

	}

	class ProxyCheckThreadFactory implements ThreadFactory {

		public Thread newThread(Runnable r) {
			return new Thread(new ProxyCheckThread());
		}

	}

	public class ProxyGetExcepction extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ProxyGetExcepction(String msg) {
			super(msg);
		}
	}
}
