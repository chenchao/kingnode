package com.kingnode.third.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.base.Strings;
import com.kingnode.diva.security.utils.Digests;
import com.kingnode.diva.utils.Encodes;
import com.kingnode.third.dao.KnUserSetupInfoDao;
import com.kingnode.third.dao.KnUserThirdInfoDao;
import com.kingnode.third.entity.KnUserSetupInfo;
import com.kingnode.third.entity.KnUserThirdInfo;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.api.system.EmpOrg;
import com.kingnode.xsimple.api.system.EmpPos;
import com.kingnode.xsimple.dao.system.KnEmployeeDao;
import com.kingnode.xsimple.dao.system.KnUserDao;
import com.kingnode.xsimple.entity.IdEntity;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnUser;
import com.kingnode.xsimple.service.system.OrganizationService;
import com.kingnode.xsimple.service.system.ResourceService;
import com.kingnode.xsimple.util.key.UuidMaker;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Component @Transactional(readOnly=true)
public class SysUserSetupInfoService{
    private static final Logger log=Logger.getLogger(SysUserSetupInfoService.class);
    private KnUserSetupInfoDao knUserSetupInfoDao;
    private KnEmployeeDao knEmployeeDao;
    private KnUserDao knUserDao;
    private KnUserThirdInfoDao knUserThirdInfoDao;
    @Autowired
    private OrganizationService organizationService;
    public DataTable<KnUserSetupInfo> SysUserSetUpList(DataTable<KnUserSetupInfo> dt){
        Pageable pager=new PageRequest(dt.pageNo(),dt.getiDisplayLength());
        Page<KnUserSetupInfo> page=knUserSetupInfoDao.findAll(pager);
        dt.setAaData(page.getContent());
        dt.setiTotalDisplayRecords(page.getTotalElements());
        return dt;
    }
    @Transactional(readOnly=false)
    public KnUserSetupInfo Save(KnUserSetupInfo ku){
        return knUserSetupInfoDao.save(ku);
    }
    @Transactional(readOnly=false)
    public void DeleteSys(Long id){
        knUserSetupInfoDao.delete(id);
    }
    public KnUserSetupInfo FindOneById(Long id){
        return knUserSetupInfoDao.findOne(id);
    }
    @Transactional(readOnly=false)
    public void DeleteAllSys(List<Long> ids){
        List<KnUserSetupInfo> delArr=(List<KnUserSetupInfo>)knUserSetupInfoDao.findAll(ids);
        knUserSetupInfoDao.delete(delArr);
    }
    @Transactional(readOnly=false)
    public Map AddToUserInfo(List<Long> ids){
        Map rtn=new HashMap();
        try{
            //获取要导入的第三方信息集合
            List<KnUserSetupInfo> setupList=(List<KnUserSetupInfo>)knUserSetupInfoDao.findAll(ids);
            //要导入系统的名称集合
            List<String> inParm=new ArrayList<String>();
            //来自系统和系统类型
            Map<String,Setting.ThirdSystemType> map=new HashMap<String,Setting.ThirdSystemType>();
            //来自系统和是否忽略大小写
            //Map<String,Setting.UserSystemType> sysTypeMap=new HashMap<>();
            for(KnUserSetupInfo userSetupInfo : setupList){
                inParm.add(userSetupInfo.getFromSys());
                map.put(userSetupInfo.getFromSys(),userSetupInfo.getThirdSystemType());
                //sysTypeMap.put(userSetupInfo.getFromSys().trim().toLowerCase(),userSetupInfo.getIgnoreCase());
            }
            /*for(KnEmployee userInfo : employeeList){
                Setting.UserSystemType accountType=sysTypeMap.get(userInfo.getUserSystem().trim().toLowerCase());
                //判断账户名是否需要大小写
                if(accountType.equals(Setting.UserSystemType.uaccountIgnore)||accountType.equals(Setting.UserSystemType.ignore)){
                    //不区分大小写
                    employeeMap.put(userInfo.getLoginName().trim().toLowerCase(),userInfo);
                }else if(accountType.equals(Setting.UserSystemType.distinguish)){
                    //账号密码都区分大小写
                    employeeMap.put(userInfo.getLoginName().trim().toLowerCase(),userInfo);
                }
            }*/
            //查询系统中的第三方用户信息
            List<KnUserThirdInfo> userList=knUserThirdInfoDao.findInFromSys(inParm);
            if(userList==null||userList.size()==0){
                rtn.put("stat",false);
                rtn.put("info","该系统下无用户，无法导入。");
                return rtn;
            }
            Map userFilter=new HashMap();
            //过滤相同UserID与相同系统的
            for(int i=userList.size()-1;i>=0;i--){
                KnUserThirdInfo u=userList.get(i);
                String mapContain=u.getUserId()+"_"+u.getFromSys();
                if(!userFilter.containsKey(mapContain)){
                    userFilter.put(mapContain,u);
                }else{
                    userList.remove(i);
                }
            }
            //将系统中的用户导入到xSimple平台中
            List<KnUser> list=new ArrayList<KnUser>();
            List<KnEmployee> list1=new ArrayList<KnEmployee>();
            Random rand=new Random();
            for(KnUserThirdInfo u : userList){
                if(Setting.ThirdSystemType.assistSystem.equals(map.get(u.getFromSys()))){//辅系统,如果有关联的markName,不插入到UserInfo中
                    if(!Strings.isNullOrEmpty(u.getMarkName())){
                        continue;
                    }
                }
                KnEmployee employee;
                //导入第三方用户时，不支持具有相同用户名的导入，整个系统用户名唯一
                KnUser ui=knUserDao.findByLoginName(u.getUaccount());
                if(ui!=null){//原来的UserInfo中存在,只需要更新即可
                    employee=knEmployeeDao.findOne(ui.getId());
                    //来自相同系统的同一用户
                    if(employee.getUserSystem().trim().toUpperCase().equals(u.getFromSys().trim().toUpperCase())){
                        employee.setUserSystem(u.getFromSys());
                        employee.setUserType(u.getUserType());
                        employee.setMarkName(u.getMarkName());
                        employee.setUserId(u.getUserId());
                        employee.setLoginName(u.getUaccount());
                        employee.setUserName(u.getFullName());
                        ui.setName(u.getFullName());
                        ui.setSalt(Encodes.encodeHex(Digests.generateSalt(ResourceService.SALT_SIZE)));
                        ui.setLoginName(u.getUaccount());
                        ui.setPlainPassword("123456");
                        if(u.getUpwd()!=null){
                            ui.setPlainPassword(u.getUpwd());
                        }
                        byte[] hashPassword=Digests.sha1(ui.getPlainPassword().getBytes(),Encodes.decodeHex(ui.getSalt()),ResourceService.HASH_INTERATIONS);
                        ui.setPassword(Encodes.encodeHex(hashPassword));
                        ui.setStatus(KnUser.ActiveType.ENABLE);
                        list.add(ui);
                        list1.add(employee);
                    }else{
                        rtn.put("stat",false);
                        rtn.put("info","导入失败,系统中已经存在相同账户名的用户，请修改账户名");
                        return rtn;
                    }
                }else{
                    ui=new KnUser();
                    employee=new KnEmployee();
                    ui.setName(u.getFullName());
                    ui.setPassword(u.getUpwd()==null?"691b14d79bf0fa2215f155235df5e670b64394cc":u.getUpwd());
                    ui.setSalt(Encodes.encodeHex(Digests.generateSalt(ResourceService.SALT_SIZE)));
                    if(Strings.isNullOrEmpty(u.getUaccount())){
                        ui.setLoginName(rand.nextLong()+"");
                        employee.setLoginName(ui.getLoginName());
                    }else{
                        ui.setLoginName(u.getUaccount());
                        employee.setLoginName(u.getUaccount());
                    }
                    if(!ui.getLoginName().contains("@")){
                        ui.setEmail(ui.getLoginName()+"@kingnode.com");
                    }else{
                        ui.setEmail("default@kingnode.com");
                    }
                    //设置加密串
                    ui.setSalt(Encodes.encodeHex(Digests.generateSalt(ResourceService.SALT_SIZE)));
                    ui.setStatus(KnUser.ActiveType.ENABLE);
                    employee.setUserSystem(u.getFromSys());
                    employee.setUserType(u.getUserType());
                    employee.setMarkName(u.getMarkName());
                    employee.setEmail(ui.getEmail());
                    employee.setUserName(u.getFullName());
                    employee.setUserId(u.getUserId());
                    employee.setUserType("employee");
                    employee.setJob(IdEntity.ActiveType.ENABLE);
                    ui.setPlainPassword("123456");
                    if(u.getUpwd()!=null){
                        ui.setPlainPassword(u.getUpwd());
                    }
                    byte[] hashPassword=Digests.sha1(ui.getPlainPassword().getBytes(),Encodes.decodeHex(ui.getSalt()),ResourceService.HASH_INTERATIONS);
                    ui.setPassword(Encodes.encodeHex(hashPassword));
                    organizationService.SaveKnEmployee(ui,employee,new Long[0],new Long[0],new ArrayList<EmpOrg>(),new ArrayList<EmpPos>());
                }
            }
            //此处接入周进接口OrganizationService
            if(list.size()!=0){
                List arr=(List)knUserDao.save(list);
                List arr1=(List)knEmployeeDao.save(list1);
                if(arr==null){
                    rtn.put("stat",false);
                    rtn.put("info","导入失败");
                }else{
                    rtn.put("stat",true);
                    rtn.put("info","导入成功");
                }
            }
            rtn.put("stat",true);
            rtn.put("info","导入成功");
        }catch(Exception e){
            log.info("AddToUserInfo:"+e);
            rtn.put("stat",false);
            rtn.put("info","导入异常");
        }
        return rtn;
    }
    @Transactional(readOnly=false)
    public Map ContactUser(String fromSys1,String fromSys2,String contact1,String contact2){
        Map map=new HashMap();
        if(Strings.isNullOrEmpty(fromSys1)||Strings.isNullOrEmpty(fromSys2)||Strings.isNullOrEmpty(contact1)||Strings.isNullOrEmpty(contact2)){
            map.put("stat",false);
            map.put("info","参数错误");
            return map;
        }
        fromSys1=fromSys1.toUpperCase();
        fromSys2=fromSys2.toUpperCase();
        contact1=contact1.trim();
        contact2=contact2.trim();
        //查找第三方系统中的用户进行关联
        List<KnUserThirdInfo> userList=knUserThirdInfoDao.findByFromSys(fromSys1);
        List<KnUserThirdInfo> list=new ArrayList<KnUserThirdInfo>();
        //			List<UserThirdInfo> userListTemp = new ArrayList<UserThirdInfo>(userList);//深层复制
        List<KnUserThirdInfo> userListTemp=knUserThirdInfoDao.findByFromSys(fromSys2);
        for(KnUserThirdInfo userThirdInfo : userList){
            String uuid=UuidMaker.getInstance().getUuid(true);
            Map jo=new HashMap();
            jo.put("userId",userThirdInfo.getUserId());
            jo.put("fullName",userThirdInfo.getFullName());
            jo.put("uaccount",userThirdInfo.getUaccount());
            jo.put("userType",userThirdInfo.getUserType());
            for(KnUserThirdInfo ult : userListTemp){
                Map temp=new HashMap();
                temp.put("userId",ult.getUserId());
                temp.put("fullName",ult.getFullName());
                temp.put("uaccount",ult.getUaccount());
                temp.put("userType",ult.getUserType());
                //非主键id,忽略大小写比较信息
                if(jo.get(contact1).toString().trim().equalsIgnoreCase(temp.get(contact2).toString().trim())){
                    ult.setMarkName(uuid);
                    userThirdInfo.setMarkName(uuid);
                    list.add(ult);
                    list.add(userThirdInfo);
                }
            }
        }
        if(list.size()!=0){
            knUserThirdInfoDao.save(list);
            map.put("stat",true);
            map.put("info","关联成功");
        }else{
            map.put("stat",true);
            map.put("info","无关联的用户数据");
        }
        return map;
    }
    @Autowired
    public void setKnUserSetupInfoDao(KnUserSetupInfoDao knUserSetupInfoDao){
        this.knUserSetupInfoDao=knUserSetupInfoDao;
    }
    @Autowired
    public void setKnEmployeeDao(KnEmployeeDao knEmployeeDao){
        this.knEmployeeDao=knEmployeeDao;
    }
    @Autowired
    public void setKnUserDao(KnUserDao knUserInfoDao){
        this.knUserDao=knUserInfoDao;
    }
    @Autowired
    public void setKnUserThirdInfoDao(KnUserThirdInfoDao knUserThirdInfoDao){
        this.knUserThirdInfoDao=knUserThirdInfoDao;
    }
}
