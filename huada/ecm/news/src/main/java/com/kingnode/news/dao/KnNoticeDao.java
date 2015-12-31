package com.kingnode.news.dao;
import javax.persistence.QueryHint;

import com.kingnode.news.entity.KnNotice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnNoticeDao extends PagingAndSortingRepository<KnNotice,Long>, JpaSpecificationExecutor<KnNotice>{
    /**
     * 查询公告列表
     *
     * @param userId 人员ID
     * @param page
     *
     * @return
     */
    @QueryHints({@QueryHint(name="org.hibernate.cacheable", value="true")})
    @Query("select u  from KnNotice u  where (u.effectiveTime is null or u.effectiveTime>:currTime) and exists(select y.userName from KnNoticeReceipt  y where y.id.noticeId=u.id and y.id.userId=:userId) ")
    Page<KnNotice> queryKnNoticeReceipt(@Param("userId") Long userId,@Param("currTime") Long currTime,Pageable page);
    @QueryHints({@QueryHint(name="org.hibernate.cacheable", value="true")}) Page<KnNotice> findAll(Specification<KnNotice> spec,Pageable pageable);
}
