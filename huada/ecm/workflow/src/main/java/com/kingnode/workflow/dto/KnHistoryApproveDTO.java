package com.kingnode.workflow.dto;
import com.kingnode.workflow.entity.KnHistoryApprove;
/**
 * 模板DTO
 *
 * @author huanganding
 */
public class KnHistoryApproveDTO{
    private Long id;
    private String approveName;//审批名称
    private String approveStatusName;//审批状态
    private KnHistoryApprove.ApproveStatus approveStatus;//审批状态
    private String approveOpinion;//审批内容
    private String approveTime;//审批时间
    private String files;//审批附件
    private Long tempId;//模板ID
    private Long applyId;//申请ID
    private String createTime;//创建时间
    private Long approveId;//审批人id
    private String type;//审批类型  同意并结束  同意并制定下一个审批人 拒绝
    private String starterName;//发起人姓名
    private String templateName;//模板名称
    private Long starerId;//发起人id
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public String getApproveName(){
        return approveName;
    }
    public void setApproveName(String approveName){
        this.approveName=approveName;
    }
    public String getApproveStatusName(){
        return approveStatusName;
    }
    public void setApproveStatusName(String approveStatusName){
        this.approveStatusName=approveStatusName;
    }
    public KnHistoryApprove.ApproveStatus getApproveStatus(){
        return approveStatus;
    }
    public void setApproveStatus(KnHistoryApprove.ApproveStatus approveStatus){
        this.approveStatus=approveStatus;
    }
    public String getApproveOpinion(){
        return approveOpinion;
    }
    public void setApproveOpinion(String approveOpinion){
        this.approveOpinion=approveOpinion;
    }
    public String getApproveTime(){
        return approveTime;
    }
    public void setApproveTime(String approveTime){
        this.approveTime=approveTime;
    }
    public String getFiles(){
        return files;
    }
    public void setFiles(String files){
        this.files=files;
    }
    public Long getTempId(){
        return tempId;
    }
    public void setTempId(Long tempId){
        this.tempId=tempId;
    }
    public Long getApplyId(){
        return applyId;
    }
    public void setApplyId(Long applyId){
        this.applyId=applyId;
    }
    public String getCreateTime(){
        return createTime;
    }
    public void setCreateTime(String createTime){
        this.createTime=createTime;
    }
    public Long getApproveId(){
        return approveId;
    }
    public void setApproveId(Long approveId){
        this.approveId=approveId;
    }
    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type=type;
    }
    public String getStarterName(){
        return starterName;
    }
    public void setStarterName(String starterName){
        this.starterName=starterName;
    }
    public String getTemplateName(){
        return templateName;
    }
    public void setTemplateName(String templateName){
        this.templateName=templateName;
    }
    public Long getStarerId(){
        return starerId;
    }
    public void setStarerId(Long starerId){
        this.starerId=starerId;
    }
}
