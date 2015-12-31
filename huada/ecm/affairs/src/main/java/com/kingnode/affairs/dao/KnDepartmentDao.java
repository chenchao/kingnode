package com.kingnode.affairs.dao;
import com.kingnode.affairs.entity.KnDepartment;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public interface KnDepartmentDao extends PagingAndSortingRepository<KnDepartment,Long>, JpaSpecificationExecutor<KnDepartment>{
    /**
     * 获取部门
     *
     * @param departmentName 部门名称
     */
    KnDepartment findByDepartmentName(String departmentName);
}
