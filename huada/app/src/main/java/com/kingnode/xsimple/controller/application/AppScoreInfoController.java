package com.kingnode.xsimple.controller.application;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.dto.ScoreDto;
import com.kingnode.xsimple.service.application.ApplicationInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * 应用评论管理
 * wangyifan
 */
@Controller @RequestMapping(value="/app-score/list")
public class AppScoreInfoController{
    @Autowired
    private ApplicationInfoService applicationInfoService;
    private static Logger logger=LoggerFactory.getLogger(AppScoreInfoController.class);
    //获取应用评论列表信息
    @RequestMapping(value="app-score-list",method=RequestMethod.POST) @ResponseBody
    public DataTable<ScoreDto> list(DataTable<ScoreDto> dt,@RequestParam(value="plateForm",required=false) String plateForm,@RequestParam(value="beginTime",required=false) String beginTime,@RequestParam(value="endTime",required=false) String endTime,@RequestParam(value="versionNum",required=false) String versionNum,@RequestParam(value="rating",required=false) String rating,@RequestParam(value="appComment",required=false) String appComment,@RequestParam(value="userName",required=false) String userName) throws ParseException{
        Map<String,String> map = new HashMap<>();
        map.put("beginTime",beginTime);
        map.put("endTime",endTime);
        map.put("versionNum",versionNum);
        map.put("rating",rating);
        map.put("userName",userName);
        map.put("appComment",appComment);
        map.put("plateForm",plateForm);
        return applicationInfoService.GetAppSoreList(dt,map);
    }
    /**
     * @param model 设置返回信息
     *
     * @return 返回跳转链接
     */
    @RequestMapping(method=RequestMethod.GET)
    public String home(Model model){
        return "/application/appScoreList";
    }
    /**
     * 评论列表导出excel
     *
     * @param response   响应的请求
     */
    @RequestMapping(value={"/export-app-list"}, method={RequestMethod.GET})
    public void exportAppScoreExcel(HttpServletResponse response,String exportType,@RequestParam("plateForm") String plateForm,@RequestParam("ids") List<Long> ids,@RequestParam(value="beginTime",required=false) String beginTime,@RequestParam(value="endTime",required=false) String endTime,@RequestParam(value="appComment",required=false) String appComment,@RequestParam(value="userName",required=false) String userName,@RequestParam(value="versionNum",required=false) String versionNum,@RequestParam(value="rating",required=false) String rating) throws Exception{
        Map<String,String> map = new HashMap<>();
        map.put("beginTime",beginTime);
        map.put("endTime",endTime);
        map.put("versionNum",versionNum);
        map.put("rating",rating);
        map.put("exportType",exportType);
        map.put("userName",userName);
        map.put("appComment",appComment);
        map.put("plateForm",plateForm);
        applicationInfoService.ExceptAppScoreInfo(response,map,ids);
    }
    /**
     * 全选删除应用评论信息
     *
     * @return 返回跳转链接
     */
    @RequestMapping(value="delete-all",method=RequestMethod.POST) @ResponseBody
    public Map<String,Boolean> deleteAll(@RequestParam("ids") List<Long> ids){
        Map<String,Boolean> map=new HashMap<String,Boolean>();
        try{
            applicationInfoService.DeleteAllAppScoreListByIds(ids);
            map.put("stat",true);
        }catch(Exception e){
            map.put("stat",false);
            logger.info("删除应用评论错误信息："+e);
        }
        return map;
    }
}
