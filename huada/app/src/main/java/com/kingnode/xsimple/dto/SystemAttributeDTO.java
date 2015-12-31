package com.kingnode.xsimple.dto;
/**
 * 陈超
 * 系统属性
 */
public class SystemAttributeDTO{
    //private String rescode; //"200",--200成功,500失败
    private String id;
    private String telNum;//"13026608678",--联系电话
    private String email;//"asdf@163.com",--电子邮箱

    public String getTelNum(){
        return telNum;
    }
    public void setTelNum(String telNum){
        this.telNum = telNum;
    }

    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }

    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }
}
