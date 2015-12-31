package com.kingnode.xsimple.service.mobile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.common.base.Strings;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.ShiroUser;
import com.kingnode.xsimple.dao.application.KnApplicationInfoDao;
import com.kingnode.xsimple.dao.application.KnRoleApplicationInfoDao;
import com.kingnode.xsimple.dao.application.KnRoleModuleFunctionInfoDao;
import com.kingnode.xsimple.dao.application.KnVersionInfoDao;
import com.kingnode.xsimple.dao.push.KnCertificateDao;
import com.kingnode.xsimple.dao.push.KnDeviceInfoDao;
import com.kingnode.xsimple.dao.push.KnMessageDao;
import com.kingnode.xsimple.dao.system.KnEmployeeDao;
import com.kingnode.xsimple.dao.system.KnFunctionVersionDao;
import com.kingnode.xsimple.dao.system.KnResourceDao;
import com.kingnode.xsimple.dao.system.KnUserDao;
import com.kingnode.xsimple.dto.FullEmployeeDTO;
import com.kingnode.xsimple.dto.FunctionDTO;
import com.kingnode.xsimple.dto.MobileRegisterDTO;
import com.kingnode.xsimple.dto.MobileUserOrgDTO;
import com.kingnode.xsimple.dto.ModuleDTO;
import com.kingnode.xsimple.dto.RoleDTO;
import com.kingnode.xsimple.dto.SimpleFunctionDTO;
import com.kingnode.xsimple.dto.SimpleModuleDTO;
import com.kingnode.xsimple.entity.application.KnApplicationInfo;
import com.kingnode.xsimple.entity.application.KnVersionInfo;
import com.kingnode.xsimple.entity.push.KnCertificateInfo;
import com.kingnode.xsimple.entity.push.KnDeviceInfo;
import com.kingnode.xsimple.entity.push.KnPushMessageInfo;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnResource;
import com.kingnode.xsimple.entity.system.KnRole;
import com.kingnode.xsimple.entity.system.KnUser;
import com.kingnode.xsimple.entity.web.KnFunctionVersionInfo;
import com.kingnode.xsimple.entity.web.KnRoleApplicationInfo;
import com.kingnode.xsimple.entity.web.KnRoleModuleFunctionInfo;
import com.kingnode.xsimple.util.Users;
import com.kingnode.xsimple.util.Utils;
import com.kingnode.xsimple.util.push.IosPushUtil;
import com.kingnode.xsimple.util.version.VersionNumUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author kongjiangwei@kingnode.com
 */
@Component @Transactional(readOnly=true)
public class MobileRestService{

