package com.kingnode.workflow.dao;
import com.kingnode.workflow.entity.KnWorkflowAttach;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public interface KnWorkflowAttachDao extends PagingAndSortingRepository<KnWorkflowAttach,Long>, JpaSpecificationExecutor<KnWorkflowAttach>{
}
