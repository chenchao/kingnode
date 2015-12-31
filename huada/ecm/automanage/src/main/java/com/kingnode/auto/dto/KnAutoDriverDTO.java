package com.kingnode.auto.dto;
import com.kingnode.auto.entity.KnAuto;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class KnAutoDriverDTO{
    private Integer ridingNumber;//乘车人数
    private String cause;//用车事由
    private KnAuto atuo;
    private Long userId;//借车人ID
    private String name;//借车人姓名
    private String destination;//目的地
    private Long reserveDate;//预借时间
    private Integer mileage;//预计里程
    private Long lendDate;//用车起始时间
    private Long restoreDate;//用车归还时间
    public String getDestination(){
        return destination;
    }
    public void setDestination(String destination){
        this.destination=destination;
    }
    public Long getReserveDate(){
        return reserveDate;
    }
    public void setReserveDate(Long reserveDate){
        this.reserveDate=reserveDate;
    }
    public Integer getMileage(){
        return mileage;
    }
    public void setMileage(Integer mileage){
        this.mileage=mileage;
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
    public KnAuto getAtuo(){
        return atuo;
    }
    public void setAtuo(KnAuto atuo){
        this.atuo=atuo;
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
}
