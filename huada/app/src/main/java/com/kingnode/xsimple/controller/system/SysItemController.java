package com.kingnode.xsimple.controller.system;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.validation.Valid;

import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.api.common.AjaxStatus;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.entity.system.KnSysItem;
import com.kingnode.xsimple.service.system.ResourceService;
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
 * 数据字典管理
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller @RequestMapping(value="/system/sysitem")
public class SysItemController{
    @Autowired
    private ResourceService rs;
    /**
     * 跳转数据字典页面
     *
     * @return
     */
    @RequiresPermissions("system-sysitem") @RequestMapping(method=RequestMethod.GET)
    public String list(){
        return "system/sysItemList";
    }
    /**
     * 对字典分页显示
     *
     * @param dt
     * @param request
     *
     * @return
     */
    @RequiresPermissions("system-sysitem") @RequestMapping(value="list",method=RequestMethod.POST) @ResponseBody
    public DataTable<KnSysItem> list(DataTable<KnSysItem> dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return rs.PageKnSysItem(searchParams,dt);
    }
    /**
     * 进入新建字典页面
     *
     * @param model
     *
     * @return
     */
    @RequiresPermissions("system-sysitem") @RequestMapping(value="create",method=RequestMethod.GET)
    public String createForm(Model model){
        model.addAttribute("action","create");
        KnSysItem si=new KnSysItem();
        si.setId(0L);
        model.addAttribute("si",si);
        return "system/sysItemForm";
    }
    /**
     * 保存字典
     *
     * @param sysItem
     *
     * @return
     */
    @RequiresPermissions("system-sysitem") @RequestMapping(value="create",method=RequestMethod.POST)
    public String create(@Valid KnSysItem sysItem,RedirectAttributes redirectAttributes){
        rs.SaveKnSysItem(sysItem);
        redirectAttributes.addFlashAttribute("message","新建字典成功!");
        return "redirect:/system/sysitem";
    }
    /**
     * 进入编辑字典
     *
     * @param id
     *
     * @return
     */
    @RequiresPermissions("system-sysitem") @RequestMapping(value="update/{id}")
    public String updateForm(@PathVariable("id") Long id,Model model){
        model.addAttribute("action","update");
        KnSysItem si=rs.ReadKnSysItem(id);
        model.addAttribute("si",si);
        return "system/sysItemForm";
    }
    /**
     * 更新字典
     *
     * @param sysItem
     *
     * @return
     */
    @RequiresPermissions("system-sysitem") @RequestMapping(value="update")
    public String update(@Valid @ModelAttribute("sysItem") KnSysItem sysItem,RedirectAttributes redirectAttributes){
        rs.SaveKnSysItem(sysItem);
        redirectAttributes.addFlashAttribute("message","更新字典成功");
        return "redirect:/system/sysitem";
    }
    /**
     * 删除角色
     *
     * @param id
     *
     * @return
     */
    @RequiresPermissions("system-sysitem") @RequestMapping(value="delete/{id}") @ResponseBody
    public AjaxStatus delete(@PathVariable("id") Long id){
        rs.DeleteKnRole(id);
        return new AjaxStatus(true,"删除字典成功");
    }
    /**
     * 检查objId是否存在
     *
     * @param objId
     *
     * @return
     */
    @RequiresPermissions("system-sysitem") @RequestMapping(value="check/{id}") @ResponseBody
    public Boolean check(@PathVariable("id") Long id,@RequestParam("objId") String objId){
        return rs.checkKnSysItemObjId(objId,id);
    }
    @ModelAttribute
    public void ReadKnRole(@RequestParam(value="id",defaultValue="-1") Long id,Model model){
        if(id!=-1){
            model.addAttribute("sysItem",rs.ReadKnSysItem(id));
        }
    }
}