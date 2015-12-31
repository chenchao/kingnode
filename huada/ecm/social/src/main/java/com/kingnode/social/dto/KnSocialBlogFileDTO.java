package com.kingnode.social.dto;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@XmlRootElement(name="KnSocialBlogFile")
public class KnSocialBlogFileDTO{
    private String name;
    private String sha;
    private Integer fileSize;
    private String fileType;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getSha(){
        return sha;
    }
    public void setSha(String sha){
        this.sha=sha;
    }
    public Integer getFileSize(){
        return fileSize;
    }
    public void setFileSize(Integer fileSize){
        this.fileSize=fileSize;
    }
    public String getFileType(){
        return fileType;
    }
    public void setFileType(String fileType){
        this.fileType=fileType;
    }
}
