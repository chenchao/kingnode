package com.kingnode.news.dao;
import java.util.List;

import com.kingnode.news.entity.KnArticle;
import com.kingnode.news.entity.KnNotice;
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
public interface KnArticleDao extends PagingAndSortingRepository<KnArticle,Long>, JpaSpecificationExecutor<KnArticle>{
    /**
     * 查询新闻列表
     * @param userId 人员ID
     * @param page
     * @return
     */
    @Query("select u  from KnArticle u where (effectiveTime is null or effectiveTime >= :curDate) order by createTime desc,isTop desc ")
    Page<KnArticle> queryKnArticle(@Param("curDate")Long curDate,Pageable page);

    /**
     * 查询指定日期的头条新闻
     * @param userId 人员ID
     * @param page
     * @return
     */
    @Query("select u  from KnArticle u where  pubDate=?1 and isTop=true order by pubDate desc")
    List<KnArticle> queryTopKnArticle(Long pubDate);


    /**
     * 查询指定日期的头条新闻
     * @param userId 人员ID
     * @param page
     * @return
     */
    @Query("select u  from KnArticle u where  isTop=true order by pubDate desc")
    List<KnArticle> queryTopKnArticle();

    /**
     * 查询新闻标题和内容，看是否是同一条新闻
     * @return
     */
    @Query("select u  from KnArticle u where  u.title=?1 and u.content=?2")
    List<KnArticle> queryKnArticle(String title,String content);

}