package com.kingnode.eka.dto;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class KnEkaSmallClassDTO{
    private String name;//名称
    private Long id;
    private String image;//图片
    public String getImage(){
        return image;
    }
    public void setImage(String image){
        this.image=image;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
}
