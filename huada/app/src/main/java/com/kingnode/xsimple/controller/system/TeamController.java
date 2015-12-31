package com.kingnode.xsimple.controller.system;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletRequest;

import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnTeam;
import com.kingnode.xsimple.service.system.OrganizationService;
import com.kingnode.xsimple.service.system.TeamService;
import com.kingnode.xsimple.util.Utils;
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
 * 团队管理
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller @RequestMapping(value="/system/team")
public class TeamController{
    @Autowired
    private TeamService teamService;
    @Autowired
    private OrganizationService organizationService;
    /**
     * 返回团队管理页面的内容
     *
     * @return
     */
    @RequiresPermissions("system-team") @RequestMapping(method=RequestMethod.GET)
    public String list(Model model){
        model.addAttribute("status",Setting.ActiveType.values());
        model.addAttribute("station",Setting.StationType.values());
        return "system/teamList";
    }
    /**
     * 团队列表
     *
     * @param dt
     * @param request
     *
     * @return
     */
    @RequiresPermissions("system-team") @RequestMapping(value="search-list") @ResponseBody
    public DataTable searchList(DataTable<KnTeam> dt,ServletRequest request){
        final Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return teamService.SearchKnTeamList(searchParams,dt);
    }
    /**
     * 查询员工
     *
     * @param dt
     * @param teamId
     * @param request
     *
     * @return
     */
    @RequiresPermissions("system-team") @RequestMapping(value="query-emp-list") @ResponseBody
    public DataTable queryEmployeeList(DataTable<KnEmployee> dt,@RequestParam(value="teamId", required=false) Long teamId,ServletRequest request){
        boolean flagEmpty=false;
        try{
            Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
            List<Long> idsList=new ArrayList<>();
            if(null!=teamId){
                KnTeam knTeam=teamService.FindKnTeam(teamId);
                Set<KnEmployee> set=knTeam.getEmps();
                if(Utils.isEmpityCollection(set)){
                    flagEmpty=true;
                }else{
                    for(KnEmployee knEmployee : set){
                        idsList.add(knEmployee.getId());
                    }
                    return organizationService.QueryEmployeeList(searchParams,dt,idsList);
                }
            }else{
                flagEmpty=true;
            }
        }catch(Exception e){
            flagEmpty=true;
            e.printStackTrace();
        }finally{
            if(flagEmpty){
                dt.setiTotalDisplayRecords(0L);
                dt.setAaData(new ArrayList<KnEmployee>());
            }
        }
        return dt;
    }
    /**
     * 员工列表
     *
     * @param dt
     * @param request
     *
     * @return
     */
    @RequiresPermissions("system-team") @RequestMapping(value="emp-member-list") @ResponseBody
    public DataTable queryEmployeeList(DataTable<KnEmployee> dt,ServletRequest request){
        try{
            final Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
            return organizationService.QueryEmployeeList(searchParams,dt,null);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 更新团队
     *
     * @param id
     * @param model
     *
     * @return
     */
    @RequiresPermissions("system-team") @RequestMapping(value="update-team/{id}",method=RequestMethod.GET)
    public String updateTeam(@PathVariable("id") Long id,Model model){
        KnTeam knTeam=teamService.FindKnTeam(id);
        knTeam.setEmps(null);
        model.addAttribute("team",knTeam);
        model.addAttribute("status",Setting.ActiveType.values());
        model.addAttribute("station",Setting.StationType.values());
        model.addAttribute("action","update");
        return "system/teamForm";
    }
    /**
     * 跳转添加团队
     *
     * @param model
     *
     * @return
     */
    @RequiresPermissions("system-team") @RequestMapping(value="add-team",method=RequestMethod.GET)
    public String addTeam(Model model){
        model.addAttribute("status",Setting.ActiveType.values());
        model.addAttribute("station",Setting.StationType.values());
        model.addAttribute("action","add");
        return "system/teamForm";
    }
    /**
     * 保存团队信息
     *
     * @param redirectAttributes
     * @param action
     * @param id
     * @param name
     * @param description
     * @param userId
     * @param active
     *
     * @return
     */
    @RequiresPermissions("system-team") @RequestMapping(value={"/save-edit"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String saveEdit(RedirectAttributes redirectAttributes,String action,String id,String name,String description,String userId,String active){
        KnTeam knTeam=null;
        try{
            KnEmployee master=organizationService.ReadKnEmployee(Long.parseLong(userId));
            if(action.equals("add")){
                knTeam=new KnTeam(System.currentTimeMillis()+"",name,description,master,Setting.ActiveType.valueOf(active));
            }else if(action.equals("update")){
                knTeam=teamService.FindKnTeam(Long.parseLong(id));
                knTeam.setName(name);
                knTeam.setDescription(description);
                knTeam.setMaster(master);
                knTeam.setActive(Setting.ActiveType.valueOf(active));
            }
            teamService.saveKnTeam(knTeam);
            redirectAttributes.addFlashAttribute("message","保存修改成功");
        }catch(Exception e){
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message","保存修改失败");
        }
        return "redirect:/system/team";
    }
    /**
     * @param tid    团队的id
     * @param uIds   员工的id集合
     * @param action "del": 指要进行删除操作 ,  "update":指要进行添加操作,把数据库中原有的员工清空再添加
     *
     * @return
     */
    @RequiresPermissions("system-team") @RequestMapping(value="update-dele-emp",method=RequestMethod.POST) @ResponseBody
    public Map addEmployee(@RequestParam("tid") Long tid,@RequestParam("uIds") List<Long> uIds,@RequestParam("action") String action){
        Map map=new HashMap();
        try{
            KnTeam knTeam=teamService.FindKnTeam(tid);
            Set<KnEmployee> set=knTeam.getEmps();
            if("del".equals(action)){
                Set<KnEmployee> knEmployeesSet=new HashSet<>();
                for(KnEmployee knEmployee : set){
                    if(!uIds.contains(knEmployee.getId())){
                        knEmployeesSet.add(knEmployee);
                    }
                }
                set=knEmployeesSet;
            }else if("update".equals(action)){
                List<KnEmployee> list=organizationService.ListEmployee(uIds);
                for(KnEmployee knEmployee : list){
                    set.add(knEmployee);
                }
            }
            knTeam.setEmps(set);
            teamService.saveKnTeam(knTeam);
            map.put("stat",true);
            map.put("info","修改成功");
        }catch(Exception e){
            map.put("stat",false);
            map.put("info","修改失败");
        }
        return map;
    }
    /**
     * 删除团队
     *
     * @param ids 团队的id
     *
     * @return
     */
    @RequiresPermissions("system-team") @RequestMapping(value="delete",method=RequestMethod.POST) @ResponseBody
    public Map deleteEmployee(@RequestParam("ids") List<Long> ids){
        Map map=new HashMap();
        try{
            teamService.DeleteKnTeam(ids);
            map.put("stat",true);
            map.put("info","删除成功");
        }catch(Exception e){
            map.put("stat",false);
            map.put("info","删除失败");
        }
        return map;
    }
}