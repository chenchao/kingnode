package com.kingnode.auto.dto;
import java.util.List;
/**
 * Created by xushuangyong on 14-10-9.
 */
public class KnAutoDTO{
    private Long id;
    private String name;//名称
    private String photo;//车车辆图片
    private String brand;//品牌
    private String model;//型号
    private String plateNumber;//车牌号码
    private Integer seating;//座位数
    private String structure;//车型（两厢/三厢/MPV/SUV/跑车/敞篷车/旅行轿车/货车/客车/皮卡）
    private String engine;//发动机(1.6L 2.0T)
    private Long count=0L;//出险次数
    private Long totalMileage=0L;//总里程
    private String totalGauge;//总油量
    private Transmission transmission;//变速箱(手动/自动)
    private String remark;//备注
    private List<String> borrowTimes;//预定时间集合
    private AutoState autoState;
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getPhoto(){
        return photo;
    }
    public void setPhoto(String photo){
        this.photo=photo;
    }
    public String getBrand(){
        return brand;
    }
    public void setBrand(String brand){
        this.brand=brand;
    }
    public String getModel(){
        return model;
    }
    public void setModel(String model){
        this.model=model;
    }
    public String getPlateNumber(){
        return plateNumber;
    }
    public void setPlateNumber(String plateNumber){
        this.plateNumber=plateNumber;
    }
    public Integer getSeating(){
        return seating;
    }
    public void setSeating(Integer seating){
        this.seating=seating;
    }
    public String getStructure(){
        return structure;
    }
    public void setStructure(String structure){
        this.structure=structure;
    }
    public String getEngine(){
        return engine;
    }
    public void setEngine(String engine){
        this.engine=engine;
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
    public Transmission getTransmission(){
        return transmission;
    }
    public void setTransmission(Transmission transmission){
        this.transmission=transmission;
    }
    public String getRemark(){
        return remark;
    }
    public void setRemark(String remark){
        this.remark=remark;
    }
    public AutoState getAutoState(){
        return autoState;
    }
    public void setAutoState(AutoState autoState){
        this.autoState=autoState;
    }
    public String getTotalGauge(){
        return totalGauge;
    }
    public void setTotalGauge(String totalGauge){
        this.totalGauge=totalGauge;
    }
    public List<String> getBorrowTimes(){
        return borrowTimes;
    }
    public void setBorrowTimes(List<String> borrowTimes){
        this.borrowTimes=borrowTimes;
    }
    public enum AutoState{
        //APPLY(被申请)，LEND(借出)，RESTORE(库存)，SCRAPPING(作废)
        //处于申请状态
        //预定表示这个车子即将有人要,借出表示车子正在使用状态,所有都不能被在预定
        APPLY,LEND,RESTORE,SCRAPPING
    }
    public enum Transmission{
        //APPLY(被申请)，LEND(借出)，RESTORE(库存)，SCRAPPING(作废)
        //处于申请状态
        //预定表示这个车子即将有人要,借出表示车子正在使用状态,所有都不能被在预定
        MANUAL,AUTO,MIX
    }
}
