package com.kingnode.xsimple.dto;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class FullEmployeeDTO extends SimpleEmployeeDTO{
    private String address;//地址
    private String sex;//性别
    private String phone;//手机ren
    private String telephone;//座机
    private String signature;//个性签名
    private String orgName;

    private String userId;//第三方ID
    private String userSystem;//来自系统
    private String userType;
    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address=address;
    }
    public String getSex(){
        return sex;
    }
    public void setSex(String sex){
        this.sex=sex;
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
    public String getSignature(){
        return signature;
    }
    public void setSignature(String signature){
        this.signature=signature;
    }
    public String getOrgName(){
        return orgName;
    }
    public void setOrgName(String orgName){
        this.orgName=orgName;
    }

    public String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId=userId;
    }
    public String getUserType(){
        return userType;
    }
    public void setUserType(String userType){
        this.userType=userType;
    }
    public String getUserSystem(){
        return userSystem;
    }
    public void setUserSystem(String userSystem){
        this.userSystem=userSystem;
    }
}
