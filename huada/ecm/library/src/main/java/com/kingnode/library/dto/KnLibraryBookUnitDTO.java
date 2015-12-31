package com.kingnode.library.dto;
import com.kingnode.library.entity.KnLibraryBookUnit;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class KnLibraryBookUnitDTO{
    private Long id;
    private KnLibraryBookDTO klb;
    private String code;//内部编号
    private Double buyPrice;//购买价格
    private String buyDate;//购买时间
    private KnLibraryBookUnit.BookType BookType;

    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public KnLibraryBookDTO getKlb(){
        return klb;
    }
    public void setKlb(KnLibraryBookDTO klb){
        this.klb=klb;
    }
    public String getCode(){
        return code;
    }
    public void setCode(String code){
        this.code=code;
    }
    public Double getBuyPrice(){
        return buyPrice;
    }
    public void setBuyPrice(Double buyPrice){
        this.buyPrice=buyPrice;
    }
    public String getBuyDate(){
        return buyDate;
    }
    public void setBuyDate(String buyDate){
        this.buyDate=buyDate;
    }
    public KnLibraryBookUnit.BookType getBookType(){
        return BookType;
    }
    public void setBookType(KnLibraryBookUnit.BookType BookType){
        this.BookType=BookType;
    }
}
