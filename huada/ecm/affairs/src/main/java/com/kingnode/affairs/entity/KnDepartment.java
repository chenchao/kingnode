package com.kingnode.affairs.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
@Entity @Table(name="kn_department")
public class KnDepartment extends AuditEntity{
    private String departmentName;//部门名称
    @Column(length=200)
    public String getDepartmentName(){
        return departmentName;
    }
    public void setDepartmentName(String departmentName){
        this.departmentName=departmentName;
    }
}
