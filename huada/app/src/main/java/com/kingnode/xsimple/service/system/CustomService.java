package com.kingnode.xsimple.service.system;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.common.base.Strings;
import com.kingnode.diva.mapper.BeanMapper;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.diva.persistence.DynamicSpecifications;
import com.kingnode.diva.persistence.SearchFilter;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.dao.system.KnCustomServiceInfoDao;
import com.kingnode.xsimple.dao.system.KnEmployeeDao;
import com.kingnode.xsimple.dao.system.KnSystemPropertieInfoDao;
import com.kingnode.xsimple.dto.SimpleEmployeeDTO;
import com.kingnode.xsimple.entity.system.KnCustomServiceInfo;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnSystemPropertieInfo;
import com.kingnode.xsimple.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author dengfeng@kingnode.com (dengfeng)
 */
@Component @Transactional(readOnly=true)
public class CustomService{
    private KnSystemPropertieInfoDao sp;
    private KnCustomServiceInfoDao cs;
    private KnEmployeeDao emp;
    @Autowired
    public void setCs(KnCustomServiceInfoDao cs){
        this.cs=cs;
    }
    @Transactional(readOnly=false)
    public void Delete(Long id){
        cs.delete(id);
    }
    @Transactional(readOnly=false)
    public void DeleteAll(Long[] ids){
        for(Long id : ids){
            cs.delete(id);
        }
    }
    @Transactional(readOnly=false)
    public void SaveCustomServiceInfoList(List<KnCustomServiceInfo> kcs){
        cs.save(kcs);
    }
    public DataTable<KnCustomServiceInfo> PageCustomServiceInfos(Map<String,Object> searchParams,DataTable<KnCustomServiceInfo> dt){
        Sort sort=getSort(dt);
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        Map<String,SearchFilter> filters=SearchFilter.parse(searchParams);
        Specification<KnCustomServiceInfo> spec=DynamicSpecifications.bySearchFilter(filters.values());
        Page<KnCustomServiceInfo> page=cs.findAll(spec,pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    public DataTable<KnEmployee> FindAllUsers(DataTable<KnEmployee> dt,final Map<String,Object> searchParams){
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength());
        List<KnEmployee> employees=emp.findKnEmployeeAllByAllAttribute();
        List<Long> ids=new ArrayList<>();
        for(KnEmployee employee:employees){
            ids.add(employee.getId());
        }
        ids.add(0L);
        final List<Long> fids=ids;
        Page<KnEmployee> ls=emp.findAll(new Specification<KnEmployee>(){
            @Override public Predicate toPredicate(Root<KnEmployee> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                expressions.add(root.<Long>get("id").in(fids));
                if(searchParams.containsKey("LIKE_fullName")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_fullName").toString())){
                    expressions.add(cb.like(cb.upper(root.<String>get("userName")),"%"+searchParams.get("LIKE_fullName").toString().trim().toUpperCase()+"%"));
                }
                if(searchParams.containsKey("LIKE_userId")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_userId").toString())){
                    expressions.add(cb.like(cb.upper(root.<String>get("userId")),"%"+searchParams.get("LIKE_userId").toString().trim().toUpperCase()+"%"));
                }
                if(searchParams.containsKey("LIKE_userType")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_userType").toString())){
                    expressions.add(cb.like(cb.upper(root.<String>get("userType")),"%"+searchParams.get("LIKE_userType").toString().trim().toUpperCase()+"%"));
                }
                return predicate;
            }
        },pageRequest);
        dt.setiTotalDisplayRecords(ls.getTotalElements());
        dt.setAaData(ls.getContent());
        return dt;
    }
    /**
     * @param params
     *
     * @return
     */
    public List<SimpleEmployeeDTO> FindUsers(String params) throws Exception{
        String pageNo=null;
        String pageSize=null;
        try{
            Map<String,Object> map=JsonMapper.nonDefaultMapper().fromJson(params,HashMap.class);
            pageNo=map.containsKey("pageNum")?(String)map.get("pageNum"):null;
            pageSize=map.containsKey("pageSize")?(String)map.get("pageSize"):null;
        }catch(Exception e){
            // e.printStackTrace();
        }
        return FindUsers(pageNo,pageSize);
    }
    /**
     * 获取人员
     *
     * @param pageNo
     * @param pageSize
     *
     * @return
     */
    public List<SimpleEmployeeDTO> FindUsers(String pageNo,String pageSize) throws Exception{
        List<SimpleEmployeeDTO> dtos=new ArrayList<>();
        if(pageNo==null&&pageSize==null){
            List<KnEmployee> ls=(List<KnEmployee>)emp.findAll();
            dtos.addAll(tranceForEmpDTO(ls));
        }else{
            Sort.Direction d=Sort.Direction.DESC;
            Sort sort=new Sort(d,"id");
            PageRequest pageRequest=new PageRequest(Integer.parseInt(pageNo)-1,Integer.parseInt(pageSize),sort);
            Map<String,SearchFilter> filters=SearchFilter.parse(new HashMap<String,Object>());
            Specification<KnEmployee> spec=DynamicSpecifications.bySearchFilter(filters.values());
            Page<KnEmployee> page=emp.findAll(spec,pageRequest);
            List<KnEmployee> employees=page.getContent();
            dtos.addAll(tranceForEmpDTO(employees));
        }
        return dtos;
    }
    /**
     * 把emp对象转换成DTO对象
     *
     * @param employees
     *
     * @return
     */
    private List<SimpleEmployeeDTO> tranceForEmpDTO(List<KnEmployee> employees){
        List<SimpleEmployeeDTO> dtos=new ArrayList<>();
        if(employees==null||employees.size()==0){
            return dtos;
        }
        for(KnEmployee emp : employees){
            SimpleEmployeeDTO dto=BeanMapper.map(emp,SimpleEmployeeDTO.class);
            dto.setUserName(emp.getUserName());
            dtos.add(dto);
        }
        return dtos;
    }
    public List<KnEmployee> FindByIds(Long[] ids){
        return emp.findByIds(ids);
    }
    public List<KnEmployee> findAll(String userType){
        return emp.findAll(userType);
    }
    private Sort getSort(DataTable<KnCustomServiceInfo> dt){
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        String[] column=new String[]{"id","fullName","uaccount","userId","userType","fromSys"};
        return new Sort(d,column[Integer.parseInt(dt.getiSortCol_0())]);
    }
    public List<KnCustomServiceInfo> FindKnCustomServiceInfo(String userId,String fromSys){
        return cs.findByUserIdAndFromSys(userId,fromSys);
    }
    public List<KnSystemPropertieInfo> ReadSystemPropertieInfo(){
        return (List<KnSystemPropertieInfo>)sp.findAll();
    }
    @Transactional(readOnly=false)
    public void SaveSystemPropertieInfo(KnSystemPropertieInfo sysinfo){
        sp.save(sysinfo);
    }
    public KnSystemPropertieInfo FindOneSystemPropertieInfo(Long id){
        return sp.findOne(id);
    }
    @Autowired
    public void setEmp(KnEmployeeDao emp){
        this.emp=emp;
    }
    @Autowired
    public void setSp(KnSystemPropertieInfoDao sp){
        this.sp=sp;
    }
    public List<KnCustomServiceInfo> FindCustomServices(){
        return (List<KnCustomServiceInfo>)cs.findAll();
    }
}
