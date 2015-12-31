package com.kingnode.xsimple.dao.push;

import com.kingnode.xsimple.entity.push.KnPushMessageInfo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnMessageDao extends PagingAndSortingRepository<KnPushMessageInfo,Long>, JpaSpecificationExecutor<KnPushMessageInfo> {

}
