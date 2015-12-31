package com.kingnode.xsimple.entity.meeting;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
@Entity @Table(name="kn_meeting_register_info")
public class KnMeetingRegisterInfo extends AuditEntity{
    private static final long serialVersionUID=-6949708803404766391L;
    private String meetingId;//会议ID
    private String registerId; //参会人员ＩＤ
    private String siteId;//座位编号
    private String state="否"; //是否签到
    private Date signTime;//签到时间
    @Column(name="meetingid")
    public String getMeetingId(){
        return meetingId;
    }
    public void setMeetingId(String meetingId){
        this.meetingId=meetingId;
    }
    @Column(name="registerid")
    public String getRegisterId(){
        return registerId;
    }
    public void setRegisterId(String registerId){
        this.registerId=registerId;
    }
    @Column(name="state")
    public String getState(){
        return state;
    }
    public void setState(String state){
        this.state=state;
    }
    public Date getSignTime(){
        return signTime;
    }
    @Column(name="signtime")
    public void setSignTime(Date signTime){
        this.signTime=signTime;
    }
    @Column(name="siteId")
    public String getSiteId(){
        return siteId;
    }
    public void setSiteId(String siteId){
        this.siteId=siteId;
    }
}
