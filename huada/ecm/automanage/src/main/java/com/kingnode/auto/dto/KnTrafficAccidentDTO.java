package com.kingnode.auto.dto;
/**
 * Created by xushuangyong on 14-10-9.
 */
public class KnTrafficAccidentDTO{
    private Long id;
    //出险车辆
    private KnAutoDTO ka;
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
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public KnAutoDTO getKa(){
        return ka;
    }
    public void setKa(KnAutoDTO ka){
        this.ka=ka;
    }
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
    public String getOccurTime(){
        return occurTime;
    }
    public void setOccurTime(String occurTime){
        this.occurTime=occurTime;
    }
    public String getOccurAddress(){
        return occurAddress;
    }
    public void setOccurAddress(String occurAddress){
        this.occurAddress=occurAddress;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description=description;
    }
}

