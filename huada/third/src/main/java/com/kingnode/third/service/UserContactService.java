package com.kingnode.third.service;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.common.base.Strings;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.third.dao.KnUserThirdInfoDao;
import com.kingnode.third.entity.KnUserThirdInfo;
import com.kingnode.xsimple.Setting;
import com.kingnode.third.dao.KnPsUserContactInfoDao;
import com.kingnode.xsimple.dao.system.KnEmployeeDao;
import com.kingnode.xsimple.dao.system.KnUserDao;
import com.kingnode.third.entity.KnPsUserContactInfo;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnUser;
import com.kingnode.xsimple.util.Utils;
import com.kingnode.xsimple.util.client.RestServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Component @Transactional(readOnly=true)
public class UserContactService{
    private static Logger logger=LoggerFactory.getLogger(UserContactService.class);
    private static final String restPsUserUrl="http://test.kingnode.com:8230/Jersey_WebClient/restRequest.jsp";
    private static final String psFromSys ="PS";//PS来自系统
    private KnUserThirdInfoDao knUserThirdInfoDao;
    private KnUserDao knUserDao;
    private KnEmployeeDao knEmployeeDao;
    private KnPsUserContactInfoDao knPsUserContactInfoDao;
    @Autowired
    public void setKnUserThirdInfoDao(KnUserThirdInfoDao knUserThirdInfoDao){
        this.knUserThirdInfoDao=knUserThirdInfoDao;
    }
    @Autowired
    public void setKnUserDao(KnUserDao knUserDao){
        this.knUserDao=knUserDao;
    }
    @Autowired
    public void setKnEmployeeDao(KnEmployeeDao knEmployeeDao){
        this.knEmployeeDao=knEmployeeDao;
    }
    @Autowired
    public void setKnPsUserContactInfoDao(KnPsUserContactInfoDao knPsUserContactInfoDao){
        this.knPsUserContactInfoDao=knPsUserContactInfoDao;
    }

