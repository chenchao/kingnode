package com.kingnode.health.dao;
import com.kingnode.health.entity.KnEcmHealthIcon;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public interface EcmHealthIconDao extends PagingAndSortingRepository<KnEcmHealthIcon,Long>, JpaSpecificationExecutor<KnEcmHealthIcon>{
}