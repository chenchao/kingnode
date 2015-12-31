package com.kingnode.workflow.dao;
import java.util.List;

import com.kingnode.workflow.entity.KnCurrentApprove;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnCurrentApproveDao extends PagingAndSortingRepository<KnCurrentApprove,Long>, JpaSpecificationExecutor<KnCurrentApprove>{
    public KnCurrentApprove findByApplyId(Long applyId);
    /**
     * 获取制定审批人的待审批申请记录列表
     * @param approveId 审批人ID
     * @param pageRequest 分页信息
     * @return List<Long>待审批申请记录ID集合
     */
    @Query("select c.applyId from KnCurrentApprove c where c.approveId=:approveId")
    List<Long> pageCurrentApproveIds(@Param("approveId")Long approveId,Pageable pageRequest);
    /**
     * 根据申请记录ID集合获取待审批对象
     * @param applyIds 申请记录ID集合
     * @return List<KnCurrentApprove>
     */
    @Query("select c from KnCurrentApprove c where c.applyId in:applyIds and c.isHasView='DISABLE'")
    List<KnCurrentApprove> listCurrentApproveByNoView(@Param("applyIds") List<Long> applyIds);
    /**
     * 根据申请记录ID获取待审批对象,不会查出已经查看过的当前审批信息
     * @param applyId 申请记录ID
     * @return KnCurrentApprove
     */
    @Query("select c from KnCurrentApprove c where c.applyId=:applyId and c.isHasView='DISABLE'")
    KnCurrentApprove findOneByApplyId(@Param("applyId") Long applyId);

    /**
     * 根据申请记录ID获取待审批对象
     * @param applyId 申请记录ID
     * @return KnCurrentApprove
     */
    @Query("select c from KnCurrentApprove c where c.applyId=:applyId")
    KnCurrentApprove findCurrentApproveByApplyId(@Param("applyId") Long applyId);
}
