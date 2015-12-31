package com.kingnode.xsimple.entity.system;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * 信息反馈对应的系统属性表
 *
 * @author dengfeng
 */
@Entity @Table(name="kn_system_propertie_info")
public class KnSystemPropertieInfo extends AuditEntity{
    private static final long serialVersionUID=-2721075007270250250L;
    private String telNum;//电话
    private String email; //邮箱
    @Column(name="tel_num",length=50)
    public String getTelNum(){
        return telNum;
    }
    public void setTelNum(String telNum){
        this.telNum=telNum;
    }
    @Column(name="email",length=50)
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email=email;
    }
}
