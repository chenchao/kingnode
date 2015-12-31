package com.kingnode.third.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.third.dao.KnUserThirdInfoDao;
import com.kingnode.third.entity.KnUserThirdInfo;
import com.kingnode.xsimple.util.client.RestServiceUtil;
import org.hibernate.metamodel.domain.JavaType;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * 通用接口用户初始化
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Component @Transactional(readOnly=true)
public class ThirdSystemUserRestService{

    private static final long serialVersionUID = -6938651033707751642L;
    @Value("#{commonInfo['psSystemName']}")
    private  String  psSystemName;//PS的来自系统
    @Value("#{commonInfo['ebsSystemName']}")
    private  String  ebsSystemName;//EBS的来自系统
    @Value("#{commonInfo['restPsUserUrl']}")
    private  String  restPsUserUrl;//PS的来自系统
    //private static final String webServiceUrl = "http://test.kingnode.com:8230/Jersey_WebClient/restRequest.jsp";//rest访问地址,如果不传,默认获取的是EBS的rest地址
    //private static final String md5Encryption = "true";//用户密码是否需要MD5加密
    private static int httpTimeout = 1000000;
    private int page = 1;// 当前页
    private int pageSize = 15;// 每页取的数目
    private boolean flagUser = false;// 标识userThirdInfo是否有已经删除的数据
    private static final Logger log = Logger
            .getLogger(ThirdSystemUserRestService.class);
    private String errorMsg;

    private KnUserThirdInfoDao knUserThirdInfoDao;
    /*************
     * 根据传入的rest地址,来自系统等,获取用户的信息进行智能更新
     */
    @Transactional(readOnly=false)
    public Map getUserInfo(String jsonparm) {
        Map rtnMap=new HashMap();
        log.info("用户的rest同步开始");
        int sum=0,i=0;
        page = 1;// 当前页
        pageSize = 15;// 每页取的数目
        try {
            if(Strings.isNullOrEmpty(jsonparm)){
                rtnMap.put("stat", false);
                rtnMap.put("info","传输的jsonparm为空");
                return rtnMap;
            }
            Map jo = JsonMapper.nonEmptyMapper().fromJson(jsonparm,Map.class);
            String restUrl,fromSys,packageName,to;
            if(jo.containsKey("packageName")){
                packageName = jo.get("packageName").toString();
            }else{
                rtnMap.put("stat", false);
                rtnMap.put("info","传输jsonparm中的packageName为空");
                return rtnMap;
            }
            if(jo.containsKey("to")){
                to = jo.get("to").toString();
            }else{
                rtnMap.put("stat", false);
                rtnMap.put("info","传输jsonparm中的to为空");
                return rtnMap;
            }
            if(jo.containsKey("restUrl")){
                restUrl = jo.get("restUrl").toString();
            }else{
                restUrl = restPsUserUrl;
            }
            if(jo.containsKey("fromSys")){
                fromSys = jo.get("fromSys").toString().toUpperCase();
            }else{
                fromSys="REST";
            }
            while(true){
                if(i++/5==0){
                    Thread.sleep(500);
                }
                int f = synchronizeUser(restUrl,fromSys,packageName,to);
                if(f!=0&&f!=-1){
                    sum += f;
                }
                if(f<pageSize&&f!=-1){
                    break;
                }
            }
        } catch (Exception e) {
            log.info("当前页:"+(page-1)+e);
            rtnMap.put("stat", false);
            rtnMap.put("info","操作失敗<br />");
        }finally{
            log.info("用户的rest同步结束,成功的数目为:"+sum);
            if(Strings.isNullOrEmpty(errorMsg)){
                rtnMap.put("stat", true);
                rtnMap.put("info","用户的rest同步操作成功,成功的数目为:"+sum);
            }else if(!rtnMap.containsKey("stat")){
                rtnMap.put("stat", true);
                rtnMap.put("info","用户的rest同步操作成功,成功的数目为:"+sum+";每页取的数目为:"+pageSize+",失败的信息为:"+errorMsg);
            }
        }
        return rtnMap;
    }


