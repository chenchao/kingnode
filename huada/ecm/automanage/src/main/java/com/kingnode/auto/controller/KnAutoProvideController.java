package com.kingnode.auto.controller;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;

import com.kingnode.auto.entity.KnAutoBorrow;
import com.kingnode.auto.service.KnAutoManageService;
import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
/**
 * Created by xushuangyong on 14-9-15.
 */
@Controller @RequestMapping(value="/auto/provide")
public class KnAutoProvideController{
    @Autowired
    private KnAutoManageService autoManageService;
    @RequestMapping(method=RequestMethod.GET)
    public String list(Model model){
        return "auto/provide";
    }
    /**
     * 对发放车辆进行分页查询
     *
     * @param dt
     * @param request
     *
     * @return
     */
    @RequestMapping(value="page-autoBorrows",method=RequestMethod.POST) @ResponseBody
    public DataTable<Map> list(DataTable<Map> dt,ServletRequest request){
        Sort sort=Utils.getSort(dt,request);
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        List<KnAutoBorrow.BorrowType> typeList=new ArrayList();
        typeList.add(KnAutoBorrow.BorrowType.APPROVAL);
        return autoManageService.PageListBorrow(searchParams,dt,sort,typeList);
    }
    /**
     * 修改车辆的状态
     *
     * @param model
     * @param id
     * @param state              APPROVAL 表示批准,REJECT表示拒绝
     * @param redirectAttributes
     *
     * @return
     */
    @RequestMapping(value="deselect",method=RequestMethod.POST)
    public String deselect(Model model,Long id,String state,RedirectAttributes redirectAttributes){
        try{
            //           RESERVE,APPROVAL,REJECT,DESELECT,LEND,RESTORE,OVERTIME,TIMEOUT
            if("DESELECT".equals(state)){
                autoManageService.CancelAutoAppToUser(id);
            }
            redirectAttributes.addFlashAttribute("message","车辆发放已取消");
            return "redirect:/auto/provide";
        }catch(Exception e){
            e.printStackTrace();
        }
        redirectAttributes.addFlashAttribute("message","车辆发放已取消");
        return "redirect:/auto/provide";
    }
    /**
     * 发放车辆
     *
     * @param model
     * @param request
     * @param redirectAttributes
     *
     * @return
     */
    @RequestMapping(value="grant-auto",method=RequestMethod.POST)
    public String grantAuto(Model model,Long id,KnAutoBorrow changeObject,ServletRequest request,RedirectAttributes redirectAttributes){
        String msg="车辆已发放";
        try{
            msg=autoManageService.GrantAuto(id,changeObject);
        }catch(Exception e){
            e.printStackTrace();
        }
        redirectAttributes.addFlashAttribute("message",msg);
        return "redirect:/auto/provide";
    }
}



