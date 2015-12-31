package com.kingnode.auto.controller;
import java.util.ArrayList;
import java.util.HashMap;
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
@Controller @RequestMapping(value="/auto/return")
public class KnAutoReturnController{
    @Autowired
    private KnAutoManageService as;
    @RequestMapping(method=RequestMethod.GET)
    public String list(Model model){
        return "auto/return";
    }
    /**
     * 对归还车辆进行分页
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
        typeList.add(KnAutoBorrow.BorrowType.LEND);
        return as.PageListBorrow(searchParams,dt,sort,typeList);
    }
    /**
     * 修改车辆的状态
     *
     * @param model
     * @param id  申请单id
     * @param state  APPROVAL 表示批准,REJECT表示拒绝
     * @param redirectAttributes
     *
     * @return
     */
    @RequestMapping(value="change-stage",method=RequestMethod.POST) @ResponseBody
    public HashMap changeStage(Model model,Long id,String state,RedirectAttributes redirectAttributes){
        HashMap map=new HashMap();
        try{
            //           RESERVE,APPROVAL,REJECT,DESELECT,LEND,RESTORE,OVERTIME,TIMEOUT
            if("RESTORE".equals(state)){
                //归还车辆
                as.ReutrnAutoToRestore(id);
            }
            map.put("success",true);
            return map;
        }catch(Exception e){
            e.printStackTrace();
        }
        map.put("success",false);
        map.put("message","");
        return map;
    }
    /**
     *
     * 查询已归还车辆列表
     * @param model
     * @param request
     * @param redirectAttributes
     *
     * @return
     */
    @RequestMapping(value="return-auto",method=RequestMethod.POST)
    public String returnAuto(Model model,Long id,KnAutoBorrow object,ServletRequest request,RedirectAttributes redirectAttributes){
        try{
            as.ReturnAuto(id,object);
        }catch(Exception e){
            e.printStackTrace();
        }
        redirectAttributes.addFlashAttribute("message","车辆已归还");
        return "redirect:/auto/return";
    }
}
