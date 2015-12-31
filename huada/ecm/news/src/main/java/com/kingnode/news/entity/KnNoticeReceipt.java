package com.kingnode.news.entity;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.IdEntity.ActiveType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_notice_receipt") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @Cacheable(true)
public class KnNoticeReceipt implements java.io.Serializable{
    private static final long serialVersionUID=-8321278859012532029L;
    @EmbeddedId
    private KnNoticeReceiptId id;
    private String userName;//用户
    private ActiveType isReceipt;//是否发送回执
    private Long receiptTime;//回执时间
    @Column(length=13)
    public Long getreceiptTime(){
        return this.receiptTime;
    }
    public void setreceiptTime(Long receiptTime){
        this.receiptTime=receiptTime;
    }
    public KnNoticeReceipt(){
    }
    public KnNoticeReceipt(Long noticeId,Long userId){
        id=new KnNoticeReceiptId(noticeId,userId);
    }
    public KnNoticeReceiptId getId(){
        return id;
    }
    public void setId(KnNoticeReceiptId id){
        this.id=id;
    }
    @Column(length=50)
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName=userName;
    }
    @Enumerated(EnumType.STRING)
    public ActiveType getIsReceipt(){
        return isReceipt;
    }
    public void setIsReceipt(ActiveType isReceipt){
        this.isReceipt=isReceipt;
    }
    public enum UserType{
        pos,role,emp
    }
    /**
     * 重写hashCode方法，用于相同对象的比较
     *
     * @return
     */
    public int hashCode(){
        return id.getNoticeId().hashCode()+id.getUserId().hashCode();
    }
    public boolean equals(Object obj){
        if(obj instanceof KnNoticeReceipt){
            KnNoticeReceipt p=(KnNoticeReceipt)obj;
            return (id.getNoticeId().toString().equals(p.getId().getNoticeId().toString())&&id.getUserId().toString().equals(p.getId().getUserId().toString()));
        }
        return super.equals(obj);
    }
}
