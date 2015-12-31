package com.kingnode.third.dao;

import java.util.List;

import com.kingnode.third.entity.KnUserThirdInfo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author kongjiangwei
 */
@SuppressWarnings("ALL")
public interface KnUserThirdInfoDao extends PagingAndSortingRepository<KnUserThirdInfo, Long>, JpaSpecificationExecutor<KnUserThirdInfo> {
    @Query("select u from KnUserThirdInfo u where fromSys in (:fromSysList)")
    List<KnUserThirdInfo> findInFromSys(@Param("fromSysList") List<String> fromSysList);

    @Query("select u from KnUserThirdInfo u where fromSys=:fromSys")
    List<KnUserThirdInfo> findByFromSys(@Param("fromSys") String fromSys);

    @Query("select u from KnUserThirdInfo u where fromSys=:fromSys and markName <> :markName")
    List<KnUserThirdInfo> findByFromSysAndNotMarkName(@Param("fromSys") String fromSys, @Param("markName") String markName);

    @Modifying
    @Query("update from KnUserThirdInfo set markName=:markName where id in (:ids)")
    int updateMarkName(@Param("markName") String markName, @Param("ids") List<Long> ids);

    @Modifying
    @Query("delete from KnUserThirdInfo where fromSys=:fromSys")
    int deleteUserInfo(@Param("fromSys") String fromSys);

    List<KnUserThirdInfo> findByMarkName(@Param("markName") String markName);
}
