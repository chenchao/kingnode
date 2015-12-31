package com.kingnode.xsimple.dto;
/**
 * 用于导入组织员工信息
 * @author kongjiangwei@kingnode.com
 */
public class ImportEmpDTO extends FullEmployeeDTO{
    private String parentId;//上级部门ID
    public String getParentId(){
        return parentId;
    }
    public void setParentId(String parentId){
        this.parentId=parentId;
    }
}
