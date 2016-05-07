package cn.blacklighting.sevice;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.blacklighting.sevice.serviceinterface.Scheduler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Created by zybang on 2016/4/21.
 */
public class SchedulerService extends UnicastRemoteObject implements Scheduler {
    private static Logger logger=LogManager.getRootLogger();
	public static final long MAX_HEAT_BEAT_GAP=3*1000;
	public static final long CLEAN_THREAD_GAP=1*1000;
    LinkedHashMap<String,PageCrawlerInfo> crawlerInfoSortedMap;

    public SchedulerService() throws RemoteException {
        crawlerInfoSortedMap=new LinkedHashMap<>();
        new CleanCrawlerMapThread().start();
    }

	@Override
	public String registerUrlDistributer() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String registerHtmlWriter() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String registerPageCrawler(PageCrawlerInfo info) throws RemoteException {
		info.lastHeatBeatTime=Calendar.getInstance().getTimeInMillis();
		Collections.synchronizedMap(crawlerInfoSortedMap).put(info.rmiUrl, info);
        info.crawlerNumber=crawlerInfoSortedMap.size()-1;
		logger.info("PageCrawler Registed :"+info.rmiUrl + " CrawlerMap size :"+crawlerInfoSortedMap.size());
		return String.format("%d:%d", crawlerInfoSortedMap.size(),crawlerInfoSortedMap.size()-1);
	}

	@Override
	public String pageCrawlerHeatBeat(PageCrawlerInfo info) throws RemoteException{
        info.lastHeatBeatTime=Calendar.getInstance().getTimeInMillis();
        Map<String,PageCrawlerInfo> m=Collections.synchronizedMap(crawlerInfoSortedMap);
        if(m.containsKey(info.rmiUrl)){
            info.crawlerNumber=m.get(info.rmiUrl).crawlerNumber;
            m.put(info.rmiUrl,info);
            logger.info("PageCrawler HeatBeat :"+info.rmiUrl + " CrawlerMap size :"+crawlerInfoSortedMap.size());
            return String.format("%d:%d",crawlerInfoSortedMap.size(),info.crawlerNumber);
        }else{
            logger.info("PageCrawler Return Online :"+info.rmiUrl + " CrawlerMap size :"+crawlerInfoSortedMap.size());
            return registerPageCrawler(info);
        }
	}
	
	class CleanCrawlerMapThread extends Thread{
		
		@Override
		public void run() {
			super.run();
			while(true){
				Calendar time=Calendar.getInstance();
				Map<String, PageCrawlerInfo> m=Collections.synchronizedMap(crawlerInfoSortedMap);
				Object[] keysArr=m.keySet().toArray();
                int crawlerNum=0;
				synchronized (m) {
					for(int i=0;i>keysArr.length;i++){
						long now=time.getTimeInMillis();
						PageCrawlerInfo info=m.get(keysArr[i]);
						if(now-info.lastHeatBeatTime>MAX_HEAT_BEAT_GAP){
							m.remove(keysArr[i]);
							logger.info("Crawler offline :"+keysArr[i]);
						}else{
                            info.crawlerNumber=crawlerNum;
                            m.put(keysArr[i].toString(),info);
                            crawlerNum++;
							break;
						}
					}
				}
				try {
					Thread.sleep(CLEAN_THREAD_GAP);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
