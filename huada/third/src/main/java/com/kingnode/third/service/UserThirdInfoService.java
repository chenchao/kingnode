package com.kingnode.third.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.common.base.Strings;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.diva.security.utils.Digests;
import com.kingnode.diva.utils.Encodes;
import com.kingnode.third.dao.KnReponsibilityThirdDao;
import com.kingnode.third.dao.KnUserReponsibilityThirdDao;
import com.kingnode.third.dao.KnUserThirdInfoDao;
import com.kingnode.third.entity.KnReponsibilityThird;
import com.kingnode.third.entity.KnUserReponsibilityThird;
import com.kingnode.third.entity.KnUserThirdInfo;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.dao.system.KnEmployeeDao;
import com.kingnode.xsimple.dao.system.KnUserDao;
import com.kingnode.xsimple.entity.IdEntity;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnUser;
import com.kingnode.xsimple.service.system.ResourceService;
import com.kingnode.xsimple.util.Users;
import com.kingnode.xsimple.util.client.RestServiceUtil;
import com.kingnode.xsimple.util.key.UuidMaker;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Component
@Transactional(readOnly = true)
public class UserThirdInfoService {
    private static final Logger log = Logger.getLogger(UserThirdInfoService.class);
    private int page = 1;// 当前页
    private int pageSize = 15;// 每页取的数目
    private int userTotal = 600;// 总数 ,默认是600
    private int userRespTotal = 100;// 用户职责的总数
    private String errorMsg;
    private boolean flagUser = false;// 标识userInfo是否有已经删除的数据
    private boolean flagResp = false;// 标识responsibility是否有已经删除的数据
    //private static final String fromSys = "ERP";//来自系统
    @Value("#{commonInfo['ebsSystemName']}")
    private String fromSys;
    //private static final String md5Encryption = "true";//用户密码是否需要MD5加密
    /*@Value("#{commonInfo['md5Encryption']}")
    private String md5Encryption;*/
    //private static final String restEbsUserUrl="http://test.kingnode.com:8230/Jersey_WebClient/restRequest.jsp";//rest访问地址
    @Value("#{commonInfo['restEbsUserUrl']}")
    private String restEbsUserUrl;
    private KnReponsibilityThirdDao knReponsibilityThirdDao;
    private KnUserDao knUserDao;
    private KnEmployeeDao knEmployeeDao;
    private KnUserThirdInfoDao knUserThirdInfoDao;
    private KnUserReponsibilityThirdDao knUserReponsibilityThirdDao;

    public DataTable<KnUserThirdInfo> UserThirdList(DataTable<KnUserThirdInfo> dt, final Map<String, Object> searchParams) {
        PageRequest pageRequest = new PageRequest(dt.pageNo(), dt.getiDisplayLength(), getSort(dt));
        Page<KnUserThirdInfo> page = knUserThirdInfoDao.findAll(new Specification<KnUserThirdInfo>() {
            @Override
            public Predicate toPredicate(Root<KnUserThirdInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                List<Expression<Boolean>> expressions = predicate.getExpressions();
                if (searchParams != null && searchParams.size() != 0) {
                    if (searchParams.containsKey("LIKE_fullName") && !Strings.isNullOrEmpty(searchParams.get("LIKE_fullName").toString().trim())) {
                        expressions.add(cb.like(cb.upper(root.<String>get("fullName")), "%" + searchParams.get("LIKE_fullName").toString().trim().toUpperCase() + "%"));
                    }
                    if (searchParams.containsKey("LIKE_uaccount") && !Strings.isNullOrEmpty(searchParams.get("LIKE_uaccount").toString().trim())) {
                        expressions.add(cb.like(cb.upper(root.<String>get("uaccount")), "%" + searchParams.get("LIKE_uaccount").toString().trim().toUpperCase() + "%"));
                    }
                    if (searchParams.containsKey("LIKE_userId") && !Strings.isNullOrEmpty(searchParams.get("LIKE_userId").toString().trim())) {
                        expressions.add(cb.like(cb.upper(root.<String>get("userId")), "%" + searchParams.get("LIKE_userId").toString().trim().toUpperCase() + "%"));
                    }
                    if (searchParams.containsKey("LIKE_userType") && !Strings.isNullOrEmpty(searchParams.get("LIKE_userType").toString().trim())) {
                        expressions.add(cb.like(cb.upper(root.<String>get("userType")), "%" + searchParams.get("LIKE_userType").toString().trim().toUpperCase() + "%"));
                    }
                    if (searchParams.containsKey("LIKE_fromSys") && !Strings.isNullOrEmpty(searchParams.get("LIKE_fromSys").toString().trim())) {
                        expressions.add(cb.like(cb.upper(root.<String>get("fromSys")), "%" + searchParams.get("LIKE_fromSys").toString().trim().toUpperCase() + "%"));
                    }
                }
                return predicate;
            }
        }, pageRequest);
        dt.setAaData(page.getContent());
        dt.setiTotalDisplayRecords(page.getTotalElements());
        return dt;
    }

