package com.kingnode.approval.dao;
import java.util.List;

import com.kingnode.approval.entity.KnApprovalVariables;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnApprovalVariablesDao extends PagingAndSortingRepository<KnApprovalVariables,Long>, JpaSpecificationExecutor<KnApprovalVariables>{
    @Query("select sum(u.value),u.name from KnApprovalVariables u where u.ka.nodeType='end' and u.ka.userId=:userId and u.ka.form.id=:formId group by u.name ")
    List<Object[]> findReimbursementReport(@Param(value="userId") Long userId,@Param(value="formId") Long formId);
}
