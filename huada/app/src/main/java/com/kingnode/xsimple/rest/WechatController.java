package com.kingnode.xsimple.rest;
import com.kingnode.xsimple.service.mobile.WechatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@RestController @RequestMapping({"/api/v1/weixin"})
public class WechatController{
    @Autowired
    private WechatService wechatService;
    /**
     * *****************
     * 根据微信账号weixinId进行取消绑定操作
     *
     * @param jsonparm 参数格式示例：{"jsonparm":"{\"weixinId\":\"eefefd232\"}"}
     *
     * @return
     */
    @RequestMapping(value="unbound-user",method={RequestMethod.POST})
    public String unBoundToUser(@RequestParam(value="jsonparm") String jsonparm){
        return wechatService.UnBoundToUser(jsonparm);
    }
}
