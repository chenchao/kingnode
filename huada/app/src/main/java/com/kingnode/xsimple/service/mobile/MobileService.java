package com.kingnode.xsimple.service.mobile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.ShiroUser;
import com.kingnode.xsimple.dao.application.KnApplicationInfoDao;
import com.kingnode.xsimple.dao.application.KnVersionInfoDao;
import com.kingnode.xsimple.dao.push.KnCertificateDao;
import com.kingnode.xsimple.dao.push.KnDeviceInfoDao;
import com.kingnode.xsimple.dao.push.KnMessageDao;
import com.kingnode.xsimple.dao.system.KnEmployeeDao;
import com.kingnode.xsimple.entity.application.KnApplicationInfo;
import com.kingnode.xsimple.entity.application.KnVersionInfo;
import com.kingnode.xsimple.entity.push.KnCertificateInfo;
import com.kingnode.xsimple.entity.push.KnDeviceInfo;
import com.kingnode.xsimple.entity.push.KnPushMessageInfo;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.util.Users;
import com.kingnode.xsimple.util.Utils;
import com.kingnode.xsimple.util.push.IosPushUtil;
import com.kingnode.xsimple.util.version.VersionNumUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * 手机注册设备信息,注销信息,检测版本信息对外提供接口
 *
 * @author wangyfian
 */
