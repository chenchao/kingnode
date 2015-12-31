package com.kingnode.auto.controller;
import java.util.Map;
import javax.servlet.ServletRequest;

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
/**
 * Created by xushuangyong on 14-9-15.
 */
@Controller @RequestMapping(value="/auto/borrow-record")
public class KnAutoBorrowRecordController{
    @Autowired
    private KnAutoManageService autoManageService;
    @RequestMapping(method=RequestMethod.GET)
    public String list(Model model){
        return "auto/borrowRecord";
    }
    /**
     *
     * 申请车辆记录
     * @param dt
     * @param request
     *
     * @return
     */
    @RequestMapping(value="page-autoBorrows",method=RequestMethod.POST) @ResponseBody
    public DataTable<Map> list(DataTable<Map> dt,ServletRequest request){
        Sort sort=Utils.getSort(dt,request);
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return autoManageService.PageListBorrow(searchParams,dt,sort,null);
    }
}
