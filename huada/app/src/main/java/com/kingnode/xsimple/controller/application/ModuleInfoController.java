package com.kingnode.xsimple.controller.application;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;

import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.entity.system.KnResource;
import com.kingnode.xsimple.entity.system.KnRole;
import com.kingnode.xsimple.entity.application.KnApplicationInfo;
import com.kingnode.xsimple.service.application.ApplicationInfoService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller @RequestMapping(value="/application/module")
public class ModuleInfoController{
    @Autowired
    private ApplicationInfoService applicationInfoService;

    @RequiresPermissions("application-module")  @RequestMapping(method=RequestMethod.GET)
    public String list(Model model){
        List<KnApplicationInfo> listData=applicationInfoService.GetAllApp();
        model.addAttribute("listApp",listData);
        return "application/moduleList";
    }
    /**
     * 进入模块编辑页面
     *
     * @param model
     *
     * @return
     */
    @RequiresPermissions("application-module")  @RequestMapping(value="create",method=RequestMethod.GET)
    public String create(Model model){
        KnResource module=new KnResource();
        model.addAttribute("module",module);
        model.addAttribute("action","create");
        return "application/moduleForm";
    }
    /**
     * 跳转更新模块页面
     * @param id 主键id
     * @param model
     * @return
     */
    @RequiresPermissions("application-module")
    @RequestMapping(value="update/{id}",method=RequestMethod.GET)
    public String update(@PathVariable("id") Long id,Model model){
        model.addAttribute("module",applicationInfoService.ReadResourceInfo(id));
        model.addAttribute("action","update");
        return "application/moduleForm";
    }
    /**
     *  保存模块信息
     * @param kr 模块对象
     * @param redirectAttributes
     * @return
     */
    @RequiresPermissions("application-module")
    @RequestMapping(value="save",method=RequestMethod.POST)
    public String save(@Valid KnResource kr,RedirectAttributes redirectAttributes){
        KnResource resource=applicationInfoService.SaveModule(kr);
        if(resource==null){
            redirectAttributes.addFlashAttribute("stat","false");
            redirectAttributes.addFlashAttribute("message","保存失败");
        }else{
            redirectAttributes.addFlashAttribute("stat","true");
            redirectAttributes.addFlashAttribute("message","保存成功");
        }
        return "redirect:/application/module";
    }
    /**
     * 根据主键id删除模块信息
     * @param id 主键id
     * @return
     */
    @RequiresPermissions("application-module")
    @RequestMapping(value="delete",method=RequestMethod.POST) @ResponseBody
    public String delete(@RequestParam("id") Long id){
        applicationInfoService.DeleteResourceById(id);
        return "true";
    }
    /**
     * 批量删除模块信息
     * @param ids 主键id集合
     * @return
     */
    @RequiresPermissions("application-module")
    @RequestMapping(value="delete-all",method=RequestMethod.POST) @ResponseBody
    public String deleteAll(@RequestParam("ids") List<Long> ids){
        applicationInfoService.DeleteAllResourceByIds(ids);
        return "true";
    }
    /**
     * 根据应用ID查询模块信息
     * @param appId 应用主键id
     * @param dt
     * @return
     */
    @RequiresPermissions("application-module")
    @RequestMapping(value="list",method=RequestMethod.POST) @ResponseBody
    public DataTable<KnResource> list(@RequestParam(value="appId",required=false) Long appId,DataTable<KnResource> dt){
        return applicationInfoService.ModuleList(dt,appId);
    }
    /**
     * 根据应用id查询表格角色信息
     * @param appId 应用主键id
     * @param dt 表格对象
     * @return 表格对象
     */
    @RequiresPermissions("application-module")
    @RequestMapping(value="find-role-by-aid",method=RequestMethod.POST) @ResponseBody
    public DataTable<KnRole> findRoleByAid(@RequestParam(value="appId",required=false) Long appId,DataTable<KnRole> dt){
        return applicationInfoService.FindRoleByAid(dt,appId);
    }
    /**
     * 根据应用id查询角色信息
     * @param appId 应用主键id
     * @return 角色集合
     */
    @RequiresPermissions("application-module")
    @RequestMapping(value="find-role-by-app-id",method=RequestMethod.POST) @ResponseBody
    public List<KnRole> findRoleByAppId(@RequestParam(value="appId",required=true) Long appId){
        return applicationInfoService.FindRoleByAppId(appId);
    }
    /**
     * 根据角色和模块的id集合保存中间关系
     * @param mids 模块主键集合
     * @param rids 角色主键集合
     * @return
     */
    @RequiresPermissions("application-module")
    @RequestMapping(value="save-to-role",method=RequestMethod.POST) @ResponseBody
    public Map saveToRole(@RequestParam("mids") List<Long> mids,@RequestParam("rids") List<Long> rids){
        return applicationInfoService.SaveModuleToRole(mids,rids);
    }
    /**
     * 通过模块ID获取角色信息
     *
     * @param mid 模块的主键id
     *
     * @return 角色集合
     */
    @RequiresPermissions("application-module")
    @RequestMapping(value="find-role-by-module",method=RequestMethod.POST) @ResponseBody
    public List<KnRole> findRoleByModule(@RequestParam("mid") Long mid){
        return applicationInfoService.FindRoleByModule(mid);
    }
    /**
     * 通过角色ID与模块ID解除角色与模块之间的关系
     *
     * @param rid 角色主键id
     * @param mid 模块主键id
     *
     * @return
     */
    @RequiresPermissions("application-module")
    @RequestMapping(value="delete-role",method=RequestMethod.POST) @ResponseBody
    public Map deleteRole(@RequestParam("rid") Long rid,@RequestParam("mid") Long mid){
        return applicationInfoService.DeleteModuleFromRole(rid,mid);
    }
    /**
     * 通过角色ID与模块ID更新角色信息
     *
     * @param rid 角色主键id
     * @param mid 模块主键id
     * @param oldrId 旧的主键id
     *
     * @return
     */
    @RequiresPermissions("application-module")
    @RequestMapping(value="update-role",method=RequestMethod.POST) @ResponseBody
    public Map updateRole(@RequestParam("rid") Long rid,@RequestParam("mid") Long mid,@RequestParam("oldrId") Long oldrId){
        return applicationInfoService.UpdateModuleInRole(rid,mid,oldrId);
    }
    /**
     * 根据模块和角色的主键id查询功能列表--拖拽页面使用
     * @param rid 角色的主键id
     * @param mid 模块的主键id
     * @return
     */
    @RequiresPermissions("application-role")
    @RequestMapping(value="find-func-by-mid",method=RequestMethod.POST) @ResponseBody
    public List<KnResource> findFuncByMid(@RequestParam("rid") Long rid,@RequestParam("mid") Long mid){
        return applicationInfoService.FindFuncByRidAndMid(rid,mid);
    }
    /**
     * 根据模块的主键id查询功能和功能下的zip版本信息
     * @param rid 角色主键id
     * @param mid 模块主键id
     * @return
     */
    @RequiresPermissions("application-module")
    @RequestMapping(value="find-func-and-func-version-by-mid",method=RequestMethod.POST) @ResponseBody
    public List findFuncAndFuncVersionByMid(@RequestParam("rid") Long rid,@RequestParam("mid") Long mid){
        return applicationInfoService.FindFuncAndFuncVersionByMid(rid,mid);
    }


