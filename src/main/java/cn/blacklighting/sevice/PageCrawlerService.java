package cn.blacklighting.sevice;

import cn.blacklighting.conf.HttpHeaderConf;
import cn.blacklighting.dao.LinkDao;
import cn.blacklighting.dao.UrlDao;
import cn.blacklighting.models.LinkEntity;
import cn.blacklighting.models.UrlEntity;
import cn.blacklighting.sevice.serviceinterface.HtmlWriter;
import cn.blacklighting.sevice.serviceinterface.Scheduler;
import cn.blacklighting.sevice.serviceinterface.UrlDistributer;
import cn.blacklighting.util.CrawlerUtil;
import org.apache.any23.encoding.TikaEncodingDetector;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zybang on 2016/4/5.
 */
public class PageCrawlerService {
    private static Logger logger= LogManager.getRootLogger();

    public static final String DEFAULT_PAGE_ENCODE = "utf-8";
    public static final int HEAT_BEAT_GAT=1*1000;
    public static final Pattern charsetPatt
            = Pattern.compile("<meta[^>]*charset ?= ?([a-z0-9\\-]+)[^>]*>", Pattern.CASE_INSENSITIVE);
    public static final Pattern urlPatt =
            Pattern.compile("^(?:(?:https?|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?!10(?:\\.\\d{1,3}){3})" +
                    "(?!127(?:\\.\\d{1,3}){3})(?!169\\.254(?:\\.\\d{1,3}){2})(?!192\\.168" +
                    "(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})" +
                    "(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5]))" +
                    "{2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\x{00a1}-\\x{ffff}0-9]+-?)" +
                    "*[a-z\\x{00a1}-\\x{ffff}0-9]+)(?:\\.(?:[a-z\\x{00a1}-\\x{ffff}0-9]+-?)" +
                    "*[a-z\\x{00a1}-\\x{ffff}0-9]+)*(?:\\.(?:[a-z\\x{00a1}-\\x{ffff}]{2,})))(?::\\d{2,5})?(?:/[^\\s]*)?$");

    private Scheduler scheduler;
    private UrlDistributer urlDistributer;
    private HtmlWriter htmlWriter;
    private ExecutorService crawlerPool;
    private ScheduledExecutorService heatBeatPool;
    private int threadPoolSize = 50;
    private AtomicBoolean shutDown;
    private DBService db;
    private UrlDao urlDao;
    private LinkDao linkDao;
    private Scheduler.PageCrawlerInfo info;
    private AtomicInteger crawlerSum=new AtomicInteger(1);
    private AtomicInteger crawlerNum=new AtomicInteger(0);

    public PageCrawlerService(Scheduler scheduler,UrlDistributer urlDistributer, HtmlWriter htmlWriter) throws UnknownHostException {
        this.scheduler=scheduler;
        this.urlDistributer = urlDistributer;
        this.htmlWriter = htmlWriter;
        this.shutDown = new AtomicBoolean(false);
        this.db = DBService.getInstance();
        this.urlDao = new UrlDao();
        this.linkDao = new LinkDao();
        this.info=new Scheduler.PageCrawlerInfo();
        info.id=CrawlerUtil.getLocalIP();
        info.rmiUrl=CrawlerUtil.getLocalIP();
    }

    public void start() throws RemoteException {
        parseSedulerReturnInfo(scheduler.registerPageCrawler(info));
        heatBeatPool=Executors.newSingleThreadScheduledExecutor();
//        heatBeatPool.scheduleAtFixedRate(new HeatBeatThread(),HEAT_BEAT_GAT,HEAT_BEAT_GAT,TimeUnit.MILLISECONDS);
        new Thread(new HeatBeatThread()).start();
        crawlerPool = Executors.newFixedThreadPool(threadPoolSize);
        for (int i = 0; i < threadPoolSize; i++) {
            crawlerPool.execute(new CrawlerThread());
        }
        logger.info("CrawlingService Start with " + threadPoolSize + " threads");
    }

    public void stop() {
        shutDown.set(true);
        heatBeatPool.shutdown();
        crawlerPool.shutdown();
        logger.info("CrawlingService shut down");
    }

    private class CrawlerThread implements Runnable {

        HttpClient client = null;
        HttpResponse response = null;
        HtmlCleaner htmlCleaner = null;
        TikaEncodingDetector detector = null;

        public CrawlerThread() {
            client = HttpClients.createDefault();
            htmlCleaner = new HtmlCleaner();
            detector = new TikaEncodingDetector();
        }

