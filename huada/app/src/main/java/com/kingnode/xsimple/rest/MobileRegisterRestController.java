package com.kingnode.xsimple.rest;
import java.util.HashMap;
import java.util.Map;

import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.service.mobile.MobileService;
import com.kingnode.xsimple.util.Utils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequestMapping(value="/api/v1/phone")
public class MobileRegisterRestController{
    private static Logger logger=LoggerFactory.getLogger(MobileRegisterRestController.class);
    @Autowired
    private MobileService mobileService;
    @Value("#{commonInfo['sendOffmessage']}")
    private boolean sendOffmessage;//是否需要发送离线消息 开关
    @Value("#{commonInfo['totkenIsCheck']}")
    private String totkenIsCheck;//是否需要验证totken,即是否需要推送消息服务 开关
    /**
     * 手机注册设备信息,注销信息,检测版本信息统一入口
     *
     * @param jsonparm 手机端传入参数
     *                 参数含义如下： {"xtype":"mdmRegisterInfo","appkey":"应用标示","totken":"设备标示","plateform":"设备来自平台","version":"版本号","userPhoneName":"设备型号","versionType":"版本的状态","zipVersion":"公共包的版本号","userPhone":"手机号码","regTime":"最后登录时间","fromSys":"设备来自产品"}
     *                 例如：        {"xtype":"mdmRegisterInfo","appkey":"111111111123213","totken":"sdfadsfadsasdasd1123123","plateform":"IPHONE","version":"1.35","userPhoneName":"小米3","versionType":"usable","zipVersion":"1.3","userPhone":"31649714646","regTime":"2014-07-01 18:58:12","fromSys":"eam"}
     */
    @RequestMapping(value="info",method=RequestMethod.POST)
    public Map registerMobileInfo(@RequestParam(value="jsonparm") String jsonparm){
        logger.info("手机注册设备信息,注销信息,检测版本信息统一入口,接受参数如下：---->jsonparm:"+jsonparm);
        Map map=new HashMap();
        try{
            if(Utils.isEmptyString(jsonparm)){
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"参数为空");
            }else{
                Map<String,String> jsonMap=JsonMapper.nonEmptyMapper().fromJson(jsonparm,Map.class);
                if(null!=jsonMap&&jsonMap.size()>0){
                    String xtype=jsonMap.containsKey("xtype")?jsonMap.get("xtype"):"";
                    totkenIsCheck=Utils.isEmptyString(totkenIsCheck)?"true":totkenIsCheck;
                    jsonMap.put("totkenIsCheck",totkenIsCheck);
                    if("mdmRegisterInfo".equals(xtype)){
                        jsonMap.put("sendOffmessage",sendOffmessage+"");
                        map=mdmRegisterInfo(jsonMap);
                    }else if("logOut".equals(xtype)){//设备注销
                        map=logOut(jsonMap);
                    }else if("checkVersion".equals(xtype)){//检查版本
                        map=checkVersion(jsonMap);
                    }else if("checkDeviceOnLine".equals(xtype)){
                        map=checkDeviceOnLine(jsonMap);
                    }else{
                        map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                        map.put(Setting.MESSAGE,"没有相应的请求,请检查后,再请求");
                    }
                }else{
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"参数格式错误");
                }
            }
        }catch(Exception e){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }finally{
            logger.info("手机注册设备信息,注销信息,检测版本信息统一入口,返回信息如下：---->:"+map);
            return map;
        }
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
    private Map checkDeviceOnLine(Map<String,String> jsonMap){
        Map map=new HashMap();
        try{
            map=checkInfo(jsonMap,"4");
            if(Setting.SUCCESSSTAT.equals(map.get(Setting.RESULTCODE))){
                map=mobileService.CheckDeviceOnLine(jsonMap);
            }
        }catch(Exception e){
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
    private Map checkVersion(Map<String,String> jsonMap){
        Map map=new HashMap();
        try{
            map=checkInfo(jsonMap,"3");
            if(Setting.SUCCESSSTAT.equals(map.get(Setting.RESULTCODE))){
                map=mobileService.CheckVersion(jsonMap);
            }
        }catch(Exception e){
            logger.error("检查版本更新异常:{}",e);
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return map;
    }
    /**
     * 设备注销接口
     *
     * @param jsonMap 手机端传入参数
     *                参数含义如下： {"xtype":"logOut","totken":"设备标示","plateform":"设备来自平台"}
     *                例如：        {"xtype":"logOut","totken":"sdfadsfadsasdasd1123123","plateform":"IPHONE"}
     *
     * @return
     */
    private Map logOut(Map<String,String> jsonMap){
        Map map=new HashMap();
        try{
            map=checkInfo(jsonMap,"2");
            if(Setting.SUCCESSSTAT.equals(map.get(Setting.RESULTCODE))){
                map=mobileService.UpdateKnChannelStat(jsonMap);
                Subject s=SecurityUtils.getSubject();
                s.logout();
            }
        }catch(Exception e){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return map;
    }
    /**
     * 手机注册通知信息
     *
     * @param jsonMap 手机端传入参数
     *                参数含义如下： {"xtype":"mdmRegisterInfo","loginName":"登录账号","appkey":"应用标示","totken":"设备标示","plateform":"设备来自平台","version":"版本号","userPhoneName":"设备型号","versionType":"版本的状态","zipVersion":"公共包的版本号","userPhone":"手机号码","regTime":"最后登录时间","fromSys":"设备来自产品"}
     *                例如：        {"xtype":"mdmRegisterInfo","loginName":"usable","appkey":"111111111123213","totken":"sdfadsfadsasdasd1123123","plateform":"IPHONE","version":"1.35","userPhoneName":"小米3","versionType":"usable","zipVersion":"1.3","userPhone":"31649714646","regTime":"","fromSys":"eam"}
     *
     * @return 返回注册是否成功以及是否有新版本等信息
     */
    private Map mdmRegisterInfo(Map<String,String> jsonMap){
        Map map=new HashMap();
        try{
            map=checkInfo(jsonMap,"1");
            if(Setting.SUCCESSSTAT.equals(map.get(Setting.RESULTCODE))){
                // 具体注册信息begin
                map=mobileService.MdmRegisterInfo(jsonMap);
                // 具体注册信息end
            }
        }catch(Exception e){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }finally{
            return map;
        }
    }
    /**
     * 检测前端传入参数,是否验证通过
     *
     * @param jsonMap
     *
     * @return 返回是否通过信息resCode = 500为不通过
     */
    private Map checkInfo(Map<String,String> jsonMap,String tip){
        Map map=new HashMap();
        try{
            String totken=jsonMap.containsKey("totken")?jsonMap.get("totken"):"";  //设备totken
            String plateform=jsonMap.containsKey("plateform")?jsonMap.get("plateform"):"";   //设备来自的平台 (IPHONE  android)
            String appkey=jsonMap.containsKey("appkey")?jsonMap.get("appkey"):""; //应用的标示
            String loginName=jsonMap.containsKey("loginName")?jsonMap.get("loginName"):""; //登录账号
            String fromSys=jsonMap.containsKey("fromSys")?jsonMap.get("fromSys"):""; //应用来自哪个产品
            String totkenIsCheck=jsonMap.containsKey("totkenIsCheck")?jsonMap.get("totkenIsCheck"):"true";
            if("1".equals(tip)||"2".equals(tip)||"3".equals(tip)){
                if(Utils.isEmptyString(plateform)){
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"设备来自的平台为空");
                    return map;
                }
            }
            if("1".equals(tip)){
                if(Utils.isEmptyString(loginName)){
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"登录账号为空");
                    return map;
                }
                if(Utils.isEmptyString(fromSys)){
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"应用来自产品为空");
                    return map;
                }
            }
            if("true".equalsIgnoreCase(totkenIsCheck)){
                if("1".equals(tip)||"2".equals(tip)||"4".equals(tip)){
                    if(Utils.isEmptyString(totken)){
                        map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                        map.put(Setting.MESSAGE,"设备totken为空");
                        return map;
                    }
                }
            }
            if("1".equals(tip)||"3".equals(tip)||"4".equals(tip)){
                if(Utils.isEmptyString(appkey)){
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"应用的标示为空");
                    return map;
                }
            }
            map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
        }catch(Exception e){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return map;
    }
}
