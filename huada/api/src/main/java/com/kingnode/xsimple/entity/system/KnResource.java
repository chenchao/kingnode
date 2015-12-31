package com.kingnode.xsimple.entity.system;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
/**
 * 系统资源表
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @Cacheable(true) @Entity @Table(name="kn_resource")
public class KnResource extends AuditEntity implements Comparable<KnResource>{
    private static final long serialVersionUID=-3011333330626690904L;
    private String name; //资源名称
    private String code;//资源代号
    private Long supId; //父菜单ID
    private String path;//路径
    private Long depth;//级别
    private String url;//属性
    private Long seq;//排序值
    private String icon;//图标
    private ResourceType type;//资源类型
    private ActiveType active;
    //模块与功能
    private String enTitle;//英文名称
    private String description;//描述
    private String markName;//标识名称，标志
    private String version;//版本号
    public KnResource(){
    }
    public KnResource(Long id){
        setId(id);
    }
    /**
     * **********
     *
     * @author kongjiangwei
     * @description 用于布局管理
     */
    public KnResource(Long id,String name,String icon,String description){
        this.id=id;
        this.name=name;
        this.icon=icon;
        this.description=description;
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
    public Long getSupId(){
        return supId;
    }
    public void setSupId(Long supId){
        this.supId=supId;
    }
    public String getPath(){
        return path;
    }
    public void setPath(String path){
        this.path=path;
    }
    public Long getDepth(){
        return depth;
    }
    public void setDepth(Long depth){
        this.depth=depth;
    }
    public String getUrl(){
        return url;
    }
    public void setUrl(String url){
        this.url=url;
    }
    public Long getSeq(){
        return seq;
    }
    public void setSeq(Long seq){
        this.seq=seq;
    }
    public String getIcon(){
        return icon;
    }
    public void setIcon(String icon){
        this.icon=icon;
    }
    @Enumerated(EnumType.STRING)
    public ResourceType getType(){
        return type;
    }
    public void setType(ResourceType type){
        this.type=type;
    }
    @Enumerated(EnumType.STRING)
    public ActiveType getActive(){
        return active;
    }
    public void setActive(ActiveType active){
        this.active=active;
    }
    public String getEnTitle(){
        return enTitle;
    }
    public void setEnTitle(String enTitle){
        this.enTitle=enTitle;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description=description;
    }
    public String getMarkName(){
        return markName;
    }
    public void setMarkName(String markName){
        this.markName=markName;
    }
    public String getVersion(){
        return version;
    }
    public void setVersion(String version){
        this.version=version;
    }
    @Override
    public int compareTo(KnResource o){
        return this.getSeq().compareTo(o.getSeq());
    }
    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this);
    }
    public enum ActiveType{
        ENABLE("可用"),COMMON("公用"),DISABLE("不可用"),PUBLICPACKAGE("公共包"),SETUPFILE("安装包");
        private final String s_type;
        ActiveType(final String s_type){
            this.s_type=s_type;
        }
        /**
         * @return Returns the s_type.
         */
        public String getTypeName(){
            return s_type;
        }
    }
    public enum ResourceType{
        MENU,//菜单
        URL, //ajax地址
        BUTTON,
        MOBILE,
        API,
        MODULE,
        FUNCTION
    }
}