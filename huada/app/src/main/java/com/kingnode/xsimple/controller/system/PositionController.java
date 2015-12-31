package com.kingnode.xsimple.controller.system;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.validation.Valid;

import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.api.common.AjaxStatus;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.api.system.EmployeePosition;
import com.kingnode.xsimple.entity.system.KnPosition;
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
 * 岗位管理
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller @RequestMapping(value="/system/position")
public class PositionController{
    @Autowired
    private OrganizationService os;
    /**
     * 打开组织岗位管理页面
     *
     * @return
     */
    @RequiresPermissions("system-position") @RequestMapping(method=RequestMethod.GET)
    public String list(Model model){
        model.addAttribute("posTree",JsonMapper.nonEmptyMapper().toJson(os.PositionJsTree()));
        return "system/positionList";
    }
    /**
     * 保存岗位
     *
     * @param pos
     * @param redirectAttributes
     *
     * @return
     */
    @RequiresPermissions("system-position") @RequestMapping(value="save", method=RequestMethod.POST)
    public String create(@Valid KnPosition pos,RedirectAttributes redirectAttributes){
        os.SaveKnPosition(pos);
        redirectAttributes.addFlashAttribute("message","保存组织成功");
        return "redirect:/system/position";
    }
    /**
     * 删除岗位信息
     *
     * @param id
     *
     * @return
     */
    @RequiresPermissions("system-position") @RequestMapping(value="delete/{id}", method=RequestMethod.DELETE) @ResponseBody
    public AjaxStatus delete(@PathVariable("id") Long id){
        return os.DeletePosition(id);
    }
    /**
     * 获取资源
     *
     * @param id 资源ID
     */
    @RequiresPermissions("system-position") @RequestMapping(value="read/{id}", method=RequestMethod.GET) @ResponseBody
    public KnPosition read(@PathVariable Long id){
        return os.ReadPos(id);
    }
    /**
     * 根据组织ID获取所有在此组织级别下的员工信息
     *
     * @param dt
     * @param id
     *
     * @return
     */
    @RequiresPermissions("system-position") @RequestMapping(value="emp-in-pos", method=RequestMethod.POST) @ResponseBody
    public DataTable<EmployeePosition> ListEmpInPos(DataTable<EmployeePosition> dt,@RequestParam(value="id", defaultValue="0") Long id,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return id.compareTo(0L)>0?os.PageEmpInPos(dt,id,searchParams):dt;
    }
    /**
     * 根据组织ID获取所有不在此组织有在职员工信息
     *
     * @param dt
     * @param id
     *
     * @return
     */
    @RequiresPermissions("system-position") @RequestMapping(value="emp-out-pos", method=RequestMethod.POST) @ResponseBody
    public DataTable<EmployeePosition> ListEmpOutPos(DataTable<EmployeePosition> dt,@RequestParam(value="id", defaultValue="0") Long id,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return id.compareTo(0L)>0?os.PageEmpOutPos(dt,id,searchParams):dt;
    }
    /**
     * 变更员工所在组织的职能（主岗位）
     *
     * @param posId
     * @param empId
     *
     * @return
     */
    @RequiresPermissions("system-position") @RequestMapping(value="major", method=RequestMethod.POST) @ResponseBody
    public AjaxStatus major(@RequestParam("posId") Long posId,@RequestParam("empId") Long empId){
        return os.MajorPosition(posId,empId);
    }
    /**
     * 员工加入岗位信息
     *
     * @param posId
     * @param empId
     *
     * @return
     */
    @RequiresPermissions("system-position") @RequestMapping(value="join", method=RequestMethod.POST) @ResponseBody
    public AjaxStatus join(@RequestParam("posId") Long posId,@RequestParam("empId") Long empId){
        return os.JoinPosition(posId,empId);
    }
    /**
     * 员工加入岗位信息
     *
     * @param posId
     * @param empId
     *
     * @return
     */
    @RequiresPermissions("system-position") @RequestMapping(value="leave", method=RequestMethod.POST) @ResponseBody
    public AjaxStatus leave(@RequestParam("posId") Long posId,@RequestParam("empId") Long empId){
        return os.LeavePosition(posId,empId);
    }
    /**
     * 打开组织岗位管理页面
     *
     * @return
     */
    @RequiresPermissions("system-position") @RequestMapping(value="branched-passage/{posId}/{empId}",method=RequestMethod.GET)
    public String branched(@PathVariable("posId") Long posId,@PathVariable("empId") Long empId,Model model){
        model.addAttribute("ep",os.ReadKnEmployeePosition(posId,empId));
        return "system/positionBranched";
    }
    /**
     * 根据组织ID获取所有在此组织级别下的员工信息
     *
     * @param dt
     *
     * @return
     */
    @RequiresPermissions("system-position") @RequestMapping(value="emp-in-pos-branched/{posId}/{empId}",method=RequestMethod.POST) @ResponseBody
    public DataTable<EmployeePosition> ListEmpInPosBranched(DataTable<EmployeePosition> dt,@PathVariable("posId") Long posId,@PathVariable("empId") Long empId,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return os.PageEmpInPosBranched(dt,posId,empId,searchParams);
    }
    /**
     * 根据组织ID获取所有不在此组织有在职员工信息
     *
     * @param dt
     *
     * @return
     */
    @RequiresPermissions("system-position") @RequestMapping(value="emp-out-pos-branched/{empId}",method=RequestMethod.POST) @ResponseBody
    public DataTable<EmployeePosition> ListEmpOutPosBranched(DataTable<EmployeePosition> dt,@PathVariable("empId") Long empId,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return os.PageEmpOutPosBranched(dt,empId,searchParams);
    }
    @RequiresPermissions("system-position") @RequestMapping(value="branched/join", method=RequestMethod.POST) @ResponseBody
    public AjaxStatus branchedJoin(@RequestParam("posId") Long posId,@RequestParam("posCode") String posCode,@RequestParam("posName") String posName,@RequestParam("leaderId") Long leaderId,@RequestParam("leaderName") String leaderName,@RequestParam("empId") Long empId,@RequestParam("empName") String empName){
        return os.JoinPositionBranched(posId,posCode,posName,leaderId,leaderName,empId,empName);
    }
    @RequiresPermissions("system-position") @RequestMapping(value="branched/leave", method=RequestMethod.POST) @ResponseBody
    public AjaxStatus branchedLeave(@RequestParam("empId") Long empId){
        return os.LeavePositionBranched(empId);
    }
}