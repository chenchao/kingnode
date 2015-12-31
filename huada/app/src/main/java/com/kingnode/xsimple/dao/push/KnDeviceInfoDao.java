package com.kingnode.xsimple.dao.push;
import java.util.List;

import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.entity.push.KnDeviceInfo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnDeviceInfoDao extends PagingAndSortingRepository<KnDeviceInfo,Long>, JpaSpecificationExecutor<KnDeviceInfo>{
    /**
     * 获取该应用下,版本的状态以及型号对应的所有的设备信息
     *
     * @param appId        应用ID
     * @param workStatList 版本的状态
     * @param Dtype        设备的型号 IOS ANDROID
     *
     * @return List<KnDeviceInfo> 返回符合条件的设备集合列表
     */
    @Query("select u from KnDeviceInfo u where u.appId=:appId and u.deviceType=:Dtype and u.workStatus in (:workStatList)")
    List<KnDeviceInfo> findChanneListByAppAndWork(@Param("appId") long appId,@Param("workStatList") List<Setting.WorkStatusType> workStatList,@Param("Dtype") Setting.VersionType Dtype);
    @Query("select u from KnDeviceInfo u where delState in (:delStatList) order by deviceType ")
    List<KnDeviceInfo> findDeviceByStats(@Param("delStatList") List<Setting.DeleteStatusType> delStatList);
    @Modifying @Query("update KnDeviceInfo u set delState=:delStateType where id in (:chanIdsList) ")
    int updateChannState(@Param("delStateType") Setting.DeleteStatusType delStateType,@Param("chanIdsList") List<Long> chanIdsList);
    @Modifying @Query("update KnDeviceInfo u set delState=:delStateType where deviceToken in (:deviceTokenList) ")
    int updateChannDeivceState(@Param("delStateType") Setting.DeleteStatusType delStateType,@Param("deviceTokenList") List<String> deviceTokenList);
    /**
     * 根据应用标示 用户id 来自系统 设备标示查询设备信息
     *
     * @param user_id  用户id
     * @param user_sys 来自系统
     * @param app_key  应用标示
     * @param totken   设备标示
     *
     * @return 返回符合条件的设备集合列表
     */
    @Query("select u from KnDeviceInfo u where u.apiKey=:app_key and u.deviceToken=:totken and u.userId=:user_id and u.userSystem=:user_sys")
    List<KnDeviceInfo> findByUsrIdAndFormSys(@Param("user_id") String user_id,@Param("user_sys") String user_sys,@Param("app_key") String app_key,@Param("totken") String totken);
    /**
     * 根据应用标示  用户id 来自系统 设备标示 以及在线状态 查询设备信息
     *
     * @param user_id        用户id
     * @param user_sys       来自系统
     * @param app_key        应用标示
     * @param totken         设备标示
     * @param onlineStatName 在线状态
     *
     * @return 返回符合条件的设备集合列表
     */
    @Query("select u from KnDeviceInfo u where u.apiKey=:app_key and u.deviceToken <>:totken and u.onlineStat=:onlineStatName and u.userId=:user_id and u.userSystem=:user_sys")
    List<KnDeviceInfo> findByAppkeyAndStat(@Param("user_id") String user_id,@Param("user_sys") String user_sys,@Param("app_key") String app_key,@Param("totken") String totken,@Param("onlineStatName") String onlineStatName);
    /**
     * 根据 设备标示 以及主键ID 查询设备信息
     *
     * @param chanId 主键ID
     * @param totken 设备标示
     *
     * @return 返回符合条件的设备集合列表
     */
    @Query("select u from KnDeviceInfo u where u.id<>:chanId and u.deviceToken =:totken")
    List<KnDeviceInfo> findChanneListByTotkenAndId(@Param("totken") String totken,@Param("chanId") Long chanId);
    /**
     * 根据 idsList 设置是否在线
     *
     * @param onlineStatName 在线状态
     * @param idsList      需要设置下线的设备ids集合
     *
     * @return 返回符合条件的设备集合列表
     */
    @Modifying @Query("update KnDeviceInfo u set u.onlineStat=:onlineStatName where u.id in (:idsList) ")
    int updateChannelStat(@Param("onlineStatName") String onlineStatName,@Param("idsList") List<Long> idsList);
    /**
     * 根据 设备标示设置设备是否在线
     *
     * @param onlineStatName 在线状态
     * @param totken         设备标示
     * @param plateform      设备来自平台
     * @param user_id        用户id
     * @param user_sys       来自系统
     *
     * @return 返回符合条件的设备集合列表
     */
    @Modifying @Query("update KnDeviceInfo  set onlineStat=:onlineStatName where deviceToken =:totken and deviceType =:plateform and userId=:user_id and userSystem=:user_sys ")
    int updateKnChannelStat(@Param("onlineStatName") String onlineStatName,@Param("totken") String totken,@Param("plateform") Setting.VersionType plateform,@Param("user_id") String user_id,@Param("user_sys") String user_sys);
   /* @Query("select u from KnDeviceInfo u where onlineStat=:onlineStat ")
    List<KnDeviceInfo> findLineChann(@Param("onlineStat")String onlineStat);*/
    /**
     * 根据应用标示  用户id 来自系统 设备标示 以及在线状态 查询设备信息
     *
     * @param user_id        用户id
     * @param user_sys       来自系统
     * @param app_key        应用标示
     * @param totken         设备标示
     * @param onlineStatName 在线状态
     *
     * @return 返回符合条件的设备集合列表
     *
     * @parm updateTime  设备最后登录的时间
     */
    @Query("select u from KnDeviceInfo u where u.apiKey=:app_key and u.deviceToken <>:totken and u.onlineStat=:onlineStatName and u.userId=:user_id and u.userSystem=:user_sys and u.updateTime >:update_Time")
    List<KnDeviceInfo> findByAppkeyAndStatAndTime(@Param("user_id") String user_id,@Param("user_sys") String user_sys,@Param("app_key") String app_key,@Param("totken") String totken,@Param("onlineStatName") String onlineStatName,@Param("update_Time") Long update_Time);
    /**
     * 获取该应用下所有的设备信息
     *
     * @param appId 应用id
     *
     * @return List<KnDeviceInfo> 返回符合条件的集合列表
     */
    @Query("select u from KnDeviceInfo u where u.appId=:appId") List<KnDeviceInfo> findListByAppId(@Param("appId") Long appId);
    /**
     * 获取该应用下所有设备信息
     *
     * @param ids 应用id集合
     *
     * @return List<KnDeviceInfo> 返回符合条件的集合列表
     */
    @Query("select u from KnDeviceInfo u where u.appId in(:ids)") List<KnDeviceInfo> findListByAppIds(@Param("ids") List<Long> ids);
    /**
     * 获取该设备下所有的设备信息
     *
     * @param totken 设备标示
     *
     * @return List<KnDeviceInfo> 返回符合条件的集合列表
     */
    @Query("select u from KnDeviceInfo u where u.deviceToken =:totken") List<KnDeviceInfo> findListByTotken(@Param("totken") String totken);
    /**
     * 获取该设备下所有的设备信息
     *
     * @param totken 设备标示
     *
     * @return List<KnDeviceInfo> 返回符合条件的集合列表
     */
    @Query("select u from KnDeviceInfo u where u.deviceToken =:totken and u.userId=:user_id and u.userSystem=:user_sys and u.onlineStat=:onlineStatName")
    List<KnDeviceInfo> findByTotkenAndUserIdAndForsys(@Param("totken") String totken,@Param("user_id") String user_id,@Param("user_sys") String user_sys,@Param("onlineStatName") String onlineStatName);
    /**
     * 根据应用标示  用户id 来自系统 设备标示  查询设备信息
     *
     * @param user_id        用户id
     * @param user_sys       来自系统
     * @param app_key        应用标示
     * @param totken         设备标示
     *
     * @return 返回符合条件的设备集合列表
     *
     * @parm updateTime  设备最后登录的时间
     */
    @Query("select u from KnDeviceInfo u where u.deviceToken <>:totken and u.userId=:user_id and u.userSystem=:user_sys and u.apiKey=:app_key and u.createTime >:update_Time")
    List<KnDeviceInfo> findListByAppkeyAndTime(@Param("user_id") String user_id,@Param("user_sys") String user_sys,@Param("app_key") String app_key,@Param("totken") String totken,@Param("update_Time") Long update_Time);
    @Modifying
    @Query("delete from KnDeviceInfo where userId in (:userIdList)")
    int delteUserDevice(@Param("userIdList") List<String> userIdList);
    /**
     * 根据应用标示  用户账号 来自系统 设备标示 以及在线状态 查询设备信息
     *
     * @param account        用户账号
     * @param app_key        应用标示
     * @param onlineStatName 在线状态
     *
     * @return 返回符合条件的设备集合列表
     */
    @Query("select u from KnDeviceInfo u where u.apiKey=:app_key and  u.onlineStat=:onlineStatName and u.loginName=:account")
    List<KnDeviceInfo> findByAccountAndOtherInfo(@Param("account")String account,@Param("app_key")String app_key,@Param("onlineStatName")String onlineStatName);
}
