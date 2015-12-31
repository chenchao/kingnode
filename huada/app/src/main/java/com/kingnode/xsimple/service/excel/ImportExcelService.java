package com.kingnode.xsimple.service.excel;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Strings;
import com.kingnode.diva.mapper.BeanMapper;
import com.kingnode.diva.security.utils.Digests;
import com.kingnode.diva.utils.Encodes;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.system.EmpOrg;
import com.kingnode.xsimple.api.system.EmpPos;
import com.kingnode.xsimple.dao.system.KnEmployeeDao;
import com.kingnode.xsimple.dao.system.KnRoleDao;
import com.kingnode.xsimple.dao.system.KnUserDao;
import com.kingnode.xsimple.dto.ExcelDTO;
import com.kingnode.xsimple.dto.ImportEmpDTO;
import com.kingnode.xsimple.entity.IdEntity;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnEmployeeOrganization;
import com.kingnode.xsimple.entity.system.KnEmployeeOrganizationId;
import com.kingnode.xsimple.entity.system.KnOrganization;
import com.kingnode.xsimple.entity.system.KnRole;
import com.kingnode.xsimple.entity.system.KnUser;
import com.kingnode.xsimple.service.system.OrganizationService;
import com.kingnode.xsimple.util.Utils;
import com.kingnode.xsimple.util.excel.DownExcel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * 导入设置管理
 * wangyifan
 */
