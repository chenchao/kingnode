package com.kingnode.news.entity;

import com.kingnode.xsimple.entity.AuditEntity;
import org.joda.time.DateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="kn_article")
public class KnArticle extends AuditEntity{
    private static final long serialVersionUID=-1747526599901391066L;
    private String title;//标题
    //    private String author;//作者
    private String cover;//封面图片
    //    private String summary;//摘要
    private String content;//正文
    //    private String seoTitle;//seo优化关键字
    //    private String seoKeywords;
    //    private String seoDescription;
    //    private Boolean isPublication;//是否发布
    private Boolean isTop;//是否置顶 是否为头条
    //    private Boolean isRecommended;//是否推荐
    private Long hits;//浏览记录数
    private Long comments;//评论数
    private Long pubDate;//发布日期
    private Long effectiveTime;//有效时间
    //    private CommentAuthType commentAuthType;//回复级别
    private KnArticleCategory articleCategory;//新闻类别

    //    private Set<KnTag> tags=new HashSet<>();
    //    private Set<KnComment> comments=new HashSet<>();
    //    private Set<KnArticeComment> comments=new HashSet<>();//评论
    @Column(name="comments", length=13)
    public Long getComments(){
        return comments;
    }

    public void setComments(Long comments){
        this.comments=comments;
    }

    @Column(name="effective_time")
    public Long getEffectiveTime(){
        return effectiveTime;
    }

    public void setEffectiveTime(Long effectiveTime){
        this.effectiveTime=effectiveTime;
    }

    @Column(nullable=false, length=100, name="title")
    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title=title;
    }

    //    @Column(length=200,name="author",nullable=true)
    //    public String getAuthor(){
    //        return this.author;
    //    }
    //    public void setAuthor(String author){
    //        this.author=author;
    //    }
    @Column(length=400, name="cover")
    public String getCover(){
        return cover;
    }

    public void setCover(String cover){
        this.cover=cover;
    }

    //    @Column(nullable=false,length=400,name="summary")
    //    public String getSummary(){
    //        return summary;
    //    }
    //    public void setSummary(String summary){
    //        this.summary=summary;
    //    }
    @Lob
    public String getContent(){
        return this.content;
    }

    public void setContent(String content){
        this.content=content;
    }

    //    @Column(name="seo_title",length=200)
    //    public String getSeoTitle(){
    //        return this.seoTitle;
    //    }
    //    public void setSeoTitle(String seoTitle){
    //        this.seoTitle=seoTitle;
    //    }
    //    @Column(name="seo_keywords",length=200)
    //    public String getSeoKeywords(){
    //        return this.seoKeywords;
    //    }
    //    public void setSeoKeywords(String seoKeywords){
    //        if(seoKeywords!=null){
    //            seoKeywords=seoKeywords.replaceAll("[,\\s]*,[,\\s]*",",").replaceAll("^,|,$","");
    //        }
    //        this.seoKeywords=seoKeywords;
    //    }
    //    @Column(name="seo_description",length=200)
    //    public String getSeoDescription(){
    //        return this.seoDescription;
    //    }
    //    public void setSeoDescription(String seoDescription){
    //        this.seoDescription=seoDescription;
    //    }
    //    @Column(name="is_publication",nullable=false)
    //    public Boolean getIsPublication(){
    //        return this.isPublication;
    //    }
    //    public void setIsPublication(Boolean isPublication){
    //        this.isPublication=isPublication;
    //    }
    @Column(name="is_top", nullable=false)
    public Boolean getIsTop(){
        return this.isTop;
    }

    public void setIsTop(Boolean isTop){
        this.isTop=isTop;
    }

    //    @Column(name="is_recommended",nullable=false)
    //    public Boolean getIsRecommended(){
    //        return this.isRecommended;
    //    }
    //    public void setIsRecommended(Boolean isRecommended){
    //        this.isRecommended=isRecommended;
    //    }
    @Column(name="hits", nullable=false, length=13)
    public Long getHits(){
        return this.hits;
    }

    public void setHits(Long hits){
        this.hits=hits;
    }

    @Column(name="pub_date")
    public Long getPubDate(){
        return this.pubDate;
    }

    public void setPubDate(Long pubDate){
        this.pubDate=pubDate;
    }

    //    @NotNull(groups={CommentAuthType.class}) @Column(nullable=false) @Enumerated(EnumType.STRING)
    //    public CommentAuthType getCommentAuthType(){
    //        return commentAuthType;
    //    }
    //    public void setCommentAuthType(CommentAuthType commentAuthType){
    //        this.commentAuthType=commentAuthType;
    //    }
    //    @ManyToOne @JoinColumn(nullable=false) @JsonIgnore
    @ManyToOne(cascade={CascadeType.MERGE,CascadeType.REFRESH}, optional=true)
    @JoinColumn(name="article_Category")
    public KnArticleCategory getArticleCategory(){
        return this.articleCategory;
    }

    public void setArticleCategory(KnArticleCategory articleCategory){
        this.articleCategory=articleCategory;
    }

    //    @ManyToMany @JoinTable(name="kn_article_tag") @Fetch(FetchMode.SUBSELECT) @JsonIgnore
    //    public Set<KnTag> getTags(){
    //        return this.tags;
    //    }
    //    public void setTags(Set<KnTag> tags){
    //        this.tags=tags;
    //    }
    //    @ManyToMany @JoinTable(name="kn_article_comment") @Fetch(FetchMode.SUBSELECT) @JsonIgnore
    //    public Set<KnComment> getComments(){
    //        return this.comments;
    //    }
    //    public void setComments(Set<KnComment> comments){
    //        this.comments=comments;
    //    }
    //    @OneToMany @JoinTable(name="kn_aritce_comment") @Fetch(FetchMode.SUBSELECT) @JsonIgnore
    //    public Set<KnArticeComment> getComments(){
    //        return this.comments;
    //    }
    //    public void setComments(Set<KnArticeComment> comments){
    //        this.comments=comments;
    //    }
    @Transient
    public String getPubDateStr(){
        if(this.pubDate!=null&&this.pubDate.compareTo(0L)>0){
            return new DateTime(this.pubDate).toString("yyyy-MM-dd");
        }else{
            return "";
        }
    }
    //    @Transient @JsonIgnore
    //    public boolean getAnyone(){
    //        return CommentAuthType.anyone.equals(this.commentAuthType);
    //    }
    //    @Transient @JsonIgnore
    //    public boolean getAuthorization(){
    //        return CommentAuthType.authorization.equals(this.commentAuthType);
    //    }
    //    @Transient @JsonIgnore
    //    public boolean getNone(){
    //        return CommentAuthType.none.equals(this.commentAuthType);
    //    }
}