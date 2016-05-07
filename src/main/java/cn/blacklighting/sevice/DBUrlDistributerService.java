package cn.blacklighting.sevice;

import cn.blacklighting.dao.UrlDao;
import cn.blacklighting.models.UrlEntity;
import cn.blacklighting.sevice.serviceinterface.UrlDistributer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * url 分发器，使用MYSQL作为数据源
 * Created by zybang on 2016/4/1.
 */
public class DBUrlDistributerService implements UrlDistributer {

    private static Logger logger = LogManager.getRootLogger();

    private DBService db;
    private final LinkedBlockingQueue<UrlEntity> urlQueue;
    private int urlQueueMaxLen = 1000;
    private AtomicBoolean fethNewUrl;
    private UrlDao urlDao;

    public DBUrlDistributerService() {
        logger.info("Spider url distributer is set to DBUrlDistributerService");
        db = DBService.getInstance();
        urlDao = new UrlDao();
        urlQueue = new LinkedBlockingQueue<UrlEntity>(urlQueueMaxLen);
        fethNewUrl = new AtomicBoolean(true);
        init();
    }

    private void init() {

        //设置定时调度任务去获取新的种子URL
        ScheduledExecutorService service = Executors
                .newSingleThreadScheduledExecutor();
        service.execute(new FetchNewUrl());
    }

    private class FetchNewUrl implements Runnable {

        public void run() {
            while (fethNewUrl.get()) {
                int seedSum = 0;
                synchronized (urlQueue) {
                    while (urlQueue.size() > urlQueueMaxLen / 2) {
                        try {
//                            logger.debug("URLQUEUE SIZE IS LARGER THEN 5000");
                            urlQueue.wait(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            logger.fatal("Error on fetch new urls waiting urlQueue", e);
                        }
                    }
                }
                logger.info("Begin to fetch new urls "+urlQueue.size());
                Session s = db.getSession();
                Transaction transaction = s.beginTransaction();
                try {
                    //获取新的url 保证种子URL被先抓取
                    List<UrlEntity> urls = s.createQuery("from UrlEntity where status =0 order by weight desc")
                            .setMaxResults(urlQueueMaxLen - urlQueue.size()).list();
                    transaction.commit();
                    s.close();
                    for (UrlEntity sn : (List<UrlEntity>) urls) {
                        urlQueue.put(sn);
                        urlDao.updateUrlStatus(sn,UrlEntity.STATUS_IN_QUEUE);
                        seedSum++;
                    }
                    logger.info("New urls fetched : " + seedSum);

                } catch (Exception e) {
                    //只是将URL队列时候发生了异常，不必回滚
                    logger.fatal("Error on fetch new url", e);
                    e.printStackTrace();
                }
                if (seedSum == 0) {
                    try {
						Thread.sleep(10 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public UrlEntity getNextUrl() {
        synchronized (urlQueue){
            urlQueue.notifyAll();
        }
        UrlEntity url = null;

        try {
            logger.debug("Begin to get new url"+urlQueue.size());
            url = urlQueue.take();
            logger.debug("New url get "+url);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return url;
    }

    public void setUrlFail(UrlEntity url) {
        Session s = db.getSession();
        s.beginTransaction();
        url.setStatus(0);
        url.setRetryTime(url.getRetryTime() + 1);
        s.save(url);
        s.getTransaction().commit();
        s.close();
    }

    public void stopFetchNewUrl() {
        fethNewUrl.set(false);
    }

}
