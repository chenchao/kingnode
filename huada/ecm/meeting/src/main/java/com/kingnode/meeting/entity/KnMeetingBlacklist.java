package com.kingnode.meeting.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kingnode.xsimple.entity.AuditEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.joda.time.DateTime;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_meeting_blacklist") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class KnMeetingBlacklist extends AuditEntity{
    private Long userId;//用户id
    private String userName;//用户姓名
    private MeetingStatus status;//用户申请会议室状态，锁定不能申请
    private Long lockTime;//锁定时间
    private String lockDate;
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
    @Enumerated(EnumType.STRING) @Column(length=10)
    public MeetingStatus getStatus(){
        return status;
    }
    public void setStatus(MeetingStatus status){
        this.status=status;
    }
    @Column(length=13)
    public Long getLockTime(){
        return lockTime;
    }
    public void setLockTime(Long lockTime){
        this.lockTime=lockTime;
    }
    public enum MeetingStatus{//锁定，解除锁定
        LOCK,UNLOCK
    }
    @Transient
    public String getLockDate(){
        if(this.lockTime!=null&&this.lockTime.compareTo(0L)>0){
            return new DateTime(this.lockTime).toString("yyyy-MM-dd HH:mm:ss");
        }else{
            return "";
        }
    }
}
