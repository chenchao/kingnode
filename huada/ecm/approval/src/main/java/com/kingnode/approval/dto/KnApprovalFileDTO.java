package com.kingnode.approval.dto;
import javax.xml.bind.annotation.XmlRootElement;

import com.kingnode.approval.entity.KnApprovalFile;
@XmlRootElement(name="KnApprovalFile")
public class KnApprovalFileDTO{
    private Long id;
    private String name;
    private KnApprovalFile.FileType type;
    private String path;
    private Integer size;
    private String createDate;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public KnApprovalFile.FileType getType(){
        return type;
    }
    public void setType(KnApprovalFile.FileType type){
        this.type=type;
    }
    public String getPath(){
        return path;
    }
    public void setPath(String path){
        this.path=path;
    }
    public Integer getSize(){
        return size;
    }
    public void setSize(Integer size){
        this.size=size;
    }
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public String getCreateDate(){
        return createDate;
    }
    public void setCreateDate(String createDate){
        this.createDate=createDate;
    }
}
