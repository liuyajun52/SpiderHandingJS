package cn.blacklighting.util;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by zybang on 2016/4/12.
 */
public class CrawlerUtil {

    public static String getDomainName(String url) {
        if(!(url.startsWith("http")||url.startsWith("ftp"))){
            url="http://"+url;
        }
        URI uri = null;
        try {
            uri = new URI(url);
            String domain = uri.getHost();
            domain= domain!=null&&domain.startsWith("www.") ? domain.substring(4) : domain;
            return domain;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
