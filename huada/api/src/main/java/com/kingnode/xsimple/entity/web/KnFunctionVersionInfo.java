package com.kingnode.xsimple.entity.web;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.kingnode.xsimple.Setting.WorkStatusType;
import com.kingnode.xsimple.entity.AuditEntity;

/**
 * 功能版本表,主要用于存放功能的版本信息
 *
 * @author cici
 */
@Entity
@Table(name = "kn_function_version_info")
public class KnFunctionVersionInfo extends AuditEntity {
    private static final long serialVersionUID = 4190111264430921071L;
    private Long functionId;//功能ID
    private String funcZipUrl;//页面ZIP下载URL
    private String zipVersion;//ZIP版本号
    private String clientVersion;//客户端版本
    private String interfaceUrl;//接口地址
    private String zipSize;//zip包大小
    private String funkey;//功能版本唯一标示
    private String unZipUrl;//功能zip包中的解压后存放地址,  add by 2014-6-25 cici 目前针对戴军的CRM四海通应用
    private String remark;
    private WorkStatusType workStatus;//功能的状态

    @Column(name = "funczip_url", length = 200)
    public String getFuncZipUrl() {
        return funcZipUrl;
    }

    public void setFuncZipUrl(String funcZipUrl) {
        this.funcZipUrl = funcZipUrl;
    }

    @Column(name = "zip_version", length = 10)
    public String getZipVersion() {
        return zipVersion;
    }

    public void setZipVersion(String zipVersion) {
        this.zipVersion = zipVersion;
    }

    @Column(name = "interface_url", length = 150)
    public String getInterfaceUrl() {
        return interfaceUrl;
    }

    public void setInterfaceUrl(String interfaceUrl) {
        this.interfaceUrl = interfaceUrl;
    }

    @Column(name = "zip_size", length = 10)
    public String getZipSize() {
        return zipSize;
    }

    public void setZipSize(String zipSize) {
        this.zipSize = zipSize;
    }

    @Column(name = "funkey", length = 100)
    public String getFunkey() {
        return funkey;
    }

    public void setFunkey(String funkey) {
        this.funkey = funkey;
    }

    @Column(name = "unzip_url", length = 200)
    public String getUnZipUrl() {
        return unZipUrl;
    }

    public void setUnZipUrl(String unZipUrl) {
        this.unZipUrl = unZipUrl;
    }

    @Column(name = "remark", length = 1000)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "work_status", length = 1000)
    public WorkStatusType getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(WorkStatusType workStatus) {
        this.workStatus = workStatus;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    @Column(name = "client_version", length = 10)
    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }
}