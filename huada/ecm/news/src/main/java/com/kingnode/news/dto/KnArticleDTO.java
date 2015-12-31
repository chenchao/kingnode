package com.kingnode.news.dto;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class KnArticleDTO{
    private static final long serialVersionUID=-1747526599901391066L;
    private Long id;
    private String title;//标题
    private String cover;//封面图片
    private String summary;//摘要
    private String content;//正文
    private Boolean isTop;//是否置顶 是否为头条
    private Long hits;//浏览记录数
    private Long comments;//评论数
    private String pubDate;//发布日期
    private String createTime;//发布时间
    private String effectiveTime;//有效时间
    private String pubUserName;//发布者姓名）
//    private List<KnArticleDTO> dtoList;//新闻列表
    private KnArticleDTO top;//头条对象
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public KnArticleDTO getTop(){
        return top;
    }
    public void setTop(KnArticleDTO top){
        this.top=top;
    }
    //    public List<KnArticleDTO> getDtoList(){
//        return dtoList;
//    }
//    public void setDtoList(List<KnArticleDTO> dtoList){
//        this.dtoList=dtoList;
//    }
    public String getPubUserName(){
        return pubUserName;
    }
    public void setPubUserName(String pubUserName){
        this.pubUserName=pubUserName;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }
//    public String getAuthor(){
//        return author;
//    }
//    public void setAuthor(String author){
//        this.author=author;
//    }
    public String getCover(){
        return cover;
    }
    public void setCover(String cover){
        this.cover=cover;
    }
    public String getSummary(){
        return summary;
    }
    public void setSummary(String summary){
        this.summary=summary;
    }
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content=content;
    }
//    public String getSeoTitle(){
//        return seoTitle;
//    }
//    public void setSeoTitle(String seoTitle){
//        this.seoTitle=seoTitle;
//    }
//    public String getSeoKeywords(){
//        return seoKeywords;
//    }
//    public void setSeoKeywords(String seoKeywords){
//        this.seoKeywords=seoKeywords;
//    }
//    public String getSeoDescription(){
//        return seoDescription;
//    }
//    public void setSeoDescription(String seoDescription){
//        this.seoDescription=seoDescription;
//    }
//    public Boolean getIsPublication(){
//        return isPublication;
//    }
//    public void setIsPublication(Boolean isPublication){
//        this.isPublication=isPublication;
//    }
    public Boolean getIsTop(){
        return isTop;
    }
    public void setIsTop(Boolean isTop){
        this.isTop=isTop;
    }
//    public Boolean getIsRecommended(){
//        return isRecommended;
//    }
//    public void setIsRecommended(Boolean isRecommended){
//        this.isRecommended=isRecommended;
//    }
    public Long getHits(){
        return hits;
    }
    public void setHits(Long hits){
        this.hits=hits;
    }
    public Long getComments(){
        return comments;
    }
    public void setComments(Long comments){
        this.comments=comments;
    }
    public String getPubDate(){
        return pubDate;
    }
    public void setPubDate(String pubDate){
        this.pubDate=pubDate;
    }
    public String getEffectiveTime(){
        return effectiveTime;
    }
    public void setEffectiveTime(String effectiveTime){
        this.effectiveTime=effectiveTime;
    }
    public String getCreateTime(){
        return createTime;
    }
    public void setCreateTime(String createTime){
        this.createTime=createTime;
    }
}
