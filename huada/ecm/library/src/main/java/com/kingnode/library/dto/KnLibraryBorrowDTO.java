package com.kingnode.library.dto;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

import com.kingnode.library.entity.KnLibraryBorrow;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class KnLibraryBorrowDTO{
    private Long id;
    private String bookCode;//书籍编码
    private Long userId;//借阅人ID
    private String name;//借阅人姓名
    private String reserveDate;//预借时间
    private String lendDate;//借阅时间
    private String restoreDate;//归还时间
    private String renewDate;//续借时间
    private String shouldRestoreDate;//应归还时间
    private String cancelDate;//取消时间
    private String currentDate=(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());//系统当前时间
    private KnLibraryBorrow.BorrowType borrowType;
    private KnLibraryBookUnitDTO klbu;

    public KnLibraryBookUnitDTO getKlbu(){
        return klbu;
    }
    public void setKlbu(KnLibraryBookUnitDTO klbu){
        this.klbu=klbu;
    }
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public String getBookCode(){
        return bookCode;
    }
    public void setBookCode(String bookCode){
        this.bookCode=bookCode;
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
    public String getReserveDate(){
        return reserveDate;
    }
    public void setReserveDate(String reserveDate){
        this.reserveDate=reserveDate;
    }
    public String getLendDate(){
        return lendDate;
    }
    public void setLendDate(String lendDate){
        this.lendDate=lendDate;
    }
    public String getRestoreDate(){
        return restoreDate;
    }
    public void setRestoreDate(String restoreDate){
        this.restoreDate=restoreDate;
    }
    public String getRenewDate(){
        return renewDate;
    }
    public void setRenewDate(String renewDate){
        this.renewDate=renewDate;
    }
    public String getShouldRestoreDate(){
        return shouldRestoreDate;
    }
    public void setShouldRestoreDate(String shouldRestoreDate){
        this.shouldRestoreDate=shouldRestoreDate;
    }
    public String getCancelDate(){
        return cancelDate;
    }
    public void setCancelDate(String cancelDate){
        this.cancelDate=cancelDate;
    }
    public String getCurrentDate(){
        return currentDate;
    }
    public void setCurrentDate(String currentDate){
        this.currentDate=currentDate;
    }
    public KnLibraryBorrow.BorrowType getBorrowType(){
        return borrowType;
    }
    public void setBorrowType(KnLibraryBorrow.BorrowType borrowType){
        this.borrowType=borrowType;
    }
}
