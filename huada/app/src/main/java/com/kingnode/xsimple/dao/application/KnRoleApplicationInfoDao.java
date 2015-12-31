package com.kingnode.xsimple.dao.application;
import java.util.List;

import com.kingnode.xsimple.entity.web.KnRoleApplicationInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author kongjiangwei
 */
@SuppressWarnings("ALL")
public interface KnRoleApplicationInfoDao extends CrudRepository<KnRoleApplicationInfo,Long>{
    @Modifying
    @Query("update KnRoleApplicationInfo set appId=:appId where roleId=:rid and appId=:oldAppId")
    int updateApp(@Param("rid") Long rid,@Param("appId") Long appId,@Param("oldAppId") Long oldAppId);

    @Modifying
    @Query("delete from KnRoleApplicationInfo where roleId=:rid and appId=:appId")
    int deleteRoleFromApp(@Param("rid") Long rid,@Param("appId") Long appId);
    List<KnRoleApplicationInfo> findRoleByAppId(@Param("appId") Long appId);
}
