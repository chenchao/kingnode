package com.kingnode.xsimple.entity.meeting;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * 会议注册人员表
 *
 * @author cici
 */
@Entity @Table(name="kn_register_info")
public class KnRegisterInfo extends AuditEntity{
    private static final long serialVersionUID=-2506435150282393218L;
    private String name;//参会人员名称
    private String phone; //电话
    private String email; //邮箱
    private String company;//所属公司
    private String cusmger;//所属客户经理
    private String sex;//性别
    private String ext3;//扩展字段3
    //VO显示
    private String isSign;//是否签到
    private String signTime;//签到时间
    private String siteId;//位置
    private String state;
    public KnRegisterInfo(){
    }
    public KnRegisterInfo(Long id,String name,String phone,String email,String company,String cusmger,String sex,String ext1,String ext2,String ext3,Date signTime,String siteId,Long createTime,Long updateTime,String state){
        this.setId(id);
        this.name=name;
        this.phone=phone;
        this.email=email;
        this.company=company;
        this.cusmger=cusmger;
        this.sex=sex;
        this.ext3=ext3;
        this.setSignTimeStr(signTime);
        this.siteId=siteId;
        this.setCreateTime(createTime);
        this.setUpdateTime(updateTime);
        this.state=state;
    }
    @Transient
    public static long getSerialVersionUID(){
        return serialVersionUID;
    }
    @Column(name="name", length=20)
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    @Column(name="phone", length=15)
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone=phone;
    }
    @Column(name="email", length=50)
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email=email;
    }
    @Column(name="company", length=50)
    public String getCompany(){
        return company;
    }
    public void setCompany(String company){
        this.company=company;
    }
    @Column(name="cusmger", length=20)
    public String getCusmger(){
        return cusmger;
    }
    public void setCusmger(String cusmger){
        this.cusmger=cusmger;
    }
    @Column(name="ext3",length=100)
    public String getExt3(){
        return ext3;
    }
    public void setExt3(String ext3){
        this.ext3=ext3;
    }
    @Column(name="sex",length=10)
    public String getSex(){
        return sex;
    }
    public void setSex(String sex){
        this.sex=sex;
    }
    @Transient
    public String getIsSign(){
        return isSign;
    }
    public void setIsSign(String isSign){
        this.isSign=isSign;
    }
    @Transient
    public String getSignTime(){
        return signTime;
    }
    public void setSignTime(String signTime){
        this.signTime=signTime;
    }
    @Transient
    public String getSiteId(){
        return siteId;
    }
    public void setSiteId(String siteId){
        this.siteId=siteId;
    }
    @Transient
    public String getState(){
        return state;
    }
    public void setState(String state){
        this.state=state;
    }
    public void setSignTimeStr(Date signTime){
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        this.signTime=format.format(signTime);
    }
}