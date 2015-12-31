package com.kingnode.xsimple.entity.system;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * 客服人员信息表
 * @author cici
 */
@Entity @Table(name="kn_custom_service_info")
public class KnCustomServiceInfo extends AuditEntity{
    private static final long serialVersionUID=7829370955661684334L;
    private String fullName; //用户全名
    private String uaccount;// 账号
    private String userId; //跟 UserInfo 对象形成 外键关系
    private String fromSys; //跟 UserInfo 对象形成 外键关系
    private String userType;//用户的标识,user_type标识用“employee'， 'supplier'标识。供应商-supplier，员工-employee
    @Column(name="full_name",length=100)
    public String getFullName(){
        return fullName;
    }
    public void setFullName(String fullName){
        this.fullName=fullName;
    }
    @Column(name="user_id",length=50)
    public String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId=userId;
    }
    @Column(name="uaccount",length=20)
    public String getUaccount(){
        return uaccount;
    }
    public void setUaccount(String uaccount){
        this.uaccount=uaccount;
    }
    @Column(name="fromsys",length=50)
    public String getFromSys(){
        return fromSys;
    }
    public void setFromSys(String fromSys){
        this.fromSys=fromSys;
    }
    @Column(name="user_type",length=20)
    public String getUserType(){
        return userType;
    }
    public void setUserType(String userType){
        this.userType=userType;
    }
}
