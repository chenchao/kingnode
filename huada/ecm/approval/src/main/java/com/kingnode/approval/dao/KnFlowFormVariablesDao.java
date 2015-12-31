package com.kingnode.approval.dao;
import com.kingnode.approval.entity.KnFlowFormVariables;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public interface KnFlowFormVariablesDao extends PagingAndSortingRepository<KnFlowFormVariables,Long>, JpaSpecificationExecutor<KnFlowFormVariables>{
}
