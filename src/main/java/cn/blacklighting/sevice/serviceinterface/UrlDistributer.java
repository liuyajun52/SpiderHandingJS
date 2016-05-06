package cn.blacklighting.sevice.serviceinterface;

import cn.blacklighting.models.UrlEntity;

/**
 * Created by zybang on 2016/4/1.
 */
public interface UrlDistributer {
    public UrlEntity getNextUrl();
    public void setUrlFail(UrlEntity u);
}
