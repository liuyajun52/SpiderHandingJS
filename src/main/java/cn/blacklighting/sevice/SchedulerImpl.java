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
    public String registerHtmlWriter(HtmlWriterInfo info) throws RemoteException {
        return null;
    }

    @Override
    public void registerHtmlWriter() throws RemoteException {

    }

    @Override
    public Integer registerPageCrawler(PageCrawlerInfo info) throws RemoteException {
        return null;
    }

    @Override
    public Integer pageCrawlerHeatBeat(PageCrawlerInfo info) {
    public String registerPageCrawler(HtmlWriterInfo info) throws RemoteException {
        return null;
    }

}
