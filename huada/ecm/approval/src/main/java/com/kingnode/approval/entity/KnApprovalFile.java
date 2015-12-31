package com.kingnode.approval.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kingnode.xsimple.entity.IdEntity;
/**
 * 审批提交的文件
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_approval_file")
public class KnApprovalFile extends IdEntity{
    private static final long serialVersionUID=-3871853910604253256L;
    private KnApproval ka;
    private KnApprovalProcess kap;
    private String name;
    private FileType type;
    private String path;
    private Integer size;
    private Long userId;
    private Long createTime;
    @ManyToOne @JoinColumn(nullable=false, name="approval_id") @JsonIgnore
    public KnApproval getKa(){
        return ka;
    }
    public void setKa(KnApproval ka){
        this.ka=ka;
    }
    @Column(length=60)
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    @NotNull(groups={FileType.class}) @Column(nullable=false, length=20) @Enumerated(EnumType.STRING)
    public FileType getType(){
        return type;
    }
    public void setType(FileType type){
        this.type=type;
    }
    @Column(length=100)
    public String getPath(){
        return path;
    }
    public void setPath(String path){
        this.path=path;
    }
    @Column(length=10)
    public Integer getSize(){
        return size;
    }
    public void setSize(Integer size){
        this.size=size;
    }
    @ManyToOne @JoinColumn(nullable=true, name="approval_process_id") @JsonIgnore
    public KnApprovalProcess getKap(){
        return kap;
    }
    public void setKap(KnApprovalProcess kap){
        this.kap=kap;
    }
    @Column(length=13)
    public Long getCreateTime(){
        return createTime;
    }
    public void setCreateTime(Long createTime){
        this.createTime=createTime;
    }
    @Column(length=13)
    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId=userId;
    }
    public enum FileType{
        TEXT,WORD,EXCEL,PPT,PNG,JPG,AUDIO,VIDEO
    }
}
