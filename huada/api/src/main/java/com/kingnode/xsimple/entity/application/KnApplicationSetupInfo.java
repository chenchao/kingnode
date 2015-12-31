package com.kingnode.xsimple.entity.application;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * 应用设置表
 * @author cici
 */
@Entity @Table(name="kn_application_setup_info")
public class KnApplicationSetupInfo extends AuditEntity{
    private static final long serialVersionUID=1342503851506395120L;
    private String funcIpAddress;//功能的ip地址
    private String forwardUrl;// ios要打开的应用的URL
    private String iosPackage;//IOS的包名
    private String packageName;// android要打开的应用的包名
    private KnApplicationInfo applicationInfo;
    @Column(name="func_ip_address",length=100)
    public String getFuncIpAddress(){
        return funcIpAddress;
    }
    public void setFuncIpAddress(String funcIpAddress){
        this.funcIpAddress=funcIpAddress;
    }
    @Column(name="forward_url",length=100)
    public String getForwardUrl(){
        return forwardUrl;
    }
    public void setForwardUrl(String forwardUrl){
        this.forwardUrl=forwardUrl;
    }
    @Column(name="ios_package",length=100)
    public String getIosPackage(){
        return iosPackage;
    }
    public void setIosPackage(String iosPackage){
        this.iosPackage=iosPackage;
    }
    @Column(name="package_name",length=150)
    public String getPackageName(){
        return packageName;
    }
    public void setPackageName(String packageName){
        this.packageName=packageName;
    }
    @ManyToOne  @JoinColumn(name="app_id")
    public KnApplicationInfo getApplicationInfo(){
        return applicationInfo;
    }
    public void setApplicationInfo(KnApplicationInfo applicationInfo){
        this.applicationInfo=applicationInfo;
    }
}