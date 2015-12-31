package com.kingnode.xsimple.entity.system;
import javax.persistence.Cacheable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
/**
 * 组织员工关系表
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_employee_organization") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @Cacheable(true)
public class KnEmployeeOrganization implements java.io.Serializable{
    private static final long serialVersionUID=-5962854756758973174L;
    @EmbeddedId
    private KnEmployeeOrganizationId id;
    private int charge=0;//负责人,当值为1,关联的的员工为组织的负责人
    private int major=0;//主组织,当值为1,该组织为关联员工的主组织
    public KnEmployeeOrganization(){
    }
    public KnEmployeeOrganization(KnEmployeeOrganizationId id){
        this.id=id;
    }
    public KnEmployeeOrganization(KnEmployeeOrganizationId id,int charge,int major){
        this.id=id;
        this.charge=charge;
        this.major=major;
    }
    @JsonIgnore
    public KnEmployeeOrganizationId getId(){
        return id;
    }
    public void setId(KnEmployeeOrganizationId id){
        this.id=id;
    }
    public int getCharge(){
        return charge;
    }
    public void setCharge(int charge){
        this.charge=charge;
    }
    public int getMajor(){
        return major;
    }
    public void setMajor(int major){
        this.major=major;
    }
    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this);
    }
}