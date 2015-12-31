package com.kingnode.xsimple.service.application;

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
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.dao.application.KnApplicationInfoDao;
import com.kingnode.xsimple.dao.application.KnRoleApplicationInfoDao;
import com.kingnode.xsimple.dao.application.KnRoleModuleFunctionInfoDao;
import com.kingnode.xsimple.dao.system.KnResourceDao;
import com.kingnode.xsimple.dao.system.KnRoleDao;
import com.kingnode.xsimple.entity.application.KnApplicationInfo;
import com.kingnode.xsimple.entity.system.KnResource;
import com.kingnode.xsimple.entity.system.KnRole;
import com.kingnode.xsimple.entity.web.KnRoleApplicationInfo;
import com.kingnode.xsimple.entity.web.KnRoleModuleFunctionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 孔江伟
 */
@Component
@Transactional(readOnly=true)
public class RoleInfoService{
    private KnRoleDao krid;
    private KnApplicationInfoDao kaid;
    private KnRoleApplicationInfoDao kraid;
    private KnResourceDao krd;
    private KnRoleModuleFunctionInfoDao krmfid;

    /**
     * ************
     * 查询角色信息
     *
     * @param dt    分页相关信息
     * @param appId 应用ＩＤ
     */
    public DataTable<KnRole> GetRoleList(DataTable dt,final Long appId,final Map<String,Object> searchParams){
        Page<KnRole> list=null;
        Sort sort=getSort(dt);
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        List<KnRole> listRole=new ArrayList<>();
        if(appId!=null&&appId!=-1){
            if(appId==-2){
                listRole=(List<KnRole>)krid.findRoleListNotInAppList();
            }else{
                listRole=(List<KnRole>)krid.findRoleByAppId(appId);
            }
        }
        List<Long> ids=new ArrayList<Long>();
        for(KnRole role : listRole){
            ids.add(role.getId());
        }
        if(ids.isEmpty()){
            ids.add(0L);//以防为空时MySQL查询中报错
        }
        final List<Long> fIds=ids;
        list=krid.findAll(new Specification<KnRole>(){
            @Override
            public Predicate toPredicate(Root<KnRole> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(appId!=null&&appId!=-1){
                    expressions.add(root.<Long>get("id").in(fIds));
                }
                if(searchParams!=null&&searchParams.size()!=0){
                    if(searchParams.containsKey("LIKE_active")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_active").toString().trim())){
                        expressions.add(cb.equal(root.<KnRole.ActiveType>get("active"),KnRole.ActiveType.valueOf(searchParams.get("LIKE_active").toString())));
                    }
                    if(searchParams.containsKey("LIKE_name")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_name").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("name")),"%"+searchParams.get("LIKE_name").toString().trim().toUpperCase()+"%"));
                    }
                    if(searchParams.containsKey("LIKE_code")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_code").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("code")),"%"+searchParams.get("LIKE_code").toString().trim().toUpperCase()+"%"));
                    }
                    if(searchParams.containsKey("LIKE_description")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_description").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("description")),"%"+searchParams.get("LIKE_description").toString().trim().toUpperCase()+"%"));
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
     * 获取所有模块
     */
    public List<KnResource> GetAllModule(){
        List<KnResource> list=krd.findByResourceType(KnResource.ResourceType.MODULE);
        return krd.findByResourceType(KnResource.ResourceType.MODULE);
    }

    public List<KnApplicationInfo> GetAllApp(){
        return (List<KnApplicationInfo>)kaid.findAll();
    }

    public KnRole ReadRoleInfo(Long id){
        return krid.findOne(id);
    }

    @Transactional(readOnly=false)
    public KnRole Save(KnRole role){
        return krid.save(role);
    }

    @Transactional(readOnly=false)
    public void Delete(Long id){
        KnRole role=krid.findOne(id);
        if(role.getRes()!=null){
            role.getRes().clear();
        }
        krid.delete(role);
    }

    @Transactional(readOnly=false)
    public void DeleteAll(List<Long> ids){
        List<KnRole> list=(List<KnRole>)krid.findAll(ids);
        krid.delete(list);
    }

    @Transactional(readOnly=false)
    public List<KnRoleApplicationInfo> SaveToApp(List<Long> rids,List<Long> appIds){
        List<KnRoleApplicationInfo> list=new ArrayList<KnRoleApplicationInfo>();
        List<KnRoleApplicationInfo> all=(List<KnRoleApplicationInfo>)kraid.findAll();
        Map map=new HashMap();
        for(KnRoleApplicationInfo knRoleApplicationInfo : all){
            String key=knRoleApplicationInfo.getRoleId()+"-"+knRoleApplicationInfo.getAppId();
            if(!map.containsKey(key)){
                map.put(key,knRoleApplicationInfo);
            }
        }
        KnRoleApplicationInfo kraInfo=null;
        for(Long rid : rids){
            for(Long appId : appIds){
                String key=rid+"-"+appId;
                if(!map.containsKey(key)){
                    kraInfo=new KnRoleApplicationInfo();
                    kraInfo.setAppId(appId);
                    kraInfo.setRoleId(rid);
                    list.add(kraInfo);
                }
            }
        }
        list=(List<KnRoleApplicationInfo>)kraid.save(list);
        return list;
    }

    public List<KnApplicationInfo> FindApplicationInfoByRid(Long rid){
        return kaid.findApplicationInfoByRid(rid);
    }

    public List<KnResource> FindFunctionByMid(Long rid,Long mid){
        //排序返回
        return krd.findResourceByRidAndMid(rid,mid);
    }

    public List<KnRole> FindRoleByFunc(Long functionId){
        return krid.findRoleByFunc(functionId);
    }

    public Map UpdateRoleInApp(Long rid,Long aid,Long oldAid){
        Map map=new HashMap();
        int rtn=kraid.updateApp(rid,aid,oldAid);
        if(rtn>0){
            map.put("stat",true);
            map.put("info","修改成功");
        }else{
            map.put("stat",false);
            map.put("info","修改失败");
        }
        return map;
    }

    public Map DeleteRoleFromApp(Long rid,Long aid){
        Map map=new HashMap();
        int rtn=kraid.deleteRoleFromApp(rid,aid);
        if(rtn>0){
            map.put("stat",true);
            map.put("info","删除成功");
        }else{
            map.put("stat",false);
            map.put("info","删除失败");
        }
        return map;
    }

    /**
     * *************
     * 通过角色ID，模块ID，功能ID删除角色模块功能关系表
     */
    public Map DeleteFunction(Long rid,Long mid,Long fid){
        Map map=new HashMap();
        int rtn=krmfid.deleteRoleModuleFunctionInfo(rid,mid,fid);
        if(rtn>0){
            map.put("stat",true);
            map.put("info","删除成功");
        }else{
            map.put("stat",false);
            map.put("info","删除失败");
        }
        return map;
    }

    /**
     * ******************
     * 保存布局管理页面信息
     *
     * @param rid       角色id
     * @param arrMID    模块id集合
     * @param arrMSort  模块排序集合
     * @param arrAllFID 功能id集合
     * @param arrFSort  功能排序集合
     */
    @Transactional(readOnly=false)
    public Map DragSave(Long rid,List<Long> arrMID,List<Long> arrMSort,List<List<Long>> arrAllFID,List<List<Long>> arrFSort){
        Map map=new HashMap();
        if(rid==null){
            map.put("stat",false);
            map.put("info","角色不存在");
            return map;
        }
        //删除原有角色-模块-功能关系映射
        List<KnRoleModuleFunctionInfo> delArray=krmfid.findByRoleId(rid);
        krmfid.delete(delArray);
        List<KnRoleModuleFunctionInfo> list=new ArrayList<KnRoleModuleFunctionInfo>();
        KnRoleModuleFunctionInfo info=null;
        //添加角色模块关系映射
        for(int i=0;i<arrMID.size();i++){
            info=new KnRoleModuleFunctionInfo();
            info.setRoleId(rid);
            info.setModuleId(arrMID.get(i));
            info.setRmSort(arrMSort.get(i));//模块在角色中的排序
            list.add(info);
        }
        //添加角色-模块-功能关系映射
        Map<String,KnRoleModuleFunctionInfo> entrys=new HashMap<String,KnRoleModuleFunctionInfo>();
        if(arrMID.size()==1){
            for(int j=0;j<arrAllFID.size();j++){
                String key=rid+"-"+arrMID.get(0)+"-"+arrAllFID.get(j).get(0);
                if(!entrys.containsKey(key)){
                    info=new KnRoleModuleFunctionInfo();
                    info.setRoleId(rid);
                    info.setModuleId(arrMID.get(0));
                    info.setRmSort(arrMSort.get(0));//模块在角色中的排序
                    info.setFunctionId(arrAllFID.get(j).get(0));
                    info.setMfSort(arrFSort.get(j).get(0));//功能在模块中的排序
                    list.add(info);
                    entrys.put(key,info);
                }
            }
        }else{
            for(int i=0;i<arrMID.size();i++){
                if(arrAllFID.size()!=0){
                    List<Long> arrFIDs=arrAllFID.get(i);
                    List<Long> arrFSorts=arrFSort.get(i);
                    for(int j=0;j<arrFIDs.size();j++){
                        String key=rid+"-"+arrMID.get(i)+"-"+arrFIDs.get(j);
                        if(!entrys.containsKey(key)){
                            info=new KnRoleModuleFunctionInfo();
                            info.setRoleId(rid);
                            info.setModuleId(arrMID.get(i));
                            info.setRmSort(arrMSort.get(i));//模块在角色中的排序
                            info.setFunctionId(arrFIDs.get(j));
                            info.setMfSort(arrFSorts.get(j));//功能在模块中的排序
                            list.add(info);
                            entrys.put(key,info);
                        }
                    }
                }
            }
        }
        if(list.size()!=0){
            List<KnRoleModuleFunctionInfo> rtnList=(List<KnRoleModuleFunctionInfo>)krmfid.save(list);
            if(rtnList==null||rtnList.size()==0){
                map.put("stat",false);
                map.put("info","保存失败");
                return map;
            }
        }
        map.put("stat",true);
        map.put("info","保存成功");
        return map;
    }

    private Sort getSort(DataTable dt){
        Sort.Direction d;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }else{
            d=Sort.Direction.DESC;
        }
        Sort sort=new Sort(d,"id");
        switch(dt.getiSortCol_0()){
            case "1":
                sort=new Sort(d,"name");
                break;
            case "2":
                sort=new Sort(d,"code");
                break;
            case "3":
                sort=new Sort(d,"description");
                break;
            case "4":
                sort=new Sort(d,"active");
                break;
        }
        return sort;
    }

    @Autowired
    public void setKrid(KnRoleDao krid){
        this.krid=krid;
    }

    @Autowired
    public void setKaid(KnApplicationInfoDao kaid){
        this.kaid=kaid;
    }

    @Autowired
    public void setKraid(KnRoleApplicationInfoDao kraid){
        this.kraid=kraid;
    }

    @Autowired
    public void setKrd(KnResourceDao krd){
        this.krd=krd;
    }

    @Autowired
    public void setKrmfid(KnRoleModuleFunctionInfoDao krmfid){
        this.krmfid=krmfid;
    }
}
