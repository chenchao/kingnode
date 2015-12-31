package com.kingnode.workflow.entity;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
/**
 * @author chenchao
 * 当前审批信息表,每个审批都只有最新的一条审批信息
 */
@Entity
@Table(name="kn_wf_current_approve")
public class KnCurrentApprove  extends KnWorkflowBase{
    private Long tempId;//模板表id
    private Long nodeId;//步骤表id
    private Long approveId;//审批人id
    private ActiveType isNormalAppr;//是否正常审批
    private ActiveType isHasView;//是否已经查看
    private ActiveType isAgin;//是否重新发起
    private Long applyId;//申请表id

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
    public ActiveType getIsNormalAppr(){
        return isNormalAppr;
    }
    public void setIsNormalAppr(ActiveType isNormalAppr){
        this.isNormalAppr=isNormalAppr;
    }
    public Long getApplyId(){
        return applyId;
    }
    public void setApplyId(Long applyId){
        this.applyId=applyId;
    }
    @Enumerated(EnumType.STRING)
    public ActiveType getIsHasView(){
        return isHasView;
    }
    public void setIsHasView(ActiveType isHasView){
        this.isHasView=isHasView;
    }
    @Enumerated(EnumType.STRING)
    public ActiveType getIsAgin(){
        return isAgin;
    }
    public void setIsAgin(ActiveType isAgin){
        this.isAgin=isAgin;
    }
}
