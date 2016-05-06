package cn.blacklighting.util;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zybang on 2016/4/12.
 */
public class CrawlerUtil {
    public static final Pattern URL_EXT_PAT = Pattern.compile(".*\\.(\\w+)(\\?.*)?$");

    /**
     * 已知的不需要抓取的文件类型
     */
    public static HashSet<String> NOT_CRAWL_FILE_TYPES
            = new HashSet<String>(Arrays.asList(
            "exe", "zip", "tar", "gz", "7z", "ppt", "pptx", "doc", "docx", "rm", "rmvb", "avi", "flv", "mp3", "mp4", "wma", "jpg",
            "png", "jpeg", "js", "css", "tpl"
    ));

    /**
     * 获取给定URL的域名，获取失败返回null
     *
     * @param url
     * @return
     */
    public static String getDomainName(String url) {
        if (!(url.startsWith("http") || url.startsWith("ftp"))) {
            url = "http://" + url;
        }
        URI uri = null;
        try {
            uri = new URI(url);
            String domain = uri.getHost();
            return domain;
        } catch (URISyntaxException e) {
//            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取可用的绝对URL
     *
     * @param url
     * @param domain
     * @return
     */
    public static String getFormatUrl(String url, String domain) {

        if(url==null){
            return null;
        }
        //页内锚文本
        if(url.trim().startsWith("#")){
            return null;
        }

        //java script call
        if(url.trim().startsWith("javascript:")){
            return null;
        }

        StringBuilder urlBuilder = new StringBuilder(url);
        //判断是否是绝对URL
        if (!url.contains("//")) {
            if (url.startsWith("/")) {
                urlBuilder.insert(0, domain);
            } else {
                urlBuilder.insert(0, '/');
                urlBuilder.insert(0, domain);
            }
        }

        if (urlBuilder.indexOf("http") < 0 && urlBuilder.indexOf("ftp") < 0) {
            urlBuilder.insert(0, "http://");
        }

        url = urlBuilder.toString().replaceAll("#.*","").trim();

        return url;
    }

    /**
     * 获取url的扩展名，为了识别链接指向的文件是否为可用页面
     */
    public static String getUrlExtName(String url) {
        Matcher m = URL_EXT_PAT.matcher(url);
        if (m.matches()) {
            return m.group(1);
        }
        return null;
    }

    public static String md5(String md5){
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.err.print("Can not generate md5!!!");
        }
        return null;
    }

    public static String getLocalIP() throws UnknownHostException {
        InetAddress address=InetAddress.getLocalHost();
        String ip=address.getHostAddress();
        return ip;
    }
}
