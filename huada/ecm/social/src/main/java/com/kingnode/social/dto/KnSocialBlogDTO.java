package com.kingnode.social.dto;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kingnode.social.entity.KnSocialBlog;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@XmlRootElement(name="KnSocialBlog")
public class KnSocialBlogDTO{
    private Long id;
    private KnSocialBlog.MessageType messageType;//微博类型(暂时不用)
    private String messageInfo;//微博内容
    private String publishDate;//发布时间
    private Integer messageCollectNum;//收藏次数
    private Integer messageCommentNum;//评论次数
    private Integer messageTranspondNum;//转发次数
    private Integer messageAgreeNum;//赞同次数
    private Integer messageReadNum;//阅读次数
    private Long userId;//发表用户ID
    private String userName;//用户姓名
    private List<KnSocialBlogFileDTO> attach;//附件
    private String imageAddress;//用户头像
    private String messageSource;//信息来源 例如：魅族手机4G
    private Integer collectStatus=0;//收藏状态，0未收藏、1已收藏
    private Long parentId;//转发那条微博得ID
    private KnSocialBlogDTO parentNode;//转发得微博
    private String atUsers;//@用户id集合 例如 1,2
    private String attachTemp;//临时接受附件字符串数组
    private boolean deleteStatus;//删除状态
    private Integer type;//类型  1或者空 为论坛  2  为活动
    private String endTime;//活动截止时间

    public String getMessageInfo(){
        return messageInfo;
    }
    public void setMessageInfo(String messageInfo){
        this.messageInfo=messageInfo;
    }
    public Integer getMessageCollectNum(){
        return messageCollectNum;
    }
    public void setMessageCollectNum(Integer messageCollectNum){
        this.messageCollectNum=messageCollectNum;
    }
    public Integer getMessageCommentNum(){
        return messageCommentNum;
    }
    public void setMessageCommentNum(Integer messageCommentNum){
        this.messageCommentNum=messageCommentNum;
    }
    public Integer getMessageTranspondNum(){
        return messageTranspondNum;
    }
    public void setMessageTranspondNum(Integer messageTranspondNum){
        this.messageTranspondNum=messageTranspondNum;
    }
    public Integer getMessageAgreeNum(){
        return messageAgreeNum;
    }
    public void setMessageAgreeNum(Integer messageAgreeNum){
        this.messageAgreeNum=messageAgreeNum;
    }
    public Integer getMessageReadNum(){
        return messageReadNum;
    }
    public void setMessageReadNum(Integer messageReadNum){
        this.messageReadNum=messageReadNum;
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
    public List<KnSocialBlogFileDTO> getAttach(){
        return attach;
    }
    public void setAttach(List<KnSocialBlogFileDTO> attach){
        this.attach=attach;
    }
    public String getImageAddress(){
        return imageAddress;
    }
    public void setImageAddress(String imageAddress){
        this.imageAddress=imageAddress;
    }
    public String getMessageSource(){
        return messageSource;
    }
    public void setMessageSource(String messageSource){
        this.messageSource=messageSource;
    }
    public Integer getCollectStatus(){
        return collectStatus;
    }
    public void setCollectStatus(Integer collectStatus){
        this.collectStatus=collectStatus;
    }
    public KnSocialBlog.MessageType getMessageType(){
        return messageType;
    }
    public void setMessageType(KnSocialBlog.MessageType messageType){
        this.messageType=messageType;
    }
    public KnSocialBlogDTO getParentNode(){
        return parentNode;
    }
    public void setParentNode(KnSocialBlogDTO parentNode){
        this.parentNode=parentNode;
    }
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public Long getParentId(){
        return parentId;
    }
    public void setParentId(Long parentId){
        this.parentId=parentId;
    }
    public String getPublishDate(){
        return publishDate;
    }
    public void setPublishDate(String publishDate){
        this.publishDate=publishDate;
    }
    public String getAtUsers(){
        return atUsers;
    }
    public void setAtUsers(String atUsers){
        this.atUsers=atUsers;
    }
    @JsonIgnore
    public String getAttachTemp(){
        return attachTemp;
    }
    public void setAttachTemp(String attachTemp){
        this.attachTemp=attachTemp;
    }
    public boolean getDeleteStatus(){
        return deleteStatus;
    }
    public void setDeleteStatus(boolean deleteStatus){
        this.deleteStatus=deleteStatus;
    }
    public Integer getType(){
        return type;
    }
    public void setType(Integer type){
        this.type=type;
    }
    public String getEndTime(){
        return endTime;
    }
    public void setEndTime(String endTime){
        this.endTime=endTime;
    }
}
