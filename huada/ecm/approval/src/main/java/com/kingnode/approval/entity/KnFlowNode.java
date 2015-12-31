package com.kingnode.approval.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kingnode.xsimple.entity.IdEntity;
/**
 * 预制流程节点信息
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_flow_node")
public class KnFlowNode extends IdEntity{
    private static final long serialVersionUID=5046028572445888605L;
    private KnFlow kf;
    private String name;//节点名称
    private OrgType orgType;//审批组织类型（员工，组织，团队）
    private Long approvalId;//审批人ID，此处审批人是一个相对的可以是个人，也可以是组织中部门，也可以是团队
    private String approvalName;//审批人姓名
    private NodeType nodeType;//发标识是起始，过程节点，结束节点。
    @ManyToOne @JoinColumn(nullable=false, name="flow_id") @JsonIgnore
    public KnFlow getKf(){
        return kf;
    }
    public void setKf(KnFlow kf){
        this.kf=kf;
    }
    @Column(length=60)
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    @NotNull(groups={OrgType.class}) @Column(nullable=false, length=20) @Enumerated(EnumType.STRING)
    public OrgType getOrgType(){
        return orgType;
    }
    public void setOrgType(OrgType orgType){
        this.orgType=orgType;
    }
    @Column(length=13)
    public Long getApprovalId(){
        return approvalId;
    }
    public void setApprovalId(Long approvalId){
        this.approvalId=approvalId;
    }
    @Column(length=60)
    public String getApprovalName(){
        return approvalName;
    }
    public void setApprovalName(String approvalName){
        this.approvalName=approvalName;
    }
    @NotNull(groups={NodeType.class}) @Column(nullable=false, length=20) @Enumerated(EnumType.STRING)
    public NodeType getNodeType(){
        return nodeType;
    }
    public void setNodeType(NodeType nodeType){
        this.nodeType=nodeType;
    }
    public enum OrgType{
        EMPLOYEE,ORGANIZATION,TEAM
    }
    public enum NodeType{
        START,NODE,END
    }
}
