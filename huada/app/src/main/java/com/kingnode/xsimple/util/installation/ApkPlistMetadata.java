package com.kingnode.xsimple.util.installation;
/**
 * Apple应用元数据模型
 * 用户获取到的android和IOS安装包里面的信息,包含包名，描述，版本等信息
 */
public class ApkPlistMetadata{
    private String packageName;//应用的包名
    private String discription;//应用的描述
    private String versionNum;//应用的版本信息
    public String getPackageName(){
        return packageName;
    }
    public void setPackageName(String packageName){
        this.packageName=packageName;
    }
    public String getDiscription(){
        return discription;
    }
    public void setDiscription(String discription){
        this.discription=discription;
    }
    public String getVersionNum(){
        return versionNum;
    }
    public void setVersionNum(String versionNum){
        this.versionNum=versionNum;
    }
}