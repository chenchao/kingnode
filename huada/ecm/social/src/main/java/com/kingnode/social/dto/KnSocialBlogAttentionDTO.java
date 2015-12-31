package com.kingnode.social.dto;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@XmlRootElement(name="KnSocialBlogAttention")
public class KnSocialBlogAttentionDTO{
    private Long userId;//用户Id
    private String userName;//用户姓名
    private Long focusUserId;//被关注用户Id
    private String attentionDate;//关注时间
    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId=userId;
    }
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName=userName;
    }
    public Long getFocusUserId(){
        return focusUserId;
    }
    public void setFocusUserId(Long focusUserId){
        this.focusUserId=focusUserId;
    }
    public String getAttentionDate(){
        return attentionDate;
    }
    public void setAttentionDate(String attentionDate){
        this.attentionDate=attentionDate;
    }
}
