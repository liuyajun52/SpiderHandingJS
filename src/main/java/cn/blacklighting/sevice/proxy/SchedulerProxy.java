package cn.blacklighting.sevice.proxy;

import cn.blacklighting.conf.SpiderConf;
import cn.blacklighting.sevice.serviceinterface.Scheduler;
import cn.blacklighting.sevice.SchedulerService;
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
public class SchedulerProxy {
    private static Scheduler scheduler;
    private static Logger logger= LogManager.getRootLogger();


    public static void registerOrGetService() throws RemoteException, ConfigurationException, MalformedURLException, NotBoundException {
        if(scheduler==null){
            PropertiesConfiguration pro = SpiderConf.getConf();
            if(pro.getBoolean("scheduler.isScheduler")){
                String host=pro.getString("scheduler.host");
                int port=pro.getInt("scheduler.port");
                String name=pro.getString("scheduler.name");
                LocateRegistry.createRegistry(port);
                scheduler=new SchedulerService();
                Naming.rebind(String.format("rmi://%s:%d/%s",host,port,name),scheduler);
                logger.info("Run as scheduler at "+String.format("rmi://%s:%d/%s",host,port,name));
            }
            String schedulerUrl=pro.getString("scheduler.remote.url");
            scheduler= (Scheduler) Naming.lookup(schedulerUrl);
        }
    }

    public static Scheduler getScheduler() throws RemoteException, MalformedURLException, ConfigurationException, NotBoundException {
        if(scheduler==null){
            registerOrGetService();
        }
        return scheduler;
    }
}
