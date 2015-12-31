package com.kingnode.workflow.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
/**
 * @author chenchao
 * 模板表
 */
@Entity
@Table(name="kn_wf_template")
public class KnTemplate  extends KnWorkflowBase{
    private String title;//模板标题
    private String type;//模板类型
    @Column(name="title", length=200)
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type=type;
    }
}
