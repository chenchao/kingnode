package com.kingnode.xsimple.entity.system;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
/**
 * 组织与员工关系表复合主键
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Embeddable
public class KnEmployeeOrganizationId implements java.io.Serializable{
    private static final long serialVersionUID=8440270234543694544L;
    @OneToOne @JoinColumn(name="org_id",referencedColumnName="id") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    private KnOrganization org;
    @OneToOne @JoinColumn(name="emp_id",referencedColumnName="id") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    private KnEmployee emp;
    public KnEmployeeOrganizationId(){
    }
    public KnEmployeeOrganizationId(KnOrganization org,KnEmployee emp){
        this.org=org;
        this.emp=emp;
    }
    @JsonIgnore public KnOrganization getOrg(){
        return org;
    }
    public void setOrg(KnOrganization org){
        this.org=org;
    }
    @JsonIgnore
    public KnEmployee getEmp(){
        return emp;
    }
    public void setEmp(KnEmployee emp){
        this.emp=emp;
    }
    @Override
    public boolean equals(Object o){
        if(this==o){
            return true;
        }
        if(o==null||getClass()!=o.getClass()){
            return false;
        }
        KnEmployeeOrganizationId that=(KnEmployeeOrganizationId)o;
        if(emp!=null?!emp.equals(that.emp):that.emp!=null){
            return false;
        }
        if(org!=null?!org.equals(that.org):that.org!=null){
            return false;
        }
        return true;
    }
    @Override
    public int hashCode(){
        int result=org!=null?org.hashCode():0;
        result=31*result+(emp!=null?emp.hashCode():0);
        return result;
    }
}