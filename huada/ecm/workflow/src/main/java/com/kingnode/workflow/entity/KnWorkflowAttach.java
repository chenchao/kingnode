package com.kingnode.workflow.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_wf_file")
public class KnWorkflowAttach extends KnWorkflowBase{
    private static final long serialVersionUID=877853374819548395L;
    private String name;//标题
    private String sha;//MD5唯一标示
    private Integer fileSize;//文件大小
    private String fileType;//文件类型
    private BusType busType;//业务类型
    private Long tId;//关联表id（申请表，审批）

    @Column(length=200)
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    @Column(length=10)
    public Integer getFileSize(){
        return fileSize;
    }
    public void setFileSize(Integer fileSize){
        this.fileSize=fileSize;
    }
    @Column(length=100)
    public String getSha(){
        return sha;
    }
    public void setSha(String sha){
        this.sha=sha;
    }
    @Column(length=200)
    public String getFileType(){
        return fileType;
    }
    public void setFileType(String fileType){
        this.fileType=fileType;
    }
    @Enumerated(EnumType.STRING)
    public BusType getBusType(){
        return busType;
    }
    public void setBusType(BusType busType){
        this.busType=busType;
    }
    public enum BusType{  //审批，申请
        APPROVE,APPLY
    }
    public Long gettId(){
        return tId;
    }
    public void settId(Long tId){
        this.tId=tId;
    }
}
