package cn.blacklighting.sevice;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import cn.blacklighting.sevice.Scheduler.PageCrawlerInfo;

/**
 * Created by zybang on 2016/4/21.
 */
public class SchedulerImpl extends UnicastRemoteObject implements Scheduler {
    private static Logger logger=LogManager.getRootLogger();
	public static final long MAX_HEAT_BEAT_GAP=3*1000;
	
    LinkedHashMap<String,PageCrawlerInfo> crawlerInfoSortedMap;

    public SchedulerImpl() throws RemoteException {
        crawlerInfoSortedMap=new LinkedHashMap<>();
        
    }

	@Override
	public void registerUrlDistributer() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerHtmlWriter() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String registerPageCrawler(PageCrawlerInfo info) throws RemoteException {
		info.lastHeatBeatTime=Calendar.getInstance().getTimeInMillis();
		Collections.synchronizedMap(crawlerInfoSortedMap).put(info.rmiUrl, info);
		logger.info("PageCrawler Registed :"+info.rmiUrl + " CrawlerMap size :"+crawlerInfoSortedMap.size());
		return String.format("%d:%d", crawlerInfoSortedMap.size(),crawlerInfoSortedMap.size()-1);
	}

	@Override
	public String pageCrawlerHeatBeat(PageCrawlerInfo info) {
		// TODO Auto-generated method stubn =n =fn
		return null;
	}
	
	class CleanCrawlerMapThread extends Thread{
		
		@Override
		public void run() {
			super.run();
			while(true){
				Calendar time=Calendar.getInstance();
				Map<String, PageCrawlerInfo> m=Collections.synchronizedMap(crawlerInfoSortedMap);
				String[] keysArr=(String[])m.keySet().toArray();
				synchronized (m) {
					for(int i=keysArr.length-1;i>0;i--){
						long now=time.getTimeInMillis();
						PageCrawlerInfo info=(PageCrawlerInfo)m.get(keysArr[i]);
						if(now-info.lastHeatBeatTime>MAX_HEAT_BEAT_GAP){
							m.remove(keysArr[i]);
							logger.info("Crawler offline :"+keysArr[i]);
						}else{
							break;
						}
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
