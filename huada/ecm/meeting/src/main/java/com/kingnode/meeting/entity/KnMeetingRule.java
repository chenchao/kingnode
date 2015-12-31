package com.kingnode.meeting.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_meeting_rule") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class KnMeetingRule extends AuditEntity{
    private Integer scheduleWeek;//提前预定会议室时间。例如：1周 、2周
    private Integer freeNum;//会议室空闲数
    private Integer scheduleTime;//会议开始前提醒时间
    private Integer signInBeforeTime;//会议签到提醒。会议开始时间，前15分钟
    private Integer signInEndTime;//会议签到提醒。会议结束时间，后15分钟
    @Column(length=10)
    public Integer getScheduleWeek(){
        return scheduleWeek;
    }
    public void setScheduleWeek(Integer scheduleWeek){
        this.scheduleWeek=scheduleWeek;
    }
    @Column(length=10)
    public Integer getFreeNum(){
        return freeNum;
    }
    public void setFreeNum(Integer freeNum){
        this.freeNum=freeNum;
    }
    @Column(length=10)
    public Integer getScheduleTime(){
        return scheduleTime;
    }
    public void setScheduleTime(Integer scheduleTime){
        this.scheduleTime=scheduleTime;
    }
    @Column(length=10)
    public Integer getSignInBeforeTime(){
        return signInBeforeTime;
    }
    public void setSignInBeforeTime(Integer signInBeforeTime){
        this.signInBeforeTime=signInBeforeTime;
    }
    @Column(length=10)
    public Integer getSignInEndTime(){
        return signInEndTime;
    }
    public void setSignInEndTime(Integer signInEndTime){
        this.signInEndTime=signInEndTime;
    }
}
