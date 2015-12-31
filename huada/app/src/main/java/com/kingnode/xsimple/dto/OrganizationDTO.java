package com.kingnode.xsimple.dto;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class OrganizationDTO{
    private Long id;
    private String code;
    private String name;
    private Long children;
    public OrganizationDTO(){

    }
    public OrganizationDTO(Long id,String code,String name,Long children){
        this.id=id;
        this.code=code;
        this.name=name;
        this.children=children;
    }
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public String getCode(){
        return code;
    }
    public void setCode(String code){
        this.code=code;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public Long getChildren(){
        return children;
    }
    public void setChildren(Long children){
        this.children=children;
    }
}
