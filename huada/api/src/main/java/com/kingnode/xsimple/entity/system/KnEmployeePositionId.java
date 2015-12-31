package com.kingnode.xsimple.entity.system;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
/**
 * 岗位与员工关系表复合主键
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Embeddable
public class KnEmployeePositionId implements java.io.Serializable{
    private static final long serialVersionUID=-9076353580091139923L;
    @OneToOne @JoinColumn(name="pos_id",referencedColumnName="id") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    private KnPosition pos;
    @OneToOne @JoinColumn(name="emp_id",referencedColumnName="id") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    private KnEmployee emp;
    public KnEmployeePositionId(){
    }
    public KnEmployeePositionId(KnPosition pos,KnEmployee emp){
        this.pos=pos;
        this.emp=emp;
    }
    public KnPosition getPos(){
        return pos;
    }
    public void setPos(KnPosition pos){
        this.pos=pos;
    }
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
        KnEmployeePositionId that=(KnEmployeePositionId)o;
        return emp.equals(that.emp)&&pos.equals(that.pos);
    }
    @Override
    public int hashCode(){
        int result=pos.hashCode();
        result=31*result+emp.hashCode();
        return result;
    }
}