    private static Logger logger=LoggerFactory.getLogger(MobileRestService.class);
    private KnEmployeeDao knEmployeeDao;
    private KnVersionInfoDao knVersionInfoDao;
    private KnApplicationInfoDao knApplicationInfoDao;
    private KnDeviceInfoDao knDeviceInfoDao;
    private KnCertificateDao knCertificateDao;
    private KnMessageDao messDao;
    private KnUserDao knUserDao;
    private KnRoleModuleFunctionInfoDao knRoleModuleFunctionInfoDao;
    private KnRoleApplicationInfoDao roleApplicationInfoDao;//应用角色中间表
    private KnResourceDao knResourceDao;
    private KnFunctionVersionDao knFunctionVersionDao;
    @Value("#{commonInfo['androidHttpUrl']}")
    private String androidHttpUrl;//ANDROID 推送连接的地址
    @Value("#{commonInfo['kimHost']}")
    private String kimDomain;//KIM的Domain地址
    @Value("#{commonInfo['kimDomain']}")
    private String kimHost;//KIM的host地址
    @Value("#{commonInfo['kndCloudUrl']}")
    private String kndCloudUrl;
    @Autowired
    public void setKnEmployeeDao(KnEmployeeDao knEmployeeDao){
        this.knEmployeeDao=knEmployeeDao;
    }
    @Autowired
    public void setKnVersionInfoDao(KnVersionInfoDao knVersionInfoDao){
        this.knVersionInfoDao=knVersionInfoDao;
    }
    @Autowired
    public void setKnApplicationInfoDao(KnApplicationInfoDao knApplicationInfoDao){
        this.knApplicationInfoDao=knApplicationInfoDao;
    }
    @Autowired
    public void setKnDeviceInfoDao(KnDeviceInfoDao knDeviceInfoDao){
        this.knDeviceInfoDao=knDeviceInfoDao;
    }
    @Autowired
    public void setKnCertificateDao(KnCertificateDao knCertificateDao){
        this.knCertificateDao=knCertificateDao;
    }
    @Autowired
    public void setMessDao(KnMessageDao messDao){
        this.messDao=messDao;
    }
    @Autowired
    public void setKnUserDao(KnUserDao knUserDao){
        this.knUserDao=knUserDao;
    }
    @Autowired
    public void setRoleApplicationInfoDao(KnRoleApplicationInfoDao roleApplicationInfoDao){
        this.roleApplicationInfoDao=roleApplicationInfoDao;
    }
    @Autowired
    public void setKnRoleModuleFunctionInfoDao(KnRoleModuleFunctionInfoDao knRoleModuleFunctionInfoDao){
        this.knRoleModuleFunctionInfoDao=knRoleModuleFunctionInfoDao;
    }
    @Autowired
    public void setKnResourceDao(KnResourceDao knResourceDao){
        this.knResourceDao=knResourceDao;
    }
    @Autowired
    public void setKnFunctionVersionDao(KnFunctionVersionDao knFunctionVersionDao){
        this.knFunctionVersionDao=knFunctionVersionDao;
    }
    /**
     * 注册设备信息
     *
     * @param mrd 手机端传入参数
     *                参数含义如下： {"xtype":"mdmRegisterInfo","appkey":"应用标示","totken":"设备标示","plateform":"设备来自平台","version":"版本号","userPhoneName":"设备型号","versionType":"版本的状态","zipVersion":"公共包的版本号","userPhone":"手机号码","regTime":"最后登录时间","fromSys":"设备来自产品"}
     *                例如：        {"xtype":"mdmRegisterInfo","appkey":"111111111123213","totken":"sdfadsfadsasdasd1123123","plateform":"IPHONE","version":"1.35","userPhoneName":"小米3","versionType":"usable","zipVersion":"1.3","userPhone":"31649714646","regTime":"2014-07-01 18:58:12","fromSys":"eam"}
     *
     * @return
     */
    @Transactional(readOnly=false)  //  关闭只读 ,对数据库有操作
    public MobileUserOrgDTO MdmRegisterInfo(MobileRegisterDTO mrd){
        long chanId=Long.valueOf(0);
        MobileUserOrgDTO dto=new MobileUserOrgDTO();
        try{
            Map userMap=getUserInfo();
            if(Setting.FAIURESTAT.equals(userMap.get(Setting.RESULTCODE))){
                dto.setErrorCode(Setting.FAIURESTAT);
                dto.setErrorMessage(userMap.get(Setting.MESSAGE).toString());
                dto.setStatus(false);
                return dto;
            }
            String user_id=(String)userMap.get("userId");//用户id
            String user_sys=(String)userMap.get("userSystem");  //用户来自系统
            String app_key=mrd.getAppKey(); //应用的标示
            String totken=mrd.getToken(); //设备totken
            String versionType=mrd.getVersionType();//版本的状态
            String plate_form=mrd.getPlateform();   //设备来自的平台 (IPHONE  android)
            String version=mrd.getVersion();  //版本号
            String regTime=mrd.getRegTime();  //最后登录时间
            List<KnApplicationInfo> knApplicationInfoList=knApplicationInfoDao.findApplicationByAppkey(app_key);
            if(Utils.isEmpityCollection(knApplicationInfoList)){
                dto.setErrorCode(Setting.FAIURESTAT);
                dto.setErrorMessage("应用不存在");
                dto.setStatus(false);
                return dto;
            }
            List<KnDeviceInfo> devList=knDeviceInfoDao.findListByTotken(totken);
            Map devMap=checkDeviceIsOrDelete(devList,user_id,user_sys);
            if(Setting.FAIURESTAT.equals(devMap.get(Setting.RESULTCODE))||Setting.DeleteStatusType.device.name().equals(devMap.get("DELEOFTYPE"))||Setting.DeleteStatusType.account.name().equals(devMap.get("DELEOFTYPE"))){
                dto.setErrorCode(Setting.FAIURESTAT);
                dto.setErrorMessage(userMap.get(Setting.MESSAGE).toString());
                dto.setStatus(false);
                return dto;
            }
            List<KnDeviceInfo> channelInfoList=knDeviceInfoDao.findByUsrIdAndFormSys(user_id,user_sys,app_key,totken);
            KnDeviceInfo chInf=null;
            if(Utils.isEmpityCollection(channelInfoList)){ //设备信息不存在
                mrd.setUserId(user_id);
                mrd.setUserSystem(user_sys);
                Map<String,Object> chanMap=createChannelInfo(mrd);
                if((Boolean)chanMap.get("bool")){
                    chanId=(long)chanMap.get("chanId");
                    chInf=(KnDeviceInfo)chanMap.get("KnDeviceInfo");
                }
            }else{
                chInf=channelInfoList.get(0);
                chInf.setChversion(version);
                chInf.setUpdateTime(System.currentTimeMillis());
                chInf.setUserPhone(mrd.getUserPhone());
                chInf.setDeviceName(mrd.getUserPhoneName());
                chInf.setOnlineStat(Setting.OnlineType.online.name());
                for(Setting.WorkStatusType obj : Setting.WorkStatusType.values()){
                    if(obj.name().equals(versionType)){
                        chInf.setWorkStatus(Setting.WorkStatusType.valueOf(versionType));
                        break;
                    }
                }
                knDeviceInfoDao.save(chInf);
                chanId=chInf.getId();
            }
            final String pushMessname = chInf.getPushMessname() ;
            Boolean sendOffmessage = mrd.isSendOffmessage();
            if(sendOffmessage) sendOfflineMess(pushMessname) ;
            dto=checkNewVerList(app_key,plate_form,versionType,version);
            if(Setting.SUCCESSSTAT.equals(dto.getErrorCode())){
                if(null!=chInf){
                    dto.setRegTime(chInf.getUpdateTime());
                }
            }
            List<KnDeviceInfo> updateList=null;//推送设备信息
            if(Utils.isEmptyString(regTime)){
                updateList=knDeviceInfoDao.findByAppkeyAndStat(user_id,user_sys,app_key,totken,Setting.OnlineType.online.name());
            }else{
                updateList=knDeviceInfoDao.findByAppkeyAndStatAndTime(user_id,user_sys,app_key,totken,Setting.OnlineType.online.name(),Long.valueOf(regTime));
            }
            setOffList(updateList,totken,chanId);//设置下线状态
            Long time = Utils.isEmptyStr(chInf.getUpdateTime()) ?chInf.getCreateTime():chInf.getUpdateTime();
            pushInfo(updateList,app_key,chInf.getDeviceName(),time);//推送下线信息
        }catch(Exception e){
            logger.error("注册设备信息,错误信息 -----> "+e);
            dto.setErrorCode(Setting.FAIURESTAT);
            dto.setErrorMessage("后台异常,稍后尝试");
            dto.setStatus(false);
        }
        return dto;
    }

    /**
     * 根据用户的主键id和appkey获取用户的信息和角色信息等
     *
     * @param uid    用户的主键id
     * @param appkey 应用的appkey
     *
     * @return
     */
    public MobileUserOrgDTO GetUserInfo(Long uid,String appkey,MobileUserOrgDTO muodto){
        KnUser loginUser=knUserDao.findOne(uid);
        KnEmployee employee=knEmployeeDao.findOne(uid);
        if(loginUser==null||employee==null){
            muodto.setStatus(false);
            muodto.setErrorCode(Setting.FAIURESTAT);
            muodto.setErrorMessage("账号不存在,请重新登录");
            return muodto;
        }
        final String markName=employee.getMarkName();
        final String userSys=employee.getUserSystem();
        FullEmployeeDTO employeeDTO=new FullEmployeeDTO();
        employeeDTO.setId(loginUser.getId());
        employeeDTO.setLoginName(loginUser.getLoginName());
        employeeDTO.setEmail(loginUser.getEmail());
        employeeDTO.setAddress(employee.getAddress());
        employeeDTO.setImageAddress(employee.getImageAddress());
        employeeDTO.setUserName(loginUser.getName());
        employeeDTO.setTelephone(employee.getPhone());
        employeeDTO.setSignature(employee.getSignature());
        employeeDTO.setUserSystem(employee.getUserSystem());
        employeeDTO.setUserType(employee.getUserType());
        employeeDTO.setUserId(employee.getUserId());
        muodto.setFullEmployeeDTO(employeeDTO);
        //查询用户下的角色列表
        List<RoleDTO> roleDTOs=new ArrayList();
        List<KnRole> roleInfoList=loginUser.getRole();
        if(roleInfoList!=null&&roleInfoList.size()!=0){
            //查询应用底下的角色信息进行过滤
            List<Long> roleAppIds = findRoleIdByAppkey(appkey);
            for(KnRole roleInfo : roleInfoList){
                //用户的角色在此应用中才给数据
                if(roleAppIds.contains(roleInfo.getId())){
                    RoleDTO roleDTO=new RoleDTO();
                    roleDTO.setRoleId(roleInfo.getId());
                    roleDTO.setRoleName(roleInfo.getName());
                    roleDTOs.add(roleDTO);
                }
            }
            muodto.setRoleDTOList(roleDTOs);
        }
        //将云端的地址,kim的地址等信息返回
        muodto.setKimDomain(kimDomain);//kim的demo地址
        muodto.setKimHost(kimHost);//kim的host地址
        muodto.setKndCloudUrl(kndCloudUrl);//云端的地址
        muodto.setStatus(true);
        muodto.setErrorCode(Setting.SUCCESSSTAT);
        muodto.setErrorMessage("获取成功");
        return muodto;
    }

