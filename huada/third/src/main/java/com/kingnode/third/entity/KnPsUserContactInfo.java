package com.kingnode.third.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * PS用户的联系人关系表
 * @author cici
 */
@Entity @Table(name="kn_ps_usercontact_info")
public class KnPsUserContactInfo extends AuditEntity{
    private static final long serialVersionUID=-6764592318693713943L;
    private String createrId;//创建者的id
    private String createrFrom;// 创建者来自系统
    private String contactId;//联系人Id
    private String contactFrom;// 联系人来自系统
    @Column(name="creater_id",length =100)
    public String getCreaterId(){
        return createrId;
    }
    public void setCreaterId(String createrId){
        this.createrId=createrId;
    }
    @Column(name="creater_from",length =100)
    public String getCreaterFrom(){
        return createrFrom;
    }
    public void setCreaterFrom(String createrFrom){
        this.createrFrom=createrFrom;
    }
    @Column(name="contact_id",length =100)
    public String getContactId(){
        return contactId;
    }
    public void setContactId(String contactId){
        this.contactId=contactId;
    }
    @Column(name="contact_from",length =100)
    public String getContactFrom(){
        return contactFrom;
    }
    public void setContactFrom(String contactFrom){
        this.contactFrom=contactFrom;
    }
}