package com.kingnode.third.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * 第三方用户信息
 *
 * @author cici
 */
@Entity @Table(name="kn_user_third_info")
public class KnUserThirdInfo extends AuditEntity{
    private static final long serialVersionUID=623767811813203243L;
    private String userId="";//用户的id
    private String fullName="";//用户的全名
    private String uaccount="";// 账号
    private String upwd;// 密码
    private String fromSys="";// 来自哪个系统
    private String userType;//用户的标识,user_type标识用“employee'， 'supplier'标识。供应商-supplier，员工-employee
    private String markName="";//用户的标识,用于标识多个用户但为同一个人时候的唯一性
    @Column(name="mark_name",columnDefinition="varchar(50) default ''")
    public String getMarkName(){
        return markName;
    }
    public void setMarkName(String markName){
        this.markName=markName;
    }
    @Column(name="userid",columnDefinition="varchar(100) default ''")
    public String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId=userId;
    }
    @Column(name="full_name",columnDefinition="varchar(100) default ''") @NotNull
    public String getFullName(){
        return fullName;
    }
    public void setFullName(String fullName){
        this.fullName=fullName;
    }
    @NotNull @Column(name="uaccount",columnDefinition="varchar(100) default ''")
    public String getUaccount(){
        return uaccount;
    }
    public void setUaccount(String uaccount){
        this.uaccount=uaccount;
    }
    @Column(name="upwd",length=100)
    public String getUpwd(){
        return upwd;
    }
    public void setUpwd(String upwd){
        this.upwd=upwd;
    }
    @NotNull @Column(name="fromsys",columnDefinition="varchar(100) default ''")
    public String getFromSys(){
        return fromSys;
    }
    public void setFromSys(String fromSys){
        this.fromSys=fromSys;
    }
    @Column(name="user_type",columnDefinition="varchar(20) default ''")
    public String getUserType(){
        return userType;
    }
    public void setUserType(String userType){
        this.userType=userType;
    }
}