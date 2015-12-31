package com.kingnode.auto.entity;
import java.beans.Transient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_auto")
public class KnAuto extends AuditEntity{
    private static final long serialVersionUID=-7439492051114373312L;
    private static final String uploadPath="/uf/automanage/photo/";//员工图像储存的目录
    private String name;//车辆名称
    private String photo;//车车辆图片
    private String brand;//品牌
    private String model;//型号
    private String plateNumber;//车牌号码
    private Integer seating;//座位数
    private String structure;//车型（两厢/三厢/MPV/SUV/跑车/敞篷车/旅行轿车/货车/客车/皮卡）
    private String engine;//发动机(1.6L 2.0T)
    private Long count=0L;//出险次数
    private Long totalMileage;//总里程
    private String totalGauge;//总油量
    private Transmission transmission;//变速箱(手动/自动／手自一体)
    private String remark;//备注
    private AutoState autoState;
    @Transient
    public static String getUploadPath(){
        return uploadPath;
    }
    public String getName(){
        return name;
    }
    @Column(length=20)
    public void setName(String name){
        this.name=name;
    }
    public String getPhoto(){
        return photo;
    }
    public void setPhoto(String photo){
        this.photo=photo;
    }
    @Column(length=20)
    public String getBrand(){
        return brand;
    }
    public void setBrand(String brand){
        this.brand=brand;
    }
    @Column(length=20)
    public String getModel(){
        return model;
    }
    public void setModel(String model){
        this.model=model;
    }
    @Column(length=20)
    public String getPlateNumber(){
        return plateNumber;
    }
    public void setPlateNumber(String plateNumber){
        this.plateNumber=plateNumber;
    }
    @Column(length=3)
    public Integer getSeating(){
        return seating;
    }
    public void setSeating(Integer seating){
        this.seating=seating;
    }
    @Column(length=20)
    public String getStructure(){
        return structure;
    }
    public void setStructure(String structure){
        this.structure=structure;
    }
    @Column(length=20, nullable=false)
    public Transmission getTransmission(){
        return transmission;
    }
    public void setTransmission(Transmission transmission){
        this.transmission=transmission;
    }
    @Column(length=20)
    public String getEngine(){
        return engine;
    }
    public void setEngine(String engine){
        this.engine=engine;
    }
    @Column(length=200)
    public String getRemark(){
        return remark;
    }
    public void setRemark(String remark){
        this.remark=remark;
    }
    @Enumerated(EnumType.STRING) @Column(length=10)
    public AutoState getAutoState(){
        return autoState;
    }
    public void setAutoState(AutoState autoState){
        this.autoState=autoState;
    }
    public Long getCount(){
        return count;
    }
    public void setCount(Long count){
        this.count=count;
    }
    public Long getTotalMileage(){
        return totalMileage;
    }
    public void setTotalMileage(Long totalMileage){
        this.totalMileage=totalMileage;
    }
    public String getTotalGauge(){
        return totalGauge;
    }
    public void setTotalGauge(String totalGauge){
        this.totalGauge=totalGauge;
    }
    public enum AutoState{
        //APPLY(被申请)，LEND(借出)，RESTORE(库存)，SCRAPPING(作废)
        //处于申请状态
        RESTORE,LEND,SCRAPPING
    }
    public enum Transmission{
        //手动，自动，手自一体
        MANUAL,AUTO,MIX
    }
}