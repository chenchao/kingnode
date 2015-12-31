package com.kingnode.xsimple.entity.meeting;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * 会议实体
 *
 * @author cici
 */
@Entity @Table(name="kn_meeting_info")
public class KnMeetingInfo extends AuditEntity{
    private static final long serialVersionUID=-9148943963270964557L;
    private String theme;//会议主题
    private Date startTime; //会议开始时间
    private Date endTime;//会议结束时间
    private String conductUnit; //举办单位
    private String conductAddress;//举办地址
    private String username; //接入账号
    private String password;//接入密码
    private String illustrate; //会议说明
    @Column(name="theme",length=50)
    public String getTheme(){
        return theme;
    }
    public void setTheme(String theme){
        this.theme=theme;
    }
    @Column(name="start_time")
    public Date getStartTime(){
        return startTime;
    }
    public void setStartTime(Date startTime){
        this.startTime=startTime;
    }
    @Column(name="end_time")
    public Date getEndTime(){
        return endTime;
    }
    public void setEndTime(Date endTime){
        this.endTime=endTime;
    }
    @Column(name="username",length=200)
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username=username;
    }
    @Column(name="password",length=1000)
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password=password;
    }
    @Column(name="illustrate",length=200)
    public String getIllustrate(){
        return illustrate;
    }
    public void setIllustrate(String illustrate){
        this.illustrate=illustrate;
    }
    @Column(name="conduct_unit",length=20)
    public String getConductUnit(){
        return conductUnit;
    }
    public void setConductUnit(String conductUnit){
        this.conductUnit=conductUnit;
    }
    @Column(name="conduct_address",length=80)
    public String getConductAddress(){
        return conductAddress;
    }
    public void setConductAddress(String conductAddress){
        this.conductAddress=conductAddress;
    }
}