    /**
     * 根据用户的之间id和相应模块功能等信息,比较相应的地址
     *
     * @param uid       用户的之间id
     * @param appKey    模块功能json
     * @param weixinFlag 返回的功能包是zip还是unzip
     *
     * @return
     */
    public MobileUserOrgDTO GetModeulFunctionByUserId(Long uid,String appKey,List<ModuleDTO> moduleDTOs,MobileUserOrgDTO rtnDTO,boolean weixinFlag){
        try{
            KnUser loginUser=knUserDao.findOne(uid);
            if(loginUser==null){
                rtnDTO.setStatus(false);
                rtnDTO.setErrorCode(Setting.FAIURESTAT);
                rtnDTO.setErrorMessage("用户失效,请重新登录");
                return rtnDTO;
            }
            List<KnRole> roleInfoList=loginUser.getRole();
            //根据应用的appkey查询应用下的角色信息,如果传输了appkey,则过滤appkey获取角色信息
            List<Long> roleAppIds = findRoleIdByAppkey(appKey);
            List<Long> roleIds=new ArrayList<Long>();
            for(KnRole roleInfo : roleInfoList){
                if(Utils.isEmpityCollection(roleAppIds)){
                    roleIds.add(roleInfo.getId());
                }else{
                    //传输了appkey,获取该应用信息底下的角色信息
                    if(roleAppIds.contains(roleInfo.getId())){
                        roleIds.add(roleInfo.getId());
                    }
                }
            }
            //查询模块在角色中的排序情况
            List<KnRoleModuleFunctionInfo> roleMouleInfos=knRoleModuleFunctionInfoDao.getRMInfoOnlyByRoles(roleIds);
            Map<String,KnRoleModuleFunctionInfo> mInRMap=new HashMap<String,KnRoleModuleFunctionInfo>();
            //模块角色映射
            Map<Long,Long> mRMap=new HashMap<>();
            for(KnRoleModuleFunctionInfo info : roleMouleInfos){
                String key=info.getRoleId()+"-"+info.getModuleId();
                if(!mInRMap.containsKey(key)){
                    mInRMap.put(key,info);
                }
                if(!mRMap.containsKey(info.getModuleId())){
                    mRMap.put(info.getModuleId(),info.getRoleId());
                }
            }
            //存放模块信息
            List moduleList=new ArrayList();
            List sortModuleJa=new ArrayList();//模块排序的sort数组
            //查询角色下面的模块,模块下面的功能
            //角色下的模块
            List<KnResource> moduleInfoList=knResourceDao.findModuleByRoleIds(roleIds,KnResource.ResourceType.MODULE);
            //获取传输过来的集合
            Map<String,Object> map=getModuleMap(moduleDTOs);
            Map<Long,String> moduleMap=(Map<Long,String>)map.get("module");
            Map<Long,Map<Long,String>> functionMap=(Map<Long,Map<Long,String>>)map.get("function");
            Map<String,String> zipMap=(Map<String,String>)map.get("zip");
            List funKey=new ArrayList();
            for(KnResource moduleInfo : moduleInfoList){
                SimpleModuleDTO simpleModuleDTO=new SimpleModuleDTO();//模块排序
                simpleModuleDTO.setModuleId(moduleInfo.getId());
                List sortFarr=new ArrayList();//模块下功能的排序数组
                //模块信息
                ModuleDTO moduleDTO=new ModuleDTO();
                String key=mRMap.get(moduleInfo.getId())+"-"+moduleInfo.getId();
                simpleModuleDTO.setmSortId(mInRMap.get(key).getRmSort());
                moduleDTO.setModuleId(moduleInfo.getId());
                moduleDTO.setMtitle(moduleInfo.getName());
                moduleDTO.setmVersion(moduleInfo.getVersion());
                moduleDTO.setmSortId(mInRMap.get(key).getRmSort());
                //模块下的功能
                List<KnResource> functionInfoList=knResourceDao.findFunctionByRoleIdsAndModuleId(roleIds,moduleInfo.getId(),KnResource.ResourceType.FUNCTION);
                //获取功能在模块下的排序
                List<KnRoleModuleFunctionInfo> funcInMolduleList=knRoleModuleFunctionInfoDao.findByRoleIdsAndMoludeId(roleIds,moduleInfo.getId());
                Map<Long,Long> funcInModuleMap=new HashMap<Long,Long>();
                for(KnRoleModuleFunctionInfo info : funcInMolduleList){
                    if(!funcInModuleMap.containsKey(info.getFunctionId())){
                        funcInModuleMap.put(info.getFunctionId(),info.getMfSort());
                    }
                }
                //存放模块下的功能集合
                List functionJa=new ArrayList();
                            /*Map<String,Map<String,String>> funMapList = getFunctionMap(jsonMap);
                            Map<String,String> funcMap =  funMapList.get("function");
                            Map<String,String> zipsMap =  funMapList.get("zip");*/
                //角色下面功能的map集合
                Map<Long,KnResource> functionRoleMap=new HashMap<Long,KnResource>();
                for(KnResource functionInfo : functionInfoList){
                    functionRoleMap.put(functionInfo.getId(),functionInfo);
                }
                //用于存放功能下面的公用包的信息,存放到所有功能的最后
                List publicJa=new ArrayList();
                Map<Long,String> tempMap=new HashMap<Long,String>();
                for(KnResource functionInfo : functionInfoList){
                    tempMap=functionMap.get(moduleInfo.getId().toString());
                    //将角色中有的功能加入
                    if(functionRoleMap.containsKey(functionInfo.getId())){
                        FunctionDTO functionDTO=new FunctionDTO();
                        SimpleFunctionDTO simpleFunctionDTO=new SimpleFunctionDTO();//模块下的功能的排序
                        functionDTO.setfSortId(funcInModuleMap.get(functionInfo.getId())==null?0:funcInModuleMap.get(functionInfo.getId()));
                        boolean flag=true;
                        KnFunctionVersionInfo fv=getVersionByFunction(functionInfo);
                        //数据库中的 功能  要跟传过来的 功能个数相同 并且 版本号也要完全一样
                        if(tempMap==null||!tempMap.containsKey(functionInfo.getId().toString())){//功能不存在
                            functionDTO.setK2(Setting.ADD);
                        }else if(functionInfo.getVersion()!=null&&!functionInfo.getVersion().equals(tempMap.get(functionInfo.getId().toString()))){//功能存在且版本不同
                            functionDTO.setK2(Setting.UPDATE);
                        }else{
                            //功能存在,版本一致,(zip版本一致,zip版本不一致);功能存在版本不一致(zip版本一致,zip版本不一致);
                            //功能存在且zip版本不同
                            if(fv!=null&&fv.getZipVersion()!=null&&!fv.getZipVersion().equals(zipMap.get(functionInfo.getId().toString()))){
                                functionDTO.setK2(Setting.UPDATE);
                            }else{
                                flag=false;
                            }
                        }
                        if(flag){
                            functionDTO.setFunctionId(functionInfo.getId());
                            functionDTO.setFtitle(functionInfo.getName());
                            functionDTO.setfVersion(functionInfo.getVersion());
                            functionDTO.setfIcon(functionInfo.getIcon());
                            if(weixinFlag){
                                functionDTO.setZip(fv==null?"":fv.getUnZipUrl());
                            }else{
                                functionDTO.setZip(fv==null?"":fv.getFuncZipUrl());
                            }
                            functionDTO.setfVersion(fv==null?"":fv.getZipVersion());
                            functionDTO.setInterfaceUrl(fv==null?"":fv.getInterfaceUrl());
                            functionDTO.setZipSize(fv==null?"":fv.getZipSize());
                            functionDTO.setFunckey(functionInfo.getMarkName());
                            functionDTO.setStatus(functionInfo.getActive()==null?"":functionInfo.getActive().name());
                            if(functionInfo.getActive()!=null&&functionInfo.getType().equals(KnResource.ActiveType.PUBLICPACKAGE)){
                                functionDTO.setfSortId(Long.MAX_VALUE);
                                publicJa.add(functionDTO);
                            }else{
                                functionDTO.setfSortId(funcInModuleMap.get(functionInfo.getId())==null?0:funcInModuleMap.get(functionInfo.getId()));
                                functionJa.add(functionDTO);
                            }
                        }
                        if(fv!=null){
                            //去除公共包的排序
                            if(functionInfo.getActive()!=null&&!functionInfo.getActive().equals(KnResource.ActiveType.PUBLICPACKAGE)&&!fv.getWorkStatus().equals(Setting.WorkStatusType.unusable)){
                                simpleFunctionDTO.setfSortId(funcInModuleMap.get(functionInfo.getId())==null?0:funcInModuleMap.get(functionInfo.getId()));
                                simpleFunctionDTO.setFunctionId(functionInfo.getId());
                                sortFarr.add(simpleFunctionDTO);
                            }
                        }
                        if(tempMap!=null){
                            tempMap.remove(functionInfo.getId().toString());
                        }
                        functionRoleMap.remove(functionInfo.getId().toString());
                    }
                }
                //功能排序加到模块排序数组中
                if(sortFarr.size()!=0){
                    simpleModuleDTO.setSimpleFunctionDTOList(sortFarr);
                    sortModuleJa.add(simpleModuleDTO);
                }
                if(tempMap!=null){
                    for(Long key1 : tempMap.keySet()){ //库中不存在的functionId
                        FunctionDTO functionDTO=new FunctionDTO();
                        functionDTO.setFunctionId(key1);
                        functionDTO.setK2(Setting.DELETE);
                        functionJa.add(functionDTO);
                    }
                }
                //比较模块信息
                if(!moduleMap.containsKey(moduleInfo.getId().toString())){ //client 中不存在 库中的module
                    //库中存在 ，传过来的不存在
                    moduleDTO.setK2(Setting.ADD);
                }else{ //client 中存在库中的module
                    moduleDTO.setK2(Setting.UPDATE);
                }
                functionJa.addAll(publicJa);
                if(functionJa!=null&&functionJa.size()!=0){
                    //功能信息加入模块中去
                    moduleDTO.setFunctionDTOList(functionJa);
                    moduleList.add(moduleDTO);
                }
                moduleMap.remove(moduleInfo.getId().toString());
            }
            for(Long key2 : moduleMap.keySet()){ //库中不存在的moduleId
                ModuleDTO moduleDTO=new ModuleDTO();
                moduleDTO.setModuleId(key2);
                moduleDTO.setFunctionDTOList(new ArrayList<FunctionDTO>());
                moduleDTO.setmVersion(moduleMap.get(key2));
                moduleDTO.setK2(Setting.DELETE);
                moduleList.add(moduleDTO);
            }
            //返回角色下的模块信息
            rtnDTO.setModuleDTOList(moduleList);
            //将排序加入
            rtnDTO.setSimpleModuleDTOList(sortModuleJa);
            //将功能标识单独拿出来存放
            funKey=Utils.removeDuplicate(funKey);
            rtnDTO.setStatus(true);
            rtnDTO.setErrorCode(Setting.SUCCESSSTAT);
            rtnDTO.setErrorMessage("获取成功");
        }catch(Exception ex){
            logger.error(ex.getMessage());
            ex.printStackTrace();
            rtnDTO.setStatus(true);
            rtnDTO.setErrorCode(Setting.FAIURESTAT);
            rtnDTO.setErrorMessage("获取失败");
        }finally{
            return rtnDTO;
        }
    }

