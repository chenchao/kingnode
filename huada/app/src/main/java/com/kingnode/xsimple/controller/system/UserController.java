package com.kingnode.xsimple.controller.system;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.validation.Valid;

import com.kingnode.diva.security.utils.Digests;
import com.kingnode.diva.utils.Encodes;
import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.entity.system.KnUser;
import com.kingnode.xsimple.service.system.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * 系统用户管理
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller @RequestMapping(value="/system/user")
public class UserController{
    private static Logger logger=LoggerFactory.getLogger(UserController.class);
    @Autowired
    private ResourceService rs;
    @RequestMapping(method=RequestMethod.GET)
    public String list(){
        return "system/userList";
    }
    /**
     * 对用户分页显示
     *
     * @param dt
     * @param request
     *
     * @return
     */
    @RequestMapping(method=RequestMethod.POST) @ResponseBody
    public DataTable<KnUser> list(DataTable<KnUser> dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return rs.PageKnUser(searchParams,dt);
    }
    /**
     * 进入新闻分类编辑页面
     *
     * @param model
     *
     * @return
     */
    @RequestMapping(value="create", method=RequestMethod.GET)
    public String createForm(Model model){
        model.addAttribute("action","create");
        KnUser ku=new KnUser();
        ku.setId(0L);
        ku.setSalt(Encodes.encodeHex(Digests.generateSalt(ResourceService.SALT_SIZE)));
        model.addAttribute("u",ku);
        model.addAttribute("roles",rs.ListKnRoleExitsActive());
        return "system/userForm";
    }
    /**
     * 保存用户
     *
     * @param user
     *
     * @return
     */
    @RequestMapping(value="create", method=RequestMethod.POST)
    public String create(@Valid KnUser user,@RequestParam(value="ids", defaultValue="") Long[] ids,RedirectAttributes redirectAttributes){
        rs.SaveKnUser(user,ids);
        redirectAttributes.addFlashAttribute("message","新建用户成功!");
        return "redirect:/system/user";
    }
    /**
     * 更新用户
     *
     * @param id
     *
     * @return
     */
    @RequestMapping(value="update/{id}")
    public String updateForm(@PathVariable("id") Long id,Model model){
        model.addAttribute("action","update");
        model.addAttribute("u",rs.ReadUser(id));
        model.addAttribute("roles",rs.ListKnRoleExitsActive());
        return "system/userForm";
    }
    /**
     * 更新用户
     *
     * @param user
     *
     * @return
     */
    @RequestMapping(value="update")
    public String update(@Valid @ModelAttribute("user") KnUser user,@RequestParam(value="ids", defaultValue="") Long[] ids,RedirectAttributes redirectAttributes){
        rs.SaveKnUser(user,ids);
        redirectAttributes.addFlashAttribute("message","更新用户成功");
        return "redirect:/system/user";
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
    @RequestMapping(value="delete/{id}")
    public String delete(@PathVariable("id") Long id,RedirectAttributes redirectAttributes){
        String loginName=rs.DeleteKnUser(id);
        redirectAttributes.addFlashAttribute("message","删除用户"+loginName+"成功");
        return "redirect:/system/user";
    }
    @ModelAttribute
    public void ReadKnRole(@RequestParam(value="id", defaultValue="-1") Long id,Model model){
        if(id!=-1){
            model.addAttribute("user",rs.ReadUser(id));
        }
    }
    /**
     * 用户分配角色
     * @param roleList 角色id集合
     * @param userList 用户id集合
     * @param tip 标示:0为全部分配和1选中分配
     * @return 返回成功失败信息
     */
    @RequestMapping(value="save-role-user", method=RequestMethod.POST) @ResponseBody
    public Map<String,Object> saveRoleOfUserList(@RequestParam("roleList") List<Long> roleList,@RequestParam("userList") List<Long> userList,@RequestParam(value="tip",required=false) String tip){
        Map<String,Object> map=new HashMap();
        try{
            map=rs.SaveRoleOfUserList(userList,roleList,tip);
        }catch(Exception e){
            map.put("stat",false);
            map.put("info","保存失败");
            logger.error("用户分配角色错误信息：{}",e);
        }
        return map;
    }
}