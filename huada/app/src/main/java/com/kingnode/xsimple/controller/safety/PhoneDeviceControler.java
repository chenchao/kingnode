package com.kingnode.xsimple.controller.safety;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;

import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.entity.push.KnDeviceInfo;
import com.kingnode.xsimple.entity.push.KnPushMessageInfo;
import com.kingnode.xsimple.service.safety.DeviceService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * 设备管理信息控制器
 *
 * @author dengfeng@kingnode.com (dengfeng)
 */
@Controller @RequestMapping(value="/safety/channel")
public class PhoneDeviceControler{
    @Autowired
    private DeviceService deviceService;
    /**
     * 跳转手机设备管理列表
     *
     * @param model
     *
     * @return
     */
    @RequiresPermissions("safety-channel") @RequestMapping(method=RequestMethod.GET)
    public String list(Model model){
        model.addAttribute("platType",Setting.PlatformType.values());
        model.addAttribute("delStatus",Setting.DeleteStatusType.values());
        model.addAttribute("messType",Setting.MessageType.values()); // 消息类别
        model.addAttribute("msgState",Setting.MsgState.values()); // 消息状态
        model.addAttribute("plateMess",Setting.PlateformType.values()); //消息类型
        return "safety/phoneDeviceList";
    }
    /**
     * 查询手机设备管理列表
     *
     * @param dt
     * @param request
     *
     * @return
     */
    @RequiresPermissions("safety-channel") @RequestMapping(value="search-list") @ResponseBody
    public DataTable searchList(DataTable<KnDeviceInfo> dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return deviceService.SearchKnDeviceInfoList(dt,searchParams);
    }
    /**
     * 删除手机设备信息
     *
     * @param ids
     * @param delType
     *
     * @return
     */
    @RequiresPermissions("safety-channel") @RequestMapping(value="delete", method=RequestMethod.POST) @ResponseBody
    public Map delete(@RequestParam("ids") List<Long> ids,@RequestParam(value="delType", required=true) String delType){
        Map map=new HashMap();
        try{
            if("channel".equals(delType)){
                deviceService.Delete(ids);
            }else if("mess".equals(delType)){
                deviceService.DeleteMessage(ids);
            }
            map.put("stat",true);
        }catch(Exception e){
            map.put("stat",false);
        }
        return map;
    }
    /**
     * 取消用户擦除
     *
     * @param ids
     *
     * @return
     */
    @RequiresPermissions("safety-channel") @RequestMapping(value="cancel-del",method=RequestMethod.POST) @ResponseBody
    public Map cancelDel(@RequestParam("ids") List<Long> ids){
        Map map=new HashMap();
        try{
            deviceService.CancelDel(ids);
            map.put("stat","true");
        }catch(Exception e){
            map.put("stat","false");
        }
        return map;
    }
    /**
     * 查询设备用户的消息信息
     *
     * @param dt
     * @param deviceId
     * @param request
     *
     * @return
     */
    @RequiresPermissions("safety-channel") @RequestMapping(value="query-mess-list") @ResponseBody
    public DataTable queryMessList(DataTable<KnPushMessageInfo> dt,@RequestParam(value="deviceId", required=false) String deviceId,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return deviceService.QueryMessList(dt,searchParams,deviceId);
    }
}
