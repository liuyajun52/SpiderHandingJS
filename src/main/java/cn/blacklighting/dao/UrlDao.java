package cn.blacklighting.dao;

import cn.blacklighting.entity.UrlEntity;
import org.hibernate.Session;

/**
 * Created by zybang on 2016/4/12.
 */
public class UrlDao extends DaoBase {
    public UrlDao(){
    }

    public void addUrl(UrlEntity url){
        Session s=this.db.getSession();
        s.beginTransaction();
        s.save(url);
        s.getTransaction().commit();
        s.close();
    }

    public void updateUrl(UrlEntity url){
        Session s=this.db.getSession();
        s.beginTransaction();
        s.merge(url);
        s.getTransaction().commit();
        s.close();
    }

    public void saveOrUpdate(UrlEntity url){
        
    }

}
