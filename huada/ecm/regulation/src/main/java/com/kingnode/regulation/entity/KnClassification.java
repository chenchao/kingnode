package com.kingnode.regulation.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * Created by xushuangyong on 14-9-27.
 */
@Entity @Table(name="kn_classification")
public class KnClassification extends AuditEntity{
    private String classification;//分类名称
    @Column(nullable=false, unique=true)
    public String getClassification(){
        return classification;
    }
    public void setClassification(String classification){
        this.classification=classification;
    }
}
