package com.kingnode.news.dao;
import java.util.List;

import com.kingnode.news.entity.KnArticleCategory;
import com.kingnode.news.entity.KnArticleComment;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public interface KnArticleCommentDao extends PagingAndSortingRepository<KnArticleComment,Long>, JpaSpecificationExecutor<KnArticleComment>{
    List<KnArticleComment> findByPid(Long pid);
}