    @Transactional(readOnly = false)
    public void DeleteUserThird(Long id) {
        knUserThirdInfoDao.delete(id);
    }

    @Transactional(readOnly = false)
    public void DeleteAllUserThird(List<Long> ids) {
        List<KnUserThirdInfo> delArr = (List<KnUserThirdInfo>) knUserThirdInfoDao.findAll(ids);
        knUserThirdInfoDao.delete(delArr);
    }

    @Transactional(readOnly = false)
    public Map ContactUser(List<Long> ids) {
        Map map = new HashMap();
        try {
            //查询用户中是否已经有了关联的信息字段,如果没有,新增关联,如果有,更新关联信息
            List<KnUserThirdInfo> userList = (List<KnUserThirdInfo>) knUserThirdInfoDao.findAll(ids);
            String uuid = null;
            for (KnUserThirdInfo userThirdInfo : userList) {
                if (!Strings.isNullOrEmpty(userThirdInfo.getMarkName())) {
                    uuid = userThirdInfo.getMarkName();
                    break;
                }
            }
            if (Strings.isNullOrEmpty(uuid)) {
                uuid = UuidMaker.getInstance().getUuid(true);
            }
            //设置关联
            int rtn = knUserThirdInfoDao.updateMarkName(uuid, ids);
            if (rtn > 0) {
                map.put("stat", true);
                map.put("info", "关联成功");
            } else {
                map.put("stat", false);
                map.put("info", "关联失败");
            }
        } catch (Exception ex) {
            map.put("stat", false);
            map.put("info", "关联失败");
        }
        return map;
    }

