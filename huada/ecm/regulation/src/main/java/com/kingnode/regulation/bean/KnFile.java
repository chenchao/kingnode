package com.kingnode.regulation.bean;
/**
 * Created by xushuangyong on 14-9-26.
 */
public class KnFile{
    private String originalFileName;//上传原始资料名称
    private Long fileSize;//字节大小
    private String readCount;//
    private String path;
    private String ext;
    private String savePath;//保存路径
    private String fileFormat;
    public String getSavePath(){
        return savePath;
    }
    public void setSavePath(String savePath){
        this.savePath=savePath;
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
    public String getReadCount(){
        return readCount;
    }
    public void setReadCount(String readCount){
        this.readCount=readCount;
    }
    public String getPath(){
        return path;
    }
    public void setPath(String path){
        this.path=path;
    }
    public String getExt(){
        return ext;
    }
    public void setExt(String ext){
        this.ext=ext;
    }
    public String getFileFormat(){
        return fileFormat;
    }
    public void setFileFormat(String fileFormat){
        this.fileFormat=fileFormat;
    }
}