    public String GetUndUserinfo(String jsonparm){
        logger.info("V3获取上下级联系人的关系----->"+jsonparm);
        String backInfo="";
        String returnData = "";
        Map returnMap=new HashMap();
        try{
            if(Utils.isEmptyString(jsonparm)){
                Map<String,String> map=new HashMap<String,String>();
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"参数为空");
                backInfo=JsonMapper.nonEmptyMapper().toJson(map);
            }else{
                Map<String,String> jsonMap=JsonMapper.nonEmptyMapper().fromJson(jsonparm,Map.class);
                if(null!=jsonMap&&jsonMap.size()>0){
                    if(!jsonMap.containsKey("empId")||!jsonMap.containsKey("userId")||!jsonMap.containsKey("fromSys")){
                        Map<String,String> map=new HashMap<String,String>();
                        map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                        map.put(Setting.MESSAGE,"用户的empId||userId||fromSys为空");
                        backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                    }else{
                        String empId = jsonMap.get("empId");;
                        String userId = jsonMap.get("userId");
                        String fromSys = jsonMap.get("fromSys").toString();
                        if(Strings.isNullOrEmpty(jsonMap.get("empId"))){
                            Map<String,String> map=new HashMap<String,String>();
                            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                            map.put(Setting.MESSAGE,"用户的empId为空");
                            backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                        }else{
                            returnData = getUndUserInfo(empId);
                            Map psJo = new HashMap();
                            if(!Strings.isNullOrEmpty(returnData)){
                                Map jo=JsonMapper.nonEmptyMapper().fromJson(returnData,Map.class);
                                //上级人员
                                List<Map> listUp=(ArrayList)jo.get("detail");
                                List<Map> tempJaUp = new ArrayList<>();
                                if(listUp!=null){
                                    for(int i=0;i<listUp.size();i++){
                                        Map temp = listUp.get(i);
                                        String OPRID = temp.get("OPRID").toString().trim().toUpperCase();
                                        List<KnUserThirdInfo> list = this.getInfoByUaccount(OPRID);
                                        if(list!=null&&list.size()!=0){
                                            KnUserThirdInfo user = null ;
                                            KnEmployee ui=null ;
                                            String markName ;
                                            String empId2 = "";
                                            for (KnUserThirdInfo userThirdInfo : list) {
                                                markName = userThirdInfo.getMarkName();
                                                user = userThirdInfo;
                                                if(Strings.isNullOrEmpty(markName)){//不存在markName
                                                    user = userThirdInfo;
                                                }
                                                if(psFromSys.equalsIgnoreCase(userThirdInfo.getFromSys())){//只是查找PS用户的信息,将PS的empId放入userType中
                                                    empId2 = userThirdInfo.getUserType();
                                                }
                                            }
                                            if(Strings.isNullOrEmpty(user.getMarkName())){//没有关联的用户,只有一个,直接在UserInfo中获取即可
                                                List<KnUser> userList = this.getInfoByLoginName(OPRID);
                                                Long[] ids=new Long[userList.size()];
                                                for(int ic=0;ic<userList.size();i++){
                                                    KnUser ku=userList.get(i);
                                                    ids[i]=ku.getId();
                                                }
                                                List<KnEmployee> knEmployees=this.getInfoByIds(ids);
                                                if(knEmployees.size()!=0){
                                                    ui = knEmployees.get(0);
                                                }
                                            }else{//在UserInfo 中获取markName一致的用户信息
                                                List<KnEmployee> userList = this.getInfoByMarkName(user.getMarkName());
                                                if(userList.size()!=0){
                                                    ui = userList.get(0);
                                                }
                                            }
                                            if(ui!=null){
                                                temp.put("imgAddress", ui.getImageAddress());
                                                temp.put("userType", empId2);
                                                temp.put("id", ui.getId());
                                            }else{
                                                temp.put("imgAddress","");
                                                temp.put("userType", empId2);
                                                temp.put("id", "");
                                            }
                                        }else{
                                            temp.put("imgAddress","");
                                            temp.put("userType", "");
                                            temp.put("id", "");
                                        }
                                        tempJaUp.add(temp);
                                    }
                                }
                                psJo.put("detail", tempJaUp);
                                //同级人员
                                List<Map> arraySame = (ArrayList)jo.get("listdata");
                                List<Map> tempJaSame = new ArrayList();
                                if(listUp!=null){
                                    for(int i=0;i<arraySame.size();i++){
                                        Map temp = arraySame.get(i);
                                        String OPRID = temp.get("OPRID").toString().trim().toUpperCase();
                                        List<KnUserThirdInfo> list = this.getInfoByUaccount(OPRID);
                                        if(list!=null&&list.size()!=0){
                                            KnUserThirdInfo user = null ;
                                            KnEmployee ui=null ;
                                            String markName ;
                                            String empId2 ="";
                                            for (KnUserThirdInfo userThirdInfo : list) {
                                                markName = userThirdInfo.getMarkName();
                                                user = userThirdInfo;
                                                if(Strings.isNullOrEmpty(markName)){//不存在markName
                                                    user = userThirdInfo;
                                                }
                                                if(psFromSys.equalsIgnoreCase(userThirdInfo.getFromSys())){//只是查找PS用户的信息,将PS的empId放入userType中
                                                    empId2 = userThirdInfo.getUserType();
                                                }
                                            }
                                            if(Strings.isNullOrEmpty(user.getMarkName())){//没有关联的用户,只有一个,直接在UserInfo中获取即可
                                                List<KnUser> userList = this.getInfoByLoginName(OPRID);
                                                Long[] ids=new Long[userList.size()];
                                                for(int ic=0;ic<userList.size();i++){
                                                    KnUser ku=userList.get(i);
                                                    ids[i]=ku.getId();
                                                }
                                                List<KnEmployee> knEmployees=this.getInfoByIds(ids);
                                                if(knEmployees.size()!=0){
                                                    ui = knEmployees.get(0);
                                                }
                                            }else{//在UserInfo 中获取markName一致的用户信息
                                                List<KnEmployee> userList = this.getInfoByMarkName(user.getMarkName());
                                                if(userList.size()!=0){
                                                    ui = userList.get(0);
                                                }
                                            }
                                            if(ui!=null){
                                                temp.put("imgAddress",ui.getImageAddress());
                                                temp.put("userType", empId2);
                                                temp.put("id", ui.getId());
                                            }else{
                                                temp.put("imgAddress","");
                                                temp.put("userType", empId2);
                                                temp.put("id", "");
                                            }
                                        }else{
                                            temp.put("imgAddress","");
                                            temp.put("userType", "");
                                            temp.put("id", "");
                                        }
                                        tempJaSame.add(temp);
                                    }
                                }
                                psJo.put("listdata", tempJaSame);
                                //下级人员
                                List<Map> arrayDwon = (ArrayList)jo.get("listdata1");
                                List<Map> tempJaDown = new ArrayList();
                                if(listUp!=null){
                                    for(int i=0;i<arrayDwon.size();i++){
                                        Map temp = arrayDwon.get(i);
                                        String OPRID = temp.get("OPRID").toString().trim().toUpperCase();
                                        List<KnUserThirdInfo> list = this.getInfoByUaccount(OPRID);
                                        if(list!=null&&list.size()!=0){
                                            KnUserThirdInfo user = null ;
                                            KnEmployee ui=null ;
                                            String markName ;
                                            String empId2 ="";
                                            for (KnUserThirdInfo userThirdInfo : list) {
                                                markName = userThirdInfo.getMarkName();
                                                user = userThirdInfo;
                                                if(Strings.isNullOrEmpty(markName)){//不存在markName
                                                    user = userThirdInfo;
                                                }
                                                if(psFromSys.equalsIgnoreCase(userThirdInfo.getFromSys())){//只是查找PS用户的信息,将PS的empId放入userType中
                                                    empId2 = userThirdInfo.getUserType();
                                                }
                                            }
                                            if(Strings.isNullOrEmpty(user.getMarkName())){//没有关联的用户,只有一个,直接在UserInfo中获取即可
                                                List<KnUser> userList = this.getInfoByLoginName(OPRID);
                                                Long[] ids=new Long[userList.size()];
                                                for(int ic=0;ic<userList.size();ic++){
                                                    KnUser ku=userList.get(ic);
                                                    ids[ic]=ku.getId();
                                                }
                                                List<KnEmployee> knEmployees=this.getInfoByIds(ids);
                                                if(knEmployees.size()!=0){
                                                    ui = knEmployees.get(0);
                                                }
                                            }else{//在UserInfo 中获取markName一致的用户信息
                                                List<KnEmployee> userList = this.getInfoByMarkName(user.getMarkName());
                                                if(userList.size()!=0){
                                                    ui = userList.get(0);
                                                }
                                            }
                                            if(ui!=null){
                                                temp.put("imgAddress", ui.getImageAddress());
                                                temp.put("userType", empId2);
                                                temp.put("id", ui.getId());
                                            }else{
                                                temp.put("imgAddress","");
                                                temp.put("userType", empId2);
                                                temp.put("id", "");
                                            }
                                        }else{
                                            temp.put("imgAddress","");
                                            temp.put("userType", "");
                                            temp.put("id", "");
                                        }
                                        tempJaDown.add(temp);
                                    }
                                }
                                psJo.put("listdata1", tempJaDown);
                            }
                            returnMap.put("psData", psJo);
                            List<KnPsUserContactInfo> psList = this.getInfoByCreaterIdAndSys(userId,fromSys);
                            List<KnEmployee> list = new ArrayList<KnEmployee>();
                            if(psList!=null){
                                for (KnPsUserContactInfo psUserContactInfo : psList) {
                                    List<KnEmployee> userTemp = this.getInfoByUserIdAndFromSys(psUserContactInfo.getContactId(),psUserContactInfo.getContactFrom());
                                    list.addAll(userTemp);
                                }
                            }
                            returnMap.put("contactUser", list);
                            backInfo=JsonMapper.nonEmptyMapper().toJson(returnMap);
                        }
                    }
                }else{
                    Map<String,String> map=new HashMap<String,String>();
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"参数为空");
                    backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                }
            }
        }catch(Exception e){
            Map<String,String> map=new HashMap<String,String>();
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"网络繁忙,稍后再试");
            backInfo=JsonMapper.nonEmptyMapper().toJson(map);
            logger.info("获取用户上下级关系异常,传入的jsonparm为"+jsonparm+"\n 错误信息为:"+e);
        }finally{
            logger.info("V3获取用户上下级关系返回的数据----->"+backInfo);
            return backInfo;
        }
    }


    /*******************
     * PS创建联系人的接口
     * @param jsonparm 手机端传入参数
     *                 参数含义如下：{"createrId":"12121","createrFrom":"PS","contactId":"235345","contactFrom":"PS"}
     * @return
     */
    @Transactional(readOnly=false)
    public String AddContact(String jsonparm){
        logger.info("V3 新建联系人接口----->"+jsonparm);
        String backInfo="";
        try{
            if(Strings.isNullOrEmpty(jsonparm)){
                Map<String,String> map=new HashMap<String,String>();
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"jsonparm值为空");
                backInfo=JsonMapper.nonEmptyMapper().toJson(map);
            }else{
                Map<String,String> jsonMap=JsonMapper.nonEmptyMapper().fromJson(jsonparm,Map.class);
                if(!jsonMap.containsKey("createrId")||!jsonMap.containsKey("createrFrom")||!jsonMap.containsKey("contactId")||!jsonMap.containsKey("contactFrom")){
                    Map<String,String> map=new HashMap<String,String>();
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"创建者或加入的联系人为空");
                    backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                }else{
                    String createrId = jsonMap.get("createrId");
                    String createrFrom = jsonMap.get("createrFrom");
                    String contactId = jsonMap.get("contactId");
                    String contactFrom = jsonMap.get("contactFrom");
                    if(Strings.isNullOrEmpty(createrId)||Strings.isNullOrEmpty(createrFrom)||Strings.isNullOrEmpty(contactId)||Strings.isNullOrEmpty(contactFrom)){
                        Map<String,String> map=new HashMap<String,String>();
                        map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                        map.put(Setting.MESSAGE,"创建者或加入的联系人为空");
                        backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                    }else{
                        KnPsUserContactInfo psUser = new KnPsUserContactInfo();
                        psUser.setCreaterId(createrId);
                        psUser.setCreaterFrom(createrFrom.toUpperCase());
                        psUser.setContactId(contactId);
                        psUser.setContactFrom(contactFrom.toUpperCase());
                        KnPsUserContactInfo rtnUser=this.save(psUser);
                        Map<String,String> map=new HashMap<String,String>();
                        if(rtnUser!=null){
                            map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                            map.put(Setting.MESSAGE,"联系人创建成功");
                        }else{
                            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                            map.put(Setting.MESSAGE,"联系人创建失败");
                        }
                        backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                    }
                }
            }
        }catch (Exception e) {
            Map<String,String> map=new HashMap<String,String>();
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"联系人创建异常");
            backInfo=JsonMapper.nonEmptyMapper().toJson(map);
            logger.info("联系人创建异常,传入的jsonparm为"+jsonparm+"\n 错误信息为:"+e);
        }finally{
            logger.info("V3联系人创建成功----->"+backInfo);
            return backInfo;
        }
    }

    /*********************
     * PS删除联系人的接口
     * @param jsonparm 手机端传入参数
     *                 参数含义如下：{"createrId":"12121","createrFrom":"PS","contactId":"235345","contactFrom":"PS"}
     * @return
     */
    @Transactional(readOnly=false)
    public String RemoveContact(String jsonparm){
        logger.info("V3 删除联系人接口----->"+jsonparm);
        String backInfo="";
        try{
            if(Strings.isNullOrEmpty(jsonparm)){
                Map<String,String> map=new HashMap<String,String>();
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"jsonparm值为空");
                backInfo=JsonMapper.nonEmptyMapper().toJson(map);
            }else{
                Map<String,String> jsonMap=JsonMapper.nonEmptyMapper().fromJson(jsonparm,Map.class);
                if(!jsonMap.containsKey("createrId")||!jsonMap.containsKey("createrFrom")||!jsonMap.containsKey("contactId")||!jsonMap.containsKey("contactFrom")){
                    Map<String,String> map=new HashMap<String,String>();
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"创建者或加入的联系人为空");
                    backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                }
                String createrId = jsonMap.get("createrId");
                String createrFrom = jsonMap.get("createrFrom");
                String contactId = jsonMap.get("contactId");
                String contactFrom = jsonMap.get("contactFrom");
                if(Strings.isNullOrEmpty(createrId)||Strings.isNullOrEmpty(createrFrom)||Strings.isNullOrEmpty(contactId)||Strings.isNullOrEmpty(contactFrom)){
                    Map<String,String> map=new HashMap<String,String>();
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"创建者或加入的联系人为空");
                    backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                }else{
                    StringBuffer sbf = new StringBuffer();
                    this.deleteByParms(createrId,createrFrom,contactId,contactFrom);
                    Map<String,String> map=new HashMap<String,String>();
                    map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                    map.put(Setting.MESSAGE,"联系人删除成功");
                    backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                }
            }
        }catch (Exception e) {
            Map<String,String> map=new HashMap<String,String>();
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"联系人删除异常");
            backInfo=JsonMapper.nonEmptyMapper().toJson(map);
            logger.info("联系人删除异常,传入的jsonparm为"+jsonparm+"\n 错误信息为:"+e);
        }finally{
            logger.info("V3获取用户上下级关系返回的数据----->"+backInfo);
            return backInfo;
        }
    }

    /*********************
     *  PS查询用户信息
     * @param jsonparm 手机端传入参数
     *                 参数含义如下：{"sysType":"PS","userName":"张"}
     * @return
     */
    public String FindUserByName(String jsonparm){
        logger.info("V3 查询联系人接口----->"+jsonparm);
        String backInfo="";
        try{
            if(Strings.isNullOrEmpty(jsonparm)){
                Map<String,String> map=new HashMap<String,String>();
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"jsonparm值为空");
                backInfo=JsonMapper.nonEmptyMapper().toJson(map);
            }else{
                Map<String,String> jsonMap=JsonMapper.nonEmptyMapper().fromJson(jsonparm,Map.class);
                String userName = null,sysType=null,phone=null,pageNum=null,pageSize=null;
                int page = 0,end = 0;
                List<KnUser> list =null;
                if(jsonMap.containsKey("sysType")){
                    sysType = jsonMap.get("sysType");
                }
                if(jsonMap.containsKey("userName")){
                    userName = jsonMap.get("userName");
                }
                if(jsonMap.containsKey("phone")){
                    phone = jsonMap.get("phone");
                }
                if(jsonMap.containsKey("pageNum")){
                    pageNum = jsonMap.get("pageNum");
                }
                if(jsonMap.containsKey("pageSize")){
                    pageSize = jsonMap.get("pageSize");
                }
                if( Strings.isNullOrEmpty(pageNum) || Strings.isNullOrEmpty(pageSize)){//不分页
                    list = this.getInfoByUserName(userName,0,0);
                }else{
                    page =  (Integer.parseInt(pageNum)-1) * Integer.parseInt(pageSize) ;
                    end =  Integer.parseInt(pageSize) ;
                    list = this.getInfoByUserName(userName,page,end);
                }
                Long[] ids=new Long[list.size()];
                Map<Long,String> userMap=new HashMap<Long,String>();
                for(int ic=0;ic<list.size();ic++){
                    KnUser ku=list.get(ic);
                    ids[ic]=ku.getId();
                    userMap.put(ku.getId(),ku.getName());
                }
                Map<String,Object> searchParms=new HashMap<String,Object>();
                if(!Strings.isNullOrEmpty(phone)){
                    searchParms.put("phone",phone);
                }
                if(!Strings.isNullOrEmpty(sysType)&&!"ALL".equalsIgnoreCase(sysType)){
                    searchParms.put("fromSystem",sysType.toUpperCase());
                }
                List<KnEmployee> knEmployees=this.getKnEmployeesByParms(searchParms,ids);
                List<Map> returnList=new ArrayList<Map>();
                for(KnEmployee ke:knEmployees){
                    Map kv=new HashMap();
                    kv.put("id",ke.getId());
                    kv.put("userId",ke.getUserId());
                    kv.put("fullName",userMap.get(ke.getId()));
                    kv.put("userSystem",ke.getUserSystem());
                    kv.put("userType",ke.getUserType());
                    kv.put("imgAddress",ke.getImageAddress());
                    returnList.add(kv);
                }
                Map<String,Object> map=new HashMap<String,Object>();
                map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                map.put("user", list);
                backInfo=JsonMapper.nonEmptyMapper().toJson(map);
            }
        }catch (Exception e) {
            Map<String,String> map=new HashMap<String,String>();
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"查询用户异常");
            backInfo=JsonMapper.nonEmptyMapper().toJson(map);
            logger.info("查询用户异常异常,传入的jsonparm为"+jsonparm+"\n 错误信息为:"+e);
        }finally{
            logger.info("V3查询用户数据----->"+backInfo);
            return backInfo;
        }
    }

    /*********************
     *   PS人员简介
     * @param jsonparm 手机端传入参数
     *                 参数含义如下：{"userId":"100070"}
     * @return
     */
    public String GetUserDetail(String jsonparm){
        logger.info("V3 获取个人简要信息接口----->"+jsonparm);
        String backInfo="";
        try{
            if(Strings.isNullOrEmpty(jsonparm)){
                Map<String,String> map=new HashMap<String,String>();
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"jsonparm值为空");
                backInfo=JsonMapper.nonEmptyMapper().toJson(map);
            }else{
                Map<String,String> jsonMap=JsonMapper.nonEmptyMapper().fromJson(jsonparm,Map.class);
                String userId = null;
                if(!jsonMap.containsKey("userId")){
                    Map<String,String> map=new HashMap<String,String>();
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"jsonparm中的userId为空");
                    backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                }else{
                    userId = jsonMap.get("userId");
                    if(Strings.isNullOrEmpty(userId)) {
                        Map<String,String> map=new HashMap<String,String>();
                        map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                        map.put(Setting.MESSAGE,"jsonparm中的userId为空");
                        backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                    }else{
                        String returnData = getUserInfoDetail(userId);
                        Map json=JsonMapper.nonEmptyMapper().fromJson(returnData,Map.class);
                        List ja = (ArrayList)json.get("listdata");
                        List tempJa=new ArrayList();
                        if(ja!=null){
                            for(int i=0;i<ja.size();i++){
                                Map temp = (Map)ja.get(i);
                                String OPRID = temp.get("OPRID").toString().trim().toUpperCase();
                                List<KnUserThirdInfo> list = this.getInfoByUaccount(OPRID);
                                if(list!=null&&list.size()!=0){
                                    KnUserThirdInfo user = null ;
                                    KnEmployee ui=null ;
                                    String markName ;
                                    String empId = "";
                                    for (KnUserThirdInfo userThirdInfo : list) {
                                        markName = userThirdInfo.getMarkName();
                                        user = userThirdInfo;
                                        if(Strings.isNullOrEmpty(markName)){//不存在markName
                                            user = userThirdInfo;
                                        }
                                        if(psFromSys.equalsIgnoreCase(userThirdInfo.getFromSys())){//只是查找PS用户的信息,将PS的empId放入userType中
                                            empId = userThirdInfo.getUserType();
                                        }
                                    }
                                    if(Strings.isNullOrEmpty(user.getMarkName())){//没有关联的用户,只有一个,直接在UserInfo中获取即可
                                        List<KnUser> userList = this.getInfoByLoginName(OPRID);
                                        Long[] ids=new Long[userList.size()];
                                        for(int ic=0;ic<userList.size();ic++){
                                            KnUser ku=userList.get(ic);
                                            ids[ic]=ku.getId();
                                        }
                                        List<KnEmployee> knEmployees=this.getInfoByIds(ids);
                                        if(knEmployees.size()!=0){
                                            ui = knEmployees.get(0);
                                        }
                                    }else{//在UserInfo 中获取markName一致的用户信息
                                        List<KnEmployee> userList = this.getInfoByMarkName(user.getMarkName());
                                        if(userList.size()!=0){
                                            ui = userList.get(0);
                                        }
                                    }
                                    if(ui!=null){
                                        temp.put("imgAddress", ui.getImageAddress());
                                        temp.put("userType", empId);
                                        temp.put("id", ui.getId());
                                    }else{
                                        temp.put("imgAddress","");
                                        temp.put("userType", empId);
                                        temp.put("id", "");
                                    }
                                }else{
                                    temp.put("imgAddress","");
                                    temp.put("userType", "");
                                    temp.put("id", "");
                                }
                                tempJa.add(temp);
                            }
                        }
                        json.put("listdata", ja);
                        Map valueMap=new HashMap();
                        valueMap.put("psData",json);
                        valueMap.put(Setting.RESULTCODE, Setting.SUCCESSSTAT);
                        backInfo=JsonMapper.nonEmptyMapper().toJson(valueMap);
                    }
                }
            }
        }catch (Exception e) {
            Map<String,String> map=new HashMap<String,String>();
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"查询用户异常");
            backInfo=JsonMapper.nonEmptyMapper().toJson(map);
            logger.info("查询用户异常异常,传入的jsonparm为"+jsonparm+"\n 错误信息为:"+e);
        }finally{
            logger.info("V3查询用户数据----->"+backInfo);
            return backInfo;
        }
    }

    /*********************
     * 获取联系人头像地址
     * @param jsonparm 手机端传入参数
     *                 参数含义如下：{"userId":"1110”,”fromSys”:”PS”}
     * @return
     */
    public String GetUserImg(String jsonparm){
        logger.info("V3 获取联系人头像地址信息接口----->"+jsonparm);
        String backInfo="";
        try{
            if(Strings.isNullOrEmpty(jsonparm)){
                Map<String,String> map=new HashMap<String,String>();
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"jsonparm值为空");
                backInfo=JsonMapper.nonEmptyMapper().toJson(map);
            }else{
                Map<String,String> jsonMap=JsonMapper.nonEmptyMapper().fromJson(jsonparm,Map.class);
                String userId = null,fromSys=null;
                if(!jsonMap.containsKey("userId")){
                    Map<String,String> map=new HashMap<String,String>();
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"jsonparm中的userId为空");
                    backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                }
                userId = jsonMap.get("userId");
                if(jsonMap.containsKey("fromSys")){
                    fromSys = jsonMap.get("fromSys");
                }
                if(Strings.isNullOrEmpty(userId)) {
                    Map<String,String> map=new HashMap<String,String>();
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"jsonparm中的userId为空");
                    backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                }
                List<String> objList = this.getImageAddressByUserIdAndFromSys(userId,fromSys);
                Map<String,String> map=new HashMap<String,String>();
                if(objList==null||objList.size()==0){
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"用户不存在");
                }else{
                    map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                    map.put("imgAddress", objList.get(0)==null?"": objList.get(0).toString());
                }
                backInfo=JsonMapper.nonEmptyMapper().toJson(map);
            }
        }catch (Exception e) {
            Map<String,String> map=new HashMap<String,String>();
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"获取联系人头像地址信息接口异常");
            backInfo=JsonMapper.nonEmptyMapper().toJson(map);
            logger.info(" 获取联系人头像地址信息接口异常,传入的jsonparm为"+jsonparm+"\n 错误信息为:"+e);
        }finally{
            logger.info("V3 获取联系人头像地址信息接口数据----->"+backInfo);
            return backInfo;
        }
    }

    /*********************
     * 获取rest的数据信息
     * @param jsonparm 手机端传入参数
     *                 参数含义如下：
     * @return
     */
    public String GetRestData(String jsonparm){
        logger.info(" 获取rest的数据信息接口----->"+jsonparm);
        String backInfo="";
        try{
            if(Strings.isNullOrEmpty(jsonparm)){
                Map<String,String> map=new HashMap<String,String>();
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"jsonparm值为空");
                backInfo=JsonMapper.nonEmptyMapper().toJson(map);
            }else{
                // 传的JSON
                //String paras = "data=" + URLEncoder.encode(jsonparm,"UTF-8");
                String paras = "data=" + jsonparm;
                //TODO
                //dengfeng rest请求改为post方式提交
                backInfo = RestServiceUtil.getStringResponse(paras,restPsUserUrl,true);
            }
        }catch (Exception e) {
            Map<String,String> map=new HashMap<String,String>();
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"获取rest的数据信息异常");
            backInfo=JsonMapper.nonEmptyMapper().toJson(map);
            logger.info(" 获取rest的数据信息异常,传入的jsonparm为"+jsonparm+"\n 错误信息为:"+e);
        }finally{
            logger.info("V3查询用户数据----->"+backInfo);
            return backInfo;
        }
    }

    /********************
     * 获取rest的数据信息后迭代获取后查询数据库获取用户头像等信息
     * @param jsonparm
     * @return
     */
    public String GetDataRoll(String jsonparm){
        logger.info("V3 获取rest的数据信息接口----->"+jsonparm);
        String backInfo="";
        try{
            if(Strings.isNullOrEmpty(jsonparm)){
                Map<String,String> map=new HashMap<String,String>();
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"jsonparm值为空");
                backInfo=JsonMapper.nonEmptyMapper().toJson(map);
            }else{
                Map<String,String> jsonMap=JsonMapper.nonEmptyMapper().fromJson(jsonparm,Map.class);
                if(!jsonMap.containsKey("service")){
                    Map<String,String> map=new HashMap<String,String>();
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"jsonparm中的service为空");
                    backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                }
                // 传的JSON
                String paras = "data=" + URLEncoder.encode(jsonparm, "UTF-8");
                //TODO
                //dengfeng rest请求改为post方式提交
                String returnData = RestServiceUtil.getStringResponse(paras, restPsUserUrl,true);
                Map<String,Object> json = JsonMapper.nonEmptyMapper().fromJson(returnData,Map.class);
                String returnType = json.get("status").toString().trim();
                if("success".equalsIgnoreCase(returnType)){
                    List ja = (ArrayList)json.get("listdata");
                    List tempJa = new ArrayList();
                    if(ja!=null){
                        for(int i=0;i<ja.size();i++){
                            Map temp = (Map)ja.get(i);
                            String OPRID = temp.get("OPRID").toString().trim().toUpperCase();
                            List<KnUserThirdInfo> list = this.getInfoByUaccount(OPRID);
                            if(list!=null&&list.size()!=0){
                                KnUserThirdInfo user = null ;
                                KnEmployee ui=null ;
                                String markName ;
                                String empId = "";
                                for (KnUserThirdInfo userThirdInfo : list) {
                                    markName = userThirdInfo.getMarkName();
                                    user = userThirdInfo;
                                    if(Strings.isNullOrEmpty(markName)){//不存在markName
                                        user = userThirdInfo;
                                    }
                                    if(psFromSys.equalsIgnoreCase(userThirdInfo.getFromSys())){//只是查找PS用户的信息,将PS的empId放入userType中
                                        empId = userThirdInfo.getUserType();
                                    }
                                }
                                if(Strings.isNullOrEmpty(user.getMarkName())){//没有关联的用户,只有一个,直接在UserInfo中获取即可
                                    List<KnUser> userList = this.getInfoByLoginName(OPRID);
                                    Long[] ids=new Long[userList.size()];
                                    for(int ic=0;ic<userList.size();ic++){
                                        KnUser ku=userList.get(ic);
                                        ids[ic]=ku.getId();
                                    }
                                    List<KnEmployee> knEmployees=this.getInfoByIds(ids);
                                    if(knEmployees.size()!=0){
                                        ui = knEmployees.get(0);
                                    }
                                }else{//在UserInfo 中获取markName一致的用户信息
                                    List<KnEmployee> userList = this.getInfoByMarkName(user.getMarkName());
                                    if(userList.size()!=0){
                                        ui = userList.get(0);
                                    }
                                }
                                if(ui!=null){
                                    temp.put("imgAddress", ui.getImageAddress());
                                    temp.put("userType", empId);
                                    temp.put("id", ui.getId());
                                }else{
                                    temp.put("imgAddress","");
                                    temp.put("userType", empId);
                                    temp.put("id", "");
                                }
                            }else{
                                temp.put("imgAddress","");
                                temp.put("userType", "");
                                temp.put("id", "");
                            }
                            tempJa.add(temp);
                        }
                    }
                    json.put("listdata", ja);
                    backInfo=JsonMapper.nonEmptyMapper().toJson(json);
                }
            }
        }catch (Exception e) {
            Map<String,String> map=new HashMap<String,String>();
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"获取rest的数据信息异常");
            backInfo=JsonMapper.nonEmptyMapper().toJson(map);
            logger.info(" 获取rest的数据信息异常,传入的jsonparm为"+jsonparm+"\n 错误信息为:"+e);
        }finally{
            logger.info("V3 V3查询用户数据----->"+backInfo);
            return backInfo;
        }
    }


    /**
     * 获取上下级联系人的关系
     * 用户的员工id
     * @return 数据的json集合
     */
    private String getUndUserInfo(String emplid){
        if(emplid==null){
            return "";
        }
        String serviceName = "PSHR_INT_PKG.getunduserinfo";
        String returnData = "";
        try {
            Map jo = new HashMap();
            jo.put("p_emplid", emplid);
            String postData = RestServiceUtil.getRequestString(serviceName,jo,"PS");
            // 传的JSON
            //String paras = "data=" + URLEncoder.encode(postData,"UTF-8");
            String paras = "data=" + postData;
            //TODO
            //dengfeng rest请求改为post方式提交
            returnData = RestServiceUtil.getStringResponse(paras, restPsUserUrl,true);
        } catch (Exception e) {
            logger.info("获取用户上下级关系出错:"+e);
            returnData = "";
            e.printStackTrace();
        } finally {
            return returnData.trim();
        }
    }

    /**
     * 获取联系人的信息
     * @param userId 用户的userId,即:p_oprid
     * @return
     */
    private String getUserInfoDetail(String userId){
        if(Strings.isNullOrEmpty(userId)){
            return "";
        }
        String returnData = "";
        try {
            Map jo = new HashMap();
            jo.put("p_emplid", userId==null?"":userId.toUpperCase());//变成大写进行传输
            String postData = RestServiceUtil.getRequestString("pshr_int_pkg.getoprinfo", jo,"PS");// 传的JSON
            //String paras = "data=" + URLEncoder.encode(postData, "UTF-8");
            String paras = "data=" + postData;
            //TODO
            //dengfeng rest请求改为post方式提交
            returnData = RestServiceUtil.getStringResponse(paras, restPsUserUrl,true);
        } catch (Exception e) {
            returnData="";
            logger.info("获取用户详情出错,传输的用户名p_oprid为"+userId+"异常信息为:"+e);
        } finally{
            return returnData.trim();
        }
    }


    /**
     *
     * @author kongjiangwei
     * @param uaccount
     * 用于提供外部服务接口
     * @return
     */
    private List<KnUserThirdInfo> getInfoByUaccount(final String uaccount){
        return knUserThirdInfoDao.findAll(new Specification<KnUserThirdInfo>(){
            @Override public Predicate toPredicate(Root<KnUserThirdInfo> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                expressions.add(cb.equal(cb.upper(root.<String>get("uaccount")),uaccount));
                return predicate;
            }
        });
    }

    /******************
     * 通过登录名忽略大小写查询
     * @author kongjiangwei
     * @param uaccount
     * 用于提供外部服务接口
     * @return
     */
    private List<KnUser> getInfoByLoginName(final String uaccount){
        return knUserDao.findAll(new Specification<KnUser>(){
            @Override public Predicate toPredicate(Root<KnUser> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                expressions.add(cb.equal(cb.upper(root.<String>get("loginName")),uaccount));
                return predicate;
            }
        });
    }

    /******************
     * 通过用户名忽略大小写模糊查询
     * @author kongjiangwei
     * @param name
     * 用于提供外部服务接口
     * @return
     */
    private List<KnUser> getInfoByUserName(final String name,int page,int pageSize){
        Pageable pager=null;
        if(pageSize!=0){
            pager=new PageRequest(page,pageSize);
        }
        List<KnUser> list=null;
        if(pager!=null){
            Page<KnUser> arr=knUserDao.findAll(new Specification<KnUser>(){
                @Override public Predicate toPredicate(Root<KnUser> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                    Predicate predicate=cb.conjunction();
                    List<Expression<Boolean>> expressions=predicate.getExpressions();
                    expressions.add(cb.like(cb.upper(root.<String>get("name")),name.trim().toUpperCase()));
                    return predicate;
                }
            },pager);
            list=arr.getContent();
        }else{
            list=knUserDao.findAll(new Specification<KnUser>(){
                @Override public Predicate toPredicate(Root<KnUser> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                    Predicate predicate=cb.conjunction();
                    List<Expression<Boolean>> expressions=predicate.getExpressions();
                    expressions.add(cb.like(cb.upper(root.<String>get("name")),name.trim().toUpperCase()));
                    return predicate;
                }
            });
        }
        return list;
    }
    /*******************
     * 通过Ids及一些条件模糊查询
     * @author kongjiangwei
     * @param searchParams
     * @param ids
     * 用于提供外部服务接口
     * @return
     */
    private List<KnEmployee> getKnEmployeesByParms(final Map<String,Object> searchParams,final Long[] ids){
        return knEmployeeDao.findAll(new Specification<KnEmployee>(){
            @Override public Predicate toPredicate(Root<KnEmployee> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                Expression<Long> ep=root.<Long>get("id");
                expressions.add(ep.in(ids));
                if(searchParams!=null&&searchParams.size()!=0){
                    if(searchParams.containsKey("phone")&&!Strings.isNullOrEmpty(searchParams.get("phone").toString())){
                        expressions.add(cb.like(root.<String>get("phone"),"%"+searchParams.get("phone").toString().trim().toUpperCase()+"%"));
                    }
                    if(searchParams.containsKey("fromSystem")&&!Strings.isNullOrEmpty(searchParams.get("fromSystem").toString().trim())){
                        expressions.add(cb.equal(root.<String>get("userSystem"),searchParams.get("fromSystem").toString().trim()));
                    }
                }
                return predicate;
            }
        });
    }
    /*********************
     * 获取头像信息
     * @author kongjiangwei
     * @param userId
     * @param fromSys
     * 用于提供外部服务接口
     * @return
     */
    private List<String> getImageAddressByUserIdAndFromSys(final String userId,final String fromSys){
        List<KnEmployee> list=knEmployeeDao.findAll(new Specification<KnEmployee>(){
            @Override public Predicate toPredicate(Root<KnEmployee> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                expressions.add(cb.equal(root.<String>get("userId"),userId.trim()));
                if(Strings.isNullOrEmpty(fromSys.trim())){
                    expressions.add(cb.equal(root.<String>get("userSystem"),fromSys.trim()));
                }
                return predicate;
            }
        });
        List<String> ret=new ArrayList<String>();
        for(KnEmployee ke:list){
            ret.add(ke.getImageAddress());
        }
        return ret;
    }
    /***************************
     * 通过ID集合获取KnEmployee信息
     * @author kongjiangwei
     * @param ids
     * 用于提供外部服务接口
     * @return
     */
    private List<KnEmployee> getInfoByIds(Long[] ids){
        return knEmployeeDao.findByIds(ids);
    }

    /***************************
     * 通过markName获取KnEmployee信息
     * @author kongjiangwei
     * @param markName
     * 用于提供外部服务接口
     * @return
     */
    private List<KnEmployee> getInfoByMarkName(String markName){
        return knEmployeeDao.findByMarkName(markName);
    }

    /***************************
     * 通过用户ID与来自系统获取KnEmployee信息
     * @author kongjiangwei
     * @param userId
     * @param userSystem
     * 用于提供外部服务接口
     * @return
     */
    private List<KnEmployee> getInfoByUserIdAndFromSys(String userId,String userSystem){
        return knEmployeeDao.findByUserIdAndUserSystem(userId,userSystem);
    }


    /****************************
     * 通过创建者ID与来自系统获取信息
     * @author kongjiangwei
     * @param createrId
     * @param sys
     * 用于提供外部服务接口
     * @return
     */
    private List<KnPsUserContactInfo> getInfoByCreaterIdAndSys(final String createrId,final String sys){
        return knPsUserContactInfoDao.findAll(new Specification<KnPsUserContactInfo>(){
            @Override public Predicate toPredicate(Root<KnPsUserContactInfo> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                expressions.add(cb.equal(root.<String>get("createrId"),createrId));
                expressions.add(cb.equal(root.<String>get("createrFrom"),sys));
                return predicate;
            }
        });
    }

    /****************************
     * 保存信息
     * @author kongjiangwei
     * @param info
     * 用于提供外部服务接口
     * @return
     */
    private KnPsUserContactInfo save(KnPsUserContactInfo info){
        return knPsUserContactInfoDao.save(info);
    }
    /*******************************
     * 通过条件删除
     * @param createrId
     * @param createrFrom
     * @param contactId
     * @param contactFrom
     * 用于提供外部服务接口
     */
    private void deleteByParms(String createrId,String createrFrom,String contactId,String contactFrom){
        knPsUserContactInfoDao.deleteInfoByParms(createrId,createrFrom,contactId,contactFrom);
    }

}
