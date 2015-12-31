package com.kingnode.third.dao;

import com.kingnode.third.entity.KnPsUserContactInfo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnPsUserContactInfoDao extends PagingAndSortingRepository<KnPsUserContactInfo, Long>, JpaSpecificationExecutor<KnPsUserContactInfo> {

    @Modifying
    @Query("delete from KnPsUserContactInfo where createrId=:createrId and createrFrom=:createrFrom and contactId=:contactId and contactFrom=:contactFrom")
    int deleteInfoByParms(@Param("createrId") String createrId, @Param("createrFrom") String createrFrom, @Param("contactId") String contactId, @Param("contactFrom") String contactFrom);
}