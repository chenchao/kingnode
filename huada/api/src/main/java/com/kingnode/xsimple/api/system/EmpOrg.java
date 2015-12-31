package com.kingnode.xsimple.api.system;
public class EmpOrg{
    private Long id; //组织id
    private Boolean major;//是否是主组织
    private Boolean charge;//是否是该组织主负责人
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public boolean isMajor(){
        return major;
    }
    public void setMajor(boolean major){
        this.major=major;
    }
    public boolean isCharge(){
        return charge;
    }
    public void setCharge(boolean charge){
        this.charge=charge;
    }
}