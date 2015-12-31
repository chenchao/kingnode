package com.kingnode.attendance.controller;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kingnode.attendance.service.AttendanceService;
import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.api.common.DataTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller @RequestMapping(value="/attendance/empattendance")
public class AttendanceConttroler{

    @Autowired
    private AttendanceService attendanceService;

    /**
     * 跳转到公告列表页面
     * @return
     */
    @RequestMapping(method=RequestMethod.GET)
    public String home(){
        return "attendance/empattendanceList";
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    @RequestMapping(method=RequestMethod.POST) @ResponseBody
    public DataTable list(DataTable dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return attendanceService.PageEmpAttendance(searchParams,dt);
    }
    /**
     * 导出考勤信息
     * @param userName
     * @param startTime
     * @param endTime
     * @param tip
     * @param ids
     * @param response
     */
    @RequestMapping(value="exportEmp",method=RequestMethod.POST) @ResponseBody
    public void exportExcel(@RequestParam("userName") String userName,@RequestParam("startTime") String startTime
            ,@RequestParam("endTime") String endTime,@RequestParam("tip") String tip,@RequestParam("ids") String ids,HttpServletResponse response){
        attendanceService.exportExcel(userName,startTime,endTime,tip,ids,response);
    }

}
