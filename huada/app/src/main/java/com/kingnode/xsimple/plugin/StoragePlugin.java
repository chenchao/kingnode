package com.kingnode.xsimple.plugin;
import java.io.File;
import java.util.List;

import com.kingnode.xsimple.api.common.FileInfo;
import com.kingnode.xsimple.entity.system.KnPluginConfig;
import com.kingnode.xsimple.service.PluginConfigService;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public abstract class StoragePlugin implements Comparable<StoragePlugin>{
    @Autowired
    private PluginConfigService pluginConfigService;
    public final String getId(){
        return getClass().getAnnotation(Component.class).value();
    }
    public abstract String getName();
    public abstract String getVersion();
    public abstract String getAuthor();
    public abstract String getSiteUrl();
    public abstract String getInstallUrl();
    public abstract String getUninstallUrl();
    public abstract String getSettingUrl();
    public boolean getIsInstalled(){
        return this.pluginConfigService.pluginIdExists(getId());
    }
    public KnPluginConfig getPluginConfig(){
        return this.pluginConfigService.findByPluginId(getId());
    }
    public boolean getIsEnabled(){
        KnPluginConfig localPluginConfig=getPluginConfig();
        return localPluginConfig!=null?localPluginConfig.getIsEnabled():false;
    }
    public String getAttribute(String name){
        KnPluginConfig localPluginConfig=getPluginConfig();
        return localPluginConfig!=null?localPluginConfig.getAttribute(name):null;
    }
    public Integer getOrder(){
        KnPluginConfig localPluginConfig=getPluginConfig();
        return localPluginConfig!=null?localPluginConfig.getSeq():null;
    }
    public abstract void upload(String paramString1,File paramFile,String paramString2);
    public abstract String getUrl(String paramString);
    public abstract List<FileInfo> browser(String paramString);
    public boolean equals(Object obj){
        if(obj==null){
            return false;
        }
        if(getClass()!=obj.getClass()){
            return false;
        }
        if(this==obj){
            return true;
        }
        StoragePlugin localStoragePlugin=(StoragePlugin)obj;
        return new EqualsBuilder().append(getId(),localStoragePlugin.getId()).isEquals();
    }
    public int hashCode(){
        return new HashCodeBuilder(17,37).append(getId()).toHashCode();
    }
    public int compareTo(StoragePlugin storagePlugin){
        return new CompareToBuilder().append(getOrder(),storagePlugin.getOrder()).append(getId(),storagePlugin.getId()).toComparison();
    }
}