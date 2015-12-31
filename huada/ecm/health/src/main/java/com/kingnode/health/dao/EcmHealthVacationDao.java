package com.kingnode.health.dao;
import java.util.List;

import com.kingnode.health.entity.KnEcmHealthVacation;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface EcmHealthVacationDao extends PagingAndSortingRepository<KnEcmHealthVacation,Long>, JpaSpecificationExecutor<KnEcmHealthVacation>{
    /**
     * 获取大于指定日期的请假对象集合
     * @param date 指定日期
     * @return List<KnEcmHealthVacation>
     */
    @Query("select h from KnEcmHealthVacation h where h.vacation>:date order by h.vacation asc")
    List<KnEcmHealthVacation> listKnEcmHealthVacation(@Param("date") Long date);
    /**
     * 获取等于指定日期的请假对象集合
     * @param date 指定日期
     * @return List<KnEcmHealthVacation>
     */
    @Query("select h from KnEcmHealthVacation h where h.vacation=:date")
    List<KnEcmHealthVacation> listKnEcmHealthVacationEqDate(@Param("date") Long date);
}