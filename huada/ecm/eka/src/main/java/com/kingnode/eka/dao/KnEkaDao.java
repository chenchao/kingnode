package com.kingnode.eka.dao;
import java.util.List;

import com.kingnode.eka.entity.KnEka;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public interface KnEkaDao extends PagingAndSortingRepository<KnEka,Long>, JpaSpecificationExecutor<KnEka>{
}