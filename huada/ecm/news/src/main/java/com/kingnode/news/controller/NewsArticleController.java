package com.kingnode.news.controller;

import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.diva.web.Servlets;
import com.kingnode.news.entity.KnArticle;
import com.kingnode.news.service.NewsService;
import com.kingnode.xsimple.api.common.DataTable;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 新闻内容管理
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller
@RequestMapping(value="/news/article")
public class NewsArticleController{
    @Autowired
    private NewsService newsService;

    /**
     * 新闻分类管理首页
     *
     * @return jsp文件
     */
    @RequestMapping(method=RequestMethod.GET)
    public String home(Model model){
        return "news/articleList";
    }

    /**
     * 新闻分类管理首页
     *
     * @return jsp文件
     */
    @RequestMapping(method=RequestMethod.POST)
    @ResponseBody
    public DataTable<KnArticle> list(@RequestParam(value="getPubDate", required=false) String getPubDate,@RequestParam(value="letPubDate", required=false) String letPubDate,DataTable<KnArticle> dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return newsService.PageKnArticle(searchParams,dt,getPubDate,letPubDate);
    }

    /**
     * 进入新闻分类编辑页面
     */
    @RequestMapping(value="create", method=RequestMethod.GET)
    public String createForm(Model model){
        KnArticle ka=new KnArticle();
        //        ka.setIsPublication(true);
        //        ka.setAuthor(Users.name());
        model.addAttribute("article",ka);
        model.addAttribute("categorys",newsService.ListKnArticleCategory(0L));
        model.addAttribute("action","create");

        return "news/articleForm";
    }

    @RequestMapping(value="create", method=RequestMethod.POST)
    public String create(@Valid KnArticle ka,@RequestParam("_effectiveTime") String effectiveTime,RedirectAttributes redirectAttributes){
        ka.setHits(0L);
        ka.setIsTop(false);
        ka.setComments(0l);
        ka.setPubDate(newsService.FindDate(new Date(),"yyyy-MM-dd").getTime());
        newsService.SaveKnArticle(ka,effectiveTime);
        redirectAttributes.addFlashAttribute("message","创建新闻内容成功");
        return "redirect:/news/article";
    }

    @RequestMapping(value="update/{id}", method=RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id,Model model){
        model.addAttribute("article",newsService.ReadKnArticle(id));
        model.addAttribute("categorys",newsService.ListKnArticleCategory(0L));
        model.addAttribute("action","update");
        return "news/articleForm";
    }

    @RequestMapping(value="update", method=RequestMethod.POST)
    public String update(@Valid @ModelAttribute("article") KnArticle ka,@RequestParam("_effectiveTime") String effectiveTime,RedirectAttributes redirectAttributes){
        newsService.SaveKnArticle(ka,effectiveTime);
        redirectAttributes.addFlashAttribute("message","更新新闻内容成功");
        return "redirect:/news/article";
    }

    @RequestMapping(value="delete/{id}", method=RequestMethod.POST)
    @ResponseBody
    public String delete(@PathVariable("id") Long id,RedirectAttributes redirectAttributes){
        newsService.DeleteKnArticle(id);
        redirectAttributes.addFlashAttribute("message","删除新闻内容成功");
        return "true";
    }

    @RequestMapping(value="publication", method=RequestMethod.POST)
    @ResponseBody
    public String publication(@RequestParam(value="ids") List<Long> ids,@RequestParam(value="pub") boolean pub){
        newsService.PublicationArticle(ids,pub);
        return "true";
    }

    @RequestMapping(value="topic/{id}", method=RequestMethod.POST)
    @ResponseBody
    public String topic(@PathVariable(value="id") Long id){
        newsService.TopicArticle(id);
        return "true";
    }

    /**
     * 新闻详情
     */
    @RequestMapping(value="detail/{id}/{showType}", method=RequestMethod.GET)
    public String detail(@PathVariable("id") Long id,@PathVariable("showType") String showType,Model model){
        model.addAttribute("article",newsService.ReadKnArticle(id));
        model.addAttribute("topic",newsService.QueryNewestTopArticle());
        model.addAttribute("showType",showType);
        return "news/articleDetail";
    }

    @RequestMapping(value="validatetop/{id}", method=RequestMethod.GET)
    @ResponseBody
    public String validate(@PathVariable("id") Long id){
        KnArticle cle=newsService.QueryTopArticle(id);
        String result=JsonMapper.nonDefaultMapper().toJson(cle);
        return result;
    }

    @ModelAttribute
    public void ReadKnArticleCategory(@RequestParam(value="id", defaultValue="-1") Long id,Model model){
        if(id!=-1){
            model.addAttribute("article",newsService.ReadKnArticle(id));
        }
    }
}
