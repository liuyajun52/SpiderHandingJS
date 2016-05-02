package cn.blacklighting.sevice;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedHashMap;

/**
 * Created by zybang on 2016/4/21.
 */
public class SchedulerImpl extends UnicastRemoteObject implements Scheduler {

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
	public Integer registerPageCrawler(PageCrawlerInfo info) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer pageCrawlerHeatBeat(PageCrawlerInfo info) {
		// TODO Auto-generated method stub
		return null;
	}

}
