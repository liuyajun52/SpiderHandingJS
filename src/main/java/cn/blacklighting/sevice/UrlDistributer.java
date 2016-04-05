package cn.blacklighting.sevice;

import cn.blacklighting.entity.UrlEntity;

/**
 * Created by zybang on 2016/4/1.
 */
public interface UrlDistributer {
    public UrlEntity getNextUrl();
    public void setUrlFail(UrlEntity u);
}