@Component @Transactional(readOnly=true)
public class MobileService{
    private static Logger logger=LoggerFactory.getLogger(MobileService.class);
    private KnEmployeeDao knEmployeeDao;
    private KnVersionInfoDao knVersionInfoDao;
    private KnApplicationInfoDao knApplicationInfoDao;
    private KnDeviceInfoDao knDeviceInfoDao;
    private KnCertificateDao knCertificateDao;
    private KnMessageDao messDao;
    @Value("#{commonInfo['androidHttpUrl']}")
    private String androidHttpUrl;//ANDROID 推送连接的地址
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
    /**
     * 注册设备信息
     *
     * @param jsonMap 手机端传入参数
     *                参数含义如下： {"xtype":"mdmRegisterInfo","appkey":"应用标示","totken":"设备标示","plateform":"设备来自平台","version":"版本号","userPhoneName":"设备型号","versionType":"版本的状态","zipVersion":"公共包的版本号","userPhone":"手机号码","regTime":"最后登录时间","fromSys":"设备来自产品"}
     *                例如：        {"xtype":"mdmRegisterInfo","appkey":"111111111123213","totken":"sdfadsfadsasdasd1123123","plateform":"IPHONE","version":"1.35","userPhoneName":"小米3","versionType":"usable","zipVersion":"1.3","userPhone":"31649714646","regTime":"2014-07-01 18:58:12","fromSys":"eam"}
     *
     * @return
     */
    @Transactional(readOnly=false)  //  关闭只读 ,对数据库有操作
    public Map MdmRegisterInfo(Map<String,String> jsonMap){
        long chanId=Long.valueOf(0);
        Map map=new HashMap();
        try{
            Map userMap=getUserInfo();
            if(Setting.FAIURESTAT.equals(userMap.get(Setting.RESULTCODE))){
                return userMap;
            }
            String user_id=(String)userMap.get("userId");//用户id
            String user_sys=(String)userMap.get("userSystem");  //用户来自系统
            String app_key=jsonMap.containsKey("appkey")?jsonMap.get("appkey"):""; //应用的标示
            String versionType=jsonMap.containsKey("versionType")?jsonMap.get("versionType"):"";//版本的状态
            String plate_form=jsonMap.containsKey("plateform")?jsonMap.get("plateform"):"";   //设备来自的平台 (IPHONE  android)
            String version=jsonMap.containsKey("version")?jsonMap.get("version"):"0";   //版本号
            String  totkenIsCheck = jsonMap.containsKey("totkenIsCheck")?jsonMap.get("totkenIsCheck"):"true";
            List<KnApplicationInfo> knApplicationInfoList=knApplicationInfoDao.findApplicationByAppkey(app_key);
            if(Utils.isEmpityCollection(knApplicationInfoList)){
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"应用不存在");
                return map;
            }
            Long updateTime = 0L;
            String totken=jsonMap.containsKey("totken")?jsonMap.get("totken"):"";  //设备totken
            if("true".equalsIgnoreCase(totkenIsCheck)||("false".equalsIgnoreCase(totkenIsCheck)&&!Utils.isEmptyString(totken))){
                String regTime=jsonMap.containsKey("regTime")?String.valueOf((Object)jsonMap.get("regTime")):"";  //最后登录时间
                List<KnDeviceInfo> devList=knDeviceInfoDao.findListByTotken(totken);
                Map devMap=checkDeviceIsOrDelete(devList,user_id,user_sys);
                if(Setting.FAIURESTAT.equals(devMap.get(Setting.RESULTCODE))||Setting.DeleteStatusType.device.name().equals(devMap.get("DELEOFTYPE"))||Setting.DeleteStatusType.account.name().equals(devMap.get("DELEOFTYPE"))){
                    return devMap;
                }
                List<KnDeviceInfo> channelInfoList=knDeviceInfoDao.findByUsrIdAndFormSys(user_id,user_sys,app_key,totken);
                KnDeviceInfo chInf=null;
                if(Utils.isEmpityCollection(channelInfoList)){ //设备信息不存在
                    jsonMap.put("userId",user_id);
                    jsonMap.put("userSystem",user_sys);
                    Map<String,Object> chanMap=createChannelInfo(jsonMap);
                    if((Boolean)chanMap.get("bool")){
                        chanId=(long)chanMap.get("chanId");
                        chInf=(KnDeviceInfo)chanMap.get("KnDeviceInfo");
                    }
                }else{
                    chInf=channelInfoList.get(0);
                    chInf.setChversion(version);
                    chInf.setUpdateTime(System.currentTimeMillis());
                    chInf.setUserPhone(jsonMap.containsKey("userPhone")?jsonMap.get("userPhone"):"");
                    chInf.setDeviceName(jsonMap.containsKey("userPhoneName")?jsonMap.get("userPhoneName"):"");
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
                final String pushMessname=chInf.getPushMessname();
                Boolean sendOffmessage=jsonMap.containsKey("sendOffmessage")?Boolean.parseBoolean(jsonMap.get("sendOffmessage")):false;
                if(sendOffmessage){
                    Thread.sleep(1000);
                    sendOfflineMess(pushMessname);
                }
                List<KnDeviceInfo> updateList=null;//推送设备信息
                if(Utils.isEmptyString(regTime)){
                    updateList=knDeviceInfoDao.findByAppkeyAndStat(user_id,user_sys,app_key,totken,Setting.OnlineType.online.name());
                }else{
                    updateList=knDeviceInfoDao.findByAppkeyAndStatAndTime(user_id,user_sys,app_key,totken,Setting.OnlineType.online.name(),Long.valueOf(regTime));
                }
                setOffList(updateList,totken,chanId);//设置下线状态
                Long time=Utils.isEmptyString(chInf.getUpdateTime())?chInf.getCreateTime():chInf.getUpdateTime();
                pushInfo(updateList,app_key,chInf.getDeviceName(),time);//推送下线信息
                if(null!=chInf){
                    updateTime =  chInf.getUpdateTime();
                }
            }
            map=checkNewVerList(app_key,plate_form,versionType,version);
            if(Setting.SUCCESSSTAT.equals(map.get(Setting.RESULTCODE))){
                map.put("regTime",updateTime);
            }
        }catch(Exception e){
            logger.error("注册设备信息,错误信息 {}",e);
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return map;
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
                    delState=Utils.isEmptyString(info.getDelState())?"":info.getDelState().name();
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
    private Map checkNewVerList(String app_key,String plate_form,String versionType,String version){
        Map map=new HashMap();
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
            List<KnVersionInfo> versionList=VersionNumUtil.getMaxVerListByNum(knVersionInfoList,version);
            if(!Utils.isEmpityCollection(versionList)){//提示有版本更新
                title="有新版本,请更新";
                KnVersionInfo knVerObj = versionList.get(0);
                if(Setting.VersionType.IPHONE.name().equals(plate_form)||Setting.VersionType.IPAD.name().equals(plate_form)){
                    newUrl=knVerObj.getIosHttpsAddress()==null?"":knVerObj.getIosHttpsAddress();
                }else{
                    newUrl=knVerObj.getAddress()==null?"":knVerObj.getAddress();
                }
                vf = Utils.isEmptyString(knVerObj.getForcedUpdate())?vf:knVerObj.getForcedUpdate();
            }
            map.put("title",title);
            map.put("newUrl",newUrl);
            map.put("vf",vf);
            map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
        }catch(Exception e){
            logger.error("检测新版本信息,错误信息 {}",e);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
        }
        return map;
    }
    /**
     * 新增设备信息
     *
     * @param jsonMap 前台传入参数信息
     *                参数含义如下： {"xtype":"mdmRegisterInfo","userId":"用户id","userSystem":"用户来自系统","appkey":"应用标示","totken":"设备标示","plateform":"设备来自平台","version":"版本号","userPhoneName":"设备型号","versionType":"版本的状态","zipVersion":"公共包的版本号","userPhone":"手机号码","regTime":"最后登录时间","fromSys":"设备来自产品"}
     *                例如：        {"xtype":"mdmRegisterInfo","userId":"111","userSystem":"ERP","appkey":"111111111123213","totken":"sdfadsfadsasdasd1123123","plateform":"IPHONE","version":"1.35","userPhoneName":"小米3","versionType":"usable","zipVersion":"1.3","userPhone":"31649714646","regTime":"2014-07-01 18:58:12","fromSys":"eam"}
     *
     * @return 返回成功状态以及设备的主键id
     */
    private Map<String,Object> createChannelInfo(Map<String,String> jsonMap){
        boolean bool=false;
        Map<String,Object> backMap=new HashMap<String,Object>();
        try{
            KnDeviceInfo ci=new KnDeviceInfo();
            String app_key=jsonMap.containsKey("appkey")?jsonMap.get("appkey"):""; //应用的标示
            if(!Utils.isEmptyString(app_key)){
                List<KnApplicationInfo> knApplicationInfoList=knApplicationInfoDao.findApplicationByAppkey(app_key);
                if(!Utils.isEmpityCollection(knApplicationInfoList)){
                    String totken=jsonMap.containsKey("totken")?jsonMap.get("totken"):"",
                            fromSys=jsonMap.containsKey("fromSys")?jsonMap.get("fromSys"):"",
                            loginName=jsonMap.containsKey("loginName")?jsonMap.get("loginName"):"",
                            plateform=jsonMap.containsKey("plateform")?jsonMap.get("plateform"):"";
                    KnApplicationInfo knApplicationInfo=knApplicationInfoList.get(0);
                    ci.setAppId( knApplicationInfo.getId() );
                    ci.setApiKey( knApplicationInfo.getApiKey() );
                    ci.setAppTitle( knApplicationInfo.getTitle() );
                    ci.setChversion(jsonMap.containsKey("version")?jsonMap.get("version"):"");
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
                    ci.setDeviceToken(jsonMap.containsKey("totken")?jsonMap.get("totken"):"");
                    ci.setFormSystem(jsonMap.containsKey("fromSys")?jsonMap.get("fromSys"):"");
                    ci.setDelState(Setting.DeleteStatusType.nodelete);//设置擦除的状态
                    ci.setUserPhone(jsonMap.containsKey("userPhone")?jsonMap.get("userPhone"):"");
                    ci.setOnlineStat(Setting.OnlineType.online.name());
                    String versionType=jsonMap.containsKey("versionType")?jsonMap.get("versionType"):"";//版本的状态
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
                    ci.setDeviceName(jsonMap.containsKey("userPhoneName")?jsonMap.get("userPhoneName"):"");
                    ci.setUserSystem(jsonMap.containsKey("userSystem")?jsonMap.get("userSystem"):"");
                    ci.setUserId(jsonMap.containsKey("userId")?jsonMap.get("userId"):"");
                    knDeviceInfoDao.save(ci);
                    bool=true;
                    backMap.put("chanId",ci.getId());
                    backMap.put("KnDeviceInfo",ci);
                }
            }
        }catch(Exception e){
            logger.error("注册设备信息,错误信息 {}",e);
        }
        backMap.put("bool",bool);
        return backMap;
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
                String xtype=Setting.Xtype.OFFLINE.name(), pushBody="此账号在别处登录，你被迫下线！";
                pushMap.put("xtype",xtype);
                pushMap.put("time",time);
                pushMap.put("dName",deviceName);
                IosPushUtil pushUtil=IosPushUtil.getInstall();
                List<KnCertificateInfo> pushList=knCertificateDao.findCerListByAppkey(app_key);
                Map iosMap=pushUtil.pushIosInfo(JsonMapper.nonDefaultMapper().toJson(pushMap),pushBody,pushList,totkenList);//IOS 推送下线消息
                String timeStr = "" ;
                if(!Utils.isEmptyString(time)){
                    timeStr = new DateTime(time).toString("yyyy-MM-dd HH:mm:ss");
                }
                String messageBody = "你的账号已于"+timeStr+"在其它地方登录。登录设备是"+deviceName+",请注意账号安全。如果这不是你的操作，你的密码很可能已经泄漏，建议联系管理员处理！" ;
                Map androidMap=pushUtil.sendAndroidMess(updateList,"账号存在风险",messageBody,Setting.ANDROIDOFFLINE);//ANDROID 推送下线消息
                boolean iosBool=false, androidBool=false;
                if(null!=iosMap&&iosMap.size()>0){
                    if(Setting.SUCCESSSTAT.equals(iosMap.get(Setting.RESULTCODE))){
                        iosBool=true;
                    }
                }
                if(null!=androidMap&&androidMap.size()>0){
                    if(Setting.SUCCESSSTAT.equals(androidMap.get(Setting.RESULTCODE))){
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
            logger.error("注册设备信息,推送错误信息 {}",e);
        }
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
            logger.error("注册设备信息,设置下线状态错误信息 {}",e);
        }
    }
    /**
     * @param jsonMap 手机端传入参数
     *                参数含义如下： {"xtype":"logOut","totken":"设备标示","plateform":"设备来自平台"}
     *                例如：        {"xtype":"logOut","totken":"sdfadsfadsasdasd1123123","plateform":"IPHONE"}
     *
     * @Description: (注销接口,便于推送下线以及其他消息)
     */
    @Transactional(readOnly=false)  //  关闭只读 ,对数据库有操作
    public Map UpdateKnChannelStat(Map<String,String> jsonMap){
        Map map=new HashMap();
        try{
            String  totkenIsCheck = jsonMap.containsKey("totkenIsCheck")?jsonMap.get("totkenIsCheck"):"true";
            if(!"true".equalsIgnoreCase(totkenIsCheck)){
                map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                map.put(Setting.MESSAGE,"注销成功");
                return  map;
            }
            Map userMap=getUserInfo();
            if(Setting.FAIURESTAT.equals(userMap.get(Setting.RESULTCODE))){
                return userMap;
            }
            String user_id=(String)userMap.get("userId");//用户id
            String user_sys=(String)userMap.get("userSystem");  //用户来自系统
            String totken=jsonMap.containsKey("totken")?jsonMap.get("totken"):"";  //设备totken
            String plate_form=jsonMap.containsKey("plateform")?jsonMap.get("plateform"):"";   //设备来自的平台 (IPHONE  android)
            if(Utils.isEmptyString(plate_form)){
                plate_form=Setting.VersionType.IPHONE.name();
            }else{//下面放置格式化失败
                String finType="";
                for(Setting.VersionType type : Setting.VersionType.values()){
                    if(type.name().equals(plate_form)){
                        finType=plate_form;
                    }
                }
                if(Utils.isEmptyString(finType)){
                    plate_form=Setting.VersionType.IPHONE.name();
                }
            }
            knDeviceInfoDao.updateKnChannelStat(Setting.OnlineType.off.name(),totken,Setting.VersionType.valueOf(plate_form.toUpperCase()),user_id,user_sys);
            map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
            map.put(Setting.MESSAGE,"注销成功");
        }catch(Exception e){
            logger.error("注销接口,错误信息 {}",e);
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return map;
    }
    /**
     * 检测新版本信息
     *
     * @param jsonMap 手机端传入参数
     *                参数含义如下： {"xtype":"checkVersion","appkey":"应用标示","plateform":"设备来自平台","version":"版本号","versionType":"版本的状态"}
     *                例如：        {"xtype":"checkVersion","appkey":"111111111123213","plateform":"IPHONE","version":"1.35","versionType":"usable"}
     *
     * @return
     */
    public Map CheckVersion(Map<String,String> jsonMap){
        Map map=new HashMap();
        try{
            String app_key=jsonMap.containsKey("appkey")?jsonMap.get("appkey"):""; //应用的标示
            String versionType=jsonMap.containsKey("versionType")?jsonMap.get("versionType"):"";//版本的状态
            String plate_form=jsonMap.containsKey("plateform")?jsonMap.get("plateform"):"";   //设备来自的平台 (IPHONE  android)
            String version=jsonMap.containsKey("version")?jsonMap.get("version"):"0";   //版本号 (1.02)
            map=checkNewVerList(app_key,plate_form,versionType,version);
        }catch(Exception e){
            logger.error("检测新版本信息,错误信息 {}",e);
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return map;
    }
    /**
     * 检测设备是否在线,以及擦除
     *
     * @param jsonMap 手机端传入参数
     *                参数含义如下： {"xtype":"checkDeviceOnLine","appkey":"应用标示","totken":"设备标示"}
     *                例如：        {"xtype":"checkDeviceOnLine","appkey":"111111111123213","totken":"dsfasdfsda"}
     *
     * @return
     */
    public Map CheckDeviceOnLine(Map<String,String> jsonMap){
        Map devMap=new HashMap();
        try{
            String  totkenIsCheck = jsonMap.containsKey("totkenIsCheck")?jsonMap.get("totkenIsCheck"):"true";
            if(!"true".equalsIgnoreCase(totkenIsCheck)){
                devMap.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                devMap.put("TYPE","ONLINE");
                devMap.put("DELEOFTYPE",Setting.DeleteStatusType.nodelete.name());
                return  devMap;
            }
            Map userMap=getUserInfo();
            if(Setting.FAIURESTAT.equals(userMap.get(Setting.RESULTCODE))){
                return userMap;
            }
            String user_id=(String)userMap.get("userId");//用户id
            String user_sys=(String)userMap.get("userSystem");  //用户来自系统
            String app_key=jsonMap.containsKey("appkey")?jsonMap.get("appkey"):""; //应用的标示
            String totken=jsonMap.containsKey("totken")?jsonMap.get("totken"):"";  //设备totken
            List<KnDeviceInfo> devList=knDeviceInfoDao.findListByTotken(totken);
            devMap=checkDeviceIsOrDelete(devList,user_id,user_sys);
            if(Setting.FAIURESTAT.equals(devMap.get(Setting.RESULTCODE))||Setting.DeleteStatusType.device.name().equals(devMap.get("DELEOFTYPE"))||Setting.DeleteStatusType.account.name().equals(devMap.get("DELEOFTYPE"))){
                return devMap;
            }
            List<KnDeviceInfo> onOrOffList=knDeviceInfoDao.findByTotkenAndUserIdAndForsys(totken,user_id,user_sys,Setting.OnlineType.online.name());
            if(Utils.isEmpityCollection(onOrOffList)){
                devMap.put("TYPE","OFFLINE");
                String time="", dName="";
                List<KnDeviceInfo> offList=knDeviceInfoDao.findByTotkenAndUserIdAndForsys(totken,user_id,user_sys,Setting.OnlineType.off.name());
                if(!Utils.isEmpityCollection(offList)){
                    KnDeviceInfo firObj=offList.get(0);
                    Long updateTime=Utils.isEmptyString(firObj.getUpdateTime())?firObj.getCreateTime():firObj.getUpdateTime();
                    List<KnDeviceInfo> otherList=knDeviceInfoDao.findListByAppkeyAndTime(user_id,user_sys,app_key,totken,updateTime);
                    if(!Utils.isEmpityCollection(otherList)){
                        KnDeviceInfo senObj=otherList.get(0);
                        time=senObj.getUpdateTime()+"";
                        dName=senObj.getDeviceName();
                    }
                }
                devMap.put("time",time);
                devMap.put("dName",dName);
            }else{
                devMap.put("TYPE","ONLINE");
            }
        }catch(Exception e){
            logger.error("检测设备是否在线,以及擦除 {}",e);
            devMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            devMap.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return devMap;
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
            Long user_id=knEmployee.getId();//员工主键id
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
            devMap.put("userId",String.valueOf(user_id));
            devMap.put("userSystem",user_sys);
            devMap.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
        }catch(Exception e){
            logger.error("获取用户的来自系统以及用户ID {}",e);
            devMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            devMap.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return devMap;
    }
}
