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
    public void registerUrlDistributer() throws RemoteException {

    }

    @Override
    public void registerHtmlWriter() throws RemoteException {

    }

    @Override
    public Integer registerPageCrawler() throws RemoteException {
        return null;
    }

}
