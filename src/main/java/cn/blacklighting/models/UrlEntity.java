package cn.blacklighting.models;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by Yajun Liu on 2016/4/4 0004.
 */
@Entity
@Table(name = "url", schema = "spider")
@DynamicInsert
@DynamicUpdate
public class UrlEntity implements Serializable {
    /**
     * URL状态：新获取
     */
    public static final int STATUS_NEW = 0;

    /**
     * URL状态：在抓取队列
     */
    public static final int STATUS_IN_QUEUE = 1;

    /**
     * URL状态：抓取成功
     */
    public static final int STATUS_CRAWED = 2;

    /**
     * URL状态：抓取失败
     */
    public static final int STATUS_FAILED = 3;

    /**
     * URL状态：不需要抓取
     */
    public static final int STATUS_UNNEED =4;

    /**
     * URL状态：已经尝试，但是无内容
     */
    public static final int STATUS_NO_CONTENT=5;

    /**
     * URL状态：页面被标记为noindex
     */
    public static final int STATUS_NO_INDEX=6;

    private int id;
    private String url;
    private Integer status;
    private Byte needHandJs;
    private Integer pageId;
    private Integer weight;
    private Integer retryTime;
    private Integer deepth;
    private String domain;
    private Byte isSeed;
    private Integer maxDeepth;
    private String md5;
    private int outLinkAmount;
    private int toLinkAmount;
    private int pageRank;
    private Timestamp createTime;
    private Timestamp updateTime;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "url", nullable = true, length = 256)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Basic
    @Column(name = "status", columnDefinition = "int default 0", nullable = true)
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Basic
    @Column(name = "need_hand_JS", columnDefinition = "tiny int default 0", nullable = true)
    public Byte getNeedHandJs() {
        return needHandJs;
    }

    public void setNeedHandJs(Byte needHandJs) {
        this.needHandJs = needHandJs;
    }

    @Basic
    @Column(name = "page_id", nullable = true)
    public Integer getPageId() {
        return pageId;
    }

    public void setPageId(Integer pageId) {
        this.pageId = pageId;
    }

    @Basic
    @Column(name = "weight", columnDefinition = "int default 5", nullable = true)
    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Basic
    @Column(name = "retry_time", columnDefinition = "int default 0", nullable = true)
    public Integer getRetryTime() {
        return retryTime;
    }

    public void setRetryTime(Integer retryTime) {
        this.retryTime = retryTime;
    }

    @Basic
    @Column(name = "deepth", columnDefinition = "int default 0", nullable = true)
    public Integer getDeepth() {
        return deepth;
    }

    public void setDeepth(Integer deepth) {
        this.deepth = deepth;
    }

    @Basic
    @Column(name = "domain", nullable = true, length = 256)
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Basic
    @Column(name = "is_seed", columnDefinition = "tiny int default 0", nullable = true)
    public Byte getIsSeed() {
        return isSeed;
    }

    public void setIsSeed(Byte isSeed) {
        this.isSeed = isSeed;
    }

    @Basic
    @Column(name = "max_deepth", columnDefinition = "int default 3", nullable = true)
    public Integer getMaxDeepth() {
        return maxDeepth;
    }

    public void setMaxDeepth(Integer maxDeepth) {
        this.maxDeepth = maxDeepth;
    }


    @Basic
    @Column(name = "md5", nullable = true, length = 64)
    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Basic
    @Column(name = "out_link_amount", nullable = false)
    public int getOutLinkAmount() {
        return outLinkAmount;
    }

    public void setOutLinkAmount(int outLinkAmount) {
        this.outLinkAmount = outLinkAmount;
    }

    @Basic
    @Column(name = "to_link_amount", nullable = false)
    public int getToLinkAmount() {
        return toLinkAmount;
    }

    public void setToLinkAmount(int toLinkAmout) {
        this.toLinkAmount = toLinkAmout;
    }

    @Basic
    @Column(name = "page_rank")
    public int getPageRank() {
        return pageRank;
    }

    public void setPageRank(int pageRank) {
        this.pageRank = pageRank;
    }

    @Basic
    @Column(name = "create_time",columnDefinition = "timestamp default current_timestamp" )
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

    @PrePersist
    protected void onCreate(){
        this.createTime=new Timestamp(Calendar.getInstance().getTime().getTime());
    }

    @PreUpdate
    protected void onUpdate(){
        this.updateTime=new Timestamp(Calendar.getInstance().getTime().getTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UrlEntity urlEntity = (UrlEntity) o;

        if (id != urlEntity.id) return false;
        if (outLinkAmount != urlEntity.outLinkAmount) return false;
        if (toLinkAmount != urlEntity.toLinkAmount) return false;
        if (pageRank != urlEntity.pageRank) return false;
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
        if (md5 != null ? !md5.equals(urlEntity.md5) : urlEntity.md5 != null) return false;
        if (createTime != null ? !createTime.equals(urlEntity.createTime) : urlEntity.createTime != null) return false;
        return updateTime != null ? updateTime.equals(urlEntity.updateTime) : urlEntity.updateTime == null;

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
        result = 31 * result + (md5 != null ? md5.hashCode() : 0);
        result = 31 * result + outLinkAmount;
        result = 31 * result + toLinkAmount;
        result = 31 * result + pageRank;
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        return result;
    }
}
