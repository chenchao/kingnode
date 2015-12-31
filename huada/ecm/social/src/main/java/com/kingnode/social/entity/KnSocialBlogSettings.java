package com.kingnode.social.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_social_blog_settings")
public class KnSocialBlogSettings extends AuditEntity{
    private String userName;
    private String email;
    private EmailType type;
    @Column(length=50)
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName=userName;
    }
    @Column(length=200)
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email=email;
    }
    @Enumerated(EnumType.STRING) @Column(length=20)
    public EmailType getType(){
        return type;
    }
    public void setType(EmailType type){
        this.type=type;
    }
    public enum EmailType{//领导信箱、投诉建议、我要投稿
        MAILBOX,ADVICE,CONTRIBUTE
    }
}
