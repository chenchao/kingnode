package com.kingnode.social.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kingnode.xsimple.entity.AuditEntity;
import org.joda.time.DateTime;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_social_blog_collect")
public class KnSocialBlogCollect extends AuditEntity{
    private static final long serialVersionUID=6170381267109626018L;
    private Long collectTime;//收藏时间
    private Long userId;//用户ID
    private String userName;//用户姓名
    private KnSocialBlog ksb;//收藏微博信息ID
    @Column(length=13)
    public Long getCollectTime(){
        return collectTime;
    }
    public void setCollectTime(Long collectTime){
        this.collectTime=collectTime;
    }
    @Column(length=13)
    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId=userId;
    }
    @Column(length=50)
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName=userName;
    }
    @ManyToOne @JoinColumn(name="m_id")
    public KnSocialBlog getKsb(){
        return ksb;
    }
    public void setKsb(KnSocialBlog ksb){
        this.ksb=ksb;
    }
    @Transient
    public String getCollectDate(){
        if(this.collectTime!=null&&this.collectTime.compareTo(0L)>0){
            return new DateTime(this.collectTime).toString("yyyy-MM-dd HH:mm:ss");
        }else{
            return "";
        }
    }
}