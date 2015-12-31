package com.kingnode.health.service;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.common.collect.Lists;
import com.kingnode.diva.utils.Threads;
import com.kingnode.health.dao.EcmHealthDao;
import com.kingnode.health.dao.EcmHealthNumberDao;
import com.kingnode.health.dao.EcmHealthSetDao;
import com.kingnode.health.dao.EcmHealthVacationDao;
import com.kingnode.health.entity.HealthArrange;
import com.kingnode.health.entity.KnEcmHealth;
import com.kingnode.health.entity.KnEcmHealthNumber;
import com.kingnode.health.entity.KnEcmHealthSet;
import com.kingnode.health.entity.KnEcmHealthVacation;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.dao.system.KnEmployeeDao;
import com.kingnode.xsimple.dao.system.KnOrganizationDao;
import com.kingnode.xsimple.entity.IdEntity;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnEmployeeOrganization;
import com.kingnode.xsimple.service.safety.MessageService;
import com.kingnode.xsimple.util.Users;
import com.kingnode.xsimple.util.Utils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ConstantConditions") @Service @Transactional(readOnly=true)
public class HealthService implements Runnable{
    private static org.slf4j.Logger log=LoggerFactory.getLogger(HealthService.class);
    private final String HEALTH_OVERDUE="23:00";//定时任务中设定逾期的时间

