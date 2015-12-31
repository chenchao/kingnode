package com.kingnode.xsimple.controller.system;
import java.io.File;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.validation.Valid;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.diva.security.utils.Digests;
import com.kingnode.diva.utils.Encodes;
import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.api.system.EmpOrg;
import com.kingnode.xsimple.api.system.EmpPos;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnPositionBranchedPassage;
import com.kingnode.xsimple.entity.system.KnUser;
import com.kingnode.xsimple.service.system.OrganizationService;
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
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
/**
 * 员工管理
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller @RequestMapping(value="/system/employee")
public class EmployeeController{
    @Autowired
    private OrganizationService os;
    @Autowired
    private ResourceService rs;
    /**
     * 打开员工管理页面
     *
     * @return
     */
    @RequiresPermissions("system-employee") @RequestMapping(method=RequestMethod.GET)
    public String list(){
        return "system/employeeList";
    }
    /**
     * 对员工分页显示
     *
     * @param dt
     * @param request
     *
     * @return
     */
    @RequiresPermissions("system-employee") @RequestMapping(method=RequestMethod.POST) @ResponseBody
    public DataTable<KnEmployee> list(DataTable<KnEmployee> dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return os.PageKnEmployee(searchParams,dt);
    }
    /**
     * 打开创建员工页面
     *
     * @return
     */
    @RequiresPermissions("system-employee") @RequestMapping(value="create",method=RequestMethod.GET)
    public String create(Model model){
        model.addAttribute("action","create");
        KnUser u=new KnUser();
        u.setId(0L);
        u.setSalt(Encodes.encodeHex(Digests.generateSalt(ResourceService.SALT_SIZE)));
        model.addAttribute("u",u);
        model.addAttribute("orgTree",JsonMapper.nonEmptyMapper().toJson(os.OrganizationJsTree()));
        model.addAttribute("posTree",JsonMapper.nonEmptyMapper().toJson(os.PositionJsTree()));
        model.addAttribute("roles",rs.ListKnRoleExitsActive());
        model.addAttribute("teams",os.ListTeam());
        return "system/employeeForm";
    }
    /**
     * 保存用户
     *
     * @param user
     *
     * @return
     */
    @RequiresPermissions("system-employee") @RequestMapping(value="create",method=RequestMethod.POST)
    public String create(@Valid KnUser user,@Valid KnEmployee emp,@RequestParam(value="roles",defaultValue="") Long[] roles,@RequestParam(value="teams",defaultValue="") Long[] teams,@RequestParam(value="empOrg",defaultValue="") String org,@RequestParam(value="empPos",defaultValue="") String pos,@RequestParam("files") MultipartFile file,RedirectAttributes redirectAttributes){
        JsonMapper jm=JsonMapper.nonDefaultMapper();
        List<EmpOrg> empOrg=jm.fromJson(org,new TypeReference<List<EmpOrg>>(){
        });
        List<EmpPos> empPos=jm.fromJson(pos,new TypeReference<List<EmpPos>>(){
        });
        os.SaveKnEmployee(user,emp,roles,teams,empOrg,empPos);
        if(!file.isEmpty()){
            String name=file.getOriginalFilename();
            int i=file.getOriginalFilename().lastIndexOf(".");
            emp.setImageAddress(Setting.USERIMAGE+user.getId()+(i>=0?name.substring(i):".jpg"));
            updateImg(emp,file);
            os.SaveKnEmployee(emp);
        }
        redirectAttributes.addFlashAttribute("message","新建员工成功!");
        return "redirect:/system/employee";
    }
    /**
     * 跳转更新员工
     *
     * @param id
     *
     * @return
     */
    @RequiresPermissions("system-employee") @RequestMapping(value="update/{id}")
    public String updateForm(@PathVariable("id") Long id,Model model){
        model.addAttribute("action","update");
        model.addAttribute("u",rs.ReadUser(id));
        KnEmployee ke=os.ReadKnEmployee(id);
        ke.setImageAddress(Strings.isNullOrEmpty(ke.getImageAddress())?"":ke.getImageAddress());
        model.addAttribute("e",ke);
        model.addAttribute("orgTree",JsonMapper.nonEmptyMapper().toJson(os.OrganizationJsTree()));
        model.addAttribute("posTree",JsonMapper.nonEmptyMapper().toJson(os.PositionJsTree()));
        model.addAttribute("roles",rs.ListKnRoleExitsActive());
        model.addAttribute("teams",os.ListTeam());
        //获取上级人员信息
        KnPositionBranchedPassage branchedPassage=os.ReadKnPositionBranchedPassage(id);
        model.addAttribute("leaderId",branchedPassage==null?"":branchedPassage.getLeaderId());
        model.addAttribute("leaderName",branchedPassage==null?"":branchedPassage.getLeaderName());
        return "system/employeeForm";
    }
    /**
     * 更新员工
     *
     * @param user
     *
     * @return
     */
    @RequiresPermissions("system-employee") @RequestMapping(value="update")
    public String update(@Valid @ModelAttribute("user") KnUser user,@Valid @ModelAttribute("emp") KnEmployee emp,@RequestParam(value="roles",defaultValue="") Long[] roles,@RequestParam(value="teams",defaultValue="") Long[] teams,@RequestParam(value="empOrg",defaultValue="") String org,@RequestParam(value="empPos",defaultValue="") String pos,@RequestParam("files") MultipartFile file,RedirectAttributes redirectAttributes){
        if(!file.isEmpty()){
            String name=file.getOriginalFilename();
            int i=file.getOriginalFilename().lastIndexOf(".");
            emp.setImageAddress(Setting.USERIMAGE+user.getId()+(i>=0?name.substring(i):".jpg"));
            updateImg(emp,file);
        }
        JsonMapper jm=JsonMapper.nonDefaultMapper();
        List<EmpOrg> empOrg=jm.fromJson(org,new TypeReference<List<EmpOrg>>(){
        });
        List<EmpPos> empPos=jm.fromJson(pos,new TypeReference<List<EmpPos>>(){
        });
        os.SaveKnEmployee(user,emp,roles,teams,empOrg,empPos);
        redirectAttributes.addFlashAttribute("message","更新员工成功");
        return "redirect:/system/employee";
    }
    /**
     * 检查用户名是否存在
     *
     * @param loginName
     *
     * @return
     */
    @RequestMapping(value="check/{id}") @ResponseBody
    public Boolean check(@PathVariable("id") Long id,@RequestParam("loginName") String loginName){
        return rs.checkLoginName(loginName,id);
    }
    /**
     * 删除用户
     *
     * @param id
     * @param redirectAttributes
     *
     * @return
     */
    @RequiresPermissions("system-employee") @RequestMapping(value="delete/{id}")
    public String delete(@PathVariable("id") Long id,RedirectAttributes redirectAttributes){
        String loginName=os.DeleteKnEmployee(id);
        redirectAttributes.addFlashAttribute("message","删除用户"+loginName+"成功");
        return "redirect:/system/employee";
    }
    private void updateImg(KnEmployee emp,MultipartFile file){
        try{
            WebApplicationContext webApplicationContext=ContextLoader.getCurrentWebApplicationContext();
            File localFile=new File(webApplicationContext.getServletContext().getRealPath(emp.getImageAddress()));
            if(!localFile.getParentFile().exists()){
                localFile.getParentFile().mkdirs();
            }
            file.transferTo(localFile);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @ModelAttribute
    public void ReadKnRole(@RequestParam(value="id", defaultValue="-1") Long id,Model model){
        if(id!=-1){
            model.addAttribute("user",rs.ReadUser(id));
            model.addAttribute("emp",os.ReadKnEmployee(id));
        }
    }
}