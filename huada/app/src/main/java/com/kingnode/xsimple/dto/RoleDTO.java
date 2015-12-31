package com.kingnode.xsimple.dto;
/**
 * @author kongjiangwei@kingnode.com
 */
public class RoleDTO{
    public Long getRoleId(){
        return roleId;
    }
    public void setRoleId(Long roleId){
        this.roleId=roleId;
    }
    public String getRoleName(){
        return roleName;
    }
    public void setRoleName(String roleName){
        this.roleName=roleName;
    }
    private Long roleId;
    private String roleName;
}
