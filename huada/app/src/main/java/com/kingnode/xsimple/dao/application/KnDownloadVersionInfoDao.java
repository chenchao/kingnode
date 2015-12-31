package com.kingnode.xsimple.dao.application;
import java.util.List;

import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.entity.application.KnDownloadVersionInfo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @wangyifan
 */
@SuppressWarnings("ALL")
public interface KnDownloadVersionInfoDao extends PagingAndSortingRepository<KnDownloadVersionInfo,Long>, JpaSpecificationExecutor<KnDownloadVersionInfo>{
    /**
     * 获取版本下载集合列表信息
     *
     * @param ver_id   版本idcode_Num
     * @param code_Num 验证码
     *
     * @return List<KnDownloadVersionInfo> 返回符合条件的版本下载集合列表
     */
    @Query("select u from KnDownloadVersionInfo u where versionId=:ver_id and codeNum =:code_Num")
    List<KnDownloadVersionInfo> findByVerIdAndCodeNum(@Param("ver_id") Long ver_id,@Param("code_Num") String code_Num);
    /**
     * 获取手机号获取验证码列表信息
     *
     * @param phone_Num 手机号码
     * @param codeNum   验证码标示
     *
     * @return List<KnDownloadVersionInfo> 返回符合条件的验证码列表
     */
    @Query("select u from KnDownloadVersionInfo u where phoneNum=:phone_Num and likeStatus =:codeNum order by createTime desc ")
    List<KnDownloadVersionInfo> findListByPhone(@Param("phone_Num") String phone_Num,@Param("codeNum") Setting.LikeStatusType codeNum);
}
