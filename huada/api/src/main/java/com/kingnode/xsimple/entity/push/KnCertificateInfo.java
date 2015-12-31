package com.kingnode.xsimple.entity.push;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kingnode.xsimple.Setting.WorkStatusType;
import com.kingnode.xsimple.entity.AuditEntity;
import com.kingnode.xsimple.entity.application.KnApplicationInfo;
/**
 * 应用推送支持的平台信息表
 * @author cici
 */
@Entity @Table(name="kn_push_platform_info")
public class KnCertificateInfo extends AuditEntity{
    private static final long serialVersionUID=-7108106100213012678L;
    private String platformType;//平台类型
    private String certificatePath;//证书文件
    private String certificatePwd;//证书密码,不加密
    private String packageName;//应用的包名
    private String isSupport;//是否支持
    private String remark;//备注
    private WorkStatusType workStatus;//证书状态,目前就可用以及不可用
    private KnApplicationInfo applicationInfo;//所属应用
    public KnCertificateInfo(){

    }
    public KnCertificateInfo(String platformType,String certificatePath,String certificatePwd,String packageName,String isSupport,String remark,WorkStatusType workStatus,KnApplicationInfo applicationInfo){
        this.platformType=platformType;
        this.certificatePath=certificatePath;
        this.certificatePwd=certificatePwd;
        this.packageName=packageName;
        this.isSupport=isSupport;
        this.remark=remark;
        this.workStatus=workStatus;
        this.applicationInfo=applicationInfo;
    }
    @Column(name="platform_type",length =50)
    public String getPlatformType(){
        return this.platformType;
    }
    public void setPlatformType(String platformType){
        this.platformType=platformType;
    }
    @Column(name="certificate_path",length =500)
    public String getCertificatePath(){
        return this.certificatePath;
    }
    public void setCertificatePath(String certificatePath){
        this.certificatePath=certificatePath;
    }
    @Column(name="certificate_pwd",length =200)
    public String getCertificatePwd(){
        return this.certificatePwd;
    }
    public void setCertificatePwd(String certificatePwd){
        this.certificatePwd=certificatePwd;
    }
    @Column(name="is_support",length =4)
    public String getIsSupport(){
        return this.isSupport;
    }
    public void setIsSupport(String isSupport){
        this.isSupport=isSupport;
    }
    @Column(name="remark",length =1000)
    public String getRemark(){
        return this.remark;
    }
    public void setRemark(String remark){
        this.remark=remark;
    }
    @Column(name="package_name",length =50)
    public String getPackageName(){
        return packageName;
    }
    public void setPackageName(String packageName){
        this.packageName=packageName;
    }
    @Column(name="work_status",length =20) @Enumerated(EnumType.STRING)
    public WorkStatusType getWorkStatus(){
        return workStatus;
    }
    public void setWorkStatus(WorkStatusType workStatus){
        this.workStatus=workStatus;
    }
    @Transient
    public String getWorkStatusType(){
        String workStatusType="";
        if(!isEmptyString(workStatus) ){
            workStatusType=WorkStatusType.valueOf(workStatus.toString()).getTypeName() ;
        }
        return workStatusType;
    }
    @ManyToOne @JoinColumn(name="app_id")
    public KnApplicationInfo getApplicationInfo(){
        return this.applicationInfo;
    }
    public void setApplicationInfo(KnApplicationInfo applicationInfo){
        this.applicationInfo=applicationInfo;
    }
    @Transient
    public String getTypeName(){
        String typeName;
        if(!isEmptyString(this.workStatus)){
            typeName=this.workStatus.getTypeName();
        }else{
            typeName=WorkStatusType.unusable.getTypeName();
        }
        return typeName;
    }
}