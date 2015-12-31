package com.kingnode.approval.entity;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kingnode.xsimple.entity.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
/**
 * 固定流程节点信息
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_flow")
public class KnFlow extends IdEntity{
    private static final long serialVersionUID=9100962253130699099L;
    private String name;//流程名称
    private String content;//流程说明
    private KnFlowForm form;//流程固化表单
    private Set<KnFlowNode> node;//流程节点信息
    @Column(length=50)
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    @Column(length=500)
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content=content;
    }
    @ManyToOne @JoinColumn(name="form_id")
    public KnFlowForm getForm(){
        return form;
    }
    public void setForm(KnFlowForm form){
        this.form=form;
    }
    @OneToMany(mappedBy="kf") @Fetch(FetchMode.SUBSELECT) @Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @JsonIgnore
    public Set<KnFlowNode> getNode(){
        return node;
    }
    public void setNode(Set<KnFlowNode> node){
        this.node=node;
    }
}
