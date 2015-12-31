package com.kingnode.xsimple.service.system;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kingnode.diva.mapper.BeanMapper;
import com.kingnode.diva.persistence.DynamicSpecifications;
import com.kingnode.diva.persistence.SearchFilter;
import com.kingnode.diva.security.utils.Digests;
import com.kingnode.diva.utils.Encodes;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.api.common.JsTree;
import com.kingnode.xsimple.api.common.JsTreeState;
import com.kingnode.xsimple.api.common.Resource;
import com.kingnode.xsimple.dao.system.KnClientDao;
import com.kingnode.xsimple.dao.system.KnResourceDao;
import com.kingnode.xsimple.dao.system.KnRoleDao;
import com.kingnode.xsimple.dao.system.KnSysItemDao;
import com.kingnode.xsimple.dao.system.KnUserDao;
import com.kingnode.xsimple.entity.IdEntity.ActiveType;
import com.kingnode.xsimple.entity.system.KnResource;
import com.kingnode.xsimple.entity.system.KnResource.ResourceType;
import com.kingnode.xsimple.entity.system.KnRole;
import com.kingnode.xsimple.entity.system.KnSysItem;
import com.kingnode.xsimple.entity.system.KnUser;
import com.kingnode.xsimple.util.Users;
import com.kingnode.xsimple.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Component @Transactional(readOnly=true) public class ResourceService{
    public static final String HASH_ALGORITHM="SHA-1";
    public static final int HASH_INTERATIONS=1024;
    public static final int SALT_SIZE=8;
    private static Logger logger=LoggerFactory.getLogger(ResourceService.class);
    private KnResourceDao resDao;
    private KnRoleDao roleDao;
    private KnUserDao userDao;
    private KnClientDao clientDao;
    private KnSysItemDao sysItemDao;
    private CacheManager cacheManager;
    /** 资源管理 begin* */
    public KnResource ReadKnResource(Long id){
        return resDao.findOne(id);
    }
    public List<JsTree> GetResourceTree(){
        List<KnResource> list=resDao.findByTypeNotAndTypeNot(ResourceType.FUNCTION,ResourceType.MODULE,new Sort(Sort.Direction.ASC,"supId","seq"));
        List<JsTree> res=Lists.newArrayList();
        for(KnResource kr : list){
            JsTree jt=new JsTree();
            jt.setId(kr.getId().toString());
            jt.setParent(kr.getSupId()==null?"#":(kr.getSupId().compareTo(0L)>0?kr.getSupId().toString():"#"));
            jt.setText(kr.getName());
            jt.setIcon(kr.getIcon());
            res.add(jt);
        }
        return res;
    }
    /**
     * 保存资源
     *
     * @param res
     *
     * @return
     */
    @Transactional(readOnly=false) public void SaveKnResource(KnResource res){
        if(res.getId()!=null&&res.getId().compareTo(0L)>0){
            if(res.getSupId().compareTo(0L)>0){
                KnResource supRes=resDao.findOne(res.getSupId());
                res.setPath(supRes.getPath()+res.getId()+".");
                res.setDepth(supRes.getDepth()+1);
            }else{
                res.setPath(res.getId()+".");
                res.setDepth(1L);
            }
        }else{
            resDao.save(res);
            if(res.getSupId().compareTo(0L)>0){
                KnResource supZr=resDao.findOne(res.getSupId());
                res.setPath(supZr.getPath()+res.getId()+".");
                res.setDepth(supZr.getDepth()+1);
            }else{
                res.setPath(res.getId()+".");
                res.setDepth(1L);
            }
        }
        resDao.save(res);
    }
    @Transactional(readOnly=false) public void DeleteKnResource(Long id){
        KnResource res=resDao.findOne(id);
        //判断是否有角色，有角色要清理角色与资源关系
        List<KnResource> delList=resDao.findByPathLike(res.getPath()+"%");
        //通过KnResource寻找KnRole
        List<KnRole> roles=(List<KnRole>)roleDao.findAll();
        if(!roles.isEmpty()){
            for(KnRole role : roles){
                if(!role.getRes().isEmpty()){
                    for(KnResource re : delList){
                        if(role.getRes().contains(re)){
                            role.getRes().remove(re);
                        }
                    }
                }
            }
            roleDao.save(roles);
        }
        resDao.delete(delList);
    }
    /** 资源管理 begin* */
    /** 角色管理 begin* */
    /**
     * @param searchParams
     * @param dt
     *
     * @return
     */
    public DataTable<KnRole> PageKnRole(final Map<String,Object> searchParams,DataTable<KnRole> dt){
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        String[] column=new String[]{"name","code","active"};
        Sort sort=new Sort(d,column[Integer.parseInt(dt.getiSortCol_0())]);
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        Map<String,SearchFilter> filters=SearchFilter.parse(searchParams);
        SearchFilter sf=filters.get("EQ_active");
        if(sf!=null){
            if(Strings.isNullOrEmpty(sf.value.toString())){
                filters.remove("EQ_active");
            }else{
                sf.value=ActiveType.valueOf(sf.value.toString());
                filters.put("EQ_active",sf);
            }
        }
        //去除空格处理
        sf=filters.get("LIKE_code");
        if(sf!=null){
            sf.value=sf.value.toString().trim();
            filters.put("LIKE_code",sf);
        }
        sf=filters.get("LIKE_name");
        if(sf!=null){
            sf.value=sf.value.toString().trim();
            filters.put("LIKE_name",sf);
        }
        sf=filters.get("LIKE_description");
        if(sf!=null){
            sf.value=sf.value.toString().trim();
            filters.put("LIKE_description",sf);
        }
        Specification<KnRole> spec=DynamicSpecifications.bySearchFilter(filters.values());
        Page<KnRole> page=roleDao.findAll(spec,pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    /**
     * 保存role到数据库
     *
     * @param role
     *
     * @return
     */
    @Transactional(readOnly=false) public void SaveKnRole(KnRole role,List<Long> list){
        if(role.getRes()!=null){
            role.getRes().clear();
        }else{
            role.setRes(new HashSet<KnResource>());
        }
        for(Long id : list){
            role.getRes().add(new KnResource(id));
        }
        roleDao.save(role);
    }
    /**
     * 删除角色
     *
     * @param id
     *
     * @return
     */
    @Transactional(readOnly=false) public void DeleteKnRole(Long id){
        KnRole role=roleDao.findOne(id);
        if(role.getRes()!=null){
            role.getRes().clear();
        }
        this.roleDao.delete(role);
    }
    /**
     * 通过角色id获取角色
     *
     * @param roleId
     *
     * @return
     */
    public KnRole ReadKnRole(Long roleId){
        return this.roleDao.findOne(roleId);
    }
    public List<JsTree> GetResourceTreeHasSelect(KnRole role){
        Map<Long,Long> map=Maps.newHashMap();
        for(KnResource kr : role.getRes()){
            map.put(kr.getId(),kr.getId());
        }
        List<KnResource> list=resDao.findByTypeNotAndTypeNot(ResourceType.FUNCTION,ResourceType.MODULE,new Sort(Sort.Direction.ASC,"supId","seq"));
        List<JsTree> jts=Lists.newArrayList();
        for(KnResource kr : list){
            JsTree jt=new JsTree();
            jt.setId(kr.getId().toString());
            jt.setParent(kr.getSupId()==null?"#":(kr.getSupId().compareTo(0L)>0?kr.getSupId().toString():"#"));
            jt.setText(kr.getName());
            JsTreeState jtState=new JsTreeState();
            //一级节点除了主页,都是未选中状态
            if((kr.getSupId()==null||0L==kr.getSupId())&&!"main".equalsIgnoreCase(kr.getCode())){
                jtState.setSelected(false);
            }else{
                jtState.setSelected(map.get(kr.getId())!=null);
            }
            jt.setState(jtState);
            jts.add(jt);
        }
        return jts;
    }
    public List<KnRole> ListKnRoleExitsActive(){
        return roleDao.findByActive(ActiveType.ENABLE);
    }
    /** 角色管理 begin* */
    /** 用户管理 begin* */
    /**
     * 根据loginName字段查找用户
     *
     * @param loginName 登陆名
     *
     * @return
     */
    public KnUser FindUserByLoginName(String loginName){
        return userDao.findByLoginName(loginName);
    }
    public DataTable<KnUser> PageKnUser(Map<String,Object> searchParams,DataTable<KnUser> dt){
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        String[] column=new String[]{"loginName","name","email","status","loginTime","loginIp"};
        Sort sort=new Sort(d,column[Integer.parseInt(dt.getiSortCol_0())]);
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        Map<String,SearchFilter> filters=SearchFilter.parse(searchParams);
        SearchFilter sf=filters.get("EQ_status");
        if(sf!=null){
            sf.value=ActiveType.ENABLE.toString().equals(sf.value.toString())?ActiveType.ENABLE:ActiveType.DISABLE;
            filters.put("EQ_status",sf);
        }
        Specification<KnUser> spec=DynamicSpecifications.bySearchFilter(filters.values());
        Page<KnUser> page=userDao.findAll(spec,pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setiTotalRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    public Boolean checkLoginName(String loginName,Long id){
        KnUser u=userDao.findByLoginName(loginName);
        return u==null||!id.equals(0L)&&u.getId().equals(id);
    }
    @Transactional(readOnly=false) public void SaveKnUser(KnUser ku,Long[] ids){
        if(ku.getRole()!=null){
            ku.getRole().clear();
        }else{
            ku.setRole(new ArrayList<KnRole>());
        }
        if(!Strings.isNullOrEmpty(ku.getPlainPassword())){
            entryptPassword(ku);
        }
        for(Long id : ids){
            ku.getRole().add(new KnRole(id));
        }
        userDao.save(ku);
    }
    private void entryptPassword(KnUser user){
        byte[] hashPassword=Digests.sha1(user.getPlainPassword().getBytes(),Encodes.decodeHex(user.getSalt()),HASH_INTERATIONS);
        user.setPassword(Encodes.encodeHex(hashPassword));
    }
    /**
     * 通过用户id查找用户
     *
     * @param id
     *
     * @return
     */
    public KnUser ReadUser(Long id){
        return userDao.findOne(id);
    }
    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     *
     * @return
     *
     * @throws Exception
     */
    @Transactional(readOnly=false) public Map<String,String> ChangePassword(String oldPassword,String newPassword) throws Exception{
        Map map=new HashMap();
        KnUser ku=userDao.findOne(Users.id());
        KnUser knUser=new KnUser();
        knUser.setLoginName(ku.getLoginName());
        knUser.setPlainPassword(oldPassword);
        knUser.setSalt(ku.getSalt());
        //外部提供明文密码,就会生成新的加密码,否则不生成新的加密密码
        entryptPassword(knUser);
        //获取登录密码
        if(knUser.getPassword().equals(ku.getPassword())){
            ku.setPlainPassword(newPassword);
            entryptPassword(ku);
            userDao.save(ku);
            map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
            map.put(Setting.MESSAGE,"密码修改成功");
        }else{
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"原始密码错误");
        }
        return map;
    }
    @Transactional(readOnly=false) public String DeleteKnUser(Long id){
        KnUser ku=ReadUser(id);
        ku.getRole().clear();
        userDao.delete(ku);
        return ku.getLoginName();
    }
    /** 用户管理 end* */
    /** 数据字典 begin* */
    /**
     * @param searchParams
     * @param dt
     *
     * @return
     */
    public DataTable<KnSysItem> PageKnSysItem(final Map<String,Object> searchParams,DataTable<KnSysItem> dt){
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        String[] column=new String[]{"itemId","objId","annexa","annexb","annexc","annexd","annexe","active"};
        Sort sort=new Sort(d,column[Integer.parseInt(dt.getiSortCol_0())]);
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        Map<String,SearchFilter> filters=SearchFilter.parse(searchParams);
        SearchFilter sf=filters.get("EQ_active");
        if(sf!=null){
            if(Strings.isNullOrEmpty(sf.value.toString())){
                filters.remove("EQ_active");
            }else{
                sf.value=ActiveType.valueOf(sf.value.toString());
                filters.put("EQ_active",sf);
            }
        }
        Specification<KnSysItem> spec=DynamicSpecifications.bySearchFilter(filters.values());
        Page<KnSysItem> page=sysItemDao.findAll(spec,pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    /**
     * 通过id获取字典
     *
     * @param id
     *
     * @return
     */
    public KnSysItem ReadKnSysItem(Long id){
        return sysItemDao.findOne(id);
    }
    public Boolean checkKnSysItemObjId(String objId,Long id){
        KnSysItem u=sysItemDao.findByObjId(objId);
        return u==null||!id.equals(0L)&&u.getId().equals(id);
    }
    /**
     * 保存role到数据库
     *
     * @param obj
     *
     * @return
     */
    @Transactional(readOnly=false) public void SaveKnSysItem(KnSysItem obj){
        sysItemDao.save(obj);
    }
    /** 数据字典 end* */
    public void addAuthCode(String authCode,String username){
        cacheManager.getCache("oauth-code-cache").put(authCode,username);
    }
    public void addAccessToken(String accessToken,String username){
        cacheManager.getCache("oauth-code-cache").put(accessToken,username);
    }
    public boolean checkAuthCode(String authCode){
        return cacheManager.getCache("oauth-code-cache").get(authCode)!=null;
    }
    public String getUsernameByAuthCode(String authCode){
        return (String)cacheManager.getCache("oauth-code-cache").get(authCode).get();
    }
    public long getExpireIn(){
        return 3600L;
    }
    public Object findByClientId(String clientId){
        return clientDao.findByClientId(clientId);
    }
    public boolean checkClientId(String clientId){
        return findByClientId(clientId)!=null;
    }
    public boolean checkClientSecret(String clientSecret){
        return clientDao.findByClientSecret(clientSecret)!=null;
    }
    public List<KnRole> CacheRoles(Long id){
        KnUser ku=userDao.findOne(id);
        List<KnRole> list=Lists.newArrayList();
        if(ku!=null){
            list=ku.getRole();
            for(KnRole role : list){
                if(role.getActive().equals(ActiveType.DISABLE)){
                    list.remove(role);
                }
            }
        }
        return list;
    }
    public List<Resource> CaseResource(Long id){
        Map<Long,List<Resource>> map=Maps.newHashMap();
        List<KnResource> res=resDao.findResByIdIn(id,KnResource.ResourceType.MENU,KnResource.ActiveType.ENABLE);
        for(KnResource kr : res){
            List<Resource> re=map.get(kr.getSupId());
            if(re==null){
                re=Lists.newArrayList();
            }
            re.add(BeanMapper.map(kr,Resource.class));
            map.put(kr.getSupId(),re);
        }
        return MakeResource(map,0L);
    }
    private List<Resource> MakeResource(Map<Long,List<Resource>> map,Long supId){
        List<Resource> res=Lists.newArrayList();
        List<Resource> reList=map.get(supId);
        if(reList!=null){
            for(Resource re : reList){
                re.setChildren(MakeResource(map,re.getId()));
                res.add(re);
            }
        }
        return res;
    }
    @Autowired public void setRoleDao(KnRoleDao roleDao){
        this.roleDao=roleDao;
    }
    @Autowired public void setResDao(KnResourceDao resDao){
        this.resDao=resDao;
    }
    @Autowired public void setUserDao(KnUserDao userDao){
        this.userDao=userDao;
    }
    @Autowired public void setClientDao(KnClientDao clientDao){
        this.clientDao=clientDao;
    }
    @Autowired public void setCacheManager(CacheManager cacheManager){
        this.cacheManager=cacheManager;
    }
    @Autowired public void setSysItemDao(KnSysItemDao sysItemDao){
        this.sysItemDao=sysItemDao;
    }
    /**
     * 用户分配角色
     * @param roleList 角色id集合
     * @param userList 用户id集合
     * @param tip      标示:0为全部分配和1选中分配
     * @return 返回成功失败信息
     */
    @Transactional(readOnly=false) public Map<String,Object> SaveRoleOfUserList(List<Long> userList,List<Long> roleList,String tip){
        Map<String,Object> map=new HashMap();
        roleList.add(0L);
        List<KnRole> knRoleList=findRoleListByIdList(roleList);
        if(!Utils.isEmpityCollection(knRoleList)){
        List<KnUser> knUserList=null;
        if("1".equals(tip)){
                if(!Utils.isEmpityCollection(userList)){
                    knUserList=findUserListByIdList(userList);
                }
        }else{
            knUserList=(List<KnUser>)userDao.findAll();
        }
            List<KnUser> userRoleList = Lists.newArrayList();
        for(KnUser knUser : knUserList){
                List<KnRole> userDataRoleList = Lists.newArrayList();
                userDataRoleList.addAll(knRoleList);
                if(!Utils.isEmpityCollection(userDataRoleList)){
                    if(knUser.getRole()==null){
                        knUser.setRole(new ArrayList<KnRole>());
                    }
                    userDataRoleList.addAll(knUser.getRole());
                    knUser.setRole(Utils.removeDuplicate(userDataRoleList));
                    userRoleList.add(knUser);
                }
            }
            if(!Utils.isEmpityCollection(userRoleList)){
                userDao.save(userRoleList);
            }
        }
        map.put("stat",true);
        map.put("info","保存成功");
        return map;
    }

    /**
     * 根据用户id集合获取用户的集合
     *
     * @param userIdList 用户id集合
     *
     * @return 返回用户的集合
     */
    private List<KnUser> findUserListByIdList(final List<Long> userIdList){
        List<KnUser> list=userDao.findAll(new Specification<KnUser>(){
            @Override public Predicate toPredicate(Root<KnUser> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(!Utils.isEmpityCollection(userIdList)){
                    Expression<Long> ep=root.<Long>get("id");
                    expressions.add(ep.in(userIdList));
                }
                return predicate;
            }
        });
        return list;
    }

    /**
     * 根据角色id集合获取角色的集合
     *
     * @param roleIdList 角色id集合
     *
     * @return 返回角色的集合
     */
    private List<KnRole> findRoleListByIdList(final List<Long> roleIdList){
        List<KnRole> list=roleDao.findAll(new Specification<KnRole>(){
            @Override public Predicate toPredicate(Root<KnRole> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(!Utils.isEmpityCollection(roleIdList)){
                    Expression<Long> ep=root.<Long>get("id");
                    expressions.add(ep.in(roleIdList));
                }
                return predicate;
            }
        });
        return list;
    }
}