package com.kingnode.xsimple.dao;
import com.kingnode.xsimple.entity.system.KnPluginConfig;
import org.springframework.data.repository.CrudRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public interface KnPluginConfigDao extends CrudRepository<KnPluginConfig,Long>{
    public Long countByPluginId(String pluginId);
    public KnPluginConfig findByPluginId(String pluginId);
}