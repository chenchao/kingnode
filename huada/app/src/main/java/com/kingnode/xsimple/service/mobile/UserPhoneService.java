package com.kingnode.xsimple.service.mobile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import com.google.common.collect.Lists;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.dao.application.KnApplicationInfoDao;
import com.kingnode.xsimple.dao.application.KnApplicationSetUpInfoDao;
import com.kingnode.xsimple.dao.application.KnRoleApplicationInfoDao;
import com.kingnode.xsimple.dao.application.KnRoleModuleFunctionInfoDao;
import com.kingnode.xsimple.dao.push.KnDeviceInfoDao;
import com.kingnode.xsimple.dao.system.KnEmployeeDao;
import com.kingnode.xsimple.dao.system.KnFunctionVersionDao;
import com.kingnode.xsimple.dao.system.KnResourceDao;
import com.kingnode.xsimple.dao.system.KnUserDao;
import com.kingnode.xsimple.entity.application.KnApplicationInfo;
import com.kingnode.xsimple.entity.application.KnApplicationSetupInfo;
import com.kingnode.xsimple.entity.push.KnDeviceInfo;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnEmployeeOrganization;
import com.kingnode.xsimple.entity.system.KnResource;
import com.kingnode.xsimple.entity.system.KnRole;
import com.kingnode.xsimple.entity.system.KnUser;
import com.kingnode.xsimple.entity.web.KnFunctionVersionInfo;
import com.kingnode.xsimple.entity.web.KnRoleApplicationInfo;
import com.kingnode.xsimple.entity.web.KnRoleModuleFunctionInfo;
import com.kingnode.xsimple.util.PathUtil;
import com.kingnode.xsimple.util.Users;
import com.kingnode.xsimple.util.Utils;
import com.kingnode.xsimple.util.client.RestServiceUtil;
import com.kingnode.xsimple.util.version.VersionNumUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Component @Transactional(readOnly=true) public class UserPhoneService{
    private static final String serviceName="PSHR_INT_PKG.getoprinfover";
    private static final String APPUSER="app_user";//从前端获取的用户名
    private static final String APPPWD="app_pwd";//从前端获取的密码
    private static final String APPKEY="appkey";//从前端获取的apiKey
    private static final String APPID="appid";//从前端获取的appid
    private static final String USERID="app_userid";
    private static Logger log=LoggerFactory.getLogger(UserPhoneService.class);
    private KnEmployeeDao knEmployeeDao;
    private KnUserDao knUserDao;
    private KnApplicationInfoDao knApplicationInfoDao;
    private KnApplicationSetUpInfoDao knApplicationSetUpInfoDao;
    private KnFunctionVersionDao knFunctionVersionDao;
    private KnRoleModuleFunctionInfoDao knRoleModuleFunctionInfoDao;
    private KnResourceDao knResourceDao;
    private KnDeviceInfoDao knDeviceInfoDao;
    private KnRoleApplicationInfoDao roleApplicationInfoDao;//应用角色中间表
    @Value("#{commonInfo['psSystemName']}") private String psFromSys;//PS来自系统
    @Value("#{commonInfo['ebsSystemName']}") private String ebsFromSys;//EBS来自系统
    @Value("#{commonInfo['userPsCheck']}") private String userPsCheck;//用户密码信息走哪里,true走PS认证
    @Value("#{commonInfo['userEbsCheck']}") private String userEbsCheck;//用户密码信息走哪里,true走EBS认证
    @Value("#{commonInfo['userSingletonCheck']}") private String userSingletonCheck;//单点登录    ture --> 进行单点登录
    @Value("#{commonInfo['userDeleteonCheck']}") private String userDeleteonCheck;//检测是否擦除状态
    @Value("#{commonInfo['restPsUserUrl']}") private String restPsUserUrl;//rest访问地址
    @Value("#{commonInfo['kimHost']}") private String kimDomain;//KIM的Domain地址
    @Value("#{commonInfo['kimDomain']}") private String kimHost;//KIM的host地址
    @Value("#{commonInfo['kndCloudUrl']}") private String kndCloudUrl;
    @Autowired public void setRoleApplicationInfoDao(KnRoleApplicationInfoDao roleApplicationInfoDao){
        this.roleApplicationInfoDao=roleApplicationInfoDao;
    }
    @Autowired public void setKnDeviceInfoDao(KnDeviceInfoDao knDeviceInfoDao){
        this.knDeviceInfoDao=knDeviceInfoDao;
    }
    @Autowired public void setKnEmployeeDao(KnEmployeeDao knEmployeeDao){
        this.knEmployeeDao=knEmployeeDao;
    }
    @Autowired public void setKnUserDao(KnUserDao knUserDao){
        this.knUserDao=knUserDao;
    }
    @Autowired public void setKnApplicationSetUpInfoDao(KnApplicationSetUpInfoDao knApplicationSetUpInfoDao){
        this.knApplicationSetUpInfoDao=knApplicationSetUpInfoDao;
    }
    @Autowired public void setKnApplicationInfoDao(KnApplicationInfoDao knApplicationInfoDao){
        this.knApplicationInfoDao=knApplicationInfoDao;
    }
    @Autowired public void setKnResourceDao(KnResourceDao knResourceDao){
        this.knResourceDao=knResourceDao;
    }
    @Autowired public void setKnRoleModuleFunctionInfoDao(KnRoleModuleFunctionInfoDao knRoleModuleFunctionInfoDao){
        this.knRoleModuleFunctionInfoDao=knRoleModuleFunctionInfoDao;
    }
    @Autowired public void setKnFunctionVersionDao(KnFunctionVersionDao knFunctionVersionDao){
        this.knFunctionVersionDao=knFunctionVersionDao;
    }
    /**
     * 将base64编码的字符串转换成图片保存到相应的路径
     *
     * @param imageInf 图片 字符串
     * @param path     保存的路径
     *
     * @return 是否保存成功状态
     *
     * @throws IOException IO读写错误
     */
    public static boolean saveFileToDisk(String imageInf,String path){//对字节数组字符串进行Base64解码并生成图片
        try{
            if(imageInf==null){
                //图像数据为空
                return false;
            }
            BASE64Decoder decoder=new BASE64Decoder();
            //Base64解码
            byte[] b=decoder.decodeBuffer(imageInf);
            for(int i=0;i<b.length;++i){
                if(b[i]<0){//调整异常数据
                    b[i]+=256;
                }
            }
            //生成jpeg图片
            OutputStream out=new FileOutputStream(path);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }catch(IOException e){
            return false;
        }
    }
    /**
     * 用户登录,用户输入用户的用户名和密码进行登录,忽略大小写进行查询,用户查询完毕后，
     * 1.如果都查询不到，返回用户不存在
     * 2.如果查询到的用户只有一个，且相应的用户系统设置一致,直接登录，查询该用户的角色，模块，功能信息
     * 3.如果查询到有多个用户，返回到前端让用户进行选择后加入用户的系统和用户的id进行查询登录。
     *
     * @param jsonparm
     *
     * @return
     */
    @Transactional(readOnly=false) public String login(String jsonparm){
        String backInfo="";
        Map returnMap=new HashMap();
        Map<String,String> jsonMap=JsonMapper.nonEmptyMapper().fromJson(jsonparm,Map.class);
        try{
            if(Strings.isNullOrEmpty(jsonparm)){
                Map<String,String> map=new HashMap<String,String>();
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"参数为空");
                backInfo=JsonMapper.nonEmptyMapper().toJson(map);
            }else{
                KnUser loginUser=null;
                KnEmployee employee=null;
                //将获取到的用户的信息进行解密
                String uaccount=jsonMap.containsKey(APPUSER)?jsonMap.get(APPUSER):"";
                String upwd=jsonMap.containsKey(APPPWD)?jsonMap.get(APPPWD):"";
                //二维码登录	start
                if(jsonMap.containsKey("rdmparam")&&jsonMap.containsKey("uid")){
                    String randrom=jsonMap.get("rdmparam");
                    returnMap.put("randrom",randrom);
                }
                //二维码登录	end
                //如果有微信Id,证明是从微信端过来的用户,根据微信Id查询用户是否已经绑定,如果绑定,直接返回用户信息
                String weixinId=null;
                boolean weixinFlag=false;
                if(jsonMap.containsKey("weixinId")){
                    weixinFlag=true;
                    weixinId=jsonMap.get("weixinId").trim();
                    if(Strings.isNullOrEmpty(uaccount)){//账号为空,首次登陆微信绑定账号
                        List<KnEmployee> employeeList=knEmployeeDao.findByWeixinId(weixinId);
                        if(employeeList.size()!=0){
                            employee=employeeList.get(0);
                            loginUser=knUserDao.findOne(employee.getId());
                        }else{
                            Map<String,String> map=new HashMap<String,String>();
                            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                            map.put(Setting.MESSAGE,"该微信账号暂未绑定用户");
                            backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                            return backInfo;
                        }
                    }
                    //用户的主键id进行的登录信息
                    if(loginUser==null){
                        if(jsonMap.containsKey(USERID)){
                            //如果有传输用户的主键id,直接查询用户进行登录
                            Long userId=Long.parseLong(jsonMap.get(USERID));
                            loginUser=knUserDao.findOne(userId);
                            employee=knEmployeeDao.findOne(userId);
                        }else{
                            Map returnXSimpleUser=getXSimpleUser(uaccount,"");
                            List<KnUser> xSimpleUserList=(List<KnUser>)(returnXSimpleUser.containsKey("userInfoList")?returnXSimpleUser.get("userInfoList"):null);
                            if(xSimpleUserList==null||xSimpleUserList.size()==0){
                                Map<String,String> map=new HashMap<String,String>();
                                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                                map.put(Setting.MESSAGE,"xSimple中此用户账号不存在");
                                backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                                return backInfo;
                            }else{
                                loginUser=xSimpleUserList.get(0);
                                employee=knEmployeeDao.findOne(loginUser.getId());
                                //查询用户所属系统,根据所属系统进行登录验证
                                String fromSys=employee.getUserSystem();
                                if(Strings.isNullOrEmpty(fromSys)){//没有用户来自系统,进行xSimple本地验证
                                    if(!checkXSimplePwd(loginUser,upwd)){
                                        Map<String,String> map=new HashMap<String,String>();
                                        map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                                        map.put(Setting.MESSAGE,"用户名或密码错误");
                                        backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                                        return backInfo;
                                    }
                                }else{//有来自系统,根据来自系统进行验证
                                    if(psFromSys.equalsIgnoreCase(fromSys)&&userPsCheck!=null&&"true".equalsIgnoreCase(userPsCheck.trim())){//从ps验证
                                        String ebsUserId=checkUserByUserNameAndPwd(uaccount,upwd);
                                        if(Strings.isNullOrEmpty(ebsUserId)){
                                            log.info("PS用户验证失败");
                                            Map<String,String> map=new HashMap<String,String>();
                                            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                                            map.put(Setting.MESSAGE,"PS用户验证失败");
                                            backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                                            return backInfo;
                                        }
                                        log.info("PS用户验证成功");
                                    }else if(ebsFromSys.equalsIgnoreCase(fromSys)&&userEbsCheck!=null&&"true".equalsIgnoreCase(userEbsCheck.trim())){//从ebs验证
                                        String ebsUserId=checkUserByUserNameAndPwd(uaccount,upwd);
                                        if(Strings.isNullOrEmpty(ebsUserId)){
                                            log.info("EBS用户验证失败");
                                            //ebs验证失败,没有这个用户信息
                                            Map<String,String> map=new HashMap<String,String>();
                                            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                                            map.put(Setting.MESSAGE,"EBS用户验证失败");
                                            backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                                            return backInfo;
                                        }
                                        log.info("EBS用户验证成功");
                                    }else{
                                        if(!checkXSimplePwd(loginUser,upwd)){
                                            Map<String,String> map=new HashMap<String,String>();
                                            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                                            map.put(Setting.MESSAGE,"用户名或密码错误");
                                            backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                                            return backInfo;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(loginUser==null){
                        Map<String,String> map=new HashMap<String,String>();
                        map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                        map.put(Setting.MESSAGE,"用户名或密码不正确");
                        backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                        return backInfo;
                    }
                    returnMap.put("lgn",loginUser.getLoginName());
                    //判断账号是否被禁用,离职状态指禁用状态
                    if(Setting.NOTWORK.equals(loginUser.getStatus())){
                        Map<String,String> map=new HashMap<String,String>();
                        map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                        map.put(Setting.MESSAGE,"此账号已经被禁用,无法登录.");
                        backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                        return backInfo;
                    }
                    String plateform=jsonMap.containsKey("plateform")?jsonMap.get("plateform"):"";
                    if(!Strings.isNullOrEmpty(weixinId)){//微信账号登录绑定
                        employee.setWeixinId(weixinId);
                    }else if("PC".equalsIgnoreCase(plateform)){//PC端的不验证擦除和挤下线
                        weixinFlag=true;
                    }else{
                        // 检测设备是否擦除
                        if(!Strings.isNullOrEmpty(userDeleteonCheck)&&"true".equalsIgnoreCase(userDeleteonCheck.trim())){
                            boolean bool=checkUserIsDelete(jsonMap,employee);
                            if(!bool){
                                return backInfo;
                            }
                        }
                        //设备和用户会进行绑定,更换同平台的设备不能登录
                        if(!Strings.isNullOrEmpty(userSingletonCheck)&&"true".equalsIgnoreCase(userSingletonCheck.trim())){
                            boolean bool=valiUserTotken(jsonMap,employee);
                            if(!bool){
                                return backInfo;
                            }
                        }
                    }
                    //激活im用户
                    //将用户信息同步到kim,比如新上传的图像信息,用户成功登陆才去激活,有单点登录和设备擦除的状态,以免浪费资源
                    //	new UserInfoTaskThread(new UserPasswordTask(uaccount,upwd,loginUser)).start();
                    returnMap.put("kimDomain",kimDomain);//kim的demo地址
                    returnMap.put("kimHost",kimHost);//kim的host地址
                    Map userJo=new HashMap();
                    List userJa=new ArrayList();
                    userJo.put("id",loginUser.getId());
                    userJo.put("userId",employee.getUserId());
                    userJo.put("uaccount",loginUser.getLoginName());
                    userJo.put("upwd",loginUser.getPassword());
                    userJo.put("fromSys",employee.getUserSystem());
                    userJo.put("imgAddress",employee.getImageAddress());
                    userJo.put("fullName",loginUser.getName());
                    userJo.put("userType",employee.getUserType());
                    userJo.put("email",loginUser.getEmail());
                    userJo.put("phone",employee.getPhone());
                    userJo.put("signature",loginUser.getSalt());
                    userJo.put("telephone",employee.getTelephone());
                    userJa.add(userJo);
                    //如果用户中的markName不为空,则查询其相对应的用户的信息组合进行返回
                    final String markName=employee.getMarkName();
                    final String userSys=employee.getUserSystem();
                    /*if(!Strings.isNullOrEmpty(markName)){//有两个用户信息
                        List<KnUserThirdInfo> userTemp=knUserThirdInfoDao.findAll(new Specification<KnUserThirdInfo>(){
                            @Override public Predicate toPredicate(Root<KnUserThirdInfo> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                                Predicate predicate=cb.conjunction();
                                List<Expression<Boolean>> expressions=predicate.getExpressions();
                                expressions.add(cb.equal(root.<String>get("markName"),markName));
                                expressions.add(cb.notEqual(root.<String>get("fromSys"),userSys));
                                return predicate;
                            }
                        });
                        for(KnUserThirdInfo userThirdInfo : userTemp){
                            Map temp=new HashMap();
                            temp.put("id",userThirdInfo.getId());
                            temp.put("userId",userThirdInfo.getUserId());
                            temp.put("uaccount",userThirdInfo.getUaccount());
                            temp.put("fromSys",userThirdInfo.getFromSys());
                            temp.put("imgAddress","");
                            temp.put("fullName",userThirdInfo.getFullName());
                            temp.put("userType",userThirdInfo.getUserType());
                            temp.put("email","");
                            temp.put("phone","");
                            temp.put("signature","");
                            temp.put("telephone","");
                            userJa.add(temp);
                        }
                    }*/
                    //用户的数组
                    returnMap.put("userInfo",userJa);
                    Long appId=null;
                    if(jsonMap.containsKey(APPID)){
                        appId=Long.parseLong(jsonMap.get(APPID));
                    }
                    log.info("登录传过来的参数存在 appid 且不为空的话则为false=");
                    if(jsonMap.containsKey(APPKEY)){//用户传输了appkey,如果用户不传输则查询所有的,如果用户传输则查找相应的
                        //查询公共包的信息
                        List<KnApplicationSetupInfo> applicationSetupInfoList=knApplicationSetUpInfoDao.findAppSetUpListPageByAppApiKey(jsonMap.get(APPKEY).toString());
                        if(applicationSetupInfoList!=null&&applicationSetupInfoList.size()!=0){
                            List applicaitonSetupJa=new ArrayList();
                            for(KnApplicationSetupInfo applicationSetupInfo : applicationSetupInfoList){
                                Map jo=new HashMap();
                                jo.put("funcIpAddress",applicationSetupInfo.getFuncIpAddress());
                                jo.put("forwardUrl",applicationSetupInfo.getForwardUrl());
                                jo.put("iosPackage",applicationSetupInfo.getIosPackage());
                                jo.put("packageName",applicationSetupInfo.getPackageName());
                                if(appId!=null){
                                    appId=applicationSetupInfo.getApplicationInfo().getId();
                                }
                                applicaitonSetupJa.add(jo);
                            }
                            //公共的信息
                            returnMap.put("commonInfos",applicaitonSetupJa);
                        }
                    }
                    if(appId==null){
                        List<KnApplicationInfo> appList=knApplicationInfoDao.findApplicationByAppkey(jsonMap.get(APPKEY));
                        if(appList.size()!=0){
                            appId=appList.get(0).getId();
                        }
                    }
                    //应用的id与app_key字段对应上(首次登录才会返回，以后登录用户会传appid来就不会返回此 key值了；),
                    returnMap.put(APPID,appId);
                    //查询用户属于哪个角色,查询角色下面的模块和功能信息进行返回
                    final Long userId=employee.getId();
                    //获取用户角色信息
                    List<KnRole> roleInfoList=loginUser.getRole();
                    if(roleInfoList==null||roleInfoList.size()==0){
                        returnMap.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                        returnMap.put(Setting.MESSAGE,"暂无角色");
                        backInfo=JsonMapper.nonEmptyMapper().toJson(returnMap);
                        return backInfo;
                    }
                    List<Long> roleIds=new ArrayList<Long>();
                    roleIds.add(0L);
                    for(KnRole roleInfo : roleInfoList){
                        roleIds.add(roleInfo.getId());
                    }
                    //查询模块在角色中的排序情况
                    List<KnRoleModuleFunctionInfo> roleMouleInfos=knRoleModuleFunctionInfoDao.getRMInfoOnlyByRoles(roleIds);
                    Map<String,KnRoleModuleFunctionInfo> mInRMap=new HashMap<String,KnRoleModuleFunctionInfo>();
                    for(KnRoleModuleFunctionInfo info : roleMouleInfos){
                        String key=info.getRoleId()+"-"+info.getModuleId();
                        if(!mInRMap.containsKey(key)){
                            mInRMap.put(key,info);
                        }
                    }
                    //有多个角色
                    List jsonArray=new ArrayList();
                    //单独把功能的标示拿出来
                    //List funKey = new ArrayList();
                    for(KnRole roleInfo : roleInfoList){
                        Map jo=new HashMap();
                        jo.put("roleId",roleInfo.getId());
                        jo.put("roleName",roleInfo.getName());
                        //查询角色下面的模块,模块下面的功能
                        //角色下的模块
                        List<KnResource> moduleInfoList=knResourceDao.findModuleByRoleId(roleInfo.getId(),KnResource.ResourceType.MODULE);
                        //存放模块信息
                        List moduleList=new ArrayList();
                        //获取传输过来的集合
                        Map<String,Object> map=getModuleMap(jsonMap);
                        Map<String,String> moduleMap=(Map<String,String>)map.get("module");
                        Map<String,Map<String,String>> functionMap=(Map<String,Map<String,String>>)map.get("function");
                        Map<String,String> zipMap=(Map<String,String>)map.get("zip");
                        for(KnResource moduleInfo : moduleInfoList){
                            //模块信息
                            Map mjo=new HashMap();
                            String key=roleInfo.getId()+"-"+moduleInfo.getId();
                            mjo.put("mid",moduleInfo.getId());
                            mjo.put("mversion",moduleInfo.getVersion());
                            mjo.put("mtitle",moduleInfo.getName());
                            mjo.put("enmtitle",moduleInfo.getEnTitle());
                            mjo.put("micon",moduleInfo.getIcon());
                            mjo.put("moduleKey",moduleInfo.getCode());
                            mjo.put("mSortId",mInRMap.get(key).getRmSort());
                            //模块下的功能
                            List<KnResource> functionInfoList=knResourceDao.findResourceByRidAndMid(roleInfo.getId(),moduleInfo.getId());
                            //获取功能在模块下的排序
                            List<KnRoleModuleFunctionInfo> funcInMolduleList=knRoleModuleFunctionInfoDao.findByRoleIdAndMoludeId(roleInfo.getId(),moduleInfo.getId());
                            Map<Long,Long> funcInModuleMap=new HashMap<Long,Long>();
                            for(KnRoleModuleFunctionInfo info : funcInMolduleList){
                                funcInModuleMap.put(info.getFunctionId(),info.getMfSort());
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
                            Map<String,String> tempMap=new HashMap<String,String>();
                            for(KnResource functionInfo : functionInfoList){
                                tempMap=functionMap.get(moduleInfo.getId().toString());
                                //将角色中有的功能加入
                                if(functionRoleMap.containsKey(functionInfo.getId())){
                                    Map fjo=new HashMap();
                                    //单独把功能的标示拿出来
                                    //Map funObj = new HashMap();
                                    fjo.put("fsort",funcInModuleMap.get(functionInfo.getId())==null?0:funcInModuleMap.get(functionInfo.getId()));
                                    boolean flag=true;
                                    KnFunctionVersionInfo fv=getVersionByFunction(functionInfo);
                                    //数据库中的 功能  要跟传过来的 功能个数相同 并且 版本号也要完全一样
                                    if(tempMap==null||!tempMap.containsKey(functionInfo.getId().toString())){//功能不存在
                                        fjo.put(Setting.K2,Setting.ADD);
                                    }else if(!Strings.isNullOrEmpty(functionInfo.getVersion())&&!functionInfo.getVersion().equals(tempMap.get(functionInfo.getId()))){//功能存在且版本不同
                                        fjo.put(Setting.K2,Setting.UPDATE);
                                    }else{
                                        //功能存在,版本一致,(zip版本一致,zip版本不一致);功能存在版本不一致(zip版本一致,zip版本不一致);
                                        //功能存在且zip版本不同
                                        if(fv!=null&&fv.getZipVersion()!=null&&!fv.getZipVersion().equals(zipMap.get(functionInfo.getId().toString()))){
                                            fjo.put(Setting.K2,Setting.UPDATE);
                                        }else{
                                            flag=false;
                                        }
                                    }
                                    //功能的标示
                                    fjo.put("p_TYPE_CODE",functionInfo.getMarkName());
                                    //funObj.put("p_TYPE_CODE", functionInfo.getMarkName());
                                    //funKey.add(funObj);
                                    if(flag&&fv!=null){
                                        fjo.put("fid",functionInfo.getId());
                                        fjo.put("fversion",functionInfo.getVersion());
                                        fjo.put("ftitle",functionInfo.getName());
                                        fjo.put("enftitle",functionInfo.getEnTitle());
                                        fjo.put("ficon",functionInfo.getIcon());
                                        if(weixinFlag){
                                            fjo.put("zip",fv.getUnZipUrl());
                                        }else{
                                            fjo.put("zip",fv.getFuncZipUrl());
                                        }
                                        fjo.put("zipver",fv.getZipVersion());
                                        fjo.put("interfaceUrl",fv==null?"":(Strings.isNullOrEmpty(fv.getInterfaceUrl())?"":fv.getInterfaceUrl()));
                                        fjo.put("zipsize",fv.getZipSize());
                                        fjo.put("funckey",functionInfo.getMarkName());
                                        fjo.put("fversion",functionInfo.getVersion());
                                        fjo.put("fstatus",functionInfo.getActive()==null?"":functionInfo.getActive());
                                        /*if(functionInfo.getActive()!=null&&functionInfo.getType().equals(KnResource.ActiveType.PUBLICPACKAGE)){
                                            fjo.put("fsort", Integer.MAX_VALUE);
                                            publicJa.add(fjo);
                                        }else{
                                            fjo.put("fsort", funcInModuleMap.get(functionInfo.getId())==null?0:funcInModuleMap.get(functionInfo.getId()));
                                            functionJa.add(fjo);
                                        }*/
                                        fjo.put("fsort",funcInModuleMap.get(functionInfo.getId())==null?0:funcInModuleMap.get(functionInfo.getId()));
                                        functionJa.add(fjo);
                                    }
                                    if(tempMap!=null){
                                        tempMap.remove(functionInfo.getId().toString());
                                    }
                                    functionRoleMap.remove(functionInfo.getId().toString());
                                }
                            }
                            if(tempMap!=null){
                                for(String key1 : tempMap.keySet()){ //库中不存在的functionId
                                    Map obj=new HashMap();
                                    obj.put(Setting.K2,Setting.DELETE);
                                    obj.put("fid",key1);//
                                    functionJa.add(obj);
                                }
                            }
                            //比较模块信息
                            if(!moduleMap.containsKey(moduleInfo.getId().toString())){ //client 中不存在 库中的module
                                //库中存在 ，传过来的不存在
                                mjo.put(Setting.K1,Setting.ADD);
                            }else{ //client 中存在库中的module
                                mjo.put(Setting.K1,Setting.UPDATE);
                            }
                            if(functionJa!=null&&functionJa.size()!=0){
                                //功能信息加入模块中去
                                mjo.put("items",functionJa);
                                moduleList.add(mjo);
                            }
                            moduleMap.remove(moduleInfo.getId().toString());
                        }
                        for(String key : moduleMap.keySet()){ //库中不存在的moduleId
                            Map moduleJson=new HashMap();
                            moduleJson.put(Setting.K1,Setting.DELETE);
                            moduleJson.put("mid",key);
                            moduleJson.put("mversion",moduleMap.get(key));
                            moduleJson.put("items",new ArrayList<>());
                            moduleList.add(moduleJson);
                        }
                        //模块信息加入角色中
                        jo.put("mdatas",moduleList);
                        jsonArray.add(jo);
                    }
                    //角色数组
                    returnMap.put("roleInfos",jsonArray);
                    //所有的功能的标识数组
                    //returnMap.put("funKey", funKey);
                    //用于存放功能下面的公用包的信息,存放到所有功能的最后;
                    returnMap.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                }
            }
        }catch(Exception e){
            Map<String,String> map=new HashMap<String,String>();
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"用户登录失败");
            backInfo=JsonMapper.nonEmptyMapper().toJson(map);
            log.info("用户登录异常,传入的jsonparm为"+jsonparm+"\n 错误信息为:"+e);
        }finally{
            log.info("登返回参数如下----->"+backInfo);
            //将云服务的访问地址加入
            returnMap.put("kndCloudUrl",kndCloudUrl);
            //二维码登录	start
            if(jsonMap.containsKey("rdmparam")&&!Strings.isNullOrEmpty(jsonMap.get("rdmparam").toString())){ // Hibernate.createClob(
                log.info("待实现————》"+jsonMap.get("rdmparam"));
            }
            //二维码登录	end
            backInfo=JsonMapper.nonEmptyMapper().toJson(returnMap);
            return backInfo;
        }
    }
    /**
     * 获取xSimple本地用户信息
     *
     * @param uaccount 账号
     * @param upwd     密码(可选)
     *
     * @return 用户对象集合
     */
    private Map getXSimpleUser(final String uaccount,final String upwd){
        //获取KnUser集合信息
        List<KnUser> userInfoList=knUserDao.findAll(new Specification<KnUser>(){
            @Override public Predicate toPredicate(Root<KnUser> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                expressions.add(cb.equal(cb.upper(root.<String>get("loginName")),uaccount.trim().toUpperCase()));
                if(!Strings.isNullOrEmpty(upwd.trim())){
                    expressions.add(cb.equal(cb.upper(root.<String>get("password")),upwd.trim().toUpperCase()));
                }
                return predicate;
            }
        });
        //KnEmployee信息
        List<KnEmployee> employeeList=null;
        Map map=new HashMap();
        if(userInfoList==null||userInfoList.size()==0){//查询不到相应的用户
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"用户名或密码错误");
        }else{
            if(userInfoList.size()==1){//只查询到1条用户数据
                //查询用户的设置是否有设置
                KnUser userInfo=userInfoList.get(0);
                if(!Strings.isNullOrEmpty(upwd.trim())){
                    boolean tempFlag=checkUserSystem(userInfo,uaccount,upwd);
                    if(!tempFlag){
                        return null;
                    }
                }
                map.put("userInfoList",userInfoList);
            }else{//查询到多条用户数据
                List<KnUser> userLoginList=new ArrayList<KnUser>();
                Long[] ids=new Long[userInfoList.size()];
                Map userMap=new HashMap();
                for(int i=0;i<userInfoList.size();i++){
                    KnUser userInfo=userInfoList.get(i);
                    ids[i]=userInfo.getId();
                    userMap.put(userInfo.getId(),userInfo);
                    if(!Strings.isNullOrEmpty(upwd)){
                        boolean tempFlag=checkUserSystem(userInfo,uaccount,upwd);
                        if(tempFlag){//用户存在,用户名和密码都正确
                            userLoginList.add(userInfo);
                        }
                    }else{
                        userLoginList.add(userInfo);
                    }
                }
                if(userInfoList!=null&&userInfoList.size()!=0){
                    employeeList=knEmployeeDao.findByIds(ids);
                }
                if(userLoginList.size()==0){//用户的用户名和密码错误
                    return null;
                }else if(userLoginList.size()==1){//只有一条有效的记录
                    map.put("userInfoList",userInfoList);
                    //				loginUser = userLoginList.get(0);
                }else{//有多个用户,加入返回到前端进行选择
                    Map msgMap=new HashMap();
                    msgMap.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                    msgMap.put(Setting.MESSAGE,"有多条用户信息");
                    List ja=new ArrayList();
                    for(KnEmployee employee : employeeList){
                        KnUser userInfo=(KnUser)userMap.get(employee.getId());
                        Map jo=new HashMap();
                        jo.put("id",userInfo.getId());
                        jo.put("userId",employee.getUserId());
                        jo.put("uaccount",userInfo.getLoginName());
                        jo.put("upwd",userInfo.getPassword());
                        jo.put("fromSys",employee.getUserSystem());
                        jo.put("imgAddress",employee.getImageAddress());
                        jo.put("fullName",userInfo.getName());
                        jo.put("userType",employee.getUserType());
                        jo.put("email",userInfo.getEmail());
                        jo.put("phone",employee.getPhone());
                        jo.put("signature",userInfo.getSalt());
                        jo.put("telephone",employee.getTelephone());
                        ja.add(jo);
                    }
                    msgMap.put("userInfo",ja);
                    map.put("getXSimpleUser",msgMap);
                    map.put("userInfoList",userInfoList);
                }
            }
        }
        return map;
    }
    /**
     * 根据用户对象判断用户的用户名和密码是否正确
     *
     * @param userInfo 用户的对象
     * @param uaccount 用户的账号
     * @param upwd     用户的密码
     *
     * @return true 表示用户登录成功,false表示登录失败
     */
    private boolean checkUserSystem(KnUser userInfo,String uaccount,String upwd){
        KnEmployee employee=knEmployeeDao.findOne(userInfo.getId());
        String fromSys=employee.getUserSystem();
        Map<String,String> map=new HashMap<String,String>();
        try{
            //如果用户没有来自系统,则默认忽略账号大小写设置,因为目前的用户大多数来自ebs,账号都为大写
            if(Strings.isNullOrEmpty(fromSys)){
                //用户和密码都正确,用户登录成功,否则用户登录失败
                boolean flag=checkUser(userInfo,uaccount,upwd,2);
                if(!flag){
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"用户名或密码错误");
                    return false;
                }else{
                    map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                    map.put(Setting.MESSAGE,"登录成功");
                }
            }else{
                /*List<KnUserSetupInfo> tempList=knUserSetupInfoDao.findByFromSys(fromSys.trim());
                //如果用户没有来自系统,则默认忽略账号大小写设置,因为目前的用户大多数来自ebs,账号都为大写
                if(tempList==null||tempList.size()==0){
                    boolean flag=checkUser(userInfo,uaccount,upwd,2);
                    if(!flag){
                        map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                        map.put(Setting.MESSAGE,"用户名或密码错误");
                        return false;
                    }else{
                        map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                        map.put(Setting.MESSAGE,"登录成功");
                    }
                }else{//有系统用户设置,根据系统用户设置中用户的设置进行判断
                    //只要有一个设置中是符合要求的则登录成功
                    for(KnUserSetupInfo userSetupInfo : tempList){
                        Setting.UserSystemType userType=userSetupInfo.getIgnoreCase();
                        boolean flag=false;
                        if(Setting.UserSystemType.ignore.equals(userType.name())){//用户名和密码都不区分
                            flag=checkUser(userInfo,uaccount,upwd,1);
                        }else if(Setting.UserSystemType.uaccountIgnore.equals(userType.name())){//不区分用户名
                            flag=checkUser(userInfo,uaccount,upwd,2);
                        }else if(Setting.UserSystemType.upwdIgnore.equals(userType.name())){//不区分密码
                            flag=checkUser(userInfo,uaccount,upwd,3);
                        }else{//都区分
                            flag=checkUser(userInfo,uaccount,upwd,0);
                        }
                        if(!flag){
                            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                            map.put(Setting.MESSAGE,"用户名或密码错误");
                            return false;
                        }else{
                            map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                            map.put(Setting.MESSAGE,"登录成功");
                            break;
                        }
                    }
                }*/
            }
            return true;
        }catch(Exception e){
            return false;
        }
    }
    /**
     * 判断用户中的用户名和密码是否一致
     *
     * @param userInfo 用户对象
     * @param uaccount 用户名
     * @param upwd     密码
     * @param type     0:都区分,1都不区分,2不区分用户名,3不区分密码
     *
     * @return 一致返回true,不一致返回false
     */
    private boolean checkUser(KnUser userInfo,String uaccount,String upwd,int type){
        boolean flag=false;
        switch(type){
        case 0:{//都区分
            if(userInfo.getLoginName().equals(uaccount.trim())&&userInfo.getPassword().equals(upwd)){
                flag=true;
            }
            break;
        }
        case 1:{//都不区分
            if(userInfo.getLoginName().equalsIgnoreCase(uaccount.trim())&&userInfo.getPassword().equalsIgnoreCase(upwd)){
                flag=true;
            }
            break;
        }
        case 2:{//不区分用户名
            if(userInfo.getLoginName().equalsIgnoreCase(uaccount.trim())&&userInfo.getPassword().equals(upwd)){
                flag=true;
            }
            break;
        }
        case 3:{//不区分密码
            if(userInfo.getLoginName().equals(uaccount.trim())&&userInfo.getPassword().equalsIgnoreCase(upwd)){
                flag=true;
            }
            break;
        }
        default:{//默认都区分
            if(userInfo.getLoginName().equals(uaccount.trim())&&userInfo.getPassword().equals(upwd)){
                flag=true;
            }
            break;
        }
        }
        return flag;
    }
    /**
     * @param userInfo 登录用户对象
     * @param upwd     用户密码
     *
     * @return true 表示用户密码正确,false表示用户密码错误
     *
     * @description 根据用户判断其密码是否正确
     */
    private boolean checkXSimplePwd(KnUser userInfo,String upwd){
        if(userInfo==null){
            return false;
        }
        if(userInfo.getPassword()==null&&upwd==null){//都为空,允许登录
            return true;
        }else if(upwd!=null&&upwd.equals(userInfo.getPassword())){//密码一致
            return true;
        }
        return false;
    }
    /**
     * @Description: (检测设备是否擦除)
     * @param: @param userJson 前台传入参数
     * @param: @param employee 用户
     * @return: boolean    返回boolean true 可以登录 ，false 禁止登录
     */
    private boolean checkUserIsDelete(final Map jsonMap,KnEmployee employee){
        boolean bool=false;
        Map map=new HashMap();
        try{
            String appkey=jsonMap.get("app_key").toString();
            String totken=jsonMap.containsKey("totken")?jsonMap.get("totken").toString().trim().toUpperCase():""; //设备号
            if(!Strings.isNullOrEmpty(appkey)){
                //先查询设备
                List<KnDeviceInfo> chanDelList=knDeviceInfoDao.findAll(new Specification<KnDeviceInfo>(){
                    @Override public Predicate toPredicate(Root<KnDeviceInfo> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                        Predicate predicate=cb.conjunction();
                        List<Expression<Boolean>> expressions=predicate.getExpressions();
                        expressions.add(cb.equal(cb.upper(root.<String>get("deviceToken")),jsonMap.containsKey("totken")?jsonMap.get("totken").toString().trim().toUpperCase():""));
                        return predicate;
                    }
                });
                if(chanDelList!=null&&chanDelList.size()!=0){
                    String delState="";
                    boolean tFlag=false, aFlag=false, dFlag=false, isFlag=false;
                    for(KnDeviceInfo info : chanDelList){
                        delState=null!=info.getDelState()?info.getDelState().toString():"";
                        if(Setting.DeleteStatusType.device.name().toString().equalsIgnoreCase(delState)){
                            //设备被擦除过
                            tFlag=true;
                        }else if(Setting.DeleteStatusType.account.name().toString().equalsIgnoreCase(delState)){
                            //账号被擦除过
                            if(!Strings.isNullOrEmpty(employee.getUserId())&&!Strings.isNullOrEmpty(employee.getUserSystem())&&employee.getUserId().equals(info.getUserId())&&employee.getUserSystem().equals(info.getUserSystem())){
                                aFlag=true;
                            }
                        }
                       /* else if(Setting.DeleteStatusType.dodelete.name().toString().equalsIgnoreCase(delState)){
                            if(!Strings.isNullOrEmpty(employee.getUserId())&&!Strings.isNullOrEmpty(employee.getUserSystem())&&employee.getUserId().equals(info.getUserId())&&employee.getUserSystem().equals(info.getUserSystem())){
                                //待擦除
                                dFlag=true;
                                info.setDelState(Setting.DeleteStatusType.isdelete);//将设备的信息设置为已擦除的状态
                                knDeviceInfoDao.save(info);
                            }
                        }else if(Setting.DeleteStatusType.isdelete.name().toString().equalsIgnoreCase(delState)){
                            //已擦除
                            isFlag=true;
                        }*/
                    }
                    /*if(tFlag){//设备擦出
                        map.put("DELEOFTYPE",Setting.DeleteStatusType.device.toString());
                        map.put(Setting.MESSAGE,"该设备被擦除,无法登录.");
                    }else if(aFlag){//帐号擦出
                        map.put("DELEOFTYPE",Setting.DeleteStatusType.account.toString());
                        map.put(Setting.MESSAGE,"该账号被擦除,无法登录.");
                    }else if(dFlag){//原来使用的dodelete待擦出
                        map.put("DELEOFTYPE",Setting.DeleteStatusType.dodelete.toString());
                        map.put(Setting.MESSAGE,"该设备为待擦除状态,无法登录.");
                    }else if(isFlag){//已擦除
                        map.put("DELEOFTYPE",Setting.DeleteStatusType.isdelete.toString());
                        map.put(Setting.MESSAGE,"该设备为已擦除状态,无法登录.");
                    }*/
                    if(tFlag||aFlag||dFlag||isFlag){
                        //
                        //						jsonObject.put(ConstantAttribute.RESULTCODE, ConstantAttribute.SUCCESSSTAT ); //不能登录
                    }else{
                        //						jsonObject.put(ConstantAttribute.RESULTCODE, ConstantAttribute.SUCCESSSTAT ); //不能登录
                        bool=true;
                    }
                    map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT); //不能登录
                }else{
                    map.put("DELEOFTYPE","");
                    bool=true;
                }
            }
        }catch(Exception e){
            log.info("V3检测擦除错误信息  ----->"+e);
        }
        return bool;
    }
    /**
     * @param @param employee 用户
     *
     * @Description: (设备和用户会进行绑定,更换同平台的设备不能登录)
     * @param: @param jsonMap 前台传入参数
     * @return: boolean    返回boolean
     * @Date 2014-3-6 上午9:53:56
     */
    private boolean valiUserTotken(final Map jsonMap,final KnEmployee employee){
        Map map=new HashMap();
        boolean bool=false;
        String totken="", plateform="";
        try{
            totken=jsonMap.get("totken").toString();
            plateform=jsonMap.get("plateform").toString();
        }catch(Exception e){
        }
        if(Strings.isNullOrEmpty(totken)||Strings.isNullOrEmpty(plateform)){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"设备totken或来自平台为空");
        }else{
            List<KnDeviceInfo> chanList=knDeviceInfoDao.findAll(new Specification<KnDeviceInfo>(){
                @Override public Predicate toPredicate(Root<KnDeviceInfo> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                    Predicate predicate=cb.conjunction();
                    List<Expression<Boolean>> expressions=predicate.getExpressions();
                    expressions.add(cb.equal(root.<String>get("userId"),employee.getUserId()));
                    expressions.add(cb.equal(cb.upper(root.<String>get("formSystem")),employee.getUserSystem().toUpperCase()));
                    expressions.add(cb.equal(cb.upper(root.<String>get("deviceType")),jsonMap.containsKey("plateform")?jsonMap.get("plateform").toString().trim().toUpperCase():""));
                    return predicate;
                }
            });
            if(chanList!=null&&chanList.size()!=0){
                bool=true;
                KnDeviceInfo chInf=new KnDeviceInfo();
                chInf.setUserId(employee.getUserId());
                chInf.setFormSystem(employee.getUserSystem());
                chInf.setDeviceToken(totken);
                if(Utils.isEmptyString(plateform)){
                    plateform=Setting.VersionType.IPHONE.name();
                }
                chInf.setDeviceType(Setting.VersionType.valueOf(plateform));
                knDeviceInfoDao.save(chInf);
            }else{
                KnDeviceInfo updateObj=chanList.get(0);
                if(!totken.equals(updateObj.getDeviceToken())){
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"此账号已经在别的设备登录过,不允许登录");
                }else{
                    bool=true;
                    knDeviceInfoDao.save(updateObj);
                }
            }
        }
        log.info("V3检测设备和用户绑定的结果   ---->"+bool);
        return bool;
    }
    /**
     * 根据用户名和密码到EBS那边进行登录的验证，如果成功，则返回用户的id，
     * 如果失败,则返回空
     */
    public String checkUserByUserNameAndPwd(String userName,String password){
        String returnStr=null;
        try{
            Map jo=new HashMap();
            jo.put("p_oprid",userName==null?"":userName.toUpperCase());//变成大写进行传输
            jo.put("p_oprpwd",password);
            String postData=RestServiceUtil.getRequestString(serviceName,jo,"PS");// 传的JSON
            String paras="data="+postData;
            //TODO
            //dengfeng rest请求改为post方式提交
            String returnData=RestServiceUtil.getStringResponse(paras,restPsUserUrl,true);
            String jsonStr=returnData.trim();
            if(jsonStr!=null&&!"".equals(jsonStr)){
                Map jsonObj=JsonMapper.nonEmptyMapper().fromJson(jsonStr,Map.class);
                List ja=(ArrayList)jsonObj.get("listdata");
                if(ja!=null&&ja.size()!=0){
                    Map jsonObject=(Map)ja.get(0);
                    if("success".equalsIgnoreCase(jsonObj.get("status").toString())){//返回成功,有返回USER_ID
                        returnStr=jsonObject.get("EMPLID").toString();
                    }else{
                        returnStr=null;
                    }
                }
            }
        }catch(Exception e){
            returnStr="";
            log.info("检查用户登录失败,传输的用户名为"+userName+"用户的密码为:"+password+",异常信息为:"+e);
        }finally{
            return returnStr;
        }
    }
    private Map<String,Object> getModuleMap(Map userJson){
        Map<String,Object> map=new HashMap<String,Object>();
        Map<String,Map<String,String>> functionMap=new HashMap<String,Map<String,String>>();
        Map<String,String> zipMap=new HashMap<String,String>();
        Map<String,String> moduleMap=new HashMap<String,String>();
        if(userJson.containsKey("mdatas")){//用户传输的模块的集合数据
            List jsonArray=(ArrayList)userJson.get("mdatas");
            for(int i=0;null!=jsonArray&&i<jsonArray.size();i++){
                Map jo=(Map)jsonArray.get(i);
                if(jo.containsKey("mversion")&&jo.containsKey("mid")){
                    moduleMap.put(jo.get("mid").toString(),jo.get("mversion")==null?"":jo.get("mversion").toString());
                    if(jo.containsKey("items")){
                        List itemsJa=(ArrayList)jo.get("items");
                        Map<String,String> function=new HashMap<String,String>();
                        for(int j=0;null!=itemsJa&&j<itemsJa.size();j++){
                            Map jobj=(Map)itemsJa.get(j);
                            if(jobj.containsKey("fversion")){
                                function.put(jobj.get("fid").toString(),jobj.get("fversion")==null?"":jobj.get("fversion").toString());
                            }
                            if(jobj.containsKey("zipver")){
                                zipMap.put(jobj.get("fid").toString(),jobj.get("zipver")==null?"":jobj.get("zipver").toString());
                            }
                        }
                        functionMap.put(jo.get("mid").toString(),function);
                    }
                }
            }
        }
        map.put("module",moduleMap);
        map.put("function",functionMap);
        map.put("zip",zipMap);
        return map;
    }
    private Map<String,Map<String,String>> getFunctionMap(Map userJson){
        Map<String,Map<String,String>> map=new HashMap<String,Map<String,String>>();
        Map<String,String> functionMap=new HashMap<String,String>();
        Map<String,String> zipMap=new HashMap<String,String>();
        if(userJson.containsKey("items")){//用户传输的模块的集合数据
            List jsonArray=(ArrayList)userJson.get("items");
            for(int i=0;null!=jsonArray&&i<jsonArray.size();i++){
                Map jo=(Map)jsonArray.get(i);
                if(jo.containsKey("fversion")&&jo.containsKey("fid")){
                    functionMap.put(jo.get("fid").toString(),jo.get("fversion")==null?"":jo.get("fversion").toString());
                }
                for(int j=0;null!=jsonArray&&j<jsonArray.size();j++){
                    Map jobj=(Map)jsonArray.get(j);
                    if(jobj.containsKey("fversion")){
                        functionMap.put(jobj.get("fid").toString(),jobj.get("fversion")==null?"":jo.get("fversion").toString());
                    }
                    if(jobj.containsKey("zipver")){
                        zipMap.put(jobj.get("fid").toString(),jobj.get("zipver")==null?"":jo.get("zipver").toString());
                    }
                }
            }
        }
        map.put("function",functionMap);
        map.put("zip",zipMap);
        return map;
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
    /**
     * 根据用户的主键id和appkey获取用户的信息和角色信息等
     *
     * @param uid    用户的主键id
     * @param appkey 应用的appkey
     *
     * @return
     */
    public Map GetUserInfo(Long uid,String appkey){
        Map returnMap=new HashMap();
        if(Utils.isEmptyString(uid)||Utils.isEmptyString(appkey)){
            returnMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            returnMap.put(Setting.MESSAGE,"appkey和用户为空");
            return returnMap;
        }
        KnUser loginUser=knUserDao.findOne(uid);
        KnEmployee employee=knEmployeeDao.findOne(uid);
        if(loginUser==null||employee==null){
            returnMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            returnMap.put(Setting.MESSAGE,"账号不存在,请重新登录");
            return returnMap;
        }
        final String markName=employee.getMarkName();
        final String userSys=employee.getUserSystem();
        Map userJo=new HashMap();
        List userJa=new ArrayList();
        userJo.put("id",loginUser.getId());
        userJo.put("userId",employee.getId());
        userJo.put("uaccount",loginUser.getLoginName());
        userJo.put("upwd",loginUser.getPassword());
        userJo.put("fromSys",employee.getUserSystem());
        userJo.put("imgAddress",employee.getImageAddress());
        userJo.put("fullName",loginUser.getName());
        userJo.put("userType",employee.getUserType());
        userJo.put("email",employee.getEmail());
        userJo.put("phone",employee.getPhone());
        userJo.put("signature",employee.getSignature());
        userJo.put("telephone",employee.getTelephone());
        userJo.put("userNo",employee.getUserId());
        List<String> org=Lists.newArrayList();
        for(KnEmployeeOrganization keo : employee.getOrg()){
            org.add(keo.getId().getOrg().getName());
        }
        userJo.put("department",StringUtils.join(org,","));
        userJa.add(userJo);
        //用户的数组
        returnMap.put("userInfo",userJa);
        //查询用户下的角色列表
        List jsonArray=new ArrayList();
        List<KnRole> roleInfoList=loginUser.getRole();
        if(roleInfoList==null||roleInfoList.size()==0){
            returnMap.put("roleInfos",jsonArray);
        }else{
            //查询应用底下的角色信息进行过滤
            List<Long> roleAppIds=findRoleIdByAppkey(appkey);
            for(KnRole roleInfo : roleInfoList){
                //用户的角色在此应用中才给数据
                if(roleAppIds.contains(roleInfo.getId())){
                    Map jo=new HashMap();
                    jo.put("roleId",roleInfo.getId());
                    jo.put("roleName",roleInfo.getName());
                    jsonArray.add(jo);
                }
            }
        }
        returnMap.put("roleInfos",jsonArray);
        //将云端的地址,kim的地址等信息返回
        returnMap.put("kimDomain",kimDomain);//kim的demo地址
        returnMap.put("kimHost",kimHost);//kim的host地址
        returnMap.put("kndCloudUrl",kndCloudUrl);//云端的地址
        returnMap.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
        returnMap.put(Setting.MESSAGE,"获取成功");
        return returnMap;
    }
    /**
     * 根据应用的appkey获取应用底下的应用的角色id列表
     *
     * @param appkey 应用的apiKey
     *
     * @return 角色的id集合
     */
    private List<Long> findRoleIdByAppkey(String appkey){
        List<Long> roleIds=new ArrayList<Long>();
        if(Strings.isNullOrEmpty(appkey)){
            return roleIds;
        }
        List<KnApplicationInfo> appList=knApplicationInfoDao.findApplicationByAppkey(appkey);
        if(Utils.isEmpityCollection(appList)){
            return roleIds;
        }
        Long appId=appList.get(0).getId();
        List<KnRoleApplicationInfo> roleApplicationInfos=roleApplicationInfoDao.findRoleByAppId(appId);
        if(roleApplicationInfos!=null){
            for(KnRoleApplicationInfo kra : roleApplicationInfos){
                roleIds.add(kra.getRoleId());
            }
        }
        return roleIds;
    }
    /**
     * 根据用户的之间id和相应模块功能等信息,比较相应的地址
     *
     * @param uid        用户的之间id
     * @param jsonMap    模块功能json
     * @param weixinFlag 返回的功能包是zip还是unzip
     *
     * @return
     */
    public Map GetModeulFunctionByUserId(Long uid,Map jsonMap,boolean weixinFlag){
        Map returnMap=new HashMap();
        try{
            if(uid==null){
                returnMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                returnMap.put(Setting.MESSAGE,"用户失效,请重新登录");
                return returnMap;
            }
            KnUser loginUser=knUserDao.findOne(uid);
            if(loginUser==null){
                returnMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                returnMap.put(Setting.MESSAGE,"用户失效,请重新登录");
                return returnMap;
            }
            List<KnRole> roleInfoList=loginUser.getRole();
            //根据应用的appkey查询应用下的角色信息,如果传输了appkey,则过滤appkey获取角色信息
            List<Long> roleAppIds=new ArrayList<Long>();
            if(jsonMap.containsKey(APPKEY)){
                String appkey=(String)jsonMap.get(APPKEY);
                roleAppIds=findRoleIdByAppkey(appkey);
            }
            List<Long> roleIds=new ArrayList<Long>();
            if(!Utils.isEmpityCollection(roleInfoList)){
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
            }
            roleIds.add(0L);
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
            Map<String,Object> map=getModuleMap(jsonMap);
            Map<String,String> moduleMap=(Map<String,String>)map.get("module");
            Map<String,Map<String,String>> functionMap=(Map<String,Map<String,String>>)map.get("function");
            Map<String,String> zipMap=(Map<String,String>)map.get("zip");
            List funKey=new ArrayList();
            for(KnResource moduleInfo : moduleInfoList){
                Map sortMjo=new HashMap();//模块排序
                sortMjo.put("mid",moduleInfo.getId());
                List sortFarr=new ArrayList();//模块下功能的排序数组
                //模块信息
                Map mjo=new HashMap();
                String key=mRMap.get(moduleInfo.getId())+"-"+moduleInfo.getId();
                sortMjo.put("msort",mInRMap.get(key).getRmSort());
                mjo.put("mid",moduleInfo.getId());
                mjo.put("mversion",moduleInfo.getVersion());
                mjo.put("mtitle",moduleInfo.getName());
                mjo.put("enmtitle",moduleInfo.getEnTitle());
                mjo.put("micon",moduleInfo.getIcon());
                mjo.put("moduleKey",moduleInfo.getCode());
                mjo.put("msort",mInRMap.get(key).getRmSort());
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
                Map<String,String> tempMap=new HashMap<String,String>();
                for(KnResource functionInfo : functionInfoList){
                    tempMap=functionMap.get(moduleInfo.getId().toString());
                    //将角色中有的功能加入
                    if(functionRoleMap.containsKey(functionInfo.getId())){
                        Map fjo=new HashMap();
                        Map fsortJo=new HashMap();//模块下的功能的排序
                        //单独把功能的标示拿出来
                        Map funObj=new HashMap();
                        fjo.put("fsort",funcInModuleMap.get(functionInfo.getId())==null?0:funcInModuleMap.get(functionInfo.getId()));
                        boolean flag=true;
                        KnFunctionVersionInfo fv=getVersionByFunction(functionInfo);
                        //数据库中的 功能  要跟传过来的 功能个数相同 并且 版本号也要完全一样
                        if(tempMap==null||!tempMap.containsKey(functionInfo.getId().toString())){//功能不存在
                            fjo.put(Setting.K2,Setting.ADD);
                        }else if(functionInfo.getVersion()!=null&&!functionInfo.getVersion().equals(tempMap.get(functionInfo.getId().toString()))){//功能存在且版本不同
                            fjo.put(Setting.K2,Setting.UPDATE);
                        }else{
                            //功能存在,版本一致,(zip版本一致,zip版本不一致);功能存在版本不一致(zip版本一致,zip版本不一致);
                            //功能存在且zip版本不同
                            if(fv!=null&&fv.getZipVersion()!=null&&!fv.getZipVersion().equals(zipMap.get(functionInfo.getId().toString()))){
                                fjo.put(Setting.K2,Setting.UPDATE);
                            }else{
                                flag=false;
                            }
                        }
                        //功能的标示
                        funObj.put("p_TYPE_CODE",functionInfo.getMarkName());
                        funKey.add(funObj);
                        if(flag){
                            fjo.put("fid",functionInfo.getId());
                            fjo.put("fversion",functionInfo.getVersion());
                            fjo.put("ftitle",functionInfo.getName());
                            fjo.put("enftitle",functionInfo.getEnTitle());
                            fjo.put("ficon",functionInfo.getIcon());
                            if(weixinFlag){
                                fjo.put("zip",fv==null?"":(Utils.isEmptyString(fv.getUnZipUrl())?"":fv.getUnZipUrl()));
                            }else{
                                fjo.put("zip",fv==null?"":(Utils.isEmptyString(fv.getFuncZipUrl())?"":fv.getFuncZipUrl()));
                            }
                            fjo.put("zipver",fv==null?"":fv.getZipVersion());
                            fjo.put("interfaceUrl",fv==null?"":(Strings.isNullOrEmpty(fv.getInterfaceUrl())?"":fv.getInterfaceUrl()));
                            fjo.put("zipsize",fv==null?"":fv.getZipSize());
                            fjo.put("funckey",functionInfo.getMarkName());
                            fjo.put("fversion",functionInfo.getVersion());
                            fjo.put("fstatus",functionInfo.getActive()==null?"":functionInfo.getActive());
                            if(functionInfo.getActive()!=null&&functionInfo.getActive().equals(KnResource.ActiveType.PUBLICPACKAGE)){
                                fjo.put("fsort",Integer.MAX_VALUE);
                                publicJa.add(fjo);
                            }else{
                                fjo.put("fsort",funcInModuleMap.get(functionInfo.getId())==null?0:funcInModuleMap.get(functionInfo.getId()));
                                functionJa.add(fjo);
                            }
                        }
                        if(fv!=null){
                            //去除公共包的排序
                            if(functionInfo.getActive()!=null&&!functionInfo.getActive().equals(KnResource.ActiveType.PUBLICPACKAGE)&&!fv.getWorkStatus().equals(Setting.WorkStatusType.unusable)){
                                fsortJo.put("fsort",funcInModuleMap.get(functionInfo.getId())==null?0:funcInModuleMap.get(functionInfo.getId()));
                                fsortJo.put("fid",functionInfo.getId());
                                sortFarr.add(fsortJo);
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
                    sortMjo.put("items",sortFarr);
                    sortModuleJa.add(sortMjo);
                }
                if(tempMap!=null){
                    for(String key1 : tempMap.keySet()){ //库中不存在的functionId
                        Map obj=new HashMap();
                        obj.put(Setting.K2,Setting.DELETE);
                        obj.put("fid",key1);//
                        functionJa.add(obj);
                    }
                }
                //比较模块信息
                if(!moduleMap.containsKey(moduleInfo.getId().toString())){ //client 中不存在 库中的module
                    //库中存在 ，传过来的不存在
                    mjo.put(Setting.K1,Setting.ADD);
                }else{ //client 中存在库中的module
                    mjo.put(Setting.K1,Setting.UPDATE);
                }
                functionJa.addAll(publicJa);
                if(functionJa!=null&&functionJa.size()!=0){
                    //功能信息加入模块中去
                    mjo.put("items",functionJa);
                    moduleList.add(mjo);
                }
                moduleMap.remove(moduleInfo.getId().toString());
            }
            for(String key2 : moduleMap.keySet()){ //库中不存在的moduleId
                Map moduleJson=new HashMap();
                moduleJson.put(Setting.K1,Setting.DELETE);
                moduleJson.put("mid",key2);
                moduleJson.put("mversion",moduleMap.get(key2));
                moduleJson.put("items",new ArrayList<>());
                moduleList.add(moduleJson);
            }
            //返回角色下的模块信息
            returnMap.put("mdatas",moduleList);
            //将排序加入
            returnMap.put("sdatas",sortModuleJa);
            //将功能标识单独拿出来存放
            funKey=Utils.removeDuplicate(funKey);
            returnMap.put("funKey",funKey);
            returnMap.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
            returnMap.put(Setting.MESSAGE,"获取成功");
        }catch(Exception ex){
            log.error("工作区获取异常：{}",ex);
            returnMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            returnMap.put(Setting.MESSAGE,"获取失败");
        }finally{
            return returnMap;
        }
    }
    /**
     * 获取用户的信息,没有传输值代表获取用户自己的信息,
     *
     * @return
     */
    public Map GetUserInfo(String json){
        Map map=new HashMap();
        Long id=null;
        if(Utils.isEmptyString(json)){
            id=Users.id();
        }else{
            Map jsonMap=JsonMapper.nonEmptyMapper().fromJson(json,Map.class);
            id=jsonMap.containsKey("id")?(Long)jsonMap.get("id"):0;
            if(id==null||id==0){
                id=Users.id();
            }
        }
        KnEmployee knEmployee=knEmployeeDao.findOne(id);
        if(null==knEmployee){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"用户信息为空");
        }else{
            map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
            map.put("id",knEmployee.getId());
            map.put("userName",Utils.filterNullValue(knEmployee.getUserName()));
            map.put("imageAddress",Utils.filterNullValue(knEmployee.getImageAddress()));
            map.put("phone",Utils.filterNullValue(knEmployee.getPhone()));
            map.put("telephone",Utils.filterNullValue(knEmployee.getTelephone()));
            map.put("email",Utils.filterNullValue(knEmployee.getEmail()));
            map.put("address",Utils.filterNullValue(knEmployee.getAddress()));
            map.put("sex",knEmployee.getSex());
            map.put("signature",Utils.filterNullValue(knEmployee.getSignature()));
        }
        return map;
    }
    /**
     * 更新用户的信息
     *
     * @param jsonparm{"id":"主键id","userName":"用户名称","imageAddress":"base64转换用户图像地址","phone":"用户手机号码","telephone":"用户座机号码","email":"邮箱","address":"用户地址"} 例如 {"id":"1","userName":"小黑","imageAddress":"","phone":"1346411111","telephone":"0755-1111111","email":"ssss@qq.com","address":"xxxxxx"}
     *
     * @return
     */
    @Transactional(readOnly=false)  //  关闭只读 ,对数据库有操作
    public Map UpdateUserInfo(String jsonparm){
        log.info("更新用户的信息,接受参数如下：---->jsonparm:"+jsonparm);
        Map map=new HashMap();
        if(Utils.isEmptyString(jsonparm)){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"参数为空");
        }else{
            Map<String,String> jsonMap=JsonMapper.nonEmptyMapper().fromJson(jsonparm,Map.class);
            if(null!=jsonMap&&jsonMap.size()>0){
                Long id=Users.id();
                if(Utils.isEmptyString(id)){
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"用户ID为空");
                }else{
                    KnEmployee knEmployee=knEmployeeDao.findOne(id);
                    if(null==knEmployee){
                        map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                        map.put(Setting.MESSAGE,"用户信息为空");
                    }else{
                        String userName=jsonMap.containsKey("userName")?jsonMap.get("userName"):Setting.LINE_SEPEAC;  //用户名称
                        String imageAddress=jsonMap.containsKey("imgAddress")?jsonMap.get("imgAddress"):"";  //用户图像地址
                        String phone=jsonMap.containsKey("phone")?jsonMap.get("phone"):Setting.LINE_SEPEAC;  //用户手机号码
                        String telephone=jsonMap.containsKey("telephone")?jsonMap.get("telephone"):Setting.LINE_SEPEAC;  //用户座机号码
                        String email=jsonMap.containsKey("email")?jsonMap.get("email"):Setting.LINE_SEPEAC; //用户邮箱
                        String address=jsonMap.containsKey("address")?jsonMap.get("address"):Setting.LINE_SEPEAC; //用户地址
                        String signture=jsonMap.containsKey("signature")?jsonMap.get("signature"):Setting.LINE_SEPEAC;//个性签名
                        if(!Setting.LINE_SEPEAC.equals(userName)){
                            knEmployee.setUserName(userName);
                        }
                        if(!Setting.LINE_SEPEAC.equals(phone)){
                            knEmployee.setPhone(phone);
                        }
                        if(!Setting.LINE_SEPEAC.equals(telephone)){
                            knEmployee.setTelephone(telephone);
                        }
                        if(!Setting.LINE_SEPEAC.equals(address)){
                            knEmployee.setAddress(address);
                        }
                        if(!Setting.LINE_SEPEAC.equals(email)){
                            knEmployee.setEmail(email);
                        }
                        if(!Setting.LINE_SEPEAC.equals(signture)){
                            knEmployee.setSignature(signture);
                        }
                        if(!Utils.isEmptyString(imageAddress)){
                            String imagePath=PathUtil.getRootPath()+Setting.USERIMAGE;
                            File savedir=new File(imagePath);
                            if(!savedir.exists()){
                                savedir.mkdirs();
                            }
                            String newImage=imageAddress.replaceAll(" ","+");
                            saveFileToDisk(newImage,imagePath+"/"+knEmployee.getId()+".jpg");
                            knEmployee.setImageAddress(Setting.USERIMAGE+knEmployee.getId()+".jpg");
                        }
                        knEmployeeDao.save(knEmployee);
                        map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                        map.put(Setting.MESSAGE,"更新成功");
                        map.put("imgAddress",knEmployee.getImageAddress());//更新图像的话,返回图片名字,其他则返回空
                    }
                }
            }else{
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"参数格式错误");
            }
        }
        return map;
    }
}
