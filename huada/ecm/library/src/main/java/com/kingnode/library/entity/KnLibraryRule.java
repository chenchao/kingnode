package com.kingnode.library.entity;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_library_rule")
public class KnLibraryRule extends AuditEntity{
    private static final long serialVersionUID=8946886223577426786L;
    private Integer limitUnit;//限借本数
    private Integer borrowDay;//借阅最长多少天
    private Integer overdue;//逾期规则
    private Integer renew;//续借规则
    private Integer expiration;//到期提醒
    public Integer getLimitUnit(){
        return limitUnit;
    }
    public void setLimitUnit(Integer limitUnit){
        this.limitUnit=limitUnit;
    }
    public Integer getBorrowDay(){
        return borrowDay;
    }
    public void setBorrowDay(Integer borrowDay){
        this.borrowDay=borrowDay;
    }
    public Integer getOverdue(){
        return overdue;
    }
    public void setOverdue(Integer overdue){
        this.overdue=overdue;
    }
    public Integer getRenew(){
        return renew;
    }
    public void setRenew(Integer renew){
        this.renew=renew;
    }
    public Integer getExpiration(){
        return expiration;
    }
    public void setExpiration(Integer expiration){
        this.expiration=expiration;
    }
}
