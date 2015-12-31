package com.kingnode.meeting.entity;
import javax.persistence.Column;
import javax.persistence.Embeddable;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Embeddable
public class KnMeetingSignInId implements java.io.Serializable{
    private static final long serialVersionUID=2416902121230163658L;
    private Long meetingId;//会议ID
    private Long userId;//用户ID
    @Column(length=13)
    public Long getMeetingId(){
        return meetingId;
    }
    public void setMeetingId(Long meetingId){
        this.meetingId=meetingId;
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
        if(!(o instanceof KnMeetingSignInId)){
            return false;
        }
        KnMeetingSignInId that=(KnMeetingSignInId)o;
        return meetingId.equals(that.meetingId)&&userId.equals(that.userId);
    }
    @Override
    public int hashCode(){
        int result=meetingId.hashCode();
        result=31*result+userId.hashCode();
        return result;
    }
}
