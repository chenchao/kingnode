package com.kingnode.approval.dao;
import com.kingnode.approval.entity.KnApprovalProcess;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnApprovalProcessDao extends PagingAndSortingRepository<KnApprovalProcess,Long>, JpaSpecificationExecutor<KnApprovalProcess>{
    @Modifying @Query("update KnApprovalProcess set resultType='process' where id=:id") int setResultType(@Param(value="id") Long id);
}
