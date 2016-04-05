package cn.blacklighting.sevice;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 * Created by zybang on 2016/4/1.
 */
public class DBService {

    private static org.apache.log4j.Logger logger= Logger.getLogger(DBService.class);

    private final SessionFactory ourSessionFactory;
    private final ServiceRegistry serviceRegistry;
    private static DBService service ;

    private DBService(){
        try {
            Configuration configuration = new Configuration();
            configuration.configure();

            serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
            ourSessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            logger.fatal("Error on DBService get Factory",ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    //session factory 本身是单例模式的，这个服务也设计成单例模式
    public static DBService getInstance(){
        if(service==null){
            service=new DBService();
        }
        return service;
    }


    public Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }
}
