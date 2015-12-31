package com.kingnode.xsimple.dto;
import java.io.Serializable;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class MdmDTO implements Serializable{
    private static final long serialVersionUID=4929432896104147715L;
    private String appkey;//111111111123213",
    private String totken;//sdfadsfadsasdasd1123123",
    private String plateform;//IPHONE",
    private String version;//1.35",
    private String userPhoneName;//小米3",
    private String versionType;//usable",
    private String zipVersion;//1.3",
    private String userPhone;//31649714646",
    private String regTime;//2014-07-01 18:58:12",
    private String fromSys;//eam"
    public String getAppkey(){
        return appkey;
    }
    public void setAppkey(String appkey){
        this.appkey=appkey;
    }
    public String getTotken(){
        return totken;
    }
    public void setTotken(String totken){
        this.totken=totken;
    }
    public String getPlateform(){
        return plateform;
    }
    public void setPlateform(String plateform){
        this.plateform=plateform;
    }
    public String getVersion(){
        return version;
    }
    public void setVersion(String version){
        this.version=version;
    }
    public String getUserPhoneName(){
        return userPhoneName;
    }
    public void setUserPhoneName(String userPhoneName){
        this.userPhoneName=userPhoneName;
    }
    public String getVersionType(){
        return versionType;
    }
    public void setVersionType(String versionType){
        this.versionType=versionType;
    }
    public String getZipVersion(){
        return zipVersion;
    }
    public void setZipVersion(String zipVersion){
        this.zipVersion=zipVersion;
    }
    public String getUserPhone(){
        return userPhone;
    }
    public void setUserPhone(String userPhone){
        this.userPhone=userPhone;
    }
    public String getRegTime(){
        return regTime;
    }
    public void setRegTime(String regTime){
        this.regTime=regTime;
    }
    public String getFromSys(){
        return fromSys;
    }
    public void setFromSys(String fromSys){
        this.fromSys=fromSys;
    }
}
