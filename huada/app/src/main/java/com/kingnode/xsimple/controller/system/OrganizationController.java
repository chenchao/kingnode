package com.kingnode.xsimple.controller.system;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.validation.Valid;

import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.api.common.AjaxStatus;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.api.system.EmployeeOrganization;
import com.kingnode.xsimple.entity.system.KnOrganization;
import com.kingnode.xsimple.service.system.OrganizationService;
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
 * 组织管理
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller @RequestMapping(value="/system/organization")
public class OrganizationController{
    @Autowired
    private OrganizationService os;
    /**
     * 打开组织管理页面
     *
     * @param model
     *
     * @return
     */
    @RequiresPermissions("system-organization") @RequestMapping(method=RequestMethod.GET)
    public String list(Model model){
        model.addAttribute("orgTree",JsonMapper.nonEmptyMapper().toJson(os.OrganizationJsTree()));
        return "system/organizationList";
    }
    /**
     * 保存组织
     *
     * @param org
     * @param redirectAttributes
     *
     * @return
     */
    @RequiresPermissions("system-organization") @RequestMapping(value="save", method=RequestMethod.POST)
    public String create(@Valid KnOrganization org,RedirectAttributes redirectAttributes){
        os.SaveOrg(org);
        redirectAttributes.addFlashAttribute("message","保存组织成功");
        return "redirect:/system/organization";
    }
    /**
     * 删除组织
     *
     * @param id
     *
     * @return
     */
    @RequiresPermissions("system-organization") @RequestMapping(value="delete/{id}", method=RequestMethod.DELETE) @ResponseBody
    public AjaxStatus delete(@PathVariable("id") Long id){
        return os.DeleteOrganization(id);
    }
    /**
     * 获取资源
     *
     * @param id 资源ID
     */
    @RequiresPermissions("system-organization") @RequestMapping(value="read/{id}",method=RequestMethod.GET) @ResponseBody
    public KnOrganization read(@PathVariable Long id){
        return os.ReadOrg(id);
    }
    /**
     * 根据组织ID获取所有在此组织级别下的员工信息
     *
     * @param dt
     * @param id
     *
     * @return
     */
    @RequiresPermissions("system-organization") @RequestMapping(value="emp-in-org",method=RequestMethod.POST) @ResponseBody
    public DataTable<EmployeeOrganization> ListEmpInOrg(DataTable<EmployeeOrganization> dt,@RequestParam(value="id",defaultValue="0") Long id,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return id.compareTo(0L)>0?os.PageEmpInOrg(dt,id,searchParams):dt;
    }
    /**
     * 根据组织ID获取所有不在此组织有在职员工信息
     *
     * @param dt
     * @param id
     *
     * @return
     */
    @RequiresPermissions("system-organization") @RequestMapping(value="emp-out-org",method=RequestMethod.POST) @ResponseBody
    public DataTable<EmployeeOrganization> ListEmpOutOrg(DataTable<EmployeeOrganization> dt,@RequestParam(value="id",defaultValue="0") Long id,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return id.compareTo(0L)>0?os.PageEmpOutOrg(dt,id,searchParams):dt;
    }
    /**
     * 修改员工在组织中的主副负责人状态
     *
     * @param orgId
     * @param empId
     *
     * @return
     */
    @RequiresPermissions("system-organization") @RequestMapping(value="charge", method=RequestMethod.POST) @ResponseBody
    public AjaxStatus charge(@RequestParam("orgId") Long orgId,@RequestParam("empId") Long empId){
        return os.ChargeOrganization(orgId,empId);
    }
    /**
     * 修改员工在组织中的主副组织状态
     *
     * @param orgId
     * @param empId
     *
     * @return
     */
    @RequiresPermissions("system-organization") @RequestMapping(value="major", method=RequestMethod.POST) @ResponseBody
    public AjaxStatus major(@RequestParam("orgId") Long orgId,@RequestParam("empId") Long empId){
        return os.MajorOrganization(orgId,empId);
    }
    /**
     * 员工加入组织
     *
     * @param orgId
     * @param empId
     *
     * @return
     */
    @RequiresPermissions("system-organization") @RequestMapping(value="join", method=RequestMethod.POST) @ResponseBody
    public AjaxStatus join(@RequestParam("orgId") Long orgId,@RequestParam("empId") Long empId){
        return os.JoinOrganization(orgId,empId);
    }
    /**
     * 员工离开组织
     *
     * @param orgId
     * @param empId
     *
     * @return
     */
    @RequiresPermissions("system-organization") @RequestMapping(value="leave", method=RequestMethod.POST) @ResponseBody
    public AjaxStatus leave(@RequestParam("orgId") Long orgId,@RequestParam("empId") Long empId){
        return os.LeaveOrganization(orgId,empId);
    }
}
