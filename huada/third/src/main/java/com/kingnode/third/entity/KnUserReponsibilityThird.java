package com.kingnode.third.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * 第三方平台的用户职责表
 *
 * @author cici
 */
@Entity @Table(name="kn_user_reponsibility_third")
public class KnUserReponsibilityThird extends AuditEntity{
    private static final long serialVersionUID=8186590501893151090L;
    private String userId;//用户id
    private String responsibilityId;//职责id
    private String responsibilityName;//职责名称
    private String startDate;//开始时间
    private String endDate;//结束时间
    private String fromSys;//来自系统
    @Column(name="userid",length=100)
    public String getUserId(){
        return this.userId;
    }
    public void setUserId(String userId){
        this.userId=userId;
    }
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
    @Column(name="start_date",length=50)
    public String getStartDate(){
        return this.startDate;
    }
    public void setStartDate(String startDate){
        this.startDate=startDate;
    }
    @Column(name="end_date",length=50)
    public String getEndDate(){
        return this.endDate;
    }
    public void setEndDate(String endDate){
        this.endDate=endDate;
    }
    @Column(name="fromsys",length=50)
    public String getFromSys(){
        return fromSys;
    }
    public void setFromSys(String fromSys){
        this.fromSys=fromSys;
    }
}