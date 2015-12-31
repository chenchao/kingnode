package com.kingnode.meeting.dto;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@XmlRootElement(name="Meeting")
public class KnMeetingDTO{
    private Long id;//会议Id
    private String statusType;//会议状态
    private String title;//会议标题
    private String moderator;//会议主持人
    private String recorder;//会议纪要人
    private String attendee;// 与会人员;
    private String attendeeIds;//与会人员id集合
    private String attendeeType;//与会人人员，或者部门
    private String dateTime;//会议日期
    private String beginTime;//会议开始日期时间
    private String endTime;//会议开始时间时间
    private Long roomId;//会议室ID
    private String roomName;// 会议室
    private String meetingType;// 会议类型
    private String description;// 会议说明
    private String summary;//会议纪要
    private Integer signInBeforeTime;//会议签到提醒。会议开始时间，前15分钟
    private Integer signInEndTime;//会议签到提醒。会议结束时间，后15分钟
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public String getStatusType(){
        return statusType;
    }
    public void setStatusType(String statusType){
        this.statusType=statusType;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public String getModerator(){
        return moderator;
    }
    public void setModerator(String moderator){
        this.moderator=moderator;
    }
    public String getRecorder(){
        return recorder;
    }
    public void setRecorder(String recorder){
        this.recorder=recorder;
    }
    public String getAttendee(){
        return attendee;
    }
    public void setAttendee(String attendee){
        this.attendee=attendee;
    }
    public String getDateTime(){
        return dateTime;
    }
    public void setDateTime(String dateTime){
        this.dateTime=dateTime;
    }
    public String getBeginTime(){
        return beginTime;
    }
    public void setBeginTime(String beginTime){
        this.beginTime=beginTime;
    }
    public String getEndTime(){
        return endTime;
    }
    public void setEndTime(String endTime){
        this.endTime=endTime;
    }
    public Long getRoomId(){
        return roomId;
    }
    public void setRoomId(Long roomId){
        this.roomId=roomId;
    }
    public String getRoomName(){
        return roomName;
    }
    public void setRoomName(String roomName){
        this.roomName=roomName;
    }
    public String getMeetingType(){
        return meetingType;
    }
    public void setMeetingType(String meetingType){
        this.meetingType=meetingType;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description=description;
    }
    public String getSummary(){
        return summary;
    }
    public void setSummary(String summary){
        this.summary=summary;
    }
    public String getAttendeeIds(){
        return attendeeIds;
    }
    public void setAttendeeIds(String attendeeIds){
        this.attendeeIds=attendeeIds;
    }
    public String getAttendeeType(){
        return attendeeType;
    }
    public void setAttendeeType(String attendeeType){
        this.attendeeType=attendeeType;
    }
    public Integer getSignInBeforeTime(){
        return signInBeforeTime;
    }
    public void setSignInBeforeTime(Integer signInBeforeTime){
        this.signInBeforeTime=signInBeforeTime;
    }
    public Integer getSignInEndTime(){
        return signInEndTime;
    }
    public void setSignInEndTime(Integer signInEndTime){
        this.signInEndTime=signInEndTime;
    }
}
