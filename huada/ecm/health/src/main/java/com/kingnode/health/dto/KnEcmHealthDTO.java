package com.kingnode.health.dto;
import java.util.List;

import com.kingnode.health.entity.KnEcmHealth;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class KnEcmHealthDTO{
    private Long id;
    private String description;//病症描述
    private String waitingNumber;//候诊号
    private String diagnoseResult;//诊断结果
    private Double diagnoseCost;//诊断费用
    private KnEcmHealth.HealthStatus status;//状态
    private List<String> icons;//图片
    private String doctor;//医生
    private String doctorPhone;//医生电话
    private String statusStr;//状态(字符串)
    private String proposerTime;//申请时间
    private String waitingTime;//候诊时间
    private String diagnoseTime;//就诊时间
    private Integer attachmentFlag;//附件标记 大于1表示有附件
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description=description;
    }
    public String getWaitingNumber(){
        return waitingNumber;
    }
    public void setWaitingNumber(String waitingNumber){
        this.waitingNumber=waitingNumber;
    }
    public String getDiagnoseResult(){
        return diagnoseResult;
    }
    public void setDiagnoseResult(String diagnoseResult){
        this.diagnoseResult=diagnoseResult;
    }
    public Double getDiagnoseCost(){
        return diagnoseCost;
    }
    public void setDiagnoseCost(Double diagnoseCost){
        this.diagnoseCost=diagnoseCost;
    }
    public KnEcmHealth.HealthStatus getStatus(){
        return status;
    }
    public void setStatus(KnEcmHealth.HealthStatus status){
        this.status=status;
    }
    public List<String> getIcons(){
        return icons;
    }
    public void setIcons(List<String> icons){
        this.icons=icons;
    }
    public String getDoctor(){
        return doctor;
    }
    public void setDoctor(String doctor){
        this.doctor=doctor;
    }
    public String getDoctorPhone(){
        return doctorPhone;
    }
    public void setDoctorPhone(String doctorPhone){
        this.doctorPhone=doctorPhone;
    }
    public String getStatusStr(){
        if(status!=null){
            switch(status){
            case PROPOSER:
                statusStr="已申请";
                break;
            case WAITING:
                statusStr="候诊";
                break;
            case DIAGNOSE:
                statusStr="已诊断";
                break;
            case ABANDON:
                statusStr="已放弃";
                break;
            case OVERDUE:
                statusStr="已逾期";
                break;
            }
        }
        return statusStr;
    }
    public void setStatusStr(String statusStr){
        this.statusStr=statusStr;
    }
    public String getProposerTime(){
        return proposerTime;
    }
    public void setProposerTime(String proposerTime){
        this.proposerTime=proposerTime;
    }
    public String getWaitingTime(){
        return waitingTime;
    }
    public void setWaitingTime(String waitingTime){
        this.waitingTime=waitingTime;
    }
    public String getDiagnoseTime(){
        return diagnoseTime;
    }
    public void setDiagnoseTime(String diagnoseTime){
        this.diagnoseTime=diagnoseTime;
    }
    public Integer getAttachmentFlag(){
        return attachmentFlag;
    }
    public void setAttachmentFlag(Integer attachmentFlag){
        this.attachmentFlag=attachmentFlag;
    }
}
