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
import com.kingnode.xsimple.entity.IdEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
/**
 * 组织结构表
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_organization") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @Cacheable(true)
public class KnOrganization extends IdEntity{
    private static final long serialVersionUID=-3125262430737955267L;
    private String code;//编码,没有意义
    private String name;//名称
    private String description; //描述
    private Long supId; //父节点id
    private String path; //目录结构,树形结构体现
    private Long depth; //第几层,1级2级目录
    private Long seq=0L;//排序
    private OrgType orgType; //组织类型,集团
    private ActiveType active; //是否启动
    private Set<KnEmployeeOrganization> emps; //组织下的员工
    public KnOrganization(){
    }
    public KnOrganization(Long id){
        setId(id);
    }
    @Column(length=100)
    public String getCode(){
        return code;
    }
    public void setCode(String code){
        this.code=code;
    }
    @Column(length=50)
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
    @Column(length=10)
    public Long getSeq(){
        return seq;
    }
    public void setSeq(Long seq){
        this.seq=seq;
    }
    @Enumerated(EnumType.STRING) @Column(length=20)
    public OrgType getOrgType(){
        return orgType;
    }
    public void setOrgType(OrgType orgType){
        this.orgType=orgType;
    }
    @Enumerated(EnumType.STRING) @Column(length=10)
    public ActiveType getActive(){
        return active;
    }
    public void setActive(ActiveType active){
        this.active=active;
    }
    //  一多定义
    @OneToMany(mappedBy="id.org",targetEntity=KnEmployeeOrganization.class)
    // Fecth策略定义
    @Fetch(FetchMode.SUBSELECT) @JsonIgnore @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    public Set<KnEmployeeOrganization> getEmps(){
        return emps;
    }
    public void setEmps(Set<KnEmployeeOrganization> emps){
        this.emps=emps;
    }
    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this);
    }
    public enum OrgType{
        GROUP,COMPANY,ORGANIZATION,DEPARTMENT,
    }
}
