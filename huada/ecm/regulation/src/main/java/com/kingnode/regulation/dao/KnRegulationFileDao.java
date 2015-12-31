package com.kingnode.regulation.dao;
import java.util.List;

import com.kingnode.regulation.entity.KnRegulationFile;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public interface KnRegulationFileDao extends PagingAndSortingRepository<KnRegulationFile,Long>, JpaSpecificationExecutor<KnRegulationFile>{
    List<KnRegulationFile> findByClassification(String classification);
}
