package com.kingnode.xsimple.entity.application;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kingnode.xsimple.Setting.WorkStatusType;
import com.kingnode.xsimple.entity.AuditEntity;
/**
 * 应用信息表
 *
 * @author cici
 */
@Entity @Table(name="kn_application_info")
public class KnApplicationInfo extends AuditEntity{
    private static final long serialVersionUID=-8680175361028512930L;
    private String title;// 应用名称
    private String enTitle;// 英文名称
    private String icon;// 应用图标
    private String apiKey;// API Key类似于用户名
    private String forFirm;// 所属企业
    private String remark;// 备注
    private String downLoadUrl;//下载地址
    private WorkStatusType workStatus;//应用的状态
    private String optionsRadios;// 验证方式 1无验证  2登录验证  3手机验证 4邀请码验证
    private Integer totalDowns;//下载量
    @Column(name="title",length=100)
    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    @Column(name="entitle",length=150)
    public String getEnTitle(){
        return this.enTitle;
    }
    public void setEnTitle(String enTitle){
        this.enTitle=enTitle;
    }
    @Column(name="icon",length=100)
    public String getIcon(){
        return this.icon;
    }
    public void setIcon(String icon){
        this.icon=icon;
    }
    @Column(name="api_key",length=40,updatable=false)//不可修改
    public String getApiKey(){
        return this.apiKey;
    }
    public void setApiKey(String apiKey){
        this.apiKey=apiKey;
    }
    @Column(name="forfirm",length=100)
    public String getForFirm(){
        return this.forFirm;
    }
    public void setForFirm(String forFirm){
        this.forFirm=forFirm;
    }
    @Column(name="remark",length=1000)
    public String getRemark(){
        return this.remark;
    }
    public void setRemark(String remark){
        this.remark=remark;
    }
    @Column(name="download_url",length=500)
    public String getDownLoadUrl(){
        return downLoadUrl;
    }
    public void setDownLoadUrl(String downLoadUrl){
        this.downLoadUrl=downLoadUrl;
    }
    @Enumerated(EnumType.STRING) @Column(name="work_status")
    public WorkStatusType getWorkStatus(){
        return workStatus;
    }
    public void setWorkStatus(WorkStatusType workStatus){
        this.workStatus=workStatus;
    }
    @Transient
    public String getWorkStatusName(){
        if(this.workStatus==null){
            return "不可用";
        }
        return this.workStatus.getTypeName();
    }
    @Column(name="options_radios", length=100)
    public String getOptionsRadios(){
        return optionsRadios;
    }
    public void setOptionsRadios(String optionsRadios){
        this.optionsRadios=optionsRadios;
    }
    @Column(name="total_downs",length=500)
    public Integer getTotalDowns(){
        return totalDowns;
    }
    public void setTotalDowns(Integer totalDowns){
        this.totalDowns=totalDowns;
    }
}