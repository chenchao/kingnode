package com.kingnode.xsimple.entity.system;
import java.util.Set;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kingnode.xsimple.entity.AuditEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
/**
 * 岗位表
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_position") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @Cacheable(true)
public class KnPosition extends AuditEntity{
    private static final long serialVersionUID=-5612728553672221019L;
    private String code;
    private String name;
    private String description;
    private Long supId;
    private String path;
    private Long depth;
    private Long seq=0L;
    private Set<KnEmployeePosition> emps;
    private ActiveType active;
    public KnPosition(){
    }
    public KnPosition(Long id){
        setId(id);
    }
    @Column(length=10)
    public String getCode(){
        return code;
    }
    public void setCode(String code){
        this.code=code;
    }
    @Column(length=100)
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    @Column(length=200)
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description=description;
    }
    @Column(length=13)
    public Long getSupId(){
        return supId;
    }
    public void setSupId(Long supId){
        this.supId=supId;
    }
    @Column(length=100)
    public String getPath(){
        return path;
    }
    public void setPath(String path){
        this.path=path;
    }
    @Column(length=5)
    public Long getDepth(){
        return depth;
    }
    public void setDepth(Long depth){
        this.depth=depth;
    }
    @Column(length=13)
    public Long getSeq(){
        return seq;
    }
    public void setSeq(Long seq){
        this.seq=seq;
    }
    @Enumerated(EnumType.STRING) @Column(length=10)
    public ActiveType getActive(){
        return active;
    }
    public void setActive(ActiveType active){
        this.active=active;
    }
    //  一多定义
    @OneToMany(mappedBy="id.pos",targetEntity=KnEmployeePosition.class)
    // Fecth策略定义
    @Fetch(FetchMode.SUBSELECT) @JsonIgnore @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    public Set<KnEmployeePosition> getEmps(){
        return emps;
    }
    public void setEmps(Set<KnEmployeePosition> emps){
        this.emps=emps;
    }
    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this);
    }
}