package com.kingnode.xsimple.api.system;
import java.io.Serializable;

import com.kingnode.xsimple.entity.IdEntity.ActiveType;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class EmployeePosition implements Serializable{
    private static final long serialVersionUID=747224785026335079L;
    private Long userId;
    private String loginName;
    private String userName;
    private String email;
    private ActiveType status;
    private ActiveType major;
    public EmployeePosition(){
    }
    public EmployeePosition(Long userId,String loginName,String userName,String email){
        this.userId=userId;
        this.loginName=loginName;
        this.userName=userName;
        this.email=email;
    }
    public EmployeePosition(Long userId,String loginName,String userName,String email,ActiveType status){
        this.userId=userId;
        this.loginName=loginName;
        this.userName=userName;
        this.email=email;
        this.status=status;
    }
    public EmployeePosition(Long userId,String loginName,String userName,String email,ActiveType status,ActiveType major){
        this.userId=userId;
        this.loginName=loginName;
        this.userName=userName;
        this.email=email;
        this.status=status;
        this.major=major;
    }
    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId=userId;
    }
    public String getLoginName(){
        return loginName;
    }
    public void setLoginName(String loginName){
        this.loginName=loginName;
    }
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName=userName;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public ActiveType getStatus(){
        return status;
    }
    public void setStatus(ActiveType status){
        this.status=status;
    }
    public ActiveType getMajor(){
        return major;
    }
    public void setMajor(ActiveType major){
        this.major=major;
    }
}
