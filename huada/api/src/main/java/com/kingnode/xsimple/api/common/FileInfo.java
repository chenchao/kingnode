package com.kingnode.xsimple.api.common;
import java.util.Date;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class FileInfo{
    private String name;
    private String url;
    private Boolean isDirectory;
    private Long size;
    private Date lastModified;
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getUrl(){
        return this.url;
    }
    public void setUrl(String url){
        this.url=url;
    }
    public Boolean getIsDirectory(){
        return this.isDirectory;
    }
    public void setIsDirectory(Boolean isDirectory){
        this.isDirectory=isDirectory;
    }
    public Long getSize(){
        return this.size;
    }
    public void setSize(Long size){
        this.size=size;
    }
    public Date getLastModified(){
        return this.lastModified;
    }
    public void setLastModified(Date lastModified){
        this.lastModified=lastModified;
    }
    public enum FileType{
        image,flash,media,file
    }
    public enum OrderType{
        name,size,type
    }
}
