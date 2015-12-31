package com.kingnode.news.controller;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.validation.Valid;

import com.kingnode.diva.web.Servlets;
import com.kingnode.news.dto.KnNoticeDTO;
import com.kingnode.news.entity.KnNotice;
import com.kingnode.news.entity.KnNoticeReceipt;
import com.kingnode.news.service.NewsService;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.service.system.CustomService;
import com.kingnode.xsimple.service.system.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller @RequestMapping(value="/news/notice")
public class NoticeController{
    @Autowired
    private NewsService newsService;
    @Autowired
    private OrganizationService orgService;
    @Autowired
    private CustomService customService;
    @Value("#{commonInfo['imgDefault']}")
    private final static String imgDefault="/assets/global/img/gonggaoshiyi.png";//ReadClientProper.prop.getProperty("noticeDefaultImg");




    /**
     * 跳转到公告列表页面
     * @return
     */
    @RequestMapping(method=RequestMethod.GET)
    public String home(){
        return "news/noticeList";
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    @RequestMapping(method=RequestMethod.POST) @ResponseBody
    public  DataTable<KnNoticeDTO> list(DataTable<KnNoticeDTO> dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        try{
            return newsService.PageKnNotice(searchParams, dt);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 进入公告新增界面
     * @param model
     * @return
     */
    @RequestMapping(value="create",method=RequestMethod.GET)
    public String createForm(Model model){
        model.addAttribute("notice",new KnNotice());
        model.addAttribute("imgDefault",imgDefault);
        model.addAttribute("action","create");
        model.addAttribute("orgs",orgService.findAll());
        model.addAttribute("emps",orgService.ListEmployee());
        return "news/noticeForm";
    }

    /**
     * 保存公共信息
     * @param kac
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value="create",method=RequestMethod.POST)
    public String create(@Valid @ModelAttribute("notice") KnNotice kac,RedirectAttributes redirectAttributes,@RequestParam(value="_effectiveTime") String effectiveTime,ServletRequest request){
        String[] orgids = request.getParameterValues("orgList");
        String[] userIds = request.getParameterValues("empList") ;//!= null ? request.getParameter("empList").toString().split(",") : null;
        newsService.SaveKnNotice(kac,userIds,orgids,effectiveTime);
        redirectAttributes.addFlashAttribute("message","创建公告成功");
        return "redirect:/news/notice";
    }


    @RequestMapping(value="detail/{id}/{showType}/{already}/{need}",method=RequestMethod.GET)
    public String detail(@PathVariable("id") Long id,@PathVariable("showType") String showType,
            @PathVariable("already") String already,
            @PathVariable("need") String need,
            Model model,ServletRequest request){
        model.addAttribute("notice",newsService.ReadKnNotice(id));
        model.addAttribute("showType",showType);
        model.addAttribute("already",already);
        model.addAttribute("need",need);
        model.addAttribute("no",Integer.parseInt(need)-Integer.parseInt(already));
        return "news/noticeDetail";
    }


    /**
     * 分页查询
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value="listReceipt",method=RequestMethod.POST) @ResponseBody
    public  DataTable<KnNoticeReceipt> listReceipt(Model model,DataTable<KnNoticeReceipt> dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        searchParams.put("EQ_id",request.getParameter("id").toString());
        return newsService.PageKnNoticeReceipt(searchParams, dt);
    }
    /**
     * 删除公告
     * @param id
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "delete/{id}", method = RequestMethod.POST) @ResponseBody
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        newsService.DeleteKnNotice(id);
        redirectAttributes.addFlashAttribute("message", "删除公告成功");
        return "true";
    }


}