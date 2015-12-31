package com.kingnode.auto.dao;
import java.util.List;

import com.kingnode.auto.entity.KnAutoBorrow;
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
public interface KnAutoBorrowDao extends PagingAndSortingRepository<KnAutoBorrow,Long>, JpaSpecificationExecutor<KnAutoBorrow>{
    @Query(value="select ab  from KnAutoBorrow ab  where  ab.lendDate < :time and ab.bt in ('APPLY','APPROVAL')")
    List<KnAutoBorrow> queryOverTimeAutoBorrow(@Param("time") Long time);
     @Query(value="select ab  from KnAutoBorrow ab  where  ab.lendDate <= :time and ab.lendDate>:curtime and ab.bt in ('APPROVAL')")
    //@Query(value="select ab  from KnAutoBorrow ab  where  ab.lendDate <= :time and ab.lendDate>:curtime and ab.bt in ('APPROVAL')")
    List<KnAutoBorrow> queryLMAutoBorrow(@Param("time") Long time,@Param("curtime") Long curtime);


    /**
     * 根据登录用户名 查询用户信息列表
     *
     * @param name 登录用户名
     *
     * @return 符合条件的用户信息
     */
    @Query("select ab from KnAutoBorrow ab where ab.userId=:userId and ab.bt in  (:stateList)  order by ab.updateTime desc")
    Page<KnAutoBorrow> PageKnAutoBorrow(@Param("userId") Long userId,@Param("stateList") List<KnAutoBorrow.BorrowType> stateList,Pageable page);
    @Query("select ab from KnAutoBorrow ab where ab.userId=:userId order by ab.updateTime desc ") Page<KnAutoBorrow>
    PageKnAutoBorrow(@Param("userId") Long userId,Pageable page);
    @Query("select count(ab.userId) from KnAutoBorrow ab where ab.userId=:userId ") Long queryCount(@Param("userId") Long userId);
    /**
     * 查询审批通过得指定车辆的预定情况
     * @param time
     * @return
     */
    @Query(value="select ab  from KnAutoBorrow ab  where  ab.bt in ('APPROVAL','APPLY','LEND') and ab.ka.id=?1 and ((ab.lendDate>=?2 and ab.lendDate<?3) or (ab.restoreDate>=?2 and ab.restoreDate<?3) or (ab.lendDate<=?2 and ab.restoreDate>=?2))")
//    @Query(value="select ab  from KnAutoBorrow ab  where   ab.ka.id=?1 and ((ab.lendDate>=?2 and ab.lendDate<?3) or (ab.restoreDate>=?2 and ab.restoreDate<?3) or (ab.lendDate<=?2 and ab.restoreDate>=?2))")
    List<KnAutoBorrow> queryApprovelAutoBorrow(Long autoId,Long startToday,Long endToday);
}
