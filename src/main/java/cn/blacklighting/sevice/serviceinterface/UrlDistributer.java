package cn.blacklighting.sevice.serviceinterface;

import cn.blacklighting.models.UrlEntity;

/**
 * Created by zybang on 2016/4/1.
 */
public interface UrlDistributer {
    public UrlEntity getNextUrl();  //获取URL
    public void setUrlFail(UrlEntity u); //标记URL抓取失败
}
