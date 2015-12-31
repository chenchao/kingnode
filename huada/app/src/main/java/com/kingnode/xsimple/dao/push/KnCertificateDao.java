package com.kingnode.xsimple.dao.push;
import java.util.List;

import com.kingnode.xsimple.entity.push.KnCertificateInfo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnCertificateDao extends PagingAndSortingRepository<KnCertificateInfo,Long>, JpaSpecificationExecutor<KnCertificateInfo>{
    /**
     * 根据应用appkey 查询证书信息,用于推送
     *
     * @param appkey 应用appkey
     *
     * @return 返回符合条件的证书列表信息
     */
    @Query("select u from KnCertificateInfo u where applicationInfo.apiKey=:appkey") List<KnCertificateInfo> findCerListByAppkey(@Param("appkey")String appkey);
}
