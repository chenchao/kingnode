package com.kingnode.social.dao;
import com.kingnode.social.entity.KnSocialBlogAt;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnSocialBlogAtDao extends PagingAndSortingRepository<KnSocialBlogAt,Long>, JpaSpecificationExecutor<KnSocialBlogAt>{
    @Modifying
    @Query("delete from KnSocialBlogAt where mId=:id")
    void deleteByBlogId(@Param(value="id")Long id);
}
