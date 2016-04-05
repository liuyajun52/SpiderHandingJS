package cn.blacklighting.entity;

import javax.persistence.*;

/**
 * Created by Yajun Liu on 2016/4/4 0004.
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
    private Integer deepth;
    private Integer domain;
    private Byte isSeed;
    private Integer maxDeepth;

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

    @Basic
    @Column(name = "deepth")
    public Integer getDeepth() {
        return deepth;
    }

    public void setDeepth(Integer deepth) {
        this.deepth = deepth;
    }

    @Basic
    @Column(name = "domain")
    public Integer getDomain() {
        return domain;
    }

    public void setDomain(Integer domain) {
        this.domain = domain;
    }

    @Basic
    @Column(name = "is_seed")
    public Byte getIsSeed() {
        return isSeed;
    }

    public void setIsSeed(Byte isSeed) {
        this.isSeed = isSeed;
    }

    @Basic
    @Column(name = "max_deepth")
    public Integer getMaxDeepth() {
        return maxDeepth;
    }

    public void setMaxDeepth(Integer maxDeepth) {
        this.maxDeepth = maxDeepth;
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
        if (deepth != null ? !deepth.equals(urlEntity.deepth) : urlEntity.deepth != null) return false;
        if (domain != null ? !domain.equals(urlEntity.domain) : urlEntity.domain != null) return false;
        if (isSeed != null ? !isSeed.equals(urlEntity.isSeed) : urlEntity.isSeed != null) return false;
        if (maxDeepth != null ? !maxDeepth.equals(urlEntity.maxDeepth) : urlEntity.maxDeepth != null) return false;

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
        result = 31 * result + (deepth != null ? deepth.hashCode() : 0);
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        result = 31 * result + (isSeed != null ? isSeed.hashCode() : 0);
        result = 31 * result + (maxDeepth != null ? maxDeepth.hashCode() : 0);
        return result;
    }
}
