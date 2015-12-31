package com.kingnode.health.controller;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;

import com.google.common.collect.Lists;
import com.kingnode.diva.web.Servlets;
import com.kingnode.health.entity.HealthArrange;
import com.kingnode.health.entity.KnEcmHealth;
import com.kingnode.health.entity.KnEcmHealthIcon;
import com.kingnode.health.entity.KnEcmHealthNumber;
import com.kingnode.health.entity.KnEcmHealthSet;
import com.kingnode.health.entity.KnEcmHealthVacation;
import com.kingnode.health.service.HealthService;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.entity.IdEntity;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.LoggerFactory;
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
@Controller @RequestMapping(value="/health") public class HealthController{
    private static org.slf4j.Logger log=LoggerFactory.getLogger(HealthService.class);
    @Autowired private HealthService hs;
    /**
     * 进入预约或候诊列表页面 status取值:appointment,预约列表;waiting,候诊列表;history,历史列表
     */
    @RequestMapping(value="/{status}", method=RequestMethod.GET) public String list(Model model,@PathVariable(value="status") String status){
        String returnURL="health/historyList";
        if("appointment".equals(status)){
            returnURL="health/appointmentList";
        }
        if("waiting".equals(status)){
            returnURL="health/waitingList";
        }
        model.addAttribute("status",status);
        return returnURL;
    }
    /**
     * 预约或候诊列表分页显示数据
     */
    @RequestMapping(value="/{status}", method=RequestMethod.POST) @ResponseBody
    public DataTable<KnEcmHealth> list(DataTable<KnEcmHealth> dt,ServletRequest request,@PathVariable(value="status") String status){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        dt=hs.PageKnEcmHealth(dt,searchParams);
        return dt;
    }
    /**
     * 进入就诊页面
     */
    @RequestMapping(value="diagnose/{id}", method=RequestMethod.GET) public String diagnoseForm(Model model,@PathVariable(value="id") Long id){
        KnEcmHealth health=hs.ReadKnEcmHealth(id);
        health.setDiagnoseDate(DateTime.now().getMillis());
        hs.SetOrgName(health);
        model.addAttribute("action","diagnose");
        model.addAttribute("h",health);
        return "health/diagnoseForm";
    }
    /**
     * 预约视图
     */
    @RequestMapping(value="view/{id}", method=RequestMethod.GET) public String viewForm(Model model,@PathVariable(value="id") Long id){
        KnEcmHealth health=hs.ReadKnEcmHealth(id);
        hs.SetOrgName(health);
        model.addAttribute("action","view");
        model.addAttribute("h",health);
        return "health/diagnoseForm";
    }
    /**
     * 保存就诊信息
     */
    @RequestMapping(value="diagnose", method=RequestMethod.POST)
    public String diagnose(KnEcmHealth health,RedirectAttributes redirectAttributes,Integer arrangeType,String arrangeCode){
        KnEcmHealth knEcmHealth=hs.ReadKnEcmHealth(health.getId());
        //判断就诊时间是否有改变
        if(!health.getWaitingTime().equals(knEcmHealth.getWaitingTime())){
            DateTimeFormatter format=DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
            knEcmHealth.setWaitingDate(DateTime.parse(health.getWaitingTime(),format).getMillis());
            //knEcmHealth.setArrangeFlag(1);//将该记录变为非常规排序
            hs.SaveKnEcmHealth(knEcmHealth);
            //获取对应的排序对象并修改
            KnEcmHealthNumber healthNumber=hs.ReadHealthNumberByHealthId(knEcmHealth.getId());
            healthNumber.setStart(knEcmHealth.getWaitingDate());
            healthNumber.setActive(IdEntity.ActiveType.ENABLE);
            //判断修改后不是是在同一天
            if(!dateInOneDay(healthNumber.getStart(),knEcmHealth.getWaitingDate())){
                healthNumber.setDay(new DateTime(healthNumber.getStart()).toString("yyyy-MM-dd"));
                Integer max=hs.MaxNumberWithDay(healthNumber.getDay())+1;
                healthNumber.setNumber(max);
                healthNumber.setCode("D"+new DateTime(healthNumber.getStart()).toString("yyyyMMdd")+"-"+getFoot(max));
            }
            //判断排号类型是常规还是插入
            KnEcmHealthNumber lastNumber=hs.LastHealthNumber();
            if(lastNumber.getStart()<knEcmHealth.getWaitingDate()){
                healthNumber.setType(2);
            }else{
                healthNumber.setType(1);
            }
            //保存排号对象
            hs.SaveKnEcmHealthNumber(healthNumber);
            //消息推送
            try{
                List<KnEcmHealth> list=Lists.newArrayList();
                list.add(knEcmHealth);
                hs.pushWaitingMessage(list,3);
            }catch(ParseException e){
                log.info("消息推送失败!");
                e.printStackTrace();
            }
            redirectAttributes.addFlashAttribute("message","修改就诊时间成功!");
        }else{
            //诊断操作
            knEcmHealth.setStatus(KnEcmHealth.HealthStatus.DIAGNOSE);//诊断状态
            knEcmHealth.setDiagnoseCost(health.getDiagnoseCost());//诊断费用
            knEcmHealth.setDiagnoseResult(health.getDiagnoseResult());//诊断结果
            DateTimeFormatter format=DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
            knEcmHealth.setDiagnoseDate(DateTime.parse(health.getDiagnoseTime(),format).getMillis());//诊断时间
            if(StringUtils.isNotBlank(health.getConsultationTime())){
                knEcmHealth.setConsultationDate(DateTime.parse(health.getConsultationTime(),format).getMillis());//复诊时间
            }
            hs.SaveKnEcmHealth(knEcmHealth);
            //设置排号对象为无效
            KnEcmHealthNumber oldNumber=hs.ReadHealthNumberByHealthId(knEcmHealth.getId());
            oldNumber.setActive(IdEntity.ActiveType.DISABLE);
            hs.SaveKnEcmHealthNumber(oldNumber);
            //有复诊的情况，产生一条新的候诊记录
            if(StringUtils.isNotBlank(health.getConsultationTime())){
                KnEcmHealth newHealth=new KnEcmHealth();
                newHealth.setWaitingDate(knEcmHealth.getConsultationDate());//新纪录候诊时间
                newHealth.setWaitingNumber(arrangeCode);//新纪录候诊号
                newHealth.setConsultationFlag(1);//标记为复诊
                newHealth.setStatus(KnEcmHealth.HealthStatus.WAITING);//新纪录为候诊状态
                newHealth.setPhone(knEcmHealth.getPhone());
                newHealth.setEmployee(knEcmHealth.getEmployee());
                //附件信息
                List<KnEcmHealthIcon> oldIcons=knEcmHealth.getHealthIcons();
                if(oldIcons!=null&&oldIcons.size()>0){
                    List<KnEcmHealthIcon> newIcons=new ArrayList<>();
                    for(KnEcmHealthIcon oldIcon : oldIcons){
                        KnEcmHealthIcon newIcon=new KnEcmHealthIcon();
                        newIcon.setIcon(oldIcon.getIcon());
                        newIcons.add(newIcon);
                    }
                    newHealth.setHealthIcons(newIcons);
                }
                newHealth.setDescription(knEcmHealth.getDescription());
                newHealth.setDoctor(knEcmHealth.getDoctor());
                newHealth.setDoctorPhone(knEcmHealth.getDoctorPhone());
                newHealth.setProposerDate(knEcmHealth.getProposerDate());
                hs.SaveKnEcmHealth(newHealth);
                //生成一条新的排号记录,并保存
                KnEcmHealthNumber newNumber=new KnEcmHealthNumber();
                newNumber.setDay(new DateTime(newHealth.getWaitingDate()).toString("yyyy-MM-dd"));
                newNumber.setStart(newHealth.getWaitingDate());
                newNumber.setActive(IdEntity.ActiveType.ENABLE);
                newNumber.setCode(arrangeCode);
                newNumber.setType(arrangeType);
                String number=arrangeCode.substring(arrangeCode.lastIndexOf("-")+1);
                newNumber.setNumber(Integer.parseInt(number));
                newNumber.setHealthId(newHealth.getId());
                hs.SaveKnEcmHealthNumber(newNumber);
                //消息推送
                try{
                    List<KnEcmHealth> list=Lists.newArrayList();
                    list.add(newHealth);
                    hs.pushWaitingMessage(list,2);
                }catch(ParseException e){
                    log.info("消息推送失败!");
                    e.printStackTrace();
                }
            }
            redirectAttributes.addFlashAttribute("message","就诊完成!");
        }
        return "redirect:/health/waiting";
    }
    /**
     * 判断俩个日期是否在同一天
     *
     * @param oneDate 日期1
     * @param twoDate 日期2
     *
     * @return boolean
     */
    private boolean dateInOneDay(long oneDate,long twoDate){
        if(new DateTime(oneDate).toString("yyyy-MM-dd").equals(new DateTime(twoDate).toString("yyyy-MM-dd"))){
            return true;
        }
        return false;
    }
    /**
     * 常规排号
     */
    @RequestMapping(value="arrange", method=RequestMethod.POST) @ResponseBody public String arrange(@RequestParam("ids") List<Long> ids){
        KnEcmHealthSet set=hs.ReadKnEcmHealthSet();
        if(set==null){
            return "{\"result\":\"init\",\"count\":"+0+"}";
        }
        List<KnEcmHealth> healthList=hs.ListKnEcmHealth(ids);
        KnEcmHealthNumber lastNumber=hs.LastHealthNumber();
        long last=DateTime.now().getMillis();
        if(lastNumber!=null&&lastNumber.getStart()>last){
            last=lastNumber.getStart();
        }
        HealthArrange arrange=hs.InitArrange(set,last);
        List<KnEcmHealthNumber> numberList=Lists.newArrayList();
        for(KnEcmHealth health : healthList){
            KnEcmHealthNumber number=arrange.next();
            health.setWaitingNumber(number.getCode());
            health.setWaitingDate(number.getStart());
            health.setStatus(KnEcmHealth.HealthStatus.WAITING);
            health.setConsultationFlag(0);//复诊标记
            number.setHealthId(health.getId());
            number.setType(1);
            numberList.add(number);
        }
        hs.SaveKnEcmHealth(healthList);
        hs.SaveKnEcmHealthNumber(numberList);
        try{
            hs.pushWaitingMessage(healthList,1);
        }catch(ParseException e){
            log.info("消息推送失败!");
            e.printStackTrace();
        }
        return "{\"result\":\"success\",\"count\":"+healthList.size()+"}";
    }
    /**
     * 进入设置页面
     */
    @RequestMapping(value="settings", method=RequestMethod.GET) public String settingsForm(Model model){
        KnEcmHealthSet set=hs.ReadKnEcmHealthSet();
        model.addAttribute("action","settings");
        model.addAttribute("s",set);
        return "health/settingsForm";
    }
    /**
     * 保存设置信息
     */
    @RequestMapping(value="settings", method=RequestMethod.POST)
    public String setting(KnEcmHealthSet healthSet,RedirectAttributes redirectAttributes,Integer actionStart,Integer flagTag){
        if(actionStart!=null&&actionStart>0){
            healthSet.setAction(actionStart);
        }else{
            healthSet.setAction(8*60);
        }
        if(flagTag!=null&&flagTag>0){
            healthSet.setFlag(flagTag);
        }else{
            healthSet.setFlag(1);
        }
        //字符串去控股
        healthSet.setAmStartDate(healthSet.getAmStartDate().trim());
        healthSet.setAmEndDate(healthSet.getAmEndDate().trim());
        if(StringUtils.isNotBlank(healthSet.getPmStartDate())){
            healthSet.setPmStartDate(healthSet.getPmStartDate().trim());
        }
        if(StringUtils.isNotBlank(healthSet.getPmEndDate())){
            healthSet.setPmEndDate(healthSet.getPmEndDate().trim());
        }
        healthSet.setDoctor(healthSet.getDoctor().trim());
        healthSet.setDoctorPhone(healthSet.getDoctorPhone().trim());
        hs.SaveKnEcmHealthSet(healthSet);
        redirectAttributes.addFlashAttribute("message","设置完成!");
        return "redirect:/health/settings";
    }
    /**
     * 预约或候诊列表分页显示数据
     */
    @RequestMapping(value="/vacation", method=RequestMethod.POST) @ResponseBody
    public DataTable<KnEcmHealthVacation> vacation(DataTable<KnEcmHealthVacation> dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        dt=hs.PageKnEcmHealthVacation(dt,searchParams);
        return dt;
    }
    /**
     * 新增休假日期
     */
    @RequestMapping(value="add-vacation", method=RequestMethod.POST) @ResponseBody public String addVacation(@RequestParam("vacation") String vacation){
        DateTimeFormatter format=DateTimeFormat.forPattern("yyyy-MM-dd");
        Long date=DateTime.parse(vacation.trim(),format).getMillis();
        //判断休假日期是否已存在
        KnEcmHealthVacation healthVacation=hs.ReadKnEcmHealthVacationEqDate(date);
        String result="failure";
        if(healthVacation!=null){
            result="exists";
        }else{
            healthVacation=new KnEcmHealthVacation();
            healthVacation.setVacation(date);
            healthVacation.setType(1);
            healthVacation.setActive(IdEntity.ActiveType.ENABLE);
            hs.SaveKnEcmHealthVacation(healthVacation);
            if(healthVacation.getId()>0){
                result="success";
            }
        }
        return "{\"result\":\""+result+"\"}";
    }
    /**
     * 删除休假日期
     */
    @RequestMapping(value="del-vacation", method=RequestMethod.POST) @ResponseBody public String delVacation(@RequestParam("id") Long id){
        String result="success";
        hs.DeleteKnEcmHealthVacation(id);
        KnEcmHealthVacation healthVacation=hs.ReadKnEcmHealthVacation(id);
        if(healthVacation!=null){
            result="failure";
        }
        return "{\"result\":\""+result+"\"}";
    }
    /**
     * 复诊为俩周以后
     */
    @RequestMapping(value="consultation-two-weeks", method=RequestMethod.POST) @ResponseBody public String consultationTwoWeeks(){
        return createHeathNumber(3,"");
    }
    /**
     * 复诊排号
     *
     * @param consultationDate       搜索日期
     * @param consultationDateBefore 指定的排号日期
     * @param consultationDateAfter  指定的排号时间
     *
     * @return String 排号完成的json格式字符串
     */
    @RequestMapping(value="consultation-arrange", method=RequestMethod.POST) @ResponseBody
    public String consultationArrange(@RequestParam("consultationDate") String consultationDate,@RequestParam("consultationDateBefore") String consultationDateBefore,@RequestParam("consultationDateAfter") String consultationDateAfter){
        String result="";
        if(StringUtils.isNotBlank(consultationDateAfter)){
            result=createHeathNumber(1,consultationDateBefore.trim()+" "+consultationDateAfter.trim());
        }else if(StringUtils.isNotBlank(consultationDateBefore)){
            result=createHeathNumber(2,consultationDateBefore.trim());
        }else if(StringUtils.isNotBlank(consultationDate)){
            result=createHeathNumber(2,consultationDate.trim());
        }else{
            result=createHeathNumber(4,"");
        }
        return result;
    }
    /**
     * 创建候诊号并将结果组装为json格式
     *
     * @param type 类型 1.表示指定了时间的排号  2.表示指定了日期的排号 3.表示指定日期为2周后的排号 4.不指定日期和时间的排号
     * @param date 指定的日期和时间  格式：yyyy-MM-dd HH:mm
     *
     * @return String 返回页面的json对象
     */
    private String createHeathNumber(int type,String date){
        String result="success";
        KnEcmHealthNumber healthNumber=new KnEcmHealthNumber();
        DateTimeFormatter format=DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
        KnEcmHealthNumber lastNumber=hs.LastHealthNumber();
        KnEcmHealthSet set=hs.ReadKnEcmHealthSet();
        if(type==1){//指定时间的排号，不判断排号时间是否被占用
            healthNumber.setStart(DateTime.parse(date,format).getMillis());
            healthNumber.setDay(new DateTime(healthNumber.getStart()).toString("yyyy-MM-dd"));
            Integer number=hs.MaxNumberWithDay(healthNumber.getDay());
            healthNumber.setNumber(number==null?0:(number+1));
            healthNumber.setCode("D"+new DateTime(healthNumber.getStart()).toString("yyyyMMdd")+"-"+getFoot(healthNumber.getNumber()));
        }else{//指定日期的排号
            String[] times=set.getAmStartDate().trim().split(":");
            long searchDate=DateTime.now().getMillis();
            switch(type){
            case 2://指定日期
                searchDate=DateTime.parse(date+" "+"00:00",format).millisOfDay().withMinimumValue().plusHours(Integer.parseInt(times[0])).plusMinutes(Integer.parseInt(times[1])).getMillis();
                break;
            case 3://2周后
                searchDate=DateTime.now().plusDays(14).millisOfDay().withMinimumValue().plusHours(Integer.parseInt(times[0])).plusMinutes(Integer.parseInt(times[1])).getMillis();
                break;
            case 4://不指定日期和时间
                searchDate=lastNumber.getStart();
                break;
            }
            HealthArrange arrange=hs.InitArrange(set,(searchDate>Long.parseLong(lastNumber.getStart().toString()))?searchDate:lastNumber.getStart());
            healthNumber=arrange.next();
        }
        //判断是否插入排号
        if(healthNumber.getStart()>lastNumber.getStart()+set.getSpacing()*60*1000){
            healthNumber.setType(2);
        }else{
            healthNumber.setType(1);//常规排号
        }
        String consultationDate=new DateTime(healthNumber.getStart()).toString("yyyy-MM-dd HH:mm");
        StringBuffer sb=new StringBuffer();
        sb.append("{");
        sb.append("\"result\"").append(":\"").append(result).append("\",");
        sb.append("\"arrangeCode\"").append(":\"").append(healthNumber.getCode()).append("\",");
        sb.append("\"consultationDateBefore\"").append(":\"").append(consultationDate.substring(0,10)).append("\",");
        sb.append("\"consultationDateAfter\"").append(":\"").append(consultationDate.substring(11)).append("\",");
        sb.append("\"arrangeType\"").append(":\"").append(healthNumber.getType()).append("\"");
        sb.append("}");
        return sb.toString();
    }
    /**
     * 候诊号的最后数字部分的字符串转换
     *
     * @param number 候诊号
     *
     * @return String
     */
    private String getFoot(int number){
        if(number<10){
            return "000"+number;
        }else if(number<100){
            return "00"+number;
        }else if(number<1000){
            return "0"+number;
        }
        return number+"";
    }
}