    /**
     * 在设备注册的时候进行离线消息的查询并发送
     * @param pushMessname
     */
    @Transactional(readOnly=false)
    private void sendOfflineMess(final String pushMessname){
        List<KnPushMessageInfo> list = messDao.findAll(new Specification<KnPushMessageInfo>(){
            @Override
            public Predicate toPredicate(Root<KnPushMessageInfo> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                Root<KnDeviceInfo> kr=cq.from(KnDeviceInfo.class);
                expressions.add(cb.equal(root.<KnDeviceInfo>get("deviceInfo"),kr.<Long>get("id")));
                expressions.add(cb.equal(cb.upper(kr.<String>get("pushMessname")) , pushMessname.toUpperCase()));
                expressions.add(cb.equal(root.<Setting.MsgState>get("msgState"), Setting.MsgState.nosend ));
                return predicate;
            }
        }) ;
        if(!Utils.isEmpityCollection(list)){
            //发送离线消息
            for(KnPushMessageInfo knPushMessageInfo : list){
                List<KnDeviceInfo> androidList = new ArrayList<>();
                androidList.add(knPushMessageInfo.getDeviceInfo());
                IosPushUtil.getInstall().sendAndroidMess(androidList,knPushMessageInfo.getTitle(),knPushMessageInfo.getContent(),knPushMessageInfo.getUri());
                knPushMessageInfo.setMsgState(Setting.MsgState.send);
                messDao.save(knPushMessageInfo);
            }
        }
    }

