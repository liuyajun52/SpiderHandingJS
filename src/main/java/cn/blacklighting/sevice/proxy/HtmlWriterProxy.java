package cn.blacklighting.sevice.proxy;

import cn.blacklighting.conf.SpiderConf;
import cn.blacklighting.sevice.HtmlToFileWriterService;
import cn.blacklighting.sevice.serviceinterface.HtmlWriter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * Created by zybang on 2016/4/21.
 */
public class HtmlWriterProxy {
    private static HtmlWriter writer;
    private static Logger logger= LogManager.getRootLogger();
    public static void registerOrGetService() throws ConfigurationException, RemoteException, MalformedURLException, NotBoundException {
        PropertiesConfiguration pro = SpiderConf.getConf();
        if(writer==null){
            if(pro.getBoolean("htmlWriter.isHtmlWriter")){
                String type=pro.getString("htmlWriter.type");
                String host=pro.getString("htmlWriter.host");
                int port=pro.getInt("htmlWriter.port");
                String name=pro.getString("htmlWriter.name");
                LocateRegistry.createRegistry(port);

                if(type.equals("local")){
                    writer=new HtmlToFileWriterService();
                }else {
                    //TODO add HDFS writer register
                }
                Naming.rebind(String.format("rmi://%s:%d/%s",host,port,name),writer);
                logger.info("Run as html writer at "+String.format("rmi://%s:%d/%s",host,port,name));
            }
        }
        String htmlUrl=pro.getString("htmlWriter.remote.url");
        writer=(HtmlWriter)Naming.lookup(htmlUrl);
    }

    public static HtmlWriter getWriter() throws RemoteException, MalformedURLException, ConfigurationException, NotBoundException {
        if(writer==null){
            registerOrGetService();
        }
        return writer;
    }
}
