package com.kingnode.affairs.service;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.common.collect.Lists;
import com.kingnode.affairs.dao.KnAffairsPersonDao;
import com.kingnode.affairs.dao.KnDepartmentDao;
import com.kingnode.affairs.dto.ReturnListDTO;
import com.kingnode.affairs.entity.KnAffairsPerson;
import com.kingnode.affairs.entity.KnDepartment;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 *         考勤管理service
 */
@Component @Transactional(readOnly=false)
public class AffairsService{
    private static Logger logger=LoggerFactory.getLogger(AffairsService.class);
    private KnAffairsPersonDao personDao;
    private KnDepartmentDao dDao;
    public KnAffairsPerson ReadKnAffairsPerson(Long id){
        return personDao.findOne(id);
    }
    public KnAffairsPerson SaveKnAffairsPerson(KnAffairsPerson person){
        return personDao.save(person);
    }
    public void DeleteKnAffairsPerson(Long id){
        KnAffairsPerson person=ReadKnAffairsPerson(id);
        String departmentName=person.getDepartmentName();
        personDao.delete(id);
        List<KnAffairsPerson> persons=personDao.findByDepartmentName(departmentName);
        if(persons.size()==0){
            KnDepartment department=findByDname(departmentName);
            if(department!=null){
                dDao.delete(department);
            }
        }
    }
    public KnDepartment findByDname(String dname){
        return dDao.findByDepartmentName(dname);
    }
    public KnDepartment SaveKnDepartment(KnDepartment departName){
        return dDao.save(departName);
    }
    public DataTable<KnAffairsPerson> PageKnAffairsPerson(final Map<String,Object> searchParams,DataTable<KnAffairsPerson> dt,Sort sort){
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        Page<KnAffairsPerson> page=this.personDao.findAll(new Specification<KnAffairsPerson>(){
            @Override
            public Predicate toPredicate(Root<KnAffairsPerson> knResourceRoot,CriteriaQuery<?> criteriaQuery,CriteriaBuilder criteriaBuilder){
                Predicate predicate=criteriaBuilder.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(searchParams.size()!=0){
                    if(!Utils.isEmptyStr(searchParams.get("LIKE_affairsName"))){
                        expressions.add(criteriaBuilder.like(knResourceRoot.<String>get("affairsName"),"%"+searchParams.get("LIKE_affairsName").toString()+"%"));
                    }
                    if(!Utils.isEmptyStr(searchParams.get("LIKE_departmentName"))){
                        expressions.add(criteriaBuilder.like(knResourceRoot.<String>get("departmentName"),"%"+searchParams.get("LIKE_departmentName").toString()+"%"));
                    }
                }
                return predicate;
            }
        },pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setiTotalRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    /** =============reset接口开始========== */
    public ReturnListDTO<KnAffairsPerson> PageKnAffairsPerson(Integer pageNo,Integer pageSize,final Long id,String name){
        KnDepartment department=this.dDao.findOne(id);
        if(department==null){
            return new ReturnListDTO<>();
        }
        String departmentName=department.getDepartmentName();
        return PageKnAffairsPerson(pageNo,pageSize,departmentName,name);
    }
    /**
     * 列出文件列表,接口调用
     *
     * @param pageNo
     * @param pageSize
     *
     * @return
     */
    public ReturnListDTO<KnAffairsPerson> PageKnAffairsPerson(Integer pageNo,Integer pageSize,final String departmentName,final String name){
        ReturnListDTO<KnAffairsPerson> dto=new ReturnListDTO();
        List<KnAffairsPerson> result=null;
        Page<KnAffairsPerson> page=null;
        PageRequest pageRequest=new PageRequest(pageNo,pageSize,null);
        page=this.personDao.findAll(new Specification<KnAffairsPerson>(){
            @Override
            public Predicate toPredicate(Root<KnAffairsPerson> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                List<Predicate> predicates=Lists.newArrayList();
                if(!Utils.isEmptyStr(departmentName)){
                    predicates.add(cb.equal(root.<String>get("departmentName"),departmentName));
                }
                if(!Utils.isEmptyStr(name)){
                    predicates.add(cb.like(root.<String>get("name"),"%"+name+"%"));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        },pageRequest);
        dto.setList(page.getContent());
        dto.setTotalSize(page.getTotalElements());
        dto.setStatus(true);
        return dto;
    }
    public ReturnListDTO<KnDepartment> PageKnDepartment(Integer pageNo,Integer pageSize,final String departmentName){
        ReturnListDTO<KnDepartment> dto=new ReturnListDTO();
        PageRequest pageRequest=new PageRequest(pageNo,pageSize,null);
        Page<KnDepartment> page=this.dDao.findAll(new Specification<KnDepartment>(){
            @Override
            public Predicate toPredicate(Root<KnDepartment> knResourceRoot,CriteriaQuery<?> criteriaQuery,CriteriaBuilder criteriaBuilder){
                Predicate predicate=criteriaBuilder.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(!Utils.isEmptyStr(departmentName)){
                    expressions.add(criteriaBuilder.equal(knResourceRoot.<String>get("departmentName"),"%"+departmentName+"%"));
                }
                return predicate;
            }
        },pageRequest);
        dto.setList(page.getContent());
        dto.setTotalSize(page.getTotalElements());
        dto.setStatus(true);
        return dto;
    }
    @Autowired
    public void setDao(KnAffairsPersonDao dao){
        this.personDao=dao;
    }
    @Autowired
    public void setdDao(KnDepartmentDao dDao){
        this.dDao=dDao;
    }
}
