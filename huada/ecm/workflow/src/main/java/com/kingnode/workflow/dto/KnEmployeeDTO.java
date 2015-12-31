package com.kingnode.workflow.dto;
/**
 * 用户DTO
 *
 * @author huanganding
 */
public class KnEmployeeDTO{
    private Long id;
    private String empName; //用户名称
    private String empIcon; //图片路径
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public String getEmpName(){
        return empName;
    }
    public void setEmpName(String empName){
        this.empName=empName;
    }
    public String getEmpIcon(){
        return empIcon;
    }
    public void setEmpIcon(String empIcon){
        this.empIcon=empIcon;
    }
}
