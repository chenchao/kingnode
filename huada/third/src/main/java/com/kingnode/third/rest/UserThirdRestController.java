package com.kingnode.third.rest;
import java.util.List;

import com.kingnode.third.service.UserThirdInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
/**
 * 第三方系统用户
 *
 * @author cici
 */
@RestController @RequestMapping({"/api/v1/user-third"})
public class UserThirdRestController{
    @Autowired
    private UserThirdInfoService service;
    /**
     * 获取用户的第三方系统信息
     *
     * @return
     */
    @RequestMapping(value="/detail", method={RequestMethod.GET})
    public List detail(String jsonparm){
        return service.FindKnUserThirdByLoginUser();
    }
}
