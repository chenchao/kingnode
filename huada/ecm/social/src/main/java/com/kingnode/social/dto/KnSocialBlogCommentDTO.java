package com.kingnode.social.dto;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@XmlRootElement(name="KnSocialBlogComment")
public class KnSocialBlogCommentDTO{
    private Long id;
    private String comment;//评论内容
    private String status;//内容状态
    private Long mId;//微博信息ID
    private Long pId;//上一层消息得ID
    private Long userId;//用户ID
    private String userName;//用户姓名
    private String commentDate;//评论时间
    private String imageAddress;//头像
    private Integer replyNum;//回复数
    private String atUsers;//@用户集合 例如 1#张三,2#李四
    private boolean deleteStatus;//删除状态
    private String depart;//部门
    private String commentCount;//评论数量
    public String getCommentCount(){
        return commentCount;
    }
    public void setCommentCount(String commentCount){
        this.commentCount=commentCount;
    }
    public String getDepart(){
        return depart;
    }
    public void setDepart(String depart){
        this.depart=depart;
    }
    public String getComment(){
        return comment;
    }
    public void setComment(String comment){
        this.comment=comment;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status=status;
    }
    public Long getmId(){
        return mId;
    }
    public void setmId(Long mId){
        this.mId=mId;
    }
    public Long getpId(){
        return pId;
    }
    public void setpId(Long pId){
        this.pId=pId;
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
    public String getCommentDate(){
        return commentDate;
    }
    public void setCommentDate(String commentDate){
        this.commentDate=commentDate;
    }
    public String getImageAddress(){
        return imageAddress;
    }
    public void setImageAddress(String imageAddress){
        this.imageAddress=imageAddress;
    }
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public Integer getReplyNum(){
        return replyNum;
    }
    public void setReplyNum(Integer replyNum){
        this.replyNum=replyNum;
    }
    public String getAtUsers(){
        return atUsers;
    }
    public void setAtUsers(String atUsers){
        this.atUsers=atUsers;
    }
    public boolean isDeleteStatus(){
        return deleteStatus;
    }
    public void setDeleteStatus(boolean deleteStatus){
        this.deleteStatus=deleteStatus;
    }
}
