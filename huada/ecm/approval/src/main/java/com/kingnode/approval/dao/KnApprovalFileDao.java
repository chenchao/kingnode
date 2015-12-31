package com.kingnode.approval.dao;
import java.util.List;

import com.kingnode.approval.entity.KnApprovalFile;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnApprovalFileDao extends PagingAndSortingRepository<KnApprovalFile,Long>, JpaSpecificationExecutor<KnApprovalFile>{
    @Query("select u from KnApprovalFile u where u.userId=:userId and u.type in :fileTypes")
    List<KnApprovalFile> findKnApprovalFile(@Param(value="userId") Long userId,@Param(value="fileTypes") List<KnApprovalFile.FileType> fileTypes);
}
