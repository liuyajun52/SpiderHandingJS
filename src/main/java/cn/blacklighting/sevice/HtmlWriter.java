package cn.blacklighting.sevice;

import cn.blacklighting.models.UrlEntity;

/**
 * Created by zybang on 2016/4/5.
 */
public interface HtmlWriter {
    void writeHtml(UrlEntity url,byte[] html);
}
