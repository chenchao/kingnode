package com.kingnode.xsimple.dto;
/**
 * 员工的基本信息
 */
public class SimpleEmployeeDTO{
    private Long id;
    private String loginName;//用户的userId
    private String userName;//用户的全名
    private String userSystem;//用户来自系统
    private String userType;//用户类型（员工，供应商，采购商）
    private String email;
    private String imageAddress;//用户图像
    public SimpleEmployeeDTO(){
    }
    public SimpleEmployeeDTO(Long id,String loginName,String userName,String email){
        this.id=id;
        this.loginName=loginName;
        this.userName=userName;
        this.email=email;
    }
    public SimpleEmployeeDTO(Long id,String userName,String userSystem,String userType,String email){
        this.id=id;
        this.userName=userName;
        this.userSystem=userSystem;
        this.userType=userType;
        this.email=email;
    }
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public String getLoginName(){
        return loginName;
    }
    public void setLoginName(String loginName){
        this.loginName=loginName;
    }
    public void setUserName(String userName){
        this.userName=userName;
    }
    public String getUserName(){
        return userName;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public String getUserSystem(){
        return userSystem;
    }
    public void setUserSystem(String userSystem){
        this.userSystem=userSystem;
    }
    public String getUserType(){
        return userType;
    }
    public void setUserType(String userType){
        this.userType=userType;
    }
    public String getImageAddress(){
        return imageAddress;
    }
    public void setImageAddress(String imageAddress){
        this.imageAddress=imageAddress;
    }
}
