package com.kingnode.xsimple.dao.application;
import java.util.List;

import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.entity.application.KnApplicationInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * wangyfian
 */
@SuppressWarnings("ALL")
public interface KnApplicationInfoDao extends PagingAndSortingRepository<KnApplicationInfo, Long>, JpaSpecificationExecutor<KnApplicationInfo>{
    @Query("select u from KnApplicationInfo u where id in (select appId from KnRoleApplicationInfo where roleId=:rid)")
    List<KnApplicationInfo> findApplicationInfoByRid(@Param("rid") Long rid);



    /**
     *  根据应用名称查询应用集合
     * @param name  应用名称
     * @return 返回符合条件的应用集合
     */
    @Query("select u from KnApplicationInfo u where title=:name")
    List<KnApplicationInfo> findAppListByTitle(@Param("name") String name);

    List<KnApplicationInfo> findByTitleLike(String title);

    /***********************
     * 根据功能ID查找应用信息
     * @param functionId
     * @return
     */
    @Query("select u from KnApplicationInfo u where id in (select appId from KnFunctionAppInfo where functionId=:functionId)")
    List<KnApplicationInfo> findAppByFuncId(@Param("functionId") Long functionId);

    /**
     *  根据应用名称查询应用并返回分页集合
     * @param appName  应用名称
     * @return 返回符合条件的应用分页集合
     */
    @Query("select u from KnApplicationInfo u where title like:appName")
    Page<KnApplicationInfo> findAppListPageByTitle(@Param("appName") String appName,Pageable pager);
    /**
     *  根据应用标示查询应用并返回集合
     * @param app_key  应用标示
     * @return 返回符合条件的应用集合
     */
    @Query("select u from KnApplicationInfo u where apiKey =:app_key")
    List<KnApplicationInfo> findApplicationByAppkey(@Param("app_key")String app_key);

    /**
     *  获取除了当前应用标示的,且应用状态为可用应用集合
     * @param app_key  应用标示
     * @param workStatList  应用状态
     * @return 返回符合条件的应用集合
     */

    @Query("select u from KnApplicationInfo u where apiKey <>:app_key and workStatus in(:workStatList)")
    List<KnApplicationInfo> findApplicationByAppkeyAndStats(@Param("app_key") String app_key,@Param("workStatList") List<Setting.WorkStatusType> workStatList);

    /**
     *  新增或是休息版本时,修改下载地址以及验证方式
     * @param kid  应用id
     * @param down_url  下载地址
     * @param options_radios  验证方式 1无验证  2登录验证  3手机验证 4邀请码验证
     */
    @Modifying @Query("update KnApplicationInfo u set downLoadUrl=:down_url,optionsRadios=:options_radios where id =:kid ")
    void updateDownUrlAndOptionById(@Param("kid") Long kid,@Param("down_url")String down_url,@Param("options_radios")String options_radios);
}
