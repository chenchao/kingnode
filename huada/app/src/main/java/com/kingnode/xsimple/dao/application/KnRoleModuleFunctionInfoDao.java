package com.kingnode.xsimple.dao.application;
import java.util.List;

import com.kingnode.xsimple.entity.web.KnRoleModuleFunctionInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author kongjiangwei
 */
@SuppressWarnings("ALL")
public interface KnRoleModuleFunctionInfoDao extends CrudRepository<KnRoleModuleFunctionInfo,Long>{
    //通过角色ＩＤ，模块ID，功能ＩＤ删除角色模块功能信息表信息
    @Modifying
    @Query("delete from KnRoleModuleFunctionInfo where roleId=:roleId and moduleId=:moduleId and functionId=:functionId")
    int deleteRoleModuleFunctionInfo(@Param("roleId") Long roleId,@Param("moduleId") Long moduleId,@Param("functionId") Long functionId);

    @Query("select u from  KnRoleModuleFunctionInfo u where roleId is not null and moduleId is not null and functionId is null")
    List<KnRoleModuleFunctionInfo> getRMInfoOnlyByRoleAndModule();

    @Query("select u from  KnRoleModuleFunctionInfo u where roleId in (:roleIds) and moduleId is not null and functionId is null")
    List<KnRoleModuleFunctionInfo> getRMInfoOnlyByRoles(@Param("roleIds") List<Long> roleIds);

    @Modifying
    @Query("delete from KnRoleModuleFunctionInfo where roleId=:roleId and moduleId=:moduleId and functionId is null")
    int deleteRoleModuleFunctionInfo(@Param("roleId") Long roleId,@Param("moduleId") Long moduleId);

    @Modifying
    @Query("update KnRoleModuleFunctionInfo set roleId=:roleId where roleId=:oldRid and moduleId=:moduleId and functionId is null")
    int updateRoleModuleInfo(@Param("roleId") Long roleId,@Param("moduleId") Long moduleId,@Param("oldRid") Long oldRid);

    List<KnRoleModuleFunctionInfo> findByRoleId(@Param("roleId") Long roleId);

    @Modifying
    @Query("delete from KnRoleModuleFunctionInfo where roleId=:roleId and functionId=:functionId and moduleId is not null")
    int deleteRMFInfoByRidAndFid(@Param("roleId") Long roleId,@Param("functionId") Long functionId);

    @Query("select u from  KnRoleModuleFunctionInfo u where roleId=:roleId and moduleId=:moduleId and functionId=:functionId")
    List<KnRoleModuleFunctionInfo> findByRoleIdAndMoludeIdAndFunctionId(@Param("roleId") Long roleId,@Param("moduleId") Long moduleId,@Param("functionId") Long functionId);

    @Query("select u from  KnRoleModuleFunctionInfo u where roleId=:roleId and moduleId=:moduleId and functionId is not null")
    List<KnRoleModuleFunctionInfo> findByRoleIdAndMoludeId(@Param("roleId") Long roleId,@Param("moduleId") Long moduleId);

    @Query("select u from  KnRoleModuleFunctionInfo u where roleId in (:roleIds) and moduleId=:moduleId and functionId is not null")
    List<KnRoleModuleFunctionInfo> findByRoleIdsAndMoludeId(@Param("roleIds") List<Long> roleIds,@Param("moduleId") Long moduleId);
}
