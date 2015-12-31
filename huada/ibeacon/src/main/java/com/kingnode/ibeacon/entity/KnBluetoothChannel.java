package com.kingnode.ibeacon.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kingnode.xsimple.Setting.ChannelStatusType;
import com.kingnode.xsimple.Setting.ChannelType;
import com.kingnode.xsimple.entity.AuditEntity;
/**
 * 蓝牙信息表
 *
 * @author dengfeng
 */
@Entity @Table(name="kn_bluetooth_channel_info")
public class KnBluetoothChannel extends AuditEntity{
    private String channelId; //设备号
    private String major; //主id
    private String minor; //辅id
    private String channelName; //设备名
    private ChannelType channelType; //设备类型   电池 ,交流电
    private ChannelStatusType channelStatus; // 状态   可用, 禁用
    private String channelInfo; //设备信息  中文描述
    public KnBluetoothChannel(){
    }
    public KnBluetoothChannel(String channelId,String major,String minor,String channelName,ChannelType channelType,ChannelStatusType channelStatus,String channelInfo){
        this.channelId=channelId;
        this.major=major;
        this.minor=minor;
        this.channelName=channelName;
        this.channelType=channelType;
        this.channelStatus=channelStatus;
        this.channelInfo=channelInfo;
    }
    @Column(name="channelid",length=100)
    public String getChannelId(){
        return channelId;
    }
    public void setChannelId(String channelId){
        this.channelId=channelId;
    }
    @Column(name="major",length=100)
    public String getMajor(){
        return major;
    }
    public void setMajor(String major){
        this.major=major;
    }
    @Column(name="minor",length=50)
    public String getMinor(){
        return minor;
    }
    public void setMinor(String minor){
        this.minor=minor;
    }
    @Column(name="channelname",length=50)
    public String getChannelName(){
        return channelName;
    }
    public void setChannelName(String channelName){
        this.channelName=channelName;
    }
    @Enumerated(EnumType.STRING) @Column(name="channeltype",length=100)
    public ChannelType getChannelType(){
        return channelType;
    }
    public void setChannelType(ChannelType channelType){
        this.channelType=channelType;
    }
    @Column(name="channelInfo",length=50)
    public String getChannelInfo(){
        return channelInfo;
    }
    public void setChannelInfo(String channelInfo){
        this.channelInfo=channelInfo;
    }
    @Column(name="channelstatus",length=50) @Enumerated(EnumType.STRING)
    public ChannelStatusType getChannelStatus(){
        return channelStatus;
    }
    public void setChannelStatus(ChannelStatusType channelStatus){
        this.channelStatus=channelStatus;
    }
    @Transient
    public String getChanState(){
        String chanState=ChannelStatusType.AVAILABLE.getTypeName();
        if(!isEmptyString(this.channelStatus)&&"DISABLE".equalsIgnoreCase(this.channelStatus.name())){
            chanState=ChannelStatusType.DISABLE.getTypeName();
        }
        return chanState;
    }
    @Transient
    public String getChanType(){
        String chanType=ChannelType.BATTERY.getTypeName();
        if(!isEmptyString(this.channelType)&&"AC".equalsIgnoreCase(this.channelType.name())){
            chanType=ChannelType.AC.getTypeName();
        }
        return chanType;
    }
}
