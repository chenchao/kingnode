package com.kingnode.workflow.dao;
import com.kingnode.workflow.entity.KnApply;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public interface KnApplyDao extends PagingAndSortingRepository<KnApply,Long>, JpaSpecificationExecutor<KnApply>{
}
