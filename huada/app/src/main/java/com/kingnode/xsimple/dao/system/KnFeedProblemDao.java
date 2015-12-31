package com.kingnode.xsimple.dao.system;
import java.util.List;

import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.entity.system.KnFeedProblem;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * cici
 */
@SuppressWarnings("ALL")
public interface KnFeedProblemDao extends PagingAndSortingRepository<KnFeedProblem,Long>, JpaSpecificationExecutor<KnFeedProblem>{


    @Query("select u from KnFeedProblem u where u.feedProblenType=:feedProblenType")
    List<KnFeedProblem> findListByType(@Param("feedProblenType") Setting.FeedProblenType feedProblenType);
}
