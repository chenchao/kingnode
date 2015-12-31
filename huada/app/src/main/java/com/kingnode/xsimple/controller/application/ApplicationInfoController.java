package com.kingnode.xsimple.controller.application;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;

import com.google.common.base.Strings;
import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.entity.application.KnApplicationInfo;
import com.kingnode.xsimple.service.application.ApplicationInfoService;
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
 * 应用管理
 * wangyifan
 */
@Controller @RequestMapping(value="/application/list")
public class ApplicationInfoController{
    @Autowired
    private ApplicationInfoService applicationInfoService;
    private static Logger logger=LoggerFactory.getLogger(ApplicationInfoController.class);
    //获取应用信息
    @RequiresPermissions("application-list") @RequestMapping(value="list",method=RequestMethod.POST) @ResponseBody
    public DataTable<KnApplicationInfo> list(DataTable<KnApplicationInfo> dt){
        return applicationInfoService.GetAppList(dt);
    }
    /**
     * @param model 设置返回信息
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("application") @RequestMapping(method=RequestMethod.GET)
    public String home(Model model){
        return "/application/applyList";
    }
    /**
     * 跳转新增应用页面
     *
     * @param model 设置返回信息
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("application-list") @RequestMapping(value="create",method=RequestMethod.GET)
    public String create(Model model){
        KnApplicationInfo knAppInfo=new KnApplicationInfo();
        model.addAttribute("knAppInfo",knAppInfo);
        model.addAttribute("action","create");
        return "/application/applyForm";
    }
    /**
     * 应用管理列表首页
     *
     * @param appName 应用名称
     * @param dt      页面列表信息
     * @param request 获取页面请求参数
     *
     * @return 表格信息
     */
    @RequiresPermissions("application-list") @RequestMapping(value="apply-list",method=RequestMethod.POST) @ResponseBody
    public DataTable<KnApplicationInfo> applyList(@RequestParam(value="appName",required=false) String appName,DataTable<KnApplicationInfo> dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return applicationInfoService.ListOfKnApply(searchParams,dt,appName);
    }
    /**
     * 新增应用
     *
     * @param knAppInfo          应用实体信息
     * @param redirectAttributes 设置返回参数
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("application-list") @RequestMapping(value="create",method=RequestMethod.POST)
    public String save(KnApplicationInfo knAppInfo,RedirectAttributes redirectAttributes){
        try{
            Map<String,Object> map=applicationInfoService.SaveOrUpdateKnAppInfo(knAppInfo);
            boolean bool=(boolean)map.get("stat");
            if(bool){
                redirectAttributes.addFlashAttribute("stat",true);
                redirectAttributes.addFlashAttribute("message","保存成功");
            }else{
                redirectAttributes.addFlashAttribute("stat",false);
                redirectAttributes.addFlashAttribute("message",map.get("msg"));
            }
        }catch(Exception e){
            redirectAttributes.addFlashAttribute("stat",false);
            redirectAttributes.addFlashAttribute("message","保存失败");
        }
        return "redirect:/application/list";
    }
    /**
     * 打开更新应用页面
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("application-list") @RequestMapping(value="update/{id}",method=RequestMethod.GET)
    public String update(@PathVariable("id") Long id,Model model){
        KnApplicationInfo knApplicationInfo=applicationInfoService.FindKnAppInfoById(id);
        model.addAttribute("knAppInfo",knApplicationInfo);//查询应用信息
        model.addAttribute("action","update");//跳转编辑的标示
        return "/application/applyForm";
    }
    /**
     * 更新应用信息
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("application-list") @RequestMapping(value="update",method=RequestMethod.POST)
    public String update(KnApplicationInfo knApplicationInfo,RedirectAttributes redirectAttributes){
        try{
            Map<String,Object> map=applicationInfoService.SaveOrUpdateKnAppInfo(knApplicationInfo);
            boolean bool=(boolean)map.get("stat");
            if(bool){
                redirectAttributes.addFlashAttribute("stat",true);
                redirectAttributes.addFlashAttribute("message","更新成功");
            }else{
                redirectAttributes.addFlashAttribute("stat",false);
                redirectAttributes.addFlashAttribute("message",map.get("msg"));
            }
        }catch(Exception e){
            redirectAttributes.addFlashAttribute("stat",false);
            redirectAttributes.addFlashAttribute("message","更新失败");
        }
        return "redirect:/application/list";
    }
    /**
     * 单选删除应用信息
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("application-list") @RequestMapping(value="delete",method=RequestMethod.POST) @ResponseBody
    public Map<String,Boolean> delete(@RequestParam("id") Long id){
        Map<String,Boolean> map=new HashMap<String,Boolean>();
        try{
            applicationInfoService.DeleteKnAppInfoById(id);
            map.put("stat",true);
        }catch(Exception e){
            logger.error("删除应用错误信息：{}",e);
            map.put("stat",false);
        }
        return map;
    }
    /**
     * 全选删除应用信息
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("application-list") @RequestMapping(value="delete-all",method=RequestMethod.POST) @ResponseBody
    public Map<String,Boolean> deleteAll(@RequestParam("ids") List<Long> ids){
        Map<String,Boolean> map=new HashMap<String,Boolean>();
        try{
            applicationInfoService.DeleteAllKnAppInfoByIds(ids);
            map.put("stat",true);
        }catch(Exception e){
            map.put("stat",false);
            logger.error("删除应用错误信息：{}",e);
        }
        return map;
    }
    /**
     * 系统首页应用下载量信息
     *
     * @return 返回 下载信息
     */
    @RequestMapping(value="download-total",method=RequestMethod.POST) @ResponseBody
    public Map<String,Object> downloadTotal(){
        Map<String,Object> map=new HashMap();
        try{
            map=applicationInfoService.DownloadTotal();
        }catch(Exception e){
            map.put("stat",false);
            logger.error("系统首页应用下载量信息,错误信息：{}",e);
        }
        return map;
    }
    /**
     * 系统首页应用角色功能用户的下载量信息
     *
     * @return 返回 下载信息
     */
    @RequestMapping(value="get-num-list",method=RequestMethod.POST) @ResponseBody
    public Map<String,Object> getNumList(){
        Map<String,Object> map=new HashMap();
        try{
            map=applicationInfoService.GetNumList();
        }catch(Exception e){
            map.put("stat",false);
            logger.error("系统首页应用角色功能用户的下载量信息,错误信息：{}",e);
        }
        return map;
    }
    /**
     * 查看应用名称是否存在
     *
     * @param title 应用名称
     * @param id   应用id
     *
     * @return 是否存在 true 存在  false 不存在
     */
    @RequestMapping(value="check-name") @ResponseBody
    public Boolean checkName(@RequestParam(value="title") String title,@RequestParam(value="id") Long id){
        return applicationInfoService.CheckName(title,id);
    }
}
