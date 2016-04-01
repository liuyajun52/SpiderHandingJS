package cn.blacklighting.sevice;

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
            throw new ExceptionInInitializerError(ex);
        }
    }

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
