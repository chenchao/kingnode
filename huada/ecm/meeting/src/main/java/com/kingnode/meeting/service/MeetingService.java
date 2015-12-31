package com.kingnode.meeting.service;
import java.text.ParseException;
import java.util.Date;
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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kingnode.diva.persistence.DynamicSpecifications;
import com.kingnode.diva.persistence.SearchFilter;
import com.kingnode.diva.utils.Collections3;
import com.kingnode.diva.utils.Threads;
import com.kingnode.meeting.dao.KnMeetingAttendeeDao;
import com.kingnode.meeting.dao.KnMeetingBlacklistDao;
import com.kingnode.meeting.dao.KnMeetingDao;
import com.kingnode.meeting.dao.KnMeetingEquipmentDao;
import com.kingnode.meeting.dao.KnMeetingFileDao;
import com.kingnode.meeting.dao.KnMeetingRoomDao;
import com.kingnode.meeting.dao.KnMeetingRuleDao;
import com.kingnode.meeting.dao.KnMeetingSignInDao;
import com.kingnode.meeting.dao.KnMeetingSummaryDao;
import com.kingnode.meeting.entity.KnMeeting;
import com.kingnode.meeting.entity.KnMeetingAttendee;
import com.kingnode.meeting.entity.KnMeetingBlacklist;
import com.kingnode.meeting.entity.KnMeetingEquipment;
import com.kingnode.meeting.entity.KnMeetingFile;
import com.kingnode.meeting.entity.KnMeetingRoom;
import com.kingnode.meeting.entity.KnMeetingRule;
import com.kingnode.meeting.entity.KnMeetingSignIn;
import com.kingnode.meeting.entity.KnMeetingSummary;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.dao.meeting.KnMeetingInfoDao;
import com.kingnode.xsimple.dao.meeting.KnRegisterInfoDao;
import com.kingnode.xsimple.entity.meeting.KnMeetingInfo;
import com.kingnode.xsimple.entity.meeting.KnMeetingRegisterInfo;
import com.kingnode.xsimple.entity.meeting.KnRegisterInfo;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.service.safety.MessageService;
import com.kingnode.xsimple.service.system.OrganizationService;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class MeetingService implements Runnable{
    private static Logger logger=LoggerFactory.getLogger(MeetingService.class);
    private KnMeetingDao kmd;
    private KnMeetingRoomDao kmrd;
    private KnMeetingFileDao kmfd;
    private KnMeetingSignInDao kmsid;
    private KnMeetingSummaryDao kmsd;
    private KnMeetingInfoDao mid;
    private KnRegisterInfoDao rid;
    private KnMeetingBlacklistDao mbd;
    private KnMeetingRuleDao ruleDao;
    private KnMeetingAttendeeDao attendeeDao;
    private KnMeetingEquipmentDao equipmentDao;
    private OrganizationService os;
    private MessageService ms;
    @Autowired
    public void setOs(OrganizationService os){
        this.os=os;
    }
    @Autowired
    public void setMs(MessageService ms){
        this.ms=ms;
    }
    @Autowired
    public void setEquipmentDao(KnMeetingEquipmentDao equipmentDao){
        this.equipmentDao=equipmentDao;
    }
    @Autowired
    public void setAttendeeDao(KnMeetingAttendeeDao attendeeDao){
        this.attendeeDao=attendeeDao;
    }
    @Autowired
    public void setKmd(KnMeetingDao kmd){
        this.kmd=kmd;
    }
    @Autowired
    public void setKmrd(KnMeetingRoomDao kmrd){
        this.kmrd=kmrd;
    }
    @Autowired
    public void setKmfd(KnMeetingFileDao kmfd){
        this.kmfd=kmfd;
    }
    @Autowired
    public void setKmsid(KnMeetingSignInDao kmsid){
        this.kmsid=kmsid;
    }
    @Autowired
    public void setKmsd(KnMeetingSummaryDao kmsd){
        this.kmsd=kmsd;
    }
    @Autowired
    public void setMid(KnMeetingInfoDao mid){
        this.mid=mid;
    }
    @Autowired
    public void setRid(KnRegisterInfoDao rid){
        this.rid=rid;
    }
    @Autowired
    public void setMbd(KnMeetingBlacklistDao mbd){
        this.mbd=mbd;
    }
    @Autowired
    public void setRuleDao(KnMeetingRuleDao ruleDao){
        this.ruleDao=ruleDao;
    }
    @Transactional(readOnly=false)
    public KnMeetingEquipment SaveKnMeetingEquipment(KnMeetingEquipment e){
        return equipmentDao.save(e);
    }
    public List<KnMeetingEquipment> ReadKnMeetingEquipmentAll(){
        return equipmentDao.findByStatus(KnMeetingEquipment.EquipmentStatus.AVAILABLE);
    }
    public List<KnMeeting> PageKnMeeting(Long userId,String date,Integer pageNo,Integer pageSize){
        Long beginDate=new DateTime(date).getMillis();
        Long endDate=DateTime.parse(date+" 23:59:59",DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).getMillis();
        return kmd.findMeeting(userId,beginDate,endDate,new PageRequest(pageNo,pageSize)).getContent();
    }
    @Transactional(readOnly=false)
    public KnMeetingAttendee SaveKnMeetingAttendee(KnMeetingAttendee attendee){
        return attendeeDao.save(attendee);
    }
    public Long isExistMeeting(Long beginDate,Long endTime,Long id){
        return kmd.findExistMeeting(beginDate,endTime,id);
    }
    public KnMeeting ReadKnMeeting(Long id){
        return kmd.findOne(id);
    }
    public List<KnMeetingRoom> ListKnMeetingRoom(Integer pageNo,Integer pageSize){
        return kmrd.findByStatus(KnMeetingRoom.RoomStatus.AVAILABLE,new PageRequest(pageNo,pageSize));
    }
    public KnMeetingRoom ReadKnMeetingRoom(Long id){
        return kmrd.findOne(id);
    }
    public KnMeetingBlacklist ReadKnMeetingBlacklist(Long id){
        return mbd.findOne(id);
    }
    public KnMeetingBlacklist ReadKnMeetingBlacklistByUserId(Long userId){
        return mbd.findByUserId(userId);
    }
    public KnMeetingRule ReadKnMeetingRule(){
        List<KnMeetingRule> list=(List<KnMeetingRule>)ruleDao.findAll();
        if(list!=null&&list.size()>0){
            return list.get(0);
        }
        return new KnMeetingRule();
    }
    private Integer ReadKnMeetingCount(Long id){
        return kmrd.findKnMeetingCount(new Date().getTime(),id);
    }
    public DataTable<KnMeetingRoom> PageKnMeetingRoomByNameOrAddr(final String name,final String status,DataTable<KnMeetingRoom> dt){
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength());
        Page<KnMeetingRoom> page=kmrd.findAll(new Specification<KnMeetingRoom>(){
            @Override
            public Predicate toPredicate(Root<KnMeetingRoom> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate p1, p2, p3;
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(!Strings.isNullOrEmpty(name)){
                    p1=cb.like(root.get("name").as(String.class),"%"+name+"%");
                    p2=cb.like(root.get("addr").as(String.class),"%"+name+"%");
                    expressions.add(cb.or(p1,p2));
                }
                if(!Strings.isNullOrEmpty(status)){
                    p3=cb.equal(root.get("status").as(KnMeetingRoom.RoomStatus.class),KnMeetingRoom.RoomStatus.AVAILABLE.toString().equals(status)?KnMeetingRoom.RoomStatus.AVAILABLE:KnMeetingRoom.RoomStatus.DISABLED);
                    expressions.add(p3);
                }
                return predicate;
            }
        },pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        List<KnMeetingRoom> list=Lists.newArrayList();
        for(KnMeetingRoom room : page.getContent()){
            Integer flag=ReadKnMeetingCount(room.getId());
            if(flag>0){
                room.setUseState("1");//使用中
            }else{
                room.setUseState("0");//空闲
            }
            list.add(room);
        }
        dt.setAaData(list);
        return dt;
    }
    public DataTable<KnMeetingEquipment> PageKnMeetingEquipment(String name,DataTable<KnMeetingEquipment> dt){
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength());
        Page<KnMeetingEquipment> page=equipmentDao.findByNameLikeAndStatus(name,KnMeetingEquipment.EquipmentStatus.AVAILABLE,pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    public KnMeetingEquipment ReadKnMeetingEquipment(Long id){
        return equipmentDao.findOne(id);
    }
    /**
     * 获取用户已结束得会议ids集合
     *
     * @param userId
     * @param lockDate
     */
    private Long[] listEndMeeting(Long userId,Long lockDate){
        Long[] ids;
        if(lockDate==null){
            ids=kmd.findEndMeeting(userId,System.currentTimeMillis());
        }else{
            ids=kmd.findEndMeeting(userId,lockDate,System.currentTimeMillis());
        }
        return ids;
    }
    public Integer ReadMeetingRule(Long userId,Long lockDate){
        Long[] ids=listEndMeeting(userId,lockDate);
        Integer sumNum=0;
        Integer num=0;
        if(ids!=null&&ids.length>0){
            sumNum=ids.length;
            num=kmsid.findKnMeetingSignInByParam(userId,ids);
        }
        return sumNum-num;
    }
    public KnMeetingAttendee ReadKnMeetingAttendee(Long attendeeId,Long id){
        return attendeeDao.findByAttendeeIdAndKmId(attendeeId,id);
    }
    @Transactional(readOnly=false)
    public KnMeetingBlacklist SaveKnMeetingBlacklist(KnMeetingBlacklist blacklist){
        return mbd.save(blacklist);
    }
    @Transactional(readOnly=false)
    public KnMeeting SaveKnMeeting(KnMeeting km){
        return kmd.save(km);
    }
    @Transactional(readOnly=false)
    public void SaveKnMeetingFile(KnMeetingFile kmf){
        kmfd.save(kmf);
    }
    @Transactional(readOnly=false)
    public void SaveKnMeetingSummary(KnMeetingSummary kms){
        kmsd.save(kms);
    }
    @Transactional(readOnly=false)
    public void SaveKnMeetingSignIn(KnMeetingSignIn kms){
        kmsid.save(kms);
    }
    @Transactional(readOnly=false)
    public KnMeetingRule SaveKnMeetingRule(KnMeetingRule rule){
        return ruleDao.save(rule);
    }
    @Transactional(readOnly=false)
    public KnMeetingRoom SaveKnMeetingRoom(KnMeetingRoom room){
        return kmrd.save(room);
    }
    public KnMeetingSummary ReadKnMeetingSummary(Long id){
        return kmsd.findOne(id);
    }
    public List<KnMeetingFile> ListKnMeetingFile(Long id){
        return kmd.findOne(id).getKmf();
    }
    public List<KnMeetingSignIn> PageKnMeetingSignIn(Long meetId,Integer pageNo,Integer pageSize){
        return kmsid.findByIdMeetingId(meetId,new PageRequest(pageNo,pageSize)).getContent();
    }
    public List<KnMeetingSignIn> PageKnMeetingSignIn(Long meetId,Long userId,Integer pageNo,Integer pageSize){
        return kmsid.findByIdMeetingIdAndIdUserId(meetId,userId,new PageRequest(pageNo,pageSize)).getContent();
    }
    public boolean isModerator(Long id,Long userId){
        List ls=kmd.findByNameAndIdKnMeeting(id,userId);
        if(ls!=null&&!ls.isEmpty()){
            return true;
        }else{
            return false;
        }
    }
    public DataTable<KnMeetingBlacklist> PageKnMeetingBlacklists(DataTable<KnMeetingBlacklist> dt){
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength());
        Page<KnMeetingBlacklist> page=mbd.findAll(pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    public List<KnMeetingInfo> ListMeetingInfos(){
        return (List<KnMeetingInfo>)mid.findAll();
    }
    public DataTable<KnRegisterInfo> PageRegisterInfos(Map<String,Object> searchParams,DataTable<KnRegisterInfo> dt){
        Sort sort=getSort(dt);
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        Map<String,SearchFilter> filters=SearchFilter.parse(searchParams);
        Specification<KnRegisterInfo> spec=DynamicSpecifications.bySearchFilter(filters.values());
        Page<KnRegisterInfo> page=rid.findAll(spec,pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    public DataTable<KnRegisterInfo> PageAttendees(final String theme,final String name,final String sex,final String phone,DataTable<KnRegisterInfo> dt){
        Pageable pageable=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),getSort(dt));
        Page<KnRegisterInfo> page=rid.findAll(new Specification<KnRegisterInfo>(){
            @Override
            public Predicate toPredicate(Root<KnRegisterInfo> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                Root<KnMeetingRegisterInfo> r=cq.from(KnMeetingRegisterInfo.class);
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                expressions.add(cb.equal(root.<Long>get("id"),r.<Long>get("registerId")));
                expressions.add(cb.equal(r.<String>get("meetingId"),theme));
                if(!Strings.isNullOrEmpty(name)){
                    expressions.add(cb.like(root.<String>get("name"),"%"+name+"%"));
                }
                if(!Strings.isNullOrEmpty(sex)){
                    expressions.add(cb.equal(root.<String>get("sex"),sex));
                }
                if(!Strings.isNullOrEmpty(phone)){
                    expressions.add(cb.like(root.<String>get("phone"),"%"+phone+"%"));
                }
                return predicate;
            }
        },pageable);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    private Sort getSort(DataTable<KnRegisterInfo> dt){
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        String[] column=new String[]{"id","name","sex","phone","email","company","cusmger"};
        return new Sort(d,column[Integer.parseInt(dt.getiSortCol_0())]);
    }
    @Transactional(readOnly=false)
    public KnRegisterInfo SaveRegisterInfo(KnRegisterInfo registerInfo){
        return rid.save(registerInfo);
    }
    @Transactional(readOnly=false)
    public void DeleteRegisterInfo(Long id){
        rid.delete(id);
    }
    public void DeleteAttendeeByMeetingId(Long id){
        attendeeDao.DeleteAttendeeByMeetingId(id);
    }
    public KnRegisterInfo FindRegisterInfoById(Long id){
        return rid.findOne(id);
    }
    public List<KnMeeting> FindByDateKnMeeting(String date,Long id){
        Long beginDate=new DateTime(date).getMillis();
        Long endTime=DateTime.parse(date+" 23:59:59",DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).getMillis();
        return kmd.findByDateKnMeeting(beginDate,endTime,id);
    }
    public boolean CheckSignIn(Long meetingId,Long userId){
        return kmsid.findByIdMeetingIdAndIdUserId(meetingId,userId)!=null;
    }
    public List<KnMeeting> FindKnMeeting(Long beginDate,Long endTime,Long userId){
        return kmd.findKnMeeting(beginDate,endTime,userId);
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
    @Override
    public void run(){
        final KnMeetingRule rule=ReadKnMeetingRule();//获取会议规则
        logger.info("每分钟执行一次");
        try{
            logger.info("检测推送会议发起人开会前签到");
            signInBeforeCue(rule);
            logger.info("检测推送会议发起人开会结束签到");
            signInAfterCue(rule);
            logger.info("检测推送消息与会人员");
            attendeeCue(rule);
        }catch(ParseException e){
            e.printStackTrace();
        }
    }
    /**
     * 根据会议规则提前若干分钟推送会议发起人签到
     *
     * @throws ParseException
     */
    private void signInBeforeCue(KnMeetingRule rule) throws ParseException{
        List<KnMeeting> list=kmd.findMeetingBefore(System.currentTimeMillis());
        for(KnMeeting m : list){
            DateTime start=new DateTime(System.currentTimeMillis());
            DateTime end=new DateTime(m.getBeginDate());
            Integer num=getMinutes(start,end);
            if(num<=rule.getSignInBeforeTime()){
                final List<Long> ids=Lists.newArrayList();
                ids.add(m.getUserId());
                StringBuilder sb=new StringBuilder("会议即将开始，请签到，空闲三次将被取消预订会议室资格。如需取消会议，请及时取消。");
                ms.SendMess(ids,Setting.MessageType.intermes.toString(),"华大基因",sb.toString(),sb.toString(),107,"","107@"+m.getId());
                m.setSignInCueStatus(1);//推送成功修改状态为1
                SaveKnMeeting(m);
            }
        }
    }
    /**
     * 根据会议规则当会议结束后若干分钟推送会议发起人签到
     *
     * @throws ParseException
     */
    private void signInAfterCue(KnMeetingRule rule) throws ParseException{
        List<KnMeeting> list=kmd.findMeetingAfter(System.currentTimeMillis());
        for(KnMeeting m : list){
            if(!CheckSignIn(m.getId(),m.getUserId())){
                DateTime start=new DateTime(m.getEndDate());
                DateTime end=new DateTime(System.currentTimeMillis());
                Integer num=getMinutes(start,end);
                if(num>=rule.getSignInEndTime()){
                    final List<Long> ids=Lists.newArrayList();
                    ids.add(m.getUserId());
                    StringBuilder sb=new StringBuilder("您发起的"+m.getTitle()+"未签到，请补签，空闲三次将被取消预订会议室资格");
                    ms.SendMess(ids,Setting.MessageType.intermes.toString(),"华大基因",sb.toString(),sb.toString(),107,"","107@"+m.getId());
                    m.setSignInCueEndStatus(1);//推送成功修改状态为1
                    SaveKnMeeting(m);
                }
            }
        }
    }
    /**
     * 根据会议室规则提前若干分钟推送消息与会人员开会
     *
     * @throws ParseException
     */
    @SuppressWarnings("ALL")
    private void attendeeCue(KnMeetingRule rule) throws ParseException{
        Long t=rule.getScheduleTime()==null?5L:rule.getScheduleTime();
        List<KnMeeting> list=kmd.findMeetingAttendeeCue(System.currentTimeMillis());
        for(KnMeeting m : list){
            DateTime start=new DateTime(System.currentTimeMillis());
            DateTime end=new DateTime(m.getBeginDate());
            Integer num=getMinutes(start,end);
            List<KnMeetingAttendee> attendees=attendeeDao.findByKmId(m.getId());
            List<Long> ids=Collections3.extractToList(attendees,"attendeeId");
            String type=Collections3.extractToList(attendees,"attendeeType").toString();
            if(num<=rule.getScheduleTime()){
                if(type.indexOf("DEPARTMENT")>0){
                    List<KnEmployee> emps=os.findEmpListByOrgIdList(ids);
                    ids=Collections3.extractToList(emps,"id");
                }
                StringBuilder sb=new StringBuilder(m.getTitle()+"将在"+t.toString()+"分钟后开始，请及时参加，");
                sb.append("地点是").append(m.getKmr().getName()).append("会议室");
                ms.SendMess(ids,Setting.MessageType.intermes.toString(),"华大基因",sb.toString(),sb.toString(),105,"","105@"+m.getId());
                m.setAttendeeCueStatus(1);//推送成功修改状态为1
                SaveKnMeeting(m);
            }
        }
    }
    /**
     * @param m       会议对象
     * @param title   标题
     * @param message 消息内容
     */
    @SuppressWarnings("ALL")
    public void pushMessage(KnMeeting m,String title,String message,Integer id,String uri){
        List<KnMeetingAttendee> list=attendeeDao.findByKmId(m.getId());
        String type=Collections3.extractToList(list,"attendeeType").toString();
        List<Long> ids=Collections3.extractToList(list,"attendeeId");
        if(type.indexOf("DEPARTMENT")>0){
            List<KnEmployee> emps=os.findEmpListByOrgIdList(ids);
            ids=Collections3.extractToList(emps,"id");
        }
        ms.SendMess(ids,Setting.MessageType.intermes.toString(),title,message,message,id,"",uri);
    }
    /**
     * 计算时间分钟差
     *
     * @param start 开始时间
     * @param end   结束时间
     *
     * @return 相差分钟
     */
    private Integer getMinutes(DateTime start,DateTime end) throws ParseException{
        return Minutes.minutesBetween(start,end).getMinutes();
    }
}