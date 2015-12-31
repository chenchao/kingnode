package com.kingnode.news.service;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.QueryHint;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kingnode.diva.mapper.BeanMapper;
import com.kingnode.diva.persistence.DynamicSpecifications;
import com.kingnode.diva.persistence.SearchFilter;
import com.kingnode.diva.utils.Collections3;
import com.kingnode.news.dao.KnArticleCategoryDao;
import com.kingnode.news.dao.KnArticleCommentDao;
import com.kingnode.news.dao.KnArticleDao;
import com.kingnode.news.dao.KnNoticeDao;
import com.kingnode.news.dao.KnNoticeReceiptDao;
import com.kingnode.news.dao.KnWelfareDao;
import com.kingnode.news.dto.KnArticleCommentDTO;
import com.kingnode.news.dto.KnNoticeDTO;
import com.kingnode.news.dto.WelfareDTO;
import com.kingnode.news.entity.KnArticle;
import com.kingnode.news.entity.KnArticleCategory;
import com.kingnode.news.entity.KnArticleComment;
import com.kingnode.news.entity.KnNotice;
import com.kingnode.news.entity.KnNoticeReceipt;
import com.kingnode.news.entity.KnWelfare;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.dao.system.KnOrganizationDao;
import com.kingnode.xsimple.entity.IdEntity.ActiveType;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnEmployeeOrganization;
import com.kingnode.xsimple.entity.system.KnOrganization;
import com.kingnode.xsimple.rest.DetailDTO;
import com.kingnode.xsimple.service.safety.MessageService;
import com.kingnode.xsimple.service.system.OrganizationService;
import com.kingnode.xsimple.util.Users;
import com.kingnode.xsimple.util.Utils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Component @Transactional(readOnly=true) public class NewsService{
    private static org.slf4j.Logger logger=LoggerFactory.getLogger(NewsService.class);
    private KnArticleCommentDao knacd;
    private KnArticleDao kad;
    private KnArticleCategoryDao kacd;
    private KnNoticeDao knd;
    private KnNoticeReceiptDao knrd;
    private KnOrganizationDao kod;
    private KnWelfareDao kwd;
    private OrganizationService os;
    private MessageService ms;
    /** 新闻分类管理 begin* */
    public Page<KnArticleCategory> PageKnArticleCategory(Map<String,Object> searchParams,int pageNumber,int pageSize,String sortType){
        Sort sort=null;
        if("auto".equals(sortType)){
            sort=new Sort(Sort.Direction.DESC,"id");
        }else if("title".equals(sortType)){
            sort=new Sort(Sort.Direction.ASC,"name");
        }
        PageRequest pageRequest=new PageRequest(pageNumber-1,pageSize,sort);
        Map<String,SearchFilter> filters=SearchFilter.parse(searchParams);
        Specification<KnArticleCategory> spec=DynamicSpecifications.bySearchFilter(filters.values());
        return kacd.findAll(spec,pageRequest);
    }
    public KnArticleCategory ReadKnArticleCategory(Long id){
        return kacd.findOne(id);
    }
    public List<KnArticleCategory> ListKnArticleCategory(Long id){
        return kacd.findByIdNot(id);
    }
    @Transactional(readOnly=false) public void SaveKnArticleCategory(KnArticleCategory kac){
        kacd.save(kac);
    }
    @Transactional(readOnly=false) public void DeleteKnArticleCategory(Long id){
        kacd.delete(id);
    }
    /** 新闻分类管理 end* */
    /** 新闻标签管理 begin* */
    /**
     * 删除新闻评论
     *
     * @param id
     *
     * @return
     */
    @Transactional(readOnly=false) public boolean DeleteKnArticleComment(Long id){
        List<KnArticleComment> list=knacd.findByPid(id);
        knacd.delete(list);
        KnArticleComment com=knacd.findOne(id);
        KnArticle cle=kad.findOne(com.getArticleId());
        cle.setComments(cle.getComments()-1);
        kad.save(cle);
        knacd.delete(id);
        return true;
    }
    /** 新闻标签管理 end* */
    /** 新闻内容评论 start */
    @Transactional(readOnly=false) public boolean SaveKnArticleComment(String id,String content,String pid){
        try{
            KnArticleCommentDTO dto=new KnArticleCommentDTO();
            dto.setArticleId(Long.valueOf(id));
            dto.setContent(content);
            dto.setPid(pid!=null&&!"".equals(pid)?Long.valueOf(pid):0l);
            KnArticleComment comm=BeanMapper.map(dto,KnArticleComment.class);
            KnArticle cle=kad.findOne(dto.getArticleId());
            //保存评论
            comm.setIsShow(true);
            comm.setTitle(cle.getTitle());
            comm.setUserId(Users.id());
            comm.setName(Users.name());
            comm.setDepth(2l);
            comm.setcTime(new Date().getTime());
            knacd.save(comm);
            //修改新闻评论数
            cle.setComments(cle.getComments()+1);
            kad.save(cle);
        }catch(Exception e){
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }
    /**
     * 删除
     *
     * @param ids
     */
    @Transactional(readOnly=false) public void deleteKnArticle(String[] ids){
        if(ids!=null&&ids.length>0){
            for(String id : ids){
                kad.delete(Long.valueOf(id));
            }
        }
    }
    /**
     * 获取评论内容
     *
     * @param commentId
     *
     * @return
     */
    public KnArticleComment FindKnArticleComment(Long commentId){
        return knacd.findOne(commentId);
    }
    /**
     * 用于外部接口，根据时间和头条进行排序
     *
     * @param pageNo
     * @param pageSize
     *
     * @return
     */
    public Page<KnArticle> PageKnArticle(int pageNo,int pageSize,final Long articleId){
        List<Sort.Order> orders=Lists.newArrayList();
        orders.add(new Sort.Order(Sort.Direction.DESC,"createTime"));
        orders.add(new Sort.Order(Sort.Direction.DESC,"isTop"));
        PageRequest pageRequest=new PageRequest(pageNo,pageSize,new Sort(orders));
        Specification<KnArticle> spec=new Specification<KnArticle>(){
            @Override public Predicate toPredicate(Root<KnArticle> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                List<Predicate> predicates=Lists.newArrayList();
                Predicate p=cb.or(cb.isNull(root.<Long>get("effectiveTime")),cb.ge(root.<Long>get("effectiveTime"),new Date().getTime()));
                predicates.add(cb.and(p));
                if(articleId!=null&&articleId.intValue()>0){
                    predicates.add(cb.notEqual(root.<Long>get("id"),articleId));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return kad.findAll(spec,pageRequest);
    }
    /**
     * 获取最近的头条
     *
     * @param knArticles
     *
     * @return
     */
    public KnArticle QueryTop(List<KnArticle> knArticles){
        KnArticle knArticle=null;
        if(knArticles!=null&&knArticles.size()>0){
            for(KnArticle cle : knArticles){
                if(cle.getIsTop()){
                    knArticle=cle;
                    break;
                }
            }
            if(knArticle==null){
                knArticle=knArticles.get(0);
            }
        }
        return knArticle!=null?knArticles.get(0):new KnArticle();
    }
    /**
     * 获取当前时间的年月日
     *
     * @return
     */
    public Date FindDate(Date date,String patter){
        SimpleDateFormat sd=new SimpleDateFormat(patter);
        Date result=null;
        try{
            result=sd.parse(sd.format(date));
        }catch(ParseException e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return result;
    }
    /**
     * 查询新闻
     *
     * @param params
     * @param dt
     * @param getPubDate
     * @param letPubDate
     *
     * @return
     */
    public DataTable<KnArticle> PageKnArticle(Map<String,Object> params,DataTable<KnArticle> dt,String getPubDate,String letPubDate){
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        String[] column=new String[]{"pubDate","title","title","isTop","hits","comments"};
        List<Sort.Order> orders=Lists.newArrayList();
        Sort.Order order1=new Sort.Order(Sort.Direction.DESC,"pubDate");
        Sort.Order order4=new Sort.Order(Sort.Direction.DESC,"createTime");
        Sort.Order order2=new Sort.Order(Sort.Direction.DESC,"isTop");
        Sort.Order order3=new Sort.Order(d,column[Integer.parseInt(dt.getiSortCol_0())]);
        orders.add(order1);
        orders.add(order2);
        orders.add(order4);
        orders.add(order3);
        //orders.add(new Order(Sort.Direction.ASC,"createtime"));
        Sort sort=new Sort(d,column[Integer.parseInt(dt.getiSortCol_0())]);
        final String startTime=params.get("LIKE_startTime")!=null&&!"".equals(params.get("LIKE_startTime").toString())?params.get("LIKE_startTime").toString():"";
        final String endTime=params.get("LIKE_endTime")!=null&&!"".equals(params.get("LIKE_endTime").toString())?params.get("LIKE_endTime").toString():"";
        final String title=params.get("LIKE_title")!=null&&!"".equals(params.get("LIKE_title").toString())?"%"+params.get("LIKE_title").toString()+"%":"%%";
        final String isTop=params.get("LIKE_isTop")!=null&&!"".equals(params.get("LIKE_isTop").toString())?params.get("LIKE_isTop").toString():"";
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),new Sort(orders));
        Specification<KnArticle> spec=new Specification<KnArticle>(){
            @Override public Predicate toPredicate(Root<KnArticle> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                List<Predicate> predicates=Lists.newArrayList();
                if(!Strings.isNullOrEmpty(startTime)){
                    predicates.add(cb.ge(root.<Long>get("pubDate"),new DateTime(startTime).getMillis()));
                }
                if(!Strings.isNullOrEmpty(endTime)){
                    predicates.add(cb.le(root.<Long>get("pubDate"),new DateTime(endTime).getMillis()));
                }
                if(!Strings.isNullOrEmpty(title)){
                    predicates.add(cb.like(root.<String>get("title"),title));
                }
                if(!Strings.isNullOrEmpty(isTop)){
                    predicates.add(cb.equal(root.<Boolean>get("isTop"),"true".equals(isTop)));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        Page<KnArticle> page=kad.findAll(spec,pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    public KnArticle ReadKnArticle(Long id){
        return kad.findOne(id);
    }
    /**
     * 修改新闻的浏览数
     *
     * @param cle
     */
    @Transactional(readOnly=false) public void UpdateKnArticle(KnArticle cle){
        cle.setHits(cle.getHits()+1);
        kad.save(cle);
    }
    /**
     * 保存新闻
     *
     * @param ka
     * @param effectiveTime
     */
    @Transactional(readOnly=false) public void SaveKnArticle(KnArticle ka,String effectiveTime){
        if(effectiveTime!=null&&!"".equals(effectiveTime.trim())){
            SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd");
            try{
                Date date=sd.parse(effectiveTime);
                ka.setEffectiveTime(date.getTime());
            }catch(ParseException e){
                logger.error(e.getMessage());
            }
        }
        List<KnArticle> list=kad.queryKnArticle(ka.getTitle(),ka.getContent());
        if(Utils.isEmpityCollection(list)||ka.getId()!=null){
            //防止重复提交
            kad.save(ka);
        }
    }
    /**
     * 删除新闻
     *
     * @param id
     */
    @Transactional(readOnly=false) public void DeleteKnArticle(Long id){
        KnArticle ka=kad.findOne(id);
        //ka.getTags().clear();
        //ka.getComments().clear();
        kad.delete(ka);
    }

    /**
     * 删除公告
     *
     * @param id
     */
    @Transactional(readOnly=false) public void DeleteKnNotice(Long id){
        //删除公告回执
        List<KnNoticeReceipt> list = knrd.queryKnNoticeReceipts(id);
        if(!Utils.isEmpityCollection(list)){
            for(KnNoticeReceipt receipt : list){
                knrd.delete(receipt);
            }
        }

        //删除公告
        KnNotice ka=knd.findOne(id);
        knd.delete(ka);
    }
    /**
     * 删除工会福利
     *
     * @param id
     */
    @Transactional(readOnly=false) public void DeleteWelfare(Long id){
        KnWelfare welfare=kwd.findOne(id);
        //ka.getTags().clear();
        //ka.getComments().clear();
        kwd.delete(welfare);
    }
    @Transactional(readOnly=false) public boolean PublicationArticle(List<Long> ids,boolean pub){
        for(KnArticle ka : kad.findAll(ids)){
            ka.setPubDate(System.currentTimeMillis());
            kad.save(ka);
        }
        return true;
    }
    /**
     * 获取当天的头条新闻,如果没有设置头条，则返回空
     *
     * @return
     */
    public KnArticle QueryTopArticle(Long articleId){
        //        long cur=this.FindDate(new Date(),"yyyy-MM-dd").getTime();
        KnArticle cle=kad.findOne(articleId);
        List<KnArticle> list=kad.queryTopKnArticle(cle.getPubDate());
        if(list!=null&&list.size()>0){
            return list.get(0);
        }
        return new KnArticle();
    }
    /**
     * 获取最近的头条
     *
     * @return
     */
    public KnArticle QueryNewestTopArticle(){
        List<KnArticle> list=kad.queryTopKnArticle();
        if(list!=null&&list.size()>0){
            return list.get(0);
        }
        return new KnArticle();
    }
    /**
     * 设置/取消 头条
     *
     * @param id
     *
     * @return
     */
    @Transactional(readOnly=false) public boolean TopicArticle(Long id){
        final KnArticle ka=kad.findOne(id);
        if(!ka.getIsTop()){
            KnArticle oldtopic=QueryTopArticle(id);
            if(oldtopic.getId()!=null){
                oldtopic.setIsTop(false);
                kad.save(oldtopic);
            }
        }
        if(!ka.getIsTop()){
            try{
                new Thread(new Runnable(){
                    @Override public void run(){
                        SendTopMessage(ka);
                    }
                }).start();
            }catch(Exception e){
                logger.error(e.getMessage());
            }
        }
        ka.setIsTop(!ka.getIsTop());
        kad.save(ka);
        return true;
    }
    /**
     * 推送信息给所有用户
     *
     * @param article
     */
    public void SendTopMessage(KnArticle article){
        List<KnEmployee> employees=os.ListEmployee();
        List<Long> ids=Collections3.extractToList(employees,"id");
        Set<Long> setIds=new HashSet<>(ids);
        ms.SendMess(new ArrayList<Long>(setIds),Setting.MessageType.intermes.toString(),DateTime.now().toString("yyyy-MM-dd")+"头条新闻","您好,有新的头条新闻["+article.getTitle()+"]","您好,有新的头条新闻["+article.getTitle()+"]",102,"","102@"+article.getId());
    }
    /**
     * 设置或取消屏蔽
     *
     * @param id
     *
     * @return
     */
    @Transactional(readOnly=false) public boolean ShowComment(Long id){
        KnArticleComment comment=knacd.findOne(id);
        comment.setIsShow(!comment.getIsShow());
        knacd.save(comment);
        return true;
    }
    /**
     * 删除评论
     *
     * @param ids
     */
    @Transactional(readOnly=false) public void deleteComment(String[] ids){
        if(ids!=null&&ids.length>0){
            for(String id : ids){
                KnArticleComment comment=knacd.findOne(Long.valueOf(id));
                //修改新闻的评论数
                KnArticle ar=kad.findOne(comment.getArticleId());
                knacd.delete(Long.valueOf(id));
                if(ar!=null&&ar.getId()!=null){
                    ar.setComments(ar.getComments()-1);
                    kad.save(ar);
                }
            }
        }
    }
    /** 新闻内容评论 end */
    /** 新闻评论 start */
    /**
     * 查询评论信息，同时会把屏蔽了的评论也会查出来
     *
     * @param params
     * @param dt
     *
     * @return
     */
    public DataTable<KnArticleComment> PageKnArticleComment(Map<String,Object> params,DataTable<KnArticleComment> dt){
        final String startTime=params.get("LIKE_startTime")!=null&&!"".equals(params.get("LIKE_startTime").toString())?params.get("LIKE_startTime").toString():"";
        final String endTime=params.get("LIKE_endTime")!=null&&!"".equals(params.get("LIKE_endTime").toString())?params.get("LIKE_endTime").toString():"";
        final String title=params.get("LIKE_title")!=null&&!"".equals(params.get("LIKE_title").toString())?"%"+params.get("LIKE_title").toString()+"%":"%%";
        final String isShow=params.get("LIKE_isShow")!=null&&!"".equals(params.get("LIKE_isShow").toString())?params.get("LIKE_isShow").toString():"";
        final String name=params.get("LIKE_name")!=null&&!"".equals(params.get("LIKE_name").toString())?"%"+params.get("LIKE_name").toString()+"%":"%%";
        final String content=params.get("LIKE_content")!=null&&!"".equals(params.get("LIKE_content").toString())?"%"+params.get("LIKE_content").toString()+"%":"%%";
        final String articleId=params.get("LIKE_articleId")!=null&&!"".equals(params.get("LIKE_articleId").toString())?params.get("LIKE_articleId").toString():"";
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),new Sort(Sort.Direction.DESC,"createTime"));
        Specification<KnArticleComment> spec=new Specification<KnArticleComment>(){
            @Override public Predicate toPredicate(Root<KnArticleComment> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                List<Predicate> predicates=Lists.newArrayList();
                if(!Strings.isNullOrEmpty(startTime)){
                    predicates.add(cb.ge(root.<Long>get("createTime"),new DateTime(startTime).getMillis()));
                }
                if(!Strings.isNullOrEmpty(endTime)){
                    predicates.add(cb.le(root.<Long>get("createTime"),new DateTime(endTime).getMillis()));
                }
                if(!Strings.isNullOrEmpty(title)){
                    predicates.add(cb.like(root.<String>get("title"),title));
                }
                if(!Strings.isNullOrEmpty(name)){
                    predicates.add(cb.like(root.<String>get("name"),name));
                }
                if(!Strings.isNullOrEmpty(content)){
                    predicates.add(cb.like(root.<String>get("content"),content));
                }
                if(!Strings.isNullOrEmpty(isShow)){
                    predicates.add(cb.equal(root.<Boolean>get("isShow"),"true".equals(isShow)));
                }
                if(!Strings.isNullOrEmpty(articleId)){
                    predicates.add(cb.equal(root.<Long>get("articleId"),Long.valueOf(articleId)));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        Page<KnArticleComment> page=knacd.findAll(spec,pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    /**
     * 根据新闻id，查询相关评论，屏蔽掉的评论不会查询出来
     *
     * @param articleId
     * @param pageNo
     * @param pageSize
     *
     * @return
     */
    public Page<KnArticleComment> PageKnArticleComment(final Long articleId,int pageNo,int pageSize){
        List<Sort.Order> orders=Lists.newArrayList();
        orders.add(new Sort.Order(Sort.Direction.DESC,"createTime"));
        orders.add(new Sort.Order(Sort.Direction.DESC,"userId"));
        orders.add(new Sort.Order(Sort.Direction.DESC,"title"));
        PageRequest pageRequest=new PageRequest(pageNo,pageSize,new Sort(orders));
        Specification<KnArticleComment> spec=new Specification<KnArticleComment>(){
            @Override public Predicate toPredicate(Root<KnArticleComment> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                List<Predicate> predicates=Lists.newArrayList();
                predicates.add(cb.equal(root.<String>get("isShow"),true));
                predicates.add(cb.equal(root.<Long>get("articleId"),articleId));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        Page<KnArticleComment> page=knacd.findAll(spec,pageRequest);
        return page;
    }
    /** 新闻评论 end */
    /** 公告管理 start */
    public Page<KnNotice> PageKnNotice(final String path,Integer pageNo,Integer pageSize){
        return knd.findAll(new Specification<KnNotice>(){
            @Override public Predicate toPredicate(Root<KnNotice> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                Predicate p4=cb.like(root.<String>get("orgPath"),path+"%");
                cq.where(cb.and(p4));
                return cq.getRestriction();
            }
        },new PageRequest(pageNo,pageSize,new Sort(Sort.Direction.ASC,"pubTime")));
    }
    /**
     * 分页查询公告信息
     *
     * @param pageNo
     * @param pageSize
     *
     * @return
     */
    public Page<KnNotice> PageKnNotice(Integer pageNo,Integer pageSize){
        Sort.Direction d=Sort.Direction.DESC;
        Sort sort=new Sort(d,"pubTime");
        Pageable pageable=new PageRequest(pageNo,pageSize,sort);
        Page<KnNotice> page=knd.queryKnNoticeReceipt(Users.id(),System.currentTimeMillis(),pageable);
        return page;
        //        }
        //        return null;
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
     * 分页查询公告信息 不会查出失效了的公告信息
     *
     * @param searchParams
     * @param dt
     *
     * @return
     */
    public DataTable<KnNoticeDTO> PageKnNotice(Map<String,Object> searchParams,DataTable<KnNoticeDTO> dt){
        final Map<String,SearchFilter> filters=SearchFilter.parse(searchParams);
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),new Sort(Sort.Direction.DESC,"pubTime"));
        final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Specification<KnNotice> spec=new Specification<KnNotice>(){
            @Override public Predicate toPredicate(Root<KnNotice> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                List<Predicate> predicates=Lists.newArrayList();
                SearchFilter startDate=filters.get("GT_effectiveTime");
                if(startDate!=null){
                    predicates.add(cb.ge(root.<Long>get("createTime"),new DateTime(startDate.value).getMillis()));
                }
                SearchFilter endTime=filters.get("LT_effectiveTim");
                if(endTime!=null){
                    try{
                        predicates.add(cb.le(root.<Long>get("createTime"),sdf.parse(endTime+" 23:59:59").getTime()));
                    }catch(ParseException e){
                    }
                }
                SearchFilter title=filters.get("LIKE_title");
                if(title!=null){
                    predicates.add(cb.like(root.<String>get("title"),"%"+title.value+"%"));
                }
                SearchFilter pubUserName=filters.get("LIKE_pubUserName");
                if(pubUserName!=null){
                    predicates.add(cb.like(root.<String>get("pubUserName"),"%"+pubUserName.value+"%"));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        Page<KnNotice> page=knd.findAll(spec,pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(TranceForDto(page.getContent()));
        return dt;
    }
    /**
     * 把结果集转换成dto，主要是为了计算已回执和需要回执的数量
     *
     * @param notices
     *
     * @return
     *
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public List<KnNoticeDTO> TranceForDto(List<KnNotice> notices){
        List<KnNoticeDTO> dtos=new ArrayList<>();
        if(notices!=null&&notices.size()>0){
            for(KnNotice notice : notices){
                KnNoticeDTO dto=BeanMapper.map(notice,KnNoticeDTO.class);
                Set<KnNoticeReceipt> receipts=notice.getReceipts();
                QueryReceiptNum(dto,receipts);
                dtos.add(dto);
            }
        }
        return dtos;
    }
    /**
     * 把未回执数和需要回执数填充到dto中
     *
     * @param dto
     * @param receipts
     */
    private void QueryReceiptNum(KnNoticeDTO dto,Set<KnNoticeReceipt> receipts){
        if(receipts!=null&&receipts.size()>0){
            int arealdyReceipt=0;
            int needReceipt=0;
            for(KnNoticeReceipt noticeReceipt : receipts){
                if(noticeReceipt.getIsReceipt()!=null){
                    needReceipt++;
                    if(noticeReceipt.getIsReceipt().equals(ActiveType.ENABLE)){
                        arealdyReceipt++;
                    }
                }
            }
            dto.setAlreadyReceipt(arealdyReceipt);
            dto.setNeedReceipt(needReceipt);
        }
    }
    /**
     * 分页查询公告回执信息
     *
     * @param
     *
     * @return
     */
    public DataTable<KnNoticeReceipt> PageKnNoticeReceipt(Map<String,Object> searchParams,DataTable<KnNoticeReceipt> dataTable){
        //请求第几页数据
        int pageNum=dataTable.pageNo();
        //每页数据量
        int iDisplayLength=dataTable.getiDisplayLength();
        String userName=searchParams.get("LIKE_userName")!=null&&!"".equals(searchParams.get("LIKE_userName"))?"%"+searchParams.get("LIKE_userName").toString()+"%":"%%";
        String isReceipt=searchParams.get("EQ_isReceipt")!=null&&!"".equals(searchParams.get("EQ_isReceipt"))?searchParams.get("EQ_isReceipt").toString():"";
        List<ActiveType> types=Lists.newArrayList();
        if(!Strings.isNullOrEmpty(isReceipt)){
            ActiveType type=(isReceipt.equals("DISABLE")||"".equals(isReceipt)?ActiveType.DISABLE:ActiveType.ENABLE);
            types.add(type);
        }else{
            types.add(ActiveType.DISABLE);
            types.add(ActiveType.ENABLE);
        }
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dataTable.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        Sort sort=new Sort(d,"receiptTime");
        Pageable pageable=new PageRequest(pageNum,iDisplayLength,sort);
        String noticeId=searchParams.get("EQ_id").toString();
        //调用分页查询方法
        Page<KnNoticeReceipt> page_list=this.knrd.queryKnNoticeReceipt(Long.parseLong(noticeId),userName,types,pageable);
        //组装分页json结果
        dataTable.setiTotalDisplayRecords(page_list.getTotalElements());
        dataTable.setAaData(page_list.getContent());
        return dataTable;
    }
    @QueryHints({@QueryHint(name="org.hibernate.cacheable", value="true")}) public KnNotice ReadKnNotice(Long id){
        return knd.findOne(id);
    }
    /**
     * 是否已经回执
     *
     * @param noticId
     *
     * @return
     */
    public boolean HaveReceipt(Long noticId){
        KnNoticeReceipt receipt=knrd.queryKnNoticeReceipt(noticId,Users.id());
        if(receipt!=null){
            if(receipt.getIsReceipt()==null){
                return false;
            }
            return ActiveType.ENABLE.toString().equals(receipt.getIsReceipt().toString());
        }
        return false;
    }
    /**
     * 是否需要回执  是的话 返回true  不是的话 返回false
     *
     * @param noticId
     *
     * @return
     */
    public boolean HasNeedRecipt(Long noticId){
        KnNoticeReceipt receipt=knrd.queryKnNoticeReceipt(noticId,Users.id());
        return receipt!=null&&receipt.getIsReceipt()!=null&&!receipt.getIsReceipt().toString().equals("");
    }
    /**
     * 获取单个公告回执对象
     *
     * @param noticId
     *
     * @return
     */
    @Transactional(readOnly=false) public KnNoticeReceipt SaveKnNoticeReceipt(Long noticId){
        //先根据登录名找出KnUser对象，然后根据knUser的id，找出KnEmployee对象，再找出name
        //        KnEmployee employee=this.QueryKnEmployee(Users.ShiroUser().loginName);
        //        if(employee!=null){
        KnNoticeReceipt receipt=knrd.queryKnNoticeReceipt(noticId,Users.id());
        receipt.setIsReceipt(ActiveType.ENABLE);
        receipt.setreceiptTime(new Date().getTime());
        return knrd.save(receipt);
        //        }
        //        return null;
    }
    /**
     * 根据登录名，获取KnEmployee对象
     *
     * @param loginName
     *
     * @return
     */
    public KnEmployee QueryKnEmployee(String loginName){
        KnEmployee employee=os.ReadKnEmployee(loginName);
        if(employee!=null){
            return employee;
        }
        return null;
    }
    /**
     * 截取字符串,会过滤掉html标签
     *
     * @param dest
     * @param length
     *
     * @return
     */
    public String SubString(String dest,int length){
        if(dest==null){
            return "";
        }
        if(length==0){
            length=20;
        }
        String result="";
        Pattern p_script=Pattern.compile("<[^>]+>",Pattern.CASE_INSENSITIVE);
        Matcher m_script=p_script.matcher(dest);
        result=m_script.replaceAll(""); // 过滤html标签
        if(result.length()>length){
            result=result.substring(0,length);
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
    /**
     * 获取某个人公告未回执的数量
     *
     * @param loginName
     *
     * @return
     */
    public int QueryDisabelReceiptNum(String loginName){
        //先根据登录名找出KnUser对象，然后根据knUser的id，找出KnEmployee对象，再找出name
        KnEmployee employee=this.QueryKnEmployee(loginName);
        if(employee!=null){
            List<KnNoticeReceipt> receipts=knrd.queryKnNoticeReceipts(employee.getId(),ActiveType.DISABLE);
            if(receipts!=null&&receipts.size()>0){
                return receipts.size();
            }
        }
        return 0;
    }
    /**
     * 保存公告和回执信息
     *
     * @param kn
     * @param users
     * @param orgIds
     * @param effectiveTime
     */
    @Transactional(readOnly=false) public void SaveKnNotice(KnNotice kn,String[] users,String[] orgIds,String effectiveTime){
        //保存公告对象
        kn=SaveKnNotice(kn,orgIds,effectiveTime);
        //此处要根据发布的范围给所有人通知
        saveNoticeReceipt(kn,users,orgIds);
    }
    /**
     * 保存公告回执信息
     *
     * @param kn
     * @param users  下面3中类型的ids
     * @param orgIds 部门ids
     */
    private void saveNoticeReceipt(final KnNotice kn,String[] users,String[] orgIds){
        //先删除此公告的回执信息
        Set<KnNoticeReceipt> receipts=kn.getReceipts();
        if(receipts!=null&&receipts.size()>0){
            knrd.delete(receipts);
        }
        //批量保存
        final Set<KnNoticeReceipt> knNoticeReceiptSet=BulidKnNoticeReceipt(QueryUserIds(users),queryEmployees(orgIds),kn);
        if(knNoticeReceiptSet!=null&&knNoticeReceiptSet.size()>0){
            knrd.save(knNoticeReceiptSet);
            new Thread(new Runnable(){
                @Override public void run(){
                    //推送消息
                    Set<Long> empids=findEmpids(knNoticeReceiptSet);
                    SendNoticeMessage(kn,new ArrayList<Long>(empids));
                }
            }).start();
        }
    }
    /**
     * 获取所有人员ids
     *
     * @param receipts
     *
     * @return
     */
    private Set<Long> findEmpids(Set<KnNoticeReceipt> receipts){
        Set<Long> result=new HashSet<>();
        if(!Utils.isEmpityCollection(receipts)){
            for(KnNoticeReceipt receipt : receipts){
                result.add(receipt.getId().getUserId());
            }
        }
        return result;
    }
    /**
     * 给所有用户
     *
     * @param kn
     */
    public void SendNoticeMessage(KnNotice kn,List<Long> empids){
        ms.SendMess(empids,Setting.MessageType.intermes.toString(),"华大基因公告",kn.getTitle(),kn.getTitle(),101,"","101@"+kn.getId());
    }
    /**
     * 保存公告对象
     *
     * @param kn
     * @param orgIds
     * @param effectiveTime
     *
     * @return
     */
    private KnNotice SaveKnNotice(KnNotice kn,String[] orgIds,String effectiveTime){
        //保存公告对象
        KnOrganization organization=queryKnOrganization(orgIds);
        kn.setOrgName(organization.getName());
        kn.setOrgPath(arrayToString(orgIds,","));
        kn.setPubTime(new Date().getTime());
        kn.setEffectiveTime(effectiveTime!=null&&!"".equals(effectiveTime.trim())?new DateTime(effectiveTime).getMillis():null);
        kn.setPubUserName(Users.name());
        kn.setPubUserId(Users.id());
        kn=knd.save(kn);
        return kn;
    }
    /**
     * 服务范围现在只支持单个部门，以后需要改称支持多个部门
     *
     * @param orgIds
     *
     * @return
     */
    private KnOrganization queryKnOrganization(String[] orgIds){
        if(orgIds!=null&&orgIds.length>0){
            String id=orgIds[0];
            return kod.findOne(Long.parseLong(id));
        }
        return new KnOrganization();
    }
    /**
     * 多个部门
     *
     * @param orgIds
     *
     * @return
     */
    private List<KnOrganization> QueryKnOrganizations(String[] orgIds){
        List<KnOrganization> orgs=Lists.newArrayList();
        if(orgIds!=null&&orgIds.length>0){
            for(String id : orgIds){
                orgs.add(kod.findOne(Long.parseLong(id)));
            }
        }
        return orgs;
    }
    /**
     * 数据转换成字符串
     *
     * @param args
     * @param flag 分隔符
     *
     * @return
     */
    private String arrayToString(String[] args,String flag){
        if(args!=null&&args.length>0){
            String result="";
            for(String str : args){
                result+=str+flag;
            }
            if(result.length()>0){
                result=result.substring(0,result.length()-1);
            }
            return result;
        }
        return "";
    }
    /**
     * 根据界面传过来的部门id，获取部门对应的人员列表
     *
     * @param orgids
     *
     * @return
     */
    private Set<KnEmployee> queryEmployees(String[] orgids){
        if(orgids!=null&&orgids.length>0){
            Set<KnEmployee> result=new HashSet<>();
            for(String id : orgids){
                if(id!=null&&!"".equals(id)){
                    KnOrganization org=os.ReadOrg(Long.parseLong(id));
                    List<KnOrganization> orgs=os.findLowerOrganListByPath(org.getPath()+"%");
                    result.addAll(os.findEmpListByOrgIdList(Collections3.extractToList(orgs,"id")));
                }
            }
            return result;
        }
        return new HashSet<>();
    }
    /**
     * 根据部门id，获取部门人员列表
     *
     * @param orgid
     *
     * @return
     */
    private Set<KnEmployee> QueryEmployeesByOrgId(String orgid){
        List<Long> orgs=Lists.newArrayList();
        orgs.add(Long.parseLong(orgid));
        List<KnEmployee> employees=os.findEmpListByOrgIdList(orgs);
        Set<KnEmployee> result=new HashSet<>(employees);
        return result;
    }
    /**
     * 组装公告回执对象
     * @param users 员工id列表
     * @param noticeId 公告id
     * @return
     */
    /**
     * 组装公告回执对象
     *
     * @param users           特定接收人
     * @param serviceOrgUsers 服务范围人员
     * @param knNotice
     *
     * @return
     */
    private Set<KnNoticeReceipt> BulidKnNoticeReceipt(Set<KnEmployee> users,Set<KnEmployee> serviceOrgUsers,KnNotice knNotice){
        Set<KnNoticeReceipt> result=new HashSet<>();
        ActiveType receipt=ActiveType.DISABLE;
        //特定人员需要回执
        if(KnNotice.ReceiptType.assign.toString().equals(knNotice.getIsReceipt().toString())){
            receipt=null;
            result.addAll(QueryKnNoticeReceipts(users,knNotice,ActiveType.DISABLE));
        }
        //如果回执的是选择所有人/不需要回执
        if(KnNotice.ReceiptType.none.toString().equals(knNotice.getIsReceipt().toString())){
            receipt=null;
        }
        //获取服务范围列表
        result.addAll(QueryKnNoticeReceipts(serviceOrgUsers,knNotice,receipt));
        return result;
    }
    /**
     * 组装KnNoticeReceipt对象列表
     *
     * @param employees
     * @param knNotice
     * @param isReceipt
     *
     * @return
     */
    private Set<KnNoticeReceipt> QueryKnNoticeReceipts(Set<KnEmployee> employees,KnNotice knNotice,ActiveType isReceipt){
        Set<KnNoticeReceipt> result=new HashSet<>();
        if(employees!=null&&employees.size()>0){
            for(KnEmployee emp : employees){
                KnNoticeReceipt receipt=new KnNoticeReceipt(knNotice.getId(),emp.getId());
                receipt.setIsReceipt(isReceipt);
                receipt.setUserName(emp.getUserName());
                result.add(receipt);
            }
        }
        return result;
    }
    /**
     * 前台数据包括人员id，岗位id，角色id，此方法把这些信息都转换成人员id列表
     *
     * @param ids
     * @param
     *
     * @return
     */
    private Set<KnEmployee> QueryUserIds(String[] ids){
        if(ids==null||ids.length==0){
            return new HashSet<>();
        }
        Set<KnEmployee> result=new HashSet<>();
        List<Long> lids=Lists.newArrayList();
        for(int i=0;i<ids.length;i++){
            if(ids[i]!=null&&!"".equals(ids[i])){
                lids.add(Long.valueOf(ids[i]));
            }
        }
        result.addAll(os.ListEmployee(lids));
        return result;
    }
    /** 公告管理 end */
    //====================工会福利===================//
    public KnWelfare FindKnWelfare(Long id){
        return kwd.findOne(id);
    }
    /**
     * 获取组织
     *
     * @param orgids
     *
     * @return
     */
    public List<KnOrganization> FindOrgizion(String orgids){
        if(StringUtils.isNotEmpty(orgids)){
            String[] orgIds=orgids.split(",");
            List<Long> ids=Lists.newArrayList();
            for(String str : orgIds){
                ids.add(Long.parseLong(str));
            }
            return os.ListKnOrganization(ids);
        }
        return Lists.newArrayList();
    }
    /**
     * 分页查询工会福利信息 不会查出失效了的信息
     *
     * @param searchParams
     * @param dt
     *
     * @return
     */
    public DataTable<KnWelfare> PageKnWelfare(Map<String,Object> searchParams,DataTable<KnWelfare> dt){
        final String startDate=searchParams.get("GT_effectiveTime")!=null?searchParams.get("GT_effectiveTime").toString():"";
        final String endTime=searchParams.get("LT_effectiveTim")!=null&&!"".equals(searchParams.get("LT_effectiveTim").toString())?searchParams.get("LT_effectiveTim").toString():"";
        final String title=searchParams.get("LIKE_title")!=null&&!"".equals(searchParams.get("LIKE_title").toString())?"%"+searchParams.get("LIKE_title").toString()+"%":"%%";
        final String name=searchParams.get("LIKE_pubUserName")!=null&&!"".equals(searchParams.get("LIKE_pubUserName").toString())?"%"+searchParams.get("LIKE_pubUserName").toString()+"%":"%%";
        List<Sort.Order> orders=Lists.newArrayList();
        orders.add(new Sort.Order(Sort.Direction.DESC,"isTop"));
        orders.add(new Sort.Order(Sort.Direction.DESC,"createTime"));
        final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),new Sort(orders));
        Specification<KnWelfare> spec=new Specification<KnWelfare>(){
            @Override public Predicate toPredicate(Root<KnWelfare> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                List<Predicate> predicates=Lists.newArrayList();
                //Predicate p=cb.or(cb.isNull(root.<Long>get("effectiveTime")),cb.ge(root.<Long>get("effectiveTime"),new Date().getTime()));
                //predicates.add(cb.and(p));
                if(!Strings.isNullOrEmpty(startDate)){
                    predicates.add(cb.ge(root.<Long>get("createTime"),new DateTime(startDate).getMillis()));
                }
                if(!Strings.isNullOrEmpty(endTime))
                    try{
                        predicates.add(cb.le(root.<Long>get("createTime"),sdf.parse(endTime+" 23:59:59").getTime()));
                    }catch(ParseException e){
                    }
                if(!Strings.isNullOrEmpty(title)){
                    predicates.add(cb.like(root.<String>get("title"),title));
                }
                if(!Strings.isNullOrEmpty(name)){
                    predicates.add(cb.like(root.<String>get("pubUserName"),name));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        Page<KnWelfare> page=kwd.findAll(spec,pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    /**
     * 置顶/取消置顶
     *
     * @param id
     *
     * @return
     */
    @Transactional(readOnly=false) public boolean TopicKnWelfare(Long id){
        //先找出别的置顶的周边福利信息出来
        List<KnWelfare> welfares=kwd.findTopKnWelfare(id);
        for(KnWelfare fare : welfares){
            fare.setIsTop(false);
            kwd.save(fare);
        }
        KnWelfare ka=kwd.findOne(id);
        ka.setIsTop(!ka.getIsTop());
        kwd.save(ka);
        return true;
    }
    @Transactional public void SaveKnWelfare(final KnWelfare welfare,String effectiveTime,final String[] orgIds){
        List<KnWelfare> list=kwd.queryKnWelfare(welfare.getTitle(),welfare.getContent());
        //防止重复提交
        if(Utils.isEmpityCollection(list)||welfare.getId()!=null){
            if(effectiveTime!=null&&!"".equals(effectiveTime)){
                SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd");
                try{
                    Date date=sd.parse(effectiveTime);
                    welfare.setEffectiveTime(date.getTime());
                }catch(ParseException e){
                    logger.error(e.getMessage());
                }
            }
            List<KnOrganization> orgs=QueryKnOrganizations(orgIds);
            welfare.setOrgName(Collections3.extractToString(orgs,"name",","));
            welfare.setOrgPath(arrayToString(orgIds,","));
            welfare.setPubTime(new Date().getTime());
            welfare.setPubUserName(Users.name());
            welfare.setPubUserId(Users.id());
            welfare.setIsTop(false);
            kwd.save(welfare);
            //推送福利消息
            new Thread(new Runnable(){
                @Override public void run(){
                    SendWelfareMessage(welfare,orgIds);
                }
            }).start();
        }
    }
    /**
     * 给所有用户
     *
     * @param welfare
     */
    public void SendWelfareMessage(KnWelfare welfare,String[] orgIds){
        Set<KnEmployee> employees=queryEmployees(orgIds);
        List<Long> ids=Collections3.extractToList(employees,"id");
        Set<Long> setIds=new HashSet<>(ids);
        ms.SendMess(new ArrayList<Long>(setIds),Setting.MessageType.intermes.toString(),"华大基因工会福利",welfare.getTitle(),welfare.getTitle(),103,"","103@"+welfare.getId());
    }
    /**
     * 用于外部接口，根据时间和头条进行排序
     *
     * @param pageNo
     * @param pageSize
     *
     * @return
     */
    public List<WelfareDTO> PageKnWelfare(int pageNo,int pageSize){
        List<Sort.Order> orders=Lists.newArrayList();
        orders.add(new Sort.Order(Sort.Direction.DESC,"isTop"));
        orders.add(new Sort.Order(Sort.Direction.DESC,"updateTime"));
        Pageable pageable=new PageRequest(pageNo,pageSize,new Sort(orders));
        Page<KnWelfare> page=kwd.findKnWelfare(System.currentTimeMillis(),pageable);
        List<WelfareDTO> list=Lists.newArrayList();
        for(KnWelfare kw : page.getContent()){
            WelfareDTO wd=BeanMapper.map(kw,WelfareDTO.class);
            wd.setPubTime(new DateTime(kw.getPubTime()).toString("yyyy-MM-dd HH:mm"));
            wd.setName(kw.getPubUserName());
            list.add(wd);
        }
        return list;
    }
    public DetailDTO<WelfareDTO> ReadKnWelfare(Long id){
        KnWelfare kw=kwd.findOne(id);
        WelfareDTO wd=BeanMapper.map(kw,WelfareDTO.class);
        wd.setPubTime(new DateTime(kw.getPubTime()).toString("yyyy-MM-dd HH:mm"));
        wd.setName(kw.getPubUserName());
        return new DetailDTO<>(true,wd);
    }
    //====================工会福利===================//
    @Autowired public void setKnacd(KnArticleCommentDao knacd){
        this.knacd=knacd;
    }
    @Autowired public void setKad(KnArticleDao kad){
        this.kad=kad;
    }
    @Autowired public void setKacd(KnArticleCategoryDao kacd){
        this.kacd=kacd;
    }
    @Autowired public void setKnd(KnNoticeDao knd){
        this.knd=knd;
    }
    @Autowired public void setKnrd(KnNoticeReceiptDao knrd){
        this.knrd=knrd;
    }
    @Autowired public void setKod(KnOrganizationDao kod){
        this.kod=kod;
    }
    @Autowired public void setKwd(KnWelfareDao kwd){
        this.kwd=kwd;
    }
    @Autowired public void setOs(OrganizationService os){
        this.os=os;
    }
    @Autowired public void setMs(MessageService ms){
        this.ms=ms;
    }
}