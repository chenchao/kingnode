package com.kingnode.xsimple.controller.application;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;

import com.google.common.base.Strings;
import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.entity.application.KnApplicationInfo;
import com.kingnode.xsimple.entity.application.KnApplicationSetupInfo;
import com.kingnode.xsimple.service.application.ApplicationSetupInfoService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * 应用设置管理
 * wangyifan
 */
@Controller @RequestMapping(value="/application/setup")
public class ApplicationSetUpController{
    private static Logger logger=LoggerFactory.getLogger(ApplicationSetUpController.class);
    @Autowired
    private ApplicationSetupInfoService applicationSetupInfoService;
    /**
     * @param model 设置返回信息
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("application-setup") @RequestMapping(method=RequestMethod.GET)
    public String home(Model model){
        List<KnApplicationInfo> listApp=applicationSetupInfoService.FindByTitleLike("%%");
        model.addAttribute("listApp",listApp);
        return "/application/applySetUpList";
    }
    /**
     * 跳转新增应用设置页面
     *
     * @param model 设置返回信息
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("application-setup") @RequestMapping(value="create",method=RequestMethod.GET)
    public String create(Model model){
        KnApplicationSetupInfo knObj=new KnApplicationSetupInfo();
        model.addAttribute("knObj",knObj);
        model.addAttribute("action","create");
        List<KnApplicationInfo> listApp=applicationSetupInfoService.FindByTitleLike("%%");
        model.addAttribute("listApp",listApp);
        return "/application/applySetUpForm";
    }
    /**
     * 应用设置管理列表首页
     *
     * @param appName   应用名字
     * @param dt      页面列表信息
     * @param request 获取页面请求参数
     *
     * @return 表格信息
     */
    @RequiresPermissions("application-setup") @RequestMapping(value="apply-setup-list",method=RequestMethod.POST) @ResponseBody
    public DataTable<KnApplicationSetupInfo> versionList(@RequestParam(value="appName",required=false) String appName,DataTable<KnApplicationSetupInfo> dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return applicationSetupInfoService.ListOfKnApplicationSetUp(searchParams,dt,appName);
    }
    /**
     * 新增应用设置
     *
     * @param /file                  上传文件信息
     * @param knApplicationSetupInfo 应用设置实体信息
     * @param //redirectAttributes   设置返回参数
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("application-setup") @RequestMapping(value={"/create"},method={RequestMethod.POST})
    public String save(KnApplicationSetupInfo knApplicationSetupInfo,RedirectAttributes redirectAttributes) throws IOException{
        try{
            Long appId=knApplicationSetupInfo.getApplicationInfo().getId();
            if(Strings.isNullOrEmpty(appId+"")){
                redirectAttributes.addFlashAttribute("message","应用不存在,新增失败");
                redirectAttributes.addFlashAttribute("stat",false);
                return "redirect:/application/setup";
            }
            KnApplicationInfo knApplicationInfo=applicationSetupInfoService.FindKnAppInfoById(appId);
            if(null==knApplicationInfo){
                redirectAttributes.addFlashAttribute("message","应用不存在,新增失败");
                redirectAttributes.addFlashAttribute("stat",false);
                return "redirect:/application/setup";
            }
            knApplicationSetupInfo.setApplicationInfo(knApplicationInfo);
            boolean bool=applicationSetupInfoService.SaveOrUpdateKnApplictionSetup(knApplicationSetupInfo);
            if(bool){
                redirectAttributes.addFlashAttribute("message","新增成功");
                redirectAttributes.addFlashAttribute("stat",true);
            }else{
                redirectAttributes.addFlashAttribute("stat",false);
                redirectAttributes.addFlashAttribute("message","新增失败");
            }
        }catch(Exception e){
            redirectAttributes.addFlashAttribute("stat",false);
            redirectAttributes.addFlashAttribute("message","新增失败");
            logger.error("新增应用设置错误信息 {}",e);
        }
        return "redirect:/application/setup";
    }
    /**
     * 打开更新应用设置页面
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("application-setup") @RequestMapping(value="update/{id}",method=RequestMethod.GET)
    public String update(@PathVariable("id") Long id,Model model){
        List<KnApplicationInfo> listApp=applicationSetupInfoService.FindByTitleLike("%%");
        model.addAttribute("listApp",listApp);
        KnApplicationSetupInfo knObj=applicationSetupInfoService.FindKnAppSetUpInfoById(id);
        model.addAttribute("knObj",knObj);//查询应用设置信息
        model.addAttribute("action","update");//跳转编辑的标示
        return "/application/applySetUpForm";
    }
    /**
     * 更新应用设置信息
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("application-setup") @RequestMapping(value={"/update"},method={RequestMethod.POST})
    public String update(KnApplicationSetupInfo knApplicationSetupInfo,RedirectAttributes redirectAttributes) throws IOException{
        try{
            Long appId=knApplicationSetupInfo.getApplicationInfo().getId();
            if(Strings.isNullOrEmpty(appId+"")){
                redirectAttributes.addFlashAttribute("message","应用不存在,更新失败");
                redirectAttributes.addFlashAttribute("stat",false);
                return "redirect:/application/setup";
            }
            KnApplicationInfo knApplicationInfo=applicationSetupInfoService.FindKnAppInfoById(appId);
            if(null==knApplicationInfo){
                redirectAttributes.addFlashAttribute("message","应用不存在,更新失败");
                redirectAttributes.addFlashAttribute("stat",false);
                return "redirect:/application/setup";
            }
            knApplicationSetupInfo.setApplicationInfo(knApplicationInfo);
            boolean bool=applicationSetupInfoService.SaveOrUpdateKnApplictionSetup(knApplicationSetupInfo);
            if(bool){
                redirectAttributes.addFlashAttribute("stat",true);
                redirectAttributes.addFlashAttribute("message","更新成功");
            }else{
                redirectAttributes.addFlashAttribute("stat",false);
                redirectAttributes.addFlashAttribute("message","更新失败");
            }
        }catch(Exception e){
            redirectAttributes.addFlashAttribute("stat",false);
            redirectAttributes.addFlashAttribute("message","更新失败");
            logger.error("更新设置错误信息 {}",e);
        }
        return "redirect:/application/setup";
    }
    /**
     * 单选删除应用设置信息
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("application-setup") @RequestMapping(value="delete",method=RequestMethod.POST) @ResponseBody
    public Map<String,Boolean> delete(@RequestParam("id") Long id){
        Map<String,Boolean> map=new HashMap<String,Boolean>();
        try{
            applicationSetupInfoService.DeleteKnVersionInfoById(id);
            map.put("stat",true);
        }catch(Exception e){
            logger.error("删除应用设置错误信息：{}",e);
            map.put("stat",false);
        }
        return map;
    }
    /**
     * 全选删除应用设置信息
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("application-setup") @RequestMapping(value="delete-all",method=RequestMethod.POST) @ResponseBody
    public Map<String,Boolean> deleteAll(@RequestParam("ids") List<Long> ids){
        Map<String,Boolean> map=new HashMap<String,Boolean>();
        try{
            applicationSetupInfoService.DeleteAllKnVserionInfoByIds(ids);
            map.put("stat",true);
        }catch(Exception e){
            map.put("stat",false);
            logger.error("删除应用设置错误信息：{}",e);
        }
        return map;
    }
}
