package com.kingnode.social.dao;
import java.util.List;

import com.kingnode.social.entity.KnSocialBlog;
import com.kingnode.social.entity.KnSocialBlogCollect;
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
public interface KnSocialBlogCollectDao extends PagingAndSortingRepository<KnSocialBlogCollect,Long>, JpaSpecificationExecutor<KnSocialBlogCollect>{
    @Query("select u.ksb.id from KnSocialBlogCollect u where u.userId=:userId") List<Long> findKnSocialBlogCollectUserId(@Param(value="userId") Long userId);
    KnSocialBlogCollect findByUserIdAndKsbId(Long userId,Long id);
    @Query("select count(u) from KnSocialBlogCollect u where u.userId=:userId") Integer findKnSocialBlogCollectCount(@Param(value="userId") Long userId);
    KnSocialBlogCollect findByUserIdAndId(Long userId,Long id);
    @Query("select u.ksb from KnSocialBlogCollect u where u.ksb.active='ENABLE' and u.userId=:userId order by u.ksb.publishTime desc ") Page<KnSocialBlog> findKnSocialBlogByUserId(@Param(value="userId") Long userId,Pageable pageable);
    @Modifying
    @Query("delete from KnSocialBlogCollect u where u.ksb.id=:id")
    void deleteByBlogId(@Param(value="id")Long id);
}
