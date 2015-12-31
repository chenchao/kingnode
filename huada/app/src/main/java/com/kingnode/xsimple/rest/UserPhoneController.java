package com.kingnode.xsimple.rest;
import java.util.Map;

import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.xsimple.service.mobile.UserPhoneService;
import com.kingnode.xsimple.util.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author kongjiangwei
 */
@RestController @RequestMapping({"/api/v1/user"}) public class UserPhoneController{
    @Autowired private UserPhoneService userPhoneService;
    /**
     * 登录
     *
     * @param jsonparm
     *
     * @return
     */
    @RequestMapping(value="login", method={RequestMethod.POST}) public String login(@RequestParam(value="jsonparm") String jsonparm){
        return userPhoneService.login(jsonparm);
    }
    /**
     * 获取用户信息,kim信息,将用户的信息同步到kim,云端访问地址
     *
     * @param jsonparm 前端手机请求json串,里面包含appkey信息
     *
     * @return
     */
    @RequestMapping(value="info", method={RequestMethod.POST}) public Map info(@RequestParam(value="jsonparm") String jsonparm){
        Map<String,String> jsonMap=JsonMapper.nonEmptyMapper().fromJson(jsonparm,Map.class);
        String appkey=jsonMap.containsKey("appkey")?jsonMap.get("appkey"):null;
        return userPhoneService.GetUserInfo(Users.id(),appkey);
    }
    /**
     * 获取用户的信息
     *
     * @return
     */
    @RequestMapping(value="detail", method={RequestMethod.POST}) public Map detail(String jsonparm){
        return userPhoneService.GetUserInfo(jsonparm);
    }
    /**
     * 更新用户信息
     *
     * @param jsonparm
     *
     * @return
     */
    @RequestMapping(value="update", method={RequestMethod.POST}) public Map update(@RequestParam(value="jsonparm") String jsonparm){
        return userPhoneService.UpdateUserInfo(jsonparm);
    }
}
