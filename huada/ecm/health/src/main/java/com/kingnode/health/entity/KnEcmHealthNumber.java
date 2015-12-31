package com.kingnode.health.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.kingnode.xsimple.entity.AuditEntity;
import org.joda.time.DateTime;
/**
 * 用来记录产生的候诊号
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_health_number")
public class KnEcmHealthNumber extends AuditEntity{
    private String day; //候诊号的排号日期  格式:yyyy-MM-dd
    private Long start; //候诊的开始时间
    private Long end; //候诊的结束时间
    private Integer number; //候诊的排序号
    private Integer type; //排号的类型，1.表示常规排号，2.表示插入排号
    private ActiveType active;//是否可用
    private Long healthId;//对应的预约对象
    private String code;//产生的候诊号

    @NotNull @Column(length=20)
    public String getDay(){
        return day;
    }
    public void setDay(String day){
        this.day=day;
    }
    @NotNull @Column(length=20)
    public Long getStart(){
        return start;
    }
    public void setStart(Long start){
        this.start=start;
    }
    @Column(length=20)
    public Long getEnd(){
        return end;
    }
    public void setEnd(Long end){
        this.end=end;
    }
    @NotNull @Column(length=20)
    public Integer getNumber(){
        return number;
    }
    public void setNumber(Integer number){
        this.number=number;
    }
    @NotNull @Column(length=20)
    public Integer getType(){
        return type;
    }
    public void setType(Integer type){
        this.type=type;
    }
    @NotNull @Enumerated(EnumType.STRING) @Column(length=20)
    public ActiveType getActive(){
        return active;
    }
    public void setActive(ActiveType active){
        this.active=active;
    }
    public Long getHealthId(){
        return healthId;
    }
    public void setHealthId(Long healthId){
        this.healthId=healthId;
    }
    @NotNull @Column(length=20)
    public String getCode(){
        return code;
    }
    public void setCode(String code){
        this.code=code;
    }
}
