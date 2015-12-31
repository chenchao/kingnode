package com.kingnode.news.controller;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.validation.Valid;

import com.kingnode.diva.web.Servlets;
import com.kingnode.news.entity.KnArticleCategory;
import com.kingnode.news.service.NewsService;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.SortTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
/**
 * 新闻分类管理
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller @RequestMapping(value="/news/category")
public class NewsCategoryController{
    @Autowired
    private NewsService newsService;
    /**
     * 新闻分类管理首页
     *
     * @return jsp文件
     */
    @RequestMapping(method=RequestMethod.GET)
    public String list(@RequestParam(value="page", defaultValue="1") int pageNumber,@RequestParam(value="page.size", defaultValue=Setting.PAGE_SIZE) int pageSize,@RequestParam(value="sortType", defaultValue="auto") String sortType,Model model,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        Page<KnArticleCategory> categorys=newsService.PageKnArticleCategory(searchParams,pageNumber,pageSize,sortType);
        model.addAttribute("categorys",categorys);
        model.addAttribute("sortType",sortType);
        model.addAttribute("sortTypes",SortTypes.Get());
        // 将搜索条件编码成字符串，用于排序，分页的URL
        model.addAttribute("searchParams",Servlets.encodeParameterStringWithPrefix(searchParams,"search_"));
        return "news/categoryList";
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
        model.addAttribute("category",new KnArticleCategory());
        model.addAttribute("categorys",newsService.ListKnArticleCategory(0L));
        model.addAttribute("action","create");
        return "news/categoryForm";
    }
    @RequestMapping(value="create", method=RequestMethod.POST)
    public String create(@Valid KnArticleCategory kac,RedirectAttributes redirectAttributes){
        newsService.SaveKnArticleCategory(kac);
        redirectAttributes.addFlashAttribute("message","创建分类成功");
        return "redirect:/news/category";
    }
    @RequestMapping(value="update/{id}", method=RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id,Model model){
        model.addAttribute("category",newsService.ReadKnArticleCategory(id));
        model.addAttribute("categorys",newsService.ListKnArticleCategory(0L));
        model.addAttribute("action","update");
        return "news/categoryForm";
    }
    @RequestMapping(value="update", method=RequestMethod.POST)
    public String update(@Valid @ModelAttribute("category") KnArticleCategory kac,RedirectAttributes redirectAttributes){
        newsService.SaveKnArticleCategory(kac);
        redirectAttributes.addFlashAttribute("message","更新分类成功");
        return "redirect:/news/category";
    }
    @RequestMapping(value="delete/{id}")
    public String delete(@PathVariable("id") Long id,RedirectAttributes redirectAttributes){
        newsService.DeleteKnArticleCategory(id);
        redirectAttributes.addFlashAttribute("message","删除分类成功");
        return "redirect:/news/category";
    }
    @ModelAttribute
    public void ReadKnArticleCategory(@RequestParam(value="id", defaultValue="-1") Long id,Model model){
        if(id!=-1){
            model.addAttribute("category",newsService.ReadKnArticleCategory(id));
        }
    }
}
