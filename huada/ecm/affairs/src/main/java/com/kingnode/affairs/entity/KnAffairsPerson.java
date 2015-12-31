package com.kingnode.affairs.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * 接口人表
 */
@Entity @Table(name="kn_affairs_person")
public class KnAffairsPerson extends AuditEntity{
    private String departmentName;//部门
    private String affairsName;//事物名称
    private String name;//接口人
    private String email;//邮箱
    private String phone;//座机
    private String mobile;//手机
    private String icon;//头像
    @Column(length=200)
    public String getDepartmentName(){
        return departmentName;
    }
    public void setDepartmentName(String departmentName){
        this.departmentName=departmentName;
    }
    @Column(length=200)
    public String getAffairsName(){
        return affairsName;
    }
    public void setAffairsName(String affairsName){
        this.affairsName=affairsName;
    }
    @Column(length=200)
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    @Column(length=200)
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email=email;
    }
    @Column(length=200)
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone=phone;
    }
    @Column(length=200)
    public String getMobile(){
        return mobile;
    }
    public void setMobile(String mobile){
        this.mobile=mobile;
    }
    public String getIcon(){
        return icon;
    }
    public void setIcon(String icon){
        this.icon=icon;
    }
}
