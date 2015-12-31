package com.kingnode.xsimple.controller.system;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.validation.Valid;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.api.common.AjaxStatus;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.entity.system.KnRole;
import com.kingnode.xsimple.service.system.ResourceService;
import com.kingnode.xsimple.util.Utils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
 * 角色管理
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller @RequestMapping(value="/system/role")
public class RoleController{
    @Autowired
    private ResourceService rs;
    /**
     * 返回角色管理的页面的内容
     *
     * @return
     */
    @RequiresPermissions("system-role") @RequestMapping(method=RequestMethod.GET)
    public String index(){
        return "system/roleList";
    }
    /**
     * 对角色分页显示
     *
     * @param dt
     * @param request
     *
     * @return
     */
    @RequiresPermissions("system-role") @RequestMapping(value="list",method=RequestMethod.POST) @ResponseBody
    public DataTable<KnRole> list(DataTable<KnRole> dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return rs.PageKnRole(searchParams,dt);
    }
    /**
     * 进入角色编辑页面
     *
     * @param model
     *
     * @return
     */
    @RequiresPermissions("system-role") @RequestMapping(value="create",method=RequestMethod.GET)
    public String createForm(Model model){
        model.addAttribute("action","create");
        model.addAttribute("role",new KnRole());
        model.addAttribute("resourceTree",JsonMapper.nonEmptyMapper().toJson(rs.GetResourceTree()));
        return "system/roleForm";
    }
    /**
     * 保存角色
     *
     * @param role
     *
     * @return
     */
    @RequiresPermissions("system-role") @RequestMapping(value="create",method=RequestMethod.POST)
    public String create(@Valid KnRole role,@RequestParam(value="ids") String ids,RedirectAttributes redirectAttributes){
        List<Long> list=Lists.newArrayList();
        if(!Strings.isNullOrEmpty(ids)){
            for(String str : ids.split(",")){
                list.add(Long.valueOf(str));
            }
            list=Utils.removeDuplicate(list);
        }
        rs.SaveKnRole(role,list);
        redirectAttributes.addFlashAttribute("message","新建角色成功!");
        return "redirect:/system/role";
    }
    /**
     * 更新角色
     *
     * @param id
     *
     * @return
     */
    @RequiresPermissions("system-role") @RequestMapping(value="update/{id}")
    public String updateForm(@PathVariable("id") Long id,Model model){
        model.addAttribute("action","update");
        KnRole kr=rs.ReadKnRole(id);
        model.addAttribute("role",kr);
        model.addAttribute("resourceTree",JsonMapper.nonEmptyMapper().toJson(rs.GetResourceTreeHasSelect(kr)));
        return "system/roleForm";
    }
    /**
     * 更新角色
     *
     * @param role
     *
     * @return
     */
    @RequiresPermissions("system-role") @RequestMapping(value="update")
    public String update(@Valid @ModelAttribute("role") KnRole role,@RequestParam(value="ids") String ids,RedirectAttributes redirectAttributes){
        List<Long> list=Lists.newArrayList();
        if(!Strings.isNullOrEmpty(ids)){
            for(String str : ids.split(",")){
                list.add(Long.valueOf(str));
            }
            list=Utils.removeDuplicate(list);
        }
        rs.SaveKnRole(role,list);
        redirectAttributes.addFlashAttribute("message","更新角色成功");
        return "redirect:/system/role";
    }
    /**
     * 删除角色
     *
     * @param id
     *
     * @return
     */
    @RequiresPermissions("system-role") @RequestMapping(value="delete/{id}") @ResponseBody
    public AjaxStatus delete(@PathVariable("id") Long id){
        rs.DeleteKnRole(id);
        return new AjaxStatus(true,"删除角色成功");
    }
    @ModelAttribute
    public void ReadKnRole(@RequestParam(value="id",defaultValue="-1") Long id,Model model){
        if(id!=-1){
            model.addAttribute("role",rs.ReadKnRole(id));
        }
    }
}
