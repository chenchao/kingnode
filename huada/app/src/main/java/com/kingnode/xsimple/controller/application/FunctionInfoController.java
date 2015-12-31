package com.kingnode.xsimple.controller.application;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.validation.Valid;

import com.google.common.base.Strings;
import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.entity.application.KnApplicationInfo;
import com.kingnode.xsimple.entity.system.KnResource;
import com.kingnode.xsimple.service.application.ApplicationInfoService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
/**
 * 功能管理
 *
 * @author 孔江伟
 */
@Controller @RequestMapping(value="/application/function")
public class FunctionInfoController{
    private static Logger logger=LoggerFactory.getLogger(FunctionInfoController.class);
    @Autowired
    private ApplicationInfoService applicationInfoService;
    @RequiresPermissions("application-function") @RequestMapping(method=RequestMethod.GET)
    public String list(Model model){
        List<KnApplicationInfo> listData=applicationInfoService.GetAllApp();
        model.addAttribute("listApp",listData);
        return "application/functionList";
    }
    /**
     * 进入功能编辑页面
     *
     * @param model
     *
     * @return
     */
    @RequiresPermissions("application-function") @RequestMapping(value="create-func", method=RequestMethod.GET)
    public String createFunc(Model model){
        KnResource role=new KnResource();
        model.addAttribute("function",role);
        model.addAttribute("action","create");
        return "application/functionForm";
    }
    @RequiresPermissions("application-function") @RequestMapping(value="update-func/{id}", method=RequestMethod.GET)
    public String updateFunc(@PathVariable("id") Long id,Model model){
        model.addAttribute("function",applicationInfoService.ReadResourceInfo(id));
        model.addAttribute("action","update");
        return "application/functionForm";
    }
    @RequiresPermissions("application-function") @RequestMapping(value="save-func",method=RequestMethod.POST)
    public String saveFunc(@Valid KnResource kr,RedirectAttributes redirectAttributes){
        KnResource role=applicationInfoService.SaveFunc(kr);
        if(role==null){
            redirectAttributes.addFlashAttribute("stat","false");
            redirectAttributes.addFlashAttribute("message","保存失败");
        }else{
            redirectAttributes.addFlashAttribute("stat","true");
            redirectAttributes.addFlashAttribute("message","保存成功");
        }
        return "redirect:/application/function";
    }
    @RequiresPermissions("application-function") @RequestMapping(value="delete-func", method=RequestMethod.POST) @ResponseBody
    public String deleteFunc(@RequestParam("id") Long id){
        applicationInfoService.DeleteResourceById(id);
        return "true";
    }
    @RequiresPermissions("application-function") @RequestMapping(value="delete-all-func",method=RequestMethod.POST) @ResponseBody
    public String deleteAllFunc(@RequestParam("ids") List<Long> ids){
        applicationInfoService.DeleteAllResourceByIds(ids);
        return "true";
    }
    @RequiresPermissions("application-function") @RequestMapping(value="func-list",method=RequestMethod.POST) @ResponseBody
    public DataTable<KnResource> funcList(@RequestParam(value="appId",required=false) Long appId,DataTable<KnResource> dt,ServletRequest request,@RequestParam(value="beginTime",required=false) String beginTime,@RequestParam(value="endTime",required=false) String endTime){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        Long begin=Strings.isNullOrEmpty(beginTime)?null:DateTime.parse(beginTime,DateTimeFormat.forPattern("yyyy-MM-dd")).getMillis();
        Long end=Strings.isNullOrEmpty(endTime)?null:DateTime.parse(endTime,DateTimeFormat.forPattern("yyyy-MM-dd")).getMillis();
        return applicationInfoService.FuncList(dt,appId,searchParams,begin,end);
    }
    @RequiresPermissions("application-function") @RequestMapping(value="find-module-by-role-id",method=RequestMethod.POST) @ResponseBody
    public List<KnResource> findModuleByRoleId(@RequestParam(value="roleId",required=true) Long roleId){
        return applicationInfoService.FindModuleByRoleId(roleId);
    }
    @RequiresPermissions("application-function") @RequestMapping(value="find-one-resource",method=RequestMethod.POST) @ResponseBody
    public KnResource findOneResource(@RequestParam(value="id",required=true) Long id){
        return applicationInfoService.ReadResourceInfo(id);
    }
    @RequiresPermissions("application-function") @RequestMapping(value="save-func-to-rmf",method=RequestMethod.POST) @ResponseBody
    public Map saveFuncToRMF(@RequestParam("roleId") Long roleId,@RequestParam("mids") List<Long> mids,@RequestParam("fids") List<Long> fids){
        return applicationInfoService.SaveFuncToRMF(roleId,mids,fids);
    }
    @RequiresPermissions("application-function") @RequestMapping(value="update-func-in-rmf",method=RequestMethod.POST) @ResponseBody
    public Map updateFuncInRMF(@RequestParam("roleId") Long roleId,@RequestParam("moduleId") Long moduleId,@RequestParam("functionId") Long functionId,@RequestParam("oldRoleId") Long oldRoleId,@RequestParam("oldModuleId") Long oldModuleId){
        return applicationInfoService.UpdateFuncInRMF(roleId,moduleId,functionId,oldRoleId,oldModuleId);
    }
    @RequiresPermissions("application-function") @RequestMapping(value="delete-rmf-info-by-rid-and-fid",method=RequestMethod.POST) @ResponseBody
    public Map deleteRMFInfoByRidAndFid(@RequestParam("roleId") Long roleId,@RequestParam("functionId") Long functionId){
        return applicationInfoService.DeleteRMFInfoByRidAndFid(roleId,functionId);
    }
    @RequiresPermissions("application-function") @RequestMapping(value="find-all-function-infs",method=RequestMethod.POST) @ResponseBody
    public Page<KnResource> findAllFunctionInfs(@RequestParam("currentPage") int currentPage,@RequestParam("numPerPage") int numPerPage,@RequestParam("name") String name){
        return applicationInfoService.FindAllFunctionInfs(currentPage,numPerPage,name);
    }
    @RequiresPermissions("application-function") @RequestMapping(value="find-setup-package-by-ids",method=RequestMethod.POST) @ResponseBody
    public List<KnResource> findSetUpPackageByIds(@RequestParam("ids") List<Long> ids){
        return applicationInfoService.FindSetUpPackageByIds(ids);
    }
    @RequiresPermissions("application-function") @RequestMapping(value="find-app-by-func",method=RequestMethod.POST) @ResponseBody
    public List<KnApplicationInfo> findAppByFunc(@RequestParam("functionId") Long functionId){
        return applicationInfoService.FindAppByFunc(functionId);
    }
    /**
     * *********
     * 将安装包保存到应用中去
     *
     * @param funcIds
     * @param appIds
     *
     * @return
     */
    @RequiresPermissions("application-function") @RequestMapping(value="save-setup-package-to-app", method=RequestMethod.POST) @ResponseBody
    public Map saveSetUpPackageToApp(@RequestParam("funcIds") List<Long> funcIds,@RequestParam("appIds") List<Long> appIds){
        return applicationInfoService.SaveSetUpPackageToApp(funcIds,appIds);
    }
    /**
     * 解除功能应用的关系
     *
     * @param functionId
     * @param appId
     *
     * @return
     */
    @RequiresPermissions("application-function") @RequestMapping(value="delete-setup-package-from-app",method=RequestMethod.POST) @ResponseBody
    public Map deleteSetupPackageFromApp(@RequestParam("functionId") Long functionId,@RequestParam("appId") Long appId){
        return applicationInfoService.DeleteSetupPackageFromApp(functionId,appId);
    }
    /**
     * 通过角色编号查询角色下的功能信息
     *
     * @param roleId
     *
     * @return
     */
    @RequiresPermissions("application-function") @RequestMapping(value="find-func-by-role-id",method=RequestMethod.POST) @ResponseBody
    public List<KnResource> findFuncByRoleId(@RequestParam("roleId") Long roleId){
        return applicationInfoService.FindFuncByRoleId(roleId);
    }
    /**
     * 通过角色编号查询角色下的功能并通过模块分组
     *
     * @param roleId
     *
     * @return
     */
    @RequiresPermissions("application-function") @RequestMapping(value="find-func-by-role-id-group-by-module",method=RequestMethod.POST) @ResponseBody
    public List findFuncByRoleIdGroupByModule(@RequestParam("roleId") Long roleId){
        return applicationInfoService.FindFuncByRoleIdGroupByModule(roleId);
    }
}
