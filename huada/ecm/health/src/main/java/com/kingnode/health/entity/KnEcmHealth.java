package com.kingnode.health.entity;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kingnode.xsimple.entity.AuditEntity;
import com.kingnode.xsimple.entity.system.KnEmployee;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.joda.time.DateTime;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_health") public class KnEcmHealth extends AuditEntity{
    private static final long serialVersionUID=-2367286239176841440L;
    private KnEmployee employee;//申请人
    private String phone;//申请电话
    private Long proposerDate;//申请时间
    private String description;//病症描述
    private String waitingNumber;//候诊号
    private Long waitingDate;//候诊时间
    private Long diagnoseDate;//就诊时间
    private String diagnoseResult;//诊断结果
    private Double diagnoseCost;//诊断费用
    private Long consultationDate;//复诊时间
    private Integer consultationFlag;//标记是否属于复诊 0.否 1.是
    private HealthStatus status;//状态
    private List<KnEcmHealthIcon> healthIcons;//图片
    private String doctor;//医生
    private String doctorPhone;//医生电话
    private String proposerTime;//申请时间(字符串)
    private String waitingTime;//候诊时间(字符串)
    private String diagnoseTime;//就诊时间(字符串)
    private String consultationTime;//复诊时间(字符串)
    private String orgName;//部门名称
    @NotNull @ManyToOne @JoinColumn(name="employee_id") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE) public KnEmployee getEmployee(){
        return employee;
    }
    public void setEmployee(KnEmployee employee){
        this.employee=employee;
    }
    @Column(length=20) public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone=phone;
    }
    @NotNull @Column(length=20) public Long getProposerDate(){
        return proposerDate;
    }
    public void setProposerDate(Long proposerDate){
        this.proposerDate=proposerDate;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description=description;
    }
    @Column(length=20) public String getWaitingNumber(){
        return waitingNumber;
    }
    public void setWaitingNumber(String waitingNumber){
        this.waitingNumber=waitingNumber;
    }
    @Column(length=20) public Long getWaitingDate(){
        return waitingDate;
    }
    public void setWaitingDate(Long waitingDate){
        this.waitingDate=waitingDate;
    }
    @Column(length=20) public Long getDiagnoseDate(){
        return diagnoseDate;
    }
    public void setDiagnoseDate(Long diagnoseDate){
        this.diagnoseDate=diagnoseDate;
    }
    public String getDiagnoseResult(){
        return diagnoseResult;
    }
    public void setDiagnoseResult(String diagnoseResult){
        this.diagnoseResult=diagnoseResult;
    }
    @Column(length=20) public Double getDiagnoseCost(){
        return diagnoseCost;
    }
    public void setDiagnoseCost(Double diagnoseCost){
        this.diagnoseCost=diagnoseCost;
    }
    @Column(length=20) public Long getConsultationDate(){
        return consultationDate;
    }
    public void setConsultationDate(Long consultationDate){
        this.consultationDate=consultationDate;
    }
    public Integer getConsultationFlag(){
        return consultationFlag;
    }
    public void setConsultationFlag(Integer consultationFlag){
        this.consultationFlag=consultationFlag;
    }
    @NotNull @Enumerated(EnumType.STRING) @Column(length=20) public HealthStatus getStatus(){
        return status;
    }
    public void setStatus(HealthStatus status){
        this.status=status;
    }
    @OneToMany(cascade=CascadeType.ALL) @JoinColumn(name="health_id") @Fetch(FetchMode.SUBSELECT) @Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @JsonIgnore
    public List<KnEcmHealthIcon> getHealthIcons(){
        return healthIcons;
    }
    public void setHealthIcons(List<KnEcmHealthIcon> healthIcons){
        this.healthIcons=healthIcons;
    }
    @Column(length=20) public String getDoctor(){
        return doctor;
    }
    public void setDoctor(String doctor){
        this.doctor=doctor;
    }
    @Column(length=20) public String getDoctorPhone(){
        return doctorPhone;
    }
    public void setDoctorPhone(String doctorPhone){
        this.doctorPhone=doctorPhone;
    }
    /**
     * 申请时间(yyyy-MM-dd HH:mm)
     *
     * @return String
     */
    @Transient public String getProposerTime(){
        if(proposerDate!=null&&proposerDate>0){
            return new DateTime(proposerDate).toString("yyyy-MM-dd HH:mm");
        }
        return proposerTime;
    }
    public void setProposerTime(String proposerTime){
        this.proposerTime=proposerTime;
    }
    /**
     * 候诊时间(yyyy-MM-dd HH:mm)
     *
     * @return String
     */
    @Transient public String getWaitingTime(){
        if(waitingDate!=null&&waitingDate>0){
            return new DateTime(waitingDate).toString("yyyy-MM-dd HH:mm");
        }
        return waitingTime;
    }
    public void setWaitingTime(String waitingTime){
        this.waitingTime=waitingTime;
    }
    /**
     * 就诊时间(yyyy-MM-dd HH:mm)
     *
     * @return String
     */
    @Transient public String getDiagnoseTime(){
        if(diagnoseDate!=null&&diagnoseDate>0){
            return new DateTime(diagnoseDate).toString("yyyy-MM-dd HH:mm");
        }
        return diagnoseTime;
    }
    public void setDiagnoseTime(String diagnoseTime){
        this.diagnoseTime=diagnoseTime;
    }
    /**
     * 复诊时间(yyyy-MM-dd HH:mm)
     *
     * @return String
     */
    @Transient public String getConsultationTime(){
        if(consultationDate!=null&&consultationDate>0){
            return new DateTime(consultationDate).toString("yyyy-MM-dd HH:mm");
        }
        return consultationTime;
    }
    public void setConsultationTime(String consultationTime){
        this.consultationTime=consultationTime;
    }
    /**
     * 主部门名称
     * @return String
     */
    public String getOrgName(){
        return orgName;
    }
    public void setOrgName(String orgName){
        this.orgName=orgName;
    }
    public enum HealthStatus{//状态:已申请,候诊,已诊断,已放弃,已逾期
        PROPOSER,WAITING,DIAGNOSE,ABANDON,OVERDUE
    }
}
