package cn.blacklighting.sevice;

/**
 * Created by zybang on 2016/4/5.
 */
public class PageCrawlingService {
    private UrlDistributer urlDistributer;
    private HtmlWriter htmlWriter;

    public PageCrawlingService(UrlDistributer urlDistributer,HtmlWriter htmlWriter){
        this.urlDistributer=urlDistributer;
        this.htmlWriter=htmlWriter;
    }


}
