package com.kingnode.xsimple.controller.system;
import javax.validation.Valid;

import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.xsimple.api.common.AjaxStatus;
import com.kingnode.xsimple.entity.system.KnResource;
import com.kingnode.xsimple.service.system.ResourceService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
/**
 * 资源管理
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller @RequestMapping(value="/system/resource")
public class ResourceController{
    @Autowired
    private ResourceService rs;
    /**
     * 返回资源列表的内容到页面
     *
     * @return
     */
    @RequiresPermissions("system-resource") @RequestMapping(method=RequestMethod.GET)
    public String tree(Model model){
        model.addAttribute("resourceTree",JsonMapper.nonEmptyMapper().toJson(rs.GetResourceTree()));
        model.addAttribute("resourceTypes",KnResource.ResourceType.values());
        return "system/resourceTree";
    }
    /**
     * 获取资源
     *
     * @param id 资源ID
     */
    @RequiresPermissions("system-resource") @RequestMapping(value="read/{id}", method=RequestMethod.GET) @ResponseBody
    public KnResource read(@PathVariable Long id){
        return rs.ReadKnResource(id);
    }
    /**
     * 保存资源信息
     *
     * @param res
     * @param redirectAttributes
     *
     * @return
     */
    @RequiresPermissions("system-resource") @RequestMapping(value="save", method=RequestMethod.POST)
    public String create(@Valid KnResource res,RedirectAttributes redirectAttributes){
        rs.SaveKnResource(res);
        redirectAttributes.addFlashAttribute("message","保存资源成功");
        return "redirect:/system/resource";
    }
    /**
     * 删除资源
     *
     * @param id 资源ID
     */
    @RequiresPermissions("system-resource") @RequestMapping(value="delete/{id}", method=RequestMethod.DELETE) @ResponseBody
    public AjaxStatus delete(@PathVariable("id") Long id){
        rs.DeleteKnResource(id);
        return new AjaxStatus(true,"删除资源成功");
    }
}