    private EcmHealthDao healthDao;
    private EcmHealthSetDao healthSetDao;
    private EcmHealthVacationDao healthVacationDao;
    private EcmHealthNumberDao healthNumberDao;
    private KnEmployeeDao empDao;
    private KnOrganizationDao orgDao;
    private MessageService ms;
    @Autowired
    public void setHealthDao(EcmHealthDao healthDao){
        this.healthDao=healthDao;
    }
    @Autowired
    public void setHealthSetDao(EcmHealthSetDao healthSetDao){
        this.healthSetDao=healthSetDao;
    }
    @Autowired
    public void setHealthVacationDao(EcmHealthVacationDao healthVacationDao){
        this.healthVacationDao=healthVacationDao;
    }
    @Autowired
    public void setHealthNumberDao(EcmHealthNumberDao healthNumberDao){
        this.healthNumberDao=healthNumberDao;
    }
    @Autowired
    public void setEmpDao(KnEmployeeDao empDao){
        this.empDao=empDao;
    }
    @Autowired
    public void setOrgDao(KnOrganizationDao orgDao){
        this.orgDao=orgDao;
    }
    @Autowired
    public void setMs(MessageService ms){
        this.ms=ms;
    }
    /**
     * 获取预约列表
     *
     * @param dt           初始化表格信息
     * @param searchParams 查询条件
     *
     * @return DataTable<KnEcmHealth>
     */
    public DataTable<KnEcmHealth> PageKnEcmHealth(DataTable<KnEcmHealth> dt,final Map<String,Object> searchParams){
        Specification<KnEcmHealth> spec=new Specification<KnEcmHealth>(){
            @Override public Predicate toPredicate(Root<KnEcmHealth> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(searchParams!=null&&searchParams.size()!=0){
                    DateTimeFormatter format=DateTimeFormat.forPattern("yyyy-MM-dd");
                    //状态
                    if(searchParams.containsKey("EQ_status")&&!StringUtils.isBlank(searchParams.get("EQ_status").toString())){
                        String key=searchParams.get("EQ_status").toString().trim();
                        List<KnEcmHealth.HealthStatus> list=new ArrayList<>();
                        switch(key){
                        case "appointment"://预约列表
                            list.add(KnEcmHealth.HealthStatus.PROPOSER);
                            break;
                        case "waiting"://预约列表
                            list.add(KnEcmHealth.HealthStatus.WAITING);
                            break;
                        default:
                            list.add(KnEcmHealth.HealthStatus.DIAGNOSE);
                            list.add(KnEcmHealth.HealthStatus.ABANDON);
                            list.add(KnEcmHealth.HealthStatus.OVERDUE);
                            break;
                        }
                        expressions.add(cb.and(root.<KnEcmHealth.HealthStatus>get("status").in(list)));
                    }
                    //申请人
                    if(searchParams.containsKey("LIKE_name")&&!StringUtils.isBlank(searchParams.get("LIKE_name").toString())){
                        expressions.add(cb.like(root.<KnEmployee>get("employee").<String>get("userName"),"%"+searchParams.get("LIKE_name").toString().trim()+"%"));
                    }
                    //部门
                    if(searchParams.containsKey("LIKE_department")&&!StringUtils.isBlank(searchParams.get("LIKE_department").toString())){
                        String orgName=searchParams.get("LIKE_department").toString().trim();
                        //根据部门名称找到组织ID
                        List<Long> orgIds=orgDao.findListAllByName(orgName);
                        List<Long> empIds=new ArrayList<>();
                        if(orgIds!=null&&orgIds.size()>0){
                            //根据组织ID找到employeeID
                            empIds=empDao.findUserIdByOrgIds(orgIds);
                        }
                        if(empIds.size()<=0){
                            empIds.add(0l);
                        }
                        expressions.add(cb.and(root.<KnEmployee>get("employee").<Long>get("id").in(empIds)));
                    }
                    //申请时间
                    if(searchParams.containsKey("EQ_proposerDate")&&!StringUtils.isBlank(searchParams.get("EQ_proposerDate").toString())){
                        Long min=DateTime.parse(searchParams.get("EQ_proposerDate").toString().trim(),format).millisOfDay().withMinimumValue().getMillis();
                        Long max=DateTime.parse(searchParams.get("EQ_proposerDate").toString().trim(),format).millisOfDay().withMaximumValue().getMillis();
                        expressions.add(cb.ge(root.<Long>get("proposerDate"),min));
                        expressions.add(cb.le(root.<Long>get("proposerDate"),max));
                    }
                    /*//申请结束时间
                    if(searchParams.containsKey("LE_proposerDate")&&!StringUtils.isBlank(searchParams.get("LE_proposerDate").toString())){
                        Long time=DateTime.parse(searchParams.get("LE_proposerDate").toString().trim(),format).millisOfDay().withMaximumValue().getMillis();
                        expressions.add(cb.le(root.<Long>get("proposerDate"),time));
                    }*/
                    //就诊时间
                    if(searchParams.containsKey("EQ_diagnoseDate")&&!StringUtils.isBlank(searchParams.get("EQ_diagnoseDate").toString())){
                        Long min=DateTime.parse(searchParams.get("EQ_diagnoseDate").toString().trim(),format).millisOfDay().withMinimumValue().getMillis();
                        Long max=DateTime.parse(searchParams.get("EQ_diagnoseDate").toString().trim(),format).millisOfDay().withMaximumValue().getMillis();
                        expressions.add(cb.ge(root.<Long>get("diagnoseDate"),min));
                        expressions.add(cb.le(root.<Long>get("diagnoseDate"),max));
                    }
                    /*//就诊开始时间
                    if(searchParams.containsKey("LE_diagnoseDate")&&!StringUtils.isBlank(searchParams.get("LE_diagnoseDate").toString())){
                        Long time=DateTime.parse(searchParams.get("LE_diagnoseDate").toString().trim(),format).millisOfDay().withMaximumValue().getMillis();
                        expressions.add(cb.le(root.<Long>get("diagnoseDate"),time));
                    }*/
                    //是否复诊记录
                    if(searchParams.containsKey("EQ_consultationFlag")&&!StringUtils.isBlank(searchParams.get("EQ_consultationFlag").toString())){
                        expressions.add(cb.equal(root.<Integer>get("consultationFlag"),Integer.valueOf(searchParams.get("EQ_consultationFlag").toString().trim())));
                    }
                    //复诊时间
                    if(searchParams.containsKey("EQ_consultationDate")&&!StringUtils.isBlank(searchParams.get("EQ_consultationDate").toString())){
                        Long min=DateTime.parse(searchParams.get("EQ_consultationDate").toString().trim(),format).millisOfDay().withMinimumValue().getMillis();
                        Long max=DateTime.parse(searchParams.get("EQ_consultationDate").toString().trim(),format).millisOfDay().withMaximumValue().getMillis();
                        expressions.add(cb.ge(root.<Long>get("waitingDate"),min));
                        expressions.add(cb.le(root.<Long>get("waitingDate"),max));
                    }
                }
                return predicate;
            }
        };
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        int index=Integer.parseInt(dt.getiSortCol_0());
        String[] column=new String[]{"waitingNumber","employee.userName","employee.userId","employee.org","phone","proposerDate","waitingDate","diagnoseDate","description","diagnoseResult","diagnoseCost","status"};
        if(searchParams.containsKey("EQ_status")&&!StringUtils.isBlank(searchParams.get("EQ_status").toString())){
            String key=searchParams.get("EQ_status").toString().trim();
            if("appointment".equals(key)){
                column=new String[]{"id","employee.userName","employee.userId","employee.org","phone","proposerDate","description"};
            }
            if("waiting".equals(key)){
                if(searchParams.containsKey("WIN_Flag")&&!StringUtils.isBlank(searchParams.get("WIN_Flag").toString())&&"Y".equals(searchParams.get("WIN_Flag").toString().trim())){
                    column=new String[]{"waitingNumber","employee.userName","employee.org","phone","waitingDate"};
                }else{
                    column=new String[]{"waitingNumber","employee.userName","employee.userId","employee.org","phone","proposerDate","waitingDate","description"};
                }
            }
        }
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),new Sort(d,column[index]));
        Page<KnEcmHealth> page=healthDao.findAll(spec,pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        //附加部门
        List<KnEcmHealth> list=page.getContent();
        if(list!=null&&list.size()>0){
            for(KnEcmHealth health:list){
                SetOrgName(health);
            }
        }
        return dt;
    }
    /**
     * 设置部门名称
     * @param health 预约对象
     */
    public void SetOrgName(KnEcmHealth health){
        List<String> org=Lists.newArrayList();
        for(KnEmployeeOrganization keo : health.getEmployee().getOrg()){
            org.add(keo.getId().getOrg().getName());
        }
        health.setOrgName(StringUtils.join(org,","));
    }
    /**
     * 保存预约
     *
     * @param health 预约对象
     */
    @Transactional(readOnly=false)
    public void SaveKnEcmHealth(KnEcmHealth health){
        healthDao.save(health);
    }
    /**
     * 批量保存预约对象
     * @param healthList 预约对象集合
     */
    @Transactional(readOnly=false)
    public void SaveKnEcmHealth(List<KnEcmHealth> healthList){
        healthDao.save(healthList);
    }
    /**
     * 批量保存预约号码对象
     * @param numberList 预约对象号码集合
     */
    @Transactional(readOnly=false)
    public void SaveKnEcmHealthNumber(List<KnEcmHealthNumber> numberList){
        healthNumberDao.save(numberList);
    }
    /**
     * 保存排号对象
     *
     * @param healthNumber 排号对象
     */
    @Transactional(readOnly=false)
    public void SaveKnEcmHealthNumber(KnEcmHealthNumber healthNumber){
        healthNumberDao.save(healthNumber);
    }
    /**
     * 保存设置
     *
     * @param healthSet 设置对象
     */
    @Transactional(readOnly=false)
    public void SaveKnEcmHealthSet(KnEcmHealthSet healthSet){
        healthSetDao.save(healthSet);
    }
    /**
     * 根据id获取预约对象
     * @param id 预约对象ID
     * @return KnEcmHealth
     */
    public KnEcmHealth ReadKnEcmHealth(Long id){
        return healthDao.findOne(id);
    }
    /**
     * 获取设置对象
     * @return KnEcmHealthSet
     */
    public KnEcmHealthSet ReadKnEcmHealthSet(){
        List<KnEcmHealthSet> list=healthSetDao.ListKnEcmHealthSet();
        if(list!=null&&list.size()>0){
            return list.get(0);
        }
        return null;
    }
    /**
     * 获取当前用户的预约列表
     * @param pageNo 页码
     * @param pageSize 条数
     * @return List<KnEcmHealth>
     */
    public List<KnEcmHealth> ListKnEcmHealth(Integer pageNo,Integer pageSize){
        PageRequest pageRequest=new PageRequest(pageNo,pageSize,new Sort(Sort.Direction.DESC,"proposerDate"));
        Page<KnEcmHealth> page=healthDao.PageKnEcmHealth(Users.id(),pageRequest);
        if(page!=null&&!Utils.isEmpityCollection(page.getContent())){
            return page.getContent();
        }
        return new ArrayList<KnEcmHealth>();
    }
    /**
     * 根据ID列表获取预约列表
     * @param ids 预约ID列表
     * @return List<KnEcmHealth>
     */
    public List<KnEcmHealth> ListKnEcmHealth(List<Long> ids){
        return healthDao.ListHealthByIds(ids);
    }
    /**
     * 根据时间获取预约列表
     * @param start 开始时间
     * @param end 结束时间
     * @return List<KnEcmHealth>
     */
    public List<KnEcmHealth> ListKnEcmHealthByDate(Long start,Long end){
        return healthDao.ListHealthByDate(start,end);
    }
    /**
     * 获取当前员工对象
     * @return KnEmployee
     */
    public KnEmployee ReadKnEmployee(){
        return empDao.findOne(Users.id());
    }
    /**
     * 初始化排号对象
     * @return HealthArrange
     */
    public HealthArrange InitArrange(KnEcmHealthSet set,long last){
        //获取请假列表
        List<String> restDays=Lists.newArrayList();
        List<KnEcmHealthVacation> vacations=healthVacationDao.listKnEcmHealthVacation(new DateTime(last).plusDays(-1).getMillis());
        if(vacations!=null&&vacations.size()>0){
            for(KnEcmHealthVacation vacation : vacations){
                restDays.add(vacation.getVacationStr());
            }
        }
        //获取大于常规排号时间的排号集合
        Map<String,List<Long>> dayOfArrangeMap=new HashMap<>();
        List<KnEcmHealthNumber> dayOfArrangeList=healthNumberDao.DayOfArrangeList(new DateTime(last).getMillis());
        if(dayOfArrangeList!=null&&dayOfArrangeList.size()>0){
            for(KnEcmHealthNumber number:dayOfArrangeList){
                String key=number.getDay();
                List<Long> arrangeList=dayOfArrangeMap.get(key);
                if(arrangeList==null){
                    arrangeList=new ArrayList<>();
                }
                arrangeList.add(number.getStart());
                dayOfArrangeMap.put(key,arrangeList);
            }
        }
        //每天最大排序号集合
        Map<String,Integer> maxOrderMap=new HashMap<>();
        List<String> dayList=healthNumberDao.QueryDayList(new DateTime(last).millisOfDay().withMinimumValue().getMillis());
        if(dayList!=null&&dayList.size()>0){
            for(String day:dayList){
                Integer max=healthNumberDao.MaxNumberWithDay(day);
                maxOrderMap.put(day,max);
            }
        }
        return new HealthArrange(set,restDays,dayOfArrangeMap,maxOrderMap,last);
    }
    /**
     * 获取休假时间列表
     * @param dt 初始化信息
     * @param searchParams 搜索条件
     * @return DataTable<KnEcmHealthVacation>
     */
    public DataTable<KnEcmHealthVacation> PageKnEcmHealthVacation(DataTable<KnEcmHealthVacation> dt,final Map<String,Object> searchParams){
        Specification<KnEcmHealthVacation> spec=new Specification<KnEcmHealthVacation>(){
            @Override public Predicate toPredicate(Root<KnEcmHealthVacation> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(searchParams!=null&&searchParams.size()!=0){
                    //有效性
                    if(searchParams.containsKey("EQ_active")&&!StringUtils.isBlank(searchParams.get("EQ_active").toString())){
                        String value=searchParams.get("EQ_active").toString().trim();
                        expressions.add(cb.equal(root.<IdEntity.ActiveType>get("active"),IdEntity.ActiveType.valueOf(value)));
                    }
                    //休假时间
                    if(searchParams.containsKey("EQ_vacation")&&!StringUtils.isBlank(searchParams.get("EQ_vacation").toString())){
                        DateTimeFormatter format=DateTimeFormat.forPattern("yyyy-MM-dd");
                        expressions.add(cb.equal(root.<Long>get("vacation"),DateTime.parse(searchParams.get("EQ_vacation").toString().trim(),format).getMillis()));
                    }
                }
                return predicate;
            }
        };
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        int index=Integer.parseInt(dt.getiSortCol_0());
        String[] column=new String[]{"id","vacation"};
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),new Sort(d,column[index]));
        Page<KnEcmHealthVacation> page=healthVacationDao.findAll(spec,pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    /**
     * 获取等于指定日期的请假对象
     * @param date 指定日期
     * @return KnEcmHealthVacation
     */
    public KnEcmHealthVacation ReadKnEcmHealthVacationEqDate(Long date){
        List<KnEcmHealthVacation> list=healthVacationDao.listKnEcmHealthVacationEqDate(date);
        if(list!=null&&list.size()>0){
            return list.get(0);
        }
        return null;
    }
    /**
     * 保存请假对象
     * @param healthVacation 请假对象
     */
    @Transactional(readOnly=false)
    public void SaveKnEcmHealthVacation(KnEcmHealthVacation healthVacation){
        healthVacationDao.save(healthVacation);
    }
    /**
     * 根据ID删除请假对象
     * @param id 请假对象ID
     */
    @Transactional(readOnly=false)
    public void DeleteKnEcmHealthVacation(Long id){
        healthVacationDao.delete(id);
    }
    /**
     * 根据ID获取请假对象
     * @param id 请假对象ID
     * @return KnEcmHealthVacation
     */
    public KnEcmHealthVacation ReadKnEcmHealthVacation(Long id){
        return healthVacationDao.findOne(id);
    }

    //-----------------定时推送提醒接口------------------
    private String cronExpression="0 0/1 * * * ?";
    private int shutdownTimeout=20;
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    //=================定时任务=============//
    @PostConstruct
    public void start(){
        threadPoolTaskScheduler=new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setThreadNamePrefix("MeetingCronJob");
        threadPoolTaskScheduler.initialize();
        threadPoolTaskScheduler.schedule(this,new CronTrigger(cronExpression));
    }
    @PreDestroy
    public void stop(){
        ScheduledExecutorService scheduledExecutorService=threadPoolTaskScheduler.getScheduledExecutor();
        Threads.normalShutdown(scheduledExecutorService,shutdownTimeout,TimeUnit.SECONDS);
    }

    @Override public void run(){
        //final KnMeetingRule rule=ReadKnMeetingRule();//获取会议规则
        log.info("每分钟执行一次");
        try{
            DateTime currTime=DateTime.now();
            //晚上11点，开始判断预约是否逾期
            if(currTime.toString("yyyy-MM-dd HH:mm").substring(11).equals(HEALTH_OVERDUE)){
                log.info("预约信息逾期操作--开始");
                healthOverdue();
                log.info("预约信息逾期操作--结束");
            }

            //推送就诊消息
            KnEcmHealthSet set=ReadKnEcmHealthSet();
            if(set==null){
                return;
            }
            //获取消息推送的开始时间和结束时间。开始时间=上班时间-设置提前通知时间-0.5小时；结束时间：下班时间
            DateTimeFormatter format=DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
            String startDate=currTime.toString("yyyy-MM-dd")+" "+set.getAmStartDate().trim();
            long workStart=DateTime.parse(startDate,format).plusMinutes(-(set.getBeforeRemind())).plusMinutes(-30).getMillis();
            String end=StringUtils.isBlank(set.getPmEndDate())?set.getAmEndDate().trim():set.getPmEndDate().trim();
            String endDate=currTime.toString("yyyy-MM-dd")+" "+end;
            long workEnd=DateTime.parse(endDate,format).getMillis();
            //就诊消息推送
            if(currTime.getMillis()>workStart&&currTime.getMillis()<workEnd){
                log.info("推送就诊提醒--开始");
                pushDiagnoseMessage(currTime,set.getBeforeRemind());
                log.info("推送就诊提醒--结束");
            }
        }catch(ParseException e){
            e.printStackTrace();
        }
    }
    /**
     * 推送就诊信息
     * @param currTime 当前时间
     * @param beforeRemind 提前多少分钟提醒
     * @throws ParseException
     */
    private void pushDiagnoseMessage(DateTime currTime,Integer beforeRemind) throws ParseException{
        long start=currTime.plusMinutes(beforeRemind).secondOfMinute().withMinimumValue().millisOfSecond().withMinimumValue().getMillis();
        long end=currTime.plusMinutes(beforeRemind).secondOfMinute().withMaximumValue().millisOfSecond().withMaximumValue().getMillis();
        List<KnEcmHealth> healthList=ListKnEcmHealthByDate(start,end);
        if(healthList!=null&&healthList.size()>0){
            for(KnEcmHealth health:healthList){
                List<Long> ids=Lists.newArrayList();
                ids.add(health.getEmployee().getId());
                String msg="您的就诊时间为"+(new DateTime(health.getWaitingDate()).toString("MM.dd HH:mm"))+"，目前距就诊只有"+beforeRemind+"分钟了！请准时就医。";
                ms.SendMess(ids,Setting.MessageType.intermes.toString(),"华大基因",msg,msg,112,"","112@"+health.getId());
                log.info("就诊消息推送,推送对象:"+health.getEmployee().getUserName());
            }
        }
    }
    /**
     * 推送候诊信息
     * @param healthList 就诊列表
     * @param flag 消息内容标记  1.表示预约排号提醒  2.表示复诊提醒  3.表示变更就诊时间提醒
    * @throws ParseException
     */
    public void pushWaitingMessage(List<KnEcmHealth> healthList,int flag) throws ParseException{
        if(healthList!=null&&healthList.size()>0){
            for(KnEcmHealth health:healthList){
                List<Long> ids=Lists.newArrayList();
                ids.add(health.getEmployee().getId());
                String msg="";
                switch(flag){
                case 1:
                    msg="您的诊室预约，就诊时间为"+(new DateTime(health.getWaitingDate()).toString("MM.dd HH:mm"))+"，请准时就医。";
                    break;
                case 2:
                    msg="您的复诊安排，复诊时间为"+(new DateTime(health.getWaitingDate()).toString("MM.dd HH:mm"))+"，请准时就医。";
                    break;
                case 3:
                    msg="您的就诊已变动，新的就诊时间为"+(new DateTime(health.getWaitingDate()).toString("MM.dd HH:mm"))+"，请准时就医。";
                    break;
                }
                ms.SendMess(ids,Setting.MessageType.intermes.toString(),"华大基因",msg,msg,111,"","111@"+health.getId());
                log.info("消息内容:"+msg);
                log.info("候诊消息推送,推送对象:"+health.getEmployee().getUserName());
            }
        }
    }
    /**
     * 预约或候诊逾期操作
     */
    private void healthOverdue() throws ParseException{
        List<KnEcmHealth> healthList=ListKnEcmHealthByDate(0l,DateTime.now().getMillis());
        if(healthList!=null&&healthList.size()>0){
            //将逾期对应的排号对象设置为无效
            List<KnEcmHealthNumber> numberList=Lists.newArrayList();
            for(KnEcmHealth health : healthList){
                health.setStatus(KnEcmHealth.HealthStatus.OVERDUE);
                KnEcmHealthNumber number=ReadHealthNumberByHealthId(health.getId());
                number.setActive(IdEntity.ActiveType.DISABLE);
                numberList.add(number);
            }
            SaveKnEcmHealth(healthList);
            SaveKnEcmHealthNumber(numberList);
        }
        log.info("已记录逾期信息"+(healthList==null?0:healthList.size())+"条");
    }
    /**
     * 获取常规排号下的最后一个排号对象
     * @return KnEcmHealthNumber
     */
    public KnEcmHealthNumber LastHealthNumber(){
        PageRequest pageRequest=new PageRequest(0,1,new Sort(Sort.Direction.DESC,"start"));
        Page<KnEcmHealthNumber> page=healthNumberDao.PageKnEcmHealthNumber(pageRequest);
        if(page!=null&&!Utils.isEmpityCollection(page.getContent())){
            return page.getContent().get(0);
        }
        return null;
    }
    /**
     * 获取指定某天的最大排号号码
     * @param day 被指定的某天 格式：yyyy-MM-dd
     * @return Integer
     */
    public Integer MaxNumberWithDay(String day){
        return healthNumberDao.MaxNumberWithDay(day);
    }
    /**
     * 根据预约对象的ID获取其对应的排号对象
     * @param id 预约对象ID
     * @return KnEcmHealthNumber
     */
    public KnEcmHealthNumber ReadHealthNumberByHealthId(Long id){
        return healthNumberDao.findOneByHealthId(id);
    }
}