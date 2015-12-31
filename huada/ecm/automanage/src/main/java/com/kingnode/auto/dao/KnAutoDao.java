package com.kingnode.auto.dao;
import java.util.List;

import com.kingnode.auto.entity.KnAuto;
import com.kingnode.auto.entity.KnAutoBorrow;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * Created by xushuangyong on 14-9-18.
 */
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnAutoDao extends PagingAndSortingRepository<KnAuto,Long>, JpaSpecificationExecutor<KnAuto>{
    /**
     * 查询指定时间段,可以预定的车辆. 车子在库存满足条件,当车子被其他人申请,但是与自己时间不冲突满足满足
     *
     * @param fromtime
     * @param endtime
     *
     * @return
     */
    @Query("select auto from KnAuto  auto where auto.autoState in ('LEND','RESTORE') and not exists (select borrow.ka.id from KnAutoBorrow borrow where  borrow.ka.id=auto.id and borrow.bt in (:bts) and ((borrow.lendDate<=:fromtime and borrow.restoreDate>=:fromtime) or (borrow.lendDate<=:endtime and borrow.restoreDate>=:endtime)))  order by auto.updateTime desc")
    List<KnAuto> queryCanReserveList(@Param("fromtime") Long fromtime,@Param("endtime") Long endtime,@Param("bts") List<KnAutoBorrow.BorrowType> bts);
    /**
     * 查询该车辆是否可以被该用户预定 .车子在库存满足条件,当车子被其他人申请,但是与自己时间不冲突满足满足
     *
     * @param fromtime
     * @param endtime
     * @param id       汽车id
     *
     * @return
     */
    //@Query("select auto from KnAuto  auto where  auto.id=:id and auto.autoState in ('LEND','RESTORE') and not exists (select borrow.ka.id from KnAutoBorrow borrow where  borrow.ka.id=auto.id and borrow.bt in (:bts) and ((borrow.lendDate<=:fromtime and borrow.restoreDate>=:fromtime) or (borrow.lendDate<=:endtime and borrow.restoreDate>=:endtime)))")
    @Query("select auto from KnAuto  auto where  auto.id=:id and exists (select borrow.ka.id from KnAutoBorrow borrow where  borrow.ka.id=auto.id and borrow.bt in (:bts) and ((borrow.lendDate<:fromtime and borrow.restoreDate>:fromtime) or (borrow.lendDate<:endtime and borrow.restoreDate>:endtime) or" +
            " (borrow.lendDate>=:fromtime and borrow.restoreDate<=:endtime) ))")
    List<KnAuto> queryCanReserveAuto(@Param("fromtime") Long fromtime,@Param("endtime") Long endtime,@Param("id") Long id,@Param("bts") List<KnAutoBorrow.BorrowType> bts);
    @Query("select auto from KnAuto  auto where auto.transmission  in (:transmission)")
    List<KnAuto> QueryByTransmission(@Param("transmission") List<KnAuto.Transmission> transmissions);

}
