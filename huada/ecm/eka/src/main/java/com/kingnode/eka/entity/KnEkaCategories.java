package com.kingnode.eka.entity;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 * 一卡会  大类型
 */
@Entity @Table(name="kn_eka_categories")
public class KnEkaCategories extends AuditEntity{
    private String name;//名称
    private Integer seq;//排序
    private Set<KnEkaSmallClass> smalls;//一卡会小类

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    @OneToMany(targetEntity=KnEkaSmallClass.class,cascade=CascadeType.ALL,mappedBy="categoriesId") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    @OrderBy(value = "id ASC")
    public Set<KnEkaSmallClass> getSmalls(){
        return smalls;
    }
    public void setSmalls(Set<KnEkaSmallClass> smalls){
        this.smalls=smalls;
    }
    public Integer getSeq(){
        return seq;
    }
    public void setSeq(Integer seq){
        this.seq=seq;
    }
}
