package com.kingnode.xsimple.rest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.dto.FeedProblemDTO;
import com.kingnode.xsimple.entity.system.KnCustomServiceInfo;
import com.kingnode.xsimple.entity.system.KnSystemPropertieInfo;
import com.kingnode.xsimple.service.system.CustomService;
import com.kingnode.xsimple.service.system.IdeaFeedbackService;
import com.kingnode.xsimple.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author 陈超
 * 反馈意见接口
 */
@RestController @RequestMapping({"/api/v1/feedback"})
public class FeedbackRestController{
    @Autowired
    private CustomService cs;
    @Autowired
    private IdeaFeedbackService fs;
    /**
     * 获取系统属性信息
     * 示例：http://localhost:8080/api/v1/feedback/ready-system-attribute
     * 返回：{
             "status" : true,／／true代表成功，false代表失败。
             "errorCode" : null,//错误代码
             "errorMessage" : null,／／错误信息
             "detail" : {
             "id" : "40288824480ad95301480ad9f87f0000",
             "telNum" : "6666",／／电话
             "email" : "5555"／／邮箱
             }
             }
     * @return
     */
    @RequestMapping(method={RequestMethod.GET})
    public Map findSystemAttribute(){
        List<KnSystemPropertieInfo> syss = cs.ReadSystemPropertieInfo();
        Map map = new HashMap();
        if(  !Utils.isEmpityCollection(syss) ) {
            KnSystemPropertieInfo info = syss.get(0);
            map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
            map.put(Setting.MESSAGE,"获取成功");
            map.put("telNum",info.getTelNum()==null?"":info.getTelNum());//电话
            map.put("email",info.getEmail()==null?"":info.getEmail());//邮箱
        }else{
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"没有找到系统属性信息");
        }
        return map;
    }

    /**
     * 保存意见反馈信息
     * 示例：http://localhost:8080/api/v1/feedback/save-feed-opin
     * 参数：jsonpram＝{
             "appId":"ff808081447cf7f301447d5080ac003b",／／应用的appid

             "fromSys":"ERP", ／／用户的来自系统
             "linkPerop":"KND8" ,／／联系人姓名
             "phone":"13098937652" ,／／联系电话
             "content": "—反馈内容",／／反馈内容
             "packageName":"cn.com.kingdom"／／应用包名
             }
     * 返回：{
         "appId":"ff808081447cf7f301447d5080ac003b",／／应用的appid

         "fromSys":"ERP", ／／用户的来自系统
         "linkPerop":"KND8" ,／／联系人姓名
         "phone":"13098937652" ,／／联系电话
         "content": "—反馈内容",／／反馈内容
         "packageName":"cn.com.kingdom"／／应用包名
         }

     */
    @RequestMapping(value="customer",method={RequestMethod.GET})
    public Map readyEmployee(){
        Map map = new HashMap();
        try{
            List<KnCustomServiceInfo> list = cs.FindCustomServices();
            map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
            map.put("user",list);
        }catch(Exception e){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"参数格式不对");
        }
        return map;
    }


    /**
     * 示例：http://localhost:8080/api/v1/feop/ready-employee
     * 参数：jsonparm={
             "pageNum":"1", --当前页数
             "pageSize":"20", --每页显示多少条数据
             }

     * 返回：
     * {
         "resCode":"200",--200成功,500失败
         "user":[
         {
         "id":"2222",--用户的主键id
         "userId":"1210",--用户的userId
         "fromSys":"ERP",--来自系统
         "userType":"employee",--用户的类型
         "uaccount":"BOFU.LI",--用户英文名
         "fullName":"罗晶",--用户的全名
         },{
         "id":"2222",--用户的主键id
         "userId":"1210",--用户的userId
         "fromSys":"ERP",--来自系统
         "userType":"employee",--用户的类型
         "uaccount":"BOFU.LI",--用户英文名
         "fullName":"罗晶",--用户的全名
         }
         ]--用户的数组
         }

     */
    @RequestMapping(method={RequestMethod.POST})
    public Map saveFeedbackOpinion(@RequestParam(value="jsonparm")String jsonparam){
        boolean flag = fs.saveFeedback(jsonparam);
        Map map = new HashMap();
        if(!flag){
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"保存失败");
        }else{
            map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
            map.put(Setting.MESSAGE,"保存成功");
        }
        return map;
    }

    @RequestMapping(value="problem",method={RequestMethod.GET})
    public ListDTO<FeedProblemDTO> findFeedBackProblem(){
        return new ListDTO<>(true,fs.FindFeedProblemList());
    }
}
