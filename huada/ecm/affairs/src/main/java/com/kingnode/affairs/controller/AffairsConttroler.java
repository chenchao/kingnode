package com.kingnode.affairs.controller;
import java.util.Map;
import javax.servlet.ServletRequest;

import com.kingnode.affairs.entity.KnAffairsPerson;
import com.kingnode.affairs.entity.KnDepartment;
import com.kingnode.affairs.service.AffairsService;
import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.service.system.OrganizationService;
import com.kingnode.xsimple.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller @RequestMapping(value="/affairs")
public class AffairsConttroler{
    private static Logger logger=LoggerFactory.getLogger(AffairsConttroler.class);
    @Autowired
    private AffairsService es;
    @Autowired
    private OrganizationService os;
    @RequestMapping(value="list",method=RequestMethod.GET)
    public String list(Model model){
        return "affairs/affairsList";
    }
    /**
     * 对接口人进行分页显示
     *
     * @param dt
     * @param request
     *
     * @return
     */
    @RequestMapping(value="list",method=RequestMethod.POST) @ResponseBody
    public DataTable<KnAffairsPerson> list(DataTable<KnAffairsPerson> dt,ServletRequest request){
        Sort sort=Utils.getSort(dt,request);
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        es.PageKnAffairsPerson(searchParams,dt,sort);
        dt.setAaData(dt.getAaData());
        return dt;
    }
    @RequestMapping(value="create",method=RequestMethod.GET)
    public String create(Model model){
        model.addAttribute("actionName","创建");
        model.addAttribute("url","/affairs/create");
        return "affairs/affairsForm";
    }
    @RequestMapping(value="create",method=RequestMethod.POST)
    public String create(KnAffairsPerson persion,RedirectAttributes redirectAttributes){
        String msg="保存接口人成功!";
        String departmentName=persion.getDepartmentName();
        if(Utils.isEmptyStr(departmentName)){
            msg="保存接口人失败!";
            redirectAttributes.addFlashAttribute("message",msg);
            return "redirect:/affairs/list";
        }
        KnDepartment department=es.findByDname(departmentName.trim());
        if(department==null){
            KnDepartment newDepartment=new KnDepartment();
            newDepartment.setDepartmentName(departmentName);
            es.SaveKnDepartment(newDepartment);
        }
        persion.setDepartmentName(departmentName);
        es.SaveKnAffairsPerson(persion);
        redirectAttributes.addFlashAttribute("message",msg);
        return "redirect:/affairs/list";
    }
    /**
     * 打开更新接口人页面
     *
     * @param id
     * @param model
     *
     * @return
     */
    @RequestMapping(value="update/{id}",method=RequestMethod.GET)
    public String update(@PathVariable("id") Long id,Model model){
        KnAffairsPerson person=es.ReadKnAffairsPerson(id);
        model.addAttribute("person",person);
        model.addAttribute("actionName","修改");
        model.addAttribute("url","/affairs/update");
        return "affairs/affairsForm";
    }
    /**
     * 执行修改接口人
     *
     * @param person
     * @param redirectAttributes
     *
     * @return
     */
    @RequestMapping(value="update",method=RequestMethod.POST)
    public String update(KnAffairsPerson person,String oldDepartmentName,RedirectAttributes redirectAttributes){
        String msg="修改接口人成功!";
        KnDepartment department=es.findByDname(oldDepartmentName);
        department.setDepartmentName(person.getDepartmentName());
        es.SaveKnAffairsPerson(person);
        redirectAttributes.addFlashAttribute("message",msg);
        return "redirect:/affairs/list";
    }
    /**
     * 删除接口人
     *
     * @param id
     *
     * @return
     */
    @RequestMapping(value="delete",method=RequestMethod.GET)
    public String delete(Long id,RedirectAttributes redirectAttributes){
        String msg="删除接口人成功!";
        es.DeleteKnAffairsPerson(id);
        redirectAttributes.addFlashAttribute("message",msg);
        return "redirect:/affairs/list";
    }
}
