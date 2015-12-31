package com.kingnode.approval.dao;
import com.kingnode.approval.entity.KnFlow;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public interface KnFlowDao extends PagingAndSortingRepository<KnFlow,Long>, JpaSpecificationExecutor<KnFlow>{
}
