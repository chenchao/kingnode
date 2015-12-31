package com.kingnode.xsimple.service;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.common.base.Strings;
import com.kingnode.diva.persistence.DynamicSpecifications;
import com.kingnode.diva.persistence.SearchFilter;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.dao.meeting.KnMeetingInfoDao;
import com.kingnode.xsimple.dao.meeting.KnRegisterInfoDao;
import com.kingnode.xsimple.entity.meeting.KnMeetingInfo;
import com.kingnode.xsimple.entity.meeting.KnMeetingRegisterInfo;
import com.kingnode.xsimple.entity.meeting.KnRegisterInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Component @Transactional(readOnly=true)
public class MeetService{
    private KnMeetingInfoDao mid;
    private KnRegisterInfoDao rid;
    @Autowired
    public void setMeetingInfoDao(KnMeetingInfoDao mid){
        this.mid=mid;
    }
    @Autowired
    public void setRegisterInfoDao(KnRegisterInfoDao rid){
        this.rid=rid;
    }
    public List<KnMeetingInfo> ListMeetingInfos(){
        return (List<KnMeetingInfo>)mid.findAll();
    }
    public DataTable<KnRegisterInfo> PageRegisterInfos(Map<String,Object> searchParams,DataTable<KnRegisterInfo> dt){
        Sort sort=getSort(dt);
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        Map<String,SearchFilter> filters=SearchFilter.parse(searchParams);
        Specification<KnRegisterInfo> spec=DynamicSpecifications.bySearchFilter(filters.values());
        Page<KnRegisterInfo> page=rid.findAll(spec,pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    public DataTable<KnRegisterInfo> PageAttendees(final String theme,final String name,final String sex,final String phone,DataTable<KnRegisterInfo> dt){
        Pageable pageable=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),getSort(dt));
        Page<KnRegisterInfo> page=rid.findAll(new Specification<KnRegisterInfo>(){
            @Override
            public Predicate toPredicate(Root<KnRegisterInfo> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                Root<KnMeetingRegisterInfo> r=cq.from(KnMeetingRegisterInfo.class);
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                expressions.add(cb.equal(root.<Long>get("id"),r.<Long>get("registerId")));
                expressions.add(cb.equal(r.<String>get("meetingId"),theme));
                if(!Strings.isNullOrEmpty(name)){
                    expressions.add(cb.like(root.<String>get("name"),"%"+name+"%"));
                }
                if(!Strings.isNullOrEmpty(sex)){
                    expressions.add(cb.equal(root.<String>get("sex"),sex));
                }
                if(!Strings.isNullOrEmpty(phone)){
                    expressions.add(cb.like(root.<String>get("phone"),"%"+phone+"%"));
                }
                return predicate;
            }
        },pageable);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    private Sort getSort(DataTable<KnRegisterInfo> dt){
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        String[] column=new String[]{"id","name","sex","phone","email","company","cusmger"};
        return new Sort(d,column[Integer.parseInt(dt.getiSortCol_0())]);
    }
}