    /**
     * @param mrd 手机端传入参数
     *                参数含义如下： {"xtype":"logOut","totken":"设备标示","plateform":"设备来自平台"}
     *                例如：        {"xtype":"logOut","totken":"sdfadsfadsasdasd1123123","plateform":"IPHONE"}
     *
     * @Description: (注销接口,便于推送下线以及其他消息)
     */
    @Transactional(readOnly=false)  //  关闭只读 ,对数据库有操作
    public MobileUserOrgDTO UpdateKnChannelStat(MobileRegisterDTO mrd){
        MobileUserOrgDTO dto=new MobileUserOrgDTO();
        try{
            Map userMap=getUserInfo();
            if(Setting.FAIURESTAT.equals(userMap.get(Setting.RESULTCODE))){
                dto.setStatus(false);
                dto.setErrorCode(Setting.FAIURESTAT);
                dto.setErrorMessage(userMap.get(Setting.MESSAGE).toString());
                return dto;
            }
            String user_id=(String)userMap.get("userId");//用户id
            String user_sys=(String)userMap.get("userSystem");  //用户来自系统
            String totken=mrd.getToken();  //设备totken
            String plate_form=mrd.getPlateform();   //设备来自的平台 (IPHONE  android)
            if(Utils.isEmptyString(plate_form)){
                plate_form=Setting.VersionType.IPHONE.name();
            }else{//下面放置格式化失败
                String finType="";
                for(Setting.VersionType type : Setting.VersionType.values()){
                    if(type.equals(plate_form)){
                        finType=plate_form;
                    }
                }
                if(Utils.isEmptyString(finType)){
                    plate_form=Setting.VersionType.IPHONE.name();
                }
            }
            knDeviceInfoDao.updateKnChannelStat(Setting.OnlineType.off.name(),totken,Setting.VersionType.valueOf(plate_form.toUpperCase()),user_id,user_sys);
            dto.setStatus(true);
            dto.setErrorCode(Setting.SUCCESSSTAT);
            dto.setErrorMessage("注销成功");
        }catch(Exception e){
            logger.info("注销接口,错误信息 -----> "+e);
            dto.setStatus(false);
            dto.setErrorCode(Setting.FAIURESTAT);
            dto.setErrorMessage("后台异常,稍后尝试");
        }
        return dto;
    }

    private Map<String,Object> getModuleMap(List<ModuleDTO> moduleDTOs){
        Map<String,Object> map=new HashMap<String,Object>();
        Map<Long,Map<Long,String>> functionMap=new HashMap<Long,Map<Long,String>>();
        Map<Long,String> zipMap=new HashMap<Long,String>();
        Map<Long,String> moduleMap=new HashMap<Long,String>();
        if(!moduleDTOs.isEmpty()){//用户传输的模块的集合数据
            for(ModuleDTO moduleDTO:moduleDTOs){
                moduleMap.put(moduleDTO.getModuleId(),moduleDTO.getmVersion());
                if(!moduleDTO.getFunctionDTOList().isEmpty()){
                    Map<Long,String> function=new HashMap<Long,String>();
                    for(FunctionDTO functionDTO:moduleDTO.getFunctionDTOList()){
                        if(!Strings.isNullOrEmpty(functionDTO.getfVersion())){
                            function.put(functionDTO.getFunctionId(),functionDTO.getfVersion());
                        }
                        if(!Strings.isNullOrEmpty(functionDTO.getZipVersion())){
                            zipMap.put(functionDTO.getFunctionId(),functionDTO.getZipVersion());
                        }
                    }
                    functionMap.put(moduleDTO.getModuleId(),function);
                }
            }
        }
        map.put("module",moduleMap);
        map.put("function",functionMap);
        map.put("zip",zipMap);
        return map;
    }

    public MobileUserOrgDTO CheckVersion(MobileRegisterDTO mrd){
        MobileUserOrgDTO muoDTO=new MobileUserOrgDTO();
        try{
            String app_key=mrd.getAppKey(); //应用的标示
            String versionType=mrd.getVersionType();//版本的状态
            String plate_form=mrd.getPlateform();  //设备来自的平台 (IPHONE  android)
            String version=mrd.getVersion();   //版本号 (1.02)
            muoDTO=checkNewVerListDTO(app_key,plate_form,versionType,version);
        }catch(Exception e){
            logger.info("检测新版本信息,错误信息 -----> "+e);
            muoDTO.setStatus(false);
            muoDTO.setErrorCode(Setting.FAIURESTAT);
            muoDTO.setErrorMessage("后台异常,稍后尝试");
        }
        return muoDTO;
    }

    /**
     * 检测设备是否在线,以及擦除
     *
     * @param mrd 手机端传入参数
     *                参数含义如下： {"xtype":"checkDeviceOnLine","appkey":"应用标示","totken":"设备标示"}
     *                例如：        {"xtype":"checkDeviceOnLine","appkey":"111111111123213","totken":"dsfasdfsda"}
     *
     * @return
     */
    public  MobileUserOrgDTO  CheckDeviceOnLine(MobileRegisterDTO mrd){
        MobileUserOrgDTO muoDTO=new MobileUserOrgDTO();
        try{
            Map userMap=getUserInfo();
            if(Setting.FAIURESTAT.equals(userMap.get(Setting.RESULTCODE))){
                muoDTO.setStatus(false);
                muoDTO.setErrorCode(Setting.FAIURESTAT);
                muoDTO.setErrorMessage(userMap.get(Setting.MESSAGE).toString());
                return muoDTO;
            }
            String user_id=(String)userMap.get("userId");//用户id
            String user_sys=(String)userMap.get("userSystem");  //用户来自系统
            String app_key=mrd.getAppKey(); //应用的标示
            String totken=mrd.getToken();  //设备totken
            List<KnDeviceInfo> devList=knDeviceInfoDao.findListByTotken(totken);
            Map devMap=checkDeviceIsOrDelete(devList,user_id,user_sys);
            if(Setting.FAIURESTAT.equals(devMap.get(Setting.RESULTCODE))||Setting.DeleteStatusType.device.name().equals(devMap.get("DELEOFTYPE"))||Setting.DeleteStatusType.account.name().equals(devMap.get("DELEOFTYPE"))){
                muoDTO.setStatus(false);
                muoDTO.setErrorCode(Setting.FAIURESTAT);
                muoDTO.setErrorMessage(devMap.get(Setting.MESSAGE).toString());
                return muoDTO;
            }
            List<KnDeviceInfo> onOrOffList=knDeviceInfoDao.findByTotkenAndUserIdAndForsys(totken,user_id,user_sys,Setting.OnlineType.online.name());
            if(Utils.isEmpityCollection(onOrOffList)){
                muoDTO.setDeviceType("OFFLINE");
                String dName="";
                Long time=null;
                List<KnDeviceInfo> offList=knDeviceInfoDao.findByTotkenAndUserIdAndForsys(totken,user_id,user_sys,Setting.OnlineType.off.name());
                if(!Utils.isEmpityCollection(offList)){
                    KnDeviceInfo firObj=offList.get(0);
                    Long updateTime=Utils.isEmptyString(firObj.getUpdateTime())?firObj.getCreateTime():firObj.getUpdateTime();
                    List<KnDeviceInfo> otherList=knDeviceInfoDao.findListByAppkeyAndTime(user_id,user_sys,app_key,totken,updateTime);
                    if(!Utils.isEmpityCollection(otherList)){
                        KnDeviceInfo senObj=otherList.get(0);
                        time=senObj.getUpdateTime();
                        dName=senObj.getDeviceName();
                    }
                }
                muoDTO.setLastTime(time);
                muoDTO.setDeviceName(dName);
            }else{
                muoDTO.setDeviceType("ONLINE");
            }
            muoDTO.setStatus(true);
        }catch(Exception e){
            logger.info("检测设备是否在线,以及擦除 -----> "+e);
            muoDTO.setStatus(false);
            muoDTO.setErrorCode(Setting.FAIURESTAT);
            muoDTO.setErrorMessage("后台异常,稍后尝试");
        }
        return muoDTO;
    }

