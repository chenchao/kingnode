package com.kingnode.approval.dto;
import javax.xml.bind.annotation.XmlRootElement;

import com.kingnode.approval.entity.KnFlowNode;
@XmlRootElement(name="KnFlowNode")
public class KnFlowNodeDTO{
    private Long id;
    private String name;//节点名称
    private KnFlowNode.OrgType orgType;//审批组织类型（员工，组织，团队）
    private Long approvalId;//审批人ID，此处审批人是一个相对的可以是个人，也可以是组织中部门，也可以是团队
    private String approvalName;//审批人姓名
    private KnFlowNode.NodeType nodeType;//发标识是起始，过程节点，结束节点。
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public KnFlowNode.OrgType getOrgType(){
        return orgType;
    }
    public void setOrgType(KnFlowNode.OrgType orgType){
        this.orgType=orgType;
    }
    public Long getApprovalId(){
        return approvalId;
    }
    public void setApprovalId(Long approvalId){
        this.approvalId=approvalId;
    }
    public String getApprovalName(){
        return approvalName;
    }
    public void setApprovalName(String approvalName){
        this.approvalName=approvalName;
    }
    public KnFlowNode.NodeType getNodeType(){
        return nodeType;
    }
    public void setNodeType(KnFlowNode.NodeType nodeType){
        this.nodeType=nodeType;
    }
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
}
