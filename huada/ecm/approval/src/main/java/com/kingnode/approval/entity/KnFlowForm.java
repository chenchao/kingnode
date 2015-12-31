package com.kingnode.approval.entity;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kingnode.xsimple.entity.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
/**
 * 审批预订制表单信息
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_flow_form")
public class KnFlowForm extends IdEntity{
    private static final long serialVersionUID=5348632644615730967L;
    private String name;//表单名称
    private Integer variable;//属性数量
    private Set<KnFlowFormVariables> variables;//属性集合
    @Column(length=50)
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    @Column(length=2)
    public Integer getVariable(){
        return variable;
    }
    public void setVariable(Integer variable){
        this.variable=variable;
    }
    @OneToMany(mappedBy="kff") @Fetch(FetchMode.SUBSELECT) @Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @JsonIgnore
    public Set<KnFlowFormVariables> getVariables(){
        return variables;
    }
    public void setVariables(Set<KnFlowFormVariables> variables){
        this.variables=variables;
    }
}