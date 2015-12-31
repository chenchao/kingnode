package com.kingnode.auto.entity;
import java.beans.Transient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
/**
 * Created by xushuangyong on 14-9-29.
 */
@Entity @Table(name="kn_traffic_accident")
public class KnTrafficAccident extends AuditEntity{
    //出险车辆
    private KnAuto ka;
    //kaId
    private Long kaId;
    //出险姓名
    private String name;
    //发生时间
    private String occurTime;
    //发生地点
    private String occurAddress;
    //事故描述
    private String description;
    @ManyToOne
    @JoinColumn(nullable=false, name="auto_id") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    public KnAuto getKa(){
        return ka;
    }
    public void setKa(KnAuto ka){
        this.ka=ka;
    }
    @Column(length=20)
    public String getOccurTime(){
        return occurTime;
    }
    public void setOccurTime(String occurTime){
        this.occurTime=occurTime;
    }
    @Column(length=200)
    public String getOccurAddress(){
        return occurAddress;
    }
    public void setOccurAddress(String occurAddress){
        this.occurAddress=occurAddress;
    }
    @Column(length=500)
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description=description;
    }
    //@Transient
    public Long getKaId(){
        return kaId;
    }
    public void setKaId(Long kaId){
        this.kaId=kaId;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
}
