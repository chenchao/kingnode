package com.kingnode.xsimple.entity.system;
import java.util.Set;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kingnode.xsimple.entity.IdEntity.ActiveType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.NotBlank;
/**
 * 员工表
 * userId和userSystem为不可编辑项,生成后不可编辑--cici
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_employee") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @Cacheable(true)
public class KnEmployee implements java.io.Serializable{
    private static final long serialVersionUID=-9108444631356975586L;
    private Long id;
    private String loginName;
    private String userName;
    private String imageAddress;//用户图像
    private String phone;//手机
    private String telephone;//座机
    private String email;//员工邮箱
    private String address;//地址
    private ActiveType job=ActiveType.DISABLE;//离职状态
    private Gender sex=Gender.NONE;//性别
    private String signature;//个性签名
    private Set<KnEmployeeOrganization> org;
    private Set<KnEmployeePosition> pos;
    private Set<KnTeam> team;
    //以下供第三方系统导入使用
    private String userType;//用户类型（员工，供应商，采购商）
    private String userId;//用户ID
    private String userSystem;//用户来自系统
    private String markName="";//用户的唯一标识,用于标识一个人拥有多个用户时候的信息
    private String weixinId="";//微信的唯一标识,微信id,主要用于用户绑定微信账号
    public KnEmployee(){
    }
    public KnEmployee(Long id){
        setId(id);
    }
    @Id
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    @NotBlank @Column(name="login_name", nullable=false, unique=true, length=100)
    public String getLoginName(){
        return loginName;
    }
    public void setLoginName(String loginName){
        this.loginName=loginName;
    }
    @Column(name="user_name", length=40)
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName=userName;
    }
    @Column(name="image_address", length=100)
    public String getImageAddress(){
        return imageAddress;
    }
    public void setImageAddress(String imageAddress){
        this.imageAddress=imageAddress;
    }
    @Column(length=20)
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone=phone;
    }
    @Column(length=20)
    public String getTelephone(){
        return telephone;
    }
    public void setTelephone(String telephone){
        this.telephone=telephone;
    }
    @Column(length=100)
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email=email;
    }
    @Column(length=200)
    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address=address;
    }
    @Enumerated(EnumType.STRING) @Column(length=10)
    public Gender getSex(){
        return sex;
    }
    public void setSex(Gender sex){
        this.sex=sex;
    }
    @Enumerated(EnumType.STRING) @Column(length=10)
    public ActiveType getJob(){
        return job;
    }
    public void setJob(ActiveType job){
        this.job=job;
    }
    @Column(name="signature",columnDefinition="varchar(255) default ''")
    public String getSignature(){
        return signature;
    }
    public void setSignature(String signature){
        this.signature=signature;
    }
    //    多对多定义
    @OneToMany(mappedBy="id.emp", targetEntity=KnEmployeeOrganization.class, cascade=CascadeType.ALL, orphanRemoval=true)
    // Fecth策略定义
    @Fetch(FetchMode.SUBSELECT) @JsonIgnore @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    public Set<KnEmployeeOrganization> getOrg(){
        return org;
    }
    public void setOrg(Set<KnEmployeeOrganization> org){
        this.org=org;
    }
    //    多对多定义
    @OneToMany(mappedBy="id.emp", targetEntity=KnEmployeePosition.class, cascade=CascadeType.ALL, orphanRemoval=true)
    // Fecth策略定义
    @Fetch(FetchMode.SUBSELECT) @JsonIgnore @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    public Set<KnEmployeePosition> getPos(){
        return pos;
    }
    public void setPos(Set<KnEmployeePosition> pos){
        this.pos=pos;
    }
    //  一多定义
    @ManyToMany(cascade=CascadeType.ALL) @JoinTable(name="kn_employee_team", joinColumns={@JoinColumn(name="emp_id")}, inverseJoinColumns={@JoinColumn(name="team_id")})
    // Fecth策略定义
    @Fetch(FetchMode.SUBSELECT)
    // 集合按id排序
    @OrderBy("id ASC") @JsonIgnore @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    public Set<KnTeam> getTeam(){
        return team;
    }
    public void setTeam(Set<KnTeam> team){
        this.team=team;
    }
    @Column(name="user_id", length=200)
    public String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId=userId;
    }
    @Column(name="user_system", length=40)
    public String getUserSystem(){
        return userSystem;
    }
    public void setUserSystem(String userSystem){
        this.userSystem=userSystem;
    }
    @Column(name="user_type", length=40)
    public String getUserType(){
        return userType;
    }
    public void setUserType(String userType){
        this.userType=userType;
    }
    @Column(name="mark_name", length=100)
    public String getMarkName(){
        return markName;
    }
    public void setMarkName(String markName){
        this.markName=markName;
    }
    @Column(name="weixin_id", length=100)
    public String getWeixinId(){
        return weixinId;
    }
    public void setWeixinId(String weixinId){
        this.weixinId=weixinId;
    }
    public enum Gender{
        MAN("男"),WOMEN("女"),NONE("无");
        private final String s_type;
        private Gender(final String s_type){
            this.s_type=s_type;
        }
        public String getTypeName(){
            return s_type;
        }
    }
    public enum UserType{
        employee,supplier
    }
}
