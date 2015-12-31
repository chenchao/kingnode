package com.kingnode.xsimple.controller.platform;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;

import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.entity.system.KnFeedProblem;
import com.kingnode.xsimple.entity.system.KnIdeaFeedback;
import com.kingnode.xsimple.service.system.IdeaFeedbackService;
import com.kingnode.xsimple.util.Utils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller @RequestMapping(value="platform")
public class FeedbackController{
    @Autowired
    private IdeaFeedbackService fb;
    /**
     * 跳转意见反馈页面
     * @param model
     * @return
     */
    @RequiresPermissions("platform-feedback")
    @RequestMapping(value="opinion-feedback")
    public String home(Model model){
        model.addAttribute("problemType",Setting.FeedProblenType.problem.toString());
        model.addAttribute("answerType",Setting.FeedProblenType.answer.toString());
        return "/platform/feedback";
    }
    /**
     * 意见反馈列表
     * @param request
     * @param dt
     * @return
     */
    @RequiresPermissions("platform-feedback")
    @RequestMapping(value="feedback/list") @ResponseBody
    public DataTable<KnIdeaFeedback> list(ServletRequest request,DataTable<KnIdeaFeedback> dt){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        dt=fb.PageIdeaFeedbacks(searchParams,dt);
        return dt;
    }
    /**
     * 删除反馈信息
     * @param id
     * @return
     */
    @RequiresPermissions("platform-feedback")
    @RequestMapping(value="feedback/delete") @ResponseBody
    public boolean delete(@RequestParam(value="id") Long id){
        fb.Delete(id);
        return true;
    }
    /**
     * 批量删除反馈信息
     * @param ids
     * @return
     */
    @RequiresPermissions("platform-feedback")
    @RequestMapping(value="feedback/delete-all") @ResponseBody
    public boolean deleteAll(@RequestParam(value="ids") Long[] ids){
        fb.DeleteAll(ids);
        return true;
    }
    @RequestMapping(value="feedprob/list") @ResponseBody
    public DataTable<KnFeedProblem> listProb(ServletRequest request,DataTable<KnFeedProblem> dt){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        dt=fb.PageFeedProblem(searchParams,dt);
        return dt;
    }

    @RequestMapping(value="/save-edit",method=RequestMethod.POST) @ResponseBody
    public Map saveProblem(@RequestParam(value="id") String id,@RequestParam(value="probContext") String probContext,@RequestParam(value="feedProblenType") String feedProblenType){
        Map map=new HashMap();
        KnFeedProblem knFeedProblem=null;
        if(!Utils.isEmptyStr(id)){
            //编辑
            knFeedProblem=fb.FindoneKnFeedProblem(Long.parseLong(id));
            knFeedProblem.setProbContext(probContext);
        } else{
            if(Setting.FeedProblenType.problem.toString().equals(feedProblenType)){
                List list = fb.FindListByType(Setting.FeedProblenType.problem) ;
                if(!Utils.isEmpityCollection(list)){
                    map.put("stat",false);
                    return map ;
                }
            }
            knFeedProblem = new KnFeedProblem(probContext,Setting.FeedProblenType.valueOf(feedProblenType)) ;
        }
        fb.SaveKnFeedProblem(knFeedProblem);
        map.put("id",knFeedProblem.getId()) ;
        map.put("stat",true);
        return map;
    }

    @RequestMapping(value="del-prob",method=RequestMethod.POST) @ResponseBody
    public Map deleteProblem(@RequestParam("ids") List<Long> ids ){
        Map map=new HashMap();
        try{
            fb.DeleteProblem(ids);
            map.put("stat",true);
        }catch(Exception e){
            map.put("stat",false);
        }
        return map;
    }
}
