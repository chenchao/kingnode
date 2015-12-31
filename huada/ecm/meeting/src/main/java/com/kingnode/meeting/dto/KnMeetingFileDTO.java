package com.kingnode.meeting.dto;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@XmlRootElement(name="MeetingFile")
public class KnMeetingFileDTO{
    private Long id;
    private String name;
    private String sha;
    private String fileDate;
    private Integer fileSize;
    private String fileType;
    private String category;
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
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
    public String getFileDate(){
        return fileDate;
    }
    public void setFileDate(String fileDate){
        this.fileDate=fileDate;
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
    public String getCategory(){
        return category;
    }
    public void setCategory(String category){
        this.category=category;
    }
}
