package com.kingnode.workflow.dto;
/**
 * 节点DTO
 * @author huang an ding
 */
public class KnNodeDTO{
    private Long id;
    private Long tempId; //模板ID
    private String type; //节点类型(USER或DEPARTMENT或SUPERIOR)
    private Long approveId; //审批ID(类型为USER存的是用户ID，类型为DEPARTMENT,存的是组织ID)
    private String approveName;//审批名称
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public Long getTempId(){
        return tempId;
    }
    public void setTempId(Long tempId){
        this.tempId=tempId;
    }
    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type=type;
    }
    public Long getApproveId(){
        return approveId;
    }
    public void setApproveId(Long approveId){
        this.approveId=approveId;
    }
    public String getApproveName(){
        return approveName;
    }
    public void setApproveName(String approveName){
        this.approveName=approveName;
    }
}
