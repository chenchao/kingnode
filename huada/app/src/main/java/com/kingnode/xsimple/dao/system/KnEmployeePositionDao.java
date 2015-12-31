package com.kingnode.xsimple.dao.system;
import java.util.List;

import com.kingnode.xsimple.entity.system.KnEmployeePosition;
import com.kingnode.xsimple.entity.system.KnEmployee;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.jpa.repository.Query;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("JpaQlInspection")
public interface KnEmployeePositionDao extends PagingAndSortingRepository<KnEmployeePosition,Long>, JpaSpecificationExecutor<KnEmployeePosition>{
    List<KnEmployeePosition> findByIdPosId(Long posId);
    List<KnEmployeePosition> findByIdEmpId(Long empId);
    KnEmployeePosition findByIdPosIdAndIdEmpId(Long posId,Long empId);
    @Query("select ep.id.emp from KnEmployeePosition ep where ep.id.pos.code=?1") List<KnEmployee> findKnEmployeeByPositionCode(String code);
}