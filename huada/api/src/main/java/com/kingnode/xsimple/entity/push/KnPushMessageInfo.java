package com.kingnode.xsimple.entity.push;

import java.text.SimpleDateFormat;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.kingnode.xsimple.Setting.MessageType;
import com.kingnode.xsimple.Setting.MsgState;
import com.kingnode.xsimple.Setting.PlateformType;
import com.kingnode.xsimple.entity.AuditEntity;

/**
 * 推送消息表（ANDROID/IOS）
 *
 * @author dengfeng
 */
@Entity
@Table(name="kn_push_message_info")
public class KnPushMessageInfo extends AuditEntity{
    private static final long serialVersionUID=-8866835978111150513L;
    private String title;//消息标题
    private String content;//消息正文内容
    private String uri;
    private String nid;
    private String token;//接收者token
    private MessageType messType; // 消息类别 用 ENUM 表示
    private String fromSys; //来自系统
    private MsgState msgState; //发送消息的状态
    private PlateformType plateMess; // 消息类型
    private String ipAddress; //接收者的ip地址
    //消息跟设备表是 manyTOmany 的关系  因  manyTOmany  效率低 ，此处配置成  manyTOone 的关系 ；在消息表中存贮多条消息信息
    private KnDeviceInfo deviceInfo;

    public KnPushMessageInfo(){

    }

    public KnPushMessageInfo(String title,String content,String uri,String nid,String token,MessageType messType,String fromSys,MsgState msgState,PlateformType plateMess,String ipAddress,KnDeviceInfo deviceInfo,Long createId,Long createTime,Long updateId,Long updateTime){
        this.title=title;
        this.content=content;
        this.uri=uri;
        this.nid=nid;
        this.token=token;
        this.messType=messType;
        this.fromSys=fromSys;
        this.msgState=msgState;
        this.plateMess=plateMess;
        this.ipAddress=ipAddress;
        this.deviceInfo=deviceInfo;

        this.setCreateId(createId);
        this.setCreateTime(createTime);
        this.setUpdateId(updateId);
        this.setUpdateTime(updateTime);
    }

    @Column(name="title", length=500)
    public String getTitle(){
        return isEmptyString(title)?"":title;
    }

    public void setTitle(String title){
        this.title=title;
    }

    @Column(name="content", length=1000)
    public String getContent(){
        return isEmptyString(content)?"":content;
    }

    public void setContent(String content){
        this.content=content;
    }

    @Column(name="uri", length=100)
    public String getUri(){
        return uri;
    }

    public void setUri(String uri){
        this.uri=uri;
    }

    @Column(name="nid", length=100)
    public String getNid(){
        return nid;
    }

    public void setNid(String nid){
        this.nid=nid;
    }

    @Column(name="token", length=500)
    public String getToken(){
        return token;
    }

    public void setToken(String token){
        this.token=token;
    }

    @NotNull(groups={MessageType.class})
    @Column(name="mess_type", length=100)
    @Enumerated(EnumType.STRING)
    public MessageType getMessType(){
        return messType;
    }

    public void setMessType(MessageType messType){
        this.messType=messType;
    }

    @Column(name="from_sys", length=100)
    public String getFromSys(){
        return isEmptyString(fromSys)?"":fromSys;
    }

    public void setFromSys(String fromSys){
        this.fromSys=fromSys;
    }

    @NotNull(groups={MsgState.class})
    @Column(name="msg_state", length=100)
    @Enumerated(EnumType.STRING)
    public MsgState getMsgState(){
        return msgState;
    }

    public void setMsgState(MsgState msgState){
        this.msgState=msgState;
    }

    @NotNull(groups={PlateformType.class})
    @Column(name="plste_mess", length=100)
    @Enumerated(EnumType.STRING)
    public PlateformType getPlateMess(){
        return plateMess;
    }

    public void setPlateMess(PlateformType plateMess){
        this.plateMess=plateMess;
    }

    @Column(name="ip_address", length=100)
    public String getIpAddress(){
        return ipAddress;
    }

    public void setIpAddress(String ipAddress){
        this.ipAddress=ipAddress;
    }


    //@NotNull @ManyToOne @JoinColumn(name="appid",insertable=true) @Length(max=40)
    //@Cascade({org.hibernate.annotations.CascadeType.n })
    @NotNull
    @ManyToOne
    @JoinColumn(name="device_id", insertable=true)
    public KnDeviceInfo getDeviceInfo(){
        return deviceInfo;
    }

    public void setDeviceInfo(KnDeviceInfo deviceInfo){
        this.deviceInfo=deviceInfo;
    }

    @Transient
    public String getMessTypeName(){
        //MessageType messType
        String messTypeName="";
        if(!isEmptyString(this.messType)){
            messTypeName=this.messType.getM_type();
        }
        return messTypeName;
    }

    @Transient
    public String getMsgStateName(){
        //MsgState msgState
        String msgStateName="";
        if(!isEmptyString(this.msgState)){
            msgStateName=this.msgState.getM_state();
        }
        return msgStateName;
    }

    @Transient
    public String getPlateMessName(){
        //PlateformType plateMess
        String plateMessName="";
        if(!isEmptyString(this.plateMess)){
            plateMessName=this.plateMess.getM_type();
        }
        return plateMessName;
    }

    @Transient
    public String getCreateTimeStr(){
        if(this.createTime==null){
            return null;
        }
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(this.createTime);
    }
}