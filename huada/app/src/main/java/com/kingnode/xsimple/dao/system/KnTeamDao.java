package com.kingnode.xsimple.dao.system;

import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnTeam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnTeamDao extends PagingAndSortingRepository<KnTeam, Long>, JpaSpecificationExecutor<KnTeam> {

    List<KnTeam> findByMaster(KnEmployee emp);
    /**
     * 对团队进行分页查询
     *
     * @param keyword
     * @param page
     * @return
     */
    @Query("select t from KnTeam t  where  t.name like :keyword ")
    Page<KnTeam> queryTeam(@Param("keyword") String keyword, Pageable page);

}