package cn.blacklighting.dao;

import cn.blacklighting.sevice.DBService;

/**
 * Created by zybang on 2016/4/12.
 */
public class DaoBase {
    protected DBService db;

    public DaoBase(){
        db=DBService.getInstance();
    }
}
