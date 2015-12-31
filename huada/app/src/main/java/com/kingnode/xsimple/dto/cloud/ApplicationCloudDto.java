package com.kingnode.xsimple.dto.cloud;
import java.util.Date;

import com.kingnode.xsimple.Setting;
/**
 * 应用信息表主要用于云端更新
 */
public class ApplicationCloudDto{
    private static final long serialVersionUID=-8680175361028512930L;
    // Fields
    private String id;// 主键ID
    private String title;// 应用名称
    private String enTitle;// 英文名称
    private String icon;// 应用图标
    private String apiKey;// API Key类似于用户名
    private String secretKey;// 类似于密码(暂时不用)
    private String forFirm;// 所属企业
    private String packageName;// 应用包名
    private String creater;// 创建人
    private Date createTime;// 创建时间
    private Date updateTime;// 更新时间
    private String remark;// 备注
    private String ext1;// 扩展字段
    private String ext2;// 扩展字段
    private String downLoadUrl;//下载地址
    private Setting.WorkStatusType workStatus;//应用的状态
    private String markName;//应用的短码
    //APP应用信息表
    private String isgiftware;//是否精品  1为精品  0不是精品
    private String willadded;//即将上架  1为即将上架
    private Integer isadded;//上\下架
    private Integer downcount;//下载量
    private Integer boomcount;//畅销量
    private String fitstate;//兼容性描述
    private String qrcodeimg;//二维码
    private String applyPool;
    //VO
    private String rating;//评分星级
    private String iosver;//
    private String iosverinfo;//
    private String androidver;//
    private String androidverinfo;//
    private String createTimeStr;
    private String isgiftwareName;
    private String typeName;
    // Property accessors
    public String getId(){
        return this.id;
    }
    public void setId(String id){
        this.id=id;
    }
    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public String getEnTitle(){
        return this.enTitle;
    }
    public void setEnTitle(String enTitle){
        this.enTitle=enTitle;
    }
    public String getIcon(){
        return this.icon;
    }
    public void setIcon(String icon){
        this.icon=icon;
    }
    public String getApiKey(){
        return this.apiKey;
    }
    public void setApiKey(String apiKey){
        this.apiKey=apiKey;
    }
    public String getSecretKey(){
        return this.secretKey;
    }
    public void setSecretKey(String secretKey){
        this.secretKey=secretKey;
    }
    public String getForFirm(){
        return this.forFirm;
    }
    public void setForFirm(String forFirm){
        this.forFirm=forFirm;
    }
    public String getCreater(){
        return this.creater;
    }
    public void setCreater(String creater){
        this.creater=creater;
    }
    public Date getCreateTime(){
        return this.createTime;
    }
    public void setCreateTime(Date createTime){
        this.createTime=createTime;
    }
    public Date getUpdateTime(){
        return this.updateTime;
    }
    public void setUpdateTime(Date updateTime){
        this.updateTime=updateTime;
    }
    public String getRemark(){
        return this.remark;
    }
    public void setRemark(String remark){
        this.remark=remark;
    }
    public String getExt1(){
        return this.ext1;
    }
    public void setExt1(String ext1){
        this.ext1=ext1;
    }
    public String getExt2(){
        return this.ext2;
    }
    public void setExt2(String ext2){
        this.ext2=ext2;
    }
    public String getPackageName(){
        return packageName;
    }
    public void setPackageName(String packageName){
        this.packageName=packageName;
    }
    public String getDownLoadUrl(){
        return downLoadUrl;
    }
    public void setDownLoadUrl(String downLoadUrl){
        this.downLoadUrl=downLoadUrl;
    }
    /*	public String getZipAddress() {
            return zipAddress;
        }

        public void setZipAddress(String zipAddress) {
            this.zipAddress = zipAddress;
        }

        public String getZipVersion() {
            return zipVersion;
        }

        public void setZipVersion(String zipVersion) {
            this.zipVersion = zipVersion;
        }*/
    public Setting.WorkStatusType getWorkStatus(){
        return workStatus;
    }
    public void setWorkStatus(Setting.WorkStatusType workStatus){
        this.workStatus=workStatus;
    }
    public String getMarkName(){
        return markName;
    }
    public void setMarkName(String markName){
        this.markName=markName;
    }
    public String getIsgiftware(){
        return isgiftware;
    }
    public void setIsgiftware(String isgiftware){
        this.isgiftware=isgiftware;
    }
    public String getWilladded(){
        return willadded;
    }
    public void setWilladded(String willadded){
        this.willadded=willadded;
    }
    public Integer getIsadded(){
        return isadded;
    }
    public void setIsadded(Integer isadded){
        this.isadded=isadded;
    }
    public Integer getDowncount(){
        return downcount;
    }
    public void setDowncount(Integer downcount){
        this.downcount=downcount;
    }
    public Integer getBoomcount(){
        return boomcount;
    }
    public void setBoomcount(Integer boomcount){
        this.boomcount=boomcount;
    }
    public String getFitstate(){
        return fitstate;
    }
    public void setFitstate(String fitstate){
        this.fitstate=fitstate;
    }
    public String getQrcodeimg(){
        return qrcodeimg;
    }
    public void setQrcodeimg(String qrcodeimg){
        this.qrcodeimg=qrcodeimg;
    }
    public String getRating(){
        return rating;
    }
    public void setRating(String rating){
        this.rating=rating;
    }
    public String getIosver(){
        return iosver;
    }
    public void setIosver(String iosver){
        this.iosver=iosver;
    }
    public String getIosverinfo(){
        return iosverinfo;
    }
    public void setIosverinfo(String iosverinfo){
        this.iosverinfo=iosverinfo;
    }
    public String getAndroidver(){
        return androidver;
    }
    public void setAndroidver(String androidver){
        this.androidver=androidver;
    }
    public String getAndroidverinfo(){
        return androidverinfo;
    }
    public void setAndroidverinfo(String androidverinfo){
        this.androidverinfo=androidverinfo;
    }
    public String getCreateTimeStr(){
        return createTimeStr;
    }
    public void setCreateTimeStr(String createTimeStr){
        this.createTimeStr=createTimeStr;
    }
    public String getApplyPool(){
        return applyPool;
    }
    public void setApplyPool(String applyPool){
        this.applyPool=applyPool;
    }
    public String getIsgiftwareName(){
        return isgiftwareName;
    }
    public void setIsgiftwareName(String isgiftwareName){
        this.isgiftwareName=isgiftwareName;
    }
    public String getTypeName(){
        return typeName;
    }
    public void setTypeName(String typeName){
        this.typeName=typeName;
    }
}