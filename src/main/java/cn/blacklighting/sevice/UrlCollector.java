package cn.blacklighting.sevice;

import cn.blacklighting.models.LinkEntity;
import cn.blacklighting.models.UrlEntity;

/**
 * Created by zybang on 2016/4/8.
 */
public interface UrlCollector {
    void putUrl(UrlEntity url);
    void putLink(LinkEntity link);
}
