package com.kingnode.library.service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kingnode.diva.utils.Threads;
import com.kingnode.library.dao.KnLibraryBookDao;
import com.kingnode.library.dao.KnLibraryBookUnitDao;
import com.kingnode.library.dao.KnLibraryBorrowDao;
import com.kingnode.library.dao.KnLibraryRuleDao;
import com.kingnode.library.entity.KnLibraryBook;
import com.kingnode.library.entity.KnLibraryBookUnit;
import com.kingnode.library.entity.KnLibraryBorrow;
import com.kingnode.library.entity.KnLibraryRule;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.dao.system.KnEmployeeDao;
import com.kingnode.xsimple.dao.system.KnUserDao;
import com.kingnode.xsimple.service.safety.MessageService;
import com.kingnode.xsimple.util.Users;
import com.kingnode.xsimple.util.Utils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ConstantConditions") @Service @Transactional(readOnly=true)
public class LibraryService implements Runnable{
    private static Logger logger=LoggerFactory.getLogger(LibraryService.class);
    private KnLibraryBookDao klbd;
    private KnLibraryBookUnitDao klbud;
    private KnLibraryBorrowDao klbbd;
    private KnLibraryRuleDao klrd;
    private RestTemplate restTemplate;
    private KnUserDao kud;
    private KnEmployeeDao ked;
    private MessageService ms;
    private String cronExpression="0 1 0 * * ?";
    //    private String cronExpression="0 45 15 * * ?";
    private int shutdownTimeout=20;
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    //============外部借口调用开始=============//
    /**
     * 外部接口   查询某人的所借书籍
     *
     * @param type     type（cur／his/all）（当前／历史/所有）
     * @param pageNo
     * @param pageSize
     *
     * @return
     */
    public Page<KnLibraryBorrow> listKnLibraryBorrow(final String type,final int pageNo,final int pageSize){
        final Long userId=Users.id();
        Specification<KnLibraryBorrow> spec=new Specification<KnLibraryBorrow>(){
            @Override
            public Predicate toPredicate(Root<KnLibraryBorrow> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                List<Predicate> predicates=Lists.newArrayList();
                predicates.add(cb.equal(root.<String>get("userId"),userId));
                if("cur".equals(type.toLowerCase())){
                    predicates.add(cb.isNull(root.<String>get("restoreDate")));
                }else if("his".equals(type.toLowerCase())){
                    predicates.add(cb.isNotNull(root.<String>get("restoreDate")));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        PageRequest pageRequest=new PageRequest(pageNo,pageSize,new Sort(Sort.Direction.DESC,"lendDate"));
        return klbbd.findAll(spec,pageRequest);
    }
    /**
     * 获取书本信息
     *
     * @param bookId
     *
     * @return
     */
    public KnLibraryBook findKnLibraryBook(Long bookId){
        return klbd.findOne(bookId);
    }
    /**
     * 根据书本id获取借阅列表
     *
     * @param bookId
     *
     * @return
     */
    public List<KnLibraryBorrow> queryKnLibraryBorrows(Long bookId){
        return klbbd.queryKnLibraryBorrows(bookId);
    }
    /**
     * 借书申请
     *
     * @param bookId
     * @param
     *
     * @return false 表示没有可借书籍
     */
    @Transactional(readOnly=false)
    public boolean lendBook(Long bookId){
        //先判断是否超出了限制范围
        KnLibraryRule rule=this.findKnLibraryRule();
        int limitUnit=0;
        if(rule!=null){
            limitUnit=rule.getLimitUnit();
        }
        int count=klbbd.queryBorrowsNum(Users.id()).size();
        if(count+1>limitUnit){
            throw new RuntimeException("对不起，你所借书籍数量已经超出了限制范围");//对不起，你所借书籍数量已经超出了限制范围。book1
        }
        //先根据bookid获取可借书本
        List<KnLibraryBookUnit> units=klbud.queryBorrowsCanLend(bookId);
        //选一本修改为预约借书状态
        if(units!=null&&units.size()>0){
            //获取第一本书籍，进行借阅
            KnLibraryBookUnit unit=units.get(0);
            unit.setBookType(KnLibraryBookUnit.BookType.RESERVE);
            klbud.save(unit);
            KnLibraryBorrow borrow=new KnLibraryBorrow();
            borrow.setKlbu(unit);
            borrow.setReserveDate(new Date().getTime());
            borrow.setUserId(Users.id());
            borrow.setName(Users.name());
            borrow.setBookCode(unit.getCode());
            borrow.setReserveDate(new Date().getTime());
            borrow.setBorrowType(KnLibraryBorrow.BorrowType.RESERVE);
            klbbd.save(borrow);
        }else{
            return false;
        }
        return true;
    }
    /**
     * 借书申请
     *
     * @param borrowId 借书信息id
     * @param
     *
     * @return false 表示没有可借书籍
     */
    @Transactional(readOnly=false)
    public boolean renewBook(Long borrowId){
        KnLibraryBorrow borrow=klbbd.findOne(borrowId);
        borrow.setRenewDate(new Date().getTime());
        borrow.setShouldRestoreDate(getRestoreDateForReserve(new Date()).getTime());
        //判断续借申请是否是规则允许的范围内
        int weekNum=0;
        KnLibraryRule rule=this.findKnLibraryRule();
        if(rule!=null){
            weekNum=rule.getRenew();
        }
        int days=7*weekNum;
        Date limitDate=DateUtils.addDays(new Date(borrow.getShouldRestoreDate()),-days);
        if(new Date().before(limitDate)){
            throw new RuntimeException("对不起，还没到续借时间，现在不能续借");//对不起，还没到续借时间，现在不能续借。book2
        }
        KnLibraryBookUnit unit=borrow.getKlbu();
        unit.setBookType(KnLibraryBookUnit.BookType.LEND);
        klbud.save(unit);
        borrow.setBorrowType(KnLibraryBorrow.BorrowType.RENEW);
        klbbd.save(borrow);
        return true;
    }
    /**
     * 取消借书
     *
     * @param borrowId 借书信息id
     * @param
     *
     * @return false 表示取消书籍
     */
    @Transactional(readOnly=false)
    public boolean cancel(Long borrowId){
        //修改借阅记录，增加取消时间和还书时间（主要是为了历史）
        KnLibraryBorrow borrow=klbbd.findOne(borrowId);
        borrow.setCancelDate(new Date().getTime());
        borrow.setRestoreDate(new Date().getTime());
        borrow.setBorrowType(KnLibraryBorrow.BorrowType.DESELECT);
        klbbd.save(borrow);
        //修改书本状态
        KnLibraryBookUnit unit=borrow.getKlbu();
        unit.setBookType(KnLibraryBookUnit.BookType.RESTORE);
        klbud.save(unit);
        return true;
    }
    /**
     * 得到借书归还时间
     * reserveDate 借阅时间
     *
     * @return
     */
    private Date getRestoreDateForReserve(Date reserveDate){
        Date date=new Date(reserveDate.getTime());
        KnLibraryRule rule=this.findKnLibraryRule();
        if(rule==null){
            //            throw new RuntimeException("[借阅规则－》每本图片每次可借阅时长]没有配置");
            date=DateUtils.addDays(date,0);
        }else{
            date=DateUtils.addDays(date,rule.getBorrowDay());
        }
        return date;
    }
    /**
     * 查询分页信息
     *
     * @param info
     * @param pageNo
     * @param pageSize
     *
     * @return
     */
    public Set<KnLibraryBook> listKnLibraryBookUnit(final String info,int pageNo,int pageSize){
        PageRequest pageRequest=new PageRequest(pageNo,pageSize,new Sort(Sort.Direction.DESC,"createTime"));
        Specification<KnLibraryBookUnit> spec=new Specification<KnLibraryBookUnit>(){
            @Override
            public Predicate toPredicate(Root<KnLibraryBookUnit> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                List<Predicate> predicates=Lists.newArrayList();
                predicates.add(cb.notEqual(root.<String>get("bookType"),KnLibraryBookUnit.BookType.SCRAPPING));
                if(!Strings.isNullOrEmpty(info)){
                    Predicate p=cb.or(cb.like(root.<KnLibraryBookUnit>get("kbl").<String>get("author"),"%"+info+"%"),cb.like(root.<KnLibraryBookUnit>get("kbl").<String>get("title"),"%"+info+"%"),cb.like(root.<KnLibraryBookUnit>get("kbl").<String>get("isbn"),"%"+info+"%"));
                    predicates.add(cb.and(p));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        Page<KnLibraryBookUnit> page=klbud.findAll(spec,pageRequest);
        List<KnLibraryBookUnit> list=page.getContent();
        Set<KnLibraryBook> set=new HashSet<>();
        if(list!=null&&list.size()>0){
            for(KnLibraryBookUnit unit : list){
                set.add(unit.getKbl());
            }
        }
        return set;
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
    //============外部借口调用结束=============//
    /**
     * 保存借书规则信息
     *
     * @param rule
     */
    @Transactional(readOnly=false)
    public KnLibraryRule save(KnLibraryRule rule){
        //klrd.deleteAll();
        return klrd.save(rule);
    }
    /**
     * 获取借书规则对象
     *
     * @return
     */
    public KnLibraryRule findKnLibraryRule(){
        List<KnLibraryRule> rules=(List<KnLibraryRule>)klrd.findAll();
        if(rules!=null&&rules.size()>0){
            return rules.get(0);
        }else{
            return null;
            //throw new RuntimeException("请先配置借阅规则！");
        }
    }
    /**
     * 修改书籍信息 逻辑删除
     *
     * @param ids
     */
    @Transactional(readOnly=false)
    public void delete(String[] ids){
        if(ids!=null&&ids.length>0){
            for(String id : ids){
                updateBookState(Long.valueOf(id),KnLibraryBookUnit.BookType.SCRAPPING);
            }
        }
    }
    /**
     * 修改书籍状态
     *
     * @param id
     * @param type
     */
    public void updateBookState(Long id,KnLibraryBookUnit.BookType type){
        KnLibraryBookUnit unit=klbud.findOne(id);
        if((unit.getBookType().toString().equals(KnLibraryBookUnit.BookType.RESERVE.toString())||unit.getBookType().toString().equals(KnLibraryBookUnit.BookType.LEND.toString()))&&(type.toString().equals(KnLibraryBookUnit.BookType.SCRAPPING.toString()))){
            throw new RuntimeException("book4");//此书已被预定或借出，暂时不能删除
        }
        unit.setBookType(type);
        klbud.save(unit);
    }
    /**
     * 还书，即设置书本的还书时间
     *
     * @param ids
     */
    @Transactional(readOnly=false)
    public void returnBook(String[] ids){
        if(ids!=null&&ids.length>0){
            for(String id : ids){
                KnLibraryBorrow borrow=klbbd.findOne(Long.valueOf(id));
                borrow.setRestoreDate(new Date().getTime());
                borrow.setBorrowType(KnLibraryBorrow.BorrowType.RESTORE);
                klbbd.save(borrow);
                updateBookState(borrow.getKlbu().getId(),KnLibraryBookUnit.BookType.RESTORE);
            }
        }
    }
    /**
     * 已领，即设置书本的借出时间
     *
     * @param ids
     */
    @Transactional(readOnly=false)
    public void borrow(String[] ids){
        if(ids!=null&&ids.length>0){
            for(String id : ids){
                KnLibraryBorrow borrow=klbbd.findOne(Long.valueOf(id));
                borrow.setLendDate(new Date().getTime());
                borrow.setShouldRestoreDate(this.getRestoreDateForReserve(new Date()).getTime());
                borrow.setBorrowType(KnLibraryBorrow.BorrowType.LEND);
                klbbd.save(borrow);
                updateBookState(borrow.getKlbu().getId(),KnLibraryBookUnit.BookType.LEND);
            }
        }
    }
    /**
     * 保存书籍信息
     *
     * @param unit
     */
    @Transactional(readOnly=false)
    public void save(KnLibraryBookUnit unit,String buyDate){
        //先判断书本编号是否已经存在，如果存在了，则不能再保存，主要是防止管理员打开了两个页面进行保存，导致编号重复的问题
        if(unit.getId()==null||unit.getId().intValue()<=0){
            KnLibraryBookUnit dbUnit=klbud.findByCode(unit.getCode());
            if(dbUnit!=null&&dbUnit.getId()!=null&&dbUnit.getId().intValue()>0){
                throw new RuntimeException("book5");
            }
        }
        //如果是新增，则先判断isbn号是否存在，如果存在，就做书本的修改操作
        if(unit.getId()==null||unit.getId()<=0){
            KnLibraryBook book=klbd.findByIsbn(unit.getKbl().getIsbn());
            if(book!=null){
                unit.getKbl().setId(book.getId());
            }
        }
        KnLibraryBook book=klbd.save(unit.getKbl());
        unit.setKbl(book);
        if(buyDate!=null&&!"".equals(buyDate.trim())){//DateTime
            SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd");
            try{
                Date date=sd.parse(buyDate);
                unit.setBuyDate(date.getTime());
            }catch(ParseException e){
                e.printStackTrace();
            }
        }
        klbud.save(unit);
    }
    /**
     * 获取书籍信息
     *
     * @param id
     *
     * @return
     */
    public KnLibraryBookUnit findKnLibraryBookUnit(Long id){
        return klbud.findOne(id);
    }
    /**
     * 根据书本信息，查询书本数量信息
     *
     * @return
     */
    public List<KnLibraryBookUnit> listKnLibraryBookUnit(Long bookId){
        return klbud.findByKblId(bookId);
    }
    /**
     * 获取可以借出数量
     *
     * @param list
     *
     * @return
     */
    public List<Integer> findEnabalBorrow(List<KnLibraryBookUnit> list){
        int canCount=0;
        int delCount=0;
        if(list!=null&&list.size()>0){
            for(KnLibraryBookUnit unit : list){
                if(unit.getBookType()!=null&&KnLibraryBookUnit.BookType.RESTORE.toString().equals(unit.getBookType().toString())){
                    canCount++;
                }
                if(unit.getBookType()!=null&&KnLibraryBookUnit.BookType.SCRAPPING.toString().equals(unit.getBookType().toString())){
                    delCount++;
                }
            }
        }
        List<Integer> resultList=Lists.newArrayList();
        resultList.add(canCount);
        resultList.add(delCount);
        return resultList;
    }
    /**
     * 查询分页信息
     *
     * @param params
     * @param dt
     *
     * @return
     */
    public DataTable<KnLibraryBorrow> listKnLibraryBorrow(Map<String,Object> params,DataTable<KnLibraryBorrow> dt){
        final String code=params.get("LIKE_code")!=null&&!"".equals(params.get("LIKE_code").toString())?"%"+params.get("LIKE_code").toString()+"%":"%%";
        final Object type=params.get("LIKE_type");
        final Object timeType=params.get("LIKE_timeType");
        final Long[] longTime=this.getTime(timeType);
        final Object types=params.get("LIKE_types");
        final String name=params.get("LIKE_name")!=null&&!"".equals(params.get("LIKE_name").toString())?"%"+params.get("LIKE_name").toString()+"%":"%%";
        final String title=params.get("LIKE_title")!=null&&!"".equals(params.get("LIKE_title").toString())?"%"+params.get("LIKE_title").toString()+"%":"%%";
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),new Sort(Sort.Direction.DESC,"lendDate"));
        Specification<KnLibraryBorrow> spec=new Specification<KnLibraryBorrow>(){
            @Override
            public Predicate toPredicate(Root<KnLibraryBorrow> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                List<Predicate> predicates=Lists.newArrayList();
                if(!Strings.isNullOrEmpty(code)){
                    predicates.add(cb.like(root.<KnLibraryBookUnit>get("klbu").<String>get("code"),code));
                }
                if(!Strings.isNullOrEmpty(name)){
                    predicates.add(cb.like(root.<String>get("name"),name));
                }
                if(timeType!=null&&!"all".equals(timeType.toString())){
                    predicates.add(cb.ge(root.<Long>get("createTime"),longTime[0]));
                }
                if(!Strings.isNullOrEmpty(title)){
                    predicates.add(cb.like(root.<KnLibraryBookUnit>get("klbu").<KnLibraryBook>get("kbl").<String>get("title"),title));
                }
                if(type!=null&&!"".equals(type.toString())){
                    KnLibraryBorrow.BorrowType borrowType=(type!=null&&!"".equals(type.toString()))?KnLibraryBorrow.BorrowType.valueOf(type.toString()):null;
                    predicates.add(cb.equal(root.<String>get("borrowType"),borrowType));
                }else{
                    if(types!=null&&!"".equals(types.toString())){
                        String[] btypes;
                        if(types instanceof java.lang.String){
                            btypes=((String)types).split(",");
                        }else{
                            btypes=(String[])types;
                        }
                        List<KnLibraryBorrow.BorrowType> types=getType(btypes);
                        predicates.add(cb.and(root.<String>get("borrowType").in(types)));
                    }
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        Page<KnLibraryBorrow> page=klbbd.findAll(spec,pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    /**
     * 转换类型
     *
     * @param types
     *
     * @return
     */
    private List<KnLibraryBorrow.BorrowType> getType(String[] types){
        List<KnLibraryBorrow.BorrowType> BookTypes=Lists.newArrayList();
        for(String type : types){
            BookTypes.add(KnLibraryBorrow.BorrowType.valueOf(type));
        }
        return BookTypes;
    }
    /**
     * 查询分页信息
     *
     * @param params
     * @param dt
     *
     * @return
     */
    public DataTable<KnLibraryBookUnit> listKnLibraryBookUnit(Map<String,Object> params,DataTable<KnLibraryBookUnit> dt){
        final Object type=params.get("LIKE_type");
        final String code=params.get("LIKE_code")!=null&&!"".equals(params.get("LIKE_code").toString())?"%"+params.get("LIKE_code").toString()+"%":"%%";
        final String author=params.get("LIKE_author")!=null&&!"".equals(params.get("LIKE_author").toString())?"%"+params.get("LIKE_author").toString()+"%":"%%";
        final String title=params.get("LIKE_title")!=null&&!"".equals(params.get("LIKE_title").toString())?"%"+params.get("LIKE_title").toString()+"%":"%%";
        final Object types=params.get("LIKE_types");
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),new Sort(Sort.Direction.DESC,"createTime"));
        Specification<KnLibraryBookUnit> spec=new Specification<KnLibraryBookUnit>(){
            @Override
            public Predicate toPredicate(Root<KnLibraryBookUnit> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                List<Predicate> predicates=Lists.newArrayList();
                if(type!=null&&!"".equals(type.toString())){
                    KnLibraryBookUnit.BookType BookType=(!"".equals(type.toString()))?KnLibraryBookUnit.BookType.valueOf(type.toString()):null;
                    predicates.add(cb.equal(root.<String>get("bookType"),BookType));
                }
                if(!Strings.isNullOrEmpty(code)){
                    predicates.add(cb.like(root.<String>get("code"),code));
                }
                if(!Strings.isNullOrEmpty(author)){
                    predicates.add(cb.like(root.<KnLibraryBookUnit>get("kbl").<String>get("author"),author));
                }
                if(!Strings.isNullOrEmpty(title)){
                    predicates.add(cb.like(root.<KnLibraryBookUnit>get("kbl").<String>get("title"),title));
                }
                //                if(types!=null&&!"".equals(types.toString())){
                //                    String[] btypes;
                //                    if(types instanceof java.lang.String){
                //                        btypes=new String[1];
                //                        btypes[0]=(types).toString();
                //                    }else{
                //                        btypes=(String[])types;
                //                    }
                //                    List<KnLibraryBookUnit.BookType> types=getType(btypes);
                //                    predicates.add(cb.and(root.<String>get("BookType").in(types)));
                //                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        Page<KnLibraryBookUnit> page=klbud.findAll(spec,pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    /**
     * 获取内部编号
     *
     * @return
     */
    public String bookSysNumber(){
        //先获取表中编号最大的编号
        List<KnLibraryBookUnit> list=klbud.queryKnLibraryBookUnit();
        if(list==null||list.size()==0){
            return "0001";
        }
        KnLibraryBookUnit unit=list.get(0);
        String code=unit.getCode();
        int result=Integer.parseInt(code)+1;
        String strNo=result+"";
        if(strNo.length()==1){
            strNo="000"+strNo;
        }else if(strNo.length()==2){
            strNo="00"+strNo;
        }else if(strNo.length()==3){
            strNo="0"+strNo;
        }
        return strNo;
    }
    /**
     * 获取时间段
     *
     * @param otype
     *
     * @return
     */
    public Long[] getTime(Object otype){
        Long[] time=new Long[2];
        if(otype!=null){
            String type=otype.toString();
            Date now=new Date();
            time[1]=now.getTime();
            switch(type){
            case "oneWeek":
                time[0]=DateUtils.addDays(now,-7).getTime();
                break;
            case "oneMoth":
                time[0]=DateUtils.addDays(now,-30).getTime();
                break;
            case "threeMoth":
                time[0]=DateUtils.addDays(now,-90).getTime();
                break;
            default:
                time[0]=now.getTime();
            }
        }
        return time;
    }
    public Map ReadBookInfoFormD(String isbn){
        return restTemplate.getForObject("http://api.douban.com/v2/book/isbn/"+isbn,Map.class);
    }
    //=================定时任务=============//
    @PostConstruct
    public void start(){
        threadPoolTaskScheduler=new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setThreadNamePrefix("SpringCronJob");
        threadPoolTaskScheduler.initialize();
        threadPoolTaskScheduler.schedule(this,new CronTrigger(cronExpression));
    }
    @PreDestroy
    public void stop(){
        ScheduledExecutorService scheduledExecutorService=threadPoolTaskScheduler.getScheduledExecutor();
        Threads.normalShutdown(scheduledExecutorService,shutdownTimeout,TimeUnit.SECONDS);
    }
    /** 定时打印当前用户数到日志. */
    @Override
    public void run(){
        //找出规则配置没有领书的时间限制
        KnLibraryRule rule=this.findKnLibraryRule();
        int days=0;
        if(rule!=null){
            days=rule.getOverdue();
        }
        //查看规定天数以前的没有领书的信息
        Date now=new Date();
        Date before=DateUtils.addDays(now,-days);
        List<KnLibraryBorrow> list=klbbd.queryBorrowsNoGet(before.getTime());
        //修改状态
        updateKnLibraryBookUnit(list);
        //查询逾期书籍信息
        List<KnLibraryBorrow> noReturns=klbbd.queryBorrowsNoReturn(System.currentTimeMillis());
        //修改状态
        updateKnLibraryBookUnitNoRet(noReturns);
        //提醒用户归还书籍
        try{
            sendLibraryMessage();
        }catch(Exception e){
            logger.error(e.getMessage());
        }
        //        new Thread(new Runnable(){
        //            @Override public void run(){
        //                sendLibraryMessage();
        //            }
        //        }).start();
    }
    public void setCronExpression(String cronExpression){
        this.cronExpression=cronExpression;
    }
    /** 设置normalShutdown的等待时间,单位秒. */
    public void setShutdownTimeout(int shutdownTimeout){
        this.shutdownTimeout=shutdownTimeout;
    }
    //=================定时任务=============//
    /**
     * 获取
     *
     * @return
     */
    private List<KnLibraryBorrow> findNoReturnBefore3(){
        Date now=new Date();
        //查找规则
        KnLibraryRule rule=this.findKnLibraryRule();
        int days=0;
        if(rule!=null){
            days=rule.getExpiration();
            if(days<=0){
                return Lists.newArrayList();
            }
            days++;
        }else{
            return Lists.newArrayList();
        }
        Date afteDate=DateUtils.addDays(now,days);
        return klbbd.queryBorrowsNoReturn(afteDate.getTime());
    }
    /**
     * 推送还书提醒信息
     */
    private void sendLibraryMessage(){
        List<KnLibraryBorrow> borrows=findNoReturnBefore3();
        if(!Utils.isEmpityCollection(borrows)){
            for(KnLibraryBorrow borrow : borrows){
                String message="";
                //计算区间天数
                int i=Days.daysBetween(DateTime.now().withTimeAtStartOfDay(),new DateTime(borrow.getShouldRestoreDate())).getDays();
                if(i>0){
                    message="还有"+i+"天到期";
                }else if(i==0){
                    message="今天已到期";
                }else if(i<0){
                    message="已超期"+(-i)+"天";
                }
                String content="华大图书馆,你借阅的图书《"+borrow.getKlbu().getKbl().getTitle()+"》"+message+",请及时归还或申请续借。";
                List<Long> userid=Lists.newArrayList();
                userid.add(borrow.getUserId());
                ms.SendMess(userid,Setting.MessageType.intermes.toString(),"华大基因",content,content,100,"","100@");
            }
        }
    }
    @Transactional(readOnly=false)
    private void updateKnLibraryBookUnit(List<KnLibraryBorrow> list){
        //修改状态
        if(list!=null&&list.size()>0){
            for(KnLibraryBorrow borrow : list){
                KnLibraryBookUnit klbu=borrow.getKlbu();
                klbu.setBookType(KnLibraryBookUnit.BookType.RESTORE);
                klbud.save(klbu);
                borrow.setKlbu(klbu);
                borrow.setTimeoutDate(new Date().getTime());
                borrow.setBorrowType(KnLibraryBorrow.BorrowType.TIMEOUT);
                klbbd.save(borrow);
            }
        }
    }
    @Transactional(readOnly=false)
    private void updateKnLibraryBookUnitNoRet(List<KnLibraryBorrow> list){
        //修改状态
        if(list!=null&&list.size()>0){
            for(KnLibraryBorrow borrow : list){
                borrow.setTimeoutDate(new Date().getTime());
                borrow.setBorrowType(KnLibraryBorrow.BorrowType.OVERTIME);
                klbbd.save(borrow);
            }
        }
    }
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        this.restTemplate=restTemplate;
    }
    @Autowired
    public void setKlbud(KnLibraryBookUnitDao klbud){
        this.klbud=klbud;
    }
    @Autowired
    public void setKlbd(KnLibraryBookDao klbd){
        this.klbd=klbd;
    }
    @Autowired
    public void setKlbbd(KnLibraryBorrowDao klbbd){
        this.klbbd=klbbd;
    }
    @Autowired
    public void setKlrd(KnLibraryRuleDao klrd){
        this.klrd=klrd;
    }
    @Autowired
    public void setKed(KnEmployeeDao ked){
        this.ked=ked;
    }
    @Autowired
    public void setKud(KnUserDao kud){
        this.kud=kud;
    }
    @Autowired
    public void setMs(MessageService ms){
        this.ms=ms;
    }
}