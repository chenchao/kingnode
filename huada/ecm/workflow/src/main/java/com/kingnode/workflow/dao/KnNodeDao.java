package com.kingnode.workflow.dao;
import com.kingnode.workflow.entity.KnNode;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnNodeDao extends PagingAndSortingRepository<KnNode,Long>, JpaSpecificationExecutor<KnNode>{
    @Query("select u from KnNode u where (u.parentId is null or u.parentId=0) and u.tempId=?1")
    public KnNode queryFirstNode(Long tempId);

    @Query("select u from KnNode u where  u.parentId=?1")
    public KnNode queryNextNode(Long nodeId);

    @Query("select a from KnNode a where a.id=(select u.parentId from KnNode u where  u.id=?1)")
    public KnNode queryPrevNode(Long nodeId);
}
