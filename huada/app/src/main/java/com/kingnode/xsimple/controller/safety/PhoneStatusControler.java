package com.kingnode.xsimple.controller.safety;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.ServletRequest;

import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.entity.push.KnDeviceInfo;
import com.kingnode.xsimple.service.safety.DeviceService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * 设备状态控制器
 * @author dengfeng@kingnode.com (dengfeng)
 */
@Controller
@RequestMapping(value="/safety/status")
public class PhoneStatusControler{

    @Autowired
    private DeviceService deviceService;
    /**
     * 跳转在线设备页面
     * @param model
     * @return
     */
    @RequiresPermissions("safety-status")
    @RequestMapping(method= RequestMethod.GET)
    public String list(Model model){
        model.addAttribute("messType" , Setting.MessageType.values() ) ; // 消息类别
        model.addAttribute("platType" , Setting.PlatformType.values() ) ;
        model.addAttribute("msgState" , Setting.MsgState.values() ) ; // 消息状态
        model.addAttribute("plateMess" , Setting.PlateformType.values() ) ; //消息类型
        model.addAttribute("delStatus" , Setting.DeleteStatusType.values() ) ;
        return "safety/phoneStatusList";
    }
    /**
     * 查询在线设备
     * @param dt
     * @param request
     * @return
     */
    @RequiresPermissions("safety-status")
    @RequestMapping(value="search-online-list") @ResponseBody
    public DataTable searchOnlineList(DataTable<KnDeviceInfo> dt ,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        try{
            return  deviceService.SearchOnlineList(dt , searchParams);
        }catch(Exception e){
            dt.setiTotalDisplayRecords(-2L);
            dt.setAaData(new ArrayList<KnDeviceInfo>());
            e.printStackTrace();
        }
        return dt;
    }

}
