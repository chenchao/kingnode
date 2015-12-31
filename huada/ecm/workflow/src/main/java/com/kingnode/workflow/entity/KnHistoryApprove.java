package com.kingnode.workflow.entity;
import java.beans.Transient;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * @author chenchao
 * 审批历史信息
 */
@Entity
@Table(name="kn_wf_history_approve")
public class KnHistoryApprove  extends KnWorkflowBase{
    private Long tempId;//模板表id
    private Long nodeId;//步骤表id
    private Long approveId;//审批人id
    private Long applyId;//申请表id
    private KnNode.NodeType type;//节点类型 用户，岗位
    private Long submitTime;//审批时间
    private ApproveStatus approveStatus;//审批状态   通过  驳回  撤回……
    private ActiveType isNormalAppr;//是否正常审批, 如果是指定审批人，则为非正常审批
    private ApproveRole role;//审批角色，用户或者审批人，以后可能还会有别的角色
    private String approveOpinion;//审批意见
    private String approveName;//审批名称，用户或岗位名
    private ActiveType isAgin;//是否重新发起
    private ActiveType isHasView;//是否已经查看

    public Long getApplyId(){
        return applyId;
    }
    public void setApplyId(Long applyId){
        this.applyId=applyId;
    }
    public Long getTempId(){
        return tempId;
    }
    public void setTempId(Long tempId){
        this.tempId=tempId;
    }
    public Long getNodeId(){
        return nodeId;
    }
    public void setNodeId(Long nodeId){
        this.nodeId=nodeId;
    }
    public Long getApproveId(){
        return approveId;
    }
    public void setApproveId(Long approveId){
        this.approveId=approveId;
    }
    @Enumerated(EnumType.STRING)
    public KnNode.NodeType getType(){
        return type;
    }
    public void setType(KnNode.NodeType type){
        this.type=type;
    }
    public Long getSubmitTime(){
        return submitTime;
    }
    public void setSubmitTime(Long submitTime){
        this.submitTime=submitTime;
    }
    @Enumerated(EnumType.STRING)
    public ApproveStatus getApproveStatus(){
        return approveStatus;
    }
    public void setApproveStatus(ApproveStatus approveStatus){
        this.approveStatus=approveStatus;
    }
    @Enumerated(EnumType.STRING)
    public ActiveType getIsNormalAppr(){
        return isNormalAppr;
    }
    public void setIsNormalAppr(ActiveType isNormalAppr){
        this.isNormalAppr=isNormalAppr;
    }
   @Enumerated(EnumType.STRING)
    public ApproveRole getRole(){
        return role;
    }
    public void setRole(ApproveRole role){
        this.role=role;
    }
    public enum ApproveStatus{//同意 拒绝 撤回 审批中 重新发起 起始 结束 查看
        PASS,REJECT,FALLBACK,APPROVEING,ANGIN,START,END,VIEW
    }
    public enum ApproveRole{//申请人，审批人
        APPLYER,APPROVER
    }
    @Lob
    public String getApproveOpinion(){
        return approveOpinion;
    }
    public void setApproveOpinion(String approveOpinion){
        this.approveOpinion=approveOpinion;
    }
    @Enumerated(EnumType.STRING)
    public ActiveType getIsAgin(){
        return isAgin;
    }
    public void setIsAgin(ActiveType isAgin){
        this.isAgin=isAgin;
    }
    @Enumerated(EnumType.STRING)
    public ActiveType getIsHasView(){
        return isHasView;
    }
    public void setIsHasView(ActiveType isHasView){
        this.isHasView=isHasView;
    }
    @Transient
    public String getApproveName(){
        return approveName;
    }
    public void setApproveName(String approveName){
        this.approveName=approveName;
    }
}
