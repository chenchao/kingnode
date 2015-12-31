package com.kingnode.health.dao;
import java.util.List;

import com.kingnode.health.entity.KnEcmHealthSet;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface EcmHealthSetDao extends PagingAndSortingRepository<KnEcmHealthSet,Long>, JpaSpecificationExecutor<KnEcmHealthSet>{
    /**
     * 获取设置对象集合,降序
     * @return List<KnEcmHealthSet>
     */
    @Query("select h from KnEcmHealthSet h order by h.id desc")
    List<KnEcmHealthSet> ListKnEcmHealthSet();
}