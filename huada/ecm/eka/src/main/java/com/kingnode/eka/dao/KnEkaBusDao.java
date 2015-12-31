package com.kingnode.eka.dao;
import java.util.List;

import com.kingnode.eka.entity.KnEkaBus;
import com.kingnode.eka.entity.KnEkaCommentLog;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */

@SuppressWarnings("ALL")
public interface KnEkaBusDao extends PagingAndSortingRepository<KnEkaBus,Long>, JpaSpecificationExecutor<KnEkaBus>{
    List<KnEkaBus> findByEkaId(Long ekaId);
}