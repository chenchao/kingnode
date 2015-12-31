package com.kingnode.xsimple.dao.application;
import java.util.List;
import javax.persistence.QueryHint;

import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.entity.application.KnVersionInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @wangyifan
 */
@SuppressWarnings("ALL")
public interface KnVersionInfoDao extends PagingAndSortingRepository<KnVersionInfo,Long>, JpaSpecificationExecutor<KnVersionInfo>{
    /**
     * 根据应用版本名称查询应用集合
     *
     * @param name 应用版本名称
     *
     * @return 返回符合条件的应用版本集合
     */
    @Query("select u from KnVersionInfo u where title=:name") List<KnVersionInfo> findVersionListByTitle(@Param("name") String name);
    /**
     * 根据应用名称查询应用并返回分页集合
     *
     * @param appId 应用ID
     *
     * @return 返回符合条件的应用分页集合
     */
    @Query("select u from KnVersionInfo u where applicationInfo.id=:appId") Page<KnVersionInfo> findVersionListPageByTitle(@Param("appId") Long appId,Pageable pager);
    /**
     * 获取该应用地下的版本信息服务器中版本号最高的版本信息
     *
     * @param appId    应用ID
     * @param workStat 版本状态
     *
     * @return List<KnVersion> 返回符合条件的版本集合列表
     */
    @Query("select u from KnVersionInfo u where applicationInfo.id=:appId and workStatus in (:workStatList)")
    List<KnVersionInfo> findVerListByAppIdAndWorkStats(@Param("appId") long appId,@Param("workStatList") List<Setting.WorkStatusType> workStatList);
    /**
     * 获取所有指定类型的版本信息
     *
     * @param workStatList
     *
     * @return
     */
    @Query("select u from KnVersionInfo u where workStatus in (:workStatList) order by applicationInfo.title")
    List<KnVersionInfo> findVerListByWorkStats(@Param("workStatList") List<Setting.WorkStatusType> workStatList);
    /**
     * 获取该应用地下的版本信息服务器中版本号最高的版本信息
     *
     * @param app_key      应用标示
     * @param plate_form   设备平台
     * @param workStatList 版本状态
     *
     * @return List<KnVersion> 返回符合条件的版本集合列表
     */
    @Query("select u from KnVersionInfo u where applicationInfo.apiKey=:app_key and u.type =:plate_form and  workStatus in (:workStatList)")
    List<KnVersionInfo> findVerListByAppkeyAndWorkStatsAndPlat(@Param("app_key") String app_key,@Param("plate_form") Setting.VersionType plate_form,@Param("workStatList") List<Setting.WorkStatusType> workStatList);
    /**
     * 获取该应用地下的版本信息服务器中版本号最高的版本信息
     *
     * @param app_key      应用标示
     * @param typeList     版本运行平台
     * @param workStatList 版本状态
     *
     * @return List<KnVersion> 返回符合条件的版本集合列表
     */
    @Query("select u from KnVersionInfo u where applicationInfo.apiKey=:app_key and u.type in (:typeList) and workStatus in (:workStatList)")
    List<KnVersionInfo> findVerListByAppkeyAndVerType(@Param("app_key") String app_key,@Param("typeList") List<Setting.VersionType> typeList,@Param("workStatList") List<Setting.WorkStatusType> workStatList);
    /**
     * 获取该应用下所有版本信息
     *
     * @param appId 应用id
     *
     * @return List<KnVersion> 返回符合条件的版本集合列表
     */
    @Query("select u from KnVersionInfo u where u.applicationInfo.id=:appId") List<KnVersionInfo> findVerListByAppId(@Param("appId") Long appId);
    /**
     * 获取该应用下所有版本信息
     *
     * @param ids 应用id集合
     *
     * @return List<KnVersion> 返回符合条件的版本集合列表
     */
    @Query("select u from KnVersionInfo u where u.applicationInfo.id in(:ids)") List<KnVersionInfo> findVerListByAppIds(@Param("ids") List<Long> ids);
    /**
     * 查询所有列表,加入缓存
     *
     * @param spec
     * @param pageable
     *
     * @return
     */
    @QueryHints({@QueryHint(name="org.hibernate.cacheable", value="true")}) List<KnVersionInfo> findAll();
}
