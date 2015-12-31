package com.kingnode.approval.dto;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name="KnFlowForm")
public class KnFlowFormDTO{
    private Long id;
    private String name;//表单名称
    private Integer variable;//属性数量
    private List<KnFlowFormVariablesDTO> variables;//属性集合
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public Integer getVariable(){
        return variable;
    }
    public void setVariable(Integer variable){
        this.variable=variable;
    }
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public List<KnFlowFormVariablesDTO> getVariables(){
        return variables;
    }
    public void setVariables(List<KnFlowFormVariablesDTO> variables){
        this.variables=variables;
    }
}