package com.kingnode.news.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_content_welfare")
public class KnWelfare extends AuditEntity{
    private static final long serialVersionUID=-9221529223740445381L;
    private String title;//标题
    private Long pubTime;//发布时间
    private Long pubUserId;//发布者ID
    private String pubUserName;//发布者姓名）
    private String content;//（内容）
    private String orgPath;//存组织的path
    private String orgName;//发布者组织名称
    private String img;//（如果有图片请提供）
    private Long effectiveTime;//有效时间
    private Boolean isTop;//置顶
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public Long getPubTime(){
        return pubTime;
    }
    public void setPubTime(Long pubTime){
        this.pubTime=pubTime;
    }
    public Long getPubUserId(){
        return pubUserId;
    }
    public void setPubUserId(Long pubUserId){
        this.pubUserId=pubUserId;
    }
    public String getPubUserName(){
        return pubUserName;
    }
    public void setPubUserName(String pubUserName){
        this.pubUserName=pubUserName;
    }
    @Lob
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content=content;
    }
    public String getOrgPath(){
        return orgPath;
    }
    public void setOrgPath(String orgPath){
        this.orgPath=orgPath;
    }
    public String getOrgName(){
        return orgName;
    }
    public void setOrgName(String orgName){
        this.orgName=orgName;
    }
    public String getImg(){
        return img;
    }
    public void setImg(String img){
        this.img=img;
    }
    public Long getEffectiveTime(){
        return effectiveTime;
    }
    public void setEffectiveTime(Long effectiveTime){
        this.effectiveTime=effectiveTime;
    }
    public Boolean getIsTop(){
        return isTop;
    }
    public void setIsTop(Boolean isTop){
        this.isTop=isTop;
    }
}