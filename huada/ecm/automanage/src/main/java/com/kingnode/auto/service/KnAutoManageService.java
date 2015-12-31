package com.kingnode.auto.service;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kingnode.auto.dao.KnAutoBorrowDao;
import com.kingnode.auto.dao.KnAutoDao;
import com.kingnode.auto.dao.KnAutoDriverDao;
import com.kingnode.auto.dao.KnAutoMessageDao;
import com.kingnode.auto.dao.KnSettingDao;
import com.kingnode.auto.dao.KnTrafficAccidentDao;
import com.kingnode.auto.dto.ApplyAutoDTO;
import com.kingnode.auto.dto.AutoBorrowParamDTO;
import com.kingnode.auto.dto.KnAutoDTO;
import com.kingnode.auto.entity.KnAuto;
import com.kingnode.auto.entity.KnAutoBorrow;
import com.kingnode.auto.entity.KnAutoDriver;
import com.kingnode.auto.entity.KnAutoMessage;
import com.kingnode.auto.entity.KnSetting;
import com.kingnode.auto.entity.KnTrafficAccident;
import com.kingnode.diva.mapper.BeanMapper;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.diva.utils.Threads;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.dao.system.KnEmployeeDao;
import com.kingnode.xsimple.dao.system.KnUserDao;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnUser;
import com.kingnode.xsimple.rest.DetailDTO;
import com.kingnode.xsimple.service.safety.MessageService;
import com.kingnode.xsimple.util.Utils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Component @Transactional(readOnly=false)
public class KnAutoManageService implements Runnable{
    private static final Logger logger=LoggerFactory.getLogger(KnAutoManageService.class);
    private KnAutoBorrowDao borrowDao;
    private KnAutoDao autoDao;
    private KnAutoDriverDao driverDao;
    private KnSettingDao settingDao;
    private KnEmployeeDao empDao;
    private KnUserDao userDao;
    private KnTrafficAccidentDao tAdao;
    private KnAutoMessageDao messageDao;
    private MessageService ms;
    private String cronExpression="0 0/10 * * * ?";
    private int shutdownTimeout=20;
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    /**
     * 读取事故
     *
     * @param id
     *
     * @return
     */
    public KnTrafficAccident ReadKnTrafficAccident(Long id){
        return tAdao.findOne(id);
    }
    /**
     * 保存事故
     *
     * @return
     */
    public KnTrafficAccident SaveKnTrafficAccident(KnTrafficAccident accident){
        Long id=accident.getKaId();
        KnAuto auto=ReadKnAuto(id);
        accident.setKa(auto);
        KnTrafficAccident newAccident=tAdao.save(accident);
        SaveAuto(auto);
        return newAccident;
    }
    /**
     * 删除事故
     *
     * @param id 车险id
     *
     * @return
     */
    public void DeleteKnTrafficAccident(Long id){
        KnTrafficAccident accident=tAdao.findOne(id);
        if(accident!=null){
            KnAuto auto=accident.getKa();
            tAdao.delete(id);
            if(auto!=null){
                SaveAuto(auto);
            }
        }
    }
    /**
     * 读取事故
     *
     * @param id
     *
     * @return
     */
    public KnAuto ReadKnAuto(Long id){
        return autoDao.findOne(id);
    }
    /**
     * 保存司机
     *
     * @param driver
     *
     * @return
     */
    @Transactional(readOnly=false)
    public KnAutoDriver SaveDriver(KnAutoDriver driver){
        if(driver.getName()==null||"".equals(driver.getName().trim())){
            return null;
        }
        //主要作用是去掉一些空格
        // driver=ajustDriver(driver);
        return this.driverDao.save(driver);
    }
    @Transactional(readOnly=false)
    public KnAuto SaveAuto(KnAuto auto){
        if(auto.getPlateNumber()==null||"".equals(auto.getPlateNumber().trim())){
            return null;
        }
        //主要作用是去掉一些空格
        try{
            if(auto.getId()!=null){
                List<KnTrafficAccident> list=tAdao.findByKa(auto);
                auto.setCount((long)list.size());
            }
        }catch(Exception e){
            logger.error(e.getMessage());
        }
        return this.autoDao.save(auto);
    }
    public DataTable<KnAutoDriver> PageDrivers(final Map<String,Object> searchParams,DataTable<KnAutoDriver> dt,Sort sort){
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        Page<KnAutoDriver> page=driverDao.findAll(new Specification<KnAutoDriver>(){
            @Override
            public Predicate toPredicate(Root<KnAutoDriver> knResourceRoot,CriteriaQuery<?> criteriaQuery,CriteriaBuilder criteriaBuilder){
                Predicate predicate=criteriaBuilder.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(searchParams.size()!=0){
                    if(searchParams.get("LIKE_name")!=null&&!Strings.isNullOrEmpty(searchParams.get("LIKE_name").toString())){
                        expressions.add(criteriaBuilder.like(knResourceRoot.<String>get("name"),"%"+searchParams.get("LIKE_name").toString()+"%"));
                    }
                    if(searchParams.get("LIKE_gender")!=null&&!Strings.isNullOrEmpty(searchParams.get("LIKE_gender").toString())){
                        expressions.add(criteriaBuilder.equal(knResourceRoot.<KnAutoDriver.DriverGender>get("gender"),KnAutoDriver.DriverGender.valueOf(searchParams.get("LIKE_gender").toString())));
                    }
                    if(searchParams.get("LIKE_driving")!=null&&!Strings.isNullOrEmpty(searchParams.get("LIKE_driving").toString())){
                        expressions.add(criteriaBuilder.like(knResourceRoot.<String>get("driving"),"%"+searchParams.get("LIKE_driving").toString()+"%"));
                    }
                    if(searchParams.get("LIKE_expiryDate")!=null&&!Strings.isNullOrEmpty(searchParams.get("LIKE_expiryDate").toString())){
                        expressions.add(criteriaBuilder.like(knResourceRoot.<String>get("expiryDate"),"%"+searchParams.get("LIKE_expiryDate").toString()+"%"));
                    }
                    if(searchParams.get("LIKE_driverYear")!=null&&!Strings.isNullOrEmpty(searchParams.get("LIKE_driverYear").toString())){
                        expressions.add(criteriaBuilder.like(knResourceRoot.<String>get("driverYear"),"%"+searchParams.get("LIKE_driverYear").toString()+"%"));
                    }
                    if(searchParams.get("LIKE_drivingType")!=null&&!Strings.isNullOrEmpty(searchParams.get("LIKE_drivingType").toString())){
                        expressions.add(criteriaBuilder.like(knResourceRoot.<String>get("drivingType"),"%"+searchParams.get("LIKE_drivingType").toString()+"%"));
                    }
                    if(searchParams.get("LIKE_state")!=null&&!Strings.isNullOrEmpty(searchParams.get("LIKE_state").toString())){
                        expressions.add(criteriaBuilder.equal(knResourceRoot.<KnAutoDriver.DriverState>get("state"),KnAutoDriver.DriverState.valueOf(searchParams.get("LIKE_state").toString())));
                    }
                    if(searchParams.get("LIKE_mobile")!=null&&!Strings.isNullOrEmpty(searchParams.get("LIKE_mobile").toString())){
                        expressions.add(criteriaBuilder.like(knResourceRoot.<String>get("mobile"),"%"+searchParams.get("LIKE_mobile").toString()+"%"));
                    }
                }
                return predicate;
            }
        },pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setiTotalRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    public DataTable<KnAuto> PageAutos(final Map<String,Object> searchParams,DataTable<KnAuto> dt,Sort sort){
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        Page<KnAuto> page=this.autoDao.findAll(new Specification<KnAuto>(){
            @Override
            public Predicate toPredicate(Root<KnAuto> knResourceRoot,CriteriaQuery<?> criteriaQuery,CriteriaBuilder criteriaBuilder){
                Predicate predicate=criteriaBuilder.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(searchParams.size()!=0){
                    if(!Utils.isEmptyStr(searchParams.get("LIKE_name"))){
                        expressions.add(criteriaBuilder.like(knResourceRoot.<String>get("name"),"%"+searchParams.get("LIKE_name").toString()+"%"));
                    }
                    if(!Utils.isEmptyStr(searchParams.get("LIKE_model"))){
                        expressions.add(criteriaBuilder.equal(knResourceRoot.<KnAutoDriver.DriverGender>get("structure"),KnAutoDriver.DriverGender.valueOf(searchParams.get("LIKE_model").toString())));
                    }
                    if(!Utils.isEmptyStr(searchParams.get("LIKE_plateNumber"))){
                        expressions.add(criteriaBuilder.like(knResourceRoot.<String>get("plateNumber"),"%"+searchParams.get("LIKE_plateNumber").toString()+"%"));
                    }
                    if(!Utils.isEmptyStr(searchParams.get("LIKE_totalMileage"))){
                        expressions.add(criteriaBuilder.equal(knResourceRoot.<String>get("totalMileage"),searchParams.get("LIKE_totalMileage").toString()));
                    }
                    if(!Utils.isEmptyStr(searchParams.get("LIKE_count"))){
                        expressions.add(criteriaBuilder.equal(knResourceRoot.<String>get("count"),searchParams.get("LIKE_count").toString()));
                    }
                    if(!Utils.isEmptyStr(searchParams.get("LIKE_engine"))){
                        expressions.add(criteriaBuilder.like(knResourceRoot.<String>get("engine"),"%"+searchParams.get("LIKE_engine").toString()+"%"));
                    }
                    if(!Utils.isEmptyStr(searchParams.get("LIKE_structure"))){
                        expressions.add(criteriaBuilder.like(knResourceRoot.<String>get("structure"),"%"+searchParams.get("LIKE_structure").toString()+"%"));
                    }
                    if(searchParams.get("LIKE_seating")!=null&&!Strings.isNullOrEmpty(searchParams.get("LIKE_seating").toString())){
                        expressions.add(criteriaBuilder.equal(knResourceRoot.<Integer>get("seating"),Integer.valueOf(searchParams.get("LIKE_seating").toString())));
                    }
                    if(searchParams.get("LIKE_remark")!=null&&!Strings.isNullOrEmpty(searchParams.get("LIKE_remark").toString())){
                        expressions.add(criteriaBuilder.equal(knResourceRoot.<KnAutoDriver.DriverState>get("remark"),KnAutoDriver.DriverState.valueOf(searchParams.get("LIKE_remark").toString())));
                    }
                    if(!Utils.isEmptyStr(searchParams.get("LIKE_autoState"))){
                        expressions.add(criteriaBuilder.equal(knResourceRoot.<String>get("autoState"),KnAuto.AutoState.valueOf(searchParams.get("LIKE_autoState").toString())));
                    }
                }
                return predicate;
            }
        },pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setiTotalRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    /**
     * 下一步需要被归还得车辆list
     *
     * @param searchParams
     * @param dt
     * @param sort
     *
     * @return
     */
    public DataTable<KnAutoBorrow> PageAutoForReturn(final Map<String,Object> searchParams,DataTable<KnAutoBorrow> dt,Sort sort,final KnAutoBorrow.BorrowType bttype){
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        Page<KnAutoBorrow> page=this.borrowDao.findAll(new Specification<KnAutoBorrow>(){
            @Override
            public Predicate toPredicate(Root<KnAutoBorrow> knResourceRoot,CriteriaQuery<?> criteriaQuery,CriteriaBuilder criteriaBuilder){
                Predicate predicate=criteriaBuilder.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(searchParams.size()!=0){
                    if(!Utils.isEmptyStr(searchParams.get("LIKE_name"))){
                        expressions.add(criteriaBuilder.like(knResourceRoot.<String>get("name"),"%"+searchParams.get("LIKE_name").toString()+"%"));
                    }
                    if(!Utils.isEmptyStr(searchParams.get("LIKE_applicationDate"))){
                        String timeStr=searchParams.get("LIKE_applicationDate").toString();
                        Long time1=DateTime.parse(timeStr,DateTimeFormat.forPattern("yyyy-MM-dd")).getMillis();
                        Long time2=time1+(24*60*60);
                        expressions.add(criteriaBuilder.between(knResourceRoot.<Long>get("applicationDate"),time1,time2));
                    }
                }
                if(bttype!=null){
                    expressions.add(criteriaBuilder.equal(knResourceRoot.<KnAutoBorrow.BorrowType>get("bt"),KnAutoBorrow.BorrowType.LEND));
                }
                return predicate;
            }
        },pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setiTotalRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    /**
     * 过往车辆申请list
     *
     * @param searchParams
     * @param dt
     * @param sort
     *
     * @return
     */
    public DataTable<Map> PageListBorrow(final Map<String,Object> searchParams,DataTable<Map> dt,Sort sort,final List<KnAutoBorrow.BorrowType> typeList){
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        Page<KnAutoBorrow> page=this.borrowDao.findAll(new Specification<KnAutoBorrow>(){
            @Override
            public Predicate toPredicate(Root<KnAutoBorrow> knResourceRoot,CriteriaQuery<?> criteriaQuery,CriteriaBuilder criteriaBuilder){
                Predicate predicate=criteriaBuilder.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(searchParams.size()!=0){
                    if(!Utils.isEmptyStr(searchParams.get("LIKE_name"))){
                        expressions.add(criteriaBuilder.like(knResourceRoot.<String>get("name"),"%"+searchParams.get("LIKE_name").toString()+"%"));
                    }
                    if(!Utils.isEmptyStr(searchParams.get("LIKE_applicationDate"))){
                        String timeStr=searchParams.get("LIKE_applicationDate").toString();
                        Long time1=DateTime.parse(timeStr,DateTimeFormat.forPattern("yyyy-MM-dd")).getMillis();
                        Long time2=time1+(24*60*60);
                        expressions.add(criteriaBuilder.between(knResourceRoot.<Long>get("applicationDate"),time1,time2));
                    }
                }
                if(typeList!=null){
                    expressions.add(knResourceRoot.<KnAutoBorrow.BorrowType>get("bt").in(typeList));
                }
                return predicate;
            }
        },pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setiTotalRecords(page.getTotalElements());
        dt.setAaData(getKnAutoBorrowMapList(page.getContent()));
        return dt;
    }
    public List<Map> getKnAutoBorrowMapList(List<KnAutoBorrow> list){
        List<Map> returnList=new ArrayList<>();
        ObjectMapper objectMapper=new ObjectMapper();
        for(KnAutoBorrow object : list){
            Map<String,Object> mapObj=objectMapper.convertValue(object,Map.class);
            Map map=new HashMap();
            if(object.getNeedDriver()){
                KnAutoDriver driver=object.getDriver();
                if(driver!=null){
                    map.put("mobile",driver.getMobile());
                    map.put("name",driver.getName());
                    mapObj.put("oDriver",map);
                }
            }else{
                KnEmployee emp;
                KnUser user;
                try{
                    emp=empDao.findOne(object.getUserId());
                    user=userDao.findOne(object.getUserId());
                    map.put("mobile",emp.getPhone());
                    map.put("name",user.getLoginName());
                    mapObj.put("sDriver",map);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            returnList.add(mapObj);
        }
        return returnList;
    }
    public boolean isValidField(String name){
        return name!=null&&!Strings.isNullOrEmpty(name);
    }
    public void DeleteKnDriver(Long id){
        KnAutoDriver dirver=this.driverDao.findOne(id);
        if(dirver!=null){
            this.driverDao.delete(id);
        }
    }
    /**
     * 删除汽车
     *
     * @param id
     */
    public void DeleteKnAuto(Long id){
        KnAuto auto=this.autoDao.findOne(id);
        if(auto!=null){
            this.autoDao.delete(id);
        }
    }
    /**
     * 查找司机
     *
     * @param id
     *
     * @return
     */
    public KnAutoDriver ReadDriver(Long id){
        return this.driverDao.findOne(id);
    }
    /**
     * 查找汽车
     *
     * @param id
     *
     * @return
     */
    public KnAuto ReadAuto(Long id){
        try{
            return this.autoDao.findOne(id);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 申请单
     *
     * @param applyAuto
     */
    public void ApplyFormAuto(ApplyAutoDTO applyAuto){
        Long autoId=applyAuto.getAutoId();
        KnAuto auto=ReadAuto(autoId);
        String cause=applyAuto.getCause();//用车事由
        String destination=applyAuto.getDestination();//目的地
        Long mileage=applyAuto.getMileage();//预计里程
        //        if(!KnAuto.AutoType.RESTORE.equals(auto.getAutoState())){
        //            return;
        //        }
        Long userId=applyAuto.getUserId();//借车人ID
        String name=applyAuto.getName();//借车人姓名
        name=name==null?"":name.trim();
        cause=cause==null?"":cause.trim();
        KnAutoBorrow autoBorrow=new KnAutoBorrow();
        autoBorrow.setApprovalDate(System.currentTimeMillis());
        autoBorrow.setName(name);//名称
        autoBorrow.setCause(cause);//事由
        autoBorrow.setUserId(userId);//用户id
        autoBorrow.setKa(auto);
        autoBorrow.setMileage(mileage);
        // autoBorrow.setReserveDate(reserveDate);
        autoBorrow.setDestination(destination);
        autoBorrow.setLendDate(applyAuto.getLendDate());
        autoBorrow.setRestoreDate(applyAuto.getRestoreDate());
        autoBorrow.setApplicationDate(System.currentTimeMillis());
        autoBorrow.setRidingNumber(applyAuto.getRidingNumber());
        autoBorrow.setBt(KnAutoBorrow.BorrowType.APPLY);
        autoBorrow.setNeedDriver(true);
        borrowDao.save(autoBorrow);
    }
    public List<KnAuto> ListAutos(){
        return (List<KnAuto>)this.autoDao.findAll();
    }
    /**
     * @param id
     *
     * @return
     */
    public KnAutoBorrow ReadAutoBorrow(Long id){
        return borrowDao.findOne(id);
    }
    /**
     * 审批通过
     *
     * @param id
     * @param driverId
     */
    public void ApproveApplyForm(Long id,Long driverId){
        KnAutoBorrow object=ReadAutoBorrow(id);
        List<KnAutoBorrow.BorrowType> list=new ArrayList<>();
        list.add(KnAutoBorrow.BorrowType.APPROVAL);
        list.add(KnAutoBorrow.BorrowType.LEND);
        List<KnAuto> autoList=autoDao.queryCanReserveAuto(object.getLendDate(),object.getRestoreDate(),object.getKa().getId(),list);
        if(!Utils.isEmpityCollection(autoList)){
            throw new RuntimeException("车辆在此预定时间范围内已经有人预定,不能再审批借出。");
        }
        object.setBt(KnAutoBorrow.BorrowType.APPROVAL);
        if(driverId!=null){
            KnAutoDriver driver=ReadDriver(driverId);
            if(driver!=null){
                object.setDriver(driver);
            }
        }
        borrowDao.save(object);
        SendDrivingCarMsg(object);
    }
    /**
     * 推送出车message   审批通过后，发给驾驶员的信息
     *
     * @param object
     */
    public void SendDrivingCarMsg(final KnAutoBorrow object){
        try{
            final List<Long> ids=new ArrayList<>();
            final String title=DateTime.now().toString("MM-dd")+"公务用车";
            final String content="您在\""+new DateTime(object.getLendDate()).toString("MM-dd HH:mm")+"\"有到\""+object.getDestination()+"\"出车任务,请准时出车";
            KnAutoMessage msg=new KnAutoMessage();
            msg.setbId(object.getId());
            msg.setTitle(title);
            msg.setContent(content);
            msg.setType(KnAutoMessage.Type.CM);//update by 陈超
            messageDao.save(msg);
            new Thread(new Runnable(){
                @Override public void run(){
                    KnAutoDriver driver=object.getDriver();
                    logger.error("推送出车:"+driver);
                    if(driver!=null&&driver.getEmpId()!=null&&driver.getEmpId().intValue()>0){
                        ids.add(driver.getEmpId());
                        ms.SendMess(ids,Setting.MessageType.intermes.toString(),title,content,content,109,"","109@");
                    }
                }
            }).start();
        }catch(Exception e){
            e.printStackTrace();
            logger.error("推送出车消息出错"+e.getMessage());
        }
    }
    /**
     * 推送出车message,提前2小时发送领车信息给用户
     *
     * @param object
     */
    public void SendLCarMsg(final KnAutoBorrow object){
        try{
            final List<Long> ids=new ArrayList<>();
            ids.add(object.getUserId());
            final String title=new DateTime(new Date()).toString("MM-dd")+"公务用车";
            KnAuto auto=object.getKa();
            String carName=auto.getName();
            KnAuto.Transmission transmission=auto.getTransmission();
            String ts=displayTransmissionInfo(transmission);
            String seating=auto.getSeating()+"座";
            String carMsg=carName+","+ts;//+","+seating+","+auto.getPlateNumber();
            String time=new DateTime(object.getLendDate()).toString("MM-dd HH:mm");
            final String content="您申请的\""+carMsg+"\"请于"+time+"准时领取。";
            KnAutoMessage msg=new KnAutoMessage();
            msg.setbId(object.getId());
            msg.setTitle(title);
            msg.setContent(content);
            msg.setType(KnAutoMessage.Type.LM);
            messageDao.save(msg);
//            new Thread(new Runnable(){
//                @Override public void run(){
            try{
                ms.SendMess(ids,Setting.MessageType.intermes.toString(),title,content,content,108,"","108@");
            }catch(Exception e){
                logger.error("推送出车消息出错"+e.getMessage());
            }
//                }
//            }).start();
        }catch(Exception e){
            e.printStackTrace();
            logger.error("推送出车消息出错"+e.getMessage());
        }
    }
    private String displayTransmissionInfo(KnAuto.Transmission transmission){
        if(KnAuto.Transmission.AUTO==transmission){
            return "自动挡";
        }
        if(KnAuto.Transmission.MANUAL==transmission){
            return "手动挡";
        }
        if(KnAuto.Transmission.MIX==transmission){
            return "混合挡";
        }
        return " ";
    }
    //TODO:  这个地方  为什么不保存借阅记录
    public void RejectApplyForm(Long id){
        KnAutoBorrow object=ReadAutoBorrow(id);
        KnAuto auto=object.getKa();
        // auto.setAutoState(KnAuto.AutoState.RESTORE);
        object.setBt(KnAutoBorrow.BorrowType.REJECT);
        //设置拒绝时间
        object.setRejectDate(System.currentTimeMillis());
        //  borrowDao.save(object);
        autoDao.save(auto);
        //陈超增加
        borrowDao.save(object);
    }
    public void CancelAutoAppToUser(Long id){
        KnAutoBorrow object=ReadAutoBorrow(id);
        // KnAuto auto=object.getKa();
        object.setBt(KnAutoBorrow.BorrowType.DESELECT);
        object.setRejectDate(System.currentTimeMillis());
        borrowDao.save(object);
    }
    public void ReutrnAutoToRestore(Long id){
        KnAutoBorrow object=ReadAutoBorrow(id);
        KnAuto auto=object.getKa();
        auto.setAutoState(KnAuto.AutoState.RESTORE);
        object.setBt(KnAutoBorrow.BorrowType.RESTORE);
        //设置拒绝时间
        object.setRejectDate(System.currentTimeMillis());
        borrowDao.save(object);
        autoDao.save(auto);
    }
    /**
     * 司机分页查询
     *
     * @param dataTable
     *
     * @return
     */
    public DataTable PageDriver(DataTable dataTable){
        try{
            //请求第几页数据
            int pageNum=dataTable.getiDisplayStart();
            //每页数据量
            int iDisplayLength=dataTable.getiDisplayLength();
            String sSortDir_0=dataTable.getSortValue(), orderName=dataTable.getSortColName(), sSearch=dataTable.getsSearch();//asc
            sSearch="%"+sSearch+"%";
            Sort sort=null;
            if(orderName!=null&&!orderName.equals("")){
                if(sSortDir_0.equalsIgnoreCase("asc")){
                    sort=new Sort(Sort.Direction.ASC,orderName);
                }else if(sSortDir_0.equalsIgnoreCase("desc")){
                    sort=new Sort(Sort.Direction.DESC,orderName);
                }
            }
            Pageable pageable;
            if(orderName!=null&&!orderName.equals("")){
                pageable=new PageRequest(pageNum,iDisplayLength,sort);
            }else{
                pageable=new PageRequest(pageNum,iDisplayLength);
            }
            //调用分页查询方法
            Page<KnAutoDriver> page_list=this.driverDao.queryDriver(sSearch,pageable);
            //组装分页json结果
            dataTable.setiTotalDisplayRecords(page_list.getTotalElements());
            dataTable.setiTotalRecords(page_list.getTotalElements());
            dataTable.setAaData(page_list.getContent());
            return dataTable;
        }catch(Exception e){
            e.printStackTrace();
        }
        return dataTable;
    }
    public DataTable<KnTrafficAccident> PageKnTrafficAccident(final Map<String,Object> searchParams,DataTable<KnTrafficAccident> dt,Sort sort,final Long autoId){
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        Page<KnTrafficAccident> page=this.tAdao.findAll(new Specification<KnTrafficAccident>(){
            @Override
            public Predicate toPredicate(Root<KnTrafficAccident> knResourceRoot,CriteriaQuery<?> criteriaQuery,CriteriaBuilder criteriaBuilder){
                Predicate predicate=criteriaBuilder.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(searchParams.size()!=0){
                    if(!Utils.isEmptyStr(searchParams.get("LIKE_name"))){
                        expressions.add(criteriaBuilder.like(knResourceRoot.<String>get("name"),"%"+searchParams.get("LIKE_name").toString()+"%"));
                    }
                    if(!Utils.isEmptyStr(searchParams.get("LIKE_occurTime"))){
                        expressions.add(criteriaBuilder.equal(knResourceRoot.<String>get("occurTime"),"%"+searchParams.get("LIKE_occurTime").toString()+"%"));
                    }
                }
                if(autoId!=null){
                    expressions.add(criteriaBuilder.equal(knResourceRoot.<Long>get("kaId"),autoId));
                }
                return predicate;
            }
        },pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setiTotalRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    /**
     * 发放车辆
     *
     * @param id           持久化的 借车申请单的id
     * @param changeObject 页面传过来的申请单
     */
    public synchronized String GrantAuto(Long id,KnAutoBorrow changeObject){
        KnAutoBorrow object=borrowDao.findOne(id);
        KnAuto auto=object.getKa();
        if(auto==null){
            object.setBt(KnAutoBorrow.BorrowType.DESELECT);
            borrowDao.save(object);
            return "预约的车辆不存在,取消申请单!";
        }
        //update by 陈超  这里不需要系统来控制该不该发放，应该由管理员来控制
        //        Long time1=object.getLendDate();
        //        Long time2=object.getRestoreDate();
        //        Long time3=System.currentTimeMillis();
        //        if(!(time1<time3&&time3<time2)){
        //            return "当前时间不在预约时间内,发放失败!";
        //        }
        if(KnAuto.AutoState.LEND.equals(auto.getAutoState())){
            return "车辆处于借出状态,发放失败!";
        }
        //表示钥匙已经领取
        object.setAkey(changeObject.getAkey());
        object.setDriving(changeObject.getDriving());
        object.setRechargeable(changeObject.getRechargeable());
        object.setBt(KnAutoBorrow.BorrowType.LEND);
        object.setLendAutoState(changeObject.getLendAutoState());
        auto.setAutoState(KnAuto.AutoState.LEND);
        autoDao.save(auto);
        borrowDao.save(object);
        KnAutoDriver driver=object.getDriver();
        if(driver!=null&&driver.getId()!=null){
            driver.setDrivingType(KnAutoDriver.DriverState.TURNOUT.toString());
            driverDao.save(driver);
        }
        return "发放成功!";
    }
    /**
     * 发放车辆
     *
     * @param id
     * @param changeObject 页面传过来的公务车申请单
     */
    public synchronized void ReturnAuto(Long id,KnAutoBorrow changeObject){
        KnAutoBorrow object=null;
        if(id!=null){
            object=borrowDao.findOne(id);
        }
        if(object==null){
            logger.error("KnAutoBorrow对象的Id为空");
            return;
        }
        //表示钥匙已经领取
        object.setAkey(changeObject.getAkey());
        object.setDriving(changeObject.getDriving());
        object.setRechargeable(changeObject.getRechargeable());
        object.setRestoreGauge(changeObject.getRestoreGauge());
        object.setRestoreMileage(changeObject.getRestoreMileage());
        object.setRestoreAutoState(changeObject.getRestoreAutoState());
        object.setBt(KnAutoBorrow.BorrowType.RESTORE);
        KnAuto auto=object.getKa();
        auto.setTotalGauge(changeObject.getRestoreGauge());
        auto.setTotalMileage(changeObject.getRestoreMileage());
        auto.setAutoState(KnAuto.AutoState.RESTORE);
        autoDao.save(auto);
        borrowDao.save(object);
        KnAutoDriver driver=object.getDriver();
        if(driver!=null&&driver.getId()!=null){
            driver.setDrivingType(KnAutoDriver.DriverState.STANDBY.toString());
            driverDao.save(driver);
        }
    }
    public KnSetting SaveSetting(KnSetting setting){
        return settingDao.save(setting);
    }
    public KnSetting FetchSetting(){
        KnSetting setting=settingDao.findOne(1L);
        if(setting==null){
            return settingDao.save(new KnSetting());
        }
        return setting;
    }
    //=================定时任务开始=============//
    @PostConstruct
    public void start(){
        threadPoolTaskScheduler=new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setThreadNamePrefix("autoManageJob");
        threadPoolTaskScheduler.initialize();
        threadPoolTaskScheduler.schedule(this,new CronTrigger(cronExpression));
    }
    @PreDestroy
    public void stop(){
        ScheduledExecutorService scheduledExecutorService=threadPoolTaskScheduler.getScheduledExecutor();
        Threads.normalShutdown(scheduledExecutorService,shutdownTimeout,TimeUnit.SECONDS);
    }
    /** 定时检查超时申请单. */
    @Override
    public void run(){
        checkOverTimeAutoBorrow();
        sendLMessage();
    }
    private void checkOverTimeAutoBorrow(){
        try{
            logger.info("检查申请单超时...");
            KnSetting setting=FetchSetting();
            int minutes=setting.getMinutes();
            //根据 申请时间-x>overtime  ---> ct-overtime>申请时间,推出超时发生的时间为
            long condition=System.currentTimeMillis()-minutes*60*1000;
            List<KnAutoBorrow> list=borrowDao.queryOverTimeAutoBorrow(condition);
            for(KnAutoBorrow object : list){
                //将申请单设置为超时状态
                object.setBt(KnAutoBorrow.BorrowType.OVERTIME);
                //释放占有的车子资源
                SaveKnAutoBorrow(object);
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }
    public KnAutoBorrow SaveKnAutoBorrow(KnAutoBorrow borrow){
        return this.borrowDao.save(borrow);
    }
    private void sendLMessage(){
        try{
            logger.info("提前两小时发送消息给用户领车...");
            KnSetting setting=FetchSetting();
            //根据 申请时间l-c<2 ---> l<c+2,触发时间为curtime+2*60*60*1000;
            Long curtime=System.currentTimeMillis();
            long condition=curtime+2*60*60*1000;
            List<KnAutoBorrow> list=borrowDao.queryLMAutoBorrow(condition,curtime);
            for(KnAutoBorrow object : list){
                List<KnAutoMessage> messageList=messageDao.findByBIdAndType(object.getId(),KnAutoMessage.Type.LM);
                if(messageList.size()==0){
                    SendLCarMsg(object);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }
    public void setCronExpression(String cronExpression){
        this.cronExpression=cronExpression;
    }
    /** 设置normalShutdown的等待时间,单位秒. */
    public void setShutdownTimeout(int shutdownTimeout){
        this.shutdownTimeout=shutdownTimeout;
    }
    //=================定时任务结束=============//
    //============外部借口调用开始=============//
    /**
     * 外部接口   查询车辆列表
     *
     * @param state    APPLY,LEND,RESTORE,SCRAPPING中的一种
     * @param pageNo   分页页码
     * @param pageSize 分页数量大小
     *
     * @return
     */
    public Page<KnAuto> ListKnAuto(final KnAuto.AutoState state,final int pageNo,final int pageSize){
        Specification<KnAuto> spec=new Specification<KnAuto>(){
            @Override
            public Predicate toPredicate(Root<KnAuto> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                List<Predicate> predicates=Lists.newArrayList();
                if(state!=null){
                    predicates.add(cb.equal(root.<KnAuto.AutoState>get("autoState"),state));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        PageRequest pageRequest=new PageRequest(pageNo,pageSize,new Sort(Sort.Direction.DESC,"name"));
        return autoDao.findAll(spec,pageRequest);
    }
    /**
     * 申请借车
     * <p/>
     * RESERVE(预约)，LEND(借出)，RESTORE(库存)，SCRAPPING(作废)
     * RESERVE(预约)表示这个车子即将有人要,LEND(借出)表示车子正在使用状态,二者这都不能被预定
     * RESTORE(库存)表示,车子目前还没发生业务,处于可借状态
     * 考虑到订单可能同时预定一辆车,因此方法是必须同步的
     *
     * @param applyAuto
     *
     * @return
     */
    public DetailDTO<KnAutoBorrow> ApplyAuto(ApplyAutoDTO applyAuto){
        System.out.println(this);
        DetailDTO dto=new DetailDTO<KnAutoBorrow>();
        Long currenttime=System.currentTimeMillis();
        Long fromtime=applyAuto.getLendDate();
        Long endtime=applyAuto.getRestoreDate();
        List<KnAutoBorrow.BorrowType> list=new ArrayList<>();
        list.add(KnAutoBorrow.BorrowType.APPLY);
        list.add(KnAutoBorrow.BorrowType.APPROVAL);
        list.add(KnAutoBorrow.BorrowType.LEND);
        Long autoId=applyAuto.getAutoId();
        KnAuto auto=ReadAuto(autoId);
        List<KnAuto> returnAutos=autoDao.queryCanReserveAuto(fromtime,endtime,autoId,list);
        if(!Utils.isEmpityCollection(returnAutos)){
            dto.setErrorCode("auto_6");
            dto.setStatus(false);
            dto.setErrorMessage("车子已被预定,时间相互冲突");
            return dto;
        }
        //        if(autoId==null){
        //            dto.setErrorCode("auto_1");
        //            dto.setStatus(false);
        //            dto.setErrorMessage("预定的车子不存在");
        //            return dto;
        //        }
        //
        //        if(auto==null){
        //            dto.setErrorCode("auto_1");
        //            dto.setStatus(false);
        //            dto.setErrorMessage("预定的车子不存在");
        //            return dto;
        //        }
        //
        if(fromtime==null||endtime==null||endtime<fromtime){
            dto.setErrorCode("auto_2");
            dto.setStatus(false);
            dto.setErrorMessage("预约结束时间必须大于预约开始时间");
            return dto;
        }
        if(fromtime<currenttime){
            dto.setErrorCode("auto_3");
            dto.setStatus(false);
            dto.setErrorMessage("预约起始时间不能选在过去");
            return dto;
        }
        //        Long span=endtime-fromtime;
        //        if(span<1000*60*15){
        //            dto.setErrorCode("auto_4");
        //            dto.setStatus(false);
        //            dto.setErrorMessage("预约结束时间必须大于预约开始时间15分钟以上");
        //            return dto;
        //        }
        String cause=applyAuto.getCause();//用车事由
        String destination=applyAuto.getDestination();//目的地
        Long mileage=applyAuto.getMileage();//预计里程
        KnAuto.AutoState state=auto.getAutoState();
        if(KnAuto.AutoState.SCRAPPING.equals(state)){
            dto.setErrorCode("auto_5");
            dto.setStatus(false);
            dto.setErrorMessage("预定车子失败,车辆处于报废状态!");
            return dto;
        }
        Long userId=applyAuto.getUserId();//借车人ID
        String name=applyAuto.getName();//借车人姓名
        name=name==null?"":name.trim();
        cause=cause==null?"":cause.trim();
        KnAutoBorrow autoBorrow=new KnAutoBorrow();
        autoBorrow.setName(name);//名称
        autoBorrow.setCause(cause);//事由
        autoBorrow.setUserId(userId);//用户id
        autoBorrow.setKa(auto);
        autoBorrow.setMileage(mileage);
        //设置起始里程,订单起始里程默认是车辆的里程
        autoBorrow.setLendMileage(auto.getTotalMileage());
        autoBorrow.setLendGauge(auto.getTotalGauge());
        autoBorrow.setApplicationDate(System.currentTimeMillis());
        autoBorrow.setDestination(destination);
        autoBorrow.setLendDate(applyAuto.getLendDate());
        autoBorrow.setRestoreDate(applyAuto.getRestoreDate());
        autoBorrow.setRidingNumber(applyAuto.getRidingNumber());
        autoBorrow.setDrivingNo(applyAuto.getDrivingNo());
        autoBorrow.setWorkNo(applyAuto.getWorkNo());
        autoBorrow.setDriverYear(applyAuto.getDriverYear());
        autoBorrow.setDepartName(applyAuto.getDepartName());
        //将车子修改为预约状态中
        autoBorrow.setBt(KnAutoBorrow.BorrowType.APPLY);
        //自驾还是他驾
        autoBorrow.setNeedDriver(applyAuto.isNeedDriver());
        KnAutoBorrow borrowRecord=borrowDao.save(autoBorrow);
        dto.setDetail(borrowRecord);
        dto.setStatus(true);
        return dto;
    }
    public Page<KnAutoBorrow> PageKnAutoBorrow(AutoBorrowParamDTO dto){
        PageRequest pageRequest=new PageRequest(dto.getPageNo(),dto.getPageSize(),new Sort(Sort.Direction.DESC,"applicationDate"));
        Page<KnAutoBorrow> page;
        if(dto.getStateList()==null||dto.getStateList().isEmpty()){
            page=borrowDao.PageKnAutoBorrow(dto.getUserId(),pageRequest);
        }else{
            page=borrowDao.PageKnAutoBorrow(dto.getUserId(),dto.getStateList(),pageRequest);
        }
        return page;
    }
    /**
     * 列出指定车辆的车险
     *
     * @param sort   车险分页排序
     * @param autoId 车险相关联的车辆
     *
     * @return 车险的List集合
     */
    public List<KnTrafficAccident> ListKnTrafficAccident(Sort sort,final Long autoId){
        return this.tAdao.findAll(new Specification<KnTrafficAccident>(){
            @Override
            public Predicate toPredicate(Root<KnTrafficAccident> knResourceRoot,CriteriaQuery<?> criteriaQuery,CriteriaBuilder criteriaBuilder){
                Predicate predicate=criteriaBuilder.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(autoId!=null){
                    expressions.add(criteriaBuilder.equal(knResourceRoot.<Long>get("kaId"),autoId));
                }
                return predicate;
            }
        },sort);
    }
    /**
     * 退订车子
     *
     * @param id 要取消的申请单
     *
     * @return
     */
    public DetailDTO<KnAutoBorrow> UnsubscribeAuto(Long id){
        DetailDTO dto=new DetailDTO<KnAutoBorrow>();
        if(id==null){
            dto.setStatus(false);
            dto.setErrorMessage("退订车子的申请单不存在");
            return dto;
        }
        KnAutoBorrow borrow=borrowDao.findOne(id);
        if(borrow==null){
            dto.setStatus(false);
            dto.setErrorMessage("退订车子的申请单不存在");
            return dto;
        }
        KnAutoBorrow.BorrowType type=borrow.getBt();
        if(!(KnAutoBorrow.BorrowType.APPLY.equals(type)||KnAutoBorrow.BorrowType.APPROVAL.equals(type))){
            dto.setStatus(false);
            dto.setErrorMessage("车子不处于申请或者审批状态");
            return dto;
        }
        borrow.setBt(KnAutoBorrow.BorrowType.DESELECT);
        KnAuto auto=borrow.getKa();
        // 退订之后将车子修改为库存状态
        //update by 陈超   此处不应该修改车辆状态  车辆状态只能在领车和还车处修改
        //        if(auto!=null){
        //            auto.setAutoState(KnAuto.AutoState.RESTORE);
        //            autoDao.save(auto);
        //        }
        borrowDao.save(borrow);
        //发送车辆退订信息
        SendCancelAutoMsg(borrow);
        dto.setStatus(true);
        dto.setDetail(borrow);
        return dto;
    }
    /**
     * 推送出车message   退订 ，发给驾驶员的取消信息
     *
     * @param object
     */
    public void SendCancelAutoMsg(final KnAutoBorrow object){
        try{
            final List<Long> ids=new ArrayList<>();
            final String title=new DateTime(new Date()).toString("MM-dd HH:mm")+"公务用车";
            final String content="您在\""+new DateTime(object.getLendDate()).toString("MM-dd HH:mm")+"\"到\""+object.getDestination()+"\"的出车任务取消,请知悉。";
            KnAutoMessage msg=new KnAutoMessage();
            msg.setbId(object.getId());
            msg.setTitle(title);
            msg.setContent(content);
            msg.setType(KnAutoMessage.Type.TM);
            messageDao.save(msg);
            new Thread(new Runnable(){
                @Override public void run(){
                    KnAutoDriver driver=object.getDriver();
                    logger.error("推送取消车辆消息司机："+driver);
                    if(driver!=null&&driver.getEmpId()!=null&&driver.getEmpId().intValue()>0){
                        ids.add(driver.getEmpId());
                        ms.SendMess(ids,Setting.MessageType.intermes.toString(),title,content,content,110,"","110@");
                    }
                }
            }).start();
        }catch(Exception e){
            e.printStackTrace();
            logger.error("推送取消车辆消息出错"+e.getMessage());
        }
    }
    /**
     * 查询车辆借出情况
     *
     * @param date          查询日期
     * @param p
     * @param s
     * @param transmissions 变速箱类型
     *
     * @return
     */
    public List<KnAutoDTO> FindKnAutos(String date,int p,int s,String transmissions){
        JavaType javaType=(new ObjectMapper()).getTypeFactory().constructParametricType(List.class,KnAuto.Transmission.class);
        List<KnAuto.Transmission> transmissionsListObj=JsonMapper.nonEmptyMapper().fromJson(transmissions,javaType);
        Page<KnAuto> autoPage=this.FindKnAutos(p,s,transmissionsListObj);
        List<KnAuto> autoList=autoPage.getContent();
        List<KnAutoDTO> dtos=Lists.newArrayList();
        if(autoList!=null&&autoList.size()>0){
            Date pDateStart=new DateTime(date).toDate();
            Date pDateEnd=DateUtils.addDays(pDateStart,1);
            for(KnAuto auto : autoList){
                List<KnTrafficAccident> trafficAccidents=tAdao.findByKa(auto);
                KnAutoDTO dto=BeanMapper.map(auto,KnAutoDTO.class);
                dto.setCount((long)trafficAccidents.size());
                List<KnAutoBorrow> borrows=borrowDao.queryApprovelAutoBorrow(auto.getId(),pDateStart.getTime(),pDateEnd.getTime());
                dto.setBorrowTimes(getKnAutoBorrow(borrows,pDateStart,pDateEnd));
                dtos.add(dto);
            }
        }
        return dtos;
    }
    /**
     * 获取所有借车时间
     *
     * @param borrows
     *
     * @return
     */
    private List<String> getKnAutoBorrow(List<KnAutoBorrow> borrows,Date pDateStart,Date pDateEnd){
        List<String> times=Lists.newArrayList();
        if(borrows==null||borrows.size()>0){
            for(KnAutoBorrow borrow : borrows){
                String timeMessage=(pDateStart.after(new Date(borrow.getLendDate()))?"00:00":getHour(borrow.getLendDate(),"HH:mm"))+"~"+(new Date(borrow.getRestoreDate()).after(pDateEnd)?"23:59":getHour(borrow.getRestoreDate(),"HH:mm"));
                times.add(timeMessage);
            }
        }
        return times;
    }
    /**
     * 获取时间得某个区间端
     *
     * @param time
     *
     * @return
     */
    public String getHour(Long time,String p){
        return new DateTime(time).toString(p);
    }
    /**
     * 查询所有车辆
     *
     * @param p
     * @param s
     * @param transmissions
     *
     * @return
     */
    public Page<KnAuto> FindKnAutos(int p,int s,final List<KnAuto.Transmission> transmissions){
        List<Sort.Order> orders=Lists.newArrayList();
        orders.add(new Sort.Order(Sort.Direction.DESC,"createTime"));
        PageRequest pageRequest=new PageRequest(p,s,new Sort(orders));
        Specification<KnAuto> spec=new Specification<KnAuto>(){
            @Override
            public Predicate toPredicate(Root<KnAuto> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                List<Predicate> predicates=Lists.newArrayList();
                predicates.add(cb.notEqual(root.<String>get("autoState"),KnAuto.AutoState.SCRAPPING));
                if(transmissions!=null&&transmissions.size()>0){
                    predicates.add(cb.and(root.<String>get("transmission").in(transmissions)));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return autoDao.findAll(spec,pageRequest);
    }
    /**
     * 可以预定的车辆
     *
     * @param fromtime 预约开始时间
     * @param endtime  预约结束时间
     * @param tList    车辆类型的集合,比如自动挡,手动挡,自动和手动混合
     *
     * @return
     */
    public List<KnAuto> CanSubscribeList(Long fromtime,Long endtime,List<KnAuto.Transmission> tList){
        List<KnAutoBorrow.BorrowType> btList=new ArrayList<>();
        btList.add(KnAutoBorrow.BorrowType.APPLY);
        btList.add(KnAutoBorrow.BorrowType.APPROVAL);
        btList.add(KnAutoBorrow.BorrowType.LEND);
        List<KnAuto> list=autoDao.queryCanReserveList(fromtime,endtime,btList);
        if(tList!=null){
            Iterator<KnAuto> it=list.iterator();
            while(it.hasNext()){
                KnAuto auto=it.next();
                KnAuto.Transmission transmission=auto.getTransmission();
                if(!tList.contains(transmission)){
                    it.remove();
                }
            }
        }
        return list;
    }
    /**
     * 根据变速箱的类型查找公务用车
     *
     * @param list 变速箱类型,有MANUAL,AUTO,MIX值可以供选择
     *
     * @return
     */
    public List<KnAuto> QueryByTransmission(List<KnAuto.Transmission> list){
        return autoDao.QueryByTransmission(list);
    }
    /**
     * 是否有借车记录
     *
     * @param id
     *
     * @return
     */
    public Boolean hasAutoBorrow(Long id){
        Long count=borrowDao.queryCount(id);
        return count!=0;
    }
    //============外部借口调用结束=============//
    @Autowired
    public void setBorrowDao(KnAutoBorrowDao borrowDao){
        this.borrowDao=borrowDao;
    }
    @Autowired
    public void setAutoDao(KnAutoDao autoDao){
        this.autoDao=autoDao;
    }
    @Autowired
    public void setDriverDao(KnAutoDriverDao driverDao){
        this.driverDao=driverDao;
    }
    @Autowired
    public void setSettingDao(KnSettingDao settingDao){
        this.settingDao=settingDao;
    }
    @Autowired
    public void setEmpDao(KnEmployeeDao dao){
        this.empDao=dao;
    }
    @Autowired
    public void setUserDao(KnUserDao userDao){
        this.userDao=userDao;
    }
    @Autowired
    public void settAdao(KnTrafficAccidentDao tAdao){
        this.tAdao=tAdao;
    }
    @Autowired
    public void setMs(MessageService ms){
        this.ms=ms;
    }
    @Autowired
    public void setMessageDao(KnAutoMessageDao messageDao){
        this.messageDao=messageDao;
    }
}
