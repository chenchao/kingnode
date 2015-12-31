package com.kingnode.meeting.entity;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kingnode.diva.utils.Collections3;
import com.kingnode.xsimple.entity.AuditEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_meeting")
public class KnMeeting extends AuditEntity{
    private static final long serialVersionUID=-1914275967516681656L;
    private StatusType statusType;//会议状态
    private String title;//会议标题
    private Long userId;//会议主持人id
    private String moderator;//会议主持人
    private String recorder;//会议纪要人
    private Long beginDate;//会议开始日期时间
    private Long endDate;//会议结束时间时间
    private KnMeetingRoom kmr;// 会议室
    private List<KnMeetingFile> kmf;//会议文件
    private List<KnMeetingAttendee> kma;//与会人员
    private MeetingType meetingType;// 会议类型
    private String description;// 会议说明
    private Integer signInCueStatus;//会议签到前提醒（只针对发起人）状态 1表示提醒过
    private Integer signInCueEndStatus;//会议签到后提醒（只针对发起人）状态 1表示提醒过
    private Integer attendeeCueStatus;//提醒所有与会人员参加会议状态 1表示提醒过
    @Column(length=200)
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    @Column(length=200)
    public String getModerator(){
        return moderator;
    }
    public void setModerator(String moderator){
        this.moderator=moderator;
    }
    @Column(length=200)
    public String getRecorder(){
        return recorder;
    }
    public void setRecorder(String recorder){
        this.recorder=recorder;
    }
    @Transient
    public String getAttendee(){
        String names="";
        if(this.kma!=null&&this.kma.size()>0){
            names=Collections3.extractToList(this.kma,"attendee").toString();
            names=names.substring(1,names.length()-1);
        }
        return names;
    }
    @Transient
    public String getAttendeeIds(){
        String ids="";
        if(this.kma!=null&&this.kma.size()>0){
            ids=Collections3.extractToList(this.kma,"attendeeId").toString();
            ids=ids.substring(1,ids.length()-1);
        }
        return ids;
    }
    @Transient
    public String getAttendeeType(){
        String type="";
        if(this.kma!=null&&this.kma.size()>0){
            type=Collections3.extractToList(this.kma,"attendeeType").toString();
            type=type.substring(1,type.length()-1).split(",")[0];
        }
        return type;
    }
    @Column(length=13)
    public Long getBeginDate(){
        return beginDate;
    }
    public void setBeginDate(Long beginDate){
        this.beginDate=beginDate;
    }
    @Column(length=13)
    public Long getEndDate(){
        return endDate;
    }
    public void setEndDate(Long endDate){
        this.endDate=endDate;
    }
    @Enumerated(EnumType.STRING) @Column(length=10)
    public StatusType getStatusType(){
        return statusType;
    }
    public void setStatusType(StatusType statusType){
        this.statusType=statusType;
    }
    @Enumerated(EnumType.STRING) @Column(length=10)
    public MeetingType getMeetingType(){
        return meetingType;
    }
    public void setMeetingType(MeetingType meetingType){
        this.meetingType=meetingType;
    }
    @ManyToOne @JoinColumn(nullable=false, name="room_id") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    public KnMeetingRoom getKmr(){
        return kmr;
    }
    public void setKmr(KnMeetingRoom kmr){
        this.kmr=kmr;
    }
    @OneToMany(mappedBy="km") @Fetch(FetchMode.SUBSELECT) @JsonIgnore
    public List<KnMeetingFile> getKmf(){
        return kmf;
    }
    public void setKmf(List<KnMeetingFile> kmf){
        this.kmf=kmf;
    }
    @Column(length=500)
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description=description;
    }
    @Column(length=13)
    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId=userId;
    }
    @OneToMany(mappedBy="km")
    public List<KnMeetingAttendee> getKma(){
        return kma;
    }
    public void setKma(List<KnMeetingAttendee> kma){
        this.kma=kma;
    }
    public enum StatusType{
        NEW,PROGRESS,END,CANCEL
    }
    public enum MeetingType{
        COMPANY,DEPARTMENT
    }
    @Transient
    public String getBeginDateStr(){
        String dateStr="";
        if(beginDate!=null){
            dateStr=new SimpleDateFormat("yyyy-MM-dd").format(beginDate);
        }
        return dateStr;
    }
    @Column(length=2)
    public Integer getAttendeeCueStatus(){
        return attendeeCueStatus;
    }
    public void setAttendeeCueStatus(Integer attendeeCueStatus){
        this.attendeeCueStatus=attendeeCueStatus;
    }
    @Column(length=2)
    public Integer getSignInCueStatus(){
        return signInCueStatus;
    }
    public void setSignInCueStatus(Integer signInCueStatus){
        this.signInCueStatus=signInCueStatus;
    }
    @Column(length=2)
    public Integer getSignInCueEndStatus(){
        return signInCueEndStatus;
    }
    public void setSignInCueEndStatus(Integer signInCueEndStatus){
        this.signInCueEndStatus=signInCueEndStatus;
    }
}
