package cn.blacklighting.sevice;

import cn.blacklighting.entity.UrlEntity;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by zybang on 2016/4/1.
 */
public class DBUrlDistributer implements UrlDistributionInterface {

    static org.apache.log4j.Logger logger= Logger.getLogger(DBService.class);

    private DBService db;
    private LinkedBlockingQueue<UrlEntity> urlQueue;
    private int cheekSeedTimeInterval =3*60*60;//每三小时检查一次Seed
    private int seedQueueMaxLen=1000;
    private int urlQueueMaxLen=10000;

    public DBUrlDistributer(){
        logger.info("Spider url distributer is set to DBUrlDistributer");
        db=DBService.getInstance();
        urlQueue=new LinkedBlockingQueue<UrlEntity>(urlQueueMaxLen);
        init();
    }

    public void init(){

        //设置定时调度任务去获取新的种子URL
        ScheduledExecutorService service = Executors
                .newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new Runnable() {
            public void run() {
                fetchNewSeed();
            }
        },0,cheekSeedTimeInterval,TimeUnit.SECONDS);


    }

    private void fetchNewSeed(){
        logger.info("Begin to fetch new seed");
        Session s=db.getSession();
        s.beginTransaction();
//        List seeds=s.createQuery("from urlseed where status=0 limit "+seedQueueMaxLen).list();
//        int seedSum=0;
//        for (UrlseedEntity sn :(List<UrlseedEntity>)seeds){
//            seeds.put(sn);
//            sn.setStatus(1);
//            s.save(sn);
//            seedSum++;
//        }
        s.getTransaction().commit();
        logger.info("New seeds fetched : "+seedSum);
        s.close();
    }

    public String getNextUrl() {
        return null;
    }
}