    /**
     *
     * 取消关联
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = false)
    public Map CancleContactUser(Long id) {
        Map map = new HashMap();
        KnUserThirdInfo knUserThirdInfo = knUserThirdInfoDao.findOne(id);
        String markName = knUserThirdInfo.getMarkName();
        if (Strings.isNullOrEmpty(markName)) {
            map.put("stat", false);
            map.put("info", "该用户无关联用户，不能取消关联。");
        } else {
            List<KnUserThirdInfo> list = knUserThirdInfoDao.findByMarkName(markName);
            if (list.size() <= 2) {
                List<Long> ids = new ArrayList<>();
                for (KnUserThirdInfo userThirdInfo : list) {
                    ids.add(userThirdInfo.getId());
                }
                int rtn = knUserThirdInfoDao.updateMarkName("", ids);
                if (rtn > 0) {
                    map.put("stat", true);
                    map.put("info", "取消关联成功");
                } else {
                    map.put("stat", false);
                    map.put("info", "取消关联失败");
                }
            } else {
                knUserThirdInfo.setMarkName("");
                KnUserThirdInfo user = knUserThirdInfoDao.save(knUserThirdInfo);
                if (user != null) {
                    map.put("stat", true);
                    map.put("info", "取消关联成功");
                } else {
                    map.put("stat", false);
                    map.put("info", "取消关联失败");
                }
            }
        }
        return map;
    }

    @Transactional(readOnly = false)
    public Map AddToUserInfo(List<Long> ids) {
        Map map = new HashMap();
        try {
            List<KnUserThirdInfo> userList = (List<KnUserThirdInfo>) knUserThirdInfoDao.findAll(ids);
            List<Object> list = new ArrayList<Object>();
            for (KnUserThirdInfo userThirdInfo : userList) {
                //查找UserInfo中是否有此账号信息,如果有,更新,没有则新增
                List<KnEmployee> employeeList = knEmployeeDao.findByUserIdAndUserSystem(userThirdInfo.getUserId(), userThirdInfo.getFromSys());
                List<Long> uids = new ArrayList<>();
                for (KnEmployee employee : employeeList) {
                    uids.add(employee.getId());
                }
                List<KnUser> users = (List<KnUser>) knUserDao.findAll(uids);
                KnEmployee knEmployee = null;
                KnUser knUser = null;
                if (users.size() == 0) {//新增的用户
                    knUser = new KnUser();
                    knEmployee = new KnEmployee();
                } else {
                    knUser = users.get(0);
                    knEmployee = employeeList.get(0);
                }
                knUser.setLoginName(userThirdInfo.getUaccount());
                knUser.setName(userThirdInfo.getFullName());
                knUser.setSalt(Encodes.encodeHex(Digests.generateSalt(ResourceService.SALT_SIZE)));
                if(!Strings.isNullOrEmpty(userThirdInfo.getUpwd())){
                    byte[] hashPassword=Digests.sha1(userThirdInfo.getUpwd().getBytes(),Encodes.decodeHex(knUser.getSalt()),1024);
                    knUser.setPassword(Encodes.encodeHex(hashPassword));
                }
                knUser.setEmail(userThirdInfo.getUaccount()+"@kingnode.com");
                knUser.setStatus(IdEntity.ActiveType.ENABLE);
                KnUser user = knUserDao.save(knUser);
                if (user != null) {
                    knEmployee.setId(user.getId());
                    knEmployee.setUserId(userThirdInfo.getUserId());
                    knEmployee.setLoginName(userThirdInfo.getUaccount());
                    knEmployee.setUserName(userThirdInfo.getFullName());
                    knEmployee.setEmail(user.getEmail());
                    knEmployee.setMarkName(userThirdInfo.getMarkName());
                    knEmployee.setUserType(userThirdInfo.getUserType());
                    knEmployee.setUserSystem(userThirdInfo.getFromSys());
                    knEmployee.setJob(IdEntity.ActiveType.ENABLE);
                    knEmployeeDao.save(knEmployee);
                }
            }
            map.put("stat", true);
            map.put("info", "导入成功");
        } catch (Exception e) {
            System.out.println(e);
            map.put("stat", false);
            map.put("info", "操作失败");
        } finally {
            return map;
        }
    }

    /**
     * ******************
     * 移除关联关系
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = false)
    public Map RemoveContactUser(Long id) {
        Map map = new HashMap();
        try {
            KnUserThirdInfo knUserThirdInfo = knUserThirdInfoDao.findOne(id);
            knUserThirdInfo.setMarkName("");
            knUserThirdInfoDao.save(knUserThirdInfo);
            map.put("stat", true);
            map.put("info", "取消关联成功");
        } catch (Exception e) {
            map.put("stat", true);
            map.put("info", "取消关联失败");
        }
        return map;
    }

    private Sort getSort(DataTable<KnUserThirdInfo> dt) {
        Sort.Direction d = Sort.Direction.DESC;
        if ("asc".equals(dt.getsSortDir_0())) {
            d = Sort.Direction.ASC;
        }
        String[] column = new String[]{"id", "fullName", "uaccount", "userId", "userType", "fromSys", "markName"};
        return new Sort(d, column[Integer.parseInt(dt.getiSortCol_0())]);
    }

    /**
     * 根据webservice地址获取用户的信息 同步EBS用户数据（ERP）
     */
    @Transactional(readOnly = false)
    public Map GetUserInfo() {
        Map map = new HashMap();
        log.info("EBS用户的rest同步开始");
        int sum = 0;
        try {
            page = 1;// 当前页
            pageSize = 15;// 每页取的数目
            boolean saveFlag = false;
            // 先获取总数信息
            boolean flag = synchronizeUser(saveFlag);
            if (flag) {
                sum += pageSize;
            }
            int pageNum = userTotal % pageSize == 0 ? userTotal / pageSize : userTotal / pageSize + 1;
            for (int i = page; i < pageNum + 1; i++) {
                if (i / 5 == 0) {
                    Thread.sleep(500);
                }
                boolean f = synchronizeUser(saveFlag);
                // getResponsibility();
                if (f) {
                    sum += pageSize;
                }
            }
        } catch (Exception e) {
            log.info("当前页:" + (page - 1) + e);
            map.put("stat", false);
            map.put("info", "操作失敗<br />");
        }
        log.info("EBS用户的rest同步结束,操作的总数有:" + userTotal + ",成功的数目小于:" + sum);
        if (Strings.isNullOrEmpty(errorMsg)) {
            map.put("stat", true);
            map.put("info", "操作成功,操作的总数有:" + userTotal + ",成功的数目小于:" + sum);
        } else {
            map.put("stat", true);
            map.put("info", "操作成功,操作的总数有:" + userTotal + ",成功的数目小于:" + sum + ";每页取的数目为:" + pageSize + ",失败的信息为:" + errorMsg);
        }
        return map;
    }

