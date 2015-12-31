package com.kingnode.approval.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kingnode.xsimple.entity.IdEntity;
/**
 * 审批预订制表单属性值
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_flow_form_variables")
public class KnFlowFormVariables extends IdEntity{
    private static final long serialVersionUID=-5697556050645765906L;
    private KnFlowForm kff;
    private ValueType type;//数据类型（字符串，整型，数字型，日期可以扩展）
    private String title;//名称
    private String name;//属性名字
    private String format;//格式化方式(如数字型精度，日期的格式)
    private Long orderNum;//排序
    @ManyToOne @JoinColumn(nullable=false, name="form_id") @JsonIgnore
    public KnFlowForm getKff(){
        return kff;
    }
    public void setKff(KnFlowForm kff){
        this.kff=kff;
    }
    @NotNull(groups={ValueType.class}) @Column(nullable=false, length=20) @Enumerated(EnumType.STRING)
    public ValueType getType(){
        return type;
    }
    public void setType(ValueType type){
        this.type=type;
    }
    @Column(length=50)
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    @Column(length=30)
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    @Column(length=100)
    public String getFormat(){
        return format;
    }
    public void setFormat(String format){
        this.format=format;
    }
    @Column(length=13)
    public Long getOrderNum(){
        return orderNum;
    }
    public void setOrderNum(Long orderNum){
        this.orderNum=orderNum;
    }
    public enum ValueType{
        STRING,INTEGER,DOUBLE,DATE,LIST
    }
}
