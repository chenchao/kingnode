package com.kingnode.xsimple.dao.system;

import java.util.List;
import javax.persistence.QueryHint;

import com.kingnode.xsimple.entity.IdEntity.ActiveType;
import com.kingnode.xsimple.entity.system.KnRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author kongjiangwei
 */
@SuppressWarnings("ALL")
public interface KnRoleDao extends PagingAndSortingRepository<KnRole,Long>, JpaSpecificationExecutor<KnRole>{
    KnRole findByName(String name);

    List<KnRole> findByActive(ActiveType active);

    @Query("select u from KnRole u where id in (select roleId from KnRoleApplicationInfo  where appId =:appId) and active not in (:active)")
    Page<KnRole> findRoleListByAppId(@Param("appId") Long appId,Pageable pager,@Param("active") KnRole.ActiveType active);

    @Query("select u from KnRole u where id not in(select roleId from KnRoleApplicationInfo )")
    List<KnRole> findRoleListNotInAppList();

    @Query("select u from KnRole u where id in (select roleId from KnRoleApplicationInfo  where appId =:appId)")
    List<KnRole> findRoleByAppId(@Param("appId") Long appId);

    @Query("select u from KnRole u where id  in (select  roleId from KnRoleModuleFunctionInfo where moduleId=:mid  and functionId is null)")
    List<KnRole> findRoleByModule(@Param("mid") Long mid);

    @Query("select u from KnRole u where id  in (select  roleId from KnRoleModuleFunctionInfo where functionId=:functionId  and functionId is not null)")
    List<KnRole> findRoleByFunc(@Param("functionId") Long functionId);

    /**
     * 查询总数,加入缓存
     */
    @QueryHints({@QueryHint(name="org.hibernate.cacheable", value="true")})
    long count();
}
