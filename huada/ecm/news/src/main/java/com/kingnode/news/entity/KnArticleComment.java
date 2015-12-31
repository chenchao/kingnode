package com.kingnode.news.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
import org.hibernate.validator.constraints.Length;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_aritcle_comment")
public class KnArticleComment extends AuditEntity{
    private Long articleId;//新闻id
    private String content;//评论内容
    private Boolean isShow;//是否屏蔽
    private Long userId;//评论人ID
    private String name;//评论人姓名
    private String title;//新闻标题
    private Long pid;//评论父id
    private Long depth;//级别深度
    private Long cTime;//评论时间

    @Column(name="artice_id")
    public Long getArticleId(){
        return articleId;
    }
    public void setArticleId(Long articleId){
        this.articleId=articleId;
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
    @Column(nullable=false,length=200,name="title")
    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    @Lob
    public String getContent(){
        return this.content;
    }
    public void setContent(String content){
        this.content=content;
    }
    @Column(name="is_top",nullable=false)
    public Boolean getIsShow(){
        return this.isShow;
    }
    public void setIsShow(Boolean isShow){
        this.isShow=isShow;
    }
    public Long getDepth(){
        return depth;
    }
    public void setDepth(Long depth){
        this.depth=depth;
    }
    public Long getPid(){
        return pid;
    }
    public void setPid(Long pid){
        this.pid=pid;
    }
    public Long getcTime(){
        return cTime;
    }
    public void setcTime(Long cTime){
        this.cTime=cTime;
    }
}
