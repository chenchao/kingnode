package com.kingnode.eka.dto;
import java.util.List;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class KnEkaCategoriesDTO{
    private String name;//名称
    private Long id;
    private String image;//图片
    private String smallNames;//小类名称
    private List<KnEkaSmallClassDTO> smalss;

    public String getSmallNames(){
        return smallNames;
    }
    public void setSmallNames(String smallNames){
        this.smallNames=smallNames;
    }
    public List<KnEkaSmallClassDTO> getSmalss(){
        return smalss;
    }
    public void setSmalss(List<KnEkaSmallClassDTO> smalss){
        this.smalss=smalss;
    }
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
