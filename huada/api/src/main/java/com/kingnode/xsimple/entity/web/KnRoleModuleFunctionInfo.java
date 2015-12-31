package com.kingnode.xsimple.entity.web;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.IdEntity;

/**
 * @author kongjiangwei
 *         角色模块功能关系中间表
 */
@Entity
@Table(name = "kn_role_module_function_info")
public class KnRoleModuleFunctionInfo extends IdEntity {

    private Long roleId;//角色ID
    private Long moduleId;//模块ID
    private Long functionId;//功能ID
    private Long rmSort;//模块在角色中的排序
    private Long mfSort;//功能在模块中的排序
    private Long rfSort;//功能在角色中的排序

    @Column(name = "role_id")
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    @Column(name = "rf_sort")
    public Long getRfSort() {
        return rfSort;
    }

    public void setRfSort(Long rfSort) {
        this.rfSort = rfSort;
    }

    @Column(name = "mf_sort")
    public Long getMfSort() {
        return mfSort;
    }

    public void setMfSort(Long mfSort) {
        this.mfSort = mfSort;
    }

    @Column(name = "rm_sort")
    public Long getRmSort() {
        return rmSort;
    }

    public void setRmSort(Long rmSort) {
        this.rmSort = rmSort;
    }

    @Column(name = "function_id")
    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    @Column(name = "module_id")
    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }
}