package cn.blacklighting.sevice;

import cn.blacklighting.models.UrlEntity;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by zybang on 2016/4/5.
 */
public interface HtmlWriter extends Remote{
    void writeHtml(UrlEntity url,byte[] html)throws RemoteException;
    void shutDown() throws RemoteException;
}
