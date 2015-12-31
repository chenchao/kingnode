package com.kingnode.social.dao;
import com.kingnode.social.entity.KnSocialBlogSettings;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnSocialBlogSettingsDao extends PagingAndSortingRepository<KnSocialBlogSettings,Long>, JpaSpecificationExecutor<KnSocialBlogSettings>{
    KnSocialBlogSettings findByType(KnSocialBlogSettings.EmailType type);
}
