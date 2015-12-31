package com.kingnode.news.controller;

import com.kingnode.diva.web.Servlets;
import com.kingnode.news.entity.KnWelfare;
import com.kingnode.news.service.NewsService;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.service.system.OrganizationService;
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

import javax.servlet.ServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * @author 陈超
 */
@Controller
@RequestMapping(value="/news/welfare")
public class WelfareController{
    private final static String imgDefault="/assets/global/img/gonggaoshiyi.png";
    @Autowired
    private OrganizationService orgService;
    @Autowired
    private NewsService newsService;

    /**
     * 跳转到工会福利列表页面
     */
    @RequestMapping(method=RequestMethod.GET)
    public String home(){
        return "news/welfareList";
    }

    /**
     * 分页查询
     */
    @RequestMapping(value="list", method=RequestMethod.POST)
    @ResponseBody
    public DataTable<KnWelfare> list(DataTable<KnWelfare> dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return newsService.PageKnWelfare(searchParams,dt);
    }

    /**
     * 进入工会福利新增界面
     */
    @RequestMapping(value="create", method=RequestMethod.GET)
    public String createForm(Model model){
        model.addAttribute("welfare",new KnWelfare());
        model.addAttribute("imgDefault",imgDefault);
        model.addAttribute("action","create");
        model.addAttribute("orgs",orgService.findAll());
        return "news/welfareForm";
    }

    /**
     * 保存工会福利信息
     */
    @RequestMapping(value="create", method=RequestMethod.POST)
    public String create(@Valid @ModelAttribute("welfare") KnWelfare welfare,RedirectAttributes redirectAttributes,@RequestParam(value="_effectiveTime") String effectiveTime,ServletRequest request){
        String[] orgids=request.getParameterValues("orgList");
        newsService.SaveKnWelfare(welfare,effectiveTime,orgids);
        redirectAttributes.addFlashAttribute("message","创建工会福利成功");
        return "redirect:/news/welfare";
    }

    /**
     * 工会福利详情
     */
    @RequestMapping(value="detail/{id}", method=RequestMethod.GET)
    public String detail(@PathVariable("id") Long id,Model model){
        model.addAttribute("notice",newsService.FindKnWelfare(id));
        return "news/welfareDetail";
    }

    /**
     * 置顶
     */
    @RequestMapping(value="topic/{id}", method=RequestMethod.POST)
    @ResponseBody
    public String topic(@PathVariable(value="id") Long id){
        newsService.TopicKnWelfare(id);
        return "true";
    }

    /**
     * 删除工会福利
     */
    @RequestMapping(value="delete/{id}", method=RequestMethod.POST)
    @ResponseBody
    public String delete(@PathVariable("id") Long id,RedirectAttributes redirectAttributes){
        newsService.DeleteWelfare(id);
        redirectAttributes.addFlashAttribute("message","删除新闻内容成功");
        return "true";
    }

    /**
     * 跳转到修改工会福利
     */
    @RequestMapping(value="update/{id}", method=RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id,Model model){
        KnWelfare we=newsService.FindKnWelfare(id);
        model.addAttribute("welfare",we);
        model.addAttribute("orgsAlready",newsService.FindOrgizion(we.getOrgPath()));
        model.addAttribute("action","update");
        model.addAttribute("orgs",orgService.findAll());
        return "news/welfareForm";
    }

    /**
     * 修改工会福利
     */
    @RequestMapping(value="update", method=RequestMethod.POST)
    public String update(@Valid @ModelAttribute("article") KnWelfare welfare,@RequestParam("_effectiveTime") String effectiveTime,RedirectAttributes redirectAttributes,ServletRequest request){
        String[] orgids=request.getParameterValues("orgList");
        newsService.SaveKnWelfare(welfare,effectiveTime,orgids);
        redirectAttributes.addFlashAttribute("message","修改工会福利成功");
        return "/news/welfareList";
    }
}
