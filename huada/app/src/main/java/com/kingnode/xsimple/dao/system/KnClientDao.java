package com.kingnode.xsimple.dao.system;
import com.kingnode.xsimple.entity.system.KnClient;
import org.springframework.data.repository.CrudRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public interface KnClientDao extends CrudRepository<KnClient,Long>{
    public KnClient findByClientId(String clientId);
    public KnClient findByClientSecret(String clientSecret);
}