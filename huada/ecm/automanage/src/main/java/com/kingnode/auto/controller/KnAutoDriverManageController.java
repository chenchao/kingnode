package com.kingnode.auto.controller;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletRequest;

import com.kingnode.auto.dto.ApplyAutoDTO;
import com.kingnode.auto.entity.KnAuto;
import com.kingnode.auto.entity.KnAutoBorrow;
import com.kingnode.auto.entity.KnAutoDriver;
import com.kingnode.auto.service.KnAutoManageService;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.api.common.AjaxStatus;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.rest.DetailDTO;
import com.kingnode.xsimple.service.system.OrganizationService;
import com.kingnode.xsimple.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
/**
 * Created by xushuangyong on 14-9-15.
 */
@Controller @RequestMapping(value="/auto/driver-manage")
public class KnAutoDriverManageController{
    @Autowired
    private OrganizationService os;
    @Autowired
    private KnAutoManageService autoManageService;
    @RequestMapping(method=RequestMethod.GET)
    public String list(Model model){
        return "auto/driverManage";
    }
    /**
     * 打开新增司机页面
     *
     * @param model
     *
     * @return
     */
    @RequestMapping(value="create",method=RequestMethod.GET)
    public String create(Model model){
        model.addAttribute("actionName","创建");
        model.addAttribute("driverJson",JsonMapper.nonEmptyMapper().toJson(new HashMap<>()));
        model.addAttribute("url","/auto/driver-manage/create");
        return "auto/driveForm";
    }
    /**
     * 创建司机
     *
     * @param driver             司机
     * @param redirectAttributes
     *
     * @return
     */
    @RequestMapping(value="create",method=RequestMethod.POST)
    public String create(KnAutoDriver driver,RedirectAttributes redirectAttributes){
        String msg="保存司机成功!";
        autoManageService.SaveDriver(driver);
        redirectAttributes.addFlashAttribute("message",msg);
        return "redirect:/auto/driver-manage";
    }
    /**
     * 跳转到修改书籍信息页面
     *
     * @param id
     * @param model
     *
     * @return
     */
    @RequestMapping(value="update/{id}",method=RequestMethod.GET)
    public String update(@PathVariable("id") Long id,Model model){
        KnAutoDriver driver=autoManageService.ReadDriver(id);
        model.addAttribute("driverJson",JsonMapper.nonEmptyMapper().toJson(driver));
        model.addAttribute("driver",driver);
        model.addAttribute("actionName","修改");
        model.addAttribute("url","/auto/driver-manage/update");
        return "auto/driveForm";
    }
    @RequestMapping(value="update",method=RequestMethod.POST)
    public String update(Model model,KnAutoDriver driver,RedirectAttributes redirectAttributes){
        String msg="修改司机成功!";
        autoManageService.SaveDriver(driver);
        //TestApplyFormAuto();
        redirectAttributes.addFlashAttribute("message",msg);
        return "redirect:/auto/driver-manage";
    }
    /**
     * 对司机进行分页显示
     *test//
     * @param dt
     * @param request
     *
     * @return
     */
    @RequestMapping(value="page-drivers",method=RequestMethod.POST) @ResponseBody
    public DataTable<KnAutoDriver> list(DataTable<KnAutoDriver> dt,ServletRequest request){
        Sort sort=Utils.getSort(dt,request);
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return autoManageService.PageDrivers(searchParams,dt,sort);
    }
    /**
     * 删除司机
     *
     * @param id 司机id
     *
     * @return
     */
    @RequestMapping(value="delete",method=RequestMethod.POST) @ResponseBody
    public AjaxStatus delete(long id){
        AjaxStatus status=new AjaxStatus(false);
        try{
            autoManageService.DeleteKnDriver(id);
            status.setSuccess(true);
            return status;
        }catch(Exception e){
            e.printStackTrace();
        }
        status.setSuccess(true);
        status.setMessage("");
        return status;
    }
    public void TestApplyFormAuto(){
        KnEmployee user=os.ReadEmp(5L);
        // List<KnAuto> listAuto=autoManageService.ListAutos();
        KnAuto auto=autoManageService.ReadAuto(65L);
        ApplyAutoDTO applyAutoDTO=new ApplyAutoDTO();
        applyAutoDTO.setName(user.getLoginName());
        applyAutoDTO.setCause("商务用车");
        applyAutoDTO.setRidingNumber(100);
        applyAutoDTO.setAutoId(auto.getId());
        applyAutoDTO.setUserId(user.getId());
        applyAutoDTO.setDestination("武汉");
        applyAutoDTO.setMileage(100L);
        applyAutoDTO.setLendDate(System.currentTimeMillis()+10000);
        applyAutoDTO.setRestoreDate(System.currentTimeMillis()+8000000);
        DetailDTO<KnAutoBorrow> t1=autoManageService.ApplyAuto(applyAutoDTO);
        System.out.println("t1---------------"+t1.getStatus());
        DetailDTO<KnAutoBorrow> t2=autoManageService.ApplyAuto(applyAutoDTO);
        System.out.println("t2---------------"+t2.getStatus());
    }
}
