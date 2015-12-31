package com.kingnode.xsimple.controller.safety;
import java.util.List;
import java.util.Map;

import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.entity.application.KnVersionInfo;
import com.kingnode.xsimple.service.safety.MessageService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * 消息发送控制器
 *
 * @author dengfeng@kingnode.com (dengfeng)
 */
@Controller @RequestMapping(value="/safety/message-send")
public class MessageSendControler{
    @Autowired
    private MessageService messageService;
    /**
     * 跳转消息发送
     *
     * @param model
     *
     * @return
     */
    @RequiresPermissions("safety-message-send") @RequestMapping(method=RequestMethod.GET)
    public String list(Model model){
        model.addAttribute("platType",Setting.PlatformType.values());
        model.addAttribute("delStatus",Setting.DeleteStatusType.values());
        model.addAttribute("messType",Setting.MessageType.values()); // 消息类别
        List<KnVersionInfo> knVersionsListInfo=messageService.FindVerListByWorkStats();
        model.addAttribute("knversionList",knVersionsListInfo);
        StringBuffer deviceBuf=messageService.FindDeviceByStats();
        model.addAttribute("deviceBuf",deviceBuf.toString());
        return "safety/messageSend";
    }
    /**
     * @param messageType  发送类型  systemmes("系统消息"), clearuser("清除用户"),cleardevice("清除设备"), onlineupdate("在线更新");
     * @param cliearDevice 清除设备时 所选中的设备值
     * @param appversion   在线更新时 对应的应用版本
     * @param broadcast    发送范围   U_L 在线用户(android/ios) ；U 指定用户  ； R 指定角色
     * @param channelId    指定用户发送范围时  所选中的设备id
     * @param userinfo     指定用户发送范围时  用户名
     * @param roleinfo     指定角色发送范围时  角色id
     * @param roleName     指定角色发送范围时  角色名
     * @param title        消息标题
     * @param uri          消息uri
     * @param message      消息正文内容
     */
    @RequiresPermissions("safety-message-send") @RequestMapping(value="send-message", method=RequestMethod.POST) @ResponseBody
    public Map<String,String> sendMessage(String messageType,String cliearDevice,String appversion,String broadcast,String channelId,String userinfo,String roleinfo,String roleName,String title,String uri,String message){
        return messageService.SendMessage(messageType,cliearDevice,appversion,broadcast,channelId,userinfo,roleinfo,roleName,title,uri,message);
    }
}