        public void run() {
            while (!shutDown.get()) {
                UrlEntity urlEntity = urlDistributer.getNextUrl();
                String url = urlEntity.getUrl();
                HttpGet request = new HttpGet(url);
                request = setHttpRequestHeader(request);
                InputStream input = null;
                try {
                    response = client.execute(request);
                    int reCode = response.getStatusLine().getStatusCode();
                    if (reCode == 200) {
                        HttpEntity entity = response.getEntity();

                        if (entity == null) {
                            urlDao.updateUrlStatus(urlEntity, UrlEntity.STATUS_NO_CONTENT);
                            logger.debug("NO_CONTENT URL:" + url);
                            continue;
                        }

                        //获取内容类型&编码识别
                        String charsetStr = DEFAULT_PAGE_ENCODE;
                        boolean useDefaultEncoing = true;

                        ContentType cType = ContentType.get(entity);
                        String mimeType = cType.getMimeType();
                        if (!mimeType.toLowerCase().contains("text")) {
                            urlDao.updateUrlStatus(urlEntity, UrlEntity.STATUS_UNNEED);
                            continue;
                        }
                        Charset charset = cType.getCharset();
                        input = entity.getContent();
                        if (charset != null) {
                            charsetStr = charset.name();
                            useDefaultEncoing = false;
                        }
                        byte[] htmlBytes = IOUtils.toByteArray(input);

                        String html = new String(htmlBytes, charsetStr);
                        //如果使用默认编码，在html文档中寻找编码信息，如果找到重新解码
                        if (useDefaultEncoing) {
                            Matcher matcher = charsetPatt.matcher(html);
                            if (matcher.find()) {
                                charsetStr = matcher.group(1);
                                html = new String(htmlBytes, charsetStr);
                            } else {
                                //最后启用编码识别
                                charsetStr = detector.guessEncoding(input);
                                html = new String(htmlBytes, charsetStr);
                            }
                        }

                        htmlWriter.writeHtml(urlEntity, html.getBytes(DEFAULT_PAGE_ENCODE));
                        TagNode nodes = htmlCleaner.clean(html);
                        List<TagNode> as = nodes.getElementListByName("a", true);
                        int linkAmount = 0;
                        for (TagNode n : as) {
                            String href = n.getAttributeByName("href");
                            href=CrawlerUtil.getFormatUrl(href,urlEntity.getDomain());
                            if (href != null&&urlPatt.matcher(href).matches()) {
                                String text = n.getText().toString().trim();
                                linkAmount++;

                                String md5 = CrawlerUtil.md5(href.toLowerCase());
                                Session s=db.getSession();
                                s.beginTransaction();
                                Query query= s.createSQLQuery("INSERT INTO url (url,md5,status,domain,is_seed," +
                                        "to_link_amount) VALUE (:url,:md5,:stat,:domain,:is_seed,:to_link_amount) " +
                                        "ON DUPLICATE KEY UPDATE to_link_amount=to_link_amount+1");
                                query.setParameter("url",href);
                                query.setParameter("md5",md5);
                                query.setParameter("domain",CrawlerUtil.getDomainName((href)));
                                query.setParameter("stat",UrlEntity.STATUS_NEW);
                                query.setParameter("is_seed",0);
                                query.setParameter("to_link_amount",1);
                                query.executeUpdate();
//                                UrlEntity result = (UrlEntity) query.uniqueResult();

//                                if (result != null) {
//                                    result.setToLinkAmount(result.getToLinkAmount() + 1);
//                                    s.merge(result);
//                                } else {
//                                    result = new UrlEntity();
//                                    result.setUrl(href);
//                                    result.setToLinkAmount(1);
//                                    result.setStatus(0);
//                                    result.setDomain(CrawlerUtil.getDomainName((href)));
//                                    result.setIsSeed((byte) 1);
//                                    result.setMd5(md5);
//                                    s.save(result);
//                                }
                                s.getTransaction().commit();
                                s.close();

                                LinkEntity link = new LinkEntity();

                                link.setFromId(urlEntity.getId());
                                link.setToId(urlEntity.getId());

                                link.setFromUrl(urlEntity.getUrl());
                                link.setToUrl(href);
                                link.setText(text);
                                linkDao.add(link);
                            }
                        }
                        logger.debug("URL SUCCESS " + url + " LINK " + linkAmount);
                    } else {
                        urlDao.updateUrlStatus(urlEntity, UrlEntity.STATUS_FAILED);
                        logger.debug("URL FAIL " + url);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    urlDao.updateUrlStatus(urlEntity, UrlEntity.STATUS_FAILED);
                    logger.warn("Error in crawling thread ", e);
                } finally {
                    IOUtils.closeQuietly(input);
                    HttpClientUtils.closeQuietly(response);
                }

            }
        }

    }

    class HeatBeatThread implements Runnable{

        @Override
        public void run() {
            while(true){
                try {
                    String re=scheduler.pageCrawlerHeatBeat(info);
                    logger.debug("Heat Beat :"+re);
                    parseSedulerReturnInfo(re);
                    Thread.sleep(HEAT_BEAT_GAT);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void parseSedulerReturnInfo(String re){
        String[] infos=re.split(":");
        if(infos.length==2){
            crawlerSum.set(Integer.parseInt(infos[0]));
            crawlerNum.set(Integer.parseInt(infos[1]));
        }
    }

    private HttpGet setHttpRequestHeader(
            HttpGet request) {
        request.addHeader("Accept", HttpHeaderConf
                .getInstance().getHeaderByKey("Accept"));
        request.addHeader("Connection", HttpHeaderConf
                .getInstance().getHeaderByKey("Connection"));
        request.addHeader("User-Agent", HttpHeaderConf
                .getInstance().getHeaderByKey("User-Agent"));
        request.addHeader("Accept-Encoding", HttpHeaderConf
                .getInstance().getHeaderByKey("Accept-Encoding"));
        request.addHeader("Accept-Language", HttpHeaderConf
                .getInstance().getHeaderByKey("Accept-Language"));
        return request;
    }

}