@Component @Transactional(readOnly=true)
public class ImportExcelService{
    private KnRoleDao knRoleDao;
    private KnUserDao knUserDao;
    private KnEmployeeDao knEmployeeDao;
    private OrganizationService organizationService;
    private static Logger logger=LoggerFactory.getLogger(ImportExcelService.class);
    public static final int HASH_INTERATIONS=1024;
    private static final int SALT_SIZE=8;
    @Autowired
    public void setKnRoleDao(KnRoleDao knRoleDao){
        this.knRoleDao=knRoleDao;
    }
    @Autowired
    public void setKnEmployeeDao(KnEmployeeDao knEmployeeDao){
        this.knEmployeeDao=knEmployeeDao;
    }
    @Autowired
    public void setKnUserDao(KnUserDao knUserDao){
        this.knUserDao=knUserDao;
    }
    @Autowired
    public void setOrganizationService(OrganizationService organizationService){
        this.organizationService=organizationService;
    }
    /**
     * 动态生成excel 文件
     *
     * @param tip      生成用户,职责,OA用户信息
     * @param response
     */
    public void DownLoadExcel(String tip,HttpServletResponse response){
        try{
            LinkedList<String> titleList=new LinkedList<>();
            if("1".equals(tip)){//生成下载用户的模块信息 带有角色下拉框信息
                List<KnRole> roleList=(List<KnRole>)knRoleDao.findAll();
                if(null!=roleList&&roleList.size()>0){
                    for(KnRole knObj : roleList){
                        if(null!=knObj.getName()){
                            titleList.add(knObj.getName());
                        }
                    }
                }
            }
            DownExcel.getInstall().downloadExcel(tip,response,titleList); //动态生成excel 下载模板文件
        }catch(Exception e){
            logger.error("动态生成excel文件,出错{}",e);
        }
    }
    /**
     * <分析Excel，组装显示在页面中>
     *
     * @param file               文件信息
     * @param uploadFileFileName 文件后缀名
     * @param tip                生成用户,职责,OA用户信息
     */
    public Map<String,Object> GetObjInfOfExcel(File file,String uploadFileFileName,String tip){
        return DownExcel.getInstall().getObjInfOfExcel(file,uploadFileFileName,tip);
    }
    /**
     * 批量保存用户,员工信息
     *
     * @param name       用户名 数组
     * @param loginName  登录名 数组
     * @param userIds    用户ID 数组
     * @param userType   用户类型 数组
     * @param fromSystem 用户来自系统  数组
     * @param roleNames  用户角色 数组
     * @param email      邮件 数组
     * @param password   密码数组
     *
     * @return 返回导入是否成功 map集合
     */
    @Transactional(readOnly=false)
    public Map<String,Boolean> SaveUserList(String[] name,String[] loginName,String[] userIds,String[] userType,String[] fromSystem,String[] roleNames,String[] email,String[] password){
        Map<String,Boolean> backMap=new HashMap<>();
        try{
            List<KnUser> knUserList=(List<KnUser>)knUserDao.findAll();
            Map<String,KnUser> knUserHashMap=new HashMap<String,KnUser>();
            List<KnEmployee> knEmployeeList=(List<KnEmployee>)knEmployeeDao.findAll();
            Map<String,KnEmployee> knEmpMap=new HashMap<String,KnEmployee>();
            List<KnRole> knRoleList=(List<KnRole>)knRoleDao.findAll();
            Map<String,KnRole> knRoleMap=new HashMap<String,KnRole>();
            if(null!=knUserList&&knUserList.size()>0){
                for(KnUser knUser : knUserList){
                    knUserHashMap.put(knUser.getLoginName(),knUser);
                }
            }
            if(null!=knEmployeeList&&knEmployeeList.size()>0){
                for(KnEmployee knEmployee : knEmployeeList){
                    String userId=knEmployee.getUserId(), fromSys=knEmployee.getUserSystem();
                    if(null!=userId&&null!=fromSys){
                        knEmpMap.put(userId+"_"+fromSys,knEmployee);
                    }
                }
            }
            if(null!=knRoleList&&knRoleList.size()>0){
                for(KnRole knRole : knRoleList){
                    String roleName=knRole.getName();
                    if(null!=roleName){
                        knRoleMap.put(roleName,knRole);
                    }
                }
            }
            for(int i=0;i<loginName.length;i++){
                //保存用户信息 begin
                KnUser knUser;
                String keyTip=loginName[i];
                if(Utils.isEmptyString(keyTip)){
                    continue;
                }
                if(knUserHashMap.containsKey(keyTip)){
                    knUser=knUserHashMap.get(keyTip);
                }else{
                    knUser=new KnUser();
                }
                knUser.setName(name.length==0?null:name[i]);
                knUser.setLoginName(loginName.length==0?null:loginName[i]);
                String tempRole=roleNames.length==0?null:roleNames[i];
                String tempEm = knUser.getLoginName()+"@qq.com";
                if(null != email){
                    tempEm=email.length==0?tempEm:email[i];
                }
                knUser.setEmail(tempEm);
                Long[] roles=new Long[1];
                if(null!=tempRole){
                    List<KnRole> tempRoleList=new ArrayList<>();
                    KnRole knRole=knRoleMap.containsKey(tempRole)?knRoleMap.get(tempRole):null;
                    if(null!=knRole){
                        tempRoleList.add(knRole);
                        roles[0]=knRole.getId();
                    }
                    List<KnRole> oldList = knUser.getRole();
                    tempRoleList.addAll(oldList);
                    knUser.setRole(tempRoleList);
                }
                String tempPassword = password.length==0?Setting.PASSWORD:password[i];
                Map<String,String> userMap = Utils.entryptPassword(tempPassword);
                knUser.setSalt(userMap.get("enCodeSalt"));
                knUser.setPassword(userMap.get("password"));
                knUser.setStatus(IdEntity.ActiveType.ENABLE);
                knUserDao.save(knUser);
                knUserHashMap.put(keyTip,knUser);
                //保存用户信息 end
                //保存员工信息 begin
                KnEmployee knEmp;
                String kmpTip=userIds[i]+"_"+fromSystem[i];
                if(knEmpMap.containsKey(kmpTip)){
                    knEmp=knEmpMap.get(kmpTip);
                }else{
                    knEmp=new KnEmployee();
                }
                knEmp.setUserId(userIds[i]);
                knEmp.setUserSystem(fromSystem.length==0?null:fromSystem[i]);
                knEmp.setEmail(knUser.getEmail());
                knEmp.setUserType(userType.length==0?null:userType[i]);
                knEmp.setId(knUser.getId());
                knEmp.setLoginName(knUser.getLoginName());
                knEmp.setUserName(knUser.getName());
                knEmp.setJob(IdEntity.ActiveType.ENABLE);
                knEmployeeDao.save(knEmp);
                //organizationService.SaveKnEmployee(knUser,knEmp,roles,null,null,null);
                knEmpMap.put(kmpTip,knEmp);
                //保存员工信息 end
            }
            backMap.put("stat",true);
        }catch(Exception e){
            backMap.put("stat",false);
            logger.error("批量导入用户，错误信息{}",e);
        }
        return backMap;
    }

