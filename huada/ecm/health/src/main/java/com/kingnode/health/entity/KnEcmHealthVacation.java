package com.kingnode.health.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.kingnode.xsimple.entity.AuditEntity;
import org.joda.time.DateTime;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_health_vacation")
public class KnEcmHealthVacation extends AuditEntity{
    private Long vacation;//休假时间
    private Integer type;//类型
    private String vacationStr;//休假时间(字符串)
    private ActiveType active;//有效性
    @NotNull @Column(length=20)
    public Long getVacation(){
        return vacation;
    }
    public void setVacation(Long vacation){
        this.vacation=vacation;
    }
    public Integer getType(){
        return type;
    }
    public void setType(Integer type){
        this.type=type;
    }
    @NotNull @Enumerated(EnumType.STRING) @Column(length=20)
    public ActiveType getActive(){
        return active;
    }
    public void setActive(ActiveType active){
        this.active=active;
    }
    @Transient
    public String getVacationStr(){
        if(vacation!=null){
            return new DateTime(vacation).toString("yyyy-MM-dd");
        }
        return vacationStr;
    }
}
