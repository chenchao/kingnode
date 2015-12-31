package com.kingnode.library.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * 书籍信息表，主要资料来源于豆瓣网的APi
 *{
 *
 *     user:{
 *         commission:0.0000,
 *
 *     },
 *     lx:{
 *         commission:0.0005
 *     }
 *}
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_library_book")
public class KnLibraryBook extends AuditEntity{
    private static final long serialVersionUID=-469351022850617260L;
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
    private String catalog;//目录
    private Integer pages;//页数: 440
    private String publisher;//出版者
    private String authorIntro;//作者简介
    private String summary;//内容简介
    private Integer ratingMax;//最大评论分数
    private Integer ratingMin;//最小评论分数
    private Integer numRaters;//评论人数
    private Double average;//评论平均分数
    private String alt;//豆瓣书地址//http://book.douban.com/subject/11542973/,
    private String douban;//url=http://api.douban.com/v2/book/11542973
    @Column(length=13)
    public String getIsbn(){
        return isbn;
    }
    public void setIsbn(String isbn){
        this.isbn=isbn;
    }
    @Column(length=200)
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    @Column(precision=5,scale=2)
    public Double getPrice(){
        return price;
    }
    public void setPrice(Double price){
        this.price=price;
    }
    @Column(length=100)
    public String getSubtitle(){
        return subtitle;
    }
    public void setSubtitle(String subtitle){
        this.subtitle=subtitle;
    }
    @Column(length=100)
    public String getAuthor(){
        return author;
    }
    public void setAuthor(String author){
        this.author=author;
    }
    @Column(length=10)
    public String getPubDate(){
        return pubDate;
    }
    public void setPubDate(String pubDate){
        this.pubDate=pubDate;
    }
    @Column(length=200)
    public String getTags(){
        return tags;
    }
    public void setTags(String tags){
        this.tags=tags;
    }
    @Column(length=100)
    public String getImage(){
        return image;
    }
    public void setImage(String image){
        this.image=image;
    }
    @Column(length=100)
    public String getBinding(){
        return binding;
    }
    public void setBinding(String binding){
        this.binding=binding;
    }
    @Column(length=100)
    public String getTranslator(){
        return translator;
    }
    public void setTranslator(String translator){
        this.translator=translator;
    }
    @Lob
    public String getCatalog(){
        return catalog;
    }
    public void setCatalog(String catalog){
        this.catalog=catalog;
    }
    @Column(length=10)
    public Integer getPages(){
        return pages;
    }
    public void setPages(Integer pages){
        this.pages=pages;
    }
    @Column(length=100)
    public String getPublisher(){
        return publisher;
    }
    public void setPublisher(String publisher){
        this.publisher=publisher;
    }
    @Column(length=1000)
    public String getAuthorIntro(){
        return authorIntro;
    }
    public void setAuthorIntro(String authorIntro){
        this.authorIntro=authorIntro;
    }
    @Lob
    public String getSummary(){
        return summary;
    }
    public void setSummary(String summary){
        this.summary=summary;
    }
    @Column(length=2)
    public Integer getRatingMax(){
        return ratingMax;
    }
    public void setRatingMax(Integer ratingMax){
        this.ratingMax=ratingMax;
    }
    @Column(length=2)
    public Integer getRatingMin(){
        return ratingMin;
    }
    public void setRatingMin(Integer ratingMin){
        this.ratingMin=ratingMin;
    }
    @Column(length=10)
    public Integer getNumRaters(){
        return numRaters;
    }
    public void setNumRaters(Integer numRaters){
        this.numRaters=numRaters;
    }
    @Column(precision=5, scale=2)
    public Double getAverage(){
        return average;
    }
    public void setAverage(Double average){
        this.average=average;
    }
    @Column(length=100)
    public String getAlt(){
        return alt;
    }
    public void setAlt(String alt){
        this.alt=alt;
    }
    @Column(length=100)
    public String getDouban(){
        return douban;
    }
    public void setDouban(String douban){
        this.douban=douban;
    }

    /**
     * 重写hashCode方法，用于相同对象的比较
     * @return
     */
    public int hashCode(){
        return isbn.hashCode();
    }
    public boolean equals(Object obj){
        if(obj instanceof KnLibraryBook){
            KnLibraryBook p = (KnLibraryBook)obj;
            return( isbn.equals(p.getIsbn()));
        }
        return super.equals(obj);
    }
}