    /**
     * 根据角色的id和功能的id删除角色所属的功能
     * @param rid 角色主键id
     * @param mid 模块主键id
     * @param fid 功能主键id
     * @return
     */
    @RequiresPermissions("application-module")
    @RequestMapping(value="delete-func-from-rmf",method=RequestMethod.POST) @ResponseBody
    public Map deleteFuncFromRMF(@RequestParam("rid") Long rid,@RequestParam("mid") Long mid,@RequestParam("fid") Long fid){
        return applicationInfoService.DeleteFuncFromRMF(rid,mid,fid);
    }
    /**
     * 根据角色id查询模块翻页列表信息数据
     * @param roleId 角色主键id
     * @param dt 表格对象
     * @return
     */
    @RequiresPermissions("application-module")
    @RequestMapping(value="find-page-module-by-role-id",method=RequestMethod.POST) @ResponseBody
    public DataTable<KnResource> findPageModuleByRoleId(@RequestParam(value="roleId",required=false) Long roleId,DataTable<KnResource> dt){
        return applicationInfoService.FindPageModuleByRoleId(roleId,dt);
    }
    /**
     * 根据角色和功能的主键id查询模块的信息
     * @param roleId 角色的主键id
     * @param functionId 功能的主键id
     * @return 模块集合
     */
    @RequiresPermissions("application-module")
    @RequestMapping(value="find-module-by-role-id-and-function-id",method=RequestMethod.POST) @ResponseBody
    public List<KnResource> findModuleByRoleIdAndFunctionId(@RequestParam(value="roleId",required=true) Long roleId,@RequestParam(value="functionId",required=true) Long functionId){
        return applicationInfoService.FindModuleByRoleIdAndFunctionId(roleId,functionId);
    }
    /**
     * 拖拽角色模块功能页面使用的根据模块查询不在此角色下的资源信息
     * @param roleId 角色id
     * @param currentPage 翻页数据
     * @param numPerPage 翻页数据
     * @return
     */
    @RequiresPermissions("application-role")
    @RequestMapping(value="find-page-module-not-in-rid",method=RequestMethod.POST) @ResponseBody
    public Page<KnResource> findPageModuleNotInRid(@RequestParam("roleId") Long roleId,@RequestParam("currentPage") int currentPage,@RequestParam("numPerPage") int numPerPage){
        return applicationInfoService.FindPageModuleNotInRid(roleId,currentPage,numPerPage);
    }
}
