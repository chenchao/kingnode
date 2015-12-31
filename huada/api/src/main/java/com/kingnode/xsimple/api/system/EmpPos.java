package com.kingnode.xsimple.api.system;
public class EmpPos{
    //岗位id
    private Long id;
    //是否是主岗位
    private Boolean major=false;
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public Boolean isMajor(){
        return major;
    }
    public void setMajor(Boolean major){
        this.major=major;
    }
}