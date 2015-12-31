package com.kingnode.xsimple.entity.system;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * 意见反馈表
 *
 * @author dengfeng
 */
@Entity @Table(name="kn_ideafeed_back")
public class KnIdeaFeedback extends AuditEntity{
    private static final long serialVersionUID=2247905794846390600L;
    private String appKey;//应用的appkey
    private String fromSys;
    private String linkPerop; //联系人姓名
    private String phone; //联系电话
    private String content; //内容
    private String packageName; //客户端应用程序的包名
    @Column(name="app_key",length=100)
    public String getAppKey(){
        return appKey;
    }
    public void setAppKey(String appKey){
        this.appKey=appKey;
    }
    @Column(name="fromsys",length=50)
    public String getFromSys(){
        return fromSys;
    }
    public void setFromSys(String fromSys){
        this.fromSys=fromSys;
    }
    @Column(name="link_perop",length=100)
    public String getLinkPerop(){
        return linkPerop;
    }
    public void setLinkPerop(String linkPerop){
        this.linkPerop=linkPerop;
    }
    @Column(name="phone",length=50)
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone=phone;
    }
    @Column(name="content",length=500)
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content=content;
    }
    @Column(name="package_name",length=100)
    public String getPackageName(){
        return packageName;
    }
    public void setPackageName(String packageName){
        this.packageName=packageName;
    }
}
