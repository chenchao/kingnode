package com.kingnode.third.controller;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;

import com.kingnode.third.entity.KnUserSetupInfo;
import com.kingnode.third.service.SysUserSetupInfoService;
import com.kingnode.xsimple.api.common.DataTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
/**
 * @author kongjiagwei
 */
@Controller @RequestMapping(value="/authority/system-third")
public class SysUserSetupInfoController{
    @Autowired
    private SysUserSetupInfoService sysUserSetupInfoService;
    @RequestMapping(method=RequestMethod.GET)
    public String home(Model model){
        return "third/systemList";
    }
    @RequestMapping(value="create-sys", method=RequestMethod.GET)
    public String createSys(Model model){
        KnUserSetupInfo info=new KnUserSetupInfo();
        model.addAttribute("userSetup",info);
        model.addAttribute("action","create");
        return "third/systemUserSetUpForm";
    }
    @RequestMapping(value="update-sys/{id}", method=RequestMethod.GET)
    public String updateSys(@PathVariable("id") Long id,Model model){
        KnUserSetupInfo info=sysUserSetupInfoService.FindOneById(id);
        model.addAttribute("userSetup",info);
        model.addAttribute("action","update");
        return "third/systemUserSetUpForm";
    }
    @RequestMapping(value="save", method=RequestMethod.POST)
    public String save(@Valid KnUserSetupInfo kr,RedirectAttributes redirectAttributes){
        KnUserSetupInfo knUserSetupInfo=sysUserSetupInfoService.Save(kr);
        if(knUserSetupInfo==null){
            redirectAttributes.addFlashAttribute("stat","false");
            redirectAttributes.addFlashAttribute("message","保存失败");
        }else{
            redirectAttributes.addFlashAttribute("stat","true");
            redirectAttributes.addFlashAttribute("message","保存成功");
        }
        return "redirect:/authority/system-third";
    }
    @RequestMapping(value="sys-user-setup-list", method=RequestMethod.POST) @ResponseBody
    public DataTable<KnUserSetupInfo> sysUserSetUpList(DataTable<KnUserSetupInfo> dt){
        return sysUserSetupInfoService.SysUserSetUpList(dt);
    }
    @RequestMapping(value="delete-sys", method=RequestMethod.POST) @ResponseBody
    public String deleteSys(@RequestParam("id") Long id){
        sysUserSetupInfoService.DeleteSys(id);
        return "true";
    }
    @RequestMapping(value="delete-all-sys", method=RequestMethod.POST) @ResponseBody
    public String deleteAllSys(@RequestParam("ids") List<Long> ids){
        sysUserSetupInfoService.DeleteAllSys(ids);
        return "true";
    }
    @RequestMapping(value="add-to-user-info", method=RequestMethod.POST) @ResponseBody
    public Map addToUserInfo(@RequestParam("ids") List<Long> ids){
        return sysUserSetupInfoService.AddToUserInfo(ids);
    }
    @RequestMapping(value="contact-user", method=RequestMethod.POST) @ResponseBody
    public Map contactUser(@RequestParam("fromSys1") String fromSys1,@RequestParam("fromSys2") String fromSys2,@RequestParam("contact1") String contact1,@RequestParam("contact2") String contact2){
        return sysUserSetupInfoService.ContactUser(fromSys1,fromSys2,contact1,contact2);
    }
}
