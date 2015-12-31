package com.kingnode.xsimple.rest;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Strings;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.service.mobile.UserPhoneService;
import com.kingnode.xsimple.service.system.ResourceService;
import com.kingnode.xsimple.util.Users;
import com.kingnode.xsimple.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@RestController @RequestMapping({"/api/v1/oauth"})
public class OauthRestController{
    private static Logger logger=LoggerFactory.getLogger(OauthRestController.class);
    @Autowired
    private ResourceService rs;
    @Autowired
    private UserPhoneService userPhoneService;
    /**
     * 二维码登陆
     *
     * @param loginCode
     *
     * @return
     */
    @RequestMapping(value="code",method=RequestMethod.GET)
    public RestStatus loginByQr(@RequestParam("code") String loginCode){
        String code="/Pcq3FqoXb25qQaAgc5ek2ru2GjcXFj/HHjmfVjft2R4vG8F2heaGZrZCALVJWegrTc/vyZmevyBVCfw6auH/dON9OyHge5PZ2thc/FPJFGIdQ+6Ape77w==";
        Long id=Users.id();
        RestStatus rs=new RestStatus(code.equals(loginCode));
        rs.setErrorCode(id.toString());
        rs.setErrorMessage(Users.name());
        return rs;
    }
    /**
     * 工作区
     * 根据模块功能的jsonparm获取用户下的登录用户的模块和功能信息
     *
     * @param jsonparm 前端手机请求json串,里面包含模块和功能的信息
     *
     * @return
     */
    @RequestMapping(value="workspace",method=RequestMethod.POST)
    public Map Workspace(@RequestParam(value="jsonparm") String jsonparm){
        Map jsonMap=JsonMapper.nonEmptyMapper().fromJson(jsonparm,Map.class);
        Map ups=userPhoneService.GetModeulFunctionByUserId(Users.id(),jsonMap,false);
        logger.info("{} return workspace",Users.name());
        return ups;
    }
    /**
     * 设备注册
     *
     * @return
     */
    @RequestMapping(value="push",method=RequestMethod.GET)
    public RestStatus push(){
        return new RestStatus(true);
    }
    @RequestMapping(value="change-pwd",method=RequestMethod.POST) @ResponseBody
    public Map changePassword(@RequestParam("jsonparm") String jsonparm){
        Map map=new HashMap();
        try{
            if(Utils.isEmptyString(jsonparm)){
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"参数为空");
            }else{
                Map<String,String> jsonMap=JsonMapper.nonEmptyMapper().fromJson(jsonparm,Map.class);
                if(null!=jsonMap&&jsonMap.size()>0){
                    if(!jsonMap.containsKey("oldPassword")||Strings.isNullOrEmpty(jsonMap.get("oldPassword"))){
                        map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                        map.put(Setting.MESSAGE,"参数错误，用户原始密码为空");
                    }else if(!jsonMap.containsKey("newPassword")||Strings.isNullOrEmpty(jsonMap.get("newPassword"))){
                        map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                        map.put(Setting.MESSAGE,"参数错误，用户新密码为空");
                    }else{
                        map=rs.ChangePassword(jsonMap.get("oldPassword"),jsonMap.get("newPassword"));
                    }
                }else{
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"参数格式错误");
                }
            }
        }catch(Exception ex){
            logger.error("changePassword:"+ex.getMessage());
        }finally{
            return map;
        }
    }
}