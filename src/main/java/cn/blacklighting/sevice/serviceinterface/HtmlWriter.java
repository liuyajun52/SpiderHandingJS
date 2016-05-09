package cn.blacklighting.sevice.serviceinterface;

import cn.blacklighting.models.UrlEntity;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by zybang on 2016/4/5.
 */
public interface HtmlWriter extends Remote{
    /**
     * 写HTML
     * @param url
     * @param html
     * @throws RemoteException
     */
    void writeHtml(UrlEntity url,byte[] html)throws RemoteException;

    /**
     * 关闭写入HTML服务
     * @throws RemoteException
     */
    void shutDown() throws RemoteException;
}
