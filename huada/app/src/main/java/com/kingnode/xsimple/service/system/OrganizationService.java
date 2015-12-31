package com.kingnode.xsimple.service.system;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kingnode.diva.mapper.BeanMapper;
import com.kingnode.diva.persistence.Hibernates;
import com.kingnode.diva.persistence.SearchFilter;
import com.kingnode.xsimple.api.common.AjaxStatus;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.api.common.JsTree;
import com.kingnode.xsimple.api.common.TeamBean;
import com.kingnode.xsimple.api.system.EmpOrg;
import com.kingnode.xsimple.api.system.EmpPos;
import com.kingnode.xsimple.api.system.EmployeeOrganization;
import com.kingnode.xsimple.api.system.EmployeePosition;
import com.kingnode.xsimple.api.system.LdapUser;
import com.kingnode.xsimple.dao.system.KnEmployeeDao;
import com.kingnode.xsimple.dao.system.KnEmployeeOrganizationDao;
import com.kingnode.xsimple.dao.system.KnEmployeePositionDao;
import com.kingnode.xsimple.dao.system.KnOrganizationDao;
import com.kingnode.xsimple.dao.system.KnPositionBranchedPassageDao;
import com.kingnode.xsimple.dao.system.KnPositionDao;
import com.kingnode.xsimple.dao.system.KnTeamDao;
import com.kingnode.xsimple.dao.system.KnUserDao;
import com.kingnode.xsimple.dto.FullEmployeeDTO;
import com.kingnode.xsimple.dto.OrganizationDTO;
import com.kingnode.xsimple.dto.SimpleEmployeeDTO;
import com.kingnode.xsimple.entity.IdEntity;
import com.kingnode.xsimple.entity.IdEntity.ActiveType;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnEmployeeOrganization;
import com.kingnode.xsimple.entity.system.KnEmployeeOrganizationId;
import com.kingnode.xsimple.entity.system.KnEmployeePosition;
import com.kingnode.xsimple.entity.system.KnEmployeePositionId;
import com.kingnode.xsimple.entity.system.KnOrganization;
import com.kingnode.xsimple.entity.system.KnPosition;
import com.kingnode.xsimple.entity.system.KnPositionBranchedPassage;
import com.kingnode.xsimple.entity.system.KnRole;
import com.kingnode.xsimple.entity.system.KnTeam;
import com.kingnode.xsimple.entity.system.KnUser;
import com.kingnode.xsimple.util.Utils;
import com.kingnode.xsimple.util.key.UuidMaker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * 组织机构相关服务类
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Component
public class OrganizationService{
    private KnUserDao knUserDao;
    private KnEmployeeDao empDao;
    private KnOrganizationDao orgDao;
    private KnEmployeeOrganizationDao empOrgDao;
    private KnPositionDao posDao;
    private KnEmployeePositionDao empPosDao;
    private KnTeamDao teamDao;
    private KnPositionBranchedPassageDao kpbpd;
    private ResourceService rs;
    /** 组织管理 begin* */
    public List<JsTree> OrganizationJsTree(){
        List<KnOrganization> list=(List<KnOrganization>)orgDao.findAll();
        List<JsTree> jts=Lists.newArrayList();
        for(KnOrganization ko : list){
            JsTree jt=new JsTree();
            jt.setId(ko.getId().toString());
            jt.setParent(ko.getSupId().compareTo(0L)>0?ko.getSupId().toString():"#");
            jt.setText(ko.getName());
            jts.add(jt);
        }
        return jts;
    }
    public List<KnOrganization> ListKnOrganizationBySupId(Long supId){
        return orgDao.findBySupId(supId);
    }
    public List<OrganizationDTO> ListKnOrganization(Long supId){
        return orgDao.findBySupIdHasChildrenNum(supId);
    }
    public List<OrganizationDTO> ListKnOrganization(Long supId,IdEntity.ActiveType activeType){
        return orgDao.findBySupIdHasChildrenNumAndActive(supId,activeType);
    }
    /**
     * 根据组织id查找组织
     *
     * @return
     */
    public KnOrganization ReadOrg(long id){
        return this.orgDao.findOne(id);
    }
    /**
     * 通过code查询组织
     *
     * @param code
     *
     * @return
     */
    public KnOrganization ReadOrgByCode(String code){
        return orgDao.findByCode(code);
    }
    /**
     * 更新本地员工信息
     *
     * @param knEmployee 待更新的员工信息
     */
    @Transactional(readOnly=false)
    public void UpdateKnEmploy(KnEmployee knEmployee){
        empDao.save(knEmployee);
    }
    public void SaveEmps(List<KnEmployee> emps){
        empDao.save(emps);
    }
    @Transactional(readOnly=false)
    public void SaveUser(KnUser user){
        knUserDao.save(user);
    }
    @Transactional(readOnly=false)
    public void SaveUsers(List<KnUser> users){
        knUserDao.save(users);
    }
    /**
     * 查询所有部门
     */
    public List<KnOrganization> PageKnOrganization(Integer pageNo,Integer pageSize){
        return orgDao.findAll(new PageRequest(pageNo,pageSize)).getContent();
    }
    /**
     * 保存组织
     *
     * @param org
     */
    @Transactional(readOnly=false)
    public void SaveOrg(KnOrganization org){
        if(org.getId()!=null&&org.getId().compareTo(0L)>0){
            if(org.getSupId().compareTo(0L)>0){
                KnOrganization supZo=orgDao.findOne(org.getSupId());
                org.setPath(supZo.getPath()+org.getId()+".");
                org.setDepth(supZo.getDepth()+1);
            }else{
                org.setPath(org.getId()+".");
                org.setDepth(1L);
            }
        }else{
            this.orgDao.save(org);
            if(org.getSupId().compareTo(0L)>0){
                KnOrganization supZo=orgDao.findOne(org.getSupId());
                org.setPath(supZo.getPath()+org.getId()+".");
                org.setDepth(supZo.getDepth()+1);
            }else{
                org.setPath(org.getId()+".");
                org.setDepth(1L);
            }
        }
        this.orgDao.save(org);
    }
    /**
     * **
     * 用于广联达导入系统
     *
     * @param orgs
     */
    @Transactional(readOnly=false)
    public List<KnOrganization> SaveOrg(List<KnOrganization> orgs){
        return (List<KnOrganization>)orgDao.save(orgs);
    }
    /**
     * 维护员工部门关系
     *
     * @param employeeOrganizations
     */
    @Transactional(readOnly=false)
    public void SaveEmpOrg(List<KnEmployeeOrganization> employeeOrganizations){
        empOrgDao.save(employeeOrganizations);
    }
    /**
     * 删除组织及其下属的组织
     *
     * @param orgId
     *
     * @return
     */
    @Transactional(readOnly=false) public AjaxStatus DeleteOrganization(Long orgId){
        KnOrganization ko=orgDao.findOne(orgId);
        if(ko!=null){
            //删除子组织和组织下面的员工关系
            List<KnOrganization> organizations=orgDao.findByPathLike(ko.getPath()+"%");
            for(KnOrganization _ko : organizations){
                for(KnEmployeeOrganization keo : empOrgDao.findByIdOrgId(_ko.getId())){
                    empOrgDao.delete(keo);
                }
                orgDao.delete(_ko);
            }
        }
        return new AjaxStatus(true);
    }
    public DataTable<EmployeeOrganization> PageEmpInOrg(final DataTable<EmployeeOrganization> dt,final Long orgId,final Map<String,Object> searchParams){
        final String[] column=new String[]{"loginName","userName","email","job","major","charge"};
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength());
        final Map<String,SearchFilter> filters=SearchFilter.parse(searchParams);
        Page<KnEmployeeOrganization> page=empOrgDao.findAll(new Specification<KnEmployeeOrganization>(){
            @Override
            public Predicate toPredicate(Root<KnEmployeeOrganization> root,CriteriaQuery<?> query,CriteriaBuilder builder){
                Path<KnEmployee> ke=root.get("id").get("emp");
                List<Predicate> predicates=Lists.newArrayList();
                predicates.add(builder.equal(root.<KnEmployeeOrganizationId>get("id").<KnOrganization>get("org").get("id"),orgId));
                SearchFilter loginName=filters.get("LIKE_loginName");
                if(loginName!=null){
                    predicates.add(builder.like(ke.<String>get(loginName.fieldName),"%"+loginName.value+"%"));
                }
                SearchFilter name=filters.get("LIKE_userName");
                if(name!=null){
                    predicates.add(builder.like(ke.<String>get(name.fieldName),"%"+name.value+"%"));
                }
                SearchFilter email=filters.get("LIKE_email");
                if(email!=null){
                    predicates.add(builder.like(ke.<String>get(email.fieldName),"%"+email.value+"%"));
                }
                SearchFilter job=filters.get("EQ_job");
                if(job!=null){
                    predicates.add(builder.equal(ke.<ActiveType>get("job"),ActiveType.ENABLE.toString().equals(job.value.toString())?ActiveType.ENABLE:ActiveType.DISABLE));
                }
                SearchFilter charge=filters.get("EQ_charge");
                if(charge!=null){
                    predicates.add(builder.equal(root.get(charge.fieldName),charge.value));
                }
                SearchFilter major=filters.get("EQ_major");
                if(major!=null){
                    predicates.add(builder.equal(root.get(major.fieldName),major.value));
                }
                Integer sc=Integer.parseInt(dt.getiSortCol_0());
                Expression order=(sc<4?ke.get(column[sc]):root.get(column[sc]));
                if("asc".equals(dt.getsSortDir_0())){
                    builder.asc(order);
                }else{
                    builder.desc(order);
                }
                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        },pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        List<EmployeeOrganization> list=Lists.newArrayList();
        for(KnEmployeeOrganization ep : page.getContent()){
            list.add(new EmployeeOrganization(ep.getId().getEmp().getId(),ep.getId().getEmp().getLoginName(),ep.getId().getEmp().getUserName(),ep.getId().getEmp().getEmail(),ep.getId().getEmp().getJob(),ep.getCharge(),ep.getMajor()));
        }
        dt.setAaData(list);
        return dt;
    }
    public DataTable<EmployeeOrganization> PageEmpOutOrg(DataTable<EmployeeOrganization> dt,final Long orgId,final Map<String,Object> searchParams){
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        String[] column=new String[]{"loginName","userName","email"};
        Sort sort=new Sort(d,column[Integer.parseInt(dt.getiSortCol_0())]);
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        final Map<String,SearchFilter> filters=SearchFilter.parse(searchParams);
        Page<KnEmployee> page=empDao.findAll(new Specification<KnEmployee>(){
            @Override
            public Predicate toPredicate(Root<KnEmployee> root,CriteriaQuery<?> query,CriteriaBuilder builder){
                List<Predicate> predicates=Lists.newArrayList();
                Subquery subQuery=query.subquery(KnEmployeeOrganization.class);
                Root<KnEmployeeOrganization> subRoot=subQuery.from(KnEmployeeOrganization.class);
                subQuery.select(subRoot.<KnEmployeeOrganizationId>get("id").<KnEmployee>get("emp").<Long>get("id"));
                Predicate[] p=new Predicate[]{builder.equal(root.<Long>get("id"),subRoot.<KnEmployeeOrganizationId>get("id").<KnEmployee>get("emp").<Long>get("id")),
                        //为了代码切割
                        builder.equal(subRoot.<KnEmployeeOrganizationId>get("id").<KnOrganization>get("org").get("id"),orgId)};
                subQuery.where(p);
                predicates.add(builder.not(builder.exists(subQuery)));
                SearchFilter loginName=filters.get("LIKE_loginName");
                if(loginName!=null){
                    predicates.add(builder.like(root.<String>get("loginName"),"%"+loginName.value+"%"));
                }
                SearchFilter name=filters.get("LIKE_userName");
                if(name!=null){
                    predicates.add(builder.like(root.<String>get("userName"),"%"+name.value+"%"));
                }
                SearchFilter email=filters.get("LIKE_email");
                if(email!=null){
                    predicates.add(builder.like(root.<String>get("email"),"%"+email.value+"%"));
                }
                predicates.add(builder.equal(root.<ActiveType>get("job"),ActiveType.ENABLE));
                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        },pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        List<EmployeeOrganization> list=Lists.newArrayList();
        for(KnEmployee ep : page.getContent()){
            list.add(new EmployeeOrganization(ep.getId(),ep.getLoginName(),ep.getUserName(),ep.getEmail()));
        }
        dt.setAaData(list);
        return dt;
    }
    @Transactional(readOnly=false) public AjaxStatus JoinOrganization(Long orgId,Long empId){
        AjaxStatus as=new AjaxStatus(true);
        KnEmployeeOrganization keo=new KnEmployeeOrganization(new KnEmployeeOrganizationId(new KnOrganization(orgId),new KnEmployee(empId)));
        keo.setMajor(1);//默认设置为主组织人员
        empOrgDao.save(keo);
        //给人员设置组织
        KnEmployee emp=empDao.findOne(empId);
        if(emp.getOrg()==null){
            emp.setOrg(new HashSet<KnEmployeeOrganization>());
        }
        emp.getOrg().add(keo);
        empDao.save(emp);
        return as;
    }
    public AjaxStatus LeaveOrganization(Long orgId,Long empId){
        AjaxStatus as=new AjaxStatus(true);
        KnEmployeeOrganization kmo=empOrgDao.findByIdOrgIdAndIdEmpId(orgId,empId);
        //解除人员组织关系
        KnEmployee emp=empDao.findOne(empId);
        emp.getOrg().remove(kmo);
        empDao.save(emp);
        empOrgDao.delete(kmo);
        return as;
    }
    /**
     * 变更员工所在组织的职级（主负责人）
     *
     * @param orgId
     * @param empId
     *
     * @return
     */
    public AjaxStatus ChargeOrganization(Long orgId,Long empId){
        AjaxStatus as=new AjaxStatus(true);
        KnEmployeeOrganization kmo=empOrgDao.findByIdOrgIdAndIdEmpId(orgId,empId);
        kmo.setCharge(kmo.getCharge()==0?1:0);
        empOrgDao.save(kmo);
        return as;
    }
    /**
     * 变更员工所在组织的职能（主组织）
     *
     * @param orgId
     * @param empId
     *
     * @return
     */
    public AjaxStatus MajorOrganization(Long orgId,Long empId){
        AjaxStatus as=new AjaxStatus(true);
        KnEmployeeOrganization kmo=empOrgDao.findByIdOrgIdAndIdEmpId(orgId,empId);
        boolean t=false;
        if(kmo.getMajor()==0){
            //获取员工所有的组织，检查是否已经存在一个主组织
            List<KnEmployeeOrganization> kems=empOrgDao.findByIdEmpId(empId);
            for(KnEmployeeOrganization keo : kems){
                t=(keo.getMajor()==1);
                if(t){
                    break;
                }
            }
        }
        if(!t){
            kmo.setMajor(kmo.getMajor()==0?1:0);
            empOrgDao.save(kmo);
        }else{
            as.setSuccess(false);
            as.setCode("1");
        }
        return as;
    }
    /** 组织管理 end* */
    /** 岗位管理 begin* */
    public List<JsTree> PositionJsTree(){
        List<KnPosition> list=(List<KnPosition>)posDao.findAll();
        List<JsTree> jts=Lists.newArrayList();
        for(KnPosition kp : list){
            JsTree jt=new JsTree();
            jt.setId(kp.getId().toString());
            jt.setParent(kp.getSupId().compareTo(0L)>0?kp.getSupId().toString():"#");
            jt.setText(kp.getName());
            jts.add(jt);
        }
        return jts;
    }
    /**
     * 根据组织id查找组织
     *
     * @return
     */
    public KnPosition ReadPos(Long id){
        return this.posDao.findOne(id);
    }
    @Transactional(readOnly=false)
    public void SaveKnPosition(KnPosition pos){
        if(pos.getId()!=null&&pos.getId().compareTo(0L)>0){
            if(pos.getSupId().compareTo(0L)>0){
                KnPosition supZo=posDao.findOne(pos.getSupId());
                pos.setPath(supZo.getPath()+pos.getId()+".");
                pos.setDepth(supZo.getDepth()+1);
            }else{
                pos.setPath(pos.getId()+".");
                pos.setDepth(1L);
            }
        }else{
            this.posDao.save(pos);
            if(pos.getSupId().compareTo(0L)>0){
                KnPosition supZo=posDao.findOne(pos.getSupId());
                pos.setPath(supZo.getPath()+pos.getId()+".");
                pos.setDepth(supZo.getDepth()+1);
            }else{
                pos.setPath(pos.getId()+".");
                pos.setDepth(1L);
            }
        }
        this.posDao.save(pos);
    }
    /**
     * 根据id删除岗位
     *
     * @param empId
     *
     * @return
     */
    @Transactional(readOnly=false)
    public AjaxStatus DeletePosition(Long empId){
        KnPosition pos=posDao.findOne(empId);
        for(KnPosition ko : posDao.findByPathLike(pos.getPath()+"%")){
            for(KnEmployeePosition keo : empPosDao.findByIdPosId(ko.getId())){
                empPosDao.delete(keo);
            }
            posDao.delete(ko);
        }
        return new AjaxStatus(true);
    }
    public DataTable<EmployeePosition> PageEmpInPos(final DataTable<EmployeePosition> dt,final Long posId,final Map<String,Object> searchParams){
        final String[] column=new String[]{"loginName","userName","email","job","major"};
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength());
        final Map<String,SearchFilter> filters=SearchFilter.parse(searchParams);
        Page<KnEmployeePosition> page=empPosDao.findAll(new Specification<KnEmployeePosition>(){
            @Override
            public Predicate toPredicate(Root<KnEmployeePosition> root,CriteriaQuery<?> query,CriteriaBuilder builder){
                Path<KnEmployee> ke=root.get("id").get("emp");
                List<Predicate> predicates=Lists.newArrayList();
                predicates.add(builder.equal(root.<KnEmployeePositionId>get("id").<KnPosition>get("pos").get("id"),posId));
                SearchFilter loginName=filters.get("LIKE_loginName");
                if(loginName!=null){
                    predicates.add(builder.like(ke.<String>get("loginName"),"%"+loginName.value+"%"));
                }
                SearchFilter name=filters.get("LIKE_userName");
                if(name!=null){
                    predicates.add(builder.like(ke.<String>get("userName"),"%"+name.value+"%"));
                }
                SearchFilter email=filters.get("LIKE_email");
                if(email!=null){
                    predicates.add(builder.like(ke.<String>get("email"),"%"+email.value+"%"));
                }
                SearchFilter job=filters.get("EQ_job");
                if(job!=null){
                    predicates.add(builder.equal(ke.<ActiveType>get("job"),ActiveType.ENABLE.toString().equals(job.value.toString())?ActiveType.ENABLE:ActiveType.DISABLE));
                }
                SearchFilter major=filters.get("EQ_major");
                if(major!=null){
                    major.value=ActiveType.ENABLE.toString().equals(major.value.toString())?ActiveType.ENABLE:ActiveType.DISABLE;
                    predicates.add(builder.equal(root.<ActiveType>get("major"),ActiveType.ENABLE.toString().equals(major.value.toString())?ActiveType.ENABLE:ActiveType.DISABLE));
                }
                Integer sc=Integer.parseInt(dt.getiSortCol_0());
                Expression order=sc==4?root.get(column[sc]):ke.get(column[sc]);
                if("asc".equals(dt.getsSortDir_0())){
                    builder.asc(order);
                }else{
                    builder.desc(order);
                }
                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        },pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        List<EmployeePosition> list=Lists.newArrayList();
        for(KnEmployeePosition ep : page.getContent()){
            list.add(new EmployeePosition(ep.getId().getEmp().getId(),ep.getId().getEmp().getLoginName(),ep.getId().getEmp().getUserName(),ep.getId().getEmp().getEmail(),ep.getId().getEmp().getJob(),ep.getMajor()));
        }
        dt.setAaData(list);
        return dt;
    }
    public DataTable<EmployeePosition> PageEmpOutPos(DataTable<EmployeePosition> dt,final Long posId,final Map<String,Object> searchParams){
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        String[] column=new String[]{"loginName","userName","email"};
        Sort sort=new Sort(d,column[Integer.parseInt(dt.getiSortCol_0())]);
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        final Map<String,SearchFilter> filters=SearchFilter.parse(searchParams);
        Page<KnEmployee> page=empDao.findAll(new Specification<KnEmployee>(){
            @Override
            public Predicate toPredicate(Root<KnEmployee> root,CriteriaQuery<?> query,CriteriaBuilder builder){
                List<Predicate> predicates=Lists.newArrayList();
                Subquery subQuery=query.subquery(KnEmployeePosition.class);
                Root<KnEmployeePosition> subRoot=subQuery.from(KnEmployeePosition.class);
                subQuery.select(subRoot.<KnEmployeePositionId>get("id").<KnEmployee>get("emp").<Long>get("id"));
                Predicate[] p=new Predicate[]{builder.equal(root.<Long>get("id"),subRoot.<KnEmployeePositionId>get("id").<KnEmployee>get("emp").<Long>get("id")),
                        //为了代码切割
                        builder.equal(subRoot.<KnEmployeePositionId>get("id").<KnPosition>get("pos").get("id"),posId)};
                subQuery.where(p);
                predicates.add(builder.not(builder.exists(subQuery)));
                SearchFilter loginName=filters.get("LIKE_loginName");
                if(loginName!=null){
                    predicates.add(builder.like(root.<String>get("loginName"),"%"+loginName.value+"%"));
                }
                SearchFilter name=filters.get("LIKE_userName");
                if(name!=null){
                    predicates.add(builder.like(root.<String>get("userName"),"%"+name.value+"%"));
                }
                SearchFilter email=filters.get("LIKE_email");
                if(email!=null){
                    predicates.add(builder.like(root.<String>get("email"),"%"+email.value+"%"));
                }
                predicates.add(builder.equal(root.<ActiveType>get("job"),ActiveType.ENABLE));
                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        },pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        List<EmployeePosition> list=Lists.newArrayList();
        for(KnEmployee ep : page.getContent()){
            list.add(new EmployeePosition(ep.getId(),ep.getLoginName(),ep.getUserName(),ep.getEmail()));
        }
        dt.setAaData(list);
        return dt;
    }
    public AjaxStatus JoinPosition(Long posId,Long empId){
        AjaxStatus as=new AjaxStatus(true);
        KnEmployeePosition kep=new KnEmployeePosition(new KnEmployeePositionId(new KnPosition(posId),new KnEmployee(empId)));
        kep.setMajor(ActiveType.DISABLE);
        empPosDao.save(kep);
        return as;
    }
    public AjaxStatus LeavePosition(Long posId,Long empId){
        AjaxStatus as=new AjaxStatus(true);
        KnEmployeePosition kep=empPosDao.findByIdPosIdAndIdEmpId(posId,empId);
        empPosDao.delete(kep);
        //离开岗位
        KnEmployee emp=empDao.findOne(empId);
        emp.getPos().remove(kep);
        empDao.save(emp);
        //删除被当前人分管
        List<KnPositionBranchedPassage> passages=kpbpd.findByLeaderId(empId);
        kpbpd.delete(passages);
        return as;
    }
    /**
     * 变更员工所在组织的职能（主岗位）
     *
     * @param posId
     * @param empId
     *
     * @return
     */
    public AjaxStatus MajorPosition(Long posId,Long empId){
        AjaxStatus as=new AjaxStatus(true);
        KnEmployeePosition kmo=empPosDao.findByIdPosIdAndIdEmpId(posId,empId);
        boolean t=false;
        if(kmo.getMajor().equals(ActiveType.DISABLE)){
            //获取员工所有的组织，检查是否已经存在一个主组织
            List<KnEmployeePosition> kems=empPosDao.findByIdEmpId(empId);
            for(KnEmployeePosition keo : kems){
                t=keo.getMajor().equals(ActiveType.ENABLE);
                if(t){
                    break;
                }
            }
        }
        if(!t){
            kmo.setMajor(kmo.getMajor().equals(ActiveType.DISABLE)?ActiveType.ENABLE:ActiveType.DISABLE);
            empPosDao.save(kmo);
        }else{
            as.setSuccess(false);
            as.setCode("1");
        }
        return as;
    }
    public KnEmployeePosition ReadKnEmployeePosition(Long posId,Long empId){
        return empPosDao.findByIdPosIdAndIdEmpId(posId,empId);
    }
    public DataTable<EmployeePosition> PageEmpInPosBranched(DataTable<EmployeePosition> dt,final Long posId,final Long empId,final Map<String,Object> searchParams){
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        String[] column=new String[]{"loginName","userName","email","job"};
        Sort sort=new Sort(d,column[Integer.parseInt(dt.getiSortCol_0())]);
        final PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        final Map<String,SearchFilter> filters=SearchFilter.parse(searchParams);
        Page<KnEmployee> page=empDao.findAll(new Specification<KnEmployee>(){
            @Override
            public Predicate toPredicate(Root<KnEmployee> root,CriteriaQuery<?> query,CriteriaBuilder builder){
                List<Predicate> predicates=Lists.newArrayList();
                Subquery<KnPositionBranchedPassage> subQuery=query.subquery(KnPositionBranchedPassage.class);
                Root<KnPositionBranchedPassage> subRoot=subQuery.from(KnPositionBranchedPassage.class);
                subQuery.select(subRoot);
                Predicate[] p=new Predicate[]{builder.equal(root.<Long>get("id"),subRoot.<Long>get("subordinateId")),
                        //为了代码切割
                        builder.equal(subRoot.<Long>get("posId"),posId),
                        //为了代码切割
                        builder.equal(subRoot.<Long>get("leaderId"),empId)};
                subQuery.where(p);
                predicates.add(builder.exists(subQuery));
                SearchFilter loginName=filters.get("LIKE_loginName");
                if(loginName!=null){
                    predicates.add(builder.like(root.<String>get("loginName"),"%"+loginName.value+"%"));
                }
                SearchFilter name=filters.get("LIKE_userName");
                if(name!=null){
                    predicates.add(builder.like(root.<String>get("userName"),"%"+name.value+"%"));
                }
                SearchFilter email=filters.get("LIKE_email");
                if(email!=null){
                    predicates.add(builder.like(root.<String>get("email"),"%"+email.value+"%"));
                }
                SearchFilter job=filters.get("EQ_job");
                if(job!=null){
                    predicates.add(builder.equal(root.<ActiveType>get("job"),ActiveType.ENABLE.toString().equals(job.value.toString())?ActiveType.ENABLE:ActiveType.DISABLE));
                }
                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        },pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        List<EmployeePosition> list=Lists.newArrayList();
        for(KnEmployee ep : page.getContent()){
            list.add(new EmployeePosition(ep.getId(),ep.getLoginName(),ep.getUserName(),ep.getEmail(),ep.getJob()));
        }
        dt.setAaData(list);
        return dt;
    }
    public DataTable<EmployeePosition> PageEmpOutPosBranched(DataTable<EmployeePosition> dt,final Long empId,final Map<String,Object> searchParams){
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        String[] column=new String[]{"loginName","userName","email","job"};
        Sort sort=new Sort(d,column[Integer.parseInt(dt.getiSortCol_0())]);
        final PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        final Map<String,SearchFilter> filters=SearchFilter.parse(searchParams);
        Page<KnEmployee> page=empDao.findAll(new Specification<KnEmployee>(){
            @Override
            public Predicate toPredicate(Root<KnEmployee> root,CriteriaQuery<?> query,CriteriaBuilder builder){
                List<Predicate> predicates=Lists.newArrayList();
                Subquery<KnPositionBranchedPassage> subQuery=query.subquery(KnPositionBranchedPassage.class);
                Root<KnPositionBranchedPassage> subRoot=subQuery.from(KnPositionBranchedPassage.class);
                subQuery.select(subRoot);
                Predicate[] p=new Predicate[]{builder.equal(root.<Long>get("id"),subRoot.<Long>get("subordinateId"))};
                subQuery.where(p);
                predicates.add(builder.not(builder.exists(subQuery)));
                SearchFilter loginName=filters.get("LIKE_loginName");
                if(loginName!=null){
                    predicates.add(builder.like(root.<String>get("loginName"),"%"+loginName.value+"%"));
                }
                SearchFilter name=filters.get("LIKE_userName");
                if(name!=null){
                    predicates.add(builder.like(root.<String>get("userName"),"%"+name.value+"%"));
                }
                SearchFilter email=filters.get("LIKE_email");
                if(email!=null){
                    predicates.add(builder.like(root.<String>get("email"),"%"+email.value+"%"));
                }
                predicates.add(builder.notEqual(root.<Long>get("id"),empId));
                predicates.add(builder.equal(root.<ActiveType>get("job"),ActiveType.ENABLE));
                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        },pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        List<EmployeePosition> list=Lists.newArrayList();
        for(KnEmployee ep : page.getContent()){
            list.add(new EmployeePosition(ep.getId(),ep.getLoginName(),ep.getUserName(),ep.getEmail()));
        }
        dt.setAaData(list);
        return dt;
    }
    public AjaxStatus JoinPositionBranched(Long posId,String posCode,String posName,Long leaderId,String leaderName,Long empId,String empName){
        AjaxStatus as=new AjaxStatus(true);
        List<KnPositionBranchedPassage> list=kpbpd.findBySubordinateId(empId);
        KnPositionBranchedPassage kpbp=new KnPositionBranchedPassage();
        if(list.size()>0){
            kpbp=list.get(0);
        }
        kpbp.setPosId(posId);
        kpbp.setPosCode(posCode);
        kpbp.setPosName(posName);
        kpbp.setLeaderId(leaderId);
        kpbp.setLeaderName(leaderName);
        kpbp.setSubordinateId(empId);
        kpbp.setSubordinateName(empName);
        kpbpd.save(kpbp);
        return as;
    }
    public AjaxStatus LeavePositionBranched(Long empId){
        AjaxStatus as=new AjaxStatus(true);
        List<KnPositionBranchedPassage> kep=kpbpd.findBySubordinateId(empId);
        kpbpd.delete(kep);
        return as;
    }
    /** 岗位管理 end* */
    /** 员工管理 end* */
    public FullEmployeeDTO ReadEmployee(Long id){
        KnEmployee ke=empDao.findOne(id);
        FullEmployeeDTO fed=BeanMapper.map(ke,FullEmployeeDTO.class);
        List<String> org=Lists.newArrayList();
        for(KnEmployeeOrganization keo : ke.getOrg()){
            org.add(keo.getId().getOrg().getName());
        }
        fed.setOrgName(StringUtils.join(org,","));
        return fed;
    }
    public List<SimpleEmployeeDTO> ListEmployee(String name){
        List<SimpleEmployeeDTO> list=Lists.newArrayList();
        for(KnEmployee ke : empDao.findByUserName(name)){
            list.add(BeanMapper.map(ke,SimpleEmployeeDTO.class));
        }
        return list;
    }
    /**
     * 根据组织的主键id获取组织地下的人员信息列表
     *
     * @param orgId 组织的主键id
     *
     * @return 员工集合列表
     */
    public List<SimpleEmployeeDTO> ListEmployeeByOrgId(Long orgId){
        List<SimpleEmployeeDTO> list=Lists.newArrayList();
        for(KnEmployeeOrganization ke : empOrgDao.findByIdOrgId(orgId)){
            list.add(BeanMapper.map(ke.getId().getEmp(),SimpleEmployeeDTO.class));
        }
        return list;
    }
    /**
     * 根据组织的主键id获取组织地下的人员信息列表-仅在职员工
     *
     * @param orgId 组织的主键id
     *
     * @return 在职员工集合列表
     */
    public List<SimpleEmployeeDTO> ListEmployeeEnableByOrgId(Long orgId){
        List<SimpleEmployeeDTO> list=Lists.newArrayList();
        for(KnEmployeeOrganization ke : empOrgDao.findByIdOrgId(orgId)){
            KnEmployee emp=ke.getId().getEmp();
            //在职人员才放入信息
            if(emp!=null&&ActiveType.ENABLE.equals(emp.getJob())){
                list.add(BeanMapper.map(ke.getId().getEmp(),SimpleEmployeeDTO.class));
            }
        }
        return list;
    }
    /**
     * 根据组织的主键id获取组织底下的人员和组织底下的子组织的员工信息列表
     *
     * @param orgId 组织的主键id
     *
     * @return 员工集合列表
     */
    public List<KnEmployee> ListAllEmployeeByOrgId(Long orgId){
        List<KnEmployee> list=Lists.newArrayList();
        KnOrganization org=orgDao.findOne(orgId);
        if(org==null){
            return list;
        }
        String path=org.getPath();
        //查询组织下的所有组织列表
        List<KnOrganization> orgList=FindLowerOrganListByPath(path+"%");
        if(Utils.isEmpityCollection(orgList)){
            return list;
        }
        List<Long> orgIds=Lists.newArrayList();
        for(KnOrganization k : orgList){
            orgIds.add(k.getId());
        }
        List<KnEmployeeOrganization> kes=empOrgDao.findByIdOrgIdList(orgIds);
        if(!Utils.isEmpityCollection(kes)){
            for(KnEmployeeOrganization ke : kes){
                list.add(ke.getId().getEmp());
            }
        }
        //去除重复的用户数据
        list=Utils.removeDuplicate(list);
        return list;
    }
    /**
     * @param searchParams
     * @param dt
     *
     * @return
     */
    public DataTable<KnEmployee> PageKnEmployee(final Map<String,Object> searchParams,DataTable<KnEmployee> dt){
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        String[] column=new String[]{"loginName","userId","loginName","userName","email","job","userSystem"};
        Sort sort=new Sort(d,column[Integer.parseInt(dt.getiSortCol_0())]);
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        Page<KnEmployee> page=empDao.findAll(new Specification<KnEmployee>(){
            @Override public Predicate toPredicate(Root<KnEmployee> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(searchParams!=null&&searchParams.size()!=0){
                    //用户Id
                    if(searchParams.containsKey("LIKE_userId")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_userId").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("userId")),"%"+searchParams.get("LIKE_userId").toString().trim().toUpperCase()+"%"));
                    }
                    //登陆名
                    if(searchParams.containsKey("LIKE_loginName")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_loginName").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("loginName")),"%"+searchParams.get("LIKE_loginName").toString().trim().toUpperCase()+"%"));
                    }
                    //姓名
                    if(searchParams.containsKey("LIKE_userName")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_userName").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("userName")),"%"+searchParams.get("LIKE_userName").toString().trim().toUpperCase()+"%"));
                    }
                    //邮箱
                    if(searchParams.containsKey("LIKE_email")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_email").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("email")),"%"+searchParams.get("LIKE_email").toString().trim().toUpperCase()+"%"));
                    }
                    //在职
                    if(searchParams.containsKey("EQ_job")&&!Strings.isNullOrEmpty(searchParams.get("EQ_job").toString().trim())){
                        expressions.add(cb.equal(root.<IdEntity.ActiveType>get("job"),ActiveType.ENABLE.toString().equals(searchParams.get("EQ_job").toString())?ActiveType.ENABLE:ActiveType.DISABLE));
                    }
                    //来自系统
                    if(searchParams.containsKey("LIKE_userSystem")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_userSystem").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("userSystem")),"%"+searchParams.get("LIKE_userSystem").toString().trim().toUpperCase()+"%"));
                    }
                }
                return predicate;
            }
        },pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    @Transactional(readOnly=false)
    public void SaveKnEmployee(KnEmployee emp){
        empDao.save(emp);
    }
    @Transactional(readOnly=false)
    public void SaveKnEmployee(KnUser ku,KnEmployee emp,Long[] roles,Long[] teams,List<EmpOrg> empOrg,List<EmpPos> empPos){
        //要被删除的team的ID
        List<Long> teamDel=new ArrayList<>();
        ku.setName(emp.getUserName());
        rs.SaveKnUser(ku,roles);
        emp.setId(ku.getId());
        emp.setJob(ku.getStatus());
        if(emp.getOrg()!=null){
            emp.getOrg().clear();
        }else{
            emp.setOrg(new HashSet<KnEmployeeOrganization>());
        }
        if(empOrg!=null){
            for(EmpOrg eo : empOrg){
                emp.getOrg().add(new KnEmployeeOrganization(new KnEmployeeOrganizationId(new KnOrganization(eo.getId()),emp),eo.isCharge()?1:0,eo.isMajor()?1:0));
            }
        }
        if(emp.getPos()!=null){
            emp.getPos().clear();
        }else{
            emp.setPos(new HashSet<KnEmployeePosition>());
        }
        if(empPos!=null){
            for(EmpPos ep : empPos){
                emp.getPos().add(new KnEmployeePosition(new KnEmployeePositionId(new KnPosition(ep.getId()),emp),ep.isMajor()?ActiveType.ENABLE:ActiveType.DISABLE));
            }
        }
        if(emp.getTeam()!=null){
            List teamsArr=Arrays.asList(teams);
            //删除员工团队
            List removeTeam=new ArrayList();
            for(KnTeam team : emp.getTeam()){
                if(!teamsArr.contains(team.getId())&&team.getMaster().getId()!=emp.getId()){
                    //删除team中的员工
                    teamDel.add(team.getId());
                    removeTeam.add(team);
                }
            }
            emp.getTeam().removeAll(removeTeam);
        }else{
            emp.setTeam(new HashSet<KnTeam>());
            if(teams!=null){
                //保存员工团队
                for(Long id : teams){
                    KnTeam team=teamDao.findOne(id);
                    if(team!=null){
                        emp.getTeam().add(team);
                    }
                }
            }
        }
        //如果员工没有传输userId和userSystem,默认为新增uuid
        if(Strings.isNullOrEmpty(emp.getUserId())){
            emp.setUserId(UuidMaker.getInstance().getUuid(true));
        }
        empDao.save(emp);
        //更新Team
        if(!teamDel.isEmpty()){
            List<KnTeam> teamList=(List<KnTeam>)teamDao.findAll(teamDel);
            for(KnTeam team : teamList){
                team.getEmps().remove(emp);
            }
            teamDao.save(teamList);
        }
    }
    public KnUser ReadKnUser(Long id){
        return knUserDao.findOne(id);
    }
    public KnEmployee ReadKnEmployee(Long id){
        return empDao.findOne(id);
    }
    public KnEmployee ReadKnEmployee(String loginName){
        return empDao.findByLoginName(loginName);
    }
    @Transactional(readOnly=false)
    public String DeleteKnEmployee(Long id){
        KnEmployee ke=ReadKnEmployee(id);
        //添加非空判断-cici
        if(ke!=null){
            ke.getPos().clear();
            ke.getOrg().clear();
            ke.getTeam().clear();
            List<KnPositionBranchedPassage> list=kpbpd.findByLeaderIdOrSubordinateId(id,id);
            for(KnPositionBranchedPassage kpbp : list){
                kpbpd.delete(kpbp);
            }
            empDao.delete(ke);
            KnUser ku=knUserDao.findOne(id);
            if(ku!=null){
                ku.getRole().clear();
                knUserDao.delete(ku);
            }
            return ke.getLoginName();
        }
        return null;
    }
    /** 员工管理 end* */
    /** 团队管理 end* */
    /**
     * 获取所有团队
     *
     * @return
     */
    public List<KnTeam> ListTeam(){
        return (List<KnTeam>)this.teamDao.findAll();
    }
    /** 团队管理 end* */
    /**
     * 创建员工的对外API,能导入用户角色,员工组织,员工岗位,员工团队
     * 1 当orgs 不为null时,执行更新员工和组织关系
     * 2 当poss不为null时,执行更新员工和岗位关系
     * 3 当roles不为null时,执行更新用户角色关系
     * 4 当teams不为null时,执行更新员工团队关系
     *
     * @param ku    用户
     * @param emp   员工
     * @param orgs  组织
     * @param poss  岗位
     * @param roles 角色
     * @param teams 团队
     */
    @Transactional(readOnly=false)
    public KnEmployee SaveEmployee(KnUser ku,KnEmployee emp,EmpOrg[] orgs,EmpPos[] poss,Long[] roles,Long[] teams){
        return null;
    }
    /**
     * 根据员工id 查找id
     *
     * @param id
     *
     * @return
     */
    public KnEmployee ReadEmp(Long id){
        return this.empDao.findOne(id);
    }
    /**
     * 团队分页
     *
     * @param dataTable
     *
     * @return
     */
    public DataTable PageQueryTeam(DataTable dataTable,ResourceService resourceService){
        try{
            //请求第几页数据
            int pageNum=dataTable.getiDisplayStart();
            //每页数据量
            int iDisplayLength=dataTable.getiDisplayLength();
            String sSortDir_0=dataTable.getSortValue(), orderName=dataTable.getSortColName(), sSearch=dataTable.getsSearch();//asc
            sSearch="%"+sSearch+"%";
            Sort sort=null;
            if(orderName!=null&&!orderName.equals("")){
                if(sSortDir_0.equalsIgnoreCase("asc")){
                    sort=new Sort(Sort.Direction.ASC,orderName);
                }else if(sSortDir_0.equalsIgnoreCase("desc")){
                    sort=new Sort(Sort.Direction.DESC,orderName);
                }
            }
            Pageable pageable;
            if(orderName!=null&&!orderName.equals("")){
                pageable=new PageRequest(pageNum,iDisplayLength,sort);
            }else{
                pageable=new PageRequest(pageNum,iDisplayLength);
            }
            //调用分页查询方法
            Page<KnTeam> page_list=this.teamDao.queryTeam(sSearch,pageable);
            //组装分页json结果
            dataTable.setiTotalDisplayRecords(page_list.getTotalElements());
            dataTable.setiTotalRecords(page_list.getTotalElements());
            ArrayList<TeamBean> list=new ArrayList<>();
            for(KnTeam team : page_list.getContent()){
                KnUser user=null;
                KnEmployee emp=team.getMaster();
                if(emp!=null){
                    long id=team.getMaster().getId();
                    user=resourceService.ReadUser(id);
                }
                TeamBean bean=new TeamBean(team,user);
                list.add(bean);
            }
            dataTable.setAaData(list);
            return dataTable;
        }catch(Exception e){
            e.printStackTrace();
        }
        return dataTable;
    }
    /**
     * 由id查找团队
     *
     * @param id
     *
     * @return
     */
    public KnTeam ReadTeam(long id){
        return this.teamDao.findOne(id);
    }
    /**
     * 保存团队
     */
    public KnTeam SaveTeam(KnTeam team){
        return teamDao.save(team);
    }
    /**
     * 删除团队
     */
    public void DeleteTeam(Long id){
        teamDao.delete(id);
    }
    public List<KnOrganization> findAll(){
        return (List<KnOrganization>)orgDao.findAll();
    }
    //---下面的方法供其他模块调用
    public List<SimpleEmployeeDTO> ListEmployeeOrganization(Long empId){
        KnEmployeeOrganization keo=ReadOrganization(empId);
        List<SimpleEmployeeDTO> list=Lists.newArrayList();
        if(keo!=null){
            List<KnEmployeeOrganization> keoList=empOrgDao.findByIdOrgId(keo.getId().getOrg().getId());
            for(KnEmployeeOrganization eo : keoList){
                list.add(BeanMapper.map(eo.getId().getEmp(),SimpleEmployeeDTO.class));
            }
        }
        return list;
    }
    /**
     * 根据员工姓名获取员工列表-户所在组织的员工信息-仅在职员工
     *
     * @param empId 员工id
     *
     * @return
     */
    public List<SimpleEmployeeDTO> ListEmployeeEnableOrganization(Long empId){
        KnEmployeeOrganization keo=ReadOrganization(empId);
        List<SimpleEmployeeDTO> list=Lists.newArrayList();
        if(keo!=null){
            List<KnEmployeeOrganization> keoList=empOrgDao.findByIdOrgId(keo.getId().getOrg().getId());
            for(KnEmployeeOrganization eo : keoList){
                KnEmployee emp=eo.getId().getEmp();
                //在职人员才放入信息
                if(emp!=null&&ActiveType.ENABLE.equals(emp.getJob())){
                    list.add(BeanMapper.map(eo.getId().getEmp(),SimpleEmployeeDTO.class));
                }
            }
        }
        return list;
    }
    /**
     * 根据组织ID集合获取组织集合信息
     *
     * @param orgIds
     *
     * @return
     */
    public List<KnOrganization> ListKnOrganization(List<Long> orgIds){
        return (List<KnOrganization>)orgDao.findAll(orgIds);
    }
    /**
     * 根据部门的主键id获取该部门及部门下的所有员工信息--传入部门id，返回组织列表列表
     *
     * @param orgId
     *
     * @return
     */
    public List<KnOrganization> ListOrganizationAll(Long orgId){
        List<KnOrganization> list=Lists.newArrayList();
        KnOrganization ko=orgDao.findOne(orgId);
        for(KnOrganization _ko : orgDao.findByPathLike(ko.getPath()+"%")){
            for(KnEmployeeOrganization keo : empOrgDao.findByIdOrgId(_ko.getId())){
                list.add(keo.getId().getOrg());
            }
        }
        return list;
    }
    /**
     * @param empId
     *
     * @return
     */
    public KnEmployeeOrganization ReadOrganization(Long empId){
        return empOrgDao.findByIdEmpIdAndMajor(empId,1);
    }
    /**
     * 根据员工集合共聚所有员工的主部门和员工对应关系信息
     *
     * @param empIds
     *
     * @return
     */
    public List<KnEmployeeOrganization> ListOrganizationMajor(List<Long> empIds){
        return empOrgDao.findByIdEmpIdInAndMajor(empIds,1);
    }
    /**
     * 根据员工集合共聚所有员工的主部门和员工对应关系信息
     *
     * @param empId
     *
     * @return
     */
    public List<KnEmployeeOrganization> ListOrganizationByEmpId(Long empId){
        return empOrgDao.findByIdEmpId(empId);
    }
    /**
     * 通过部门名称跟员工名称模糊查询员工信息以及主部门信息
     *
     * @param orgName
     * @param empName
     *
     * @return
     */
    public List<KnEmployeeOrganization> ListKnEmployeeOrganization(String orgName,String empName){
        orgName="%"+orgName+"%";
        empName="%"+empName+"%";
        return empOrgDao.findByIdOrgNameLikeAndIdEmpUserNameLikeAndMajor(orgName,empName,1);
    }
    /**
     * 根据组织名称或者员工名或者员工登陆用户名获取员工集合
     *
     * @param orgName
     * @param empName
     * @param loginName
     *
     * @return
     */
    public List<KnEmployee> ListKnEmployeeLikeMore(final String orgName,final String empName,final String loginName){
        List<KnEmployee> kmo=empDao.findAll(new Specification<KnEmployee>(){
            @Override
            public Predicate toPredicate(Root<KnEmployee> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(!Strings.isNullOrEmpty(orgName)){
                    Subquery subQuery=query.subquery(KnEmployeeOrganization.class);
                    Root<KnEmployeeOrganization> subRoot=subQuery.from(KnEmployeeOrganization.class);
                    subQuery.select(subRoot.<KnEmployeeOrganizationId>get("id").<KnEmployee>get("emp").<Long>get("id"));
                    Predicate[] p=new Predicate[]{cb.equal(root.<Long>get("id"),subRoot.<KnEmployeeOrganizationId>get("id").<KnEmployee>get("emp").<Long>get("id")),
                            //为了代码切割
                            cb.like(subRoot.<KnEmployeeOrganizationId>get("id").<KnOrganization>get("org").<String>get("name"),"%"+orgName+"%")};
                    subQuery.where(p);
                    expressions.add(cb.exists(subQuery));
                }
                if(!Strings.isNullOrEmpty(empName)){
                    expressions.add(cb.like(root.<String>get("userName"),"%"+empName+"%"));
                }
                if(!Strings.isNullOrEmpty(loginName)){
                    expressions.add(cb.like(root.<String>get("loginName"),"%"+loginName+"%"));
                }
                return predicate;
            }
        });
        return kmo;
    }
    /**
     * 根据部门名字模糊查询所有组织人员
     *
     * @param orgName 组织名称
     *
     * @return
     */
    public List<KnEmployeeOrganization> ListEmployeeFromOrg(String orgName){
        return empOrgDao.findByIdOrgNameLike("%"+orgName+"%");
    }
    /**
     * 根据员工iD集合获取员工信息集合
     *
     * @param ids
     *
     * @return
     */
    public List<KnEmployee> ListEmployee(List<Long> ids){
        return (List<KnEmployee>)empDao.findAll(ids);
    }
    /**
     * 根据员工ID查询所有在职的员工信息
     *
     * @param ids
     *
     * @return
     */
    public List<KnEmployee> ListEmployeeInJob(final List<Long> ids){
        //return (List<KnEmployee>)empDao.findAll(ids);
        return empDao.findAll(new Specification<KnEmployee>(){
            @Override public Predicate toPredicate(Root<KnEmployee> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                expressions.add(root.<String>get("id").in(ids));
                expressions.add(cb.equal(root.<ActiveType>get("job"),ActiveType.ENABLE));
                return predicate;
            }
        });
    }
    /**
     * 根据员工ID集合分页获取员工信息
     *
     * @param ids
     * @param pageNo
     * @param pageSize
     *
     * @return
     */
    public List<KnEmployee> PageEmployee(final List<Long> ids,int pageNo,int pageSize){
        List<KnEmployee> list=empDao.findEmployee(ids);
        int begin=pageSize*(pageNo-1);
        int end=pageSize*pageNo;
        if(begin>list.size()){
            return new ArrayList<>();
        }else if(end>list.size()){
            return list.subList(begin,list.size());
        }else{
            return list.subList(begin,end);
        }
    }
    /**
     * 获取所有员工信息集合
     *
     * @param
     *
     * @return
     */
    public List<KnEmployee> ListEmployee(){
        return (List<KnEmployee>)empDao.findAll();
    }
    /**
     * ******
     * 获取所有用户信息
     *
     * @return
     */
    public List<KnUser> ListUser(){
        return (List<KnUser>)knUserDao.findAll();
    }
    /**
     * 根据员工姓名模糊查询员工集合-所有员工
     *
     * @param name 员工姓名
     *
     * @return
     */
    public List<KnEmployee> ListKnEmployeeLike(String name){
        return empDao.findByUserNameLike("%"+name+"%");
    }
    /**
     * 根据员工姓名模糊查询员工集合-仅在职员工
     *
     * @param name 员工姓名
     *
     * @return
     */
    public List<KnEmployee> ListKnEmployeeEnableLike(String name){
        List<KnEmployee> emps=ListKnEmployeeLike(name);
        List list=Lists.newArrayList();
        if(!Utils.isEmpityCollection(emps)){
            for(KnEmployee emp : emps){
                //仅加入在职员工
                if(ActiveType.ENABLE.equals(emp.getJob())){
                    list.add(emp);
                }
            }
        }
        return list;
    }
    /**
     * 根据员工的主键id获取该员工管辖的所有人员信息（当前人员的下属员工）--传入员工id，返回员工列表
     *
     * @param empId
     *
     * @return
     */
    public List<KnPositionBranchedPassage> ListKnPositionBranchedPassage(Long empId){
        return kpbpd.findByLeaderId(empId);
    }
    /**
     * 根据岗位分管表获取员工的分管领导
     *
     * @param empId
     *
     * @return
     */
    public KnPositionBranchedPassage ReadKnPositionBranchedPassage(Long empId){
        List<KnPositionBranchedPassage> list=kpbpd.findBySubordinateId(empId);
        return list.size()>0?list.get(0):null;
    }
    public List<KnPositionBranchedPassage> ListKnPositionBranchedPassage(){
        return (List<KnPositionBranchedPassage>)kpbpd.findAll();
    }
    /**
     * 通过员工ID集合与员工名字查询下属人员信息
     *
     * @param ids
     * @param empName
     *
     * @return
     */
    public List<KnPositionBranchedPassage> ListKnPositionBranchedPassage(final List<Long> ids,String empName){
        String likeName="%"+empName.toUpperCase()+"%";
        final String name=likeName;
        return kpbpd.findAll(new Specification<KnPositionBranchedPassage>(){
            @Override
            public Predicate toPredicate(Root<KnPositionBranchedPassage> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                expressions.add(root.<String>get("subordinateId").in(ids));
                if(!Strings.isNullOrEmpty(name)){
                    expressions.add(cb.like(cb.upper(root.<String>get("subordinateName")),name));
                }
                return predicate;
            }
        });
    }
    /**
     * 根据分管员工集合以及员工姓名模糊查询
     *
     * @param empId
     * @param empName
     *
     * @return
     */
    public Page<KnPositionBranchedPassage> PageKnPositionBranchedPassage(final List<Long> empId,final String empName,int pageNo,int pageSize){
        return kpbpd.findAll(new Specification<KnPositionBranchedPassage>(){
            @Override
            public Predicate toPredicate(Root<KnPositionBranchedPassage> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                expressions.add(root.<String>get("subordinateId").in(empId));
                if(!Strings.isNullOrEmpty(empName)){
                    expressions.add(cb.like(cb.upper(root.<String>get("subordinateName")),"%"+empName.toUpperCase()+"%"));
                }
                return predicate;
            }
        },new PageRequest(pageNo,pageSize));
    }
    /**
     * 查询除过自己得所有人员
     *
     * @param id
     * @param pageNo
     * @param pageSize
     *
     * @return
     */
    public List<KnEmployee> PageEmployeeNot(Long id,int pageNo,int pageSize){
        return empDao.findByIdNot(id,new PageRequest(pageNo,pageSize)).getContent();
    }
    public Page<KnUser> PageAuthorityKnUser(final List<Long> idsList,final Map<String,Object> searchParams,PageRequest pageRequest){
        Page<KnUser> list=knUserDao.findAll(new Specification<KnUser>(){
            @Override public Predicate toPredicate(Root<KnUser> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(!Utils.isEmpityCollection(idsList)){
                    Expression<Long> exp=root.get("id");
                    expressions.add(exp.in(idsList));
                }
                if(searchParams!=null&&searchParams.size()!=0){
                    //登录名
                    if(searchParams.containsKey("LIKE_loginName")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_loginName").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("loginName")),"%"+searchParams.get("LIKE_loginName").toString().trim().toUpperCase()+"%"));
                    }
                    //用户名
                    if(searchParams.containsKey("LIKE_name")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_name").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("name")),"%"+searchParams.get("LIKE_name").toString().trim().toUpperCase()+"%"));
                    }
                    //邮箱
                    if(searchParams.containsKey("LIKE_email")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_email").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("email")),"%"+searchParams.get("LIKE_email").toString().trim().toUpperCase()+"%"));
                    }
                    //账号状态
                    if(searchParams.containsKey("EQ_status")&&!Strings.isNullOrEmpty(searchParams.get("EQ_status").toString().trim())){
                        expressions.add(cb.equal(cb.upper(root.<String>get("status")),searchParams.get("EQ_status").toString().trim().toUpperCase()));
                    }
                }
                return predicate;
            }
        },pageRequest);
        return list;
    }
    /**
     * 根据员工ID集合获取员工信息和登陆信息
     *
     * @param ids
     *
     * @return
     */
    public List<KnEmployee> ListEmployeeOrganizationByIds(List<Long> ids){
        return empDao.findEmployeeUserByIds(ids);
    }
    /**
     * 根据员工ID集合获取用户信息和登陆信息
     *
     * @param ids
     *
     * @return
     */
    public List<KnUser> ListUserOrganizationByIds(List<Long> ids){
        return (List<KnUser>)knUserDao.findAll(ids);
    }
    /**
     * 员工查询,用户名忽略大小写查询,列表信息
     *
     * @param searchParams 查询参数
     * @param dt           列表信息
     * @param idsList
     *
     * @return
     */
    public DataTable<KnEmployee> QueryEmployeeList(final Map<String,Object> searchParams,DataTable<KnEmployee> dt,final List<Long> idsList){
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),getKnEmployeeSort(dt));
        Page<KnEmployee> list=empDao.findAll(new Specification<KnEmployee>(){
            @Override public Predicate toPredicate(Root<KnEmployee> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(null!=idsList){
                    Expression<Long> exp=root.get("id"); //root.<Long>get("id");
                    expressions.add(exp.in(idsList));
                }
                if(searchParams!=null&&searchParams.size()!=0){
                    //用户Id
                    if(searchParams.containsKey("LIKE_userId")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_userId").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("userId")),"%"+searchParams.get("LIKE_userId").toString().trim().toUpperCase()+"%"));
                    }
                    //登陆名
                    if(searchParams.containsKey("LIKE_loginName")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_loginName").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("loginName")),"%"+searchParams.get("LIKE_loginName").toString().trim().toUpperCase()+"%"));
                    }
                    //姓名
                    if(searchParams.containsKey("LIKE_userName")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_userName").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("userName")),"%"+searchParams.get("LIKE_userName").toString().trim().toUpperCase()+"%"));
                    }
                    //邮箱
                    if(searchParams.containsKey("LIKE_email")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_email").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("email")),"%"+searchParams.get("LIKE_email").toString().trim().toUpperCase()+"%"));
                    }
                    //在职
                    if(searchParams.containsKey("EQ_job")&&!Strings.isNullOrEmpty(searchParams.get("EQ_job").toString().trim())){
                        expressions.add(cb.equal(root.<IdEntity.ActiveType>get("job"),IdEntity.ActiveType.valueOf(searchParams.get("EQ_job").toString())));
                    }
                    //来自系统
                    if(searchParams.containsKey("LIKE_userSystem")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_userSystem").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("userSystem")),"%"+searchParams.get("LIKE_userSystem").toString().trim().toUpperCase()+"%"));
                    }
                }
                return predicate;
            }
        },pageRequest);
        dt.setiTotalDisplayRecords(list.getTotalElements());
        dt.setAaData(list.getContent());
        return dt;
    }
    private Sort getKnEmployeeSort(DataTable<KnEmployee> dt){
        dt.setiSortCol_0("0");
        dt.setsSortDir_0(Sort.Direction.ASC.toString());
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        String[] column=new String[]{"userId","loginName","userName","email","job","userSystem"};
        return new Sort(d,column[Integer.parseInt(dt.getiSortCol_0())]);
    }
    /**
     * 根据PATH获取所有下属组织
     *
     * @param path
     *
     * @return
     */
    public List<KnOrganization> FindLowerOrganListByPath(String path){
        return orgDao.findLowerOrganListByPath(path);
    }
    /**
     * TODO WANGYIFAN
     * 根据组织id集合获取员工对应员工的集合
     *
     * @param orgIdList 组织id集合
     *
     * @return List<KnEmployee> 返回符合条件的所有员工信息
     */
    public List<KnEmployee> findEmpListByOrgIdList(List<Long> orgIdList){
        return empDao.findEmpListByOrgIdList(orgIdList);
    }
    /**
     * 根据PATH获取所有下属组织
     *
     * @param path
     *
     * @return
     */
    public List<KnOrganization> findLowerOrganListByPath(String path){
        return orgDao.findLowerOrganListByPath(path);
    }
    /**
     * 根据ldap用户数据进行用户更新操作,更新原则,以后台数据为准,ldap那边的用户信息只进行新增操作.
     * 组织架构也是一样的
     *
     * @param lus ldap用户集合
     */
    @Transactional(readOnly=false) public void syncLdapUser(final List<LdapUser> lus){
        final List<Long> user=Lists.newArrayList();
        final List<Long> org=Lists.newArrayList();
        for(LdapUser lu : lus){
            if(lu==null){
                continue;
            }
            if(!Strings.isNullOrEmpty(lu.getLoginName())){
                KnUser ku=knUserDao.findByLoginName(lu.getLoginName());
                //员工不存在的时候才会新增,不执行更新和删除操作
                if(ku==null){
                    List<KnOrganization> orgList=null;
                    if(lu.getDepartment()==null||"".equals(lu.getDepartment())||"null".equals(lu.getDepartment().trim())){
                        //选择缺省的部门
                        orgList=orgDao.findOrgByCode("缺省");
                        lu.setDepartment("缺省");
                        //     continue;
                    }else{
                        orgList=orgDao.findOrgByCode(lu.getDepartment());
                    }
                    // List<KnOrganization> orgList=orgDao.findByName(lu.getDepartment());
                    //修改为查询编码code作为唯一性
                    KnOrganization ko;
                    if(orgList!=null&&orgList.size()>0){
                        ko=orgList.get(0);
                    }else{
                        ko=new KnOrganization();
                        ko.setActive(IdEntity.ActiveType.ENABLE);
                        ko.setName(lu.getDepartment());
                        //                    ko.setCode(lu.getDepartment().length()>10?lu.getDepartment().substring(0,10):lu.getDepartment());
                        //modify by cici 20150428
                        ko.setCode(lu.getDepartment());
                        ko.setSupId(1L);
                        ko.setDepth(2L);
                        ko.setOrgType(KnOrganization.OrgType.DEPARTMENT);
                        ko.setSeq(100L);
                        orgDao.save(ko);
                        ko.setPath("1."+ko.getId()+".");
                        orgDao.save(ko);
                    }
                    org.add(ko.getId());
                    KnEmployee ke;
                    ku=new KnUser();
                    ku.setSalt("7efbd59d9741d34f");
                    ku.setLoginName(lu.getLoginName());
                    ku.setPassword("691b14d79bf0fa2215f155235df5e670b64394cc");
                    ku.setUserOnline(0);
                    ku.getRole().add(new KnRole(3L));
                    ke=new KnEmployee();
                    ke.setLoginName(lu.getLoginName());
                    ke.setJob(IdEntity.ActiveType.ENABLE);
                    ke.setUserType("employee");
                    ke.setSex(KnEmployee.Gender.NONE);
                    ke.setUserSystem("BGITECH");
                    ku.setEmail(lu.getEmail());
                    ke.setEmail(lu.getEmail());
                    ku.setName(lu.getUserName());
                    ke.setUserName(ku.getName());
                    ke.setJob(IdEntity.ActiveType.ENABLE);
                    ku.setStatus(IdEntity.ActiveType.ENABLE);
                    knUserDao.save(ku);
                    ke.setId(ku.getId());
                    user.add(ku.getId());
                    ke.setUserId(lu.getUserId());
                    ke.setTelephone(lu.getTelephoneNumber());
                    ke.setPhone(lu.getMobile());
                    if(ke.getOrg()!=null){
                        //hibernate延迟加载属性
                        Hibernates.initLazyProperty(ke.getOrg());
                        ke.getOrg().clear();
                    }else{
                        ke.setOrg(new HashSet<KnEmployeeOrganization>());
                    }
                    ke.getOrg().add(new KnEmployeeOrganization(new KnEmployeeOrganizationId(new KnOrganization(ko.getId()),ke),0,1));
                    empDao.save(ke);
                }else{//添加平台存在的id数据,不更新此数据
                    user.add(ku.getId());
                }
            }
        }
        //由于华大接口返回的数据很多用户都是不正确的,很多在职的员工却没有返回,所以离职状态先不设置,需要他们自己手动到后台进行设置
        //modify by cici 20150428
        /*
        if(user.size()>0){
            //查找员工不存在的,将状态设置为离职和禁用
            List<KnEmployee> employeeList=empDao.findByIdNotIn(user);
            for(KnEmployee ke : employeeList){
                //TODO cici 20150427判断来自系统是华大的,就设置,非华大的不设置
                if("BGITECH".equalsIgnoreCase(ke.getUserSystem())){//华大
                    ke.setJob(IdEntity.ActiveType.DISABLE);
                    empDao.save(ke);
                }else{//去除非华大人员
                    user.add(ke.getId());
                }
            }
            List<KnUser> userList=knUserDao.findByIdNotIn(user);
            for(KnUser ku : userList){
                if(!"admin".equals(ku.getLoginName())){
                    ku.setStatus(IdEntity.ActiveType.DISABLE);
                    knUserDao.save(ku);
                }
            }
        }*/
        //去除组织架构的状态设置, 因为华大的组织架构几乎都是错误的,所以让平台自己进行设定. modify by cici 20150428
        /*
        if(org.size()>0){
            //将原来存在的组织而远程不存在的组织架构信息变为不可用
            List<KnOrganization> organizationList=orgDao.findBySupIdNotAndIdNotIn(0L,org);
            for(KnOrganization ko : organizationList){
                ko.setActive(IdEntity.ActiveType.DISABLE);
                //解除组织下面的员工关系
                for(KnEmployeeOrganization keo : empOrgDao.findByIdOrgId(ko.getId())){
                    empOrgDao.delete(keo);
                }
                orgDao.save(ko);
            }
        }*/
    }
    /**
     * 备份的原来的逻辑数据,如果不需要,后期以下方法可删除--modify by cici 20150427
     * 根据ldap用户数据进行用户更新操作,更新原则,以后台数据为准,ldap那边的用户信息只进行新增操作.
     * 组织架构也是一样的
     *
     * @param lus ldap用户集合
     */
    @Transactional(readOnly=false) private void syncLdapUserTemp(final List<LdapUser> lus){
        final List<Long> user=Lists.newArrayList();
        final List<Long> org=Lists.newArrayList();
        for(LdapUser lu : lus){
            if(lu==null){
                continue;
            }
            if(!Strings.isNullOrEmpty(lu.getDepartment())){
                if(lu.getDepartment()==null||"".equals(lu.getDepartment())||"null".equals(lu.getDepartment().trim())){
                    continue;
                }
                List<KnOrganization> orgList=orgDao.findByName(lu.getDepartment());
                KnOrganization ko;
                if(orgList.size()>0){
                    ko=orgList.get(0);
                }else{
                    ko=new KnOrganization();
                    ko.setActive(IdEntity.ActiveType.ENABLE);
                    ko.setName(lu.getDepartment());
                    ko.setCode(lu.getDepartment().length()>10?lu.getDepartment().substring(0,10):lu.getDepartment());
                    ko.setSupId(1L);
                    ko.setDepth(2L);
                    ko.setOrgType(KnOrganization.OrgType.DEPARTMENT);
                    ko.setSeq(100L);
                    orgDao.save(ko);
                    ko.setPath("1."+ko.getId()+".");
                    orgDao.save(ko);
                }
                org.add(ko.getId());
                KnUser ku=knUserDao.findByLoginName(lu.getLoginName());
                KnEmployee ke;
                if(ku==null){
                    ku=new KnUser();
                    ku.setSalt("7efbd59d9741d34f");
                    ku.setLoginName(lu.getLoginName());
                    ku.setPassword("691b14d79bf0fa2215f155235df5e670b64394cc");
                    ku.setUserOnline(0);
                    ku.getRole().add(new KnRole(3L));
                    ke=new KnEmployee();
                    ke.setLoginName(lu.getLoginName());
                    ke.setJob(IdEntity.ActiveType.ENABLE);
                    ke.setUserType("employee");
                    ke.setSex(KnEmployee.Gender.NONE);
                    ke.setUserSystem("BGITECH");
                }else{
                    ke=empDao.findOne(ku.getId());
                }
                ku.setEmail(lu.getEmail());
                ke.setEmail(lu.getEmail());
                ku.setName(lu.getUserName());
                ke.setUserName(ku.getName());
                ke.setJob(IdEntity.ActiveType.ENABLE);
                ku.setStatus(IdEntity.ActiveType.ENABLE);
                knUserDao.save(ku);
                ke.setId(ku.getId());
                user.add(ku.getId());
                ke.setUserId(lu.getUserId());
                ke.setTelephone(lu.getTelephoneNumber());
                ke.setPhone(lu.getMobile());
                if(ke.getOrg()!=null){
                    Hibernates.initLazyProperty(ke.getOrg());
                    ke.getOrg().clear();
                }else{
                    ke.setOrg(new HashSet<KnEmployeeOrganization>());
                }
                ke.getOrg().add(new KnEmployeeOrganization(new KnEmployeeOrganizationId(new KnOrganization(ko.getId()),ke),0,1));
                empDao.save(ke);
            }
        }
        if(user.size()>0){
            List<KnEmployee> employeeList=empDao.findByIdNotIn(user);
            for(KnEmployee ke : employeeList){
                ke.setJob(IdEntity.ActiveType.DISABLE);
                empDao.save(ke);
            }
            List<KnUser> userList=knUserDao.findByIdNotIn(user);
            for(KnUser ku : userList){
                if(!"admin".equals(ku.getLoginName())){
                    ku.setStatus(IdEntity.ActiveType.DISABLE);
                    knUserDao.save(ku);
                }
            }
        }
        if(org.size()>0){
            List<KnOrganization> organizationList=orgDao.findBySupIdNotAndIdNotIn(0L,org);
            for(KnOrganization ko : organizationList){
                ko.setActive(IdEntity.ActiveType.DISABLE);
                orgDao.save(ko);
            }
        }
    }
    @Autowired
    public void setKnUserDao(KnUserDao knUserDao){
        this.knUserDao=knUserDao;
    }
    @Autowired
    public void setEmpDao(KnEmployeeDao empDao){
        this.empDao=empDao;
    }
    @Autowired
    public void setOrgDao(KnOrganizationDao orgDao){
        this.orgDao=orgDao;
    }
    @Autowired
    public void setEmpPosDao(KnEmployeePositionDao empPosDao){
        this.empPosDao=empPosDao;
    }
    @Autowired
    public void setEmpOrgDao(KnEmployeeOrganizationDao empOrgDao){
        this.empOrgDao=empOrgDao;
    }
    @Autowired
    public void setPosDao(KnPositionDao posDao){
        this.posDao=posDao;
    }
    @Autowired
    public void setTeamDao(KnTeamDao teamDao){
        this.teamDao=teamDao;
    }
    @Autowired
    public void setKpbpd(KnPositionBranchedPassageDao kpbpd){
        this.kpbpd=kpbpd;
    }
    @Autowired
    public void setRs(ResourceService rs){
        this.rs=rs;
    }
}