    /**
     * 检测新版本信息
     *
     * @param app_key     应用标示
     * @param plate_form  设备来自平台
     * @param versionType 版本状态
     * @param version     版本号
     *
     * @return 返回是否有新版本的信息
     */
    private MobileUserOrgDTO checkNewVerListDTO(String app_key,String plate_form,String versionType,String version){
        MobileUserOrgDTO muoDTO=new MobileUserOrgDTO();
        String title="当前为最新版本", newUrl="", vf="0";
        try{
            List<Setting.WorkStatusType> workStatList=new ArrayList<>();
            if(Utils.isEmptyString(versionType)){
                workStatList.add(Setting.WorkStatusType.usable);
                workStatList.add(Setting.WorkStatusType.prototype);
                workStatList.add(Setting.WorkStatusType.introduce);
                workStatList.add(Setting.WorkStatusType.test);
                workStatList.add(Setting.WorkStatusType.unusable);
            }else{
                for(Setting.WorkStatusType verType : Setting.WorkStatusType.values()){
                    if(verType.name().equals(versionType)){
                        workStatList.add(Setting.WorkStatusType.valueOf(versionType));
                    }
                }
                if(Utils.isEmpityCollection(workStatList)){
                    workStatList.add(Setting.WorkStatusType.prototype);
                }
            }
            List<KnVersionInfo> knVersionInfoList=knVersionInfoDao.findVerListByAppkeyAndWorkStatsAndPlat(app_key,Setting.VersionType.valueOf(plate_form.toUpperCase()),workStatList);
            List<KnVersionInfo> versionList=VersionNumUtil.getVersionByNum(knVersionInfoList,version,true);
            if(!Utils.isEmpityCollection(versionList)){//提示有版本更新
                title="有新版本,请更新";
                if(Setting.VersionType.IPHONE.name().equals(plate_form)||Setting.VersionType.IPAD.name().equals(plate_form)){
                    newUrl=versionList.get(0).getIosHttpsAddress()==null?"":versionList.get(0).getIosHttpsAddress();
                }else{
                    newUrl=versionList.get(0).getAddress()==null?"":versionList.get(0).getAddress();
                }
            }
            muoDTO.setVf(vf);
            muoDTO.setNewUrl(newUrl);
            muoDTO.setStatus(true);
            muoDTO.setErrorMessage(title);
            muoDTO.setErrorCode(Setting.SUCCESSSTAT);
        }catch(Exception e){
            logger.info("检测新版本信息,错误信息 -----> "+e);
            muoDTO.setStatus(true);
            muoDTO.setErrorMessage("后台异常,稍后尝试");
            muoDTO.setErrorCode(Setting.FAIURESTAT);
        }
        return muoDTO;
    }

    /**
     * 新增设备信息
     *
     * @param mrd 前台传入参数信息
     *                参数含义如下： {"xtype":"mdmRegisterInfo","userId":"用户id","userSystem":"用户来自系统","appkey":"应用标示","totken":"设备标示","plateform":"设备来自平台","version":"版本号","userPhoneName":"设备型号","versionType":"版本的状态","zipVersion":"公共包的版本号","userPhone":"手机号码","regTime":"最后登录时间","fromSys":"设备来自产品"}
     *                例如：        {"xtype":"mdmRegisterInfo","userId":"111","userSystem":"ERP","appkey":"111111111123213","totken":"sdfadsfadsasdasd1123123","plateform":"IPHONE","version":"1.35","userPhoneName":"小米3","versionType":"usable","zipVersion":"1.3","userPhone":"31649714646","regTime":"2014-07-01 18:58:12","fromSys":"eam"}
     *
     * @return 返回成功状态以及设备的主键id
     */
    private Map<String,Object> createChannelInfo(MobileRegisterDTO mrd){
        boolean bool=false;
        Map<String,Object> backMap=new HashMap<String,Object>();
        try{
            KnDeviceInfo ci=new KnDeviceInfo();
            String app_key=mrd.getAppKey(); //应用的标示
            if(!Utils.isEmptyString(app_key)){
                List<KnApplicationInfo> knApplicationInfoList=knApplicationInfoDao.findApplicationByAppkey(app_key);
                if(!Utils.isEmpityCollection(knApplicationInfoList)){
                    String totken=mrd.getToken(),
                            fromSys=mrd.getFromSys(),
                            loginName=mrd.getLoginName(),
                            plateform=mrd.getPlateform();
                    KnApplicationInfo knApplicationInfo=knApplicationInfoList.get(0);
                    ci.setAppId( knApplicationInfo.getId() );
                    ci.setApiKey( knApplicationInfo.getApiKey() );
                    ci.setAppTitle( knApplicationInfo.getTitle() );
                    ci.setChversion(mrd.getVersion());
                    ci.setDeviceToken(totken);
                    if(Utils.isEmptyString(plateform)){
                        plateform=Setting.VersionType.IPHONE.name();
                    }else{//下面放置格式化失败
                        String finType="";
                        for(Setting.VersionType type : Setting.VersionType.values()){
                            if(type.toString().equalsIgnoreCase(plateform)){
                                finType=plateform;
                            }
                        }
                        if(Utils.isEmptyString(finType)){
                            plateform=Setting.VersionType.IPHONE.name();
                        }
                    }
                    ci.setDeviceType(Setting.VersionType.valueOf(plateform.toUpperCase()));
                    ci.setFormSystem(fromSys);
                    ci.setDeviceToken(totken);
                    ci.setFormSystem(fromSys);
                    ci.setDelState(Setting.DeleteStatusType.nodelete);//设置擦除的状态
                    ci.setUserPhone(mrd.getUserPhone());
                    ci.setOnlineStat(Setting.OnlineType.online.name());
                    String versionType=mrd.getVersionType();//版本的状态
                    if(!Utils.isEmptyString(versionType)){
                        for(Setting.WorkStatusType obj : Setting.WorkStatusType.values()){
                            if(obj.name().equals(versionType)){
                                ci.setWorkStatus(Setting.WorkStatusType.valueOf(versionType));
                                break;
                            }
                        }
                    }else{
                        ci.setWorkStatus(Setting.WorkStatusType.usable);
                    }
                    String pushMessname=totken.toUpperCase()+Setting.LINE_SEPEAC+loginName.toUpperCase()+Setting.LINE_SEPEAC+knApplicationInfo.getApiKey()+Setting.LINE_SEPEAC+fromSys.toUpperCase();
                    ci.setPushMessname(pushMessname);
                    ci.setLoginName(loginName);
                    ci.setDeviceName(mrd.getUserPhoneName());
                    ci.setUserSystem(mrd.getUserSystem());
                    ci.setUserId(mrd.getUserId());
                    knDeviceInfoDao.save(ci);
                    bool=true;
                    backMap.put("chanId",ci.getId());
                    backMap.put("KnDeviceInfo",ci);
                }
            }
        }catch(Exception e){
            logger.info("注册设备信息,错误信息 -----> "+e);
        }
        backMap.put("bool",bool);
        return backMap;
    }

