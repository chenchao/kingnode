package com.kingnode.xsimple.dao.system;
import java.util.List;

import com.kingnode.xsimple.entity.system.KnEmployeeOrganization;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("JpaQlInspection")
public interface KnEmployeeOrganizationDao extends PagingAndSortingRepository<KnEmployeeOrganization,Long>, JpaSpecificationExecutor<KnEmployeeOrganization>{
    KnEmployeeOrganization findByIdOrgIdAndIdEmpId(Long orgId,Long empId);
    List<KnEmployeeOrganization> findByIdOrgId(Long orgId);
    List<KnEmployeeOrganization> findByIdEmpId(Long empId);
    /**
     * 根据组织架构的主键id集合查询员工信息
     * @param id 组织id集合
     * @return
     */
    @Query("select u from KnEmployeeOrganization u where u.id.org.id in(?1)")
    List<KnEmployeeOrganization> findByIdOrgIdList(List<Long> id);
    //--以下接口供第三方应用使用的时候生产的
    /**
     * 根据用户ID和主组织标识取用户组织
     *
     * @param empId 员工ID
     * @param major 主组织唯一标识值一定要等于"1"
     *
     * @return
     */
    KnEmployeeOrganization findByIdEmpIdAndMajor(Long empId,int major);
    List<KnEmployeeOrganization> findByIdOrgNameLike(String name);
    List<KnEmployeeOrganization> findByIdEmpIdInAndMajor(List<Long> id,int major);
    /**
     * 通过部门名称跟员工名称模糊查询员工信息
     *
     * @param orgName
     * @param empName
     * @param major
     *
     * @return
     */
    List<KnEmployeeOrganization> findByIdOrgNameLikeAndIdEmpUserNameLikeAndMajor(String orgName,String empName,int major);
}