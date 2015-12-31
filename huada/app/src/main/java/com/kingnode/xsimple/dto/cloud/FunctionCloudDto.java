package com.kingnode.xsimple.dto.cloud;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.entity.web.KnFunctionVersionInfo;
/**
 * 功能信息表主要用于云端更新
 */
public class FunctionCloudDto{
    private static final long serialVersionUID=-8680175361028512930L;
    private String id;//主键ID
    private String mtitle;//功能标题
    private String enTitle;//英文名称
    private String icon;//功能图标
    private String creater;//创建人
    private Date createTime;//创建时间
    private Date updateTime;//更新时间
    private String remark;//备注
    private String ext1;//扩展字段
    private String ext2;//扩展字段
    private String markName;//标识名称，标志
    private Setting.WorkStatusType workStatus;//功能的状态
    private String funkey;//功能唯一标示
    private String version;//功能的版本号
    //vo  用于排序
    private Integer sortId;
    private Set<KnFunctionVersionInfo> funVerSet = new HashSet<KnFunctionVersionInfo>();
    private String createTimeStr;
    private String funcZipUrl;//页面ZIP下载URL
    private String zipVersion;//ZIP版本号
    private String interfaceUrl;//接口地址
    private String zipSize;//zip包大小
    private String unZipUrl;//功能zip包中的解压后存放地址,  add by 2014-6-25 cici 目前针对戴军的CRM四海通应用
    private String clientVersion;//支持的客户端的版本
    private String typeName;
    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id=id;
    }
    public String getMtitle(){
        return mtitle;
    }
    public void setMtitle(String mtitle){
        this.mtitle=mtitle;
    }
    public String getEnTitle(){
        return enTitle;
    }
    public void setEnTitle(String enTitle){
        this.enTitle=enTitle;
    }
    public String getIcon(){
        return icon;
    }
    public void setIcon(String icon){
        this.icon=icon;
    }
    public String getCreater(){
        return creater;
    }
    public void setCreater(String creater){
        this.creater=creater;
    }
    public Date getCreateTime(){
        return createTime;
    }
    public void setCreateTime(Date createTime){
        this.createTime=createTime;
    }
    public Date getUpdateTime(){
        return updateTime;
    }
    public void setUpdateTime(Date updateTime){
        this.updateTime=updateTime;
    }
    public String getRemark(){
        return remark;
    }
    public void setRemark(String remark){
        this.remark=remark;
    }
    public String getExt1(){
        return ext1;
    }
    public void setExt1(String ext1){
        this.ext1=ext1;
    }
    public String getExt2(){
        return ext2;
    }
    public void setExt2(String ext2){
        this.ext2=ext2;
    }
    public String getMarkName(){
        return markName;
    }
    public void setMarkName(String markName){
        this.markName=markName;
    }
    public Setting.WorkStatusType getWorkStatus(){
        return workStatus;
    }
    public void setWorkStatus(Setting.WorkStatusType workStatus){
        this.workStatus=workStatus;
    }
    public String getFunkey(){
        return funkey;
    }
    public void setFunkey(String funkey){
        this.funkey=funkey;
    }
    public String getVersion(){
        return version;
    }
    public void setVersion(String version){
        this.version=version;
    }
    public Integer getSortId(){
        return sortId;
    }
    public void setSortId(Integer sortId){
        this.sortId=sortId;
    }
    public Set<KnFunctionVersionInfo> getFunVerSet(){
        return funVerSet;
    }
    public void setFunVerSet(Set<KnFunctionVersionInfo> funVerSet){
        this.funVerSet=funVerSet;
    }
    public String getCreateTimeStr(){
        return createTimeStr;
    }
    public void setCreateTimeStr(String createTimeStr){
        this.createTimeStr=createTimeStr;
    }
    public String getFuncZipUrl(){
        return funcZipUrl;
    }
    public void setFuncZipUrl(String funcZipUrl){
        this.funcZipUrl=funcZipUrl;
    }
    public String getZipVersion(){
        return zipVersion;
    }
    public void setZipVersion(String zipVersion){
        this.zipVersion=zipVersion;
    }
    public String getInterfaceUrl(){
        return interfaceUrl;
    }
    public void setInterfaceUrl(String interfaceUrl){
        this.interfaceUrl=interfaceUrl;
    }
    public String getZipSize(){
        return zipSize;
    }
    public void setZipSize(String zipSize){
        this.zipSize=zipSize;
    }
    public String getUnZipUrl(){
        return unZipUrl;
    }
    public void setUnZipUrl(String unZipUrl){
        this.unZipUrl=unZipUrl;
    }
    public String getClientVersion(){
        return clientVersion;
    }
    public void setClientVersion(String clientVersion){
        this.clientVersion=clientVersion;
    }
    public String getTypeName(){
        return typeName;
    }
    public void setTypeName(String typeName){
        this.typeName=typeName;
    }
}