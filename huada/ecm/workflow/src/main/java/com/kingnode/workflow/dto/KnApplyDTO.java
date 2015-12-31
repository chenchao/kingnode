package com.kingnode.workflow.dto;
import java.util.List;

/**
 * @author huang an ding
 */
public class KnApplyDTO{
    private Long id;
    private Long userId;//申请人ID
    private String userName;//申请人姓名
    private String userIcon;//申请人头像
    private Long tempId;//模板ID
    private String tempName;//模板名称
    private String startTime;//开始时间
    private String endTime;//结束时间
    private String isHalfDay;//是否半天
    private String description;//描述
    private String other;//其他信息
    private String files;//附件
    private String applyTime;//申请时间
    private String status;//状态
    private Long currApproveId;//当前审批人Id
    private String currApproveName;//当前审批姓名
    private List<KnHistoryApproveDTO> approveInfos;//历史申请记录
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName=userName;
    }
    public String getUserIcon(){
        return userIcon;
    }
    public void setUserIcon(String userIcon){
        this.userIcon=userIcon;
    }
    public List<KnHistoryApproveDTO> getApproveInfos(){
        return approveInfos;
    }
    public void setApproveInfos(List<KnHistoryApproveDTO> approveInfos){
        this.approveInfos=approveInfos;
    }
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
    public String getStartTime(){
        return startTime;
    }
    public void setStartTime(String startTime){
        this.startTime=startTime;
    }
    public String getEndTime(){
        return endTime;
    }
    public void setEndTime(String endTime){
        this.endTime=endTime;
    }
    public String getIsHalfDay(){
        return isHalfDay;
    }
    public void setIsHalfDay(String isHalfDay){
        this.isHalfDay=isHalfDay;
    }
    public String getOther(){
        return other;
    }
    public void setOther(String other){
        this.other=other;
    }
    public String getFiles(){
        return files;
    }
    public void setFiles(String files){
        this.files=files;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description=description;
    }
    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId=userId;
    }
    public String getTempName(){
        return tempName;
    }
    public void setTempName(String tempName){
        this.tempName=tempName;
    }
    public String getApplyTime(){
        return applyTime;
    }
    public void setApplyTime(String applyTime){
        this.applyTime=applyTime;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status=status;
    }
    public Long getCurrApproveId(){
        return currApproveId;
    }
    public void setCurrApproveId(Long currApproveId){
        this.currApproveId=currApproveId;
    }
    public String getCurrApproveName(){
        return currApproveName;
    }
    public void setCurrApproveName(String currApproveName){
        this.currApproveName=currApproveName;
    }
}
