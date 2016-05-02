package cn.blacklighting.sevice;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by zybang on 2016/4/21.
 */
public interface Scheduler  extends Remote{
    class HtmlWriterInfo implements Serializable {
        String id;
        String rmiUrl;
        String type;
        Integer htmlQueueMaxSize;
        Integer htmlSize;
        String htmlDirRoot;
        Integer htmlDirMaxRoom;
        Integer htmlDirCanUse;
    }

    class PageCrawlerInfo implements Serializable{
        String id;
        String rmiUrl;
        Integer urlDisNum;
        String bindHtmlWriterId;
        Integer urlQueueMaxSize;
        Integer urlQUeueSize;
        Integer threadPoolSize;

    }
    void registerUrlDistributer() throws RemoteException;
    void registerHtmlWriter() throws RemoteException ;
    Integer registerPageCrawler(PageCrawlerInfo info)throws RemoteException ;
    Integer pageCrawlerHeatBeat(PageCrawlerInfo info);
}
