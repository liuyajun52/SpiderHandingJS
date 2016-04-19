package cn.blacklighting.dao;

import cn.blacklighting.entity.UrlEntity;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.Type;

import java.util.List;

/**
 * Created by zybang on 2016/4/12.
 */
public class UrlDao extends DaoBase {
    public void updateUrlStatus(UrlEntity url,Integer status){
        url.setStatus(status);
        Session s=this.db.getSession();
        s.beginTransaction();
        s.merge(url);
        s.getTransaction().commit();
        s.close();
    }
}
