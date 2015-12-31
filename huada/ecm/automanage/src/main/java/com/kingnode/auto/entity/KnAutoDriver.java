package com.kingnode.auto.entity;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_auto_driver")
public class KnAutoDriver extends AuditEntity{
    private static final long serialVersionUID=8455000960170046104L;
    private String name;//姓名
    private DriverGender gender;//性别
    private String driving;//驾驶证号
    private String expiryDate;//有效期
    private String driverYear;//驾龄
    private String drivingType;//驾照类型
    private String phone;//固定电话
    private String mobile;//移动电话
    private Long empId;//关联人员id
    private String empName;//关联人员名称
    private DriverState state;//驾驶员状态
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public DriverGender getGender(){
        return gender;
    }
    public void setGender(DriverGender gender){
        this.gender=gender;
    }
    public String getDriving(){
        return driving;
    }
    public void setDriving(String driving){
        this.driving=driving;
    }
    public String getExpiryDate(){
        return expiryDate;
    }
    public void setExpiryDate(String expiryDate){
        this.expiryDate=expiryDate;
    }
    public String getDriverYear(){
        return driverYear;
    }
    public void setDriverYear(String driverYear){
        this.driverYear=driverYear;
    }
    public String getDrivingType(){
        return drivingType;
    }
    public void setDrivingType(String drivingType){
        this.drivingType=drivingType;
    }
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone=phone;
    }
    public String getMobile(){
        return mobile;
    }
    public void setMobile(String mobile){
        this.mobile=mobile;
    }
    public DriverState getState(){
        return state;
    }
    public void setState(DriverState state){
        this.state=state;
    }
    public Long getEmpId(){
        return empId;
    }
    public void setEmpId(Long empId){
        this.empId=empId;
    }
    public String getEmpName(){
        return empName;
    }
    public void setEmpName(String empName){
        this.empName=empName;
    }
    public enum DriverState{
        STANDBY,VACATION,TURNOUT
    }
    public enum DriverGender{
        MALE,FEMALE
    }
}