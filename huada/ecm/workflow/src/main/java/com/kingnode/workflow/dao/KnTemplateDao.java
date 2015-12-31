package com.kingnode.workflow.dao;
import java.util.List;

import com.kingnode.workflow.entity.KnTemplate;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnTemplateDao extends PagingAndSortingRepository<KnTemplate,Long>, JpaSpecificationExecutor<KnTemplate>{
    /**
     * 根据模板标题获取模板ID集合
     * @param title 模板标题
     * @return List<Long> 模板ID集合
     */
    @Query("select t.id from KnTemplate t where t.title like:title")
    List<Long> listTemplateIdsByName(@Param("title") String title);
}
