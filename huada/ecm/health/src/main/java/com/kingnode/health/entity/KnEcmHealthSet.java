package com.kingnode.health.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_health_set") public class KnEcmHealthSet extends AuditEntity{
    private static final long serialVersionUID=-2367286239176841440L;
    private String amStartDate;//上午开始时间
    private String amEndDate;//上午结束时间
    private String pmStartDate;//下午开始时间
    private String pmEndDate;//下午结束时间
    private Integer spacing;//候诊号间隔时间(分钟)
    private Integer beforeRemind;//就诊提醒时间
    private String doctor;//医生
    private String doctorPhone;//医生电话
    private Integer action;//开始排号时间距离当前时间(单位:分钟)
    private Integer flag;//用来标记查询已排号规则的规则 1.表示大于常规排号的最后时间  2.表示大于当前操作时间
    @NotNull @Column(length=20) public String getAmStartDate(){
        return amStartDate;
    }
    public void setAmStartDate(String amStartDate){
        this.amStartDate=amStartDate;
    }
    @NotNull @Column(length=20) public String getAmEndDate(){
        return amEndDate;
    }
    public void setAmEndDate(String amEndDate){
        this.amEndDate=amEndDate;
    }
    @Column(length=20) public String getPmStartDate(){
        return pmStartDate;
    }
    public void setPmStartDate(String pmStartDate){
        this.pmStartDate=pmStartDate;
    }
    @NotNull @Column(length=20) public String getPmEndDate(){
        return pmEndDate;
    }
    public void setPmEndDate(String pmEndDate){
        this.pmEndDate=pmEndDate;
    }
    @Column(length=20) public Integer getSpacing(){
        return spacing;
    }
    public void setSpacing(Integer spacing){
        this.spacing=spacing;
    }
    @Column(length=20) public Integer getBeforeRemind(){
        return beforeRemind;
    }
    public void setBeforeRemind(Integer beforeRemind){
        this.beforeRemind=beforeRemind;
    }
    @NotNull @Column(length=20) public String getDoctor(){
        return doctor;
    }
    public void setDoctor(String doctor){
        this.doctor=doctor;
    }
    @NotNull @Column(length=20) public String getDoctorPhone(){
        return doctorPhone;
    }
    public void setDoctorPhone(String doctorPhone){
        this.doctorPhone=doctorPhone;
    }
    @NotNull @Column(length=20)
    public Integer getAction(){
        return action;
    }
    public void setAction(Integer action){
        this.action=action;
    }
    @NotNull @Column(length=20)
    public Integer getFlag(){
        return flag;
    }
    public void setFlag(Integer flag){
        this.flag=flag;
    }
}
