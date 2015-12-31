package com.kingnode.workflow.entity;
import java.beans.Transient;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
/**
 * @author chenchao
 * 流程节点表
 */
@Entity
@Table(name="kn_wf_node")
public class KnNode extends KnWorkflowBase{
    private Long tempId;//模板id
    private NodeType type;//节点类型 用户，岗位
    private Long approveId;//审批人id
    private Long parentId;//父节点id
    private String approveName;//审批名称，用户或岗位名

    public Long getTempId(){
        return tempId;
    }
    public void setTempId(Long tempId){
        this.tempId=tempId;
    }
    @Enumerated(EnumType.STRING)
    public NodeType getType(){
        return type;
    }
    public void setType(NodeType type){
        this.type=type;
    }
    public Long getApproveId(){
        return approveId;
    }
    public void setApproveId(Long approveId){
        this.approveId=approveId;
    }
    public Long getParentId(){
        return parentId;
    }
    public void setParentId(Long parentId){
        this.parentId=parentId;
    }
    public enum NodeType{//用户 部门 上级领导
        USER,DEPARTMENT,SUPERIOR
    }
    @Transient
    public String getApproveName(){
        return approveName;
    }
    public void setApproveName(String approveName){
        this.approveName=approveName;
    }
}
