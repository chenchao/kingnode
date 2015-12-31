package com.kingnode.approval.dao;
import java.util.List;

import com.kingnode.approval.entity.KnApproval;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnApprovalDao extends PagingAndSortingRepository<KnApproval,Long>, JpaSpecificationExecutor<KnApproval>{
    Page<KnApproval> findByUserId(Long userId,Pageable pageable);
    Page<KnApproval> findByUserIdAndContentLike(Long userId,String content,Pageable pageable);
    @Modifying @Query("update KnApproval u set u.nodeType=:nodeType where u.id=:id")
    int setNodeType(@Param(value="id") Long id,@Param(value="nodeType") KnApproval.NodeType nodeType);
    @Query("select u.form.id,u.form.name,count(u) as num from KnApproval u where u.userId=:userId group by u.form.id")
    List<Object[]> findApprovalCount(@Param(value="userId") Long userId);
}
