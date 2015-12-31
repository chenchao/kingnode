package com.kingnode.meeting.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_meeting_file") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class KnMeetingFile extends AuditEntity{
    private static final long serialVersionUID=-344834847340841721L;
    private String name;
    private String sha;
    private Long fileDate;
    private Integer fileSize;
    private String fileType;
    private Category category;
    private KnMeeting km;
    @Column(length=200)
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    @Column(name="file_size",length=10)
    public Integer getFileSize(){
        return fileSize;
    }
    public void setFileSize(Integer fileSize){
        this.fileSize=fileSize;
    }
    @Column(name="file_date",length=13)
    public Long getFileDate(){
        return fileDate;
    }
    public void setFileDate(Long fileDate){
        this.fileDate=fileDate;
    }
    @Column(name="sha",length=100)
    public String getSha(){
        return sha;
    }
    public void setSha(String sha){
        this.sha=sha;
    }
    @ManyToOne @JoinColumn(name="room_id")
    public KnMeeting getKm(){
        return km;
    }
    public void setKm(KnMeeting km){
        this.km=km;
    }
    @Enumerated(EnumType.STRING) @Column(length=10)
    public Category getCategory(){
        return category;
    }
    public void setCategory(Category category){
        this.category=category;
    }
    @Column(name="file_type",length=200)
    public String getFileType(){
        return fileType;
    }
    public void setFileType(String fileType){
        this.fileType=fileType;
    }
    public enum Category{
        MEETING,SUMMARY
    }
}