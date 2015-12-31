package com.kingnode.xsimple.service.application;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.dao.application.KnAppScoreInfoDao;
import com.kingnode.xsimple.dao.application.KnApplicationInfoDao;
import com.kingnode.xsimple.dao.application.KnApplicationSetUpInfoDao;
import com.kingnode.xsimple.dao.application.KnFunctionAppInfoDao;
import com.kingnode.xsimple.dao.application.KnRoleModuleFunctionInfoDao;
import com.kingnode.xsimple.dao.application.KnVersionInfoDao;
import com.kingnode.xsimple.dao.push.KnDeviceInfoDao;
import com.kingnode.xsimple.dao.system.KnFunctionVersionDao;
import com.kingnode.xsimple.dao.system.KnResourceDao;
import com.kingnode.xsimple.dao.system.KnRoleDao;
import com.kingnode.xsimple.dao.system.KnUserDao;
import com.kingnode.xsimple.dto.ScoreDto;
import com.kingnode.xsimple.dto.cloud.ApplicationCloudDto;
import com.kingnode.xsimple.dto.cloud.FunctionCloudDto;
import com.kingnode.xsimple.entity.application.KnApplicationInfo;
import com.kingnode.xsimple.entity.application.KnScoreInfo;
import com.kingnode.xsimple.entity.application.KnVersionInfo;
import com.kingnode.xsimple.entity.system.KnResource;
import com.kingnode.xsimple.entity.system.KnRole;
import com.kingnode.xsimple.entity.web.KnFunctionAppInfo;
import com.kingnode.xsimple.entity.web.KnFunctionVersionInfo;
import com.kingnode.xsimple.entity.web.KnRoleModuleFunctionInfo;
import com.kingnode.xsimple.util.PathUtil;
import com.kingnode.xsimple.util.Utils;
import com.kingnode.xsimple.util.excel.DownExcel;
import com.kingnode.xsimple.util.file.FileUtil;
import com.kingnode.xsimple.util.key.AES;
import com.kingnode.xsimple.util.key.UuidMaker;
import com.kingnode.xsimple.util.version.VersionNumUtil;
import com.kingnode.xsimple.util.xml.XmlUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * 应用管理
 */
