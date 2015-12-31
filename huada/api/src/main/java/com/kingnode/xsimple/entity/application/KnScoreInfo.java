package com.kingnode.xsimple.entity.application;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.entity.AuditEntity;
/**
 * app评分表
 *
 * @author wangyifan
 */
@Entity @Table(name="kn_app_score_info")
public class KnScoreInfo extends AuditEntity{
    private static final long serialVersionUID=-8680175361028512930L;
    private Long userId;//用户ID
    private Long versionId;//用户下载的版本ID
    private String appComment;//应用的评论
    private String rating;//应用的评分
    private Setting.VersionType versionType;
    private Long commentTime;//评论时间
    @Column(name="user_id",length=100)
    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId=userId;
    }
    @Column(name="version_id",length=100)
    public Long getVersionId(){
        return versionId;
    }
    public void setVersionId(Long versionId){
        this.versionId=versionId;
    }
    @Column(name="app_comment",length=1000)
    public String getAppComment(){
        return appComment;
    }
    public void setAppComment(String appComment){
        this.appComment=appComment;
    }
    @Column(name="rating",length=10)
    public String getRating(){
        return rating;
    }
    public void setRating(String rating){
        this.rating=rating;
    }
    @Enumerated(EnumType.STRING) @Column(name="version_type")
    public Setting.VersionType getVersionType(){
        return versionType;
    }
    public void setVersionType(Setting.VersionType versionType){
        this.versionType=versionType;
    }
    @Column(name="comment_time",length=13)
    public Long getCommentTime(){
        return commentTime;
    }
    public void setCommentTime(Long commentTime){
        this.commentTime=commentTime;
    }
}