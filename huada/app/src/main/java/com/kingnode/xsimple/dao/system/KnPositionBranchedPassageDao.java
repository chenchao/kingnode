package com.kingnode.xsimple.dao.system;
import java.util.List;

import com.kingnode.xsimple.entity.system.KnPositionBranchedPassage;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.jpa.repository.Query;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("JpaQlInspection")
public interface KnPositionBranchedPassageDao extends PagingAndSortingRepository<KnPositionBranchedPassage,Long>, JpaSpecificationExecutor<KnPositionBranchedPassage>{
    public List<KnPositionBranchedPassage> findBySubordinateId(Long empId);
    public List<KnPositionBranchedPassage> findByLeaderId(Long empId);
    public List<KnPositionBranchedPassage> findByLeaderIdOrSubordinateId(Long leaderId,Long subordinateId);
    @Query("select bp from KnPositionBranchedPassage bp where bp.leaderId in(?1)")
    List<KnPositionBranchedPassage> findByLeaderIds(List<Long> leaderIds);
}