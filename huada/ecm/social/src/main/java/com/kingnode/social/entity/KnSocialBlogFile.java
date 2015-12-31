package com.kingnode.social.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_social_blog_file")
public class KnSocialBlogFile extends AuditEntity{
    private static final long serialVersionUID=877853374819548395L;
    private String name;
    private String sha;
    private Integer fileSize;
    private String fileType;
    private KnSocialBlog ksb;
    @Column(length=200)
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    @Column(length=10)
    public Integer getFileSize(){
        return fileSize;
    }
    public void setFileSize(Integer fileSize){
        this.fileSize=fileSize;
    }
    @Column(length=100)
    public String getSha(){
        return sha;
    }
    public void setSha(String sha){
        this.sha=sha;
    }
    @Column(length=200)
    public String getFileType(){
        return fileType;
    }
    public void setFileType(String fileType){
        this.fileType=fileType;
    }
    @ManyToOne @JoinColumn(name="m_id")
    public KnSocialBlog getKsb(){
        return ksb;
    }
    public void setKsb(KnSocialBlog ksb){
        this.ksb=ksb;
    }
}
