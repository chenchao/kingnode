package com.kingnode.approval.dto;
import javax.xml.bind.annotation.XmlRootElement;

import com.kingnode.approval.entity.KnApprovalProcess;
@XmlRootElement(name="KnApprovalProcess")
public class KnApprovalProcessDTO{
    private Long id;
    private Long approvalId;//申请ID
    private Long flowId;//预制流程节点ID,来源KnFlowNode表，当存在flowid的时候说明此节点在固定流程中被重新分配人员审批了
    private Long formId;//上一流程ID
    private Long userId;//处理人ID
    private String userName;//处理人姓名
    private String comment;//处理意见
    private Long nextUserId;//下一审批人id
    private String nextUserName;//下一审批人姓名
    private KnApprovalProcess.ResultType resultType;//结果类型
    public Long getFlowId(){
        return flowId;
    }
    public void setFlowId(Long flowId){
        this.flowId=flowId;
    }
    public Long getFormId(){
        return formId;
    }
    public void setFormId(Long formId){
        this.formId=formId;
    }
    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId=userId;
    }
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName=userName;
    }
    public String getComment(){
        return comment;
    }
    public void setComment(String comment){
        this.comment=comment;
    }
    public KnApprovalProcess.ResultType getResultType(){
        return resultType;
    }
    public void setResultType(KnApprovalProcess.ResultType resultType){
        this.resultType=resultType;
    }
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public Long getNextUserId(){
        return nextUserId;
    }
    public void setNextUserId(Long nextUserId){
        this.nextUserId=nextUserId;
    }
    public String getNextUserName(){
        return nextUserName;
    }
    public void setNextUserName(String nextUserName){
        this.nextUserName=nextUserName;
    }
    public Long getApprovalId(){
        return approvalId;
    }
    public void setApprovalId(Long approvalId){
        this.approvalId=approvalId;
    }
}
