package com.kingnode.eka.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_eka_comment_log")
public class KnEkaCommentLog extends AuditEntity{
    private Long userId;//用户id
    private Long ekaId;//壹卡会id
    private CommentType commentType;//评论类型 赞或踩

    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId=userId;
    }
    public Long getEkaId(){
        return ekaId;
    }
    public void setEkaId(Long ekaId){
        this.ekaId=ekaId;
    }
    @Enumerated(EnumType.STRING) @Column(length=10)
    public CommentType getCommentType(){
        return commentType;
    }
    public void setCommentType(CommentType commentType){
        this.commentType=commentType;
    }
    public enum CommentType{//赞，踩
        PRAISE,TREAD
    }
}
