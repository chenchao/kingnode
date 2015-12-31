package com.kingnode.meeting.controller;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.google.common.base.Strings;
import com.kingnode.meeting.entity.KnMeetingBlacklist;
import com.kingnode.meeting.entity.KnMeetingEquipment;
import com.kingnode.meeting.entity.KnMeetingRoom;
import com.kingnode.meeting.entity.KnMeetingRule;
import com.kingnode.meeting.service.MeetingService;
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
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller @RequestMapping(value="/meeting")
public class MeetingController{
    @Autowired
    private MeetingService ms;
    /**
     * 黑名单加载首页
     */
    @RequestMapping(value="blacklist",method={RequestMethod.GET})
    public String blacklistIndex(){
        return "meeting/blacklist";
    }
    /**
     * 申请会议室会名单
     */
    @RequestMapping(value="blacklist",method={RequestMethod.POST}) @ResponseBody
    public DataTable blacklist(DataTable<KnMeetingBlacklist> dt){
        dt=ms.PageKnMeetingBlacklists(dt);
        return dt;
    }
    /**
     * 释放锁定人员或、锁定人员
     */
    @RequestMapping(value="updateBlacklist/{id}")
    public String updateBlacklist(@PathVariable(value="id") Long id,RedirectAttributes attributes){
        KnMeetingBlacklist blacklist=ms.ReadKnMeetingBlacklist(id);
        String msg;
        if(blacklist.getStatus().equals(KnMeetingBlacklist.MeetingStatus.LOCK)){
            blacklist.setStatus(KnMeetingBlacklist.MeetingStatus.UNLOCK);
            msg="成功释放用户:"+blacklist.getUserName()+"\n该用户可以预定会议室了";
        }else{
            blacklist.setStatus(KnMeetingBlacklist.MeetingStatus.LOCK);
            msg="成功锁定用户:"+blacklist.getUserName()+"\n该用户不能预定会议室了";
        }
        blacklist.setCreateTime(new Date().getTime());
        blacklist.setLockTime(new Date().getTime());
        ms.SaveKnMeetingBlacklist(blacklist);
        attributes.addFlashAttribute("message",msg);
        return "redirect:/meeting/blacklist";
    }
    /**
     * 跳转到会议室管理页面
     */
    @RequestMapping(value="room")
    public String meetingIndex(){
        return "meeting/meetingList";
    }
    /**
     * 加载会议室列表信息
     *
     * @param dt
     * @param lkName
     * @param status
     */
    @RequestMapping(value="meetingList") @ResponseBody
    public DataTable meetingList(DataTable<KnMeetingRoom> dt,@RequestParam(value="lkName",required=false) String lkName,@RequestParam(value="status",defaultValue="AVAILABLE") String status){
        if(!Strings.isNullOrEmpty(lkName)){
            lkName="%"+lkName+"%";
        }else{
            lkName="%%";
        }
        dt=ms.PageKnMeetingRoomByNameOrAddr(lkName,status,dt);
        return dt;
    }
    /**
     * 跳转会议室添加页面
     *
     * @param model
     * @param action
     * @param id
     *
     * @return
     */
    @RequestMapping(value="meetingForm",method=RequestMethod.GET)
    public String meetingForm(Model model,@RequestParam(value="action",required=false) String action,@RequestParam(value="id",required=false) Long id){
        KnMeetingRoom room;
        if(id!=null){
            room=ms.ReadKnMeetingRoom(id);
        }else{
            room=new KnMeetingRoom();
        }
        List<KnMeetingEquipment> equipmentList=ms.ReadKnMeetingEquipmentAll();
        model.addAttribute("equipmentList",equipmentList);
        model.addAttribute("room",room);
        model.addAttribute("action",action);
        return "meeting/meetingForm";
    }
    /**
     * 更新会议室方法
     *
     * @param model
     * @param id
     *
     * @return
     */
    @RequestMapping(value="UpdateMeeting/{id}",method=RequestMethod.GET)
    public String UpdateMeeting(Model model,@PathVariable(value="id") Long id){
        KnMeetingRoom room=ms.ReadKnMeetingRoom(id);
        List<KnMeetingEquipment> equipmentList=ms.ReadKnMeetingEquipmentAll();
        model.addAttribute("equipmentList",equipmentList);
        model.addAttribute("action","update");
        model.addAttribute("room",room);
        return "meeting/meetingForm";
    }
    /**
     * 设置会议室状态为不可用
     *
     * @param id
     *
     * @return
     */
    @RequestMapping(value="DeleteMeeting",method=RequestMethod.POST) @ResponseBody
    public boolean DeleteMeeting(@RequestParam(value="id") Long id){
        KnMeetingRoom room=ms.ReadKnMeetingRoom(id);
        room.setStatus(KnMeetingRoom.RoomStatus.DISABLED);
        ms.SaveKnMeetingRoom(room);
        return true;
    }
    /**
     * 创建会议室信息
     *
     * @param room
     * @param kmeId
     *
     * @return
     */
    @RequestMapping(value="SaveMeeting",method=RequestMethod.POST)
    public String SaveMeeting(KnMeetingRoom room,Long[] kmeId){
        if(room.getKme()!=null){
            room.getKme().clear();
        }else{
            room.setKme(new HashSet<KnMeetingEquipment>());
        }
        for(Long id : kmeId){
            room.getKme().add(new KnMeetingEquipment(id));
        }
        room.setCreateTime(new Date().getTime());
        room.setUpdateTime(new Date().getTime());
        ms.SaveKnMeetingRoom(room);
        return "redirect:/meeting/room";
    }
    /**
     * 跳转到会议室使用规则页面
     */
    @RequestMapping(value="rule")
    public String ruleIndex(Model model){
        KnMeetingRule ru=ms.ReadKnMeetingRule();
        model.addAttribute("rule",ru);
        return "meeting/meetingRule";
    }
    /**
     * 创建会议室使用规则
     */
    @RequestMapping(value="SaveRule",method=RequestMethod.POST)
    public String SaveRule(@ModelAttribute("rule") KnMeetingRule rule,RedirectAttributes attributes){
        KnMeetingRule ru=ms.SaveKnMeetingRule(rule);
        attributes.addFlashAttribute("message","创建会议室使用规则成功.");
        attributes.addFlashAttribute("rule",ru);
        return "redirect:/meeting/rule";
    }
    /**
     * 加载会议室设备首页
     */
    @RequestMapping(value="equipment")
    public String equipmentIndex(){
        return "meeting/meetingEquipmentList";
    }
    /**
     * 会议室设备列表信息
     *
     * @param dt
     *
     * @return
     */
    @RequestMapping(value="equipmentList") @ResponseBody
    public DataTable equipmentList(DataTable<KnMeetingEquipment> dt,@RequestParam(value="lkName", required=false) String lkName){
        if(!Strings.isNullOrEmpty(lkName)){
            lkName="%"+lkName+"%";
        }else{
            lkName="%%";
        }
        dt=ms.PageKnMeetingEquipment(lkName,dt);
        return dt;
    }
    /**
     * 设置会议室状态为不可用
     *
     * @param id
     *
     * @return
     */
    @RequestMapping(value="DeleteEquipment", method=RequestMethod.POST) @ResponseBody
    public boolean DeleteEquipment(@RequestParam(value="id") Long id){
        KnMeetingEquipment equipment=ms.ReadKnMeetingEquipment(id);
        equipment.setStatus(KnMeetingEquipment.EquipmentStatus.DISABLED);
        ms.SaveKnMeetingEquipment(equipment);
        return true;
    }

    @RequestMapping(value="equipmentForm",method=RequestMethod.GET)
    public String equipmentForm(Model model,@RequestParam(value="action",required=false) String action,@RequestParam(value="id",required=false) Long id){
        KnMeetingEquipment equipment;
        if(id!=null){
            equipment=ms.ReadKnMeetingEquipment(id);
        }else{
            equipment=new KnMeetingEquipment();
        }
        model.addAttribute("equipment",equipment);
        model.addAttribute("action",action);
        return "meeting/meetingEquipmentForm";
    }

    @RequestMapping(value="SaveEquipment",method=RequestMethod.POST)
    public String SaveEquipment(KnMeetingEquipment equipment){
        equipment.setCreateTime(new Date().getTime());
        equipment.setUpdateTime(new Date().getTime());
        ms.SaveKnMeetingEquipment(equipment);
        return "redirect:/meeting/equipment";
    }
}
