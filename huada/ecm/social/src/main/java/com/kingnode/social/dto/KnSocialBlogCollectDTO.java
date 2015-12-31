package com.kingnode.social.dto;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@XmlRootElement(name="KnSocialBlogCollect")
public class KnSocialBlogCollectDTO{
    private Long id;
    private String collectDate;//收藏时间
    private Long userId;//用户ID
    private String userName;//用户姓名
    private Long mId;//收藏微博信息ID
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
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
    public Long getmId(){
        return mId;
    }
    public void setmId(Long mId){
        this.mId=mId;
    }
    public String getCollectDate(){
        return collectDate;
    }
    public void setCollectDate(String collectDate){
        this.collectDate=collectDate;
    }
}