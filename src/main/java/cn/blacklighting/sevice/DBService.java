package cn.blacklighting.sevice;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Created by zybang on 2016/4/1.
 */
public class DBService {

    private static Logger logger= LogManager.getRootLogger();

    private final SessionFactory ourSessionFactory;
    private static DBService service ;

    private DBService(){
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            ourSessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Throwable ex) {
            logger.fatal("Error on DBService get Factory",ex);
            StandardServiceRegistryBuilder.destroy( registry );
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
