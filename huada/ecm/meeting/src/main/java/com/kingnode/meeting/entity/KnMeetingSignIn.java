package com.kingnode.meeting.entity;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_meeting_sign_in") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class KnMeetingSignIn implements java.io.Serializable{
    private static final long serialVersionUID=2539226185362853950L;
    private KnMeetingSignInId id;
    private String userName;//用户名称
    private Long signTime;//签到时间
    private Long createId;
    private Long createTime;
    private Long updateId;
    private Long updateTime;
    @EmbeddedId
    public KnMeetingSignInId getId(){
        return id;
    }
    public void setId(KnMeetingSignInId id){
        this.id=id;
    }
    @Column(length=50)
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName=userName;
    }
    @Column(length=13)
    public Long getSignTime(){
        return signTime;
    }
    public void setSignTime(Long signTime){
        this.signTime=signTime;
    }
    @JsonIgnore @Column(name="create_id",length=13,updatable=false)
    public Long getCreateId(){
        return createId;
    }
    public void setCreateId(Long createId){
        this.createId=createId;
    }
    @JsonIgnore @Column(name="create_time",length=13,updatable=false)
    public Long getCreateTime(){
        return createTime;
    }
    public void setCreateTime(Long createTime){
        this.createTime=createTime;
    }
    @JsonIgnore @Column(name="update_id",length=13,insertable=false)
    public Long getUpdateId(){
        return updateId;
    }
    public void setUpdateId(Long updateId){
        this.updateId=updateId;
    }
    @JsonIgnore @Column(name="update_time",length=13,insertable=false)
    public Long getUpdateTime(){
        return updateTime;
    }
    public void setUpdateTime(Long updateTime){
        this.updateTime=updateTime;
    }
}
