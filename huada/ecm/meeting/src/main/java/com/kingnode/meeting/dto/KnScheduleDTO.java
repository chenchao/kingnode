package com.kingnode.meeting.dto;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@XmlRootElement(name="KnSchedule")
public class KnScheduleDTO{
    private String dateTime;//会议日期
    private String beginTime;//会议开始日期时间
    private String endTime;//会议开始时间时间
    private String title;//会议标题
    private String moderator;//会议主持人
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
    public String getModerator(){
        return moderator;
    }
    public void setModerator(String moderator){
        this.moderator=moderator;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }
}
