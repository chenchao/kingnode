package com.kingnode.auto.controller;
import java.util.Map;
import javax.servlet.ServletRequest;

import com.kingnode.auto.entity.KnAuto;
import com.kingnode.auto.entity.KnTrafficAccident;
import com.kingnode.auto.service.KnAutoManageService;
import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.service.system.ResourceService;
import com.kingnode.xsimple.util.Utils;
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
@Controller @RequestMapping(value="/auto/manage/accident")
public class KnTrafficAccidentController{
    @Autowired
    private KnAutoManageService as;
    @Autowired
    private ResourceService rs;
    /**
     * 列出指定的车险
     *
     * @param model
     * @param id
     *
     * @return
     */
    @RequestMapping(value="{id}",method=RequestMethod.GET)
    public String list(Model model,@PathVariable("id") Long id){
        KnAuto auto=as.ReadAuto(id);
        KnTrafficAccident accident=new KnTrafficAccident();
        accident.setKaId(auto.getId());
        model.addAttribute("json",accident);
        return "auto/accidentList";
    }
    /**
     * 对车险进行分页显示
     *
     * @param dt
     * @param request
     *
     * @return
     */
    @RequestMapping(method=RequestMethod.POST) @ResponseBody
    public DataTable<KnTrafficAccident> list(DataTable<KnTrafficAccident> dt,Long autoId,ServletRequest request){
        Sort sort=Utils.getSort(dt,request);
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return as.PageKnTrafficAccident(searchParams,dt,sort,autoId);
    }
    /**
     * 给指定公共用车创建车险
     *
     * @param model
     * @param id    车辆 id
     *
     * @return
     */
    @RequestMapping(value="create/{id}",method=RequestMethod.GET)
    public String create(Model model,@PathVariable("id") Long id){
        KnAuto auto=as.ReadAuto(id);
        KnTrafficAccident accident=new KnTrafficAccident();
        accident.setKaId(auto.getId());
        model.addAttribute("actionName","创建");
        model.addAttribute("json",accident);
        model.addAttribute("url","/auto/manage/accident/create");
        return "auto/accidentForm";
    }
    @RequestMapping(value="create",method=RequestMethod.POST)
    public String create(KnTrafficAccident accident,RedirectAttributes redirectAttributes){
        String msg="保存车险成功!";
        accident=as.SaveKnTrafficAccident(accident);
        redirectAttributes.addFlashAttribute("message",msg);
        return "redirect:/auto/manage/accident/"+accident.getKaId();
    }
    /**
     * 跳转到修改车险信息页面
     *
     * @param id    车险id
     * @param model
     *
     * @return
     */
    @RequestMapping(value="update/{id}",method=RequestMethod.GET)
    public String update(@PathVariable("id") Long id,Model model){
        KnTrafficAccident accident=as.ReadKnTrafficAccident(id);
        model.addAttribute("json",accident);
        model.addAttribute("actionName","修改");
        model.addAttribute("url","/auto/manage/accident/update");
        return "auto/accidentForm";
    }
    @RequestMapping(value="update",method=RequestMethod.POST)
    public String update(Model model,KnTrafficAccident accident,RedirectAttributes redirectAttributes){
        String msg="修改车险成功!";
        accident=as.SaveKnTrafficAccident(accident);
        redirectAttributes.addFlashAttribute("message",msg);
        return "redirect:/auto/manage/accident/"+accident.getKaId();
    }
    /**
     * 删除车险
     *
     * @param id 车险id
     *
     * @return
     */
    @RequestMapping(value="delete",method=RequestMethod.GET)
    public String delete(Long id,RedirectAttributes redirectAttributes){
        String msg="删除车险成功!";
        KnTrafficAccident accident=as.ReadKnTrafficAccident(id);
        as.DeleteKnTrafficAccident(id);
        redirectAttributes.addFlashAttribute("message",msg);
        return "redirect:/auto/manage/accident/"+accident.getKaId();
    }
}
