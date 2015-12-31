package com.kingnode.third.rest;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Strings;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.third.service.UserContactService;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequestMapping({"/api/v1/ps"})
public class UserContactRestController{
    private static Logger logger=LoggerFactory.getLogger(UserContactRestController.class);
    @Autowired
    private UserContactService userContactService;
    /**
     * ******************
     * PS统一的操作入口
     *
     * @param jsonparm 手机端传入参数
     *                 参数含义如下：{"jsonparm":"{\"empId\":\"chenchuhao\",\"userId\":\"chenchuhao\",\"fromSys\":\"PS\"}"}
     *
     * @return
     */
    @RequestMapping(value="data",method={RequestMethod.POST})
    public String getPsData(@RequestParam(value="jsonparm") String jsonparm){
        logger.info("V3获取上下级联系人的关系----->"+jsonparm);
        String backInfo="";
        String returnData="";
        Map returnMap=new HashMap();
        if(Utils.isEmptyString(jsonparm)){
            Map<String,String> map=new HashMap<String,String>();
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"参数为空");
            backInfo=JsonMapper.nonEmptyMapper().toJson(map);
        }else{
            Map<String,String> jsonMap=JsonMapper.nonEmptyMapper().fromJson(jsonparm,Map.class);
            String type="";
            if(jsonMap.containsKey("methodType")){
                type=jsonMap.get("methodType").trim();
            }
            if(Strings.isNullOrEmpty(type)){//没有,代表直接访问rest地址
                backInfo=getRestData(jsonparm);
            }else if("roll".equalsIgnoreCase(type)){//代表直接访问rest地址,获取rest的返回的listdata中的USER_NAME的信息
                backInfo=getDataRoll(jsonparm);
            }else if("0".equals(type)){//0,是获取上下级联系人的关系
                backInfo=getUndUserinfo(jsonparm);
            }else if("1".equals(type)){//1是新建PS联系人接口
                backInfo=addContact(jsonparm);
            }else if("2".equals(type)){//2是删除联系人接口信息
                backInfo=removeContact(jsonparm);
            }else if("3".equals(type)){//3是根据联系人的姓名查询数据库中的用户信息
                backInfo=findUserByName(jsonparm);
            }else if("4".equals(type)){//4是获取个人简要信息
                backInfo=getUserDetail(jsonparm);
            }else if("5".equals(type)){//5是获取联系人头像地址信息
                backInfo=getUserImg(jsonparm);
            }
        }
        return backInfo;
    }
    /**
     * 获取上下级联系人的关系
     *
     * @param jsonparm 手机端传入参数
     *                 参数含义如下： {\"empId\":\"10000070\",\"fromSys\":\"PS\",\"userId\":\"PS\"}
     *                 例如：        {"empId":"10000070","fromSys":"PS","userId":"PS"}
     */

    @RequestMapping(value="un-user",method={RequestMethod.POST})
    public String getUndUserinfo(@RequestParam(value="jsonparm") String jsonparm){
        return userContactService.GetUndUserinfo(jsonparm);
    }
    /**
     * ****************
     * PS创建联系人的接口
     *
     * @param jsonparm 手机端传入参数
     *                 参数含义如下： {"jsonparm":"{\"createrId\":\"12121\",\"createrFrom\":\"PS\",\"contactId\":\"235345\",\"contactFrom\":\"PS\"}"}
     *
     * @return
     */
    @RequestMapping(value="add-contact",method={RequestMethod.POST})
    public String addContact(@RequestParam(value="jsonparm") String jsonparm){
        return userContactService.AddContact(jsonparm);
    }
    /**
     * ******************
     * PS删除联系人的接口
     *
     * @param jsonparm 手机端传入参数
     *                 参数含义如下：{"createrId":"12121","createrFrom":"PS","contactId":"235345","contactFrom":"PS"}
     *
     * @return
     */
    @RequestMapping(value="remove-contact",method={RequestMethod.POST})
    public String removeContact(@RequestParam(value="jsonparm") String jsonparm){
        return userContactService.RemoveContact(jsonparm);
    }
    /**
     * ******************
     * PS查询用户信息
     *
     * @param jsonparm 手机端传入参数
     *                 参数含义如下：{"sysType":"PS","userName":"张"}
     *
     * @return
     */
    @RequestMapping(value="find-user",method={RequestMethod.POST})
    public String findUserByName(@RequestParam(value="jsonparm") String jsonparm){
        return userContactService.FindUserByName(jsonparm);
    }
    /**
     * ******************
     * PS人员简介
     *
     * @param jsonparm 手机端传入参数
     *                 参数含义如下：{"userId":"100070"}
     *
     * @return
     */
    @RequestMapping(value="user-detail",method={RequestMethod.POST})
    public String getUserDetail(@RequestParam(value="jsonparm") String jsonparm){
        return userContactService.GetUserDetail(jsonparm);
    }
    /**
     * ******************
     * 获取联系人头像地址
     *
     * @param jsonparm 手机端传入参数
     *                 参数含义如下：{"userId":"1110”,”fromSys”:”PS”}
     *
     * @return
     */
    @RequestMapping(value="user-img",method={RequestMethod.POST})
    public String getUserImg(@RequestParam(value="jsonparm") String jsonparm){
        return userContactService.GetUserImg(jsonparm);
    }
    /**
     * ******************
     * 获取rest的数据信息
     *
     * @param jsonparm 手机端传入参数
     *                 参数含义如下：
     *
     * @return
     */
    @RequestMapping(value="rest-data",method={RequestMethod.POST})
    public String getRestData(@RequestParam(value="jsonparm") String jsonparm){
        return userContactService.GetRestData(jsonparm);
    }
    /**
     * *****************
     * 获取rest的数据信息后迭代获取后查询数据库获取用户头像等信息
     *
     * @param jsonparm
     *
     * @return
     */
    @RequestMapping(value="data-roll",method={RequestMethod.POST})
    public String getDataRoll(@RequestParam(value="jsonparm") String jsonparm){
        return userContactService.GetDataRoll(jsonparm);
    }
}
