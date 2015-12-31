package com.kingnode.xsimple.entity.application;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.kingnode.xsimple.Setting.ChanStatusType;
import com.kingnode.xsimple.Setting.LikeStatusType;
import com.kingnode.xsimple.entity.AuditEntity;
/**
 * 下载版本信息表
 *
 * @author cici
 */
@Entity @Table(name="kn_download_version_info")
public class KnDownloadVersionInfo extends AuditEntity{
    private static final long serialVersionUID=5076181798520348549L;
    private String phoneNum;//手机号
    private String companyName;//公司名称
    private String uaccount;//用户账号
    private String emailAddress;//用户邮箱
    private String roleName;//用户的角色
    private Long versionId;//用户下载的版本id
    private String appComment;//应用的评论
    private LikeStatusType likeStatus;//喜爱的状态，赞一下/感兴趣/未进行操作/已下载
    private String codeNum;//验证码
    private Long downloadTime;//下载时间
    private Long outTime;//过期时间
    private String ipAddress;//用户的ip地址
    private ChanStatusType channelStatus; //下载渠道信息
    @Column(name="phone_num",length=20)
    public String getPhoneNum(){
        return phoneNum;
    }
    public void setPhoneNum(String phoneNum){
        this.phoneNum=phoneNum;
    }
    @Column(name="company_name",length=200)
    public String getCompanyName(){
        return companyName;
    }
    public void setCompanyName(String companyName){
        this.companyName=companyName;
    }
    @Column(name="uaccount",length=20)
    public String getUaccount(){
        return uaccount;
    }
    public void setUaccount(String uaccount){
        this.uaccount=uaccount;
    }
    @Column(name="email_address",length=50)
    public String getEmailAddress(){
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress){
        this.emailAddress=emailAddress;
    }
    @Column(name="role_name",length=50)
    public String getRoleName(){
        return roleName;
    }
    public void setRoleName(String roleName){
        this.roleName=roleName;
    }
    @Column(name="version_id",length=40)
    public Long getVersionId(){
        return versionId;
    }
    public void setVersionId(Long versionId){
        this.versionId=versionId;
    }
    @Column(name="app_comment",length=1000)
    public String getAppComment(){
        return appComment;
    }
    public void setAppComment(String appComment){
        this.appComment=appComment;
    }
    @Enumerated(EnumType.STRING) @Column(name="likestatus",length=20)
    public LikeStatusType getLikeStatus(){
        return likeStatus;
    }
    public void setLikeStatus(LikeStatusType likeStatus){
        this.likeStatus=likeStatus;
    }
    @Column(name="code_num",length=20)
    public String getCodeNum(){
        return codeNum;
    }
    public void setCodeNum(String codeNum){
        this.codeNum=codeNum;
    }
    @Column(name="download_time")
    public Long getDownloadTime(){
        return downloadTime;
    }
    public void setDownloadTime(Long downloadTime){
        this.downloadTime=downloadTime;
    }
    @Column(name="outtime",updatable=true)
    public Long getOutTime(){
        return outTime;
    }
    public void setOutTime(Long outTime){
        this.outTime=outTime;
    }
    @Column(name="ipaddress",length=40)
    public String getIpAddress(){
        return ipAddress;
    }
    public void setIpAddress(String ipAddress){
        this.ipAddress=ipAddress;
    }
    @Enumerated(EnumType.STRING) @Column(name="channelstatus",length=20)
    public ChanStatusType getChannelStatus(){
        return channelStatus;
    }
    public void setChannelStatus(ChanStatusType channelStatus){
        this.channelStatus=channelStatus;
    }
}