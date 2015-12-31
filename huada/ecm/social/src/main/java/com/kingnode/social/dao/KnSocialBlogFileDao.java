package com.kingnode.social.dao;
import com.kingnode.social.entity.KnSocialBlogFile;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnSocialBlogFileDao extends PagingAndSortingRepository<KnSocialBlogFile,Long>, JpaSpecificationExecutor<KnSocialBlogFile>{
@Modifying
    @Query("delete from KnSocialBlogFile u where u.ksb.id=:id")
    void deleteByBlogId(@Param(value="id")Long id);
}
