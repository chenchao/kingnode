package com.kingnode.news.entity;
import java.util.Set;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kingnode.xsimple.entity.AuditEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_notice") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @Cacheable(true)
public class KnNotice extends AuditEntity{
    private static final long serialVersionUID=-181056522800683646L;
    private String title;//标题
    private Long pubTime;//发布时间
    private Long pubUserId;//发布者ID
    private String pubUserName;//发布者姓名）
    private String content;//（内容）
    private String orgPath;//存组织的path
    private String orgName;//发布者组织名称
    private String img;//（如果有图片请提供）
    private ReceiptType isReceipt;//（是否回执）
    private Long effectiveTime;//有效时间
    private Set<KnNoticeReceipt> receipts;
    public KnNotice(){
    }
    public KnNotice(Long id){
        setId(id);
    }
    @Column(length=60)
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    @Column(length=13)
    public Long getEffectiveTime(){
        return effectiveTime;
    }
    public void setEffectiveTime(Long effectiveTime){
        this.effectiveTime=effectiveTime;
    }
    @Column(length=13)
    public Long getPubTime(){
        return pubTime;
    }
    public void setPubTime(Long pubTime){
        this.pubTime=pubTime;
    }
    @Column(length=13)
    public Long getPubUserId(){
        return pubUserId;
    }
    public void setPubUserId(Long pubUserId){
        this.pubUserId=pubUserId;
    }
    @Column(length=20)
    public String getPubUserName(){
        return pubUserName;
    }
    public void setPubUserName(String pubUserName){
        this.pubUserName=pubUserName;
    }
    @Lob
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content=content;
    }
    @Column(length=300)
    public String getOrgPath(){
        return orgPath;
    }
    public void setOrgPath(String orgPath){
        this.orgPath=orgPath;
    }
    @Column(length=50)
    public String getOrgName(){
        return orgName;
    }
    public void setOrgName(String orgName){
        this.orgName=orgName;
    }
    @Column(length=100)
    public String getImg(){
        return img;
    }
    public void setImg(String img){
        this.img=img;
    }
    @Enumerated(EnumType.STRING)
    public ReceiptType getIsReceipt(){
        return isReceipt;
    }
    public void setIsReceipt(ReceiptType isReceipt){
        this.isReceipt=isReceipt;
    }
    @OneToMany(targetEntity=KnNoticeReceipt.class,cascade=CascadeType.ALL,mappedBy="id.noticeId") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @JsonIgnore
    public Set<KnNoticeReceipt> getReceipts(){
        return receipts;
    }
    public void setReceipts(Set<KnNoticeReceipt> receipts){
        this.receipts=receipts;
    }
    public enum ReceiptType{//不需要  所有  特定人员
        none,all,assign
    }
}
