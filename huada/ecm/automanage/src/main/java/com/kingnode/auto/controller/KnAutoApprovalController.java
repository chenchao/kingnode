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
@Controller @RequestMapping(value="/auto/approval")
public class KnAutoApprovalController{
    @Autowired
    private KnAutoManageService autoManageService;
    @RequestMapping(method=RequestMethod.GET)
    public String home(Model model){
        return "auto/approvalList";
    }
    /**
     * 对车辆审批进行分页
     *
     * @param dt
     * @param request
     *
     * @return
     */
    @RequestMapping(value="page-autoBorrows", method=RequestMethod.POST) @ResponseBody
    public DataTable<Map> list(DataTable<Map> dt,ServletRequest request){
        Sort sort=Utils.getSort(dt,request);
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        List<KnAutoBorrow.BorrowType> typeList=new ArrayList();
        typeList.add(KnAutoBorrow.BorrowType.APPLY);
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
    @RequestMapping(value="change-stage", method=RequestMethod.POST) @ResponseBody
    public HashMap changeStage(Model model,Long id,String state,Long driverId,RedirectAttributes redirectAttributes){
        HashMap map=new HashMap();
        try{
            //           RESERVE,APPROVAL,REJECT,DESELECT,LEND,RESTORE,OVERTIME,TIMEOUT
            if("APPROVAL".equals(state)){
                autoManageService.ApproveApplyForm(id,driverId);
            }
            if("REJECT".equals(state)){
                autoManageService.RejectApplyForm(id);
            }
            map.put("success",true);
            return map;
        }catch(Exception e){
            map.put("message","车辆审批失败，"+e.getMessage());
            e.printStackTrace();
        }
        map.put("success",false);

        return map;
    }
    /**
     * 对司机分页显示
     *
     * @param dataTable jquery dataTable 传过来的参数
     *
     * @return
     */
    @RequestMapping(value="page-drivers") @ResponseBody
    public DataTable pageDrivers(DataTable dataTable){
        return autoManageService.PageDriver(dataTable);
    }
}



