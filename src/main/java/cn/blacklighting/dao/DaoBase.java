package cn.blacklighting.dao;

import cn.blacklighting.sevice.DBService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.Type;

import java.util.List;

/**
 * Created by zybang on 2016/4/12.
 */
public class DaoBase {
    protected DBService db;

    public DaoBase(){
        db=DBService.getInstance();
    }

    public void add(Object o){
        Session s=this.db.getSession();
        s.beginTransaction();
        s.save(o);
        s.getTransaction().commit();
        s.close();
    }


    public void update(Object o){
        Session s=this.db.getSession();
        s.beginTransaction();
        s.merge(o);
        s.getTransaction().commit();
        s.close();
    }

    public List query(String q, Object[] params, Type[] types) {
        Session s=this.db.getSession();
        Query query=s.createQuery(q);
        query.setParameters(params,types);
        List list=query.list();
        s.close();
        return list;
    }

    public Object queryUnique(String q, Object[] params, Type[] types) {
        Session s=this.db.getSession();
        Query query=s.createQuery(q);
        query.setParameters(params,types);
        Object result=query.uniqueResult();
        s.close();
        return result;
    }
}
