package com.kingnode.news.dto;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class WelfareDTO{
    private Long id;
    private String title;
    private String name;
    private String img;
    private String pubTime;
    private String content;
    public WelfareDTO(){
    }
    public WelfareDTO(Long id,String title,String name,String img,String pubTime){
        this.id=id;
        this.title=title;
        this.name=name;
        this.img=img;
        this.pubTime=pubTime;
    }
    public WelfareDTO(Long id,String title,String name,String img,String pubTime,String content){
        this.id=id;
        this.title=title;
        this.name=name;
        this.img=img;
        this.pubTime=pubTime;
        this.content=content;
    }
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
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
    public String getImg(){
        return img;
    }
    public void setImg(String img){
        this.img=img;
    }
    public String getPubTime(){
        return pubTime;
    }
    public void setPubTime(String pubTime){
        this.pubTime=pubTime;
    }
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content=content;
    }
}
