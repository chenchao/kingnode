package com.kingnode.social.dao;
import java.util.List;

import com.kingnode.social.entity.KnSocialBlog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnSocialBlogDao extends PagingAndSortingRepository<KnSocialBlog,Long>, JpaSpecificationExecutor<KnSocialBlog>{
    @Query("select u from KnSocialBlog u where u.active='ENABLE' and (u.endTime is null or u.endTime>=:currentTime)  order by u.type desc,u.publishTime desc") Page<KnSocialBlog>
    findAllKnSocialBlog(@Param("currentTime")long currentTime,Pageable pageable);
    @Query("select u from KnSocialBlog u where u.type=2  order by u.publishTime desc") Page<KnSocialBlog>
    findActivityBlog(Pageable pageable);
    @Query("select count(u) from KnSocialBlog u where u.userId=:userId and u.type=1 ") Integer findKnSocialBlogCountByUserId(@Param(value="userId") Long userId);
    @Query("select u from KnSocialBlog u where u.active='ENABLE' and u.userId=:userId and u.type=1 order by u.publishTime desc")
    Page<KnSocialBlog> findKnSocialBlogByUserId(@Param(value="userId") Long userId,Pageable pageable);
    @Query("select u.id from KnSocialBlog u where u.userId=:userId") List<Long> findIds(@Param(value="userId") Long userId);
    @Query("select u from KnSocialBlog u where u.active='ENABLE' and u.parentId in :ids order by u.publishTime desc")
    Page<KnSocialBlog> findTranspondKnSocialBlog(@Param(value="ids") List<Long> ids,Pageable pageable);
    @Query("select u from KnSocialBlog u,KnSocialBlogAttention s where u.active='ENABLE' and u.userId=s.focusUserId and s.userId=:userId order by u.publishTime desc")
    Page<KnSocialBlog> findFocusUserBlog(@Param(value="userId") Long userId,Pageable pageable);
    @Query("select u from KnSocialBlog u,KnSocialBlogAt a where u.active='ENABLE' and u.id=a.mId and a.atUserId=:userId order by a.atTime desc")
    Page<KnSocialBlog> findAtUserBlog(@Param(value="userId") Long userId,Pageable pageable);
}
