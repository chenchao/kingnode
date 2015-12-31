package com.kingnode.xsimple.dao.application;
import java.util.List;

import com.kingnode.xsimple.entity.application.KnApplicationSetupInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @wangyifan
 */
@SuppressWarnings("ALL")
public interface KnApplicationSetUpInfoDao extends PagingAndSortingRepository<KnApplicationSetupInfo, Long>, JpaSpecificationExecutor<KnApplicationSetupInfo>{


    /**
     *  根据应用版本名称查询应用集合
     * @param name  应用版本名称
     * @return 返回符合条件的应用版本集合
     */
    @Query("select u from KnApplicationSetupInfo u where title=:name")
    List<KnApplicationSetupInfo> findVersionListByTitle(@Param("name") String name);



    /**
     *  根据应用ID查询应用设置信息并返回分页集合
     * @param name  应用名称
     * @return 返回符合条件的应用分页集合
     */
    @Query("select u from KnApplicationSetupInfo u where applicationInfo.id=:appId")
    Page<KnApplicationSetupInfo> findAppSetUpListPageByAppTitle(@Param("appId") Long appId,Pageable pager);

    /**
     *  根据应用apiKey查询应用设置信息
     * @param apiKey
     * @return 返回符合条件的应用分页集合
     */
    @Query("select u from KnApplicationSetupInfo u where applicationInfo.apiKey=:apiKey")
    List<KnApplicationSetupInfo> findAppSetUpListPageByAppApiKey(@Param("apiKey") String apiKey);

    /**
     * 获取该应用下的应用设置信息
     * @param appName  应用名称
     * @return List<KnApplicationSetupInfo> 返回符合条件的应用设置集合列表
     */
    @Query("select u from KnApplicationSetupInfo u where applicationInfo.id in (select  id from KnApplicationInfo where title like:appName)")
    List<KnApplicationSetupInfo> findVerListByAppIdAndWorkStats(@Param("appName") String appName);

    /**
     * 获取该应用下所有的设置信息
     * @param appId  应用id
     * @return List<KnApplicationSetupInfo> 返回符合条件的集合列表
     */
    @Query("select u from KnApplicationSetupInfo u where u.applicationInfo.id=:appId")
    List<KnApplicationSetupInfo> findListByAppId(@Param("appId") Long appId);

    /**
     * 获取该应用下所有设置信息
     * @param ids  应用id集合
     * @return List<KnApplicationSetupInfo> 返回符合条件的集合列表
     */
    @Query("select u from KnApplicationSetupInfo u where u.applicationInfo.id in(:ids)")
    List<KnApplicationSetupInfo> findListByAppIds(@Param("ids")List<Long> ids);
}
