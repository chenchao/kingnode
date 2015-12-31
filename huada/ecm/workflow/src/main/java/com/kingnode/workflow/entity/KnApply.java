package com.kingnode.workflow.entity;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;
/**
 * @author chenchao
 * 申请表.
 */
@Entity
@Table(name="kn_wf_apply")
public class KnApply extends KnWorkflowBase{
    private Long tempId;//模板表id
    private Long userId;//申请人id
    private Long startTime;//开始时间
    private Long endTime;//结束时间
    private ActiveType isHalfDay;//是否半天
    private String other;//其他信息，如果是出差申请，则存的是地点和项目，如果是报销，则是保险项目等
    private String description;//描述
    
    public Long getTempId(){
        return tempId;
    }
    public void setTempId(Long tempId){
        this.tempId=tempId;
    }
    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId=userId;
    }
    public Long getStartTime(){
        return startTime;
    }
    public void setStartTime(Long startTime){
        this.startTime=startTime;
    }
    public Long getEndTime(){
        return endTime;
    }
    public void setEndTime(Long endTime){
        this.endTime=endTime;
    }
    @Enumerated(EnumType.STRING)
    public ActiveType getIsHalfDay(){
        return isHalfDay;
    }
    public void setIsHalfDay(ActiveType isHalfDay){
        this.isHalfDay=isHalfDay;
    }
    @Lob
    public String getOther(){
        return other;
    }
    public void setOther(String other){
        this.other=other;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description=description;
    }
}
