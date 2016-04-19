package cn.blacklighting.entity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zybang on 2016/4/1.
 */
@Entity
@Table(name = "page", schema = "spider")
public class PageEntity {
    private int id;
    private Integer urlId;
    private String pagePath;
    private Integer includeLinksNu;
    private String docType;
    private Timestamp updateTime;
    private Byte jsHandled;

    public PageEntity() {
    }

    public PageEntity(int id, Integer urlId, String pagePath,
                      Integer includeLinksNu, String docType, Timestamp updateTime, Byte jsHandled) {
        this.id = id;
        this.urlId = urlId;
        this.pagePath = pagePath;
        this.includeLinksNu = includeLinksNu;
        this.docType = docType;
        this.updateTime = updateTime;
        this.jsHandled = jsHandled;
    }

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "url_id", nullable = true)
    public Integer getUrlId() {
        return urlId;
    }

    public void setUrlId(Integer urlId) {
        this.urlId = urlId;
    }

    @Basic
    @Column(name = "page_path", nullable = true, length = 256)
    public String getPagePath() {
        return pagePath;
    }

    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }


    @Basic
    @Column(name = "doc_type", nullable = true, length = 32)
    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    @Basic
    @Column(name = "update_time", nullable = true)
    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Basic
    @Column(name = "JS_handled", nullable = true)
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
