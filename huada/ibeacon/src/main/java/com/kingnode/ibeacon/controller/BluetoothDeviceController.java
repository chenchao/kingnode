package com.kingnode.ibeacon.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;

import com.kingnode.diva.web.Servlets;
import com.kingnode.ibeacon.service.BluetoothService;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.ibeacon.entity.KnBluetoothChannel;
import com.kingnode.xsimple.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
/**
 * 蓝牙设备信息控制器
 *
 * @author dengfeng@kingnode.com (dengfeng)
 */
@Controller @RequestMapping(value="/ibeacon/bluetooth")
public class BluetoothDeviceController{
    @Autowired
    private BluetoothService bs;
    @RequestMapping(method=RequestMethod.GET)
    public String list(Model model){
        model.addAttribute("channelType",Setting.ChannelType.values());
        model.addAttribute("channelStatus",Setting.ChannelStatusType.values());
        return "ibeacon/bluetoothList";
    }
    @RequestMapping(value="list") @ResponseBody
    public DataTable searchList(DataTable<KnBluetoothChannel> dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartWith(request,"search_");
        String channelId=null; //设备号  UUID 36位
        String major=null; //主id
        String minor=null; //辅id
        String channelName=null; //设备名
        String channelType=null; //设备类型   电池 ,交流电 与 ENUM枚举  ChannelType  对应
        String channelStatus=null; // 状态   可用, 禁用  与 ENUM枚举 ChannelStatusType 对应
        String channelInfo=null; //设备信息  中文描述
        for(String key : searchParams.keySet()){
            if(key.indexOf("channelId")>0){
                channelId=(String)searchParams.get(key);
            }
            if(key.indexOf("major")>0){
                major=(String)searchParams.get(key);
            }
            if(key.indexOf("minor")>0){
                minor=(String)searchParams.get(key);
            }
            if(key.indexOf("channelName")>0){
                channelName=(String)searchParams.get(key);
            }
            if(key.indexOf("channelType")>0){
                channelType=(String)searchParams.get(key);
            }
            if(key.indexOf("channelStatus")>0){
                channelStatus=(String)searchParams.get(key);
            }
            if(key.indexOf("channelInfo")>0){
                channelInfo=(String)searchParams.get(key);
            }
        }
        return bs.PageKnBluetoothChannel(channelId,major,minor,channelName,channelType,channelStatus,channelInfo,dt);
    }
    @RequestMapping(value="create", method=RequestMethod.GET)
    public String createForm(Model model){
        model.addAttribute("channelType",Setting.ChannelType.values());
        model.addAttribute("channelStatus",Setting.ChannelStatusType.values());
        model.addAttribute("action","add");
        return "device/bluetoothForm";
    }
    /**
     * 新增或编辑蓝牙信息
     *
     * @param redirectAttributes
     * @param ka
     *
     * @return
     */
    @RequestMapping(value={"/create"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String create(KnBluetoothChannel ka,RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("message",bs.SaveKnBluetoothChannel(ka));
        return "redirect:/ibeacon/bluetooth";
    }
    @RequestMapping(value="update/{id}", method=RequestMethod.GET)
    public String updateFrom(@PathVariable("id") Long id,Model model){
        model.addAttribute("bluetooth",bs.ReadBluetoothInfo(id));
        model.addAttribute("channelType",Setting.ChannelType.values());
        model.addAttribute("channelStatus",Setting.ChannelStatusType.values());
        model.addAttribute("action","update");
        return "device/bluetoothForm";
    }
    /**
     * 新增或编辑蓝牙信息
     *
     * @param redirectAttributes
     * @param ka                 编辑操作时蓝牙数据的id
     *
     * @return
     */
    @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String saveEdit(@ModelAttribute("kbc") KnBluetoothChannel ka,RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("message",bs.SaveKnBluetoothChannel(ka));
        return "redirect:/ibeacon/bluetooth";
    }
    @RequestMapping(value="delete", method=RequestMethod.POST) @ResponseBody
    public Map delete(@RequestParam("ids") List<Long> ids){
        Map map=new HashMap();
        try{
            bs.DeleteKnBluetoothChannel(ids);
            map.put("stat","true");
        }catch(Exception e){
            map.put("stat","false");
        }
        return map;
    }
    @ModelAttribute
    public void ReadKnArticleCategory(@RequestParam(value="id", defaultValue="-1") Long id,Model model){
        if(id!=-1){
            model.addAttribute("kbc",bs.ReadBluetoothInfo(id));
        }
    }
}
