package com.kingnode.third.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.kingnode.xsimple.Setting.ThirdSystemType;
import com.kingnode.xsimple.Setting.UserSystemType;
import com.kingnode.xsimple.entity.AuditEntity;
/**
 * 用户设置表,主要设置来自不同系统的主要性和次要性
 *
 * @author cici
 */
@Entity @Table(name="kn_user_setup_info")
public class KnUserSetupInfo extends AuditEntity{
    private static final long serialVersionUID=2515335020979661868L;
    private String fromSys;//来自系统
    private ThirdSystemType thirdSystemType;//用户系统配置,主账户/辅账户
    private UserSystemType ignoreCase;//系统类型
    @Column(name="fromsys",length=100)
    public String getFromSys(){
        return fromSys;
    }
    public void setFromSys(String fromSys){
        this.fromSys=fromSys;
    }
    @Enumerated(EnumType.STRING) @Column(name="thirdsystemtype",length=20)
    public ThirdSystemType getThirdSystemType(){
        return thirdSystemType;
    }
    public void setThirdSystemType(ThirdSystemType thirdSystemType){
        this.thirdSystemType=thirdSystemType;
    }
    @Enumerated(EnumType.STRING) @Column(name="ignore_case",length=20)
    public UserSystemType getIgnoreCase(){
        return ignoreCase;
    }
    public void setIgnoreCase(UserSystemType ignoreCase){
        this.ignoreCase=ignoreCase;
    }
}