package cn.blacklighting.sevice;

import cn.blacklighting.conf.SpiderHttpHeaderConf;
import cn.blacklighting.entity.UrlEntity;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zybang on 2016/4/5.
 */
public class PageCrawlingService {

    public static final String DEFAULT_PAGE_ENCODE="utf-8";
    private static org.apache.log4j.Logger logger = Logger.getLogger(PageCrawlingService.class);
    Pattern charsetPatt=Pattern.compile("<meta[^>]*charset ?= ?([a-z0-9\\-]+)[^>]*>",Pattern.CASE_INSENSITIVE);
    Pattern urlPatt=Pattern.compile("");
    private UrlDistributer urlDistributer;
    private HtmlWriter htmlWriter;
    private ExecutorService crawlerPool;
    private int threadPoolSize=1;
    private AtomicBoolean shutDown;

    public PageCrawlingService(UrlDistributer urlDistributer,HtmlWriter htmlWriter){
        this.urlDistributer=urlDistributer;
        this.htmlWriter=htmlWriter;
        this.shutDown=new AtomicBoolean(false);
    }


    public void start(){
        crawlerPool= Executors.newFixedThreadPool(threadPoolSize);
        for (int i = 0; i <threadPoolSize ; i++) {
            crawlerPool.execute(new CrawlingThread());
        }
        logger.info("CrawlingService Start with "+threadPoolSize+" threads");
    }

    public void stop(){
        shutDown.set(true);
        crawlerPool.shutdown();
        logger.info("CrawlingService shut down");
    }

    private class CrawlingThread implements Runnable{

        HttpClient client= null;
        HttpResponse response=null;
        HtmlCleaner htmlCleaner=null;
        public CrawlingThread(){
            client=HttpClients.createDefault();
            htmlCleaner= new HtmlCleaner();
        }

        public void run() {
            while(!shutDown.get()){
                UrlEntity urlEntity=urlDistributer.getNextUrl();
                String url=urlEntity.getUrl();
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
                InputStream input=null;
                try {
                    response=client.execute(request);
                    int reCode=response.getStatusLine().getStatusCode();
                    if (reCode == 200) {
                        HttpEntity entity = response.getEntity();
                        if (entity == null) {
                            logger.warn("NO_CONTENT URL:"+url);
                        }

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
                        byte[] htmlBytes= IOUtils.toByteArray(input);

                        String html = new String(htmlBytes,charsetStr);
                        //如果使用默认编码，在html文档中寻找编码信息，如果找到重新解码
                        if(useDefaultEncoing){
                            Matcher matcher = charsetPatt.matcher(html);
                            if(matcher.find()){
                                charsetStr=matcher.group(1);
                                html=new String(htmlBytes,charsetStr);
                            }
                        }

                        htmlWriter.writeHtml(urlEntity,html.getBytes(DEFAULT_PAGE_ENCODE));
                        TagNode nodes=htmlCleaner.clean(html);
                        List<TagNode> as = nodes.getElementListByName("a", true);
                        for(TagNode n:as){
                            n.getAttributeByName("ref");

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("Error in crawling thread ",e);
                }finally {
                    IOUtils.closeQuietly(input);
                    HttpClientUtils.closeQuietly(response);
                }

            }
        }

    }

}
