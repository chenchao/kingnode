package com.kingnode.news.dto;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class KnArticleCommentDTO{
    private Long id;
    private Long articleId;//新闻id
    private String content;//评论内容
    private String name;//评论人姓名
    private String title;//新闻标题
    private String cTime;//评论时间
    private String depart;//部门
    private String img;//人物头像
    private Long pid;//评论父id
    private KnArticleCommentDTO pDTO;//评论父元素
    private Long createId;//评论人id

    public Long getArticleId(){
        return articleId;
    }
    public void setArticleId(Long articleId){
        this.articleId=articleId;
    }
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content=content;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public String getcTime(){
        return cTime;
    }
    public void setcTime(String cTime){
        this.cTime=cTime;
    }
    public String getDepart(){
        return depart;
    }
    public void setDepart(String depart){
        this.depart=depart;
    }
    public String getImg(){
        return img;
    }
    public void setImg(String img){
        this.img=img;
    }
    public KnArticleCommentDTO getpDTO(){
        return pDTO;
    }
    public void setpDTO(KnArticleCommentDTO pDTO){
        this.pDTO=pDTO;
    }
    public Long getPid(){
        return pid;
    }
    public void setPid(Long pid){
        this.pid=pid;
    }
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public Long getCreateId(){
        return createId;
    }
    public void setCreateId(Long createId){
        this.createId=createId;
    }
}
