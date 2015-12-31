package com.kingnode.eka.entity;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 *  一卡会 小类型
 */
@Entity @Table(name="kn_eka_small_class")
public class KnEkaSmallClass extends AuditEntity{
    private String name;//名称
    private Long categoriesId;//一卡会大类型id
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public Long getCategoriesId(){
        return categoriesId;
    }
    public void setCategoriesId(Long categoriesId){
        this.categoriesId=categoriesId;
    }
}
