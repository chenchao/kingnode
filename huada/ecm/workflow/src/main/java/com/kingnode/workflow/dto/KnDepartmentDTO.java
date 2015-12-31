package com.kingnode.workflow.dto;
/**
 * 部门DTO
 *
 * @author huang an ding
 */
public class KnDepartmentDTO{
    private Long id;
    private String name; //部门名称
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
}
