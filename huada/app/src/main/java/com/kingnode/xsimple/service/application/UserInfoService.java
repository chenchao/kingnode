package com.kingnode.xsimple.service.application;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.dao.system.KnEmployeeDao;
import com.kingnode.xsimple.dao.system.KnUserDao;
import com.kingnode.xsimple.dao.application.KnApplicationInfoDao;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnUser;
import com.kingnode.xsimple.entity.application.KnApplicationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * wangyifan
 */
@Component @Transactional(readOnly=true)
public class UserInfoService{
    private KnUserDao kud;
    private KnEmployeeDao ked;
    private KnApplicationInfoDao kaid;
    private static Logger logger=LoggerFactory.getLogger(UserInfoService.class);
    @Autowired
    public void setKud(KnUserDao kud){
        this.kud=kud;
    }
    @Autowired
    public void setKed(KnEmployeeDao ked){
        this.ked=ked;
    }
    @Autowired
    public void setKaid(KnApplicationInfoDao kaid){
        this.kaid=kaid;
    }
    /**
     * 根据登录用户名 查询用户信息列表
     *
     * @param name 登录用户名
     *
     * @return 符合条件的用户信息
     */
    public List<KnUser> FindByNameKnUser(String name){
        return kud.findByNameKnUser(name);
    }
    /**
     * 查询用户列表信息
     *
     * @param searchParams
     * @param dt
     * @param appId
     *
     * @return
     */
    public DataTable<KnUser> ListOfKnUser(Map<String,Object> searchParams,DataTable<KnUser> dt,String appId){
        Page<KnUser> list=null;
        Sort sort=getSort(dt);
        Pageable pager=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        list=kud.findAll(pager);
        dt.setiTotalDisplayRecords(list.getTotalElements());
        dt.setAaData(list.getContent());
        return dt;
    }
    /**
     * 查询信息排序
     *
     * @param dt
     *
     * @return
     */
    private Sort getSort(DataTable dt){
        Sort.Direction d;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }else{
            d=Sort.Direction.DESC;
        }
        Sort sort=new Sort(d,"id");
        switch(dt.getiSortCol_0()){
        case "1":
            sort=new Sort(d,"name");
            break;
        case "2":
            sort=new Sort(d,"code");
            break;
        case "3":
            sort=new Sort(d,"description");
            break;
        case "4":
            sort=new Sort(d,"active");
            break;
        }
        return sort;
    }
    /**
     * 保存或更新 用户信息  以及 员工信息
     *
     * @param knUser     用户信息
     * @param knEmployee 员工信息
     *
     * @return 返回成功与否的状态
     */
    @Transactional(readOnly=false)
    public Map<String,Object> SaveKnUserAndEmploy(KnUser knUser,KnEmployee knEmployee){
        boolean bool=false, empBool=false, titBool=false;
        String mseeage="登录名已经存在,请修改后,在提交！";
        Map<String,Object> map=new HashMap<String,Object>();
        try{
            if(null==knUser.getId()){
                empBool=true;
                knUser.setCreateTime(System.currentTimeMillis());
            }else{
                knUser.setUpdateTime(System.currentTimeMillis());
            }
            List<KnUser> list=kud.findByNameKnUser(knUser.getLoginName());
            if(null!=list&&list.size()>0){
                if(empBool){
                    titBool=true;
                }else{
                    if(!knUser.getId().equals(list.get(0).getId())){
                        titBool=true;
                    }
                }
            }
            if(!titBool){
                kud.save(knUser);
                if(empBool){
                    knEmployee.setId(knUser.getId());
                    ked.save(knEmployee);
                }
                bool=true;
            }else{
                map.put("msg",mseeage);
            }
            map.put("stat",bool);
        }catch(Exception e){
            logger.info("新增或是更新用户错误信息 ："+e);
            map.put("stat",false);
            map.put("msg","登录名已经存在,请修改后,在提交！");
        }
        return map;
    }
    /**
     * 根据ID查询用户信息
     *
     * @param id 用户id
     *
     * @return 返回对应的用户信息
     */
    public KnUser FindUserById(long id){
        return kud.findOne(id);
    }
    /**
     * 根据id 删除单个用户信息 同时也删除与之关联的员工信息
     *
     * @param id 12313
     */
    @Transactional(readOnly=false)  //  关闭只读 ,对数据库有操作
    public void DeleteKnUserAndEmpById(Long id){
        ked.delete(id);
        kud.delete(id);
    }
    /**
     * 根据ids 删除多个用户信息 同时也删除与之关联的员工信息
     *
     * @param ids 如 123,456
     */
    @Transactional(readOnly=false)  //  关闭只读 ,对数据库有操作
    public void DeleteAllKnUserAndEmpByIds(List<Long> ids){
        List<KnEmployee> empList=(List<KnEmployee>)ked.findAll(ids);
        ked.delete(empList);
        List<KnUser> list=(List<KnUser>)kud.findAll(ids);
        kud.delete(list);
    }
    /**
     * 获取 应用列表信息
     *
     * @return 返回应用列表
     */
    public List<KnApplicationInfo> GetAllApplicationList(){
        return (List<KnApplicationInfo>)kaid.findAll();
    }

}