package com.kingnode.approval.dao;
import com.kingnode.approval.entity.KnFlowForm;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public interface KnFlowFormDao extends PagingAndSortingRepository<KnFlowForm,Long>, JpaSpecificationExecutor<KnFlowForm>{
}
