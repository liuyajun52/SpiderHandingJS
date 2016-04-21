package cn.blacklighting.models;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by zybang on 2016/4/1.
 */
@Entity
@Table(name = "page", schema = "spider")
@DynamicInsert
@DynamicUpdate
public class PageEntity {
    private int id;
    private Integer urlId;
    private String pagePath;
    private String docType;
    private Timestamp updateTime;
    private Timestamp createTime;
    private Byte jsHandled;

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
    @Column(name = "create_time",columnDefinition = "timestamp default current_timestamp")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Basic
    @Column(name ="update_time",columnDefinition = "CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    public Timestamp getUpdateTime(){return updateTime;}

    public void setUpdateTime(Timestamp updateTime){
        this.updateTime=updateTime;
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
        if (!urlId.equals(that.urlId)) return false;
        if (pagePath != null ? !pagePath.equals(that.pagePath) : that.pagePath != null) return false;
        if (docType != null ? !docType.equals(that.docType) : that.docType != null) return false;
        if (updateTime != null ? !updateTime.equals(that.updateTime) : that.updateTime != null) return false;
        if (createTime != null ? !createTime.equals(that.createTime) : that.createTime != null) return false;
        return jsHandled != null ? jsHandled.equals(that.jsHandled) : that.jsHandled == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + urlId.hashCode();
        result = 31 * result + (pagePath != null ? pagePath.hashCode() : 0);
        result = 31 * result + (docType != null ? docType.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (jsHandled != null ? jsHandled.hashCode() : 0);
        return result;
    }

    @PrePersist
    protected void onCreate(){
        this.createTime=new Timestamp(Calendar.getInstance().getTime().getTime());
    }

    @PreUpdate
    protected void onUpdate(){
        this.updateTime=new Timestamp(Calendar.getInstance().getTime().getTime());
    }
}
