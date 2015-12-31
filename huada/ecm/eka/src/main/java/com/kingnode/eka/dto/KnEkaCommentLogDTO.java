package com.kingnode.eka.dto;
import java.util.List;

import com.kingnode.eka.entity.KnEkaCommentLog;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class KnEkaCommentLogDTO{
    private Long userId;//用户id
    private Long ekaId;//壹卡会id
    private KnEkaCommentLog.CommentType commentType;//评论类型 赞或踩

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
    public KnEkaCommentLog.CommentType getCommentType(){
        return commentType;
    }
    public void setCommentType(KnEkaCommentLog.CommentType commentType){
        this.commentType=commentType;
    }
}