    private boolean synchronizeUser(boolean saveFlag) {
        boolean flag = false;
        String serviceName = "IYS_user_api2.getuserinfo";
        try {
            Map jo = new HashMap();
            jo.put("p_user_id", "");
            jo.put("p_page_id", page++);
            jo.put("p_page_size_id", pageSize);
            String postData = RestServiceUtil.getRequestString(serviceName, jo, "EBS");
            // 传的JSON
            //String paras="data="+URLEncoder.encode(postData,"UTF-8");
            String paras = "data=" + postData;
            //TODO
            //dengfeng rest请求改为post方式提交
            String returnData = RestServiceUtil.getStringResponse(paras, restEbsUserUrl,true);
            String jsonStr = returnData;
            if (jsonStr != null && !"".equals(jsonStr.trim())) {
                //将JSON类型的字符串转换为Map
                Map jsonObj = JsonMapper.nonEmptyMapper().fromJson(jsonStr.trim(), Map.class);
                if (jsonObj.containsKey("status") && "success".equals(jsonObj.get("status"))) {
                    if (!flagUser) {
                        userTotal = Integer.parseInt(jsonObj.get("size").toString());
                        // 先删除数据库中关于此系统的职责信息
                        knUserThirdInfoDao.deleteUserInfo(fromSys);
                        flagUser = true;
                    }
                    saveUser(jsonObj, saveFlag);//保存用户
                    flag = true;
                }
            }
            flag = true;
        } catch (Exception e) {
            log.info("用户当前页:" + (page - 1) + e);
            errorMsg += ("用户当前页:" + (page - 1));
            e.printStackTrace();
            flag = false;
        } finally {
            return flag;
        }
    }


    /**
     * 根据json字符串保存UserThirdSystem对象
     */
    private void saveUser(Map obj, boolean flag) {
        // 迭代json中的数组
        List ja = (ArrayList) obj.get("listdata");
        List<String> userIdList = new ArrayList<String>();
        List<KnUserThirdInfo> uiList = new ArrayList<KnUserThirdInfo>();
        for (int i = 0; i < ja.size(); i++) {
            Map o = (HashMap) ja.get(i);
            String fullName = o.get("FULL_NAME").toString();
            String userId = o.get("USER_ID").toString();
            String userName = o.get("USER_NAME").toString();
            String personId = o.get("PERSON_ID").toString();
            String password = null;
            KnUserThirdInfo user = new KnUserThirdInfo();
            if (o.containsKey("PASSWORD")) {
                password = o.get("PASSWORD").toString();
                if (!Strings.isNullOrEmpty(password)) {
                    /*if ("true".equalsIgnoreCase(md5Encryption)) {//MD5加密
                        password = DegistUtil.produceDegistCode(password);
                    }*/
                    user.setUpwd(password);
                }
            }
            String userType = o.get("USER_TYPE").toString();
            user.setFromSys(fromSys);
            user.setFullName(fullName);
            if (!Strings.isNullOrEmpty(userName)) {
                user.setUaccount(userName);
            }
            user.setUserId(userId);
            if (!Strings.isNullOrEmpty(userType)) {
                user.setUserType(userType);
            }
            // user.setPersonId(personId);
            uiList.add(user);
            userIdList.add(userId);
        }
        // 批量保存用户
        knUserThirdInfoDao.save(uiList);
        // 查找用户的职责
        if (flag) {
            for (String userId : userIdList) {
                getResponsiblityByUserId(userId);
            }
        }
    }

