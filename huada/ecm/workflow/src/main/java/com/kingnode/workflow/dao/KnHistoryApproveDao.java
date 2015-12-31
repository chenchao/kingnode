package com.kingnode.workflow.dao;
import java.util.List;

import com.kingnode.workflow.entity.KnHistoryApprove;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnHistoryApproveDao extends PagingAndSortingRepository<KnHistoryApprove,Long>, JpaSpecificationExecutor<KnHistoryApprove>{
//    /**
//     * 根据申请id，获取审批历史记录
//     * @param applyId
//     * @return
//     */
//    public List<KnHistoryApprove> findByApplyId(Long applyId);
    /**
     * 根据nodeId和申请id，查找审批历史记录
     * @param nodeId
     * @param applyId
     * @return
     */
    @Query("select u from KnHistoryApprove u where u.nodeId=?1 and u.applyId=?2 order by u.createTime desc,id desc")
    public List<KnHistoryApprove> queryNodeIdAndApplyId(Long nodeId , Long applyId);

    /**
     * 获取未审批的历史审批信息
     * @param nodeId
     * @param applyId
     * @return
     */
    @Query("select u from KnHistoryApprove u where u.applyId=?1 and u.approveStatus in('VIEW','APPROVEING') order by id desc")
    public List<KnHistoryApprove> queryNoApproveApplyId(Long applyId);

    /**
     * 获取发起人，因为可能会有驳回的情况，所以可能会有多条记录
     * @param applyId
     * @return
     */
    @Query("select u from KnHistoryApprove u where u.role='APPLYER' and u.applyId=?1")
    public List<KnHistoryApprove> queryStarter(Long applyId);
    /**
     * 获取审批历史记录，根据时间倒叙排序
     * @param applyId
     * @return
     */
    @Query("select u from KnHistoryApprove u where u.applyId=?1 order by id desc")
    public List<KnHistoryApprove> queryHistoryApprove(Long applyId);


    /**
     * 获取自己参与过的工作流信息,只查询同意或拒绝的流程
     * @param applyId
     * @return
     */
    @Query("select u from KnHistoryApprove u where u.tempId in(select a.tempId from KnHistoryApprove a where a.approveId=?1) and u.approveStatus in('PASS','REJECT','END') order by id desc")
    public Page<KnHistoryApprove> queryHistoryApproveByUserid(Long userId,Pageable page);

    /**
     * 获取指定审批人的历史审批申请记录
     * @param approveId 审批人ID
     * @param pageRequest 分页信息
     * @return List<Long> 申请记录ID集合
     */
    @Query("select u.applyId from KnHistoryApprove u where u.approveId=:approveId and u.approveStatus <>'START' group by u.applyId")
    List<Long> pageHistoryApproveIds(@Param("approveId") Long approveId,Pageable pageRequest);
}
