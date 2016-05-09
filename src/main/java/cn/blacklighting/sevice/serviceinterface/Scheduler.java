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
        public String id;       //抓取节点ID
        public String rmiUrl;   //远程调用URL
        public String bindHtmlWriterId;  //绑定HTML Writer ID
        public Integer urlQueueMaxSize;     //URL分发器队列大小
        public Integer urlQUeueSize;        //URL分发器队列当前使用大小
        public Integer threadPoolSize;      //抓取线程池大小
        public Long lastHeatBeatTime;       //上次心跳时间
        public Integer crawlerNumber;       //抓取节点编号，用户hash取余
    }

    /**
     * 注册HTML存储结点
     */
    String registerHtmlWriter(HtmlWriterInfo info) throws RemoteException ;

    /**
     * 注册抓取节点
     */
    String registerPageCrawler(PageCrawlerInfo info)throws RemoteException ;

    /**
     * 抓取节点心跳
     */
    String pageCrawlerHeatBeat(PageCrawlerInfo info) throws RemoteException;
}
