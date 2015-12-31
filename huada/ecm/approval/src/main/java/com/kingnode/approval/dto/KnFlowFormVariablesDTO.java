package com.kingnode.approval.dto;
import javax.xml.bind.annotation.XmlRootElement;

import com.kingnode.approval.entity.KnFlowFormVariables;
@XmlRootElement(name="KnFlowFormVariables")
public class KnFlowFormVariablesDTO{
    private Long id;
    private KnFlowFormVariables.ValueType type;//数据类型（字符串，整型，数字型，日期可以扩展）
    private String title;//名称
    private String name;//属性名字
    private String format;//格式化方式(如数字型精度，日期的格式)
    public KnFlowFormVariables.ValueType getType(){
        return type;
    }
    public void setType(KnFlowFormVariables.ValueType type){
        this.type=type;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getFormat(){
        return format;
    }
    public void setFormat(String format){
        this.format=format;
    }
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
}
