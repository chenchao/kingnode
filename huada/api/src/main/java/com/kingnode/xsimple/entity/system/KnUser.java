package com.kingnode.xsimple.entity.system;
import java.util.List;
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
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.kingnode.diva.utils.Collections3;
import com.kingnode.xsimple.entity.AuditEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
/**
 * 系统用户表
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @Cacheable(true) @Entity @Table(name="kn_user")
public class KnUser extends AuditEntity{
    private static final long serialVersionUID=-8334005690084605988L;
    private String loginName;//登录账号
    private String password;//登录密码
    private String salt;
    private String name;//用户名
    private String email;//邮箱
    private ActiveType status;//状态  （启用  与未启用）
    private int userOnline;
    private Long loginTime;
    private String loginIp;
    private List<KnRole> role=Lists.newArrayList(); // 有序的关联对象集合
    private String plainPassword;
    //增加约束
    @NotBlank @Column(nullable=false, unique=true, length=100)
    public String getLoginName(){
        return loginName;
    }
    public void setLoginName(String loginName){
        this.loginName=loginName;
    }
    @Column(length=100)
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password=password;
    }
    @NotBlank @Column(length=20)
    public String getSalt(){
        return salt;
    }
    public void setSalt(String salt){
        this.salt=salt;
    }
    @Column(length=50)
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    @Email @NotBlank @Column(length=100)
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email=email;
    }
    @NotNull @Enumerated(EnumType.STRING) @Column(length=10)
    public ActiveType getStatus(){
        return status;
    }
    public void setStatus(ActiveType status){
        this.status=status;
    }
    @Column(length=2)
    public int getUserOnline(){
        return userOnline;
    }
    public void setUserOnline(int userOnline){
        this.userOnline=userOnline;
    }
    @Column(length=13)
    public Long getLoginTime(){
        return loginTime;
    }
    public void setLoginTime(Long loginTime){
        this.loginTime=loginTime;
    }
    @Column(name="login_ip", length=50)
    public String getLoginIp(){
        return loginIp;
    }
    public void setLoginIp(String loginIp){
        this.loginIp=loginIp;
    }
    // 多对多定义
    @ManyToMany @JoinTable(name="kn_user_role", joinColumns={@JoinColumn(name="user_id")}, inverseJoinColumns={@JoinColumn(name="role_id")})
    // Fecth策略定义
    @Fetch(FetchMode.SUBSELECT)
    // 集合按id排序
    @OrderBy("id ASC")
    // 缓存策略n
    @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    public List<KnRole> getRole(){
        return role;
    }
    public void setRole(List<KnRole> role){
        this.role=role;
    }
    @Transient
    public String getPlainPassword(){
        return plainPassword;
    }
    public void setPlainPassword(String plainPassword){
        this.plainPassword=plainPassword;
    }
    @Transient @JsonIgnore
    public String getRoleNames(){
        return Collections3.extractToString(role,"name",", ");
    }
    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this);
    }
}