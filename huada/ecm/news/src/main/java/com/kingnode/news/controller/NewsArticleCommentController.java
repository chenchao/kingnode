package com.kingnode.news.controller;
import java.util.Map;
import javax.servlet.ServletRequest;

import com.kingnode.diva.web.Servlets;
import com.kingnode.news.entity.KnArticleComment;
import com.kingnode.news.service.NewsService;
import com.kingnode.xsimple.api.common.DataTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 * 新闻评论
 */
@Controller
@RequestMapping(value="/news/comment")
public class NewsArticleCommentController{
    @Autowired
    private NewsService newsService;

    /**
     * 新闻分类管理首页
     *
     * @return jsp文件
     */
    @RequestMapping(method=RequestMethod.GET)
    public String home(Model model){
        return "news/commentList";
    }

    /**
     * 屏蔽 取消屏蔽
     *
     * @return jsp文件
     */
    @RequestMapping(value="toshow/{id}", method=RequestMethod.GET)@ResponseBody
    public String toShow(@RequestParam(value="id", required=false) Long id,Model model){
        return newsService.ShowComment(id)+"";
    }

    /**
     * 查询评论
     *
     * @return jsp文件
     */
    @RequestMapping(method=RequestMethod.POST)
    @ResponseBody
    public DataTable<KnArticleComment> list(DataTable<KnArticleComment> dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return newsService.PageKnArticleComment(searchParams,dt);
    }

    /**
     * 删除评论信息
     */
    @RequestMapping(value="delete", method=RequestMethod.POST)
    @ResponseBody
    public String delete(@RequestParam("ids") String[] ids,RedirectAttributes redirectAttributes,ServletRequest request){
        newsService.deleteComment(ids);
        redirectAttributes.addFlashAttribute("message","删除评论信息成功");
        return "true";
    }


}
