package cn.blacklighting.sevice;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by zybang on 2016/4/21.
 */
public class SchedulerImpl extends UnicastRemoteObject implements Scheduler {


    public SchedulerImpl() throws RemoteException {
    }

    @Override
    public String registerHtmlWriter(HtmlWriterInfo info) throws RemoteException {
        return null;
    }

    @Override
    public String registerPageCrawler(HtmlWriterInfo info) throws RemoteException {
        return null;
    }

}
