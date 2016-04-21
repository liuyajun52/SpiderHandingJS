package cn.blacklighting.sevice;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by zybang on 2016/4/21.
 */
public interface Scheduler  extends Remote{
    void registerUrlDistributer() throws RemoteException;
    void registerHtmlWriter() throws RemoteException ;
    Integer registerPageCrawler()throws RemoteException ;
}
