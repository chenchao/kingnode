package com.kingnode.social.dao;
import com.kingnode.social.entity.KnSocialBlogAttention;
import com.kingnode.xsimple.entity.system.KnEmployee;
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
public interface KnSocialBlogAttentionDao extends PagingAndSortingRepository<KnSocialBlogAttention,Long>, JpaSpecificationExecutor<KnSocialBlogAttention>{
    @Query("select count(u) from KnSocialBlogAttention u where u.focusUserId=:focusUserId")
    Integer findKnSocialBlogAttentionCountByFocusUserId(@Param(value="focusUserId") Long focusUserId);//查询粉丝数
    @Query("select count(u) from KnSocialBlogAttention u where u.userId=:userId")
    Integer findKnSocialBlogAttentionCountByUserId(@Param(value="userId") Long userId);//查询关注数
    KnSocialBlogAttention findByUserIdAndFocusUserId(Long userId,Long focusUserId);//查询当前用户是否关注过此用户
    @Query("select u from KnEmployee u,KnSocialBlogAttention s where u.id=s.userId and s.focusUserId=:focusUserId order by s.attentionTime desc")
    Page<KnEmployee> findKnEmployeeByFocusUserId(@Param(value="focusUserId") Long focusUserId,Pageable pageable);//查找用户的所有粉丝
    @Query("select u from KnEmployee u,KnSocialBlogAttention s where u.id=s.focusUserId and s.userId=:userId order by s.attentionTime desc")
    Page<KnEmployee> findKnEmployeeByUserId(@Param(value="userId") Long userId,Pageable pageable);//查找当前用户所关注的用户
    @Query("select u from KnEmployee u,KnSocialBlogAttention s where u.id=s.focusUserId and s.userId=:userId and u.userName like :userName order by s.attentionTime desc")
    Page<KnEmployee> findKnEmployeeByUserIdAndUserName(@Param(value="userId") Long userId,@Param(value="userName") String userName,Pageable pageable);//查找当前用户所关注的用户
}
