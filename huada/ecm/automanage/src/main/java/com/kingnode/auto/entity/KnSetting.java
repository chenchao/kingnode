package com.kingnode.auto.entity;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * Created by xushuangyong on 14-9-22.
 */
@Entity @Table(name="kn_setting")
public class KnSetting extends AuditEntity{
    private static final long serialVersionUID=-7439492051114373315L;
    //超时默认值,默认45分钟
    private Integer minutes=45;
    public Integer getMinutes(){
        return minutes;
    }
    public void setMinutes(Integer minutes){
        this.minutes=minutes;
    }
    public void setId(Long id){
        this.id=1L;
    }
}
