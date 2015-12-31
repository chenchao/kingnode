package com.kingnode.news.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
@Entity @Table(name="kn_article_category")
public class KnArticleCategory extends AuditEntity{
    private static final long serialVersionUID=3934808353462815399L;
    private Long pid;
    private String name;
    private String seoTitle;
    private String seoKeywords;
    private String seoDescription;
    private Integer grade;
    public Long getPid(){
        return this.pid;
    }
    public void setPid(Long pid){
        this.pid=pid;
    }
    @NotEmpty @Length(max=200) @Column(nullable=false)
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name=name;
    }
    @Length(max=200)
    public String getSeoTitle(){
        return this.seoTitle;
    }
    public void setSeoTitle(String seoTitle){
        this.seoTitle=seoTitle;
    }
    @Length(max=200)
    public String getSeoKeywords(){
        return this.seoKeywords;
    }
    public void setSeoKeywords(String seoKeywords){
        if(seoKeywords!=null){
            seoKeywords=seoKeywords.replaceAll("[,\\s]*,[,\\s]*",",").replaceAll("^,|,$","");
        }
        this.seoKeywords=seoKeywords;
    }
    @Length(max=200)
    public String getSeoDescription(){
        return this.seoDescription;
    }
    public void setSeoDescription(String seoDescription){
        this.seoDescription=seoDescription;
    }
    @Column(nullable=false)
    public Integer getGrade(){
        return this.grade;
    }
    public void setGrade(Integer grade){
        this.grade=grade;
    }
}