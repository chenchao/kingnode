package com.kingnode.xsimple.entity.push;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kingnode.xsimple.Setting.DeleteStatusType;
import com.kingnode.xsimple.Setting.OnlineType;
import com.kingnode.xsimple.Setting.VersionType;
import com.kingnode.xsimple.Setting.WorkStatusType;
import com.kingnode.xsimple.entity.AuditEntity;
import com.kingnode.xsimple.entity.application.KnApplicationInfo;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
/**
 * 设备信息表
 *
 * @author cici
 */
@Entity @Table(name="kn_device_info")
public class KnDeviceInfo extends AuditEntity{
    private static final long serialVersionUID=4772851853983093538L;
    private String deviceToken;// 设备token
    private VersionType deviceType;// 设备类型 ios.android
    private String remark;// 备注
    private String formSystem; //来自于哪个产品
    private String chversion;//版本号
    private String uaccount;//用户账号
    private String onlineStat; //dengfeng  在线状态 online：在线  , off:下线
    private DeleteStatusType delState;//设备的擦除状态,isdelete("已擦除"),dodelete("待擦除"), nodelete("未擦除")
    private String userPhone;//手机号码
    private String deviceName; //设备名
    private WorkStatusType workStatus;// 版本的状态
    private String pushMessname; //android推送消息的账号 转成大写进行存贮  deviceToken + Setting.LINE_SEPEAC + loginName + Setting.LINE_SEPEAC + applicationInfo.id + Setting.LINE_SEPEAC + fromSystem  //862620029404367#¤∮ΘΨknd2#¤∮ΘΨff80808142efa45b0142f04414fb1560#¤∮ΘΨeam
    //private KnApplicationInfo applicationInfo;// 所属应用
    // KnApplicationInfo 中的应用标题字段   start
    private Long appId ;
    private String apiKey;
    private String appTitle ;
    // KnApplicationInfo 中的应用标题字段 end

    // KnEmployee 表跟 KnUser 表中的信息字段 start
    private String userSystem;//用户来自系统
    private String userId;//用户ID 对应  KnEmployee  主键id
    private String loginName;//登录账号
    // KnEmployee 表跟 KnUser 表中的信息字段 end
    private Set<KnPushMessageInfo> messageList;
    @Column(name="device_token", length=150)
    public String getDeviceToken(){
        return deviceToken;
    }
    public void setDeviceToken(String deviceToken){
        this.deviceToken=deviceToken;
    }
    @NotNull(groups={VersionType.class}) @Column(name="device_type", length=20) @Enumerated(EnumType.STRING)
    public VersionType getDeviceType(){
        return deviceType;
    }
    public KnDeviceInfo(){
    }
    public void setDeviceType(VersionType deviceType){
        this.deviceType=deviceType;
    }
    @Column(name="remark", length=1000)
    public String getRemark(){
        return remark;
    }
    public void setRemark(String remark){
        this.remark=remark;
    }
    @Column(name="form_system", length=100)
    public String getFormSystem(){
        return formSystem;
    }
    public void setFormSystem(String formSystem){
        this.formSystem=formSystem;
    }
    @Column(name="chversion", length=100)
    public String getChversion(){
        return chversion;
    }
    public void setChversion(String chversion){
        this.chversion=chversion;
    }
    @Column(name="online_stat", length=10)
    public String getOnlineStat(){
        return onlineStat;
    }
    public void setOnlineStat(String onlineStat){
        this.onlineStat=onlineStat;
    }
    @NotNull(groups={DeleteStatusType.class}) @Column(name="delstate", length=100) @Enumerated(EnumType.STRING)
    public DeleteStatusType getDelState(){
        return delState;
    }
    public void setDelState(DeleteStatusType delState){
        this.delState=delState;
    }
    @Column(name="user_phone")
    public String getUserPhone(){
        return userPhone;
    }
    public void setUserPhone(String userPhone){
        this.userPhone=userPhone;
    }
    @Column(name="device_name")
    public String getDeviceName(){
        return isEmptyString(deviceName)?"":deviceName ;
    }
    public void setDeviceName(String deviceName){
        this.deviceName=deviceName;
    }
    @NotNull(groups={WorkStatusType.class}) @Column(name="work_status", length=20) @Enumerated(EnumType.STRING)
    public WorkStatusType getWorkStatus(){
        return workStatus;
    }
    public void setWorkStatus(WorkStatusType workStatus){
        this.workStatus=workStatus;
    }
    @Column(name="push_messname", length=300)
    public String getPushMessname(){
        return pushMessname;
    }
    public void setPushMessname(String pushMessname){
        this.pushMessname=pushMessname;
    }
    @Column(name="user_system", length=100)
    public String getUserSystem(){
        return userSystem;
    }
    public void setUserSystem(String userSystem){
        this.userSystem=userSystem;
    }
    @Column(name="user_id", length=100)
    public String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId=userId;
    }
    @Column(name="login_name", length=100)
    public String getLoginName(){
        return loginName;
    }
    public void setLoginName(String loginName){
        this.loginName=loginName;
    }
    /*@ManyToOne @JoinColumn(name="appid")
    public KnApplicationInfo getApplicationInfo(){
        return applicationInfo;
    }
    public void setApplicationInfo(KnApplicationInfo knApplicationInfo){
        this.applicationInfo=knApplicationInfo;
    }*/
    @Column(name="app_id", length=100)
    public Long getAppId(){
        return appId;
    }
    public void setAppId(Long appId){
        this.appId=appId;
    }
    @Column(name="api_key",length=100)
    public String getApiKey(){
        return apiKey;
    }
    public void setApiKey(String apiKey){
        this.apiKey=apiKey;
    }
    @Column(name="app_title", length=100)
    public String getAppTitle(){
        return appTitle;
    }
    public void setAppTitle(String appTitle){
        this.appTitle=appTitle;
    }
    @Transient
    public String getUaccount(){
        return uaccount;
    }
    public void setUaccount(String uaccount){
        this.uaccount=uaccount;
    }
    @Transient
    public String getTypeName(){
        //页面显示模块状态名字
        String typeName="";
        if(!isEmptyString(this.delState)){
            typeName=this.delState.getTypeName();
        }
        return typeName;
    }
    @Transient
    public String getStyleCss(){
        //页面模块样式
        String styleCss="label-danger"; //擦除状态
        if(!isEmptyString(this.delState)&&"nodelete".equals(this.delState.name())){
            styleCss="label-success";
        }
        return styleCss;
    }
    @Transient
    public String getOnlineOff(){
        String onlineOff="已退出";
        if(!isEmptyString(this.onlineStat)&&OnlineType.online.name().equals(this.onlineStat)){
            onlineOff="最近登录";
        }
        return onlineOff;
    }
    @Transient
    public String getStyleOnlineCss(){
        //页面模块样式
        String styleOnlineCss="label-default";
        if(!isEmptyString(this.onlineStat)&&OnlineType.online.name().equals(this.onlineStat)){
            styleOnlineCss="label-success";
        }
        return styleOnlineCss;
    }
    @OneToMany(mappedBy="deviceInfo") @Fetch(FetchMode.SUBSELECT) @org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    @Cascade({org.hibernate.annotations.CascadeType.DELETE}) @JsonIgnore
    public Set<KnPushMessageInfo> getMessageList(){
        return messageList;
    }
    public void setMessageList(Set<KnPushMessageInfo> messageList){
        this.messageList=messageList;
    }
}