    @Transactional(readOnly=false)
    public Map<String,Boolean> SaveOrgList( String[] ids,String[] names,String[] types,String[] parentIds){
        Map<String,Boolean> map=new HashMap<>();
        Map<String,KnOrganization> organizationMap=new HashMap<>();
        List<KnOrganization> organizations=new ArrayList<>();
        for(int i=0;i<ids.length;i++){
            KnOrganization org=organizationService.ReadOrgByCode(ids[i]);
            if(org==null){
                org=new KnOrganization();
            }
            org.setCode(ids[i]);
            org.setName(names[i]);
            if(types[i].equals("集团")){
                org.setOrgType(KnOrganization.OrgType.GROUP);
            }else if(types[i].equals("公司")){
                org.setOrgType(KnOrganization.OrgType.COMPANY);
            }else if(types[i].equals("组织")){
                org.setOrgType(KnOrganization.OrgType.ORGANIZATION);
            }else if(types[i].equals("部门")){
                org.setOrgType(KnOrganization.OrgType.DEPARTMENT);
            }
            if(organizationMap.containsKey(parentIds[i])){
                org.setPath(organizationMap.get(parentIds[i]).getPath()+ids[i]+".");
            }else{
                org.setPath(ids[i]+".");
            }
            org.setDescription(parentIds[i]);
            org.setActive(IdEntity.ActiveType.ENABLE);
            organizationMap.put(ids[i],org);
            organizations.add(org);
        }
        organizationService.SaveOrg(organizations);
        //构建路径
        organizationMap.clear();
        for(KnOrganization organization:organizations){
            organizationMap.put(organization.getCode(),organization);
        }
        //设置上下级别关系
        for(KnOrganization organization:organizations){
            KnOrganization parent=organizationMap.get(organization.getDescription());
            if(parent!=null){
                organization.setSupId(parent.getId());
            }else{
                organization.setSupId(0L);
            }
            //设置path
            String[] paths=organization.getPath().split("\\.");
            StringBuilder sbPath=new StringBuilder();
            for(int i=0;i<paths.length;i++){
                sbPath.append(organizationMap.get(paths[i]).getId()).append(".");
            }
            organization.setPath(sbPath.toString());
            organization.setDepth((long)paths.length);
        }
        organizationService.SaveOrg(organizations);
        map.put("stat",true);
        return map;
    }
    /***********
     * 导入员工信息
     * @param emps
     * @param orgIdMap key为组织在系统中生成的ID，value为组织ID
     * @return
     * @throws Exception
     */
    @Transactional(readOnly=false)
    public Map<String,Boolean> SaveEmpList(List<ImportEmpDTO> emps,Map<Long,String> orgIdMap) throws Exception{
        Map<String,KnOrganization> organizationMap=new HashMap<>();
        //获取部门信息
        List<KnOrganization> organizations=organizationService.findAll();
        for(KnOrganization organization:organizations){
            String key=orgIdMap.get(organization.getId());
            organizationMap.put(key,organization);
        }
        Map<String,KnEmployee> employeeMap=new HashMap<>();
        //获取人员信息
        List<KnEmployee> employeeList=organizationService.ListEmployee();
        for(KnEmployee employee:employeeList){
            employeeMap.put(employee.getLoginName(),employee);
        }
        List<KnUser> userList=organizationService.ListUser();
        Map<Long,KnUser> userMap=new HashMap<>();
        for(KnUser user:userList){
            userMap.put(user.getId(),user);
        }
        List<KnEmployee> employees=new ArrayList<>();
        List<KnUser> users=new ArrayList<>();
        for(ImportEmpDTO employee : emps){
            KnEmployee newEmp=employeeMap.get(employee.getLoginName());
            KnUser user=null;
            if(newEmp==null){
                String sex=employee.getSex();
                if(Strings.isNullOrEmpty(sex)){
                    employee.setSex(KnEmployee.Gender.NONE.name());
                }else{
                    if(sex.equals(KnEmployee.Gender.NONE.getTypeName())){
                        employee.setSex(KnEmployee.Gender.NONE.name());
                    }else if(sex.equals(KnEmployee.Gender.MAN.getTypeName())){
                        employee.setSex(KnEmployee.Gender.MAN.name());
                    }else{
                        employee.setSex(KnEmployee.Gender.WOMEN.name());
                    }
                }
                newEmp=BeanMapper.map(employee,KnEmployee.class);
                user=new KnUser();
                user.setPlainPassword(newEmp.getSignature());
                user.setName(employee.getUserName());
                user.setLoginName(employee.getLoginName());
                user.setUserOnline(0);
                user.setStatus(IdEntity.ActiveType.ENABLE);
                if(!Strings.isNullOrEmpty(employee.getEmail())){
                    user.setEmail(employee.getEmail());
                }else{
                    user.setEmail(employee.getLoginName()+"@kingnode.com");
                }
                entryptPassword(user);
            }else{
                user=userMap.get(newEmp.getId());
                if(Strings.isNullOrEmpty(user.getPassword())){
                    user.setPlainPassword(newEmp.getSignature());
                    entryptPassword(user);
                }
                if(Strings.isNullOrEmpty(user.getEmail())){
                    user.setEmail(user.getEmail());
                }
                if(Strings.isNullOrEmpty(newEmp.getEmail())){
                    newEmp.setEmail(employee.getEmail());
                }
                if(Strings.isNullOrEmpty(newEmp.getEmail())){
                    newEmp.setEmail(employee.getEmail());
                }
                if(Strings.isNullOrEmpty(newEmp.getPhone())){
                    newEmp.setPhone(employee.getPhone());
                }
                if(Strings.isNullOrEmpty(newEmp.getUserSystem())){
                    newEmp.setUserSystem(employee.getUserSystem());
                }
            }
            newEmp.setSignature("");
            //获取部门信息
            KnOrganization org=organizationMap.get(employee.getParentId());
            if(user.getId()==null){
                List<EmpOrg> empOrgList=new ArrayList<>();
                if(org!=null){
                    EmpOrg empOrg=new EmpOrg();
                    empOrg.setId(org.getId());
                    empOrg.setMajor(true);
                    empOrg.setCharge(false);
                    empOrgList.add(empOrg);
                }
                organizationService.SaveKnEmployee(user,newEmp,new Long[]{},new Long[]{},empOrgList,new ArrayList<EmpPos>());
            }else{
                if(org!=null){
                    //设置员工部门之间的关系
                    if(newEmp.getOrg()!=null){
                        newEmp.getOrg().clear();
                        newEmp.getOrg().add(new KnEmployeeOrganization(new KnEmployeeOrganizationId(org,newEmp),0,1));
                    }else{
                        newEmp.setOrg(new HashSet<KnEmployeeOrganization>());
                        newEmp.getOrg().add(new KnEmployeeOrganization(new KnEmployeeOrganizationId(org,newEmp),0,1));
                    }
                }
                users.add(user);
                employees.add(newEmp);
            }
        }
        if(!users.isEmpty()){
            organizationService.SaveUsers(users);
            organizationService.SaveEmps(employees);
        }
        Map<String,Boolean> map=new HashMap<>();
        map.put("stat",true);
        return map;
    }
    @Transactional(readOnly=false)
    public Map<Long,String> SaveOrgList(List<ExcelDTO> orgList) throws Exception{
        Map<Long,String> map=new HashMap<>();
        Map<String,KnOrganization> organizationMap=new HashMap<>();
        Map<String,String> codeIdMap=new HashMap<>();
        List<KnOrganization> organizations=new ArrayList<>();
        for(ExcelDTO dto : orgList){
            KnOrganization org=organizationService.ReadOrgByCode(dto.getCol3());
            if(org==null){
                org=new KnOrganization();
            }
            //org.setCode(dto.getCol1());
            org.setName(dto.getCol2());
            org.setCode(dto.getCol3());
            if(dto.getCol4().equals("集团")){
                org.setOrgType(KnOrganization.OrgType.GROUP);
            }else if(dto.getCol4().equals("公司")){
                org.setOrgType(KnOrganization.OrgType.COMPANY);
            }else if(dto.getCol4().equals("组织")){
                org.setOrgType(KnOrganization.OrgType.ORGANIZATION);
            }else if(dto.getCol4().equals("部门")){
                org.setOrgType(KnOrganization.OrgType.DEPARTMENT);
            }
            if(organizationMap.containsKey(dto.getCol5())){
                org.setPath(organizationMap.get(dto.getCol5()).getPath()+dto.getCol1()+".");
            }else{
                org.setPath(dto.getCol1()+".");
            }
            org.setDescription(dto.getCol5());
            org.setActive(IdEntity.ActiveType.ENABLE);
            organizationMap.put(dto.getCol1(),org);
            codeIdMap.put(dto.getCol3(),dto.getCol1());
            organizations.add(org);
        }
        organizationService.SaveOrg(organizations);
        //构建路径
        organizationMap.clear();
        for(KnOrganization organization : organizations){
            String key=codeIdMap.get(organization.getCode());
            organizationMap.put(key,organization);
            map.put(organization.getId(),key);
        }
        //设置上下级别关系
        for(KnOrganization organization : organizations){
            KnOrganization parent=organizationMap.get(organization.getDescription());
            if(parent!=null){
                organization.setSupId(parent.getId());
            }else{
                organization.setSupId(0L);
            }
            //设置path
            String[] paths=organization.getPath().split("\\.");
            StringBuilder sbPath=new StringBuilder();
            for(int i=0;i<paths.length;i++){
                sbPath.append(organizationMap.get(paths[i]).getId()).append(".");
            }
            organization.setPath(sbPath.toString());
            organization.setDepth((long)paths.length);
        }
        organizationService.SaveOrg(organizations);
        return map;
    }
    /**
     * 设定安全的密码，生成随机的salt并经过1024次 sha-1 hash
     * 当用户提供明文密码是
     *
     * @param user
     */
    public static void entryptPassword(KnUser user){
        String plainPassword=user.getPlainPassword();
        if(plainPassword!=null&&!plainPassword.equals("")){
            byte[] salt=Digests.generateSalt(SALT_SIZE);
            user.setSalt(Encodes.encodeHex(salt));
            byte[] hashPassword=Digests.sha1(user.getPlainPassword().getBytes(),salt,HASH_INTERATIONS);
            user.setPassword(Encodes.encodeHex(hashPassword));
        }
    }
}