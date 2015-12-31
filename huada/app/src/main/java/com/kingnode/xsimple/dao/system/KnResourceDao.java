package com.kingnode.xsimple.dao.system;
import java.util.List;
import javax.persistence.QueryHint;

import com.kingnode.xsimple.entity.system.KnResource;
import com.kingnode.xsimple.entity.system.KnResource.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnResourceDao extends PagingAndSortingRepository<KnResource,Long>, JpaSpecificationExecutor<KnResource>{
    //-----经过整理的方法
    @QueryHints({@QueryHint(name="org.hibernate.cacheable", value="true")}) List<KnResource> findByPathLike(String path);
    List<KnResource> findByTypeNotAndTypeNot(ResourceType function,ResourceType module,Sort sort);
    @Query("select distinct b from KnRole a join a.res b where a.id in(select d.id from KnUser c join c.role d where c.id=?1) and b.type=?2 and b.active=?3") @QueryHints({@QueryHint(name="org.hibernate.cacheable", value="true")}) List<KnResource> findResByIdIn(Long id,ResourceType rt,KnResource.ActiveType at);
    //---下面代码可能随时会删除
    /**
     * 通过supId字段查找子孙资源
     *
     * @param supId
     *
     * @return
     */
    List<KnResource> findBySupId(Long supId);
    /**
     * 根据资源类型获取资源的列表信息
     *
     * @param type 资源类型
     *
     * @return 资源的列表信息
     */
    @Query("select u from KnResource u where type=:type") List<KnResource> findByResourceType(@Param("type") KnResource.ResourceType type);
    /**
     * **************
     * 根据角色与模块查询功能列表
     *
     * @param rid
     * @param mid
     *
     * @return
     */
    @Query(value="select u from KnResource u,KnRoleModuleFunctionInfo k where u.id = k.functionId and k.roleId=:rid and k.moduleId=:mid order by k.mfSort")
    List<KnResource> findResourceByRidAndMid(@Param("rid") Long rid,@Param("mid") Long mid);
    @Query("select u from KnResource u where type=:type and id  in (select distinct moduleId from KnRoleModuleFunctionInfo where roleId in (select roleId from KnRoleApplicationInfo where appId = :appId))")
    Page<KnResource> findResourceByAppIdList(@Param("appId") Long appId,@Param("type") KnResource.ResourceType type,Pageable pageable);
    @Query("select u from KnResource u where type=:type and id  in (select distinct functionId from KnRoleModuleFunctionInfo where roleId in (select roleId from KnRoleApplicationInfo where appId = :appId))")
    List<KnResource> findFunctionByAppIdList(@Param("appId") Long appId,@Param("type") KnResource.ResourceType type);
    @Query("select u from KnResource u where type=:type and  id not in (select moduleId from KnRoleModuleFunctionInfo where roleId  in (select roleId from KnRoleApplicationInfo))")
    Page<KnResource> findModuleNotInAppList(@Param("type") KnResource.ResourceType type,Pageable pageable);
    @Query("select u from KnResource u where type=:type and  id not in (select functionId from KnRoleModuleFunctionInfo where roleId  in (select roleId from KnRoleApplicationInfo))")
    List<KnResource> findFunctionNotInAppList(@Param("type") KnResource.ResourceType type);
    @Query("select u from KnResource u where type=:type and  id in (select functionId from KnRoleModuleFunctionInfo where roleId in (:roleIds) and moduleId=:moduleId and functionId is not null )")
    List<KnResource> findFunctionByRoleIdsAndModuleId(@Param("roleIds") List<Long> roleIds,@Param("moduleId") Long moduleId,@Param("type") KnResource.ResourceType type);
    @Query("select u from KnResource u where type=:type") Page<KnResource> findResourceList(@Param("type") KnResource.ResourceType type,Pageable pageable);
    @Query("select u from KnResource u,KnRoleModuleFunctionInfo k where u.type=:type and u.id = k.moduleId and k.roleId=:roleId and k.functionId=null order by k.rmSort")
    List<KnResource> findModuleByRoleId(@Param("roleId") Long roleId,@Param("type") KnResource.ResourceType type);
    @Query("select u from KnResource u where type=:type and id not in (select distinct moduleId from KnRoleModuleFunctionInfo where roleId=:roleId)")
    List<KnResource> findModuleNotInRole(@Param("roleId") Long roleId,@Param("type") KnResource.ResourceType type);
    @Query("select u from KnResource u where type=:type and id in (select distinct moduleId from KnRoleModuleFunctionInfo where roleId=:roleId)")
    Page<KnResource> findPageModuleByRoleId(@Param("roleId") Long roleId,@Param("type") KnResource.ResourceType type,Pageable pageable);
    @Query("select u from KnResource u where type=:type and id not in (select distinct moduleId from KnRoleModuleFunctionInfo where roleId=:roleId and functionId is null)")
    Page<KnResource> findPageModuleNotInRid(@Param("roleId") Long roleId,@Param("type") KnResource.ResourceType type,Pageable pageable);
    @Query("select u from KnResource u where type=:type and id in (select distinct moduleId from KnRoleModuleFunctionInfo where roleId=:roleId and functionId=:functionId)")
    List<KnResource> findModuleByRoleIdAndFunctionId(@Param("roleId") Long roleId,@Param("functionId") Long functionId,@Param("type") KnResource.ResourceType type);
    @Query("select u from KnResource u where active=:activeType and id in (:ids)")
    List<KnResource> findSetUpPackageByIds(@Param("ids") List<Long> ids,@Param("activeType") KnResource.ActiveType activeType);
    @Query("select u from KnResource u where type=:type and id in (select distinct moduleId from KnRoleModuleFunctionInfo where roleId in (:roleIds))")
    List<KnResource> findModuleByRoleIds(@Param("roleIds") List<Long> roleIds,@Param("type") KnResource.ResourceType type);
    @Query("select u from KnResource u where type=:type and id in (select distinct functionId from KnRoleModuleFunctionInfo where roleId in (:roleIds))")
    List<KnResource> findFunctionByRoleIds(@Param("roleIds") List<Long> roleIds,@Param("type") KnResource.ResourceType type);
}