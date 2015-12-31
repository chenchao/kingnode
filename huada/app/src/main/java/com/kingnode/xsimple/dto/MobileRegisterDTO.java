package com.kingnode.xsimple.dto;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * @author kongjiangwei@kingnode.com
 */
@XmlRootElement(name="MobileRegister")
public class MobileRegisterDTO{
    private String xtype;
    private String userId;
    private String userSystem;
    private String appKey;//应用标示，不能为空
    private String token;//设备标示 ，不能为空
    private String plateform;//设备来自平台，不能为空"IPHONE","Android"
    private String version;//版本号
    private String userPhoneName;//设备型号
    private String versionType;//版本的状态
    private String userPhone;//手机号码
    private String regTime;//最后登录时间
    private String fromSys;//设备来自平台如eam,不能为空
    private String loginName;//登录账号,不能为空
    private boolean sendOffmessage=false;//是否需要发送离线消息 开关
    private List<ModuleDTO> moduleDTOList;//用来存放移动端的工作区菜单
    public List<ModuleDTO> getModuleDTOList(){
        return moduleDTOList;
    }
    public void setModuleDTOList(List<ModuleDTO> moduleDTOList){
        this.moduleDTOList=moduleDTOList;
    }
    public String getXtype(){
        return xtype;
    }
    public void setXtype(String xtype){
        this.xtype=xtype;
    }
    public String getAppKey(){
        return appKey;
    }
    public void setAppKey(String appKey){
        this.appKey=appKey;
    }
    public String getPlateform(){
        return plateform;
    }
    public void setPlateform(String plateform){
        this.plateform=plateform;
    }
    public String getToken(){
        return token;
    }
    public void setToken(String token){
        this.token=token;
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
    public String getLoginName(){
        return loginName;
    }
    public void setLoginName(String loginName){
        this.loginName=loginName;
    }
    public boolean isSendOffmessage(){
        return sendOffmessage;
    }
    public void setSendOffmessage(boolean sendOffmessage){
        this.sendOffmessage=sendOffmessage;
    }
    public String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId=userId;
    }
    public String getUserSystem(){
        return userSystem;
    }
    public void setUserSystem(String userSystem){
        this.userSystem=userSystem;
    }
}
