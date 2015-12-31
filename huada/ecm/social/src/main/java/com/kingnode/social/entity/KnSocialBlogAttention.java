package com.kingnode.social.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kingnode.xsimple.entity.AuditEntity;
import org.joda.time.DateTime;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_social_blog_attention")
public class KnSocialBlogAttention extends AuditEntity{
    private static final long serialVersionUID=6482439726365047141L;
    private Long userId;//用户Id
    private String userName;//用户姓名
    private Long focusUserId;//被关注用户Id
    private Long attentionTime;//关注时间
    @Column(length=13)
    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId=userId;
    }
    @Column(length=13)
    public Long getFocusUserId(){
        return focusUserId;
    }
    public void setFocusUserId(Long focusUserId){
        this.focusUserId=focusUserId;
    }
    @Column(length=13)
    public Long getAttentionTime(){
        return attentionTime;
    }
    public void setAttentionTime(Long attentionTime){
        this.attentionTime=attentionTime;
    }
    @Column(length=50)
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName=userName;
    }
    @Transient
    public String getAttentionDate(){
        if(this.attentionTime!=null&&this.attentionTime.compareTo(0L)>0){
            return new DateTime(this.attentionTime).toString("yyyy-MM-dd HH:mm:ss");
        }else{
            return "";
        }
    }
}
