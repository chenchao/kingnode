package com.kingnode.social.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.kingnode.xsimple.entity.AuditEntity;
import org.joda.time.DateTime;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_social_blog_comment")
public class KnSocialBlogComment extends AuditEntity{
    private static final long serialVersionUID=-1513025604850040411L;
    private String comment;//评论内容
    private String status;//内容状态
    private Long commentTime;//评论时间
    private Long mId;//微博信息ID
    private Long pId;//上一层消息得ID
    private Long userId;//用户ID
    private String userName;//用户姓名
    private Integer replyNum;//回复数
    private ActiveType active;
    @Column(length=500)
    public String getComment(){
        return comment;
    }
    public void setComment(String comment){
        this.comment=comment;
    }
    @Column(length=10)
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status=status;
    }
    @Column(length=13)
    public Long getCommentTime(){
        return commentTime;
    }
    public void setCommentTime(Long commentTime){
        this.commentTime=commentTime;
    }
    @Column(length=13)
    public Long getmId(){
        return mId;
    }
    public void setmId(Long mId){
        this.mId=mId;
    }
    @Column(length=13)
    public Long getpId(){
        return pId;
    }
    public void setpId(Long pId){
        this.pId=pId;
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
    @Transient
    public String getCommentDate(){
        if(this.commentTime!=null&&this.commentTime.compareTo(0L)>0){
            return new DateTime(this.commentTime).toString("yyyy-MM-dd HH:mm:ss");
        }else{
            return "";
        }
    }
    @Column(length=13)
    public Integer getReplyNum(){
        return replyNum;
    }
    public void setReplyNum(Integer replyNum){
        this.replyNum=replyNum;
    }
    @NotNull @Enumerated(EnumType.STRING) @Column(length=10)
    public ActiveType getActive(){
        return active;
    }
    public void setActive(ActiveType active){
        this.active=active;
    }
}
