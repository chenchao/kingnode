package com.kingnode.meeting.controller;
import java.util.Map;
import javax.servlet.ServletRequest;

import com.kingnode.diva.web.Servlets;
import com.kingnode.meeting.service.MeetingService;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.entity.meeting.KnRegisterInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller @RequestMapping(value="/meet")
public class MeetController{
    @Autowired
    private MeetingService meetService;
    @RequestMapping(value="list",method=RequestMethod.GET)
    public String list(Model model){
        model.addAttribute("listData",meetService.ListMeetingInfos());
        return "meet/participantsInfo";
    }
    @RequestMapping(value="attendees") @ResponseBody
    public DataTable attendees(DataTable<KnRegisterInfo> dt,@RequestParam(value="theme",required=false) String theme,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        String name=null;
        String sex=null;
        String phone=null;
        for(String key : searchParams.keySet()){
            if(key.indexOf("name")>0){
                name=(String)searchParams.get(key);
            }
            if(key.indexOf("sex")>0){
                sex=(String)searchParams.get(key);
            }
            if(key.indexOf("phone")>0){
                phone=(String)searchParams.get(key);
            }
        }
        if(theme==null||theme.equals("-1")){
            dt=meetService.PageRegisterInfos(searchParams,dt);
        }else{
            dt=meetService.PageAttendees(theme,name,sex,phone,dt);
        }
        return dt;
    }
    @RequestMapping(value="createForm")
    public String createForm(){
        return "meet/attendeesForm";
    }
    @RequestMapping(value="create")
    public String create(KnRegisterInfo registerInfo,RedirectAttributes redirectAttributes){
        meetService.SaveRegisterInfo(registerInfo);
        redirectAttributes.addFlashAttribute("message","创建参会人员成功");
        return "redirect:/meet/list";
    }
    @RequestMapping(value="delete/{id}")
    public String delete(@PathVariable(value="id") Long id,RedirectAttributes redirectAttributes){
        meetService.DeleteRegisterInfo(id);
        redirectAttributes.addFlashAttribute("message","删除参会人员成功");
        return "redirect:/meet/list";
    }
    @RequestMapping(value="updateForm/{id}")
    public String updateForm(@PathVariable(value="id") Long id,Model model){
        model.addAttribute("registerInfo",meetService.FindRegisterInfoById(id));
        return "meet/attendeesForm";
    }
}
