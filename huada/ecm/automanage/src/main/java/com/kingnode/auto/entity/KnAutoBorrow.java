package com.kingnode.auto.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kingnode.xsimple.entity.AuditEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.joda.time.DateTime;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_auto_borrow")
public class KnAutoBorrow extends AuditEntity{
    private static final long serialVersionUID=-6293137281214969504L;
    private KnAuto ka;//车辆
    private String departName;//部门
    private String workNo="";//工号
    private String drivingNo="";//驾驶证号
    private Integer driverYear=0;//驾龄
    private KnAutoDriver driver;
    private Long userId;//借车人ID,对应employee id
    private String name;//借车人姓名
    private Integer ridingNumber;//乘车人数
    private String destination;//目的地
    private Long applicationDate;//预借时间
    private Long approvalDate;//审批时间
    private Long rejectDate;//拒绝时间
    private Long deselectDate;//取消时间
    private Long mileage;//预计里程
    private Long lendDate;//车子预定使用开始时间
    private Long restoreDate;//车子预定结束时间
    private Long lendMileage;//起始里程
    private Long restoreMileage;//还车里程
    private String lendGauge;//起始测量
    private String restoreGauge;//还车测量
    private String lendAutoState;//借出车时车辆状态
    private String restoreAutoState;//还车时车辆状态
    private Long shouldRestoreDate;//实际归还时间
    private ActiveType driving;//领取行驶证
    private ActiveType akey;//领取钥匙
    private ActiveType rechargeable;//加油卡
    private String cause;//用车事由
    private BorrowType bt;//订单状态
    private Boolean needDriver=false;
    @Column(length=20)
    public String getWorkNo(){
        return workNo;
    }
    public void setWorkNo(String workNo){
        this.workNo=workNo;
    }
    @ManyToOne @JoinColumn(nullable=false, name="auto_id") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    public KnAuto getKa(){
        return ka;
    }
    public void setKa(KnAuto ka){
        this.ka=ka;
    }
    @Column(length=13)
    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId=userId;
    }
    @Column(length=50)
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    @Column(length=2)
    public Integer getRidingNumber(){
        return ridingNumber;
    }
    public void setRidingNumber(Integer ridingNumber){
        this.ridingNumber=ridingNumber;
    }
    @Column(length=100)
    public String getDestination(){
        return destination;
    }
    public void setDestination(String destination){
        this.destination=destination;
    }
    public Long getApplicationDate(){
        return applicationDate;
    }
    @Column(length=13)
    public void setApplicationDate(Long applicationDate){
        this.applicationDate=applicationDate;
    }
    @Column(length=5)
    public Long getMileage(){
        return mileage;
    }
    public void setMileage(Long mileage){
        this.mileage=mileage;
    }
    @Column(length=13)
    public Long getLendDate(){
        return lendDate;
    }
    public void setLendDate(Long lendDate){
        this.lendDate=lendDate;
    }
    @Column(length=13)
    public Long getRestoreDate(){
        return restoreDate;
    }
    public void setRestoreDate(Long restoreDate){
        this.restoreDate=restoreDate;
    }
    @Column(length=13)
    public Long getShouldRestoreDate(){
        return shouldRestoreDate;
    }
    public void setShouldRestoreDate(Long shouldRestoreDate){
        this.shouldRestoreDate=shouldRestoreDate;
    }
    @Column(length=500)
    public String getCause(){
        return cause;
    }
    public void setCause(String cause){
        this.cause=cause;
    }
    @Column(length=7)
    public Long getLendMileage(){
        return lendMileage;
    }
    public void setLendMileage(Long lendMileage){
        this.lendMileage=lendMileage;
    }
    @Column(length=7)
    public Long getRestoreMileage(){
        return restoreMileage;
    }
    public void setRestoreMileage(Long restoreMileage){
        this.restoreMileage=restoreMileage;
    }
    @Column(length=50)
    public String getLendGauge(){
        return lendGauge;
    }
    public void setLendGauge(String lendGauge){
        this.lendGauge=lendGauge;
    }
    @Column(length=50)
    public String getRestoreGauge(){
        return restoreGauge;
    }
    public void setRestoreGauge(String restoreGauge){
        this.restoreGauge=restoreGauge;
    }
    @Column(length=50)
    public String getLendAutoState(){
        return lendAutoState;
    }
    public void setLendAutoState(String lendAutoState){
        this.lendAutoState=lendAutoState;
    }
    @Column(length=50)
    public String getRestoreAutoState(){
        return restoreAutoState;
    }
    public void setRestoreAutoState(String restoreAutoState){
        this.restoreAutoState=restoreAutoState;
    }
    @Column(length=13)
    public Long getDeselectDate(){
        return deselectDate;
    }
    public void setDeselectDate(Long deselectDate){
        this.deselectDate=deselectDate;
    }
    @Column(length=13)
    public Long getRejectDate(){
        return rejectDate;
    }
    public void setRejectDate(Long rejectDate){
        this.rejectDate=rejectDate;
    }
    @Column(length=13)
    public Long getApprovalDate(){
        return approvalDate;
    }
    public void setApprovalDate(Long approvalDate){
        this.approvalDate=approvalDate;
    }
    @Enumerated(EnumType.STRING)
    @Column(length=10)
    public ActiveType getDriving(){
        return driving;
    }
    public void setDriving(ActiveType driving){
        this.driving=driving;
    }
    @Enumerated(EnumType.STRING)
    @Column(length=10)
    public ActiveType getAkey(){
        return akey;
    }
    public void setAkey(ActiveType akey){
        this.akey=akey;
    }
    @Enumerated(EnumType.STRING)
    @Column(length=10)
    public ActiveType getRechargeable(){
        return rechargeable;
    }
    public void setRechargeable(ActiveType rechargeable){
        this.rechargeable=rechargeable;
    }
    @Enumerated(EnumType.STRING)
    @Column(length=10)
    public BorrowType getBt(){
        return bt;
    }
    public void setBt(BorrowType bt){
        this.bt=bt;
    }
    @ManyToOne @JoinColumn(name="driver_id") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    public KnAutoDriver getDriver(){
        return driver;
    }
    public void setDriver(KnAutoDriver driver){
        this.driver=driver;
    }
    public Boolean getNeedDriver(){
        return needDriver;
    }
    public void setNeedDriver(Boolean needDriver){
        this.needDriver=needDriver;
    }
    @Column(length=20)
    public String getDrivingNo(){
        return drivingNo;
    }
    public void setDrivingNo(String drivingNo){
        this.drivingNo=drivingNo;
    }
    @Column(length=20)
    public Integer getDriverYear(){
        return driverYear;
    }
    public void setDriverYear(Integer driverYear){
        this.driverYear=driverYear;
    }
    @Column(length=20)
    public String getDepartName(){
        return departName;
    }
    public void setDepartName(String departName){
        this.departName=departName;
    }
    /**
     * 获取创建时间的时间串 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    @Transient
    public String getCreateTimeStr(){
        return this.createTime!=null?new DateTime(this.createTime).toString("yyyy-MM-dd HH:mm:ss"):null;
    }
    public enum BorrowType{//申请，审批，拒绝，取消，领用，归还,逾期,超时,预约状态用时间lendDate-restoreDate时间段控制
        APPLY,APPROVAL,REJECT,DESELECT,LEND,RESTORE,OVERTIME,TIMEOUT
    }
}
