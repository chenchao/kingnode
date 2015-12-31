package com.kingnode.xsimple.dao.application;
import java.util.List;

import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.dto.ScoreDto;
import com.kingnode.xsimple.entity.application.KnScoreInfo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * 应用评论信息
 * wangyfian
 */
@SuppressWarnings("ALL")
public interface KnAppScoreInfoDao extends PagingAndSortingRepository<KnScoreInfo, Long>, JpaSpecificationExecutor<KnScoreInfo>{
    /**
     * 查询评论信息
     * @param versionNum 版本号
     * @param rat_ing 评分
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param userName 用户名
     * @return 返回评论信息
     */
    @Query("select new com.kingnode.xsimple.dto.ScoreDto(t1.id,t2.userName,t3.num,t1.appComment,t1.rating,t1.createTime,t1.versionType) from KnScoreInfo t1,KnEmployee t2,KnVersionInfo t3 where t1.userId=t2.id and t1.versionId=t3.id and  t1.rating like:rat_ing and t3.num like:versionNum and t2.userName like :user_name and t1.appComment like :app_comment and t1.versionType =:plate_form and  t1.createTime >=:beginTime and t1.createTime <=:endTime order by t1.createTime desc")
    List<ScoreDto> findListByVerAndRatAndTypeAndTime(@Param("versionNum") String versionNum,@Param("rat_ing") String rat_ing,@Param("beginTime") Long beginTime,@Param("endTime") Long endTime,@Param("user_name") String user_name,@Param("app_comment") String app_comment,@Param("plate_form") Setting.VersionType plate_form);
    /**
     * 查询评论信息
     * @return 返回评论信息
     */
    @Query("select new com.kingnode.xsimple.dto.ScoreDto(t1.id,t2.userName,t3.num,t1.appComment,t1.rating,t1.createTime,t1.versionType) from KnScoreInfo t1,KnEmployee t2,KnVersionInfo t3 where t1.userId=t2.id and t1.versionId=t3.id  order by t1.createTime desc")
    List<ScoreDto> findAllList();
    /**
     * 查询评论信息
     * @param ids  评论id集合
     * @return 返回评论信息
     */
    @Query("select new com.kingnode.xsimple.dto.ScoreDto(t1.id,t2.userName,t3.num,t1.appComment,t1.rating,t1.createTime,t1.versionType) from KnScoreInfo t1,KnEmployee t2,KnVersionInfo t3 where t1.userId=t2.id and t1.versionId=t3.id and t1.id in(:ids) order by t1.createTime desc")
    List<ScoreDto> findAllListByIds(@Param("ids")List<Long> ids);
    /**
     * 查询评论信息
     * @param versionNum 版本号
     * @param rat_ing 评分
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param userName 用户名
     * @return 返回评论信息
     */
    @Query("select new com.kingnode.xsimple.dto.ScoreDto(t1.id,t2.userName,t3.num,t1.appComment,t1.rating,t1.createTime,t1.versionType) from KnScoreInfo t1,KnEmployee t2,KnVersionInfo t3 where t1.userId=t2.id and t1.versionId=t3.id and  t1.rating like:rat_ing and t3.num like:versionNum and t2.userName like :user_name and t1.appComment like :app_comment  and  t1.createTime >=:beginTime and t1.createTime <=:endTime order by t1.createTime desc")
    List<ScoreDto> findListByVerAndRatAndTime(@Param("versionNum") String versionNum,@Param("rat_ing") String rat_ing,@Param("beginTime") Long beginTime,@Param("endTime") Long endTime,@Param("user_name") String user_name,@Param("app_comment") String app_comment);
    /**
     * 查询评论信息
     * @param versionNum 版本号
     * @param rat_ing 评分
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param userName 用户名
     * @return 返回评论信息
     */
    @Query("select new com.kingnode.xsimple.dto.ScoreDto(t1.id,t2.userName,t3.num,t1.appComment,t1.rating,t1.createTime,t1.versionType) from KnScoreInfo t1,KnEmployee t2,KnVersionInfo t3 where t1.userId=t2.id and t1.versionId=t3.id and t1.userId =:user_id and t1.versionId =:version_id and t1.versionType =:plate_form order by t1.createTime desc")
    List<ScoreDto> findListByUserIdAndOtherInfo(@Param("user_id") Long user_id,@Param("version_id") Long version_id,@Param("plate_form") Setting.VersionType plate_form);
}
