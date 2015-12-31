package com.kingnode.social.controller;
import java.util.Map;
import javax.servlet.ServletRequest;

import com.kingnode.diva.web.Servlets;
import com.kingnode.social.entity.KnSocialBlog;
import com.kingnode.social.entity.KnSocialBlogComment;
import com.kingnode.social.entity.KnSocialBlogSettings;
import com.kingnode.social.service.KnSocialBlogService;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.entity.IdEntity;
import org.springframework.beans.factory.annotation.Autowired;
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
@Controller @RequestMapping(value="/social")
public class KnSocialBlogController{
    @Autowired
    private KnSocialBlogService socialBlogService;
    @RequestMapping(value="index",method=RequestMethod.GET)
    public String index(){
        return "/forum/socialBlogList";
    }
    @RequestMapping(value="list",method=RequestMethod.POST) @ResponseBody
    public DataTable<KnSocialBlog> list(DataTable<KnSocialBlog> dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        dt=socialBlogService.PageKnSocialBlog(searchParams,dt);
        return dt;
    }
    @RequestMapping(value="/shield/{id}") @ResponseBody
    public boolean shield(@PathVariable("id") Long id){
        KnSocialBlog blog=socialBlogService.ReadKnSocialBlog(id);
        if(blog.getActive().equals(IdEntity.ActiveType.ENABLE)){
            blog.setActive(IdEntity.ActiveType.DISABLE);
        }else{
            blog.setActive(IdEntity.ActiveType.ENABLE);
        }
        socialBlogService.SaveKnSocialBlog(blog);
        return true;
    }
    @RequestMapping(value="/comment/index/{mId}", method=RequestMethod.GET)
    public String commentIndex(Model model,@PathVariable("mId") Long mId){
        model.addAttribute("mId",mId);
        return "/forum/socialBlogCommentList";
    }
    @RequestMapping(value="/comment/list/{mId}", method=RequestMethod.POST) @ResponseBody
    public DataTable<KnSocialBlogComment> commentList(DataTable<KnSocialBlogComment> dt,@PathVariable("mId") Long mId){
        dt=socialBlogService.PageKnSocialBlogComment(mId,dt);
        return dt;
    }
    @RequestMapping(value="/comment/shield/{id}") @ResponseBody
    public boolean commentShield(@PathVariable("id") Long id){
        KnSocialBlogComment comment=socialBlogService.ReadKnSocialBlogComment(id);
        if(comment.getActive().equals(IdEntity.ActiveType.ENABLE)){
            comment.setActive(IdEntity.ActiveType.DISABLE);
        }else{
            comment.setActive(IdEntity.ActiveType.ENABLE);
        }
        socialBlogService.SaveKnSocialBlogComment(comment);
        return true;
    }
    /**
     * 跳转到论坛邮箱设置页面
     */
    @RequestMapping(value="/settings/{type}")
    public String SettingsIndex(Model model,@PathVariable("type") String type){
        KnSocialBlogSettings.EmailType emailType=null;
        switch(type){
        case "MAILBOX":
            emailType=KnSocialBlogSettings.EmailType.MAILBOX;
            break;
        case "ADVICE":
            emailType=KnSocialBlogSettings.EmailType.ADVICE;
            break;
        case "CONTRIBUTE":
            emailType=KnSocialBlogSettings.EmailType.CONTRIBUTE;
            break;
        }
        KnSocialBlogSettings settings=socialBlogService.ReadKnSocialBlogSettingsByType(emailType);
        if(settings==null){
            settings=new KnSocialBlogSettings();
            settings.setType(emailType);
        }
        model.addAttribute("settings",settings);
        model.addAttribute("action",type);
        return "/forum/settingsForm";
    }
    /**
     * @param settings
     *
     * @return
     */
    @RequestMapping(value="/saveSettings")
    public String SaveSettings(KnSocialBlogSettings settings,RedirectAttributes attributes){
        settings.setCreateTime(System.currentTimeMillis());
        socialBlogService.SaveKnSocialBlogSettings(settings);
        attributes.addFlashAttribute("message","保存成功");
        return "redirect:/social/settings/"+settings.getType().toString();
    }
}