    /**
     * 根据用户id获取职责信息
     */
    private void getResponsiblityByUserId(String userId) {
        String serviceName = "IYS_user_resp_api2.getuserresp";
        try {
            Map jo = new HashMap();
            jo.put("p_user_id", userId);
            String postData = RestServiceUtil.getRequestString(serviceName, jo, "EBS");// 传的JSON
            //String paras = "data=" + URLEncoder.encode(postData, "UTF-8");
            String paras = "data=" + postData;
            //TODO
            //dengfeng rest请求改为post方式提交
            String returnData = RestServiceUtil.getStringResponse(paras, restEbsUserUrl,true);
            String jsonStr = returnData;
            if (jsonStr != null && !"".equals(jsonStr)) {
                Map jsonObj = JsonMapper.nonEmptyMapper().fromJson(jsonStr, Map.class);
                if (jsonObj.containsKey("status") && "success".equals(jsonObj.get("status"))) {
                    // 获取总数信息
                    if (!flagResp) {
                        // 先删除数据库中关于此系统的用户信息
                        knUserReponsibilityThirdDao.deleteResponsibility(fromSys);
                        flagResp = true;
                    }
                    saveUserResponsibility(jsonObj);// 保存用户的职责
                }
            }
        } catch (Exception e) {
            log.info("当前页:" + (page - 1) + "用户的id为" + userId + "用户的职责" + e);
            errorMsg += ("当前页:" + (page - 1) + "用户的id为" + userId + "用户的职责");
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * 根据json字符串保存UserResponsibilityThird对象
     */
    private void saveUserResponsibility(Map obj) {
        List ja = (ArrayList) obj.get("listdata");
        List<KnUserReponsibilityThird> list = new ArrayList<KnUserReponsibilityThird>();
        for (int i = 0; i < ja.size(); i++) {
            Map o = (HashMap) ja.get(i);
            String userId = o.get("USER_ID").toString();
            String responsibilityId = o.get("RESPONSIBILITY_ID").toString();
            String responsibilityName = o.get("RESPONSIBILITY_NAME").toString();
            String startDate = o.get("START_DATE").toString();
            String endDate = o.get("END_DATE").toString();
            // 保存到本地数据库中
            KnUserReponsibilityThird urt = new KnUserReponsibilityThird();
            urt.setUserId(userId);
            urt.setResponsibilityId(responsibilityId);
            urt.setResponsibilityName(responsibilityName);
            urt.setFromSys(fromSys);// 来自哪个系统
            urt.setEndDate(endDate);
            urt.setStartDate(startDate);
            list.add(urt);
        }
        // 批量保存
        knUserReponsibilityThirdDao.save(list);
    }

    /**
     * 同步EBS（ERP）,系统中的职责信息（全部同步）
     */
    public Map GetResponsibilityThird() {
        Map rtnMap = new HashMap();
        log.info("EBS职责的rest同步开始");
        int sum = 0;
        try {
            boolean flag = getResponsibility();
            if (flag) {
                sum += pageSize;
            }
            int pageNum = userRespTotal % pageSize == 0 ? userRespTotal
                    / pageSize : userRespTotal / pageSize + 1;
            for (int i = page; i < pageNum + 1; i++) {
                if (i / 5 == 0) {
                    Thread.sleep(500);
                }
                boolean f = getResponsibility();
                if (f) {
                    sum += pageSize;
                }
            }
        } catch (Exception e) {
            log.info("当前页:" + (page - 1) + e);
            rtnMap.put("stat", "false");
            rtnMap.put("info", "操作失敗<br />");
        }
        log.info("EBS职责的rest同步结束,操作的总数有:" + userRespTotal + ",成功的数目小于:" + sum);
        rtnMap.put("stat", "true");
        if (Strings.isNullOrEmpty(errorMsg)) {
            rtnMap.put("info", "操作成功,操作的总数有:" + userRespTotal + ",成功的数目小于:" + sum);
        } else {
            rtnMap.put("info", "操作成功,操作的总数有:" + userRespTotal + ",成功的数目小于:" + sum + "每页取的数目为:" + pageSize + ",失败的信息为:" + errorMsg);
        }
        return rtnMap;
    }

    /**
     * **********************
     * 根据本地的用户和职责的信息插入职责表中
     *
     * @return
     */
    public Map GetUserResponsibilityByLocal() {
        Map rtnMap = new HashMap();
        // 保存完用户的职责后查询加入职责表中
        List<KnUserThirdInfo> list = knUserThirdInfoDao.findByFromSys(fromSys);
        List<String> urt = new ArrayList<String>();
        for (KnUserThirdInfo info : list) {
            urt.add(info.getUserId());
        }
        int i = 0;
        log.info("EBS用户职责的rest同步开始");
        if (urt != null && urt.size() > 0) {
            for (Iterator iter = urt.iterator(); iter.hasNext(); ) {
                if (i++ / 5 == 0) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        log.info(e.getMessage());
                    }
                }
                String ob = (String) iter.next();
                getResponsiblityByUserId(ob);
            }
        }
        log.info("EBS用户职责的rest同步结束");
        rtnMap.put("stat", true);
        rtnMap.put("info", "EBS用户职责的rest同步结束");
        return rtnMap;
    }

