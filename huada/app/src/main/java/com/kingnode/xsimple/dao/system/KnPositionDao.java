package com.kingnode.xsimple.dao.system;
import java.util.List;

import com.kingnode.xsimple.entity.system.KnPosition;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public interface KnPositionDao extends PagingAndSortingRepository<KnPosition,Long>, JpaSpecificationExecutor<KnPosition>{
    List<KnPosition> findByPathLike(String path);
}