package com.kingnode.health.entity;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_health_file") public class KnEcmHealthIcon extends AuditEntity{
    private static final long serialVersionUID=-2367286239176841440L;
    private String icon;//图片地址
    private KnEcmHealth health;//预约对象
    public String getIcon(){
        return icon;
    }
    public void setIcon(String icon){
        this.icon=icon;
    }
    @ManyToOne @JoinColumn(name="health_id") public KnEcmHealth getHealth(){
        return health;
    }
    public void setHealth(KnEcmHealth health){
        this.health=health;
    }
}
