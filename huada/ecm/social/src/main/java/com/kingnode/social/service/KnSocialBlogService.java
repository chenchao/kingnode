package com.kingnode.social.service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kingnode.diva.mapper.BeanMapper;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.social.dao.KnSocialBlogAtDao;
import com.kingnode.social.dao.KnSocialBlogAttentionDao;
import com.kingnode.social.dao.KnSocialBlogCollectDao;
import com.kingnode.social.dao.KnSocialBlogCommentDao;
import com.kingnode.social.dao.KnSocialBlogDao;
import com.kingnode.social.dao.KnSocialBlogFileDao;
import com.kingnode.social.dao.KnSocialBlogSettingsDao;
import com.kingnode.social.dto.KnSocialBlogDTO;
import com.kingnode.social.entity.KnSocialBlog;
import com.kingnode.social.entity.KnSocialBlogAt;
import com.kingnode.social.entity.KnSocialBlogAttention;
import com.kingnode.social.entity.KnSocialBlogCollect;
import com.kingnode.social.entity.KnSocialBlogComment;
import com.kingnode.social.entity.KnSocialBlogFile;
import com.kingnode.social.entity.KnSocialBlogSettings;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.dao.system.KnEmployeeDao;
import com.kingnode.xsimple.entity.IdEntity;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnEmployeeOrganization;
import com.kingnode.xsimple.service.system.OrganizationService;
import com.kingnode.xsimple.util.Users;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Component @Transactional(readOnly=true)
public class KnSocialBlogService{
    private KnSocialBlogDao blogDao;
    private KnSocialBlogFileDao fileDao;
    private KnSocialBlogAttentionDao attentionDao;
    private KnSocialBlogCommentDao commentDao;
    private KnSocialBlogCollectDao collectDao;
    private KnSocialBlogAtDao atDao;
    private KnSocialBlogSettingsDao settingsDao;
    @Autowired
    private OrganizationService os;
    @Autowired
    private KnEmployeeDao empDao;
    @Transactional(readOnly=false)
    public void DeleteKnSocialBlogCollect(Long id){
        collectDao.deleteByBlogId(id);
    }
    @Transactional(readOnly=false)
    public void DeleteCommentByBlogId(Long id){
        commentDao.deleteByBlogId(id);
    }
    @Transactional(readOnly=false)
    public void DeleteKnSocialBlogAt(Long id){
        atDao.deleteByBlogId(id);
    }
    @Transactional(readOnly=false)
    public void DeleteKnSocialBlogFiles(Long id){
        fileDao.deleteByBlogId(id);
    }
    public List<KnSocialBlog> PageKnSocialBlog(Integer pageNo,Integer pageSize){
        return blogDao.findAllKnSocialBlog(System.currentTimeMillis(),new PageRequest(pageNo,pageSize)).getContent();
    }
    public List<KnSocialBlog> ActivityBlog(Integer pageNo,Integer pageSize){
        return blogDao.findActivityBlog(new PageRequest(pageNo,pageSize)).getContent();
    }
    public List<KnSocialBlog> PageKnSocialBlogByUserId(Long userId,Integer pageNo,Integer pageSize){
        return blogDao.findKnSocialBlogByUserId(userId,new PageRequest(pageNo,pageSize)).getContent();
    }
    public List<KnSocialBlog> PageCollect(Long userId,Integer pageNo,Integer pageSize){
        return collectDao.findKnSocialBlogByUserId(userId,new PageRequest(pageNo,pageSize)).getContent();
    }
    public List<KnSocialBlogComment> PageKnSocialBlogCommentByMId(Long userId,Integer pageNo,Integer pageSize){
        return commentDao.findKnSocialBlogCommentByMId(userId,new PageRequest(pageNo,pageSize)).getContent();
    }
    public KnSocialBlog ReadKnSocialBlog(Long id){
        return blogDao.findOne(id);
    }
    public Integer ReadKnSocialBlogCountByUserId(Long userId){
        return blogDao.findKnSocialBlogCountByUserId(userId);
    }
    public Integer ReadKnSocialBlogAttentionCountByFocusUserId(Long focusUserId){
        return attentionDao.findKnSocialBlogAttentionCountByFocusUserId(focusUserId);
    }
    public Integer ReadKnSocialBlogAttentionCountByUserId(Long userId){
        return attentionDao.findKnSocialBlogAttentionCountByUserId(userId);
    }
    public Integer ReadKnSocialBlogCollectCountByUserId(Long userId){
        return collectDao.findKnSocialBlogCollectCount(userId);
    }
    public KnSocialBlogAttention ReadKnSocialBlogAttentionByUserIdAndFocusUserId(Long userId,Long focusUserId){
        return attentionDao.findByUserIdAndFocusUserId(userId,focusUserId);
    }
    public KnSocialBlogCollect ReadByUserIdAndKsbId(Long userId,Long id){
        return collectDao.findByUserIdAndKsbId(userId,id);
    }
    public List<KnEmployee> PageUserForFans(Long focusUserId,Integer pageNo,Integer pageSize){
        return attentionDao.findKnEmployeeByFocusUserId(focusUserId,new PageRequest(pageNo,pageSize)).getContent();
    }
    public List<KnEmployee> PageAttentionUser(Long userId,Integer pageNo,Integer pageSize){
        return attentionDao.findKnEmployeeByUserId(userId,new PageRequest(pageNo,pageSize)).getContent();
    }
    public List<KnEmployee> PageAttentionUserSearch(Long userId,String userName,Integer pageNo,Integer pageSize){
        if(!Strings.isNullOrEmpty(userName)){
            userName="%"+userName+"%";
        }else{
            userName="%%";
        }
        return attentionDao.findKnEmployeeByUserIdAndUserName(userId,userName,new PageRequest(pageNo,pageSize)).getContent();
    }
    public List<Long> ListBlogIds(Long userId){
        return blogDao.findIds(userId);
    }
    public List<KnSocialBlog> ReadKnSocialBlogByIds(List<Long> ids,Integer pageNo,Integer pageSize){
        return blogDao.findTranspondKnSocialBlog(ids,new PageRequest(pageNo,pageSize)).getContent();
    }
    public List<KnSocialBlogComment> ReadKnSocialBlogCommentByMId(Long mId,Integer pageNo,Integer pageSize){
        return commentDao.findCommentMId(mId,new PageRequest(pageNo,pageSize)).getContent();
    }
    public List<KnSocialBlogComment> ReadKnSocialBlogCommentByMIdAndPId(Long mId,Long pId,Integer pageNo,Integer pageSize){
        return commentDao.findCommentMIdAndPId(mId,pId,new PageRequest(pageNo,pageSize)).getContent();
    }
    public KnSocialBlogCollect ReadKnSocialBlogCollectByUserIdAndId(Long userId,Long id){
        return collectDao.findByUserIdAndKsbId(userId,id);
    }
    public List<KnSocialBlog> PageFocusUserBlog(Long userId,Integer pageNo,Integer pageSize){
        return blogDao.findFocusUserBlog(userId,new PageRequest(pageNo,pageSize)).getContent();
    }
    public List<KnSocialBlog> PageAtUserBlog(Long userId,Integer pageNo,Integer pageSize){
        return blogDao.findAtUserBlog(userId,new PageRequest(pageNo,pageSize)).getContent();
    }
    public KnSocialBlogSettings ReadKnSocialBlogSettingsByType(KnSocialBlogSettings.EmailType type){
        return settingsDao.findByType(type);
    }
    public KnSocialBlogComment ReadKnSocialBlogComment(Long id){
        return commentDao.findOne(id);
    }
    @Transactional(readOnly=false)
    public KnSocialBlogSettings SaveKnSocialBlogSettings(KnSocialBlogSettings socialBlogSettings){
        return settingsDao.save(socialBlogSettings);
    }
    @Transactional(readOnly=false)
    public KnSocialBlog SaveKnSocialBlog(KnSocialBlog ksb){
        return blogDao.save(ksb);
    }
    @Transactional(readOnly=false)
    public KnSocialBlogFile SaveKnSocialBlogFile(KnSocialBlogFile file){
        return fileDao.save(file);
    }
    @Transactional(readOnly=false)
    public KnSocialBlogAttention SaveKnSocialBlogAttention(KnSocialBlogAttention attention){
        return attentionDao.save(attention);
    }
    @Transactional(readOnly=false)
    public KnSocialBlogComment SaveKnSocialBlogComment(KnSocialBlogComment comment){
        return commentDao.save(comment);
    }
    @Transactional(readOnly=false)
    public void DeleteKnSocialBlogComment(Long id){
        commentDao.delete(id);
    }
    @Transactional(readOnly=false)
    public void DeleteKnSocialBlog(Long id){
        blogDao.delete(id);
    }
    @Transactional(readOnly=false)
    public KnSocialBlogCollect SaveKnSocialBlogCollect(KnSocialBlogCollect collect){
        return collectDao.save(collect);
    }
    @Transactional(readOnly=false)
    public void DeleteKnSocialBlogCollect(KnSocialBlogCollect collect){
        collectDao.delete(collect);
    }
    @Transactional(readOnly=false)
    public void DeleteKnSocialBlogAttention(KnSocialBlogAttention attention){
        attentionDao.delete(attention);
    }
    @Transactional(readOnly=false)
    public void SaveKnSocialBlogAt(List<KnSocialBlogAt> ats){
        atDao.save(ats);
    }
    @Autowired
    public void setBlogDao(KnSocialBlogDao blogDao){
        this.blogDao=blogDao;
    }
    @Autowired
    public void setFileDao(KnSocialBlogFileDao fileDao){
        this.fileDao=fileDao;
    }
    @Autowired
    public void setAttentionDao(KnSocialBlogAttentionDao attentionDao){
        this.attentionDao=attentionDao;
    }
    @Autowired
    public void setCommentDao(KnSocialBlogCommentDao commentDao){
        this.commentDao=commentDao;
    }
    @Autowired
    public void setCollectDao(KnSocialBlogCollectDao collectDao){
        this.collectDao=collectDao;
    }
    @Autowired
    public void setAtDao(KnSocialBlogAtDao atDao){
        this.atDao=atDao;
    }
    @Autowired
    public void setSettingsDao(KnSocialBlogSettingsDao settingsDao){
        this.settingsDao=settingsDao;
    }
    /**论坛web后台操作**/
    /**
     * 查询论坛列表
     *
     * @param searchParams 查询条件
     * @param dt
     *
     * @return
     */
    public DataTable<KnSocialBlog> PageKnSocialBlog(final Map<String,Object> searchParams,DataTable<KnSocialBlog> dt){
        Specification<KnSocialBlog> spec=new Specification<KnSocialBlog>(){
            @Override
            public Predicate toPredicate(Root<KnSocialBlog> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                List<Predicate> predicates=Lists.newArrayList();
                for(String key : searchParams.keySet()){
                    if(key.indexOf("startTime")>0&&!Strings.isNullOrEmpty(searchParams.get(key).toString())){
                        predicates.add(cb.ge(root.<Long>get("publishTime"),new DateTime(searchParams.get(key)).getMillis()));
                    }
                    if(key.indexOf("endTime")>0&&!Strings.isNullOrEmpty(searchParams.get(key).toString())){
                        predicates.add(cb.le(root.<Long>get("publishTime"),new DateTime(searchParams.get(key)).getMillis()));
                    }
                    if(key.indexOf("userName")>0&&!Strings.isNullOrEmpty(searchParams.get(key).toString())){
                        predicates.add(cb.like(root.<String>get("userName"),"%"+searchParams.get(key).toString()+"%"));
                    }
                    if(key.indexOf("messageInfo")>0&&!Strings.isNullOrEmpty(searchParams.get(key).toString())){
                        predicates.add(cb.like(root.<String>get("messageInfo"),"%"+searchParams.get(key).toString()+"%"));
                    }
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),new Sort(Sort.Direction.DESC,"publishTime"));
        Page<KnSocialBlog> page=blogDao.findAll(spec,pageRequest);
        dt.setAaData(page.getContent());
        return dt;
    }
    public DataTable<KnSocialBlogComment> PageKnSocialBlogComment(final Long mId,DataTable<KnSocialBlogComment> dt){
        Specification<KnSocialBlogComment> spec=new Specification<KnSocialBlogComment>(){
            @Override
            public Predicate toPredicate(Root<KnSocialBlogComment> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                List<Predicate> predicates=Lists.newArrayList();
                predicates.add(cb.ge(root.<Long>get("mId"),mId));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),new Sort(Sort.Direction.DESC,"commentTime"));
        Page<KnSocialBlogComment> page=commentDao.findAll(spec,pageRequest);
        dt.setAaData(page.getContent());
        return dt;
    }

    @Transactional(readOnly=false)
    public void SaveActivity(KnSocialBlogDTO dto){
        dto.setEndTime(String.valueOf(DateTime.parse(dto.getEndTime(),DateTimeFormat.forPattern("yyyy-MM-dd HH:mm")).getMillis()));
        KnSocialBlog ksb=BeanMapper.map(dto,KnSocialBlog.class);
        ksb.setUserId(Users.id());
        KnEmployee emp=os.ReadEmp(ksb.getUserId());
        if(emp!=null){
            ksb.setUserName(emp.getUserName()==null?"":emp.getUserName());
        }
        ksb.setCreateTime(new Date().getTime());
        ksb.setPublishTime(new Date().getTime());
        ksb.setActive(IdEntity.ActiveType.ENABLE);
        ksb.setType(2);
        //ksb.setEndTime(new DateTime("yyyy-MM-dd HH:mm").getMillis());
        ksb=SaveKnSocialBlog(ksb);
        System.out.print(dto.getAttachTemp());
        if(dto.getAttachTemp()!=null && !dto.getAttachTemp().equals("[]")){
            KnSocialBlogFile[] files=JsonMapper.nonDefaultMapper().fromJson(dto.getAttachTemp(),KnSocialBlogFile[].class);
            for(KnSocialBlogFile file : files){
                file.setKsb(ksb);
                file.setUpdateId(1l);
                file.setCreateId(1l);
                file.setUpdateTime(System.currentTimeMillis());
                file.setCreateTime(System.currentTimeMillis());
                SaveKnSocialBlogFile(file);
            }
        }

        if(dto.getMessageType().equals(KnSocialBlog.MessageType.FORWARD)){
            KnSocialBlog blog=ReadKnSocialBlog(dto.getParentId());
            blog.setActive(IdEntity.ActiveType.ENABLE);
            blog.setMessageTranspondNum(blog.getMessageTranspondNum()==null?1:blog.getMessageTranspondNum()+1);
            SaveKnSocialBlog(blog);
        }
        if(!Strings.isNullOrEmpty(dto.getAtUsers())){
            saveKnSocialBlogAt(ksb.getId(),dto.getAtUsers());
        }
    }

    /**
     * 微博@功能
     *
     * @param id      微博id
     * @param atUsers 用户集合  例如： 1#张三,2#李四
     */
    private void saveKnSocialBlogAt(Long id,String atUsers){
        List<KnSocialBlogAt> list=Lists.newArrayList();
        String[] users=atUsers.split(",");
        for(int i=0;i<users.length;i++){
            String[] user=users[i].split("#");
            KnSocialBlogAt at=new KnSocialBlogAt();
            at.setCreateTime(System.currentTimeMillis());
            at.setAtTime(System.currentTimeMillis());
            at.setAtUserId(Long.parseLong(user[0]));
            at.setAtUserName(user[1]);
            at.setmId(id);
            list.add(at);
        }
        SaveKnSocialBlogAt(list);
    }

    /**
     * 获取人所在组织，如果有多个主组织或者没有，都会返回一个错误message
     *
     * @param empId
     *
     * @return
     */
    public String[] ReceipterMessage(Long empId){
        String[] result=new String[]{"","",""};
        if(empId>0){
            KnEmployee employee=os.ReadKnEmployee(empId);
            if(employee!=null){
                result[2]=employee.getImageAddress();
                List<String> orgs=this.QueryOrgNames(empId);
                String errMsg=null;
                if(orgs==null||orgs.size()==0){
                    errMsg="回执人没有主组织。";
                }else if(orgs.size()>1){
                    errMsg="回执人有多个主组织。";
                }else{
                    result[1]=orgs.get(0);
                }
                result[0]=errMsg;
            }
        }
        return result;
    }

    /**
     * 获取部门名称列表
     *
     * @param userId
     *
     * @return
     */
    public List<String> QueryOrgNames(Long userId){
        KnEmployee employee=os.ReadKnEmployee(userId);
        List<String> orgNames=new ArrayList<>();
        if(employee!=null){
            Set<KnEmployeeOrganization> orgs=employee.getOrg();
            if(orgs!=null&&orgs.size()>0){
                for(KnEmployeeOrganization org : orgs){
                    if(org.getMajor()==1){
                        String orgName=org.getId().getOrg().getName();
                        orgNames.add(orgName);
                    }
                }
            }
        }
        return orgNames;
    }

    public KnEmployee ReadKnEmployee(Long id){
        return empDao.findOne(id);
    }

    /**
     * 查询数量
     * @param mId
     * @return
     */
    public Long queryCount(Long mId){
        return commentDao.findBlogCommentCountByMId(mId);
    }
}
