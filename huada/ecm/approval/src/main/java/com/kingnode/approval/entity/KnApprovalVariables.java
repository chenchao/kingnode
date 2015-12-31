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
import com.kingnode.approval.entity.KnFlowFormVariables.ValueType;
/**
 * 审批表单属性名称，属性值，属性显示相关内容
 *
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_approval_variables")
public class KnApprovalVariables extends IdEntity{
    private static final long serialVersionUID=-227907733514751165L;
    private KnApproval ka;
    private ValueType type;//数据类型（字符串，整型，数字型，日期可以扩展）
    private String title;//名称
    private String name;//属性名字
    private String value;//属性值
    private String format;//格式化方式(如数字型精度，日期的格式)
    @ManyToOne @JoinColumn(nullable=false, name="approval_id") @JsonIgnore
    public KnApproval getKa(){
        return ka;
    }
    public void setKa(KnApproval ka){
        this.ka=ka;
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
    @Column(length=1000)
    public String getValue(){
        return value;
    }
    public void setValue(String value){
        this.value=value;
    }
    @Column(length=100)
    public String getFormat(){
        return format;
    }
    public void setFormat(String format){
        this.format=format;
    }
}