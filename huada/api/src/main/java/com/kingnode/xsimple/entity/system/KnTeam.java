package com.kingnode.xsimple.entity.system;
import java.util.Set;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.entity.IdEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
/**
 * 团队表
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_team") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @Cacheable(true)
public class KnTeam extends IdEntity{
    private static final long serialVersionUID=-6318347940649306088L;
    private String code;
    private String name;
    private String description;
    private KnEmployee master;
    private Set<KnEmployee> emps;
    private Setting.ActiveType active;
    public KnTeam(String code,String name,String description,KnEmployee master,Setting.ActiveType active){
        this.code=code;
        this.name=name;
        this.description=description;
        this.master=master;
        this.active=active;
    }
    public KnTeam(){
    }
    public KnTeam(Long id){
        setId(id);
    }
    public String getCode(){
        return code;
    }
    public void setCode(String code){
        this.code=code;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description=description;
    }
    @NotNull @OneToOne @JoinColumn(name="master_id") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    public KnEmployee getMaster(){
        return master;
    }
    public void setMaster(KnEmployee master){
        this.master=master;
    }
    //  一多定义
    @ManyToMany @JoinTable(name="kn_employee_team",joinColumns={@JoinColumn(name="team_id")},inverseJoinColumns={@JoinColumn(name="emp_id")})
    // Fecth策略定义
    @Fetch(FetchMode.SUBSELECT)
    // 集合按id排序
    @OrderBy("id ASC") @org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    public Set<KnEmployee> getEmps(){
        return emps;
    }
    public void setEmps(Set<KnEmployee> emps){
        this.emps=emps;
    }
    @NotNull(groups={Setting.ActiveType.class}) @Column(name="active", length=20) @Enumerated(EnumType.STRING)
    public Setting.ActiveType getActive(){
        return active;
    }
    public void setActive(Setting.ActiveType active){
        this.active=active;
    }
    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this);
    }
}