@Component @Transactional(readOnly=true)
public class ApplicationInfoService{
    private KnApplicationInfoDao knApplicationInfoDao;
    private KnResourceDao knResourceDao;
    private KnRoleDao knRoleInfoDao;
    private KnRoleModuleFunctionInfoDao knRoleModuleFunctionInfoDao;
    private KnFunctionVersionDao knFunctionVersionDao;
    private KnVersionInfoDao knVersionInfoDao;
    private KnApplicationSetUpInfoDao knApplicationSetUpInfoDao;
    private KnDeviceInfoDao knDeviceInfoDao;
    private KnFunctionAppInfoDao knFunctionAppInfoDao;
    private KnUserDao knUserDao;
    private KnAppScoreInfoDao knAppScoreInfoDao;
    @Value("#{commonInfo['kndCloudUrl']}")
    private String kndCloudUrl;//云端的访问的url
    @Value("#{commonInfo['localUrl']}")
    private String localUrl;//本地的访问的url
    @Value("#{commonInfo['companyMark']}")
    private String companyMark;//本公司的短码
    private ObjectMapper jacksonMapper=new ObjectMapper();
    private org.springframework.ws.client.core.WebServiceTemplate appWst;
    private org.springframework.ws.client.core.WebServiceTemplate functionWst;
    private static Logger logger=LoggerFactory.getLogger(ApplicationInfoService.class);
    @Autowired
    public void setApplicationInfoDao(KnApplicationInfoDao knApplicationInfoDao){
        this.knApplicationInfoDao=knApplicationInfoDao;
    }
    @Autowired
    public void setKnFunctionAppInfoDao(KnFunctionAppInfoDao knFunctionAppInfoDao){
        this.knFunctionAppInfoDao=knFunctionAppInfoDao;
    }
    @Autowired
    public void setKrd(KnResourceDao knResourceDao){
        this.knResourceDao=knResourceDao;
    }
    @Autowired
    public void setKrid(KnRoleDao knRoleInfoDao){
        this.knRoleInfoDao=knRoleInfoDao;
    }
    @Autowired
    public void setKrmfid(KnRoleModuleFunctionInfoDao knRoleModuleFunctionInfoDao){
        this.knRoleModuleFunctionInfoDao=knRoleModuleFunctionInfoDao;
    }
    @Autowired
    public void setKfvd(KnFunctionVersionDao knFunctionVersionDao){
        this.knFunctionVersionDao=knFunctionVersionDao;
    }
    @Autowired
    public void setKnVersionInfoDao(KnVersionInfoDao knVersionInfoDao){
        this.knVersionInfoDao=knVersionInfoDao;
    }
    @Autowired
    public void setKnApplicationSetUpInfoDao(KnApplicationSetUpInfoDao knApplicationSetUpInfoDao){
        this.knApplicationSetUpInfoDao=knApplicationSetUpInfoDao;
    }
    @Autowired
    public void setKnDeviceInfoDao(KnDeviceInfoDao knDeviceInfoDao){
        this.knDeviceInfoDao=knDeviceInfoDao;
    }
    @Autowired
    public void setKnUserDao(KnUserDao knUserDao){
        this.knUserDao=knUserDao;
    }
    @Autowired
    public void setKnAppScoreInfoDao(KnAppScoreInfoDao knAppScoreInfoDao){
        this.knAppScoreInfoDao=knAppScoreInfoDao;
    }
    /*  @Autowired
        public void setAppWst(WebServiceTemplate appWst){
            this.appWst=appWst;
        }
        @Autowired
        public void setFunctionWst(WebServiceTemplate functionWst){
            this.functionWst=functionWst;
        }*/
    public DataTable<KnApplicationInfo> GetAppList(DataTable dt){
        Sort sort=getSort(dt,new String[]{"id","title","forFirm","downLoadUrl"});
        Pageable pager=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        Page<KnApplicationInfo> page=knApplicationInfoDao.findAll(pager);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    private Sort getSort(DataTable dt,String[] column){
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        int index=Integer.parseInt(dt.getiSortCol_0())-1;
        return new Sort(d,column[index]);
    }
    /**
     * 根据ids 删除多个应用信息
     *
     * @param ids 如 123,456
     */
    @Transactional(readOnly=false)  //  关闭只读 ,对数据库有操作
    public void DeleteAllKnAppInfoByIds(List<Long> ids){
        // 删除与应用有关联的 版本  应用设置  设备信息
        knVersionInfoDao.delete(knVersionInfoDao.findVerListByAppIds(ids));
        knApplicationSetUpInfoDao.delete(knApplicationSetUpInfoDao.findListByAppIds(ids));
        knDeviceInfoDao.delete(knDeviceInfoDao.findListByAppIds(ids));
        List<KnApplicationInfo> list=(List<KnApplicationInfo>)knApplicationInfoDao.findAll(ids);
        knApplicationInfoDao.delete(list);
    }
    /**
     * 根据id 删除单个应用信息
     *
     * @param id 12313
     */
    @Transactional(readOnly=false)  //  关闭只读 ,对数据库有操作
    public void DeleteKnAppInfoById(Long id){
        // 删除与应用有关联的 版本  应用设置  设备信息
        knVersionInfoDao.delete(knVersionInfoDao.findVerListByAppId(id));
        knApplicationSetUpInfoDao.delete(knApplicationSetUpInfoDao.findListByAppId(id));
        knDeviceInfoDao.delete(knDeviceInfoDao.findListByAppId(id));
        knApplicationInfoDao.delete(id);
    }
    /**
     * 保存或更新 应用信息
     *
     * @param knApplicationInfo 应用信息
     *
     * @return 返回成功与否的状态 以及相关提示信息
     */
    @Transactional(readOnly=false)  //  关闭只读 ,对数据库有操作
    public Map<String,Object> SaveOrUpdateKnAppInfo(KnApplicationInfo knApplicationInfo){
        boolean bool=false, empBool=false, titBool=false;
        String mseeage="应用名已经存在,请修改后,在提交！";
        Map<String,Object> map=new HashMap<String,Object>();
        try{
            if(null==knApplicationInfo.getId()){
                empBool=true;
                knApplicationInfo.setApiKey(UUID.randomUUID().toString());
            }
            List<KnApplicationInfo> list=knApplicationInfoDao.findAppListByTitle(knApplicationInfo.getTitle());
            if(null!=list&&list.size()>0){
                if(empBool){
                    titBool=true;
                }else{
                    if(!knApplicationInfo.getId().equals(list.get(0).getId())){
                        titBool=true;
                    }
                }
            }
            if(!titBool){
                knApplicationInfoDao.save(knApplicationInfo);
                bool=true;
            }else{
                map.put("msg",mseeage);
            }
            map.put("stat",bool);
        }catch(Exception e){
            logger.error("应用新增/更新异常{}",e);
            map.put("stat",false);
            map.put("msg","应用名已经存在,请修改后,在提交！");
        }
        return map;
    }
    /**
     * 根据ID查询应用信息
     *
     * @param id 应用id
     *
     * @return 返回符合条件的应用信息
     */
    public KnApplicationInfo FindKnAppInfoById(Long id){
        return knApplicationInfoDao.findOne(id);
    }
    /**
     * 模糊查询应用集合
     *
     * @param searchParams 查询条件
     * @param dt           应用 dataTable
     * @param appName      应用名称
     *
     * @return 返回符合条件的应用集合
     */
    public DataTable<KnApplicationInfo> ListOfKnApply(Map<String,Object> searchParams,DataTable<KnApplicationInfo> dt,final String appName){
        Sort sort=getSort(dt,new String[]{"id","title","forFirm","workStatus","updateTime"});
        Pageable pageable=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        Page<KnApplicationInfo> page=knApplicationInfoDao.findAll(new Specification<KnApplicationInfo>(){
            @Override
            public Predicate toPredicate(Root<KnApplicationInfo> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(!Utils.isEmptyString(appName)){
                    expressions.add(cb.like(cb.upper(root.<String>get("title")),"%"+appName.trim().toUpperCase()+"%"));
                }
                return predicate;
            }
        },pageable);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    /**
     * ************
     * 查询模块信息
     *
     * @param dt    分页相关信息
     * @param appId 应用ＩＤ
     *
     * @return
     */
    public DataTable<KnResource> ModuleList(DataTable dt,final Long appId){
        Page<KnResource> list=null;
        Sort sort=getSort(dt,new String[]{"id","name","code","active","description"});
        Pageable pager=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        if(appId==null||appId==-1){
            list=knResourceDao.findResourceList(KnResource.ResourceType.MODULE,pager);
        }else if(appId==-2){
            //未分配
            list=knResourceDao.findModuleNotInAppList(KnResource.ResourceType.MODULE,pager);
        }else{
            list=knResourceDao.findResourceByAppIdList(appId,KnResource.ResourceType.MODULE,pager);
        }
        if(list!=null){
            dt.setiTotalDisplayRecords(list.getTotalElements());
            dt.setAaData(list.getContent());
        }
        return dt;
    }
    public List<KnApplicationInfo> GetAllApp(){
        return (List<KnApplicationInfo>)knApplicationInfoDao.findAll(new Sort(Sort.Direction.ASC,"title"));
    }
    public KnResource ReadResourceInfo(Long id){
        return knResourceDao.findOne(id);
    }
    @Transactional(readOnly=false)
    public KnResource SaveModule(KnResource kr){
        kr.setType(KnResource.ResourceType.MODULE);
        return knResourceDao.save(kr);
    }
    @Transactional(readOnly=false)
    public void DeleteResourceById(Long id){
        knResourceDao.delete(id);
    }
    @Transactional(readOnly=false)
    public void DeleteAllResourceByIds(List<Long> ids){
        List<KnResource> list=(List<KnResource>)knResourceDao.findAll(ids);
        knResourceDao.delete(list);
    }
    public DataTable<KnRole> FindRoleByAid(DataTable dt,final Long appId){
        Page<KnRole> list=null;
        Pageable pager=new PageRequest(dt.pageNo(),dt.getiDisplayLength());
        if(appId!=null&&appId!=-1&&appId!=-2){
            list=knRoleInfoDao.findRoleListByAppId(appId,pager,KnRole.ActiveType.DISABLE);
        }else{
            List<Long> ids=new ArrayList<>();
            if(appId!=null&&appId==-2){
                List<KnRole> roles=knRoleInfoDao.findRoleListNotInAppList();
                if(roles!=null&&!roles.isEmpty()){
                    for(KnRole role:roles){
                        ids.add(role.getId());
                    }
                }
            }
            final List<Long> fIds=ids;
            list=(Page<KnRole>)knRoleInfoDao.findAll(new Specification<KnRole>(){
                @Override public Predicate toPredicate(Root<KnRole> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                    Predicate predicate=cb.conjunction();
                    List<Expression<Boolean>> expressions=predicate.getExpressions();
                    expressions.add(cb.notEqual(root.<KnRole.ActiveType>get("active"),KnRole.ActiveType.DISABLE));
                    if(appId!=null&&appId==-2){
                        expressions.add(root.<Long>get("id").in(fIds));
                    }
                    return predicate;
                }
            },pager);
        }
        if(list!=null){
            dt.setiTotalDisplayRecords(list.getTotalElements());
            dt.setAaData(list.getContent());
        }
        return dt;
    }
    public DataTable<KnResource> FindPageModuleByRoleId(Long roleId,DataTable<KnResource> dt){
        Page<KnResource> list=null;
        Sort sort=getSort(dt,new String[]{"id","name","code","active","description"});
        Pageable pager=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        list=knResourceDao.findPageModuleByRoleId(roleId,KnResource.ResourceType.MODULE,pager);
        dt.setiTotalDisplayRecords(list.getTotalElements());
        dt.setAaData(list.getContent());
        return dt;
    }
    public List<KnRole> FindRoleByAppId(Long appId){
        return knRoleInfoDao.findRoleByAppId(appId);
    }
    @Transactional(readOnly=false)
    public Map SaveModuleToRole(List<Long> mids,List<Long> rids){
        List<KnRoleModuleFunctionInfo> fromDB=knRoleModuleFunctionInfoDao.getRMInfoOnlyByRoleAndModule();
        Map<String,KnRoleModuleFunctionInfo> map=new HashMap<String,KnRoleModuleFunctionInfo>();
        for(KnRoleModuleFunctionInfo info : fromDB){
            String key=info.getRoleId()+"-"+info.getModuleId();
            map.put(key,info);
        }
        List<KnRoleModuleFunctionInfo> list=new ArrayList<KnRoleModuleFunctionInfo>();
        KnRoleModuleFunctionInfo info=null;
        Random random=new Random(100);//指定种子数100
        for(Long rid : rids){
            int sort=(int)(Math.random()*1000);
            for(Long mid : mids){
                String key=rid+"-"+mid;
                sort++;
                if(!map.containsKey(key)){
                    info=new KnRoleModuleFunctionInfo();
                    info.setRoleId(rid);
                    info.setModuleId(mid);
                    info.setRmSort((long)sort);
                }else{
                    info=map.get(key);
                    info.setRmSort((long)sort);
                }
                list.add(info);
            }
        }
        List<KnRoleModuleFunctionInfo> listRtn=(List<KnRoleModuleFunctionInfo>)knRoleModuleFunctionInfoDao.save(list);
        Map rtnMap=new HashMap();
        if(listRtn==null){
            rtnMap.put("stat",false);
            rtnMap.put("info","分配角色失败");
        }else{
            rtnMap.put("stat",true);
            rtnMap.put("info","分配角色成功");
        }
        return rtnMap;
    }
    public List<KnRole> FindRoleByModule(Long mid){
        return knRoleInfoDao.findRoleByModule(mid);
    }
    public Map DeleteModuleFromRole(Long rid,Long mid){
        int rtn=knRoleModuleFunctionInfoDao.deleteRoleModuleFunctionInfo(rid,mid);
        Map map=new HashMap();
        if(rtn>0){
            map.put("stat",true);
            map.put("info","删除成功");
        }else{
            map.put("stat",false);
            map.put("info","删除失败");
        }
        return map;
    }
    public Map UpdateModuleInRole(Long rid,Long mid,Long oldrId){
        int rtn=knRoleModuleFunctionInfoDao.updateRoleModuleInfo(rid,mid,oldrId);
        Map map=new HashMap();
        if(rtn>0){
            map.put("stat",true);
            map.put("info","更新成功");
        }else{
            map.put("stat",false);
            map.put("info","更新失败");
        }
        return map;
    }
    public List<KnResource> FindFuncByRidAndMid(Long rid,Long mid){
        return knResourceDao.findResourceByRidAndMid(rid,mid);
    }
    /**
     * ******************
     * 通过角色与模块查询功能与功能zip信息
     *
     * @param rid
     * @param mid
     *
     * @return
     */
    public List FindFuncAndFuncVersionByMid(Long rid,Long mid){
        List ret=new ArrayList();
        //获取功能集合
        List<KnResource> funcList=knResourceDao.findResourceByRidAndMid(rid,mid);
        for(KnResource knResource : funcList){
            Map map=new HashMap();
            List funcVersionList=knFunctionVersionDao.findByFunctionId(knResource.getId());
            if(funcVersionList!=null&&funcVersionList.size()!=0){
                map.put("version",funcVersionList.get(0));
            }else{
                map.put("version","");
            }
            map.put("function",knResource);
            ret.add(map);
        }
        return ret;
    }
    public Map DeleteFuncFromRMF(Long rid,Long mid,Long fid){
        int rtn=knRoleModuleFunctionInfoDao.deleteRoleModuleFunctionInfo(rid,mid,fid);
        Map map=new HashMap();
        if(rtn>0){
            map.put("stat",true);
            map.put("info","删除成功");
        }else{
            map.put("stat",false);
            map.put("info","删除失败");
        }
        return map;
    }
    public List<KnResource> FindModuleByRoleIdAndFunctionId(Long roleId,Long functionId){
        return knResourceDao.findModuleByRoleIdAndFunctionId(roleId,functionId,KnResource.ResourceType.MODULE);
    }
    public Page<KnResource> FindPageModuleNotInRid(final Long roleId,int currentPage,int numPerPage){
        Pageable pager=new PageRequest(currentPage-1,numPerPage);
        Page<KnResource> list=knResourceDao.findPageModuleNotInRid(roleId,KnResource.ResourceType.MODULE,pager);
        return list;
    }
    /**
     * 获取功能列表数据
     *
     * @param dt           表格参数
     * @param appId        应用信息
     * @param searchParams 查询Map
     * @param beginTime    开始时间
     * @param endTime      结束时间
     *
     * @return
     */
    public DataTable<KnResource> FuncList(DataTable dt,final Long appId,final Map<String,Object> searchParams,final Long beginTime,final Long endTime){
        Page<KnResource> list=null;
        Sort sort=getSort(dt,new String[]{"id","name","version","markName","active","updateTime"});
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        List<Long> ids=new ArrayList<Long>();
        if(appId!=null&&appId!=-1){
            List<KnResource> knResources=new ArrayList<>();
            if(appId==-2){
                knResources=knResourceDao.findFunctionNotInAppList(KnResource.ResourceType.FUNCTION);
            }else{
                knResources=knResourceDao.findFunctionByAppIdList(appId,KnResource.ResourceType.FUNCTION);
            }
            ids.add(0L);
            for(KnResource kr : knResources){
                ids.add(kr.getId());
            }
        }
        final List<Long> fIds=ids;
        list=knResourceDao.findAll(new Specification<KnResource>(){
            @Override public Predicate toPredicate(Root<KnResource> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                expressions.add(cb.equal(root.<KnResource.ResourceType>get("type"),KnResource.ResourceType.FUNCTION));
                if(appId!=null&&appId!=-1){
                    expressions.add(root.<Long>get("id").in(fIds));
                }
                if(searchParams!=null&&searchParams.size()!=0){
                    if(searchParams.containsKey("LIKE_active")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_active").toString())){
                        expressions.add(cb.equal(root.<KnRole.ActiveType>get("active"),KnResource.ActiveType.valueOf(searchParams.get("LIKE_active").toString())));
                    }
                    if(searchParams.containsKey("LIKE_name")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_name").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("name")),"%"+searchParams.get("LIKE_name").toString().trim().toUpperCase()+"%"));
                    }
                    if(searchParams.containsKey("LIKE_markName")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_markName").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("markName")),"%"+searchParams.get("LIKE_markName").toString().trim().toUpperCase()+"%"));
                    }
                    if(searchParams.containsKey("LIKE_version")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_version").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("version")),"%"+searchParams.get("LIKE_version").toString().trim().toUpperCase()+"%"));
                    }
                }
                if(beginTime!=null){
                    expressions.add(cb.greaterThanOrEqualTo(root.<Long>get("createTime"),beginTime));
                }
                if(endTime!=null){
                    expressions.add(cb.lessThanOrEqualTo(root.<Long>get("createTime"),endTime));
                }
                return predicate;
            }
        },pageRequest);
        dt.setiTotalDisplayRecords(list.getTotalElements());
        dt.setAaData(list.getContent());
        return dt;
    }
    /**
     * 保存安装包到应用中去
     *
     * @param funcIds 功能ID集合
     * @param appIds  应用ID集合
     *
     * @return
     */
    @Transactional(readOnly=false)
    public Map SaveSetUpPackageToApp(List<Long> funcIds,List<Long> appIds){
        //获取当前应用下的功能
        HashMap<String,KnFunctionAppInfo> map=new HashMap<String,KnFunctionAppInfo>();
        List<KnFunctionAppInfo> faList=knFunctionAppInfoDao.findInfosInAppIds(appIds);
        for(KnFunctionAppInfo functionAppInfo : faList){
            String key=functionAppInfo.getAppId()+"-"+functionAppInfo.getFunctionId();
            map.put(key,functionAppInfo);
        }
        //获取当前功能中的安装包信息
        List<KnResource> setupPackageList=knResourceDao.findSetUpPackageByIds(funcIds,KnResource.ActiveType.SETUPFILE);
        //保存到数据库的信息列表
        List<KnFunctionAppInfo> list=new ArrayList<KnFunctionAppInfo>();
        for(Long app : appIds){
            for(KnResource func : setupPackageList){
                String key=app+"-"+func.getId();
                //过滤数据库中已存在的数据
                if(!map.containsKey(key)){
                    KnFunctionAppInfo faInfo=new KnFunctionAppInfo();
                    faInfo.setAppId(app);
                    faInfo.setFunctionId(func.getId());
                    list.add(faInfo);
                }
            }
        }
        //保存
        Map rtnMap=new HashMap();
        List<KnFunctionAppInfo> rtn=null;
        if(list.size()!=0){
            rtn=(List<KnFunctionAppInfo>)knFunctionAppInfoDao.save(list);
            if(rtn==null){
                rtnMap.put("stat",false);
                rtnMap.put("info","保存失败");
            }else{
                rtnMap.put("stat",true);
                rtnMap.put("info","保存成功");
            }
        }else{
            rtnMap.put("stat",false);
            rtnMap.put("info","这些功能已经分配到应用中，没有要分配的功能。");
        }
        return rtnMap;
    }
    /**
     * ******
     * 通过功能ID获取应用信息
     *
     * @param functionId
     *
     * @return
     */
    public List<KnApplicationInfo> FindAppByFunc(Long functionId){
        return knApplicationInfoDao.findAppByFuncId(functionId);
    }
    /**
     * ********
     * 删除功能应用关系
     *
     * @param functionId
     * @param appId
     *
     * @return
     */
    @Transactional(readOnly=false)
    public Map DeleteSetupPackageFromApp(Long functionId,Long appId){
        Map map=new HashMap();
        List<KnFunctionAppInfo> list=knFunctionAppInfoDao.findByFunctionIdAndAppId(functionId,appId);
        if(list.isEmpty()){
            map.put("stat",false);
            map.put("info","删除失败");
        }else{
            knFunctionAppInfoDao.delete(list.get(0));
            map.put("stat",true);
            map.put("info","删除成功");
        }
        return map;
    }
    public List<KnResource> FindSetUpPackageByIds(List<Long> ids){
        return knResourceDao.findSetUpPackageByIds(ids,KnResource.ActiveType.SETUPFILE);
    }
    public List<KnResource> FindModuleByRoleId(Long rid){
        return knResourceDao.findModuleByRoleId(rid,KnResource.ResourceType.MODULE);
    }
    @Transactional(readOnly=false)
    public KnResource SaveFunc(KnResource func){
        func.setType(KnResource.ResourceType.FUNCTION);
        if(func.getId()!=null){
            String version=func.getVersion();
            if(!Strings.isNullOrEmpty(version)){
                try{
                    double updateVersion=(Double.parseDouble(version)*10+1)/10.0d;
                    func.setVersion(updateVersion+"");
                }catch(Exception ex){
                    func.setVersion("1.0");
                }
            }else{
                func.setVersion("1.0");
            }
        }else{
            func.setVersion("1.0");
        }
        return knResourceDao.save(func);
    }
    @Transactional(readOnly=false)
    public Map SaveFuncToRMF(Long roleId,List<Long> mids,List<Long> fids){
        List<KnRoleModuleFunctionInfo> listFromDB=knRoleModuleFunctionInfoDao.findByRoleId(roleId);
        Map<String,KnRoleModuleFunctionInfo> map=new HashMap<String,KnRoleModuleFunctionInfo>();
        Map<String,KnRoleModuleFunctionInfo> rmInfo=new HashMap<String,KnRoleModuleFunctionInfo>();
        for(KnRoleModuleFunctionInfo info : listFromDB){
            StringBuffer keys=new StringBuffer().append(info.getRoleId()).append("-").append(info.getModuleId());
            if(info.getFunctionId()==null){
                if(!rmInfo.containsKey(keys)){
                    rmInfo.put(keys.toString(),info);
                }
            }else{
                keys.append("-").append(info.getFunctionId());
                map.put(keys.toString(),info);
            }
        }
        List<KnRoleModuleFunctionInfo> list=new ArrayList<KnRoleModuleFunctionInfo>();
        KnRoleModuleFunctionInfo info=null;
        for(Long mid : mids){
            StringBuffer keys=new StringBuffer().append(roleId).append("-").append(mid);
            Long rmSort=rmInfo.containsKey(keys)?rmInfo.get(keys).getRmSort():0;
            int sort=(int)(Math.random()*1000);
            for(Long fid : fids){
                keys.append("-").append(fid);
                sort++;
                if(!map.containsKey(keys.toString())){
                    info=new KnRoleModuleFunctionInfo();
                    info.setRoleId(roleId);
                    info.setModuleId(mid);
                    info.setFunctionId(fid);
                    info.setRmSort(rmSort);
                    info.setMfSort((long)sort);
                    list.add(info);
                }else{
                    info=map.get(keys.toString());
                    info.setRmSort(rmSort);
                    info.setMfSort((long)sort);
                    list.add(info);
                }
            }
        }
        List<KnRoleModuleFunctionInfo> rtnList=(List<KnRoleModuleFunctionInfo>)knRoleModuleFunctionInfoDao.save(list);
        Map rtnMap=new HashMap();
        if(rtnList==null){
            rtnMap.put("stat",false);
            rtnMap.put("info","分配模块失败");
        }else{
            rtnMap.put("stat",true);
            rtnMap.put("info","分配模块成功");
        }
        return rtnMap;
    }
    @Transactional(readOnly=false)
    public Map UpdateFuncInRMF(Long roleId,Long moduleId,Long functionId,Long oldRoleId,Long oldModuleId){
        int index=0;
        //要更改后的关系是否存在
        List<KnRoleModuleFunctionInfo> list=knRoleModuleFunctionInfoDao.findByRoleIdAndMoludeIdAndFunctionId(roleId,moduleId,functionId);
        if(list==null||list.size()==0){
            //查询当前的数据进行修改
            list=knRoleModuleFunctionInfoDao.findByRoleIdAndMoludeIdAndFunctionId(oldRoleId,oldModuleId,functionId);
            for(KnRoleModuleFunctionInfo info : list){
                info.setRoleId(roleId);
                info.setModuleId(moduleId);
            }
            List<KnRoleModuleFunctionInfo> rtnInfo=(List<KnRoleModuleFunctionInfo>)knRoleModuleFunctionInfoDao.save(list);
            if(rtnInfo!=null&&rtnInfo.size()!=0){
                index=1;
            }
        }else{
            //直接删除
            index=knRoleModuleFunctionInfoDao.deleteRoleModuleFunctionInfo(oldRoleId,oldModuleId,functionId);
        }
        Map map=new HashMap();
        if(index<=0){
            map.put("stat",false);
            map.put("info","修改功能所属模块失败");
        }else{
            map.put("stat",true);
            map.put("info","修改功能所属模块成功");
        }
        return map;
    }
    @Transactional(readOnly=false)
    public Map DeleteRMFInfoByRidAndFid(Long roleId,Long functionId){
        int i=knRoleModuleFunctionInfoDao.deleteRMFInfoByRidAndFid(roleId,functionId);
        Map map=new HashMap();
        if(i<=0){
            map.put("stat",false);
            map.put("info","删除功能所属角色失败");
        }else{
            map.put("stat",true);
            map.put("info","删除功能所属角色成功");
        }
        return map;
    }
    public Page<KnResource> FindAllFunctionInfs(int currentPage,int numPerPage,final String name){
        Pageable pager=new PageRequest(currentPage-1,numPerPage);
        Page<KnResource> arr=knResourceDao.findAll(new Specification<KnResource>(){
            @Override public Predicate toPredicate(Root<KnResource> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                expressions.add(cb.equal(root.<KnResource.ResourceType>get("type"),KnResource.ResourceType.FUNCTION));
                if(!Strings.isNullOrEmpty(name.trim())&&!name.trim().equals("")){
                    expressions.add(cb.like(cb.upper(root.<String>get("name")),"%"+name.trim().toUpperCase()+"%"));
                }
                return predicate;
            }
        },pager);
        return arr;
    }
    public List<KnFunctionVersionInfo> FindZipListByFunId(Long functionId){
        List<KnFunctionVersionInfo> functionVersionInfos=knFunctionVersionDao.findByFunctionId(functionId);
        //根据版本号进行排序,降序输出,取出的第一个数据为最高版本数据
        if(functionVersionInfos.size()>1){
            Collections.sort(functionVersionInfos,new Comparator<KnFunctionVersionInfo>(){
                public int compare(KnFunctionVersionInfo o1,KnFunctionVersionInfo o2){
                    return VersionNumUtil.versionCompareTo(o1.getZipVersion(),o2.getZipVersion());
                }
            });
        }
        return functionVersionInfos;
    }
    public KnFunctionVersionInfo ReadFunctionVersionInfo(Long id){
        return knFunctionVersionDao.findOne(id);
    }
    @Transactional(readOnly=false)
    public KnFunctionVersionInfo SaveFuncVersion(KnFunctionVersionInfo kf){
        if(kf.getFunctionId()==null){
            return null;
        }
        //通过功能ID获取功能版本信息列表
        List<KnFunctionVersionInfo> fvList=knFunctionVersionDao.findByFunctionId(kf.getFunctionId());
        if(fvList==null||fvList.size()==0){
            kf.setZipVersion("1.0");
        }else{
            if(fvList.size()!=0){
                //根据版本号进行排序,降序输出,取出的第一个数据为最高版本数据
                Collections.sort(fvList,new Comparator<KnFunctionVersionInfo>(){
                    public int compare(KnFunctionVersionInfo o1,KnFunctionVersionInfo o2){
                        return VersionNumUtil.versionCompareTo(o1.getZipVersion(),o2.getZipVersion());
                    }
                });
            }
            KnFunctionVersionInfo funObj=fvList.get(0);
            String zipVersion=Strings.isNullOrEmpty(funObj.getZipVersion())?"1.0":funObj.getZipVersion();
            double updateVersion=(Double.parseDouble(zipVersion.trim())*10+1)/10.0d;
            kf.setZipVersion(updateVersion+"");
        }
        kf.setFunkey(UuidMaker.getInstance().getUuid(false));
        return knFunctionVersionDao.save(kf);
    }
    @Transactional(readOnly=false)
    public KnFunctionVersionInfo UpdateFuncVersionStatus(Setting.WorkStatusType workStatusType,Long vid){
        KnFunctionVersionInfo info=knFunctionVersionDao.findOne(vid);
        info.setWorkStatus(workStatusType);
        return knFunctionVersionDao.save(info);
    }
    public KnFunctionVersionInfo FindFuncVersionById(Long id){
        KnFunctionVersionInfo inf=knFunctionVersionDao.findOne(id);
        KnResource function=knResourceDao.findOne(inf.getFunctionId());
        inf.setUnZipUrl(function.getName());
        return inf;
    }
    @Transactional(readOnly=false)
    public void DeleteFuncVersion(Long id){
        knFunctionVersionDao.delete(id);
    }
    /**
     * ***********
     * 通过角色编号查询功能信息
     *
     * @param roleId
     *
     * @return
     */
    public List<KnResource> FindFuncByRoleId(Long roleId){
        List<Long> roleIds=new ArrayList<>();
        roleIds.add(roleId);
        return knResourceDao.findFunctionByRoleIds(roleIds,KnResource.ResourceType.FUNCTION);
    }
    public List FindFuncByRoleIdGroupByModule(Long roleId){
        List<KnResource> moduleList=knResourceDao.findModuleByRoleId(roleId,KnResource.ResourceType.MODULE);
        List ret=new ArrayList();
        for(KnResource module : moduleList){
            String moduleName=module.getName();
            Long moduleId=module.getId();
            Map map=new HashMap();
            map.put("moduleName",moduleName);
            map.put("moduleId",moduleId);
            List<KnResource> funcInModuleList=knResourceDao.findResourceByRidAndMid(roleId,moduleId);
            map.put("list",funcInModuleList);
            ret.add(map);
        }
        return ret;
    }
    /**
     * *********************
     * 根据角色集合获取模块和功能信息
     *
     * @param roleIds  角色集合
     * @param isWeixin 是否微信
     *                 供外部接口使用
     *
     * @return
     */
    public List<Map> GetResourceByRolesAndAppkey(List<Long> roleIds,boolean isWeixin){
        List<Map> arrModule=new ArrayList<>();
        //通过角色获取模块信息
        List<KnResource> moduleList=knResourceDao.findModuleByRoleIds(roleIds,KnResource.ResourceType.MODULE);
        Map moduleInf=new HashMap();
        for(KnResource module : moduleList){
            moduleInf.put(module.getId(),module);
        }
        //通过角色与模块信息获取模块的排序信息
        List<KnRoleModuleFunctionInfo> rmSortList=knRoleModuleFunctionInfoDao.getRMInfoOnlyByRoles(roleIds);
        //获取模块下的功能及功能排序
        for(KnRoleModuleFunctionInfo module : rmSortList){
            Map moduleItem=new HashMap();
            //存放模块信息
            moduleItem.put("moduleInf",moduleInf.get(module.getModuleId()));
            //通过角色与模块查询功能信息
            List<KnResource> funcList=knResourceDao.findResourceByRidAndMid(module.getRoleId(),module.getModuleId());
            //查询功能在模块中的排序
            List<KnRoleModuleFunctionInfo> funcSort=knRoleModuleFunctionInfoDao.findByRoleIdAndMoludeId(module.getRoleId(),module.getModuleId());
            //模块下的功能信息集合
            List<Map> funcItems=new ArrayList<>();
            Map funcSorts=new HashMap();
            //功能ＩＤ集合
            List<Long> functionIds=new ArrayList<Long>();
            for(KnRoleModuleFunctionInfo info : funcSort){
                //功能在模块中的排序信息
                funcSorts.put(info.getFunctionId(),info.getMfSort());
                functionIds.add(info.getFunctionId());
            }
            //通过功能获取功能的版本信息
            List<KnFunctionVersionInfo> fvs=knFunctionVersionDao.findByFunctionIds(functionIds);
            //对功能版本信息进行处理，获取最高版本
            Map func_Version=getVersionByFunction(fvs,functionIds);
            //解析功能信息
            for(KnResource functionInfo : funcList){
                Map funcMap=new HashMap();
                KnFunctionVersionInfo fv=null;
                funcMap.put("fid",functionInfo.getId());
                funcMap.put("fversion",functionInfo.getVersion());
                funcMap.put("ftitle",functionInfo.getName());
                funcMap.put("enftitle",functionInfo.getEnTitle());
                funcMap.put("ficon",functionInfo.getIcon());
                funcMap.put("funckey",functionInfo.getMarkName());
                funcMap.put("fversion",functionInfo.getVersion());
                funcMap.put("fstatus",functionInfo.getActive()==null?"":functionInfo.getActive());
                if(funcSorts.containsKey(functionInfo.getId())){
                    funcMap.put("fsort",funcSorts.get(functionInfo.getId()));
                }
                if(functionInfo.getActive()!=null&&functionInfo.getActive().equals(KnResource.ActiveType.PUBLICPACKAGE)){
                    funcMap.put("fsort",Integer.MAX_VALUE);
                }
                if(func_Version.containsKey(functionInfo.getId())&&func_Version.get(functionInfo.getId())!=null){
                    fv=(KnFunctionVersionInfo)func_Version.get(functionInfo.getId());
                    if(isWeixin){
                        funcMap.put("zip",fv.getUnZipUrl());
                    }else{
                        funcMap.put("zip",fv.getFuncZipUrl());
                    }
                    funcMap.put("zipver",fv.getZipVersion());
                    funcMap.put("interfaceUrl",fv.getInterfaceUrl());
                    funcMap.put("zipsize",fv.getZipSize());
                }
                funcItems.add(funcMap);
            }
            //当前模块在角色中的排序
            moduleItem.put("sort",module.getRmSort());
            //加入当前模块下功能的集合
            moduleItem.put("functions",funcItems);
            arrModule.add(moduleItem);
        }
        return arrModule;
    }
    private Map getVersionByFunction(List<KnFunctionVersionInfo> list,List<Long> functionIds){
        //将功能的版本通过通过功能ＩＤ分组
        List<List<KnFunctionVersionInfo>> all=new ArrayList<List<KnFunctionVersionInfo>>();
        Map map=new HashMap();
        for(Long functionId : functionIds){
            if(!map.containsKey(functionId)){
                map.put(functionId,new ArrayList<KnFunctionVersionInfo>());
            }
            for(KnFunctionVersionInfo info : list){
                if(!Setting.WorkStatusType.unusable.equals(info.getWorkStatus())){
                    if(!map.containsKey(info.getFunctionId())){
                        List<KnFunctionVersionInfo> arr=new ArrayList<KnFunctionVersionInfo>();
                        arr.add(info);
                        map.put(info.getFunctionId(),arr);
                    }else{
                        List<KnFunctionVersionInfo> arr=(ArrayList<KnFunctionVersionInfo>)map.get(info.getFunctionId());
                        arr.add(info);
                        map.put(info.getFunctionId(),arr);
                    }
                }
                list.remove(info);
            }
        }
        for(Long functionId : functionIds){
            List<KnFunctionVersionInfo> fvList=(ArrayList<KnFunctionVersionInfo>)map.get(functionId);
            if(fvList.size()!=0){
                //根据版本号进行排序,降序输出,取出的第一个数据为最高版本数据
                Collections.sort(fvList,new Comparator<KnFunctionVersionInfo>(){
                    public int compare(KnFunctionVersionInfo o1,KnFunctionVersionInfo o2){
                        return VersionNumUtil.versionCompareTo(o1.getZipVersion(),o2.getZipVersion());
                    }
                });
                map.put(functionId,fvList.get(0));
            }else{
                map.put(functionId,null);
            }
        }
        return map;
    }
    /**
     * 系统首页应用下载量信息
     *
     * @return 返回 下载信息
     */
    public Map<String,Object> DownloadTotal(){
        Map<String,Object> map=new HashMap();
        try{
            List<KnVersionInfo> versionList=(List<KnVersionInfo>)knVersionInfoDao.findAll();
            StringBuffer ios=new StringBuffer("[");
            StringBuffer android=new StringBuffer("[");
            for(KnVersionInfo version : versionList){
                if(version.getTotalDowns()!=null&&!"0".equals(version.getTotalDowns())){
                    if(Setting.VersionType.ANDROID.toString().equals(version.getType().toString())||Setting.VersionType.ANDROID_PAD.toString().equals(version.getType().toString())){
                        android.append("['").append(version.getApplicationInfo().getTitle()).append("_").append(version.getNum()).append("', ").append(version.getTotalDowns()).append("],");
                    }else{
                        ios.append("['").append(version.getApplicationInfo().getTitle()).append("_").append(version.getNum()).append("', ").append(version.getTotalDowns()).append("],");
                    }
                }
            }
            ios.append("]");
            android.append("]");
            map.put("ios",ios.toString());
            map.put("android",android.toString());
            map.put("stat",true);
        }catch(Exception e){
            map.put("stat",false);
        }
        return map;
    }
    /**
     * 获取总数的集合信息
     */
    public Map<String,Object> GetNumList(){
        Map<String,Object> map=new HashMap();
        try{
            map.put("roleNum",knRoleInfoDao.count());
            map.put("userNum",knUserDao.count());
            map.put("stat",true);
        }catch(Exception e){
            map.put("stat",false);
        }
        return map;
    }
    /**
     * 查看应用名称是否存在
     *
     * @param name 应用名称
     * @param id   应用id
     *
     * @return 是否存在 true 存在  false 不存在
     */
    public Boolean CheckName(String name,Long id){
        List<KnApplicationInfo> appList=knApplicationInfoDao.findAppListByTitle(name);
        boolean tipBool=false;
        if(!Utils.isEmpityCollection(appList)){
            if(0L==id){
                tipBool=true;
            }else{
                if(!id.equals(appList.get(0).getId())){
                    tipBool=true;
                }
            }
        }
        return !tipBool;
    }
    /**
     * 查询所有的应用
     *
     * @param ids 应用ids集合
     *
     * @return 返回应用集合列表
     */
    public List<KnApplicationInfo> findAllList(List<Long> ids){
        if(Utils.isEmpityCollection(ids)){
            return (List<KnApplicationInfo>)knApplicationInfoDao.findAll();
        }else{
            ids.add(0L);
            return (List<KnApplicationInfo>)knApplicationInfoDao.findAll(ids);
        }
    }
    /**
     * 处理应用时间问题
     *
     * @param appList 需要处理的应用集合
     *
     * @return 返回处理后的应用集合
     */
    private List<KnApplicationInfo> setAppInfoTime(List<KnApplicationInfo> appList){
        List<KnApplicationInfo> finalList=new ArrayList<>();
        if(!Utils.isEmpityCollection(appList)){
            for(KnApplicationInfo knObj : appList){
                knObj.setUpdateTime(null);
                knObj.setCreateTime(null);
                finalList.add(knObj);
            }
        }
        return finalList;
    }
    /**
     * 处理功能时间问题
     *
     * @param funList 需要处理的功能集合
     *
     * @return 返回处理后的功能集合
     */
    private List<KnResource> setFunInfoTime(List<KnResource> funList){
        List<KnResource> finalList=new ArrayList<>();
        if(!Utils.isEmpityCollection(funList)){
            for(KnResource knObj : funList){
                knObj.setUpdateTime(null);
                knObj.setCreateTime(null);
                finalList.add(knObj);
            }
        }
        return finalList;
    }
    /**
     * 将本地数据同步到云端
     *
     * @param ids 应用id集合
     * @param tip 0 应用全部同步  1 应用部分同步 2功能全部同步  3 功能部门同步
     *
     * @return 返回是否更新成功标示
     */
    public String SynToCloud(List<Long> ids,String tip){
        String returnInfo="", methodType="";
        Map<String,Object> map=new HashMap<>();
        if("0".equals(tip)){
            map.put("appInfo",setAppInfoTime((List<KnApplicationInfo>)knApplicationInfoDao.findAll()));
            methodType="updateApplication";
        }else if("1".equals(tip)){
            ids.add(0L);
            map.put("appInfo",setAppInfoTime((List<KnApplicationInfo>)knApplicationInfoDao.findAll(ids)));
            methodType="updateApplication";
        }else if("2".equals(tip)){
            map.put("functionInfo",setFunInfoTime((List<KnResource>)knResourceDao.findByResourceType(KnResource.ResourceType.FUNCTION)));
            methodType="updateFunction";
        }else if("3".equals(tip)){
            ids.add(0L);
            map.put("functionInfo",setFunInfoTime((List<KnResource>)knResourceDao.findAll(ids)));
            methodType="updateFunction";
        }
        try{
            String sendInfo=AES.Encrypt(JsonMapper.nonEmptyMapper().toJson(map).toString(),null);
            sendInfo=getSendInfo(sendInfo,localUrl,methodType,"1",companyMark,tip);
            StreamSource source=new StreamSource(new StringReader(sendInfo));
            StringWriter writerStr=new StringWriter();
            StreamResult result=new StreamResult(writerStr);
            if("0".equals(tip)||"1".equals(tip)){
                appWst.sendSourceAndReceiveToResult(source,result);
            }else if("2".equals(tip)||"3".equals(tip)){
                functionWst.sendSourceAndReceiveToResult(source,result);
            }
            returnInfo=writerStr.getBuffer().toString();
            if(Utils.isEmptyString(returnInfo)){
                Map<String,String> backMap=new HashMap<>();
                backMap.put("resultCode",Setting.SUCCESSSTAT);
                backMap.put(Setting.MESSAGE,"后台数据异常,请稍后重试");
                returnInfo=JsonMapper.nonEmptyMapper().toJson(backMap);
            }else{
                returnInfo=XmlUtil.getCenterStr(new String(returnInfo.getBytes("utf-8")),"return");
                returnInfo=AES.Decrypt(returnInfo,null);
            }
        }catch(Exception e){
            Map<String,String> backMap=new HashMap<>();
            backMap.put("resultCode",Setting.FAIURESTAT);
            backMap.put(Setting.MESSAGE,"后台数据异常,请稍后重试");
            returnInfo=JsonMapper.nonEmptyMapper().toJson(backMap);
        }
        return returnInfo;
    }
    /**
     * 将云端的数据同步到本地
     *
     * @param tip 0 应用更新  1 功能更新
     *
     * @return 返回前台是否同步成功标示
     */
    @Transactional(readOnly=false)  //  关闭只读 ,对数据库有操作
    public Map<String,String> SynFromCloud(String tip){
        Map<String,String> map=new HashMap<>();
        String methodType="";
        try{
            if("0".equals(tip)){
                methodType="getApplication";
            }else if("1".equals(tip)){
                methodType="getFunction";
            }
            String sendInfo=getSendInfo(null,localUrl,methodType,"1",companyMark,tip);
            StreamSource source=new StreamSource(new StringReader(sendInfo));
            StringWriter writerStr=new StringWriter();
            StreamResult result=new StreamResult(writerStr);
            if("0".equals(tip)){
                appWst.sendSourceAndReceiveToResult(source,result);
            }else if("1".equals(tip)){
                functionWst.sendSourceAndReceiveToResult(source,result);
            }
            String returnInfo=writerStr.getBuffer().toString();
            if(Utils.isEmptyString(returnInfo)){
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"返回的数据为空,没有需要更新数据");
            }else{
                Map<String,KnApplicationInfo> appMap=new HashMap<>();
                Map<String,KnResource> funMap=new HashMap<>();
                if("0".equals(tip)){
                    List<KnApplicationInfo> appList=(List<KnApplicationInfo>)knApplicationInfoDao.findAll();
                    for(KnApplicationInfo obj : appList){
                        if(!Utils.isEmptyString(obj.getApiKey())){
                            appMap.put(obj.getApiKey(),obj);
                        }
                    }
                }else if("1".equals(tip)){
                    List<KnResource> functionList=(List<KnResource>)knResourceDao.findByResourceType(KnResource.ResourceType.FUNCTION);
                    for(KnResource obj : functionList){
                        if(!Utils.isEmptyString(obj.getId())){
                            funMap.put(String.valueOf(obj.getId()),obj);
                        }
                    }
                }
                returnInfo=XmlUtil.getCenterStr(new String(returnInfo.getBytes("utf-8")),"return");
                returnInfo=AES.Decrypt(returnInfo,null);
                if(Utils.isEmptyString(returnInfo)){
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"返回的数据为空,没有需要更新数据");
                }else{
                    if("0".equals(tip)){
                        map=updateApplicationInfo(appMap,returnInfo);
                    }else if("1".equals(tip)){
                        map=updateFunctionInfo(funMap,returnInfo);
                    }
                }
            }
        }catch(RuntimeException e){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,请稍后重试");
        }catch(Exception e){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,请稍后重试");
        }
        return map;
    }
    /**
     * 更新本地功能信息
     *
     * @param funMap     本地 功能map集合
     * @param returnInfo 云端返回需要更新的功能string
     *
     * @return 更新成功,失败 信息
     */
    private Map<String,String> updateFunctionInfo(Map<String,KnResource> funMap,String returnInfo){
        Map<String,String> map=new HashMap<>();
        try{
            returnInfo=Utils.isEmptyStr(returnInfo)?"":returnInfo.substring(16,returnInfo.length()-1);
            JavaType javaType=getCollectionType(ArrayList.class,FunctionCloudDto.class);
            if(Utils.isEmptyString(returnInfo)){
                map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                map.put(Setting.MESSAGE,"本地与云端数据一致,不需更新");
            }else{
                List<FunctionCloudDto> functionList=(List<FunctionCloudDto>)jacksonMapper.readValue(returnInfo,javaType);
                if(!Utils.isEmpityCollection(functionList)){
                    String path=PathUtil.getRootPath();
                    for(FunctionCloudDto funObj : functionList){
                        if(!funMap.containsKey(funObj.getId())){
                            KnResource knObj=new KnResource();
                            knObj.setName(funObj.getMtitle());
                            knObj.setEnTitle(funObj.getEnTitle());
                            knObj.setMarkName(funObj.getMarkName());
                            knObj.setVersion(funObj.getVersion());
                            knObj.setDescription(funObj.getRemark());
                            knObj.setType(KnResource.ResourceType.FUNCTION);
                            String icon=funObj.getIcon();
                            boolean bool=FileUtil.getInstance().saveFileByUrl(kndCloudUrl+icon,path+icon);
                            knResourceDao.save(knObj);
                        }
                    }
                }
                map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                map.put(Setting.MESSAGE,"更新成功");
            }
        }catch(JsonMappingException e){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台数据异常,请稍后重试");
        }catch(JsonParseException e){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台数据异常,请稍后重试");
        }catch(IOException e){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台数据异常,请稍后重试");
        }
        return map;
    }
    /**
     * 更新本地应用信息
     *
     * @param appMap  本地 应用map集合
     * @param appInfo 云端返回需要更新的应用string
     *
     * @return 更新成功,失败 信息
     */
    @Transactional(readOnly=false)  //  关闭只读 ,对数据库有操作
    private Map<String,String> updateApplicationInfo(Map<String,KnApplicationInfo> appMap,String appInfo){
        Map<String,String> map=new HashMap<>();
        try{
            appInfo=Utils.isEmptyStr(appInfo)?"":appInfo.substring(11,appInfo.length()-1);
            JavaType javaType=getCollectionType(ArrayList.class,ApplicationCloudDto.class);
            if(Utils.isEmptyString(appInfo)){
                map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                map.put(Setting.MESSAGE,"本地与云端数据一致,不需更新");
            }else{
                List<ApplicationCloudDto> appList=(List<ApplicationCloudDto>)jacksonMapper.readValue(appInfo,javaType);
                if(!Utils.isEmpityCollection(appList)){
                    String path=PathUtil.getRootPath();
                    for(ApplicationCloudDto appObj : appList){
                        if(!appMap.containsKey(appObj.getApiKey())){
                            KnApplicationInfo knObj=new KnApplicationInfo();
                            knObj.setTitle(appObj.getTitle());
                            knObj.setEnTitle(appObj.getEnTitle());
                            knObj.setApiKey(appObj.getApiKey());
                            knObj.setForFirm(appObj.getForFirm());
                            knObj.setRemark(appObj.getRemark());
                            knObj.setDownLoadUrl(appObj.getDownLoadUrl());
                            knObj.setTotalDowns(appObj.getDowncount());
                            String icon=appObj.getIcon();
                            boolean bool=FileUtil.getInstance().saveFileByUrl(kndCloudUrl+icon,path+icon);
                            knApplicationInfoDao.save(knObj);
                        }
                    }
                }
                map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                map.put(Setting.MESSAGE,"更新成功");
            }
        }catch(JsonMappingException e){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台数据异常,请稍后重试");
        }catch(JsonParseException e){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台数据异常,请稍后重试");
        }catch(IOException e){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台数据异常,请稍后重试");
        }
        return map;
    }
    /**
     * json字符串转换为对象
     *
     * @param collectionClass List
     * @param elementClasses  转换后的对象class
     *
     * @return
     */
    private JavaType getCollectionType(Class<?> collectionClass,Class<?>... elementClasses){
        return jacksonMapper.getTypeFactory().constructParametricType(collectionClass,elementClasses);
    }
    /**
     * 根据传入的关键字,对云端的访问的请求参数进行设定updateApplication  getApplication
     *
     * @param info       应用/功能清单集合
     * @param methodName 访问的方法
     * @param isEncrypt  是否进行AES加密,0表示不加密,1表示加密
     * @param tip        0 ,1 应用  2,3功能
     *
     * @return
     */
    private String getSendInfo(String info,String fromUrl,String methodName,String isEncrypt,String companyMark,String tip){
        StringBuffer sbf=new StringBuffer();
        sbf.append("<").append(methodName).append(" xmlns:ser=\"http://service.webservice.cloud.kingnode.com/\">");
        if(!Utils.isEmptyString(info)){
            if("0".equals(tip)||"1".equals(tip)){
                sbf.append("<appInfo>").append(info).append("</appInfo>");
            }else if("2".equals(tip)||"3".equals(tip)){
                sbf.append("<functionInfo>").append(info).append("</functionInfo>");
            }
        }
        sbf.append("<fromUrl>");
        if(!Utils.isEmptyString(fromUrl)){
            sbf.append(fromUrl);
        }
        sbf.append("</fromUrl>");
        sbf.append("<companyMark>");
        if(!Utils.isEmptyString(companyMark)){
            sbf.append(companyMark);
        }
        sbf.append("</companyMark>");
        sbf.append("<isEncrypt>");
        if(!Utils.isEmptyString(isEncrypt)){
            sbf.append(isEncrypt);
        }
        sbf.append("</isEncrypt>");
        sbf.append("</").append(methodName).append(">");
        return sbf.toString();
    }
    /**
     * 应用评论信息列表
     *
     * @param dt  表格数据
     * @param map
     *
     * @return 返回列表信息
     */
    public DataTable<ScoreDto> GetAppSoreList(DataTable<ScoreDto> dt,Map<String,String> map) throws ParseException{
        List<ScoreDto> finalCrmList=getAppSocreList(map);
        dt.setiTotalDisplayRecords(finalCrmList.size());
        finalCrmList=getPagerList(dt.getiDisplayStart(),dt.getiDisplayLength(),finalCrmList);//对数据进行重新分页
        dt.setAaData(finalCrmList);
        return dt;
    }
    /**
     * 获取列表信息
     *
     * @param map 查询条件
     *
     * @return 返回列表信息
     */
    private List<ScoreDto> getAppSocreList(Map<String,String> map) throws ParseException{
        String beginTime=map.get("beginTime"), endTime=map.get("endTime"), versionNum=map.get("versionNum"), rating=map.get("rating"), userName=map.get("userName"), appComment=map.get("appComment"), plateForm=map.get("plateForm");
        userName=Utils.isEmptyString(userName)?"%%":"%"+userName+"%";
        appComment=Utils.isEmptyString(appComment)?"%%":"%"+appComment+"%";
        rating=Utils.isEmptyString(rating)?"%%":"%"+rating+"%";
        versionNum=Utils.isEmptyString(versionNum)?"%%":"%"+versionNum+"%";
        for(Setting.VersionType obj : Setting.VersionType.values()){
            if(obj.name().equals(plateForm)){
                plateForm=obj.name();
                break;
            }
        }
        Long begin=Utils.isEmptyString(beginTime)?0:getStartOrEndTime(beginTime,"start",null),
                end=Utils.isEmptyString(endTime)?System.currentTimeMillis():getStartOrEndTime(endTime,"end",null);
        List<ScoreDto> list=new ArrayList<>();
        if(Utils.isEmptyString(plateForm)){
            list=knAppScoreInfoDao.findListByVerAndRatAndTime(versionNum,rating,begin,end,userName,appComment);
        }else{
            list=knAppScoreInfoDao.findListByVerAndRatAndTypeAndTime(versionNum,rating,begin,end,userName,appComment,Setting.VersionType.valueOf(plateForm));
        }
        return list;
    }
    /**
     * 对数据进行分页处理
     *
     * @param disPlayStart 开始数据
     * @param disPlayEnd   结束数据
     * @param crmList      需要分页的数据
     *
     * @return 返回分页后的数据
     */
    private List<ScoreDto> getPagerList(Integer disPlayStart,Integer disPlayEnd,List<ScoreDto> crmList){
        List<ScoreDto> newList=new ArrayList<ScoreDto>();
        int tempLeng=disPlayStart+disPlayEnd, totalSize=crmList.size();
        if(tempLeng>totalSize){
            newList=crmList.subList(disPlayStart,totalSize);
        }else{
            newList=crmList.subList(disPlayStart,tempLeng);
        }
        return newList;
    }
    /**
     * app评论导出信息
     *
     * @param response   响应的请求
     * @param appScorMap 查询条件
     * @param ids
     */
    public void ExceptAppScoreInfo(HttpServletResponse response,Map<String,String> appScorMap,List<Long> ids) throws ParseException{
        String excelTitle="excel 标题";
        List<ScoreDto> finalCrmList=new ArrayList<>();
        String excelFilePath=PathUtil.getRootPath()+Setting.BASEADDRESS+Setting.excelAddress;
        Map<String,List<String[]>> map=new HashMap();//导出excel 内容
        List<String[]> arrayList=new ArrayList<String[]>();
        arrayList.add(new String[]{"序号","用户名称","版本","评论时间","评分","评论内容"}); //列头
        String tip=appScorMap.get("exportType");
        if("0".equals(tip)){
            finalCrmList=knAppScoreInfoDao.findAllList();
        }else if("1".equals(tip)){
            ids.add(0L);
            finalCrmList=knAppScoreInfoDao.findAllListByIds(ids);
        }else if("2".equals(tip)){
            finalCrmList=getAppSocreList(appScorMap);
        }
        if(!Utils.isEmpityCollection(finalCrmList)){
            int j=0;
            for(ScoreDto obj : finalCrmList){
                j++;
                String createTime=Utils.isEmptyStr(obj.getCreateTime())?"":new DateTime(obj.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss");
                arrayList.add(new String[]{String.valueOf(j),obj.getUserName(),obj.getVersionNum(),createTime,obj.getRating(),obj.getAppComment()});
            }
        }
        excelTitle="APP-评论导出信息";
        map.put(excelTitle,arrayList);
        DownExcel downExcel=DownExcel.getInstall();
        downExcel.downLoadFile(response,downExcel.exportXlsExcel(map,excelFilePath,String.valueOf(System.currentTimeMillis())),excelTitle,true); //导出2003 excel
    }
    /**
     * 全选删除应用评论信息
     *
     * @param ids 评论id集合
     */
    @Transactional(readOnly=false)  //  关闭只读 ,对数据库有操作
    public void DeleteAllAppScoreListByIds(List<Long> ids){
        ids.add(0L);
        List<KnScoreInfo> list=(List<KnScoreInfo>)knAppScoreInfoDao.findAll(ids);
        knAppScoreInfoDao.delete(list);
    }
    /**
     * @param timeString 时间字符串
     * @param tip        标示获取开始还是结束时间
     * @param date       当前时间
     *
     * @return 返回long时间
     *
     * @throws java.text.ParseException
     * @Description: (获取当天的开始 与结束时间)
     */
    public Long getStartOrEndTime(String timeString,String tip,Date date) throws ParseException{
        SimpleDateFormat formater=new SimpleDateFormat("yyyy-MM-dd");
        if(Utils.isEmptyString(timeString)){
            timeString=formater.format(date);
        }
        if("start".equalsIgnoreCase(tip)){
            timeString=formater.format(formater.parse(timeString))+" 00:00:00";
        }else{
            timeString=formater.format(formater.parse(timeString))+" 23:59:59";
        }
        return DateTime.parse(timeString,DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).getMillis();
    }
}
