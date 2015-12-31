package com.kingnode.meeting.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_meeting_attendee") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class KnMeetingAttendee extends AuditEntity{
    private static final long serialVersionUID=6783277164857945151L;
    private Long attendeeId;//与会人员ID或者部门id
    private String attendee;//与会人员姓名或者部门名称
    private KnMeeting km;
    private AttendeeType attendeeType;
    @Enumerated(EnumType.STRING) @Column(length=10)
    public AttendeeType getAttendeeType(){
        return attendeeType;
    }
    public void setAttendeeType(AttendeeType attendeeType){
        this.attendeeType=attendeeType;
    }
    @ManyToOne @JoinColumn(name="meeting_id")
    public KnMeeting getKm(){
        return km;
    }
    public void setKm(KnMeeting km){
        this.km=km;
    }
    @Column(length=50)
    public String getAttendee(){
        return attendee;
    }
    public void setAttendee(String attendee){
        this.attendee=attendee;
    }
    @Column(length=13)
    public Long getAttendeeId(){
        return attendeeId;
    }
    public void setAttendeeId(Long attendeeId){
        this.attendeeId=attendeeId;
    }
    public enum AttendeeType{ //员工，部门
        EMPLOYEE,DEPARTMENT
    }
}
