package com.kingnode.auto.dto;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class ApplyAutoDTO{
    private Integer ridingNumber;//乘车人数
    private String cause;//用车事由
    private Long userId;//借车人ID
    private String name;//借车人姓名
    private String destination;//目的地
    private Long mileage;//预计里程
    private Long lendDate;//用车起始时间
    private Long restoreDate;//用车归还时间
    private Long autoId;//要申请的车辆Id
    private boolean needDriver;//是否需要配司机
    private String departName;//部门
    private String workNo="";//工号
    private String drivingNo="";//驾驶证号
    private Integer driverYear=0;//驾龄
    public Integer getRidingNumber(){
        return ridingNumber;
    }
    public void setRidingNumber(Integer ridingNumber){
        this.ridingNumber=ridingNumber;
    }
    public String getCause(){
        return cause;
    }
    public void setCause(String cause){
        this.cause=cause;
    }
    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId=userId;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getDestination(){
        return destination;
    }
    public void setDestination(String destination){
        this.destination=destination;
    }
    public Long getMileage(){
        return mileage;
    }
    public void setMileage(Long mileage){
        this.mileage=mileage;
    }
    public Long getLendDate(){
        return lendDate;
    }
    public void setLendDate(Long lendDate){
        this.lendDate=lendDate;
    }
    public Long getRestoreDate(){
        return restoreDate;
    }
    public void setRestoreDate(Long restoreDate){
        this.restoreDate=restoreDate;
    }
    public Long getAutoId(){
        return autoId;
    }
    public void setAutoId(Long autoId){
        this.autoId=autoId;
    }
    public boolean isNeedDriver(){
        return needDriver;
    }
    public void setNeedDriver(boolean needDriver){
        this.needDriver=needDriver;
    }
    public String getDepartName(){
        return departName;
    }
    public void setDepartName(String departName){
        this.departName=departName;
    }
    public String getWorkNo(){
        return workNo;
    }
    public void setWorkNo(String workNo){
        this.workNo=workNo;
    }
    public String getDrivingNo(){
        return drivingNo;
    }
    public void setDrivingNo(String drivingNo){
        this.drivingNo=drivingNo;
    }
    public Integer getDriverYear(){
        return driverYear;
    }
    public void setDriverYear(Integer driverYear){
        this.driverYear=driverYear;
    }
}