    /**
     * 根据webservice地址获取职责信息
     */
    private boolean getResponsibility() {
        String serviceName = "IYS_responsibility_api2.getresponsibility";
        boolean flag = false;
        try {
            Map jo = new HashMap();
            jo.put("p_page_ID", page++);
            jo.put("p_page_size_ID", pageSize);
            String postData = RestServiceUtil.getRequestString(serviceName, jo, "EBS");// 传的JSON
            //String paras = "data=" + URLEncoder.encode(postData, "UTF-8");
            String paras = "data=" + postData;
            //TODO
            //dengfeng rest请求改为post方式提交
            String returnData = RestServiceUtil.getStringResponse(paras, restEbsUserUrl,true);
            String jsonStr = returnData;
            jsonStr = jsonStr.trim();
            if (jsonStr != null && !"".equals(jsonStr)) {
                Map jsonObj = JsonMapper.nonEmptyMapper().fromJson(jsonStr, Map.class);
                if (jsonObj.containsKey("status") && "success".equals(jsonObj.get("status").toString())) {
                    // 获取总数信息
                    userRespTotal = Integer.parseInt(jsonObj.get("size").toString());
                    if (!flagUser) {
                        // 删除数据中关于此系统的职责
                        knUserReponsibilityThirdDao.deleteResponsibility(fromSys);
                        flagUser = true;
                    }
                    saveRespByWeb(jsonObj);// 保存用户
                    flag = true;
                }
            }
        } catch (Exception e) {
            log.info("职责当前页:" + (page - 1) + e);
            errorMsg += ("职责当前页:" + (page - 1));
            e.printStackTrace();
            flag = false;
        } finally {
            return flag;
        }
    }

    private void saveRespByWeb(Map jsonObj) {
        List ja = (ArrayList) jsonObj.get("listdata");
        List<KnReponsibilityThird> list = new ArrayList<KnReponsibilityThird>();
        for (int i = 0; i < ja.size(); i++) {
            Map o = (HashMap) ja.get(i);
            String responsibilityId = o.get("RESPONSIBILITY_ID").toString();
            String responsibilityName = o.get("RESPONSIBILITY_NAME").toString();
            // 保存到本地数据库中
            KnReponsibilityThird urt = new KnReponsibilityThird();
            urt.setResponsibilityId(responsibilityId);
            urt.setResponsibilityName(responsibilityName);
            urt.setFromSys(fromSys);// 来自哪个系统
            list.add(urt);
        }
        // 批量保存
        knReponsibilityThirdDao.save(list);
    }