    private int synchronizeUser(String restUrl,String fromSys,String packageName,String to) {
        int size=-1;
        try {
            Map jo = new HashMap();
            if(ebsSystemName.toUpperCase().equals(to.toUpperCase().trim())){
                jo.put("p_user_id","");
                jo.put("p_page_id",page++);
                jo.put("p_page_size_id",pageSize);
            }else if(psSystemName.toUpperCase().equals(to.toUpperCase().trim())){
                jo.put("p_page", page++);
                jo.put("p_page_cnt", pageSize);
            }
            String postData = RestServiceUtil.getRequestString(packageName,jo,to);
            // 传的JSON
            //String paras = "data=" + URLEncoder.encode(postData,"UTF-8");
            String paras = "data=" +postData;
            String returnData = RestServiceUtil.getStringResponse(paras,restUrl,true);
            String jsonStr = returnData;
            if (jsonStr != null && !"".equals(jsonStr.trim())) {
                Map jsonObj = JsonMapper.nonEmptyMapper().fromJson(jsonStr.trim(),Map.class);
                if (jsonObj.containsKey("status")&&"success".equalsIgnoreCase(jsonObj.get("status").toString())) {
                    List rtnJsonList=(ArrayList)jsonObj.get("listdata");
                    if(rtnJsonList!=null&&!rtnJsonList.isEmpty()){
                        size=rtnJsonList.size();
                    }
                    if (!flagUser) {
                        // 先删除数据库中关于此系统的职责信息
                        knUserThirdInfoDao.deleteUserInfo(fromSys);
                        flagUser = true;
                    }
                    saveUser(jsonObj,fromSys);//保存用户
                }
            }
        } catch (Exception e) {
            log.info("用户当前页:"+(page-1)+e);
            errorMsg+=("用户当前页:"+(page-1));
            size=-1;
        } finally {
            return size;
        }
    }


    /**
     * 根据json字符串保存UserThirdSystem对象
     *
     *
     */
    private void saveUser(Map obj,String fromSys) {
        // 迭代json中的数组
        List ja = (ArrayList)obj.get("listdata");
        List<String> userIdList = new ArrayList<String>();
        List<KnUserThirdInfo> uiList = new ArrayList<KnUserThirdInfo>();
        for (int i = 0; i < ja.size(); i++) {
            Map o = (HashMap) ja.get(i);
            String fullName = o.containsKey("NAME")?o.get("NAME").toString():"";//姓名
            String userId = o.containsKey("OPRID")?o.get("OPRID").toString():"";//员工id,唯一数据
            String userName = o.containsKey("OPRID")?o.get("OPRID").toString():"";//账号,用户的id进行的登录
            String password = null;
            KnUserThirdInfo user = new KnUserThirdInfo();
            if(o.containsKey("PASSWORD")){
                password = o.get("PASSWORD").toString();
                if(!Strings.isNullOrEmpty(password)){
                    user.setUpwd(password);
                }
            }
            String userType = o.containsKey("EMPLID")?o.get("EMPLID").toString():"";
            user.setFromSys(fromSys);
            user.setFullName(fullName);
            if(!Strings.isNullOrEmpty(userName)){
                user.setUaccount(userName);
            }
            user.setUserId(userId);
            if(!Strings.isNullOrEmpty(userType)){
                user.setUserType(userType);
            }
            uiList.add(user);
            userIdList.add(userId);
        }
        // 批量保存用户
        if(uiList.size()!=0){
            knUserThirdInfoDao.save(uiList);
        }
    }

    @Autowired
    public void setKnUserThirdInfoDao(KnUserThirdInfoDao knUserThirdInfoDao){
        this.knUserThirdInfoDao=knUserThirdInfoDao;
    }
}
