package com.kingnode.third.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * 第三方平台的职责表
 * @author cici
 */
@Entity @Table(name="kn_reponsibility_third")
public class KnReponsibilityThird extends AuditEntity{
    private static final long serialVersionUID=-1983477490267315537L;
    private String responsibilityId;//职责id
    private String responsibilityName;//职责名称
    private String fromSys;//来自系统
    @Column(name="responsibility_id",length=100)
    public String getResponsibilityId(){
        return this.responsibilityId;
    }
    public void setResponsibilityId(String responsibilityId){
        this.responsibilityId=responsibilityId;
    }
    @Column(name="responsibility_name",length=200)
    public String getResponsibilityName(){
        return this.responsibilityName;
    }
    public void setResponsibilityName(String responsibilityName){
        this.responsibilityName=responsibilityName;
    }
    @Column(name="fromsys",length=50)
    public String getFromSys(){
        return fromSys;
    }
    public void setFromSys(String fromSys){
        this.fromSys=fromSys;
    }
}