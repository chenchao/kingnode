package com.kingnode.xsimple.dto;
/**
 * 陈超 意见反馈dto
 *
 */
public class FeedbackOpinionDTO{
    public String appkey;//应用的appid
    public String fromSys;//用户的来自系统
    public String linkPerop;//联系人姓名
    public String phone;//联系电话
    public String content;//反馈内容
    public String packageName;//应用包名
    public String getAppkey(){
        return appkey;
    }
    public void setAppkey(String appkey){
        this.appkey=appkey;
    }
    public String getFromSys(){
        return fromSys;
    }
    public void setFromSys(String fromSys){
        this.fromSys=fromSys;
    }
    public String getLinkPerop(){
        return linkPerop;
    }
    public void setLinkPerop(String linkPerop){
        this.linkPerop=linkPerop;
    }
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone=phone;
    }
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content=content;
    }
    public String getPackageName(){
        return packageName;
    }
    public void setPackageName(String packageName){
        this.packageName=packageName;
    }
}
