package com.kingnode.xsimple.dao.meeting;
import com.kingnode.xsimple.entity.meeting.KnRegisterInfo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnRegisterInfoDao extends PagingAndSortingRepository<KnRegisterInfo,Long>, JpaSpecificationExecutor<KnRegisterInfo>{
}
