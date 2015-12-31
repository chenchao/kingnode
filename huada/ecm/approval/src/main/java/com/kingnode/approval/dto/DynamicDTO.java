package com.kingnode.approval.dto;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@XmlRootElement(name="dynamic")
public class DynamicDTO{
    private Long approvalId;
    private String sponsor;//申请人
    private String applyType;//申请类型
    private String createDate;//时间
    private String auditor;//审核人
    private String resultType;//审批结果
    private String comment;//审批意见
    private String holidayType;//请假类型
    private String content;//申请内容
    private String nodeType;//开始，进行当中，结束
    public Long getApprovalId(){
        return approvalId;
    }
    public void setApprovalId(Long approvalId){
        this.approvalId=approvalId;
    }
    public String getSponsor(){
        return sponsor;
    }
    public void setSponsor(String sponsor){
        this.sponsor=sponsor;
    }
    public String getApplyType(){
        return applyType;
    }
    public void setApplyType(String applyType){
        this.applyType=applyType;
    }
    public String getCreateDate(){
        return createDate;
    }
    public void setCreateDate(String createDate){
        this.createDate=createDate;
    }
    public String getAuditor(){
        return auditor;
    }
    public void setAuditor(String auditor){
        this.auditor=auditor;
    }
    public String getResultType(){
        return resultType;
    }
    public void setResultType(String resultType){
        this.resultType=resultType;
    }
    public String getComment(){
        return comment;
    }
    public void setComment(String comment){
        this.comment=comment;
    }
    public String getHolidayType(){
        return holidayType;
    }
    public void setHolidayType(String holidayType){
        this.holidayType=holidayType;
    }
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content=content;
    }
    public String getNodeType(){
        return nodeType;
    }
    public void setNodeType(String nodeType){
        this.nodeType=nodeType;
    }
}
