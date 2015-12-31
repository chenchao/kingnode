package com.kingnode.xsimple.service.system;

import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.common.base.Strings;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.dao.system.KnTeamDao;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnTeam;
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
public class TeamService{

    private KnTeamDao knTeamDao ;

    @Autowired
    public void setKnTeamDao(KnTeamDao knTeamDao){
        this.knTeamDao=knTeamDao;
    }

    public DataTable<KnTeam> SearchKnTeamList(final Map<String,Object> searchParams ,DataTable<KnTeam> dt){
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),getSort(dt));
        Page<KnTeam> list=knTeamDao.findAll(new Specification<KnTeam>(){
            @Override public Predicate toPredicate(Root<KnTeam> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();

                Root<KnEmployee> knEmployeeRoot=query.from(KnEmployee.class);
                expressions.add(cb.equal(root.<KnEmployee>get("master"),knEmployeeRoot.<Long>get("id")));

                if(searchParams!=null&&searchParams.size()!=0){
                    if(searchParams.containsKey("LIKE_code")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_code").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("code")),"%"+searchParams.get("LIKE_code").toString().trim().toUpperCase()+"%"));
                    }
                    //用户名
                    if(searchParams.containsKey("LIKE_name")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_name").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("name")),"%"+searchParams.get("LIKE_name").toString().trim().toUpperCase()+"%"));
                    }
                    if(searchParams.containsKey("LIKE_description")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_description").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("description")),"%"+searchParams.get("LIKE_description").toString().trim().toUpperCase()+"%"));
                    }
                    if(searchParams.containsKey("LIKE_master.loginName")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_master.loginName").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("master.loginName")),"%"+searchParams.get("LIKE_master.loginName").toString().trim().toUpperCase()+"%"));
                    }
                    if(searchParams.containsKey("LIKE_master.userName")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_master.userName").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("master.userName")),"%"+searchParams.get("LIKE_master.userName").toString().trim().toUpperCase()+"%"));
                    }
                    if(searchParams.containsKey("EQ_active")&&!Strings.isNullOrEmpty(searchParams.get("EQ_active").toString().trim())){
                        expressions.add(cb.equal(root.<Setting.ActiveType>get("active"),Setting.ActiveType.valueOf(searchParams.get("EQ_active").toString())));
                    }
                }
                return predicate;
            }
        },pageRequest);
        dt.setiTotalDisplayRecords(list.getTotalElements());
        dt.setAaData(list.getContent());
        return dt;
    }

    /**
     * 根据团队的id获得团队里面的员工人员
     * @param id
     * @return
     */
    public KnTeam FindKnTeam(Long id){
        return knTeamDao.findOne(id) ;
    }

    @Transactional(readOnly=false)
    public void saveKnTeam(KnTeam knTeam)throws Exception{
        knTeamDao.save(knTeam) ;
    }


    @Transactional(readOnly=false)
    public void DeleteKnTeam(List<Long> tIds)throws Exception{
        List<KnTeam> list=(List<KnTeam>)knTeamDao.findAll(tIds);
        knTeamDao.delete(list);
    }

    private Sort getSort(DataTable<KnTeam> dt){
        dt.setiSortCol_0("0");
        dt.setsSortDir_0(Sort.Direction.ASC.toString());
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        String[] column=new String[]{"name","description","master.loginName","master.userName"};
        return new Sort(d,column[Integer.parseInt(dt.getiSortCol_0())]);
    }

}
