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
 * 书箱的相关购买记录和内部编码表
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_library_book_unit")
public class KnLibraryBookUnit extends AuditEntity{
    private static final long serialVersionUID=7798155771502535274L;
    private KnLibraryBook kbl;//借阅的书
    private String code;//内部编号
    private Double buyPrice;//购买价格
    private Long buyDate;//购买时间
    private BookType bookType;
    @ManyToOne @JoinColumn(nullable=false, name="book_id") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    public KnLibraryBook getKbl(){
        return kbl;
    }
    public void setKbl(KnLibraryBook kbl){
        this.kbl=kbl;
    }
    @Column(length=13)
    public String getCode(){
        return code;
    }
    public void setCode(String code){
        this.code=code;
    }
    @Column(precision=5,scale=2)
    public Double getBuyPrice(){
        return buyPrice;
    }
    public void setBuyPrice(Double buyPrice){
        this.buyPrice=buyPrice;
    }
    @Column(length=13)
    public Long getBuyDate(){
        return buyDate;
    }
    public void setBuyDate(Long buyDate){
        this.buyDate=buyDate;
    }
    @Enumerated(EnumType.STRING) @Column(length=10)
    public BookType getBookType(){
        return bookType;
    }
    public void setBookType(BookType bookType){
        this.bookType=bookType;
    }
    public enum BookType{//预约，借出，库存，作废
        RESERVE,LEND,RESTORE,SCRAPPING
    }
}
