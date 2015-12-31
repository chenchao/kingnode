package com.kingnode.xsimple.rest;
import java.util.HashMap;
import java.util.Map;

import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.dto.ScoreInfoDto;
import com.kingnode.xsimple.service.mobile.ApplicationService;
import com.kingnode.xsimple.util.Users;
import com.kingnode.xsimple.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequestMapping(value="/api/v1/application") public class ApplicationRestController{
    private static Logger logger=LoggerFactory.getLogger(ApplicationRestController.class);
    @Autowired private ApplicationService applicationService;
    /**
     * 应用,版本,下载信息统一入口
     *
     * @param jsonparm 手机端传入参数
     *                 参数含义如下： {"xtype":"checkVersion","appkey":"应用标示"}
     *                 例如：        {"xtype":"checkVersion","appkey":"111111111123213"}
     */
    @RequestMapping(value="info", method=RequestMethod.POST) public Map applicationInfo(@RequestParam(value="jsonparm") String jsonparm){
        logger.info("应用,版本,下载信息统一入口,接受参数如下：---->jsonparm:"+jsonparm);
        Map map=new HashMap();
        try{
            if(Utils.isEmptyString(jsonparm)){
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"参数为空");
            }else{
                Map<String,String> jsonMap=JsonMapper.nonEmptyMapper().fromJson(jsonparm,Map.class);
                if(null!=jsonMap&&jsonMap.size()>0){
                    String xtype=jsonMap.containsKey("xtype")?jsonMap.get("xtype"):"";
                    if("appInfo".equals(xtype)){
                        map=appInfo(jsonMap);//获取应用详情
                    }else if("appList".equals(xtype)){
                        map=appList(jsonMap);//获取更多应用,除了当前应用，且应用状态为可用
                    }else if("versionInfo".equals(xtype)){
                        map=versionInfo(jsonMap);//获取软件信息
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
            logger.info("应用,版本,下载信息统一入口,返回信息如下：---->:"+map);
            return map;
        }
    }
    /**
     * app评论信息
     *
     * @param scoreInfoDto 传入评论参数
     *
     * @return 是否评论成功
     */
    @RequestMapping(value="score", method={RequestMethod.POST}) public RestStatus appScore(ScoreInfoDto scoreInfoDto){
        return applicationService.AppScore(scoreInfoDto,Users.id(),"1");
    }
    /**
     * 检测app评论信息是否需要弹出
     *
     * @param scoreInfoDto 传入检测是否弹出评论参数
     *
     * @return 是否评论成功
     */
    @RequestMapping(value="popup", method={RequestMethod.POST}) public RestStatus ejectAppScore(ScoreInfoDto scoreInfoDto){
        return applicationService.AppScore(scoreInfoDto,Users.id(),"2");
    }
    /**
     * 获取软件信息
     *
     * @param jsonMap 手机端传入参数
     *                参数含义如下： {"xtype":"versionInfo","appkey":"应用标示","type":"版本运行平台"}
     *                例如：        {"xtype":"versionInfo","appkey":"111111111123213","type":"android"}
     *
     * @return
     */
    private Map versionInfo(Map<String,String> jsonMap){
        Map map=new HashMap();
        try{
            map=checkInfo(jsonMap,"3");
            if(Setting.SUCCESSSTAT.equals(map.get(Setting.RESULTCODE))){
                map=applicationService.VersionInfo(jsonMap);
            }
        }catch(Exception e){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return map;
    }
    /**
     * 获取更多应用,除了当前应用，且应用状态为可用
     *
     * @param jsonMap 手机端传入参数
     *                参数含义如下： {"xtype":"appList","appkey":"应用标示","appStat":"应用状态"}
     *                例如：        {"xtype":"appList","appkey":"111111111123213","appStat":"usable"}
     *
     * @return
     */
    private Map appList(Map<String,String> jsonMap){
        Map<String,String> map=new HashMap();
        try{
            map=checkInfo(jsonMap,"2");
            if(Setting.SUCCESSSTAT.equals(map.get(Setting.RESULTCODE))){
                map=applicationService.AppList(jsonMap);
            }
        }catch(Exception e){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return map;
    }
    /**
     * 获取应用详情
     *
     * @param jsonMap 手机端传入参数
     *                参数含义如下： {"xtype":"appInfo","appkey":"应用标示"}
     *                例如：        {"xtype":"appInfo","appkey":"111111111123213"}
     *
     * @return 返回应用描述信息
     */
    private Map appInfo(Map<String,String> jsonMap){
        Map<String,String> map=new HashMap();
        try{
            map=checkInfo(jsonMap,"1");
            if(Setting.SUCCESSSTAT.equals(map.get(Setting.RESULTCODE))){
                // 具体注册信息begin
                map=applicationService.AppInfo(jsonMap);
                // 具体注册信息begin
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
            String appkey=jsonMap.containsKey("appkey")?jsonMap.get("appkey"):""; //应用的标示
            String type=jsonMap.containsKey("type")?jsonMap.get("type"):""; //运行平台
            String versionId=jsonMap.containsKey("versionId")?jsonMap.get("versionId"):""; //版本ID
            if("1".equals(tip)||"2".equals(tip)||"3".equals(tip)){
                if(Utils.isEmptyString(appkey)){
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"应用的标示为空");
                    return map;
                }
            }
            if("3".equals(tip)){
                if(Utils.isEmptyString(type)){
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"运行平台为空");
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
