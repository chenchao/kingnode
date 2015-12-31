package com.kingnode.xsimple.entity.system;
import java.util.List;
import java.util.Set;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kingnode.diva.utils.Collections3;
import com.kingnode.xsimple.entity.AuditEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
/**
 * 系统角色表
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @Cacheable(true) @Entity @Table(name="kn_role")
public class KnRole extends AuditEntity{
    private static final long serialVersionUID=2717890766442984886L;
    private String name;//角色名称
    private String code;//角色代号
    private String description;//角色描述
    private Set<KnResource> res;
    private ActiveType active;//状态
    public KnRole(){
    }
    public KnRole(Long id){
        setId(id);
    }
    @Column(length=50)
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    @Column(length=13)
    public String getCode(){
        return code;
    }
    public void setCode(String code){
        this.code=code;
    }
    @Column(length=500)
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description=description;
    }
    // 多对多定义
    @ManyToMany @JoinTable(name="kn_role_resource",joinColumns={@JoinColumn(name="role_id")},inverseJoinColumns={@JoinColumn(name="res_id")})
    // Fecth策略定义
    @Fetch(FetchMode.SUBSELECT)
    // 集合按id排序
    @OrderBy("path") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    public Set<KnResource> getRes(){
        return res;
    }
    public void setRes(Set<KnResource> res){
        this.res=res;
    }
    @Enumerated(EnumType.STRING) @Column(length=10)
    public ActiveType getActive(){
        return active;
    }
    public void setActive(ActiveType active){
        this.active=active;
    }
    @Transient @JsonIgnore
    public List getPermissionList(){
        return Collections3.extractToList(res,"code");
    }
    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this);
    }
}
