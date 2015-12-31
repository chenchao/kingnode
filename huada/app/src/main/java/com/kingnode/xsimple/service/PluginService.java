package com.kingnode.xsimple.service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import com.kingnode.xsimple.plugin.StoragePlugin;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.stereotype.Service;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Service
public class PluginService{
    @Resource
    private List<StoragePlugin> storage=new ArrayList<>();
    @Resource
    private Map<String,StoragePlugin> storageMap=new HashMap<>();
    public List<StoragePlugin> getStoragePlugins(){
        Collections.sort(this.storage);
        return this.storage;
    }
    public List<StoragePlugin> getStoragePlugins(boolean isEnabled){
        List<StoragePlugin> localArrayList=new ArrayList<>();
        CollectionUtils.select(this.storage,new StorageSelect(this,isEnabled),localArrayList);
        Collections.sort(localArrayList);
        return localArrayList;
    }
    public StoragePlugin getStoragePlugin(String id){
        return this.storageMap.get(id);
    }
    class StorageSelect implements Predicate{
        private boolean paramBoolean;
        StorageSelect(PluginService paramPluginService,boolean paramBoolean){
            this.paramBoolean=paramBoolean;
        }
        public boolean evaluate(Object object){
            StoragePlugin localStoragePlugin=(StoragePlugin)object;
            return localStoragePlugin.getIsEnabled()==this.paramBoolean;
        }
    }
}