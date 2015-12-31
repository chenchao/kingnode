package com.kingnode.xsimple.entity.system;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kingnode.xsimple.entity.AuditEntity;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_plugin_config")
public class KnPluginConfig extends AuditEntity{
    private static final long serialVersionUID=4641637664102409L;
    private String pluginId;
    private Boolean isEnabled;
    private Map<String,String> attributes=new HashMap<>();
    private Integer seq;
    @Column(nullable=false, updatable=false, unique=true)
    public String getPluginId(){
        return this.pluginId;
    }
    public void setPluginId(String pluginId){
        this.pluginId=pluginId;
    }
    @Column(nullable=false)
    public Boolean getIsEnabled(){
        return this.isEnabled;
    }
    public void setIsEnabled(Boolean isEnabled){
        this.isEnabled=isEnabled;
    }
    @ElementCollection(fetch=FetchType.EAGER) @CollectionTable(name="kn_plugin_config_attribute")
    public Map<String,String> getAttributes(){
        return this.attributes;
    }
    public void setAttributes(Map<String,String> attributes){
        this.attributes=attributes;
    }
    @JsonProperty @Column(length=5)
    public Integer getSeq(){
        return seq;
    }
    public void setSeq(Integer seq){
        this.seq=seq;
    }
    @Transient
    public String getAttribute(String name){
        if((getAttributes()!=null)&&(name!=null)){
            return getAttributes().get(name);
        }
        return null;
    }
    @Transient
    public void setAttribute(String name,String value){
        if((getAttributes()!=null)&&(name!=null)){
            getAttributes().put(name,value);
        }
    }
}

