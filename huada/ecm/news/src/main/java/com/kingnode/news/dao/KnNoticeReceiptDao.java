package com.kingnode.news.dao;
import java.util.List;
import javax.persistence.QueryHint;

import com.kingnode.news.entity.KnNoticeReceipt;
import com.kingnode.xsimple.entity.IdEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnNoticeReceiptDao extends PagingAndSortingRepository<KnNoticeReceipt,Long>, JpaSpecificationExecutor<KnNoticeReceipt>{
    /**
     * 分页查询公告回执对象
     *
     * @param id
     * @param userName
     * @param page
     *
     * @return
     */
    @QueryHints({@QueryHint(name="org.hibernate.cacheable", value="true")})
    @Query("select u  from KnNoticeReceipt u  where u.id.noticeId=:id and u.userName like:userName and u.isReceipt in(:isReceipts)")
    Page<KnNoticeReceipt> queryKnNoticeReceipt(@Param("id") Long id,@Param("userName") String userName,@Param("isReceipts") List<IdEntity.ActiveType> isReceipts,Pageable page);
    /**
     * 获取单个公告回执对象
     *
     * @param id       公告id
     * @param userName 回执人姓名
     *
     * @return
     */
    @QueryHints({@QueryHint(name="org.hibernate.cacheable", value="true")}) @Query("select u  from KnNoticeReceipt u  where u.id.noticeId=:id and u.id.userId=:userId ")
    KnNoticeReceipt queryKnNoticeReceipt(@Param("id") Long id,@Param("userId") Long userId);
    /**
     * 查找某个人的回执列表
     *
     * @param userId
     *
     * @return
     */
    @QueryHints({@QueryHint(name="org.hibernate.cacheable", value="true")}) @Query("select u  from KnNoticeReceipt u  where u.id.userId=:userId and u.isReceipt=:isReceipt")
    List<KnNoticeReceipt> queryKnNoticeReceipts(@Param("userId") Long userId,@Param("isReceipt") IdEntity.ActiveType isReceipt);

    @QueryHints({@QueryHint(name="org.hibernate.cacheable", value="true")}) @Query("select u  from KnNoticeReceipt u  where u.id.noticeId=?1 ")
    List<KnNoticeReceipt> queryKnNoticeReceipts(Long noticId);

}