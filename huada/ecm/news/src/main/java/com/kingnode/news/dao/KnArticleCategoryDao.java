package com.kingnode.news.dao;
import java.util.List;

import com.kingnode.news.entity.KnArticleCategory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public interface KnArticleCategoryDao extends PagingAndSortingRepository<KnArticleCategory,Long>, JpaSpecificationExecutor<KnArticleCategory>{
    public List<KnArticleCategory> findByIdNot(Long id);
}
