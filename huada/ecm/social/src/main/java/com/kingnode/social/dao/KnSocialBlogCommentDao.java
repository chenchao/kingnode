package com.kingnode.social.dao;
import com.kingnode.social.entity.KnSocialBlogComment;
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
public interface KnSocialBlogCommentDao extends PagingAndSortingRepository<KnSocialBlogComment,Long>, JpaSpecificationExecutor<KnSocialBlogComment>{
    @Query("select u from KnSocialBlogComment u where u.active='ENABLE' and u.mId=:mId order by u.commentTime desc")
    Page<KnSocialBlogComment> findCommentMId(@Param(value="mId")Long mId,Pageable pageable);
    @Query("select u from KnSocialBlogComment u where u.active='ENABLE' and u.mId=:mId and u.pId=:pId order by u.commentTime desc")
    Page<KnSocialBlogComment> findCommentMIdAndPId(@Param(value="mId") Long mId,@Param(value="pId") Long pId,Pageable pageable);
    @Query("select u from KnSocialBlogComment u,KnSocialBlog b where u.active='ENABLE' and u.mId=b.id and b.userId=:userId order by u.commentTime desc")
    Page<KnSocialBlogComment> findKnSocialBlogCommentByMId(@Param(value="userId") Long userId,Pageable pageable);
    @Query("select count(u.id) from KnSocialBlogComment u where  u.mId=?1 ")
    Long findBlogCommentCountByMId(Long mId);

    @Modifying
    @Query("delete from KnSocialBlogComment where mId=:id")
    void deleteByBlogId(@Param(value="id")Long id);
}
