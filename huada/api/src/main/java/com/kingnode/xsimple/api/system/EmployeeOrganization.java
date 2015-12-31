package com.kingnode.xsimple.api.system;
import java.io.Serializable;

import com.kingnode.xsimple.entity.IdEntity.ActiveType;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class EmployeeOrganization implements Serializable{
    private static final long serialVersionUID=747224785026335079L;
    private Long userId;
    private String loginName;
    private String userName;
    private String email;
    private ActiveType status;
    private int charge=0;
    private int major=0;
    public EmployeeOrganization(){
    }
    public EmployeeOrganization(Long userId,String loginName,String userName,String email){
        this.userId=userId;
        this.loginName=loginName;
        this.userName=userName;
        this.email=email;
    }
    public EmployeeOrganization(Long userId,String loginName,String userName,String email,ActiveType status,int charge,int major){
        this.userId=userId;
        this.loginName=loginName;
        this.userName=userName;
        this.email=email;
        this.status=status;
        this.charge=charge;
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
    public int getCharge(){
        return charge;
    }
    public void setCharge(int charge){
        this.charge=charge;
    }
    public int getMajor(){
        return major;
    }
    public void setMajor(int major){
        this.major=major;
    }
}
