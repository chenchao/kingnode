package com.kingnode.third.dao;
import java.util.List;

import com.kingnode.third.entity.KnUserSetupInfo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public interface KnUserSetupInfoDao extends PagingAndSortingRepository<KnUserSetupInfo, Long>, JpaSpecificationExecutor<KnUserSetupInfo>{
    List<KnUserSetupInfo> findByFromSys(@Param("fromSys") String fromSys);
}