    /**
     * 获取员工的来自系统以及用户ID
     *
     * @return map 形式返回
     */
    public Map getUserInfo(){
        Map devMap=new HashMap();
        try{
            ShiroUser userInfo=Users.ShiroUser();
            if(null==userInfo){
                devMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                devMap.put(Setting.MESSAGE,"用户信息不存在");
                return devMap;
            }
            if(Utils.isEmptyString(userInfo.getId())){
                devMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                devMap.put(Setting.MESSAGE,"用户信息不存在");
                return devMap;
            }
            KnEmployee knEmployee=knEmployeeDao.findOne(userInfo.getId());
            if(null==knEmployee){
                devMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                devMap.put(Setting.MESSAGE,"员工不存在");
                return devMap;
            }
            String user_id=knEmployee.getUserId();//用户id
            String user_sys=knEmployee.getUserSystem();  //用户来自系统
            if(Utils.isEmptyString(user_id)){
                devMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                devMap.put(Setting.MESSAGE,"员工不存在");
                return devMap;
            }
            if(Utils.isEmptyString(user_sys)){
                devMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                devMap.put(Setting.MESSAGE,"员工不存在");
                return devMap;
            }
            devMap.put("userId",user_id);
            devMap.put("userSystem",user_sys);
            devMap.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
        }catch(Exception e){
            logger.info("获取用户的来自系统以及用户ID -----> "+e);
            devMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            devMap.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return devMap;
    }

    /**
     * 检测设备是否擦除
     *
     * @param devList  该设备下的所有账号信息
     * @param user_id  用户id
     * @param user_sys 用户来自系统
     *
     * @return
     */
    private Map checkDeviceIsOrDelete(List<KnDeviceInfo> devList,String user_id,String user_sys){
        Map map=new HashMap();
        try{
            if(!Utils.isEmpityCollection(devList)){
                String delState="";
                boolean tFlag=false, aFlag=false;
                for(KnDeviceInfo info : devList){
                    delState=Utils.isEmptyString(info.getDelState())?"":info.getDelState().getTypeName();
                    if(Setting.DeleteStatusType.device.name().toString().equalsIgnoreCase(delState)){ //设备被擦除过
                        tFlag=true;
                    }else if(Setting.DeleteStatusType.account.name().toString().equalsIgnoreCase(delState)){//账号被擦除过
                        if(!Utils.isEmptyString(user_id)&&!Utils.isEmptyString(user_sys)&&user_id.equals(info.getUserId())&&user_sys.equals(info.getUserSystem())){
                            aFlag=true;
                        }
                    }
                }
                if(tFlag){//设备擦出
                    map.put(Setting.MESSAGE,"该设备被擦除,无法登录");
                    map.put("DELEOFTYPE",Setting.DeleteStatusType.device.name());
                }else if(aFlag){//帐号擦出
                    map.put(Setting.MESSAGE,"该账号被擦除,无法登录");
                    map.put("DELEOFTYPE",Setting.DeleteStatusType.account.name());
                }else{
                    map.put("DELEOFTYPE",Setting.DeleteStatusType.nodelete.name());
                }
            }else{
                map.put("DELEOFTYPE",Setting.DeleteStatusType.nodelete.name());
            }
            map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
        }catch(Exception e){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return map;
    }

    /**
     * 检测新版本信息
     *
     * @param app_key     应用标示
     * @param plate_form  设备来自平台
     * @param versionType 版本状态
     * @param version     版本号
     *
     * @return 返回是否有新版本的信息
     */
    private MobileUserOrgDTO checkNewVerList(String app_key,String plate_form,String versionType,String version){
        MobileUserOrgDTO dto=new MobileUserOrgDTO();
        String title="当前为最新版本", newUrl="", vf="0";
        try{
            List<Setting.WorkStatusType> workStatList=new ArrayList<>();
            if(Utils.isEmptyString(versionType)){
                workStatList.add(Setting.WorkStatusType.usable);
                workStatList.add(Setting.WorkStatusType.prototype);
                workStatList.add(Setting.WorkStatusType.introduce);
                workStatList.add(Setting.WorkStatusType.test);
                workStatList.add(Setting.WorkStatusType.unusable);
            }else{
                for(Setting.WorkStatusType verType : Setting.WorkStatusType.values()){
                    if(verType.name().equals(versionType)){
                        workStatList.add(Setting.WorkStatusType.valueOf(versionType));
                    }
                }
                if(Utils.isEmpityCollection(workStatList)){
                    workStatList.add(Setting.WorkStatusType.prototype);
                }
            }
            List<KnVersionInfo> knVersionInfoList=knVersionInfoDao.findVerListByAppkeyAndWorkStatsAndPlat(app_key,Setting.VersionType.valueOf(plate_form.toUpperCase()),workStatList);
            List<KnVersionInfo> versionList=VersionNumUtil.getVersionByNum(knVersionInfoList,version,true);
            if(!Utils.isEmpityCollection(versionList)){//提示有版本更新
                title="有新版本,请更新";
                if(Setting.VersionType.IPHONE.name().equals(plate_form)||Setting.VersionType.IPAD.name().equals(plate_form)){
                    newUrl=versionList.get(0).getIosHttpsAddress()==null?"":versionList.get(0).getIosHttpsAddress();
                }else{
                    newUrl=versionList.get(0).getAddress()==null?"":versionList.get(0).getAddress();
                }
            }
            dto.setStatus(true);
            dto.setNewUrl(newUrl);
            dto.setVf(vf);
            dto.setErrorCode(Setting.SUCCESSSTAT);
            dto.setErrorMessage(title);
        }catch(Exception e){
            logger.info("检测新版本信息,错误信息 -----> "+e);
            dto.setStatus(false);
            dto.setErrorCode(Setting.FAIURESTAT);
            dto.setErrorMessage("后台异常,稍后尝试");
        }
        return dto;
    }

    /**
     * 设置下线状态
     *
     * @param updateList 需要设置的设备信息
     * @param totken     当前登录设备信息
     * @param chanId     当前登录设备的主键id
     */
    private void setOffList(List<KnDeviceInfo> updateList,String totken,Long chanId){
        try{
            List<KnDeviceInfo> finalList=new ArrayList<KnDeviceInfo>();
            List<KnDeviceInfo> listOfTotken=knDeviceInfoDao.findChanneListByTotkenAndId(totken,chanId);
            finalList.addAll(listOfTotken);
            finalList.addAll(updateList);
            if(!Utils.isEmpityCollection(finalList)){
                List<Long> idsList=new ArrayList<>();
                for(KnDeviceInfo obj : finalList){
                    idsList.add(obj.getId());
                }
                knDeviceInfoDao.updateChannelStat(Setting.OnlineType.off.name(),idsList);
            }
        }catch(Exception e){
            logger.info("注册设备信息,设置下线状态错误信息 -----> "+e);
        }
    }

    /**
     * 推送下线信息
     *  @param updateList 需要推送下线消息的设备集合
     * @param app_key    应用标示
     * @param deviceName  设备名称
     * @param time 下线时间
     */
    private void pushInfo(List<KnDeviceInfo> updateList,String app_key,String deviceName,Long time){
        try{
            logger.info("需要推送下线消息的设备集合 -----> "+updateList.size());
            if(!Utils.isEmpityCollection(updateList)){
                List<KnPushMessageInfo> iosList=new ArrayList<KnPushMessageInfo>();//ios保存发送过的信息
                List<KnPushMessageInfo> androidList=new ArrayList<KnPushMessageInfo>();//android保存发送过的信息
                List<String> totkenList=new ArrayList<>();
                for(KnDeviceInfo obj : updateList){
                    totkenList.add(obj.getDeviceToken());
                    String vtype="";
                    if(null!=obj.getDeviceType()){
                        vtype=Utils.isEmptyString(obj.getDeviceType())?Setting.VersionType.IPHONE.name():obj.getDeviceType().getM_type();
                    }else{
                        vtype=Setting.VersionType.IPHONE.name();
                    }
                    KnPushMessageInfo knPushMessageInfo=new KnPushMessageInfo();
                    knPushMessageInfo.setMessType(Setting.MessageType.systemmes);
                    knPushMessageInfo.setTitle("注册登录,推送下线信息入口");
                    knPushMessageInfo.setDeviceInfo(obj);
                    if(Setting.VersionType.IPHONE.name().equals(vtype)||Setting.VersionType.IPAD.name().equals(vtype)){
                        knPushMessageInfo.setPlateMess(Setting.PlateformType.IOS);
                        iosList.add(knPushMessageInfo);
                    }else{
                        knPushMessageInfo.setPlateMess(Setting.PlateformType.ANDROID);
                        androidList.add(knPushMessageInfo);
                    }
                }
                Map pushMap=new HashMap();
                String xtype=Setting.PHONEOFFLINE, pushBody="此账号在别处登录，你被迫下线！";
                pushMap.put("xtype",xtype);
                pushMap.put("time",time);
                pushMap.put("dName",deviceName);
                IosPushUtil pushUtil=IosPushUtil.getInstall();
                List<KnCertificateInfo> pushList=knCertificateDao.findCerListByAppkey(app_key);
                Map iosMap=pushUtil.pushIosInfo(JsonMapper.nonDefaultMapper().toJson(pushMap),pushBody,pushList,totkenList);//IOS 推送下线消息
                String messageBody = "你的账号已于"+time+"在其它地方登录。登录设备是"+deviceName+",请注意账号安全。如果这不是你的操作，你的密码很可能已经泄漏，建议联系管理员处理！" ;
                Map androidMap=pushUtil.sendAndroidMess(updateList,"账号存在风险",messageBody,Setting.ANDROIDOFFLINE);//ANDROID 推送下线消息
                boolean iosBool=false, androidBool=false;
                if(null!=iosMap&&iosMap.size()>0){
                    if("200".equals(iosMap.get(Setting.RESULTCODE))){
                        iosBool=true;
                    }
                }
                if(null!=androidMap&&androidMap.size()>0){
                    if("200".equals(androidMap.get(Setting.RESULTCODE))){
                        androidBool=true;
                    }
                }
                if(null!=iosList&&iosList.size()>0){//循环将发送消息入库
                    for(KnPushMessageInfo iosObj : iosList){
                        if(iosBool){
                            iosObj.setMsgState(Setting.MsgState.send);
                        }else{
                            iosObj.setMsgState(Setting.MsgState.nosend);
                        }
                        messDao.save(iosObj);
                    }
                }
                if(null!=androidList&&androidList.size()>0){//循环将发送消息入库
                    for(KnPushMessageInfo androidObj : androidList){
                        if(androidBool){
                            androidObj.setMsgState(Setting.MsgState.send);
                        }else{
                            androidObj.setMsgState(Setting.MsgState.nosend);
                        }
                        messDao.save(androidObj);
                    }
                }
            }
        }catch(Exception e){
            logger.info("注册设备信息,推送错误信息 -----> "+e);
        }
    }

    /**
     * 根据应用的appkey获取应用底下的应用的角色id列表
     * @param appkey 应用的apiKey
     * @return 角色的id集合
     */
    private List<Long> findRoleIdByAppkey(String appkey){
        List<Long> roleIds = new ArrayList<Long>();
        if(Strings.isNullOrEmpty(appkey)){
            return roleIds;
        }
        List<KnApplicationInfo> appList =knApplicationInfoDao.findApplicationByAppkey(appkey);
        if(Utils.isEmpityCollection(appList)){
            return roleIds;
        }
        Long appId = appList.get(0).getId();
        List<KnRoleApplicationInfo> roleApplicationInfos = roleApplicationInfoDao.findRoleByAppId(appId);
        if(roleApplicationInfos!=null){
            for(KnRoleApplicationInfo kra : roleApplicationInfos){
                roleIds.add(kra.getRoleId());
            }
        }
        return roleIds;
    }

    /**
     * @param fi 功能对象
     *
     * @return
     *
     * @description (根据功能信息获取功能下的可用状态的最高版本的zip包信息)
     */
    private KnFunctionVersionInfo getVersionByFunction(KnResource fi){
        KnFunctionVersionInfo fv=null;
        //获取功能的版本信息
        List<KnFunctionVersionInfo> set=knFunctionVersionDao.findByFunctionId(fi.getId());
        if(set!=null&&set.size()!=0){
            List<KnFunctionVersionInfo> list=new ArrayList<KnFunctionVersionInfo>();
            for(KnFunctionVersionInfo functionVersionInfo : set){
                //去除不可用的状态信息
                if(Setting.WorkStatusType.unusable.equals(functionVersionInfo.getWorkStatus())){
                    continue;
                }
                list.add(functionVersionInfo);
            }
            if(list.size()!=0){
                //根据版本号进行排序,降序输出,取出的第一个数据为最高版本数据
                Collections.sort(list,new Comparator<KnFunctionVersionInfo>(){
                    public int compare(KnFunctionVersionInfo o1,KnFunctionVersionInfo o2){
                        return VersionNumUtil.versionCompareTo(o1.getZipVersion(),o2.getZipVersion());
                    }
                });
                fv=list.get(0);
            }
        }
        return fv;
    }
}
