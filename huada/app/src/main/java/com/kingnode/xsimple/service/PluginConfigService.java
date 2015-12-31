package com.kingnode.xsimple.service;
import com.kingnode.xsimple.dao.KnPluginConfigDao;
import com.kingnode.xsimple.entity.system.KnPluginConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Service
@Transactional(readOnly=true)
public class PluginConfigService{
    private KnPluginConfigDao kpcd;
    @Autowired
    public void setKpcd(KnPluginConfigDao kpcd){
        this.kpcd=kpcd;
    }
    public boolean pluginIdExists(String pluginId){
        if(pluginId==null){
            return false;
        }
        Long count=kpcd.countByPluginId(pluginId);
        return count>0L;
    }
    public KnPluginConfig findByPluginId(String pluginId){
        if(pluginId==null){
            return null;
        }
        return kpcd.findByPluginId(pluginId);
    }
    public void SaveKnPluginConfig(KnPluginConfig kpc){
        kpcd.save(kpc);
    }
}
