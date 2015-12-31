package com.kingnode.third.rest;
import java.util.Map;

import com.kingnode.third.service.ThirdSystemUserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * @author kongjiangwei
 */
@Controller @RequestMapping({"/authority/third-system","/setup/third-system"})
public class ThirdSystemUserRestController{
    @Autowired
    private ThirdSystemUserRestService thirdSystemUserRestService;

    @RequestMapping(value="user",method=RequestMethod.POST) @ResponseBody
    public Map getUserInfo(@RequestParam("jsonparm") String jsonparm){
        return thirdSystemUserRestService.getUserInfo(jsonparm);
    }
}
