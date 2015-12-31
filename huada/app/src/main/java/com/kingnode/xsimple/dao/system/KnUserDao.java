package com.kingnode.xsimple.dao.system;
import java.util.List;
import javax.persistence.QueryHint;

import com.kingnode.xsimple.entity.system.KnOrganization;
import com.kingnode.xsimple.entity.system.KnUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnUserDao extends PagingAndSortingRepository<KnUser,Long>, JpaSpecificationExecutor<KnUser>{
    @QueryHints({@QueryHint(name="org.hibernate.cacheable",value="true")}) KnUser findByLoginName(String loginName);
    @QueryHints({@QueryHint(name="org.hibernate.cacheable",value="true")}) KnUser findByEmail(String email);
    /**
     * 对用户进行分页查询
     *
     * @param keyword
     * @param page
     *
     * @return
     */
    @QueryHints({@QueryHint(name="org.hibernate.cacheable",value="true")}) @Query("select u  from KnUser u  where  u.loginName like:keyword or u.name like :keyword")
    Page<KnUser> queryUser(@Param("keyword") String keyword,Pageable page);
    /**
     * 对员工进行分页查询
     *
     * @param keyword
     * @param page
     *
     * @return
     */
    @Query("select u  from KnUser u  where exists(select 1 from KnEmployee e where u.id=e.id) and (u.loginName like:keyword or u.name like :keyword)")
    Page<KnUser> queryEmployee(@Param("keyword") String keyword,Pageable page);
    /**
     * 对员工进行分页查询
     *
     * @param keyword
     * @param page
     *
     * @return
     */
    @Query("select u  from KnUser u  where exists(select 1 from KnEmployee e where u.id=e.id)") Page<KnUser> queryEmployee(Specification spec,Pageable page);
    /**
     * 根据登录用户名 查询用户信息列表
     *
     * @param name 登录用户名
     *
     * @return 符合条件的用户信息
     */
    @Query("select u from KnUser u where loginName=:name") List<KnUser> findByNameKnUser(@Param("name") String name);
    @Query("select u from KnUser u where id in (select id from KnEmployee where userSystem in (:fromSysList))")
    List<KnUser> findInFromSys(@Param("fromSysList") List<String> fromSysList);
    /**
     * 根据组织查出用户
     *
     * @param page
     * @param orgId 组织id
     * @param posId 岗位id
     *
     * @return
     */
    @Query("select u from KnUser u  where exists(select 1 from KnEmployee e where u.id=e.id) and exists(select 1 from KnEmployeeOrganization keo where keo.id.org.id=:orgId and keo.id.emp.id=u.id ) and exists(select 1 from KnEmployeePosition kep where kep.id.pos.id=:posId and kep.id.emp.id=u.id ) and (u.loginName like:keyword or u.name like :keyword)")
    Page<KnUser> queryEmployee(@Param("orgId") long orgId,@Param("posId") long posId,@Param("keyword") String keyword,Pageable page);
    /**
     * 根据组织id查出员工
     *
     * @param page
     * @param posId 员工id
     *
     * @return
     */
    @Query("select u from KnUser u  where exists(select 1 from KnEmployee e where u.id=e.id) and exists(select 1 from KnEmployeeOrganization keo where keo.id.org.id=:orgId and keo.id.emp.id=u.id )and (u.loginName like:keyword or u.name like :keyword)")
    Page<KnUser> queryEmployeeWithOrg(@Param("orgId") long orgId,@Param("keyword") String keyword,Pageable page);
    /**
     * 根据岗位id查出员工
     *
     * @param page
     * @param posId 岗位id
     *
     * @return
     */
    @Query("select u from KnUser u  where exists(select 1 from KnEmployee e where u.id=e.id)  and exists(select 1 from KnEmployeePosition kep where kep.id.pos.id =:posId and kep.id.emp.id=u.id ) and (u.loginName like:keyword or u.name like :keyword)")
    Page<KnUser> queryEmployeeWithPos(@Param("posId") Long posId,@Param("keyword") String keyword,Pageable page);
    /**
     * 根据岗位id查出员工
     *
     * @param page
     * @param posId 岗位id
     *
     * @return
     */
    @Query("select u from KnUser u  where exists(select 1 from KnEmployee e where u.id=e.id and e.userId like:userId and e.userSystem like :userSystem )  and (u.loginName like:loginName and u.name like :name and u.email like :email and u.status like :status) ")
    Page<KnUser> pageQueryEmployee(@Param("userId") String userId,@Param("userSystem") String userSystem,@Param("loginName") String loginName,@Param("name") String name,@Param("email") String email,@Param("status") String status,Pageable page);
    /**
     * 根据登录账号查询用户信息
     *
     * @param username 登录账号
     *
     * @return 返回符合条件的用户集合
     */
    @Query("select u from KnUser u  where u.loginName=:username") List<KnUser> findListByLoginName(@Param("username") String username);
    @Query("select u from KnUser u  where u.name like :username") List<KnUser> findListByName(@Param("username") String username);
    @Query("select u from KnUser u where u.id in (select o.id.emp.id from KnEmployeeOrganization o where o.id.org.id in (:orgIds))")
    Page<KnUser> findUsersByOrgIds(@Param(value="orgIds") List<Long> orgIds,Pageable pageable);
    @Query("select u from KnUser u where u.id in (select o.id.emp.id from KnEmployeeOrganization o where o.id.org.id in (:orgIds)) and u.id <> :userId")
    Page<KnUser> findUsersByOrgIdsNotSelf(@Param(value="orgIds") List<Long> orgIds,@Param(value="userId") Long userId,Pageable pageable);
    /**
     * 根据登录账号和用户姓名,以及部门名称查询用户id集合
     *
     * @param login_name  登录账号
     * @param user_name   用户名称
     * @param dept_name   部门名称
     * @param orgTypeList 组织标示
     *
     * @return 返回符合条件的用户id集合
     */
    @Query("select u.id from KnUser u,KnEmployeeOrganization o  where u.id = o.id.emp.id and u.name like :user_name and u.loginName like :login_name and o.id.org.name like:dept_name and o.id.org.orgType in(:orgTypeList)")
    List<Long> findListByNameAndLoginNameAndOrgName(@Param(value="login_name") String login_name,@Param(value="user_name") String user_name,@Param(value="dept_name") String dept_name,@Param(value="orgTypeList") List<KnOrganization.OrgType> orgTypeList);
    /**
     * 获取所有用户id集合
     *
     * @return 返回符合条件的用户id集合
     */
    @Query("select u.id from KnUser u") List<Long> findAllIdList();
    List<KnUser> findByIdNotIn(List<Long> ids);
}

