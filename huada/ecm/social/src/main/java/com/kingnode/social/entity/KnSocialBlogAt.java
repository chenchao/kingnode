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
@Entity @Table(name="kn_social_blog_at")
public class KnSocialBlogAt extends AuditEntity{
    private static final long serialVersionUID=6170381267109626018L;
    private Long atTime;//@时间
    private Long cId;//消息ID  备注暂时不用（考虑到@评论时使用）
    private Long mId;//微博Id
    private String atUserName;//@用户姓名
    private Long atUserId;//@用户Id
    @Transient
    public String getAtDate(){
        if(this.atTime!=null&&this.atTime.compareTo(0L)>0){
            return new DateTime(this.atTime).toString("yyyy-MM-dd HH:mm:ss");
        }else{
            return "";
        }
    }
    @Column(length=13)
    public Long getAtTime(){
        return atTime;
    }
    public void setAtTime(Long atTime){
        this.atTime=atTime;
    }
    @Column(length=50)
    public String getAtUserName(){
        return atUserName;
    }
    public void setAtUserName(String atUserName){
        this.atUserName=atUserName;
    }
    @Column(length=13)
    public Long getAtUserId(){
        return atUserId;
    }
    public void setAtUserId(Long atUserId){
        this.atUserId=atUserId;
    }
    @Column(length=13)
    public Long getcId(){
        return cId;
    }
    public void setcId(Long cId){
        this.cId=cId;
    }
    @Column(length=13)
    public Long getmId(){
        return mId;
    }
    public void setmId(Long mId){
        this.mId=mId;
    }
}