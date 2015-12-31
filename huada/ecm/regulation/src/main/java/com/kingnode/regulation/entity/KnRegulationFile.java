package com.kingnode.regulation.entity;
import java.beans.Transient;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * Created by xushuangyong on 14-9-26.
 */
@Entity @Table(name="kn_regulation_file")
public class KnRegulationFile extends AuditEntity{
    private static final String uploadPath="/uf/regulation/photo/";//员工储存的目录
    private Long uploadTime;//上传时间
    private String savePath;//保存路径
    private String classification;//分类名称
    private String profileName;//资料名称
    private String originalFileName;//上传原始资料名称
    private Long fileSize;//字节大小
    private Long readCount;//
    private String fileFormat="";
    @Transient
    public static String getUploadPath(){
        return uploadPath;
    }
    public Long getUploadTime(){
        return uploadTime;
    }
    public void setUploadTime(Long uploadTime){
        this.uploadTime=uploadTime;
    }
    public String getSavePath(){
        return savePath;
    }
    public void setSavePath(String savePath){
        this.savePath=savePath;
    }
    public String getClassification(){
        return classification;
    }
    public void setClassification(String classification){
        this.classification=classification;
    }
    public String getProfileName(){
        return profileName;
    }
    public void setProfileName(String profileName){
        this.profileName=profileName;
    }
    public String getOriginalFileName(){
        return originalFileName;
    }
    public void setOriginalFileName(String originalFileName){
        this.originalFileName=originalFileName;
    }
    public Long getFileSize(){
        return fileSize;
    }
    public void setFileSize(Long fileSize){
        this.fileSize=fileSize;
    }
    public Long getReadCount(){
        return readCount;
    }
    public void setReadCount(Long readCount){
        this.readCount=readCount;
    }
    public String getFileFormat(){
        return fileFormat;
    }
    public void setFileFormat(String fileFormat){
        this.fileFormat=fileFormat;
    }
}
