package com.kingnode.xsimple.entity.application;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kingnode.xsimple.Setting.VersionType;
import com.kingnode.xsimple.Setting.WorkStatusType;
import com.kingnode.xsimple.entity.AuditEntity;
/**
 * 应用下的版本信息
 * @author cici
 */
@Entity @Table(name="kn_application_version")@Cacheable(true)
public class KnVersionInfo extends AuditEntity{
    private static final long serialVersionUID=-5743376697322270957L;
    private String num;//版本号
    private String info;//版本信息
    private String address;//下载存放地址,Android存放的是安装包的地址,ios存放的是plist的地址
    private String iosHttpsAddress;//存放ios7的https的路径的地址
    private String imgAddress;//图标logo存放地址
    private String versionSize;//版本的大小
    private Integer totalDowns;//下载的总数
    private String remark;//备注,版本的说明
    private String forcedUpdate;//是否强制更新  1是,0否
    private WorkStatusType workStatus;//版本的状态
    private VersionType type;//类型,如:ipad,android
    private KnApplicationInfo applicationInfo;//所属产品,所属应用,如:EAM，ECM
    @Column(name="num", length=20)
    public String getNum(){
        return num;
    }
    public void setNum(String num){
        this.num=num;
    }
    @Column(name="info", length=1000)
    public String getInfo(){
        return info;
    }
    public void setInfo(String info){
        this.info=info;
    }
    @Column(name="address",length=100)
    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address=address;
    }
    @Column(name="img_address",length=100)
    public String getImgAddress(){
        return imgAddress;
    }
    public void setImgAddress(String imgAddress){
        this.imgAddress=imgAddress;
    }
    @Column(name="version_size",length=20)
    public String getVersionSize(){
        return versionSize;
    }
    public void setVersionSize(String versionSize){
        this.versionSize=versionSize;
    }
    @Column(name="remark",length=2000)
    public String getRemark(){
        return remark;
    }
    public void setRemark(String remark){
        this.remark=remark;
    }
    @Column(name="total_downs",length=11)
    public Integer getTotalDowns(){
        return totalDowns;
    }
    public void setTotalDowns(Integer totalDowns){
        this.totalDowns=totalDowns;
    }
    @Column(name="ios_https_address",length=200)
    public String getIosHttpsAddress(){
        return iosHttpsAddress;
    }
    public void setIosHttpsAddress(String iosHttpsAddress){
        this.iosHttpsAddress=iosHttpsAddress;
    }
    @Column(name="forced_update",length=10)
    public String getForcedUpdate(){
        return forcedUpdate;
    }
    public void setForcedUpdate(String forcedUpdate){
        this.forcedUpdate=forcedUpdate;
    }
    @Enumerated(EnumType.STRING) @Column(name="work_status",length=20)
    public WorkStatusType getWorkStatus(){
        return workStatus;
    }
    public void setWorkStatus(WorkStatusType workStatus){
        this.workStatus=workStatus;
    }
    @Enumerated(EnumType.STRING) @Column(name="type",length=20)
    public VersionType getType(){
        return type;
    }
    public void setType(VersionType type){
        this.type=type;
    }
    @ManyToOne  @JoinColumn(name="app_id")
    public KnApplicationInfo getApplicationInfo(){
        return applicationInfo;
    }
    public void setApplicationInfo(KnApplicationInfo applicationInfo){
        this.applicationInfo=applicationInfo;
    }
    @Transient
    public String getWorkStatusName(){
        if(this.workStatus==null){
            return "不可用";
        }
        return this.workStatus.getTypeName();
    }
    @Transient
    public String getTypeName(){
        return this.type.getM_type();
    }
}