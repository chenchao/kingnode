package com.kingnode.news.entity;
import javax.persistence.Column;
import javax.persistence.Embeddable;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Embeddable
public class KnNoticeReceiptId implements java.io.Serializable{
    private static final long serialVersionUID=1472752312013021980L;
    private Long noticeId;
    private Long userId;
    public KnNoticeReceiptId(){
    }
    public KnNoticeReceiptId(Long noticeId,Long userId){
        this.noticeId=noticeId;
        this.userId=userId;
    }
    @Column(length=13)
    public Long getNoticeId(){
        return noticeId;
    }
    public void setNoticeId(Long noticeId){
        this.noticeId=noticeId;
    }
    @Column(length=13)
    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId=userId;
    }
    @Override
    public boolean equals(Object o){
        if(this==o){
            return true;
        }
        if(!(o instanceof KnNoticeReceiptId)){
            return false;
        }
        KnNoticeReceiptId that=(KnNoticeReceiptId)o;
        return noticeId.equals(that.noticeId)&&userId.equals(that.userId);
    }
    @Override
    public int hashCode(){
        int result=noticeId.hashCode();
        result=31*result+userId.hashCode();
        return result;
    }
}