    public DataTable<KnUserThirdInfo> PageRelUserList(KnEmployee emp, DataTable<KnUserThirdInfo> dt, Sort sort) {
        String markName = emp.getMarkName();
        String formSystem = emp.getUserSystem();
        String userId = emp.getUserId();
        List<KnUserThirdInfo> page = this.knUserThirdInfoDao.findByMarkName(markName);
        Iterator<KnUserThirdInfo> it = page.iterator();
        while (it.hasNext()) {
            KnUserThirdInfo user = it.next();
            String userId2 = user.getUserId();
            String fromSystem2 = user.getFromSys();
            if (formSystem != null && userId != null && userId2 != null && fromSystem2 != null) {
                if (formSystem.equals(fromSystem2) && userId.equals(userId2)) {
                    it.remove();
                }
            }
        }
        dt.setiTotalDisplayRecords(page.size());
        dt.setiTotalRecords(page.size());
        dt.setAaData(page);
        return dt;
    }


    public List<KnUserThirdInfo> ListRelUsers(KnEmployee emp) {
        String markName = emp.getMarkName();
        String formSystem = emp.getUserSystem();
        String userId = emp.getUserId();
        List<KnUserThirdInfo> userList = this.knUserThirdInfoDao.findByMarkName(markName);
        Iterator<KnUserThirdInfo> it = userList.iterator();
        while (it.hasNext()) {
            KnUserThirdInfo user = it.next();
            String userId2 = user.getUserId();
            String fromSystem2 = user.getFromSys();
            if (formSystem != null && userId != null && userId2 != null && fromSystem2 != null) {
                if (formSystem.equals(fromSystem2) && userId.equals(userId2)) {
                    it.remove();
                }
            }
        }
        return userList;
    }
    /**
     * 根据登录的用户信息,获取当前用户关联的用户信息集合
     * @return
     */
    public List<KnUserThirdInfo> FindKnUserThirdByLoginUser(){
        List<KnUserThirdInfo> list = null;
        final KnEmployee knEmployee = knEmployeeDao.findOne(Users.id());
        if(knEmployee==null){
            return list;
        }
        if(!Strings.isNullOrEmpty(knEmployee.getMarkName())){//有两个用户信息
            list=knUserThirdInfoDao.findAll(new Specification<KnUserThirdInfo>(){
                @Override public Predicate toPredicate(Root<KnUserThirdInfo> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                    Predicate predicate=cb.conjunction();
                    List<Expression<Boolean>> expressions=predicate.getExpressions();
                    expressions.add(cb.equal(root.<String>get("markName"),knEmployee.getMarkName()));
                    expressions.add(cb.notEqual(root.<String>get("fromSys"),knEmployee.getUserSystem()));
                    return predicate;
                }
            });
        }
        return list;
    }

    @Autowired
    public void setKnUserThirdInfoDao(KnUserThirdInfoDao knUserThirdInfoDao) {
        this.knUserThirdInfoDao = knUserThirdInfoDao;
    }

    @Autowired
    public void setKnUserReponsibilityThirdDao(KnUserReponsibilityThirdDao knUserReponsibilityThirdDao) {
        this.knUserReponsibilityThirdDao = knUserReponsibilityThirdDao;
    }

    @Autowired
    public void setKnReponsibilityThirdDao(KnReponsibilityThirdDao knReponsibilityThirdDao) {
        this.knReponsibilityThirdDao = knReponsibilityThirdDao;
    }

    @Autowired
    public void setKnUserDao(KnUserDao knUserDao) {
        this.knUserDao = knUserDao;
    }

    @Autowired
    public void setKnEmployeeDao(KnEmployeeDao knEmployeeDao) {
        this.knEmployeeDao = knEmployeeDao;
    }
}
