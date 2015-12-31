package com.kingnode.health.dao;
import java.util.List;

import com.kingnode.health.entity.KnEcmHealth;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL") public interface EcmHealthDao extends PagingAndSortingRepository<KnEcmHealth,Long>, JpaSpecificationExecutor<KnEcmHealth>{
    /**
     * 根据用户ID获取所有预约集合
     *
     * @param userId      用户ID
     * @param pageRequest 分页信息
     *
     * @return Page<KnEcmHealth> 分页处理后的预约集合
     */
    @Query("select h from KnEcmHealth h where h.employee.id=:id order by h.proposerDate desc") Page<KnEcmHealth> PageKnEcmHealth(@Param("id") Long id,Pageable pageRequest);
    /**
     * 根据ID集合获取状态为已申请的预约集合
     *
     * @param ids 预约ID集合
     *
     * @return List<KnEcmHealth>
     */
    @Query("select h from KnEcmHealth h where h.id in(:ids) and h.status='PROPOSER' order by h.proposerDate asc") List<KnEcmHealth> ListHealthByIds(@Param("ids") List<Long> ids);
    /**
     * 根据时间获取预约列表
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     *
     * @return List<KnEcmHealth>
     */
    @Query("select h from KnEcmHealth h where h.status='WAITING' and h.waitingDate>=:startDate and h.waitingDate<:endDate order by h.waitingDate desc")
    List<KnEcmHealth> ListHealthByDate(@Param("startDate") Long startDate,@Param("endDate") Long endDate);
}