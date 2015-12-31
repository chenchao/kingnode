package com.kingnode.xsimple.controller.application;
import java.util.HashMap;
import java.util.Map;

import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.service.mobile.ApplicationService;
import com.kingnode.xsimple.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
/**
 * 下载页面Controlller
 *
 * @author cici
 */
@Controller @RequestMapping(value=Setting.appViewsUrl)
public class JspDownLoadController{
    private static Logger logger=LoggerFactory.getLogger(JspDownLoadController.class);
    @Autowired
    private ApplicationService applicationService;
    /**
     * @param model 跳转下载地址页面
     *
     * @return 返回跳转链接
     */
    @RequestMapping(value="{id}", method=RequestMethod.GET)
    public String update(@PathVariable("id") Long id,Model model){
        return Setting.appViewsUrl+"/"+id;
    }

    /**
     * 应用,版本,下载信息统一入口
     *
     * @param jsonparm 手机端传入参数
     *                 参数含义如下： {"xtype":"checkVersion","appkey":"应用标示"}
     *                 例如：        {"xtype":"checkVersion","appkey":"111111111123213"}
     */
    @RequestMapping(value="download",method=RequestMethod.POST)
    public Map applicationInfo(@RequestParam(value="jsonparm") String jsonparm){
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
                    if("downVersionInfo".equals(xtype)){
                        map=downVersionInfo(jsonMap);//下载软件信息
                    }else if("checkCodeNum".equals(xtype)){
                        map=checkCodeNum(jsonMap);//检测发送的验证码是否正确
                    }else if("sendCodeNum".equals(xtype)){
                        map=sendCodeNum(jsonMap);//发送验证码
                    }else if("checkCodeInfo".equals(xtype)){
                        map=checkCodeInfo(jsonMap);//检测手机以及验证码是否正确
                    }else if("checkUserInfo".equals(xtype)){
                        map=checkUserInfo(jsonMap);//检测用户名以及密码是否正确
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
     * 检测用户名以及密码是否正确
     *
     * @param jsonMap 手机端传入参数
     *                参数含义如下： {"xtype":"checkCodeInfo","username":"用户名","password":"密码"}
     *                例如：        {"xtype":"checkCodeInfo","username":"13649466667","password":"1232"}
     *
     * @return
     */
    private Map checkUserInfo(Map<String,String> jsonMap){
        Map map=new HashMap();
        try{
            map=checkInfo(jsonMap,"8");
            if(Setting.SUCCESSSTAT.equals(map.get(Setting.RESULTCODE))){
                map=applicationService.CheckUserInfo(jsonMap);
            }
        }catch(Exception e){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return map;
    }
    /**
     * 检测手机以及验证码是否正确
     *
     * @param jsonMap 手机端传入参数
     *                参数含义如下： {"xtype":"checkCodeInfo","phoneNum":"手机号码","codeNum":"验证码"}
     *                例如：        {"xtype":"checkCodeInfo","phoneNum":"13649466667","codeNum":"1232"}
     *
     * @return
     */
    private Map checkCodeInfo(Map<String,String> jsonMap){
        Map map=new HashMap();
        try{
            map=checkInfo(jsonMap,"7");
            if(Setting.SUCCESSSTAT.equals(map.get(Setting.RESULTCODE))){
                map=applicationService.CheckCodeInfo(jsonMap);
            }
        }catch(Exception e){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return map;
    }
    /**
     * 发送验证码
     *
     * @param jsonMap 手机端传入参数
     *                参数含义如下： {"xtype":"sendCodeNum","phoneNum":"手机号码"}
     *                例如：        {"xtype":"sendCodeNum","phoneNum":"13649466667"}
     *
     * @return
     */
    private Map sendCodeNum(Map<String,String> jsonMap){
        Map map=new HashMap();
        try{
            map=checkInfo(jsonMap,"6");
            if(Setting.SUCCESSSTAT.equals(map.get(Setting.RESULTCODE))){
                map=applicationService.SendCodeNum(jsonMap);
            }
        }catch(Exception e){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return map;
    }
    /**
     * 检测发送的验证码是否正确
     *
     * @param jsonMap 手机端传入参数
     *                参数含义如下： {"xtype":"checkCodeNum","codeNum":"验证码","downloadId":"下载版本id"}
     *                例如：        {"xtype":"checkCodeNum","codeNum":"9862","downloadId":"13649466667"}
     *
     * @return
     */
    private Map checkCodeNum(Map<String,String> jsonMap){
        Map map=new HashMap();
        try{
            map=checkInfo(jsonMap,"5");
            if(Setting.SUCCESSSTAT.equals(map.get(Setting.RESULTCODE))){
                map=applicationService.CheckCodeNum(jsonMap);
            }
        }catch(Exception e){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return map;
    }
    /**
     * 下载软件信息
     *
     * @param jsonMap 手机端传入参数
     *                参数含义如下： {"xtype":"downVersionInfo","channelStatus":"下载来源渠道","versionId":"应用版本的主键id","phoneNum":"手机号","email":"邮箱地址","companyName":"公司名称","uaccount":"账号","roleName":"角色名称"}
     *                例如：        {"xtype":"downVersionInfo","channelStatus":"weixin","versionId":"123123","phoneNum":"213123","email":"6464@qq.com","companyName":"深圳市xxxx","uaccount":"xiaoli","roleName":"管理员"}
     *
     * @return
     */
    private Map downVersionInfo(Map<String,String> jsonMap){
        Map map=new HashMap();
        try{
            map=checkInfo(jsonMap,"4");
            if(Setting.SUCCESSSTAT.equals(map.get(Setting.RESULTCODE))){
                map=applicationService.DownVersionInfo(jsonMap);
            }
        }catch(Exception e){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return map;
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
            String versionId=jsonMap.containsKey("versionId")?jsonMap.get("versionId"):""; //版本ID
            String codeNum=jsonMap.containsKey("codeNum")?jsonMap.get("codeNum"):""; //验证码
            String phoneNum=jsonMap.containsKey("phoneNum")?jsonMap.get("phoneNum"):""; //手机号码
            String username=jsonMap.containsKey("username")?jsonMap.get("username"):""; //用户名
            String password=jsonMap.containsKey("password")?jsonMap.get("password"):""; //密码
            String verify=jsonMap.containsKey("verify")?jsonMap.get("verify"):""; //邀请码
            if("4".equals(tip)||"5".equals(tip)){
                if(Utils.isEmptyString(versionId)){
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"版本ID为空");
                    return map;
                }
            }
            if("5".equals(tip)|| "7".equals(tip)){
                if(Utils.isEmptyString(codeNum)){
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"验证码为空");
                    return map;
                }
            }
            if("6".equals(tip)|| "7".equals(tip) || "9".equals(tip)){
                if(Utils.isEmptyString(phoneNum)){
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"手机号码为空");
                    return map;
                }
            }
            if("8".equals(tip)){
                if(Utils.isEmptyString(username)){
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"用户名为空");
                    return map;
                }
                if(Utils.isEmptyString(password)){
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"密码为空");
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
