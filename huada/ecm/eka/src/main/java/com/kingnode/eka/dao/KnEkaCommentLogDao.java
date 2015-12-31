package com.kingnode.eka.dao;
import java.util.List;

import com.kingnode.eka.entity.KnEka;
import com.kingnode.eka.entity.KnEkaCommentLog;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */

@SuppressWarnings("ALL")
public interface KnEkaCommentLogDao extends PagingAndSortingRepository<KnEkaCommentLog,Long>, JpaSpecificationExecutor<KnEkaCommentLog>{
    @Query("select u  from KnEkaCommentLog u  where u.userId=?1 and u.ekaId=?2")
    public KnEkaCommentLog queryEkaCommentLog(Long userId,Long ekaId);
}