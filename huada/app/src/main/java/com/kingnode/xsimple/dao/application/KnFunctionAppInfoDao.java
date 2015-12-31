package com.kingnode.xsimple.dao.application;
import java.util.List;

import com.kingnode.xsimple.entity.web.KnFunctionAppInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnFunctionAppInfoDao extends CrudRepository<KnFunctionAppInfo, Long>{
    @Query("select u from KnFunctionAppInfo u where  appId in (:appIds)")
    List<KnFunctionAppInfo> findInfosInAppIds(@Param("appIds") List<Long> appIds);
    /**********************
     * 解除应用与功能之间的关系
     * @param appId
     * @param functionId
     * @return
     */
    @Modifying
    @Query("delete from KnFunctionAppInfo where appId=:appId and functionId=:functionId")
    int deleteByFunctionIdAndAppId(@Param("appId") Long appId,@Param("functionId") Long functionId);

    List<KnFunctionAppInfo> findByFunctionIdAndAppId(@Param("appId") Long appId,@Param("functionId") Long functionId);
}
