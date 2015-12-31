package com.kingnode.xsimple.entity.system;
import javax.persistence.Cacheable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.IdEntity.ActiveType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
/**
 * 岗位与员工关系表
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_employee_position") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @Cacheable(true)
public class KnEmployeePosition implements java.io.Serializable{
    private static final long serialVersionUID=3075248354418481101L;
    @EmbeddedId
    private KnEmployeePositionId id;
    private ActiveType major;//主岗位
    public KnEmployeePosition(){
    }
    public KnEmployeePosition(KnEmployeePositionId id){
        this.id=id;
    }
    public KnEmployeePosition(KnEmployeePositionId id,ActiveType major){
        this.id=id;
        this.major=major;
    }
    public KnEmployeePositionId getId(){
        return id;
    }
    public void setId(KnEmployeePositionId id){
        this.id=id;
    }
    @Enumerated(EnumType.STRING)
    public ActiveType getMajor(){
        return major;
    }
    public void setMajor(ActiveType major){
        this.major=major;
    }
    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this);
    }
}