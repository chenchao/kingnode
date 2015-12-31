package com.kingnode.health.dao;
import java.util.List;

import com.kingnode.health.entity.KnEcmHealthNumber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL") public interface EcmHealthNumberDao extends PagingAndSortingRepository<KnEcmHealthNumber,Long>, JpaSpecificationExecutor<KnEcmHealthNumber>{
    /**
     * 获取大于指定日期的排号对象集合
     * @param date 指定日期
     * @return List<KnEcmHealthNumber>
     */
    @Query("select h from KnEcmHealthNumber h where h.start>:date and h.active='ENABLE' order by h.start asc")
    List<KnEcmHealthNumber> DayOfArrangeList(@Param("date") long date);
    /**
     * 获取大于指定日期的排号日的分组集合
     * @param date 指定日期
     * @return List<String>
     */
    @Query("select h.day from KnEcmHealthNumber h where h.start>:date group by h.day order by h.start asc")
    List<String> QueryDayList(@Param("date") long date);
    /**
     * 获取指定日期的排号对象的最大号码
     * @param day 指定日期
     * @return int
     */
    @Query("select max(h.number) from KnEcmHealthNumber h where h.day=:day group by h.day order by h.start asc")
    int MaxNumberWithDay(@Param("day") String day);
    /**
     * 获取常规排号下的有效排号对象集合并分页
     * @param pageRequest 分页信息
     * @return Page<KnEcmHealthNumber>
     */
    @Query("select h from KnEcmHealthNumber h where h.active='ENABLE' and h.type=1")
    Page<KnEcmHealthNumber> PageKnEcmHealthNumber(Pageable pageRequest);
    /**
     * 根据预约对象的ID获取其对应的排号对象
     * @param healthId 预约对象ID
     * @return KnEcmHealthNumber
     */
    @Query("select h from KnEcmHealthNumber h where h.healthId=:healthId")
    KnEcmHealthNumber findOneByHealthId(@Param("healthId") Long healthId);
}