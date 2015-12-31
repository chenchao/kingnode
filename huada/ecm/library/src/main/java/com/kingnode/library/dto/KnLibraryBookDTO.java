package com.kingnode.library.dto;
import java.util.List;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class KnLibraryBookDTO{
    private Long id;
    private String isbn;//书号ISBN: 9787111389187
    private String title;//标题
    private Double price;//定价: 79.00元
    private String subtitle;//副标题,
    private String author;//作者，可能有多个使用使用,分开
    private String pubDate;//出版年: 2012-8
    private String tags;//标签
    private String image;//封面图片
    private String binding;//装订
    private String translator;//译者
//    private String catalog;//目录
    private Integer pages;//页数: 440
    private String publisher;//出版者
//    private String authorIntro;//作者简介
    private String summary;//内容简介
    private Integer ratingMax;//最大评论分数
    private Integer ratingMin;//最小评论分数
    private Integer numRaters;//评论人数
    private Double average;//评论平均分数
    private String alt;//豆瓣书地址//http://book.douban.com/subject/11542973/,
    private String douban;//url=http://api.douban.com/v2/book/11542973
    private Integer inventoryNum;//库存
    private Integer canBorrowNum;//可借数量
    private List<KnLibraryBorrowDTO> klbs ;//借阅人列表

    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public String getIsbn(){
        return isbn;
    }
    public void setIsbn(String isbn){
        this.isbn=isbn;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public Double getPrice(){
        return price;
    }
    public void setPrice(Double price){
        this.price=price;
    }
    public String getSubtitle(){
        return subtitle;
    }
    public void setSubtitle(String subtitle){
        this.subtitle=subtitle;
    }
    public String getAuthor(){
        return author;
    }
    public void setAuthor(String author){
        this.author=author;
    }
    public String getPubDate(){
        return pubDate;
    }
    public void setPubDate(String pubDate){
        this.pubDate=pubDate;
    }
    public String getTags(){
        return tags;
    }
    public void setTags(String tags){
        this.tags=tags;
    }
    public String getImage(){
        return image;
    }
    public void setImage(String image){
        this.image=image;
    }
    public String getBinding(){
        return binding;
    }
    public void setBinding(String binding){
        this.binding=binding;
    }
    public String getTranslator(){
        return translator;
    }
    public void setTranslator(String translator){
        this.translator=translator;
    }
    public Integer getPages(){
        return pages;
    }
    public void setPages(Integer pages){
        this.pages=pages;
    }
    public String getPublisher(){
        return publisher;
    }
    public void setPublisher(String publisher){
        this.publisher=publisher;
    }
//    public String getAuthorIntro(){
//        return authorIntro;
//    }
//    public void setAuthorIntro(String authorIntro){
//        this.authorIntro=authorIntro;
//    }
    public String getSummary(){
        return summary;
    }
    public void setSummary(String summary){
        this.summary=summary;
    }
    public Integer getRatingMax(){
        return ratingMax;
    }
    public void setRatingMax(Integer ratingMax){
        this.ratingMax=ratingMax;
    }
    public Integer getRatingMin(){
        return ratingMin;
    }
    public void setRatingMin(Integer ratingMin){
        this.ratingMin=ratingMin;
    }
    public Integer getNumRaters(){
        return numRaters;
    }
    public void setNumRaters(Integer numRaters){
        this.numRaters=numRaters;
    }
    public Double getAverage(){
        return average;
    }
    public void setAverage(Double average){
        this.average=average;
    }
    public String getAlt(){
        return alt;
    }
    public void setAlt(String alt){
        this.alt=alt;
    }
    public String getDouban(){
        return douban;
    }
    public void setDouban(String douban){
        this.douban=douban;
    }
    public List<KnLibraryBorrowDTO> getKlbs(){
        return klbs;
    }
    public void setKlbs(List<KnLibraryBorrowDTO> klbs){
        this.klbs=klbs;
    }
    public Integer getInventoryNum(){
        return inventoryNum;
    }
    public void setInventoryNum(Integer inventoryNum){
        this.inventoryNum=inventoryNum;
    }
    public Integer getCanBorrowNum(){
        return canBorrowNum;
    }
    public void setCanBorrowNum(Integer canBorrowNum){
        this.canBorrowNum=canBorrowNum;
    }
}
