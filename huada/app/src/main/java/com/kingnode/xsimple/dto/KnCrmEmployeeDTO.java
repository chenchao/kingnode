package com.kingnode.xsimple.dto;
/**
 * crm 考勤信息
 * wangyifan
 */
public class KnCrmEmployeeDTO{
    private Long id;
    private String userId;//用户的userId
    private String userSystem;//来自系统
    private String userType;//用户的类型
    private String userName;//用户的全名
    private String markName;//登录账号
    public KnCrmEmployeeDTO(){
    }
    public KnCrmEmployeeDTO(Long id,String userName,String userSystem,String userId,String markName,String userType){
        this.id=id;
        this.userName=userName;
        this.userSystem=userSystem;
        this.userId=userId;
        this.markName= markName;
        this.userType=userType;
    }

    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId=userId;
    }
    public void setUserSystem(String userSystem){
        this.userSystem=userSystem;
    }
    public String getUserSystem(){
        return userSystem;
    }
    public String getUserType(){
        return userType;
    }
    public void setUserType(String userType){
        this.userType=userType;
    }
    public void setUserName(String userName){
        this.userName=userName;
    }
    public String getUserName(){
        return userName;
    }
    public String getMarkName(){
        return markName;
    }
    public void setMarkName(String markName){
        this.markName=markName;
    }
}
