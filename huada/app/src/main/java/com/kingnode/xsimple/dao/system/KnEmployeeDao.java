package com.kingnode.xsimple.dao.system;

import java.util.List;
import javax.persistence.QueryHint;

import com.kingnode.xsimple.dto.KnCrmEmployeeDTO;
import com.kingnode.xsimple.dto.SimpleEmployeeDTO;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnOrganization;
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
public interface KnEmployeeDao extends PagingAndSortingRepository<KnEmployee, Long>, JpaSpecificationExecutor<KnEmployee> {
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<KnEmployee> findByUserNameLike(String userName);

    @Query("select e from KnEmployee e where e.id in ?1")
    List<KnEmployee> findEmployeeUserByIds(List<Long> ids);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<KnEmployee> findByUserName(String userName);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    KnEmployee findByLoginName(String loginName);

    List<KnEmployee> findByIdNotIn(List<Long> ids);

    //以下方法随时会删除
    @Query("select u from KnEmployee u where u.id in (:ids)")
    List<KnEmployee> findByIds(@Param(value = "ids") Long[] ids);

    @Query("select new com.kingnode.xsimple.dto.SimpleEmployeeDTO(s.id,u.userSystem,u.userType,s.loginName) from KnEmployee u,KnUser s where u.id=s.id and s.id in (:ids)")
    List<SimpleEmployeeDTO> findKnEmployeeDTO(@Param(value = "ids") Long[] ids);

    //跟SimpleEmployeeDTO中的构造器数据不对,修改查询语句,原来为SimpleEmployeeDTO(s.id,u.userSystem,u.userType,s.loginName) --cici
    @Query("select new com.kingnode.xsimple.dto.SimpleEmployeeDTO(s.id,u.loginName,u.userName,s.email) from KnEmployee u,KnUser s where u.id=s.id")
    List<SimpleEmployeeDTO> findKnEmployeeAll();

    /**
     * 查询员工列表中所有属性对象的员工信息;加入缓存
     *
     * @return 员工列表
     */
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    @Query("select u from KnEmployee u,KnUser s where u.id=s.id")
    List<KnEmployee> findKnEmployeeAllByAllAttribute();

    @Query("select u from KnEmployee u where u.userType=:userType")
    List<KnEmployee> findAll(@Param(value = "userType") String userType);

    /**
     * 根据 用户id 和来自系统查询用户信息
     *
     * @param user_id  用户id
     * @param user_sys 来自系统
     *
     * @return 返回符合条件的员工信息
     */
    @Query("select u from KnEmployee u where u.userId=:user_id and u.userSystem=:user_sys")
    List<KnEmployee> findKnListByUserIdAndSys(@Param("user_id") String user_id, @Param("user_sys") String user_sys);

    @Query("select e.imageAddress from KnEmployee e,KnUser u where e.id=u.id and u.id=:id")
    String findUserIcon(@Param(value = "id") Long id);

    @Query("select u from KnEmployee u where userSystem=:userSystem")
    List<KnEmployee> findInUserSystem(@Param(value = "userSystem") List<String> userSystem);

    List<KnEmployee> findByMarkName(@Param("markName") String markName);

    List<KnEmployee> findByWeixinId(@Param("weixinId") String weixinId);

    List<KnEmployee> findByUserIdAndUserSystem(@Param("userId") String userId, @Param("userSystem") String userSystem);

    @Query("select u.id from KnEmployee u where u.id in (select o.id.emp.id from KnEmployeeOrganization o where o.id.org.id in (:orgIds))")
    List<Long> findUserIdByOrgIds(@Param(value = "orgIds") List<Long> orgIds);

    /**
     * 根据idsList获取相应的员工信息
     *
     * @param idsList 员工idsList
     *
     * @return List<KnEmployee> 返回符合条件的所有员工信息
     */
    @Query("select new com.kingnode.xsimple.dto.KnCrmEmployeeDTO(u.id,k.name,u.userSystem,u.userId,k.loginName,u.userType)  from KnEmployee u,KnUser k where u.id = k.id and u.id in(:idsList)")
    List<KnCrmEmployeeDTO> findListAllByIds(@Param(value = "idsList") List<Long> idsList);

    /**
     * 查询crm员工列表信息
     *
     * @param kn_loginName 登录账号
     * @param userName     用户姓名
     * @param user_Id      用户ID
     * @param user_System  用户来自系统
     *
     * @return 返回用户列表信息
     */
    @Query("select new com.kingnode.xsimple.dto.KnCrmEmployeeDTO(u.id,k.name,u.userSystem,u.userId,k.loginName,u.userType)  from KnEmployee u,KnUser k where u.id = k.id and k.loginName like :kn_loginName and k.name like :userName and u.userId like :user_Id and u.userSystem like :user_System ")
    Page<KnCrmEmployeeDTO> findKnEmployeeAndKnUserList(@Param(value = "kn_loginName") String kn_loginName, @Param(value = "userName") String userName, @Param(value = "user_Id") String user_Id, @Param(value = "user_System") String user_System, Pageable pageable);

    /**
     * 根据id获取员工对应的部门信息
     *
     * @param emp_id     员工id
     * @param department 部门标示
     *
     * @return List<KnEmployee> 返回符合条件的所有员工信息
     */
    @Query("select u from KnEmployee u left join fetch u.org t1  left join fetch t1.id t2 left join fetch t2.org t3 where u.id =:emp_id and t3.orgType=:department")
    List<KnEmployee> findById(@Param(value = "emp_id") Long emp_id, @Param(value = "department") KnOrganization.OrgType department);

    /**
     * 查询除过本人得所有人员
     */
    Page<KnEmployee> findByIdNot(Long id, Pageable pageable);

    /**
     * TODO WANGYIFAN
     * 根据组织id集合获取员工对应员工的集合
     *
     * @param orgIdList 组织id集合
     *
     * @return List<KnEmployee> 返回符合条件的所有员工信息
     */
    @Query("select u from KnEmployee u left join fetch u.org t1  left join fetch t1.id t2 left join fetch t2.org t3 where  t3.id in(:orgIdList)")
    List<KnEmployee> findEmpListByOrgIdList(@Param(value = "orgIdList") List<Long> orgIdList);

    /**
     * 分页查询,加入缓存
     */
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Page<KnEmployee> findAll(Specification<KnEmployee> spec, Pageable pageable);

    /**
     * 使用的是sql语句
     */
    @Query(nativeQuery = true, value = "select u.* from kn_employee u where u.id in (:ids) and u.job='ENABLE' order by  convert(u.user_name using gbk) ")
    List<KnEmployee> findEmployee(@Param(value = "ids") List<Long> ids);

    /**
     * 根据帐号查询员工
     *
     * @param account 帐号
     *
     * @return 泛海符合条件的员工
     */
    @Query("select u from KnEmployee u  where u.loginName =:account")
    List<KnEmployee> findListByAccount(@Param(value = "account") String account);
}
