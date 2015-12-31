package com.kingnode.xsimple.dao.application;
import java.util.List;

import com.kingnode.xsimple.entity.application.KnVersionImageInfo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @wangyifan
 */
@SuppressWarnings("ALL")
public interface KnVersionImageInfoDao extends PagingAndSortingRepository<KnVersionImageInfo, Long>, JpaSpecificationExecutor<KnVersionImageInfo>{


    /**
     * 获取该版本id下的所有图片信息
     * @param ver_id  版本id
     * @return List<KnVersionImageInfo> 返回符合条件的版本图片集合列表
     */

    @Query("select u from KnVersionImageInfo u where versionId=:ver_id")
    List<KnVersionImageInfo> findVerListByVerId(@Param("ver_id") Long ver_id);
}
