package cn.blacklighting.dao;

import cn.blacklighting.models.UrlEntity;
import org.hibernate.Session;

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
