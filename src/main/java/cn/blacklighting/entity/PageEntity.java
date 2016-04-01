package cn.blacklighting.entity;

import javax.persistence.*;

/**
 * Created by zybang on 2016/4/1.
 */
@Entity
@Table(name = "page", schema = "spider", catalog = "")
public class PageEntity {
    private int id;
    private Integer urlId;
    private String pagePath;
    private Integer includeLinksNu;
    private String docType;
    private Integer updateTime;
    private Byte jsHandled;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "url_id")
    public Integer getUrlId() {
        return urlId;
    }

    public void setUrlId(Integer urlId) {
        this.urlId = urlId;
    }

    @Basic
    @Column(name = "page_path")
    public String getPagePath() {
        return pagePath;
    }

    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }

    @Basic
    @Column(name = "include_links_nu")
    public Integer getIncludeLinksNu() {
        return includeLinksNu;
    }

    public void setIncludeLinksNu(Integer includeLinksNu) {
        this.includeLinksNu = includeLinksNu;
    }

    @Basic
    @Column(name = "doc_type")
    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    @Basic
    @Column(name = "update_time")
    public Integer getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Integer updateTime) {
        this.updateTime = updateTime;
    }

    @Basic
    @Column(name = "JS_handled")
    public Byte getJsHandled() {
        return jsHandled;
    }

    public void setJsHandled(Byte jsHandled) {
        this.jsHandled = jsHandled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PageEntity that = (PageEntity) o;

        if (id != that.id) return false;
        if (urlId != null ? !urlId.equals(that.urlId) : that.urlId != null) return false;
        if (pagePath != null ? !pagePath.equals(that.pagePath) : that.pagePath != null) return false;
        if (includeLinksNu != null ? !includeLinksNu.equals(that.includeLinksNu) : that.includeLinksNu != null)
            return false;
        if (docType != null ? !docType.equals(that.docType) : that.docType != null) return false;
        if (updateTime != null ? !updateTime.equals(that.updateTime) : that.updateTime != null) return false;
        if (jsHandled != null ? !jsHandled.equals(that.jsHandled) : that.jsHandled != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (urlId != null ? urlId.hashCode() : 0);
        result = 31 * result + (pagePath != null ? pagePath.hashCode() : 0);
        result = 31 * result + (includeLinksNu != null ? includeLinksNu.hashCode() : 0);
        result = 31 * result + (docType != null ? docType.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        result = 31 * result + (jsHandled != null ? jsHandled.hashCode() : 0);
        return result;
    }
}
