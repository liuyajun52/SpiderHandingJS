package cn.blacklighting.sevice;

import cn.blacklighting.entity.UrlEntity;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**url 分发器，使用MYSQL作为数据源
 * Created by zybang on 2016/4/1.
 */
public class DBUrlDistributer implements UrlDistributer {

    private static org.apache.log4j.Logger logger= Logger.getLogger(DBUrlDistributer.class);

    private DBService db;
    private final LinkedBlockingQueue<UrlEntity> urlQueue;
    private int urlQueueMaxLen=10000;
    private AtomicBoolean fethNewUrl;

    public DBUrlDistributer(){
        logger.info("Spider url distributer is set to DBUrlDistributer");
        db=DBService.getInstance();
        urlQueue=new LinkedBlockingQueue<UrlEntity>(urlQueueMaxLen);
        fethNewUrl=new AtomicBoolean(true);
        init();
    }

    private void init(){

        //设置定时调度任务去获取新的种子URL
        ScheduledExecutorService service = Executors
                .newSingleThreadScheduledExecutor();
        service.execute(new FetchNewUrl());
    }

    private void fetchNewUrl(){
        while (urlQueue.size() > urlQueueMaxLen / 2) {
            synchronized (urlQueue) {
                try {
                    urlQueue.wait(60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    logger.fatal("Error on fetch new url wating urlQueue",e);
                }
            }
        }
        if(urlQueue.size()==urlQueueMaxLen){
            logger.info("URLQueue is full");
            return;
        }
        logger.info("Begin to fetch new seed");
        Session s=db.getSession();
        s.beginTransaction();
        try {
            //获取新的url 保证种子URL被先抓取
            List urls=s.createQuery("from UrlEntity where status =0 order by isSeed desc")
                    .setMaxResults(urlQueueMaxLen-urlQueue.size()).list();
            int seedSum=0;
            for (UrlEntity sn :(List<UrlEntity>)urls){

                urlQueue.put(sn);
                sn.setStatus((byte)1);
                s.update(sn);
                seedSum++;
            }
            s.getTransaction().commit();
            logger.info("New seeds fetched : "+seedSum);

        } catch (InterruptedException e) {
            logger.fatal("Error on fetch new seeds or url",e);
            e.printStackTrace();
        }finally {
            //只是将URL队列时候发生了异常，不必回滚
            s.getTransaction().commit();
            s.close();
        }

    }

    private class FetchNewUrl implements Runnable{

        public void run() {
            while(fethNewUrl.get()){
                fetchNewUrl();
            }
        }
    }

    public UrlEntity getNextUrl() {
        synchronized (this){
            if(urlQueue.isEmpty()){
                try {
                    this.wait(60*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    logger.fatal("Error on getNewUrl waiting  on Object",e);
                }
            }
        }
        UrlEntity url=null;
        synchronized (urlQueue){
            if(urlQueue.size()<urlQueueMaxLen){
                //唤醒取url线程去取新的URL
                urlQueue.notifyAll();
            }
            //此时队列不可能为空，使用poll也是安全的
            url=urlQueue.poll();
        }
        return url;
    }

    public void setUrlFail(UrlEntity url) {
        Session s=db.getSession();
        s.beginTransaction();
        url.setStatus((byte)0);
        url.setRetryTime(url.getRetryTime()+1);
        s.save(url);
        s.getTransaction().commit();
        s.close();
    }

    public void stopFetchNewUrl(){
        fethNewUrl.set(false);
    }

}
