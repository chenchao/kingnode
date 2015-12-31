package com.kingnode.xsimple.controller.safety;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;

import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.entity.push.KnPushMessageInfo;
import com.kingnode.xsimple.service.safety.MessageService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * 消息查询 管理控制器
 *
 * @author dengfeng@kingnode.com (dengfeng)
 */
@Controller @RequestMapping(value="/safety/message-search")
public class MessageSearchControler{
    @Autowired
    private MessageService messageService;
    /**
     * 跳转消息查询列表页面
     *
     * @param model
     *
     * @return
     */
    @RequiresPermissions("safety-message-search") @RequestMapping(method=RequestMethod.GET)
    public String list(Model model){
        model.addAttribute("messType",Setting.MessageType.values()); // 消息类别
        model.addAttribute("msgState",Setting.MsgState.values()); // 消息状态
        model.addAttribute("plateMess",Setting.PlateformType.values()); //消息类型
        return "safety/messageList";
    }
    /**
     * 消息查询列表
     *
     * @param dt
     * @param request
     *
     * @return
     */
    @RequiresPermissions("safety-message-search") @RequestMapping(value="search-list") @ResponseBody
    public DataTable searchList(DataTable<KnPushMessageInfo> dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return messageService.SearchList(dt,searchParams);
    }
    /**
     * 删除发送的消息信息
     *
     * @param ids
     *
     * @return
     */
    @RequiresPermissions("safety-message-search") @RequestMapping(value="delete", method=RequestMethod.POST) @ResponseBody
    public Map delete(@RequestParam("ids") List<Long> ids){
        Map map=new HashMap();
        try{
            messageService.DeleteMessage(ids);
            map.put("stat",true);
        }catch(Exception e){
            map.put("stat",false);
        }
        return map;
    }
}
