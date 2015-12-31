package com.kingnode.xsimple.dao.system;
import java.util.List;

import com.kingnode.xsimple.entity.system.KnCustomServiceInfo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnCustomServiceInfoDao extends PagingAndSortingRepository<KnCustomServiceInfo,Long>, JpaSpecificationExecutor<KnCustomServiceInfo>{
    List<KnCustomServiceInfo> findByUserIdAndFromSys(String userId,String formSys);
}
