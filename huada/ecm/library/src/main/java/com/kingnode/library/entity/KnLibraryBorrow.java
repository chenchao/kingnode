package com.kingnode.library.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kingnode.xsimple.entity.AuditEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
/**
 * 书籍借阅记录表
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_library_book_borrow")
public class KnLibraryBorrow extends AuditEntity{
    private static final long serialVersionUID=378065267948247021L;
    private KnLibraryBookUnit klbu;
    private String bookCode;//书籍编码
    private Long userId;//借阅人ID
    private String name;//借阅人姓名
    private Long reserveDate;//预借时间
    private Long lendDate;//借阅时间
    private Long restoreDate;//归还时间
    private Long renewDate;//续借时间
    private Long shouldRestoreDate;//应归还时间
    private Long timeoutDate;//申请借书，超时未领时间
    private Long cancelDate;//取消时间
    private BorrowType borrowType;
    @ManyToOne @JoinColumn(nullable=false, name="book_unit_id") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    public KnLibraryBookUnit getKlbu(){
        return klbu;
    }
    public void setKlbu(KnLibraryBookUnit klbu){
        this.klbu=klbu;
    }
    @Column(length=13)
    public String getBookCode(){
        return bookCode;
    }
    public void setBookCode(String bookCode){
        this.bookCode=bookCode;
    }
    @Column(length=13)
    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId=userId;
    }
    @Column(length=100)
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    @Column(length=13)
    public Long getReserveDate(){
        return reserveDate;
    }
    public void setReserveDate(Long reserveDate){
        this.reserveDate=reserveDate;
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
    public Long getRenewDate(){
        return renewDate;
    }
    public void setRenewDate(Long renewDate){
        this.renewDate=renewDate;
    }
    @Column(length=13)
    public Long getShouldRestoreDate(){
        return shouldRestoreDate;
    }
    public void setShouldRestoreDate(Long shouldRestoreDate){
        this.shouldRestoreDate=shouldRestoreDate;
    }
    @Column(length=13)
    public Long getTimeoutDate(){
        return timeoutDate;
    }
    public void setTimeoutDate(Long timeoutDate){
        this.timeoutDate=timeoutDate;
    }
    @Column(length=13)
    public Long getCancelDate(){
        return cancelDate;
    }
    public void setCancelDate(Long cancelDate){
        this.cancelDate=cancelDate;
    }
    @Enumerated(EnumType.STRING) @Column(length=10)
    public BorrowType getBorrowType(){
        return borrowType;
    }
    public void setBorrowType(BorrowType borrowType){
        this.borrowType=borrowType;
    }
    public enum BorrowType{//预约，取消，领用，续借,归还,逾期,超时
        RESERVE,DESELECT,LEND,RENEW,RESTORE,OVERTIME,TIMEOUT
    }
}