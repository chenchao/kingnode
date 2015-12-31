package com.kingnode.xsimple.entity.web;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;

/**
 * 角色-应用对应表
 *
 * @author cici
 */
@Entity
@Table(name="kn_role_application_info")
public class KnRoleApplicationInfo extends AuditEntity{
    private static final long serialVersionUID=-6082775107616855779L;
    private Long roleId;//角色id
    private Long appId;//应用id

    @Column(name="role_id", length=40)
    public Long getRoleId(){
        return roleId;
    }

    public void setRoleId(Long roleId){
        this.roleId=roleId;
    }

    @Column(name="app_id", length=40)
    public Long getAppId(){
        return appId;
    }

    public void setAppId(Long appId){
        this.appId=appId;
    }
}