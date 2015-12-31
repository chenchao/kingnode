package com.kingnode.xsimple.dto;
import java.io.Serializable;
/**
 * 版本检测DTO
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class VersionDTO implements Serializable{
    private static final long serialVersionUID=-4286649735137207831L;
    private String appKey;//应用标示:111111111123213
    private String platform;//设备来自平台:IPHONE
    private String version="0";//版本号:1.35
    private String versionType;//版本的状态:usable
    public String getAppKey(){
        return appKey;
    }
    public void setAppKey(String appKey){
        this.appKey=appKey;
    }
    public String getPlatform(){
        return platform;
    }
    public void setPlatform(String platform){
        this.platform=platform;
    }
    public String getVersion(){
        return version;
    }
    public void setVersion(String version){
        this.version=version;
    }
    public String getVersionType(){
        return versionType;
    }
    public void setVersionType(String versionType){
        this.versionType=versionType;
    }
}