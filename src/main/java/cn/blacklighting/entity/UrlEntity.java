package cn.blacklighting.entity;

import javax.persistence.*;

/**
 * Created by zybang on 2016/4/1.
 */
@Entity
@Table(name = "url", schema = "spider", catalog = "")
public class UrlEntity {
    private int id;
    private String url;
    private Byte status;
    private Byte needHandJs;
    private Integer pageId;
    private Integer weight;
    private Integer retryTime;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Basic
    @Column(name = "status")
    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    @Basic
    @Column(name = "need_hand_JS")
    public Byte getNeedHandJs() {
        return needHandJs;
    }

    public void setNeedHandJs(Byte needHandJs) {
        this.needHandJs = needHandJs;
    }

    @Basic
    @Column(name = "page_id")
    public Integer getPageId() {
        return pageId;
    }

    public void setPageId(Integer pageId) {
        this.pageId = pageId;
    }

    @Basic
    @Column(name = "weight")
    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Basic
    @Column(name = "retry_time")
    public Integer getRetryTime() {
        return retryTime;
    }

    public void setRetryTime(Integer retryTime) {
        this.retryTime = retryTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UrlEntity urlEntity = (UrlEntity) o;

        if (id != urlEntity.id) return false;
        if (url != null ? !url.equals(urlEntity.url) : urlEntity.url != null) return false;
        if (status != null ? !status.equals(urlEntity.status) : urlEntity.status != null) return false;
        if (needHandJs != null ? !needHandJs.equals(urlEntity.needHandJs) : urlEntity.needHandJs != null) return false;
        if (pageId != null ? !pageId.equals(urlEntity.pageId) : urlEntity.pageId != null) return false;
        if (weight != null ? !weight.equals(urlEntity.weight) : urlEntity.weight != null) return false;
        if (retryTime != null ? !retryTime.equals(urlEntity.retryTime) : urlEntity.retryTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (needHandJs != null ? needHandJs.hashCode() : 0);
        result = 31 * result + (pageId != null ? pageId.hashCode() : 0);
        result = 31 * result + (weight != null ? weight.hashCode() : 0);
        result = 31 * result + (retryTime != null ? retryTime.hashCode() : 0);
        return result;
    }
}
