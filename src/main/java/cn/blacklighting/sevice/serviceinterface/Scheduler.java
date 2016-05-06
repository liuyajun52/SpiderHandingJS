package cn.blacklighting.sevice.serviceinterface;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by zybang on 2016/4/21.
 */
public interface Scheduler  extends Remote{
    class HtmlWriterInfo implements Serializable {
        public String id;
        public String rmiUrl;
        public String type;
        public Integer htmlQueueMaxSize;
        public Integer htmlSize;
        public String htmlDirRoot;
        public Integer htmlDirMaxRoom;
        public Integer htmlDirCanUse;
    }

    class PageCrawlerInfo implements Serializable{
        public String id;
        public String rmiUrl;
        public Integer urlDisNum;
        public String bindHtmlWriterId;
        public Integer urlQueueMaxSize;
        public Integer urlQUeueSize;
        public Integer threadPoolSize;
        public Long lastHeatBeatTime;
        public Integer crawlerNumber;
    }
    String registerUrlDistributer() throws RemoteException;
    String registerHtmlWriter() throws RemoteException ;
    String registerPageCrawler(PageCrawlerInfo info)throws RemoteException ;
    String pageCrawlerHeatBeat(PageCrawlerInfo info) throws RemoteException;
}
