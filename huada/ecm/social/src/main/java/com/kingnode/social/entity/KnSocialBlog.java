package com.kingnode.social.entity;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kingnode.xsimple.entity.AuditEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.joda.time.DateTime;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_social_blog")
public class KnSocialBlog extends AuditEntity{
    private static final long serialVersionUID=-5287684277457730523L;
    private MessageType messageType;//微博类型
    private String messageInfo;//微博内容
    private Long publishTime;//发布时间
    private Integer messageCollectNum;//收藏次数
    private Integer messageCommentNum;//评论次数
    private Integer messageTranspondNum;//转发次数
    private Integer messageAgreeNum;//赞同次数
    private Integer messageReadNum;//阅读次数
    private Long userId;//发表用户ID
    private String userName;//用户姓名
    private String messageSource;//信息来源 例如：魅族手机4G
    private List<KnSocialBlogFile> attach;//附件
    private Long parentId;//转发那条微博得ID
    private ActiveType active;
    private Integer type;//类型  1或者空 为论坛  2  为活动
    private Long endTime;//活动截止时间
    @Enumerated(EnumType.STRING) @Column(length=20) @NotNull
    public MessageType getMessageType(){
        return messageType;
    }
    public void setMessageType(MessageType messageType){
        this.messageType=messageType;
    }
    @Column(length=500)
    public String getMessageInfo(){
        return messageInfo;
    }
    public void setMessageInfo(String messageInfo){
        this.messageInfo=messageInfo;
    }
    @Column(length=13)
    public Long getPublishTime(){
        return publishTime;
    }
    public void setPublishTime(Long publishTime){
        this.publishTime=publishTime;
    }
    @Column(length=10)
    public Integer getMessageCollectNum(){
        return messageCollectNum;
    }
    public void setMessageCollectNum(Integer messageCollectNum){
        this.messageCollectNum=messageCollectNum;
    }
    @Column(length=10)
    public Integer getMessageCommentNum(){
        return messageCommentNum;
    }
    public void setMessageCommentNum(Integer messageCommentNum){
        this.messageCommentNum=messageCommentNum;
    }
    @Column(length=10)
    public Integer getMessageTranspondNum(){
        return messageTranspondNum;
    }
    public void setMessageTranspondNum(Integer messageTranspondNum){
        this.messageTranspondNum=messageTranspondNum;
    }
    @Column(length=10)
    public Integer getMessageAgreeNum(){
        return messageAgreeNum;
    }
    public void setMessageAgreeNum(Integer messageAgreeNum){
        this.messageAgreeNum=messageAgreeNum;
    }
    @Column(length=10)
    public Integer getMessageReadNum(){
        return messageReadNum;
    }
    public void setMessageReadNum(Integer messageReadNum){
        this.messageReadNum=messageReadNum;
    }
    @Column(length=13)
    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId=userId;
    }
    @Column(length=50)
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName=userName;
    }
    @OneToMany(mappedBy="ksb") @Fetch(FetchMode.SUBSELECT) @Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @JsonIgnore
    public List<KnSocialBlogFile> getAttach(){
        return attach;
    }
    public void setAttach(List<KnSocialBlogFile> attach){
        this.attach=attach;
    }
    @Column(length=50)
    public String getMessageSource(){
        return messageSource;
    }
    public void setMessageSource(String messageSource){
        this.messageSource=messageSource;
    }
    @Column(length=13)
    public Long getParentId(){
        return parentId;
    }
    public void setParentId(Long parentId){
        this.parentId=parentId;
    }
    @Transient
    public String getPublishDate(){
        if(this.publishTime!=null&&this.publishTime.compareTo(0L)>0){
            return new DateTime(this.publishTime).toString("yyyy-MM-dd HH:mm:ss");
        }else{
            return "";
        }
    }
    public enum MessageType{//普通，群发，转发
        GENERAL,MASS,FORWARD
    }
    @NotNull @Enumerated(EnumType.STRING) @Column(length=10)
    public ActiveType getActive(){
        return active;
    }
    public void setActive(ActiveType active){
        this.active=active;
    }
    @NotNull
    public Integer getType(){
        return type;
    }
    public void setType(Integer type){
        this.type=type;
    }
    public Long getEndTime(){
        return endTime;
    }
    public void setEndTime(Long endTime){
        this.endTime=endTime;
    }
}
