package com.kingnode.social.dto;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@XmlRootElement(name="SocialBlogUser")
public class SocialBlogUserDTO{
    private String userId;//用户Id
    private String userName;//用户姓名
    private String sex;//性别
    private String email;//邮箱
    private String imageAddress;//头像
    private String address;//地址
    private Integer socialBlogNum; //微博数量
    private Integer fansNum;//粉丝数
    private Integer attentionNum;//关注数
    private Integer collectNum;//收藏数
    private Long isAttention=0L;//是否关注 0 未关注 大于1 关注（如果是关注，该属性设置为关注信息的ID）
    private String phone;//手机
    private String telephone;//座机
    private String department;//部门
    private Long departId;//部门Id
    public String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId=userId;
    }
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName=userName;
    }
    public String getSex(){
        return sex;
    }
    public void setSex(String sex){
        this.sex=sex;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public String getImageAddress(){
        return imageAddress;
    }
    public void setImageAddress(String imageAddress){
        this.imageAddress=imageAddress;
    }
    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address=address;
    }
    public Integer getSocialBlogNum(){
        return socialBlogNum;
    }
    public void setSocialBlogNum(Integer socialBlogNum){
        this.socialBlogNum=socialBlogNum;
    }
    public Integer getFansNum(){
        return fansNum;
    }
    public void setFansNum(Integer fansNum){
        this.fansNum=fansNum;
    }
    public Integer getAttentionNum(){
        return attentionNum;
    }
    public void setAttentionNum(Integer attentionNum){
        this.attentionNum=attentionNum;
    }
    public Long getIsAttention(){
        return isAttention;
    }
    public void setIsAttention(Long isAttention){
        this.isAttention=isAttention;
    }
    public Integer getCollectNum(){
        return collectNum;
    }
    public void setCollectNum(Integer collectNum){
        this.collectNum=collectNum;
    }
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone=phone;
    }
    public String getTelephone(){
        return telephone;
    }
    public void setTelephone(String telephone){
        this.telephone=telephone;
    }
    public String getDepartment(){
        return department;
    }
    public void setDepartment(String department){
        this.department=department;
    }
    public Long getDepartId(){
        return departId;
    }
    public void setDepartId(Long departId){
        this.departId=departId;
    }
}
