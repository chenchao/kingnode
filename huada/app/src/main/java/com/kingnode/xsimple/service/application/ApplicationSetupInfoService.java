package com.kingnode.xsimple.service.application;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.dao.application.KnApplicationInfoDao;
import com.kingnode.xsimple.dao.application.KnApplicationSetUpInfoDao;
import com.kingnode.xsimple.entity.application.KnApplicationInfo;
import com.kingnode.xsimple.entity.application.KnApplicationSetupInfo;
import com.kingnode.xsimple.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * 应用设置管理
 *
 * @wangyifan
 */
@Component @Transactional(readOnly=true)
public class ApplicationSetupInfoService{
    private KnApplicationInfoDao knApplicationInfoDao;
    private KnApplicationSetUpInfoDao knApplicationSetUpInfoDao;
    private static Logger logger=LoggerFactory.getLogger(ApplicationSetupInfoService.class);
    @Autowired
    public void setKnApplicationInfoDao(KnApplicationInfoDao knApplicationInfoDao){
        this.knApplicationInfoDao=knApplicationInfoDao;
    }
    @Autowired
    public void setKnApplicationSetUpInfoDao(KnApplicationSetUpInfoDao knApplicationSetUpInfoDao){
        this.knApplicationSetUpInfoDao=knApplicationSetUpInfoDao;
    }
    /**
     * 根据ids 删除多个应用设置信息
     *
     * @param ids 如 123,456
     */
    @Transactional(readOnly=false)  //  关闭只读 ,对数据库有操作
    public void DeleteAllKnVserionInfoByIds(List<Long> ids){
        List<KnApplicationSetupInfo> list=(List<KnApplicationSetupInfo>)knApplicationSetUpInfoDao.findAll(ids);
        knApplicationSetUpInfoDao.delete(list);
    }
    /**
     * 根据id 删除单个应用设置信息
     *
     * @param id 12313
     */
    @Transactional(readOnly=false)  //  关闭只读 ,对数据库有操作
    public void DeleteKnVersionInfoById(Long id){
        knApplicationSetUpInfoDao.delete(id);
    }
    /**
     * 模糊查询应用设置集合
     *
     * @param searchParams 查询条件
     * @param dt           应用 dataTable
     * @param appName      应用名称
     *
     * @return 返回符合条件的应用设置集合
     */
    public DataTable<KnApplicationSetupInfo> ListOfKnApplicationSetUp(Map<String,Object> searchParams,DataTable<KnApplicationSetupInfo> dt,final String appName){
        Sort sort=getSort(dt,new String[]{"applicationInfo.icon","applicationInfo.title","iosPackage","packageName","updateTime"});
        Pageable pageable=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        Page<KnApplicationSetupInfo> page=knApplicationSetUpInfoDao.findAll(new Specification<KnApplicationSetupInfo>(){
            @Override
            public Predicate toPredicate(Root<KnApplicationSetupInfo> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                Root<KnApplicationInfo> r=cq.from(KnApplicationInfo.class);
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                expressions.add(cb.like(root.<KnApplicationInfo>get("applicationInfo").<String>get("title"),r.<String>get("title")));
                if(!Utils.isEmptyString(appName)){
                    expressions.add(cb.like(cb.upper(root.<KnApplicationInfo>get("applicationInfo").<String>get("title")),"%"+appName.trim().toUpperCase()+"%"));
                }
                return predicate;
            }
        },pageable);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    /**
     * 对 datatable  进行排序
     *
     * @param dt 需要排序的 DataTable
     *
     * @return 返回排序信息
     */
    private Sort getSort(DataTable dt,String[] column){
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        int index=Integer.parseInt(dt.getiSortCol_0())-1;
        return new Sort(d,column[index]);
    }
    /**
     * 根据应用名称查询应用信息集合
     *
     * @return 返回符合条件应用信息集合
     */
    public List<KnApplicationInfo> FindByTitleLike(String appName){
        return knApplicationInfoDao.findByTitleLike(appName);
    }
    /**
     * 根据应用设置ID查询应用设置
     *
     * @return 返回对应应用设置信息
     */
    public KnApplicationSetupInfo FindKnAppSetUpInfoById(Long appId){
        return knApplicationSetUpInfoDao.findOne(appId);
    }
    /**
     * 保存或更新 应用设置信息
     *
     * @param knApplicationSetupInfo 应用设置信息
     *
     * @return 返回成功与否的状态 以及相关提示信息
     */
    @Transactional(readOnly=false)  //  关闭只读 ,对数据库有操作
    public boolean SaveOrUpdateKnApplictionSetup(KnApplicationSetupInfo knApplicationSetupInfo){
        boolean bool=false;
        try{
            knApplicationSetUpInfoDao.save(knApplicationSetupInfo);
            bool=true;
        }catch(Exception e){
            logger.error("新增或是更新设置错误信息 {}",e);
        }
        return bool;
    }
    /**
     * 根据应用ID查询应用信息
     *
     * @return 返回对应应用信息
     */
    public KnApplicationInfo FindKnAppInfoById(Long appId){
        return knApplicationInfoDao.findOne(appId);
    }
}
