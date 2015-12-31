package com.kingnode.xsimple.controller.application;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.validation.Valid;

import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.entity.application.KnApplicationInfo;
import com.kingnode.xsimple.entity.system.KnResource;
import com.kingnode.xsimple.entity.system.KnRole;
import com.kingnode.xsimple.entity.web.KnRoleApplicationInfo;
import com.kingnode.xsimple.service.application.RoleInfoService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
 * 角色管理列表
 * @author 孔江伟
 */
@Controller @RequestMapping(value="/application/role")
public class RoleInfoController{
    @Autowired
    private RoleInfoService roleInfoService;
    /**
     * 跳转角色列表
     *
     * @param model
     *
     * @return
     */
    @RequiresPermissions("application-role") @RequestMapping(method=RequestMethod.GET)
    public String list(Model model){
        List<KnApplicationInfo> listData=roleInfoService.GetAllApp();
        model.addAttribute("listData",listData);
        return "application/roleList";
    }
    /**
     * 进入角色编辑页面
     *
     * @param model
     *
     * @return
     */
    @RequiresPermissions("application-role") @RequestMapping(value="create", method=RequestMethod.GET)
    public String create(Model model){
        KnRole role=new KnRole();
        model.addAttribute("role",role);
        model.addAttribute("action","create");
        return "application/roleForm";
    }
    /**
     * 根据主键角色的主键id查询跳转布局管理页面
     *
     * @param id    角色主键id
     * @param model
     *
     * @return
     */
    @RequiresPermissions("application-role") @RequestMapping(value="to-drag-function/{id}", method=RequestMethod.GET)
    public String toDragFunction(@PathVariable("id") Long id,Model model){
        model.addAttribute("role",roleInfoService.ReadRoleInfo(id));
        return "application/dragFunction";
    }
    /**
     * 根据角色的主键id跳转角色的更新页面
     *
     * @param id    角色的主键id
     * @param model
     *
     * @return
     */
    @RequiresPermissions("application-role") @RequestMapping(value="update/{id}", method=RequestMethod.GET)
    public String update(@PathVariable("id") Long id,Model model){
        model.addAttribute("role",roleInfoService.ReadRoleInfo(id));
        model.addAttribute("action","update");
        return "application/roleForm";
    }
    /**
     * 保存角色信息
     *
     * @param kr                 角色对象
     * @param redirectAttributes
     *
     * @return
     */
    @RequiresPermissions("application-role") @RequestMapping(value="save", method=RequestMethod.POST)
    public String save(@Valid KnRole kr,RedirectAttributes redirectAttributes){
        KnRole role=roleInfoService.Save(kr);
        if(role==null){
            redirectAttributes.addFlashAttribute("stat","false");
            redirectAttributes.addFlashAttribute("message","保存失败");
        }else{
            redirectAttributes.addFlashAttribute("stat","true");
            redirectAttributes.addFlashAttribute("message","保存成功");
        }
        return "redirect:/application/role";
    }
    /**
     * 根据主键id删除角色信息
     *
     * @param id 角色的主键id
     *
     * @return
     */
    @RequiresPermissions("application-role") @RequestMapping(value="delete", method=RequestMethod.POST) @ResponseBody
    public String delete(@RequestParam("id") Long id){
        roleInfoService.Delete(id);
        return "true";
    }
    /**
     * 批量删除角色信息
     *
     * @param ids 角色主键id集合
     *
     * @return
     */
    @RequiresPermissions("application-role") @RequestMapping(value="delete-all", method=RequestMethod.POST) @ResponseBody
    public String deleteAll(@RequestParam("ids") List<Long> ids){
        roleInfoService.DeleteAll(ids);
        return "true";
    }
    /**
     * 根据角色的主键id集合和应用的主键id集合保存角色和应用的关系
     *
     * @param rids   角色主键id集合
     * @param appids 应用主键id集合
     *
     * @return
     */
    @RequiresPermissions("application-role") @RequestMapping(value="save-to-app", method=RequestMethod.POST) @ResponseBody
    public boolean saveToApp(@RequestParam("rids") List<Long> rids,@RequestParam("appIds") List<Long> appids){
        List<KnRoleApplicationInfo> list=roleInfoService.SaveToApp(rids,appids);
        if(list==null){
            return false;
        }else{
            return true;
        }
    }
    /**
     * 根据角色的主键id查询应用的信息
     *
     * @param id 角色的主键id
     *
     * @return
     */
    @RequiresPermissions("application-role") @RequestMapping(value="find-application-info-by-rid", method=RequestMethod.POST) @ResponseBody
    public List<KnApplicationInfo> findApplicationInfoByRid(@RequestParam("id") Long id){
        return roleInfoService.FindApplicationInfoByRid(id);
    }
    /**
     * 通过角色ID与模块ID获取功能信息
     *
     * @param rid 角色的主键id
     * @param mid 模块的主键id
     *
     * @return
     */
    @RequiresPermissions("application-role") @RequestMapping(value="find-function-by-mid", method=RequestMethod.POST) @ResponseBody
    public List<KnResource> findFunctionByMid(@RequestParam("rid") Long rid,@RequestParam("mid") Long mid){
        return roleInfoService.FindFunctionByMid(rid,mid);
    }
    /**
     * 通过功能获取角色信息
     *
     * @param functionId
     *
     * @return
     */
    @RequiresPermissions("application-role") @RequestMapping(value="find-role-by-func",method=RequestMethod.POST) @ResponseBody
    public List<KnRole> findRoleByFunc(@RequestParam("functionId") Long functionId){
        return roleInfoService.FindRoleByFunc(functionId);
    }
    /**
     * 根据应用的主键id查询角色表格信息
     *
     * @param appId   应用主键id
     * @param dt      表格对象
     * @param request
     *
     * @return
     */
    @RequiresPermissions("application-role") @RequestMapping(value="list", method=RequestMethod.POST) @ResponseBody
    public DataTable<KnRole> list(@RequestParam(value="appId", required=false) Long appId,DataTable<KnRole> dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return roleInfoService.GetRoleList(dt,appId,searchParams);
    }
    /**
     * 根据角色的id和应用的id更新角色所属的应用
     *
     * @return
     */
    @RequiresPermissions("application-role") @RequestMapping(value="update-app", method=RequestMethod.POST) @ResponseBody
    public Map updateApp(@RequestParam("rid") Long rid,@RequestParam("aid") Long aid,@RequestParam("oldId") Long oldId){
        return roleInfoService.UpdateRoleInApp(rid,aid,oldId);
    }
    /**
     * 根据角色的id和应用的id删除角色所属的应用
     *
     * @param rid
     * @param aid
     *
     * @return
     */
    @RequestMapping(value="delete-app", method=RequestMethod.POST) @ResponseBody
    public Map deleteApp(@RequestParam("rid") Long rid,@RequestParam("aid") Long aid){
        return roleInfoService.DeleteRoleFromApp(rid,aid);
    }
    /**
     * 根据角色的id和功能的id删除角色所属的功能
     *
     * @param rid
     * @param mid
     * @param fid
     *
     * @return
     */
    @RequiresPermissions("application-role") @RequestMapping(value="delete-func", method=RequestMethod.POST) @ResponseBody
    public Map deleteFunc(@RequestParam("rid") Long rid,@RequestParam("mid") Long mid,@RequestParam("fid") Long fid){
        return roleInfoService.DeleteFunction(rid,mid,fid);
    }
    /**
     * 保存图形界面布局管理
     *
     * @param rid       角色管理
     * @param arrMID    模块信息集合
     * @param arrMSort  模块在角色中的排序
     * @param arrAllFID 每个模块下的功能信息，二维数组
     * @param arrFSort  每个模块下的功能信息的排序，二维数组
     *
     * @return
     */
    @RequiresPermissions("application-role") @RequestMapping(value="drag-save",method=RequestMethod.POST) @ResponseBody
    public Map dragSave(@RequestParam("rid") Long rid,@RequestParam("arrMID") List<Long> arrMID,@RequestParam("arrMSort") List<Long> arrMSort,@RequestParam("arrAllFID") List<List<Long>> arrAllFID,@RequestParam("arrFSort") List<List<Long>> arrFSort){
        return roleInfoService.DragSave(rid,arrMID,arrMSort,arrAllFID,arrFSort);
    }
}
