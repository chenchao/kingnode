package com.kingnode.approval.dao;
import com.kingnode.approval.entity.KnFlowNode;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public interface KnFlowNodeDao extends PagingAndSortingRepository<KnFlowNode,Long>, JpaSpecificationExecutor<KnFlowNode>{
}
