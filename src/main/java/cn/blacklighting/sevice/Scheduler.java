package cn.blacklighting.sevice;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by zybang on 2016/4/21.
 */
public interface Scheduler  extends Remote{
    class PageCrawlerInfo implements Serializable{
        String ip;

    }
    void registerUrlDistributer() throws RemoteException;
    void registerHtmlWriter() throws RemoteException ;
    Integer registerPageCrawler(PageCrawlerInfo info)throws RemoteException ;
    Integer pageCrawlerHeatBeat(PageCrawlerInfo info);
}
