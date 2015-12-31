package com.kingnode.news.dao;
import java.util.List;

import com.kingnode.news.entity.KnWelfare;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnWelfareDao extends PagingAndSortingRepository<KnWelfare,Long>, JpaSpecificationExecutor<KnWelfare>{
    @Query("select u  from KnWelfare u  where u.effectiveTime is null or u.effectiveTime>?1 order by u.isTop desc, createTime desc ")
    Page<KnWelfare> findKnWelfare(Long effectiveTime,Pageable pageable);
    /**
     * 获取置顶工会福利信息，排除特定id的信息
     * @param
     * @return
     */
    @Query("select u  from KnWelfare u  where u.id<>?1 and u.isTop=true ")
    List<KnWelfare> findTopKnWelfare(Long id);

    /**
     * 查询工会福利，看是否是同一条
     * @return
     */
    @Query("select u  from KnWelfare u where  u.title=?1 and u.content=?2")
    List<KnWelfare> queryKnWelfare(String title,String content);
}
