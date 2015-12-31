package com.kingnode.meeting.rest;
import java.io.IOException;
import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kingnode.diva.mapper.BeanMapper;
import com.kingnode.diva.qrcode.TwoDimensionCode;
import com.kingnode.diva.utils.Collections3;
import com.kingnode.meeting.dto.KnMeetingDTO;
import com.kingnode.meeting.dto.KnMeetingFileDTO;
import com.kingnode.meeting.dto.KnMeetingRoomDTO;
import com.kingnode.meeting.dto.KnMeetingSignInDTO;
import com.kingnode.meeting.dto.KnOrganizationDTO;
import com.kingnode.meeting.dto.KnScheduleDTO;
import com.kingnode.meeting.dto.MeetingPersonDTO;
import com.kingnode.meeting.entity.KnMeeting;
import com.kingnode.meeting.entity.KnMeetingAttendee;
import com.kingnode.meeting.entity.KnMeetingBlacklist;
import com.kingnode.meeting.entity.KnMeetingFile;
import com.kingnode.meeting.entity.KnMeetingRoom;
import com.kingnode.meeting.entity.KnMeetingRule;
import com.kingnode.meeting.entity.KnMeetingSignIn;
import com.kingnode.meeting.entity.KnMeetingSignInId;
import com.kingnode.meeting.entity.KnMeetingSummary;
import com.kingnode.meeting.service.MeetingService;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnEmployeeOrganization;
import com.kingnode.xsimple.entity.system.KnOrganization;
import com.kingnode.xsimple.rest.DetailDTO;
import com.kingnode.xsimple.rest.ListDTO;
import com.kingnode.xsimple.rest.PersonMeetingDTO;
import com.kingnode.xsimple.rest.RestStatus;
import com.kingnode.xsimple.service.system.OrganizationService;
import com.kingnode.xsimple.util.Users;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * 会议rest服务
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 *
 *
 */
@RestController @RequestMapping({"/api/v1/meeting","/api/secure/v1/meeting"})
public class MeetingRestController{
    @Autowired
    private MeetingService ms;
    @Autowired
    private OrganizationService os;
    /**
     * 获取会议列表
     *
     * @param date     日期 例如 2014-01-01
     * @param pageNo   页码 0表示第一页，默认值为0
     * @param pageSize 每页显示数，默认为10条数据
     *                 <pre>
     *                                                                 接口方法：/api/v1/meeting/list/2014-01-11/?p=0&s=10
     *                                                                 成功返回值：
     *                                                                 {
     *                                                                     "status" : true,
     *                                                                     "errorCode" : null,
     *                                                                     "errorMessage" : null,
     *                                                                     "list" : [ {
     *                                                                         "title":"",//会议标题
     *                                                                         "moderator":"",//会议主持人
     *                                                                         "recorder":"",//会议纪要人
     *                                                                         "attendee":"",// 与会人员;
     *                                                                         "attendeeIds":"",//与会人员id集合
     *                                                                         "attendeeType":"",//与会人人员，或者部门
     *                                                                         "dateTime":"",//会议日期
     *                                                                         "beginTime":"",//会议开始日期时间
     *                                                                         "endTime":"",//会议开始时间时间
     *                                                                         "roomId":"",//会议室ID
     *                                                                         "roomName":"",// 会议室
     *                                                                         "meetingType":"",// 会议类型
     *                                                                         "description":"",// 会议说明
     *                                                                         "summary":""//会议纪要
     *                                                                     }]
     *                                                                 }
     *                                                                 失败返回
     *                                                                 {
     *                                                                 "status" : false,
     *                                                                 "errorCode" : null,
     *                                                                 "errorMessage" : null,
     *                                                                 "list" : []
     *                                                                 }
     *                                                            </pre>
     */
    @RequestMapping(value="/list/{date}", method={RequestMethod.GET})
    public ListDTO<KnMeetingDTO> List(@PathVariable("date") String date,@RequestParam(value="p", defaultValue="0") Integer pageNo,@RequestParam(value="s", defaultValue="5") Integer pageSize){
        List<KnMeeting> kms=ms.PageKnMeeting(Users.id(),date,pageNo,pageSize);
        List<KnMeetingDTO> list=Lists.newArrayList();
        for(KnMeeting km : kms){
            KnMeetingDTO dto=BeanMapper.map(km,KnMeetingDTO.class);
            dto.setDateTime(new DateTime(km.getBeginDate()).toString("yyyy-MM-dd"));
            dto.setBeginTime(new DateTime(km.getBeginDate()).toString("HH:mm"));
            dto.setEndTime(new DateTime(km.getEndDate()).toString("HH:mm"));
            dto.setRoomName(km.getKmr().getName());
            list.add(dto);
        }
        return new ListDTO<>(true,list);
    }
    /**
     * 获取会议详情
     *
     * @param id 会议id
     *           <pre>
     *                                         接口方法：/api/v1/meeting/read/1/?p=0&s=10
     *                                         成功返回值：
     *                                         {
     *                                             "status" : true,
     *                                             "errorCode" : null,
     *                                             "errorMessage" : null,
     *                                             "detail" : [ {
     *                                                 "title":"",//会议标题
     *                                                 "moderator":"",//会议主持人
     *                                                 "recorder":"",//会议纪要人
     *                                                 "attendee":"",// 与会人员;
     *                                                 "attendeeIds":"",//与会人员id集合
     *                                                 "attendeeType":"",//与会人人员，或者部门
     *                                                 "dateTime":"",//会议日期
     *                                                 "beginTime":"",//会议开始日期时间
     *                                                 "endTime":"",//会议开始时间时间
     *                                                 "roomId":"",//会议室ID
     *                                                 "roomName":"",// 会议室
     *                                                 "meetingType":"",// 会议类型
     *                                                 "description":"",// 会议说明
     *                                                 "summary":""//会议纪要
     *                                             }]
     *                                         }
     *                                         失败返回
     *                                         {
     *                                         "status" : false,
     *                                         "errorCode" : null,
     *                                         "errorMessage" : null,
     *                                         "detail" : null
     *                                         }
     *                                    </pre>
     */
    @RequestMapping(value="/read/{id}", method={RequestMethod.GET})
    public DetailDTO<KnMeetingDTO> Read(@PathVariable("id") Long id){
        KnMeeting km=ms.ReadKnMeeting(id);
        KnMeetingSummary kms=ms.ReadKnMeetingSummary(id);
        KnMeetingDTO dto=BeanMapper.map(km,KnMeetingDTO.class);
        dto.setDateTime(new DateTime(km.getBeginDate()).toString("yyyy-MM-dd"));
        dto.setBeginTime(new DateTime(km.getBeginDate()).toString("HH:mm"));
        dto.setEndTime(new DateTime(km.getEndDate()).toString("HH:mm"));
        dto.setRoomName(km.getKmr().getName());
        dto.setRoomId(km.getKmr().getId());
        String summary="";
        if(kms!=null){
            summary=kms.getSummary();
        }
        dto.setSummary(summary);
        KnMeetingRule rule=ms.ReadKnMeetingRule();//获取会议规则
        if(rule!=null){
            dto.setSignInBeforeTime(rule.getSignInBeforeTime()!=null?rule.getSignInBeforeTime():10);
            dto.setSignInEndTime(rule.getSignInEndTime()!=null?rule.getSignInEndTime():10);
        }
        return new DetailDTO<>(true,dto);
    }
    /**
     * 获取会议室列表
     *
     * @param date
     * @param pageNo   页码 0表示第一页，默认值为0
     * @param pageSize 每页显示数，默认为10条数据
     *                 <pre>
     *                                                                 接口方法：/api/v1/meeting/rooms/2014-01-11/?p=0&s=10
     *                                                                 成功返回值：
     *                                                                 {
     *                                                                     "status" : true,
     *                                                                     "errorCode" : null,
     *                                                                     "errorMessage" : null,
     *                                                                     "list" : [ {
     *                                                                         "icon":"";//会议室图标
     *                                                                          "name":"";//会议室名称，
     *                                                                          "addr":"";// 会议室地址，
     *                                                                          "num":"";// 可容纳人数，
     *                                                                          "remark":"";// 会议室备注
     *                                                                          "equipment":"",//设备名称
     *                                                                          "equipmentIcon":"",//设备图标
     *                                                                          "schedule":[{
     *                                                                              "dateTime":"", //会议日期
     *                                                                              "beginTime":"", //会议开始日期时间
     *                                                                              "endTime":"", //会议开始时间时间
     *                                                                              "title":"", //会议标题
     *                                                                              "moderator":"", //会议主持人
     *                                                                          }]
     *                                                                     }]
     *                                                                 }
     *                                                                 失败返回
     *                                                                 {
     *                                                                 "status" : false,
     *                                                                 "errorCode" : null,
     *                                                                 "errorMessage" : null,
     *                                                                 "list" : []
     *                                                                 }
     *                                                            </pre>
     */
    @SuppressWarnings("ALL") @RequestMapping(value="/rooms/{date}", method={RequestMethod.GET})
    public ListDTO<KnMeetingRoomDTO> ListKnMeetingRoom(@PathVariable("date") String date,@RequestParam(value="p", defaultValue="0") Integer pageNo,@RequestParam(value="s", defaultValue="5") Integer pageSize){
        Map<String,Object> map=new TreeMap<String,Object>(new Comparator(){
            public int compare(Object o1,Object o2){
                Collator myCollator=Collator.getInstance();
                return myCollator.compare((String)o1,(String)o2);
            }
        });
        List<KnMeetingRoom> kmrs=ms.ListKnMeetingRoom(pageNo,pageSize);
        List<KnMeetingRoomDTO> list=Lists.newArrayList();
        for(KnMeetingRoom kmr : kmrs){
            KnMeetingRoomDTO dto=BeanMapper.map(kmr,KnMeetingRoomDTO.class);
            dto.setEquipment(Collections3.extractToList(kmr.getKme(),"name"));
            dto.setEquipmentIcon(Collections3.extractToList(kmr.getKme(),"icon"));
            List<KnScheduleDTO> kss=Lists.newArrayList();
            for(KnMeeting km : ms.FindByDateKnMeeting(date,kmr.getId())){
                KnScheduleDTO ksd=new KnScheduleDTO();
                ksd.setDateTime(new DateTime(km.getBeginDate()).toString("yyyy-MM-dd"));
                ksd.setBeginTime(new DateTime(km.getBeginDate()).toString("HH:mm"));
                ksd.setEndTime(new DateTime(km.getEndDate()).toString("HH:mm"));
                kss.add(ksd);
            }
            dto.setSchedule(kss);
            map.put(dto.getName(),dto);
        }
        for(String key : map.keySet()){
            list.add((KnMeetingRoomDTO)map.get(key));
        }
        return new ListDTO<>(true,list);
    }
    /**
     * 获取会议室详情
     *
     * @param id   会议id
     * @param date 时间 例如 2014-01-01
     *             <pre>
     *                                             接口方法：/api/v1/meeting/room/1/2014-01-11/?p=0&s=10
     *                                             成功返回值：
     *                                             {
     *                                             "status" : true,
     *                                             "errorCode" : null,
     *                                             "errorMessage" : null,
     *                                             "list" : [ {
     *                                                 "icon":"";//会议室图标
     *                                                  "name":"";//会议室名称，
     *                                                  "addr":"";// 会议室地址，
     *                                                  "num":"";// 可容纳人数，
     *                                                  "remark":"";// 会议室备注
     *                                                  "equipment":"",//设备名称
     *                                                  "equipmentIcon":"",//设备图标
     *                                                  "schedule":[{
     *                                                      "dateTime":"", //会议日期
     *                                                      "beginTime":"", //会议开始日期时间
     *                                                      "endTime":"", //会议开始时间时间
     *                                                      "title":"", //会议标题
     *                                                      "moderator":"", //会议主持人
     *                                                  }]
     *                                             }]
     *                                             }
     *                                             失败返回
     *                                             {
     *                                             "status" : false,
     *                                             "errorCode" : null,
     *                                             "errorMessage" : null,
     *                                             "list" : []
     *                                             }
     *                                        </pre>
     */
    @RequestMapping(value="/room/{id}/{date}", method={RequestMethod.GET})
    public DetailDTO<KnMeetingRoomDTO> ReadKnMeetingRoom(@PathVariable("id") Long id,@PathVariable("date") String date){
        KnMeetingRoom kmr=ms.ReadKnMeetingRoom(id);
        KnMeetingRoomDTO dto=BeanMapper.map(kmr,KnMeetingRoomDTO.class);
        dto.setEquipment(Collections3.extractToList(kmr.getKme(),"name"));
        dto.setEquipmentIcon(Collections3.extractToList(kmr.getKme(),"icon"));
        List<KnScheduleDTO> kss=Lists.newArrayList();
        for(KnMeeting km : ms.FindByDateKnMeeting(date,kmr.getId())){
            KnScheduleDTO ksd=new KnScheduleDTO();
            ksd.setDateTime(new DateTime(km.getBeginDate()).toString("yyyy-MM-dd"));
            ksd.setBeginTime(new DateTime(km.getBeginDate()).toString("HH:mm"));
            ksd.setEndTime(new DateTime(km.getEndDate()).toString("HH:mm"));
            ksd.setTitle(km.getTitle());
            ksd.setModerator(km.getModerator());
            kss.add(ksd);
        }
        dto.setSchedule(kss);
        return new DetailDTO<>(true,dto);
    }
    /**
     * 新建会议
     *
     * @param kmt {
     *            "title":"",//会议标题
     *            "moderator":"",//会议主持人
     *            "recorder":"",//会议纪要人
     *            "attendee":"",// 与会人员;
     *            "attendeeIds":"",//与会人员id集合
     *            "attendeeType":"",//与会人人员，或者部门
     *            "dateTime":"",//会议日期
     *            "beginTime":"",//会议开始日期时间
     *            "endTime":"",//会议开始时间时间
     *            "roomId":"",//会议室ID
     *            "roomName":"",// 会议室
     *            "meetingType":"",// 会议类型
     *            "description":"",// 会议说明
     *            "summary":""//会议纪要
     *            }
     *            <pre>
     *                                      接口方法：/api/v1/meeting/create
     *                                      成功返回值：
     *                                      {
     *                                          "status" : true,
     *                                          "errorCode" : null,
     *                                          "errorMessage" : null,
     *                                          "detail" : 1  //会议id
     *                                      }
     *                                      失败返回
     *                                      {
     *                                      "status" : false,
     *                                      "errorCode" : null,
     *                                      "errorMessage" : null,
     *                                      "detail" : null
     *                                      }
     *                                  </pre>
     */
    @RequestMapping(value="/create", method={RequestMethod.POST})
    public DetailDTO CreateKnMeeting(KnMeetingDTO kmt){
        Long beginDate=DateTime.parse(kmt.getDateTime()+" "+kmt.getBeginTime(),DateTimeFormat.forPattern("yyyy-MM-dd HH:mm")).getMillis();
        Long endDate=DateTime.parse(kmt.getDateTime()+" "+kmt.getEndTime(),DateTimeFormat.forPattern("yyyy-MM-dd HH:mm")).getMillis();
        Long existMeeting=ms.isExistMeeting(beginDate,endDate,kmt.getRoomId());
        DetailDTO rs=new DetailDTO<>(true,0);
        if(existMeeting>0&&kmt.getId()==null){
            rs.setStatus(false);
            rs.setErrorCode("001");
            rs.setErrorMessage("该时间段，会议室已被预定");
            return rs;
        }else{
            KnMeeting km=BeanMapper.map(kmt,KnMeeting.class);
            km.setUserId(Users.id());
            KnMeetingRoom kmr=new KnMeetingRoom();
            kmr.setId(kmt.getRoomId());
            km.setKmr(kmr);
            km.setBeginDate(beginDate);
            if(km.getBeginDate().compareTo(System.currentTimeMillis())>0){
                km.setEndDate(endDate);
                final KnMeeting meeting=ms.SaveKnMeeting(km);
                if(kmt.getAttendeeIds()!=null){
                    String ids[]=kmt.getAttendeeIds().split(",");
                    String names[]=kmt.getAttendee().split(",");
                    ms.DeleteAttendeeByMeetingId(meeting.getId());
                    for(int i=0;i<ids.length;i++){
                        KnMeetingAttendee attendee=new KnMeetingAttendee();
                        attendee.setAttendeeId(Long.parseLong(ids[i].trim()));
                        attendee.setAttendee(names[i]);
                        attendee.setKm(meeting);
                        if(kmt.getAttendeeType().equals("EMPLOYEE")){
                            attendee.setAttendeeType(KnMeetingAttendee.AttendeeType.EMPLOYEE);
                        }else{
                            attendee.setAttendeeType(KnMeetingAttendee.AttendeeType.DEPARTMENT);
                        }
                        attendee.setCreateTime(System.currentTimeMillis());
                        ms.SaveKnMeetingAttendee(attendee);
                    }
                }
                final Long meetId=km.getId();
                final StringBuffer message=new StringBuffer("");
                message.append(meeting.getModerator());
                message.append("邀请您在");
                message.append(new DateTime(meeting.getBeginDate()).toString("MM-dd HH:mm"));
                message.append("在"+ms.ReadKnMeetingRoom(kmt.getRoomId()).getName()+"会议室").append("参加").append(meeting.getTitle());
                new Thread(new Runnable(){
                    @Override public void run(){
                        ms.pushMessage(meeting,"华大基因",message.toString(),104,"104@"+meetId);
                    }
                }).start();
                rs.setDetail(meetId);
                return rs;
            }else{
                rs.setStatus(false);
                rs.setErrorCode("002");
                rs.setErrorMessage("会议开始时间不能早于当前时间");
                return rs;
            }
        }
    }
    /**
     * 修改会议
     *
     * @param kmt {
     *            "title":"",//会议标题
     *            "moderator":"",//会议主持人
     *            "recorder":"",//会议纪要人
     *            "attendee":"",// 与会人员;
     *            "attendeeIds":"",//与会人员id集合
     *            "attendeeType":"",//与会人人员，或者部门
     *            "dateTime":"",//会议日期
     *            "beginTime":"",//会议开始日期时间
     *            "endTime":"",//会议开始时间时间
     *            "roomId":"",//会议室ID
     *            "roomName":"",// 会议室
     *            "meetingType":"",// 会议类型
     *            "description":"",// 会议说明
     *            "summary":""//会议纪要
     *            }
     * @param id  会议id
     *            <pre>
     *                                                         接口方法：/api/v1/meeting/update/1
     *                                                         成功返回值：
     *                                                         {
     *                                                             "status" : true,
     *                                                             "errorCode" : null,
     *                                                             "errorMessage" : null
     *                                                         }
     *                                                         失败返回值：
     *                                                         {
     *                                                             "status" : false,
     *                                                             "errorCode" : “”,
     *                                                             "errorMessage" : null
     *                                                         }
     *                                                         </pre>
     */
    @RequestMapping(value="/update/{id}", method={RequestMethod.POST})
    public RestStatus UpdateKnMeeting(KnMeetingDTO kmt,@PathVariable("id") Long id){
        KnMeeting km=ms.ReadKnMeeting(id);
        BeanMapper.copy(kmt,km);
        KnMeetingRoom kmr=new KnMeetingRoom();
        kmr.setId(id);
        km.setKmr(kmr);
        km.setBeginDate(DateTime.parse(kmt.getDateTime()+" "+kmt.getBeginTime(),DateTimeFormat.forPattern("yyyy-MM-dd HH:mm")).getMillis());
        km.setEndDate(DateTime.parse(kmt.getDateTime()+" "+kmt.getEndTime(),DateTimeFormat.forPattern("yyyy-MM-dd HH:mm")).getMillis());
        //TODO:处理文件上传待实现
        ms.SaveKnMeeting(km);
        return new RestStatus(true);
    }
    /**
     * 获取资料列表(公用)
     *
     * @param id 会议id
     *           <pre>
     *                                         接口方法：/api/v1/meeting/file/1
     *                                         成功返回值：
     *                                         {
     *                                             "status" : true,
     *                                             "errorCode" : null,
     *                                             "errorMessage" : null,
     *                                             "detail" : {
     *                                                    "name";"a.mdf",//文件名称
     *                                                    "sha";"sha",//sha码
     *                                                    "fileDate";"10102340304049",//上传文件时间
     *                                                    "fileSize";"102400",//文件大小
     *                                                    "fileType";"DOC",//文件类型
     *                                                    "category";"MEETING"//枚举类型 MEETING,SUMMARY
     *                                             }
     *                                         }
     *                                         失败返回
     *                                         {
     *                                         "status" : false,
     *                                         "errorCode" : null,
     *                                         "errorMessage" : null,
     *                                         "detail" : null
     *                                         }
     *                                    </pre>
     */
    @RequestMapping(value="/file/{id}", method={RequestMethod.GET})
    public ListDTO<KnMeetingFileDTO> ListKnMeetingFile(@PathVariable("id") Long id){
        List<KnMeetingFile> kmfList=ms.ListKnMeetingFile(id);
        List<KnMeetingFileDTO> list=Lists.newArrayList();
        for(KnMeetingFile kmf : kmfList){
            KnMeetingFileDTO kmfDTO=new KnMeetingFileDTO();
            kmfDTO.setId(kmf.getId());
            kmfDTO.setName(kmf.getName());
            kmfDTO.setCategory(kmf.getCategory().toString());
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            kmfDTO.setFileDate(sdf.format(kmf.getFileDate()));
            kmfDTO.setFileSize(kmf.getFileSize());
            kmfDTO.setFileType(kmf.getFileType());
            kmfDTO.setSha(kmf.getSha());
            list.add(kmfDTO);
        }
        return new ListDTO<>(true,list);
    }
    /**
     * 上传会议资料
     *
     * @param dto {
     *            "name";"a.mdf",//文件名称
     *            "sha";"sha",//sha码
     *            "fileDate";"10102340304049",//上传文件时间
     *            "fileSize";"102400",//文件大小
     *            "fileType";"DOC",//文件类型
     *            "category";"MEETING"//枚举类型 MEETING,SUMMARY
     *            }
     * @param id  会议id
     *
     * @return
     *
     * @throws ParseException <pre>
     *                                                                          接口方法：/api/v1/meeting/file/1
     *                                                                          成功返回值：
     *                                                                                  {
     *                                                                         "status" : true,
     *                                                                         "errorCode" : null,
     *                                                                         "errorMessage" : null
     *                                                                                  }
     *                                                                          失败返回值：
     *                                                                                  {
     *                                                                         "status" : false,
     *                                                                         "errorCode" : “”,
     *                                                                         "errorMessage" : null
     *                                                                                  }
     *                                                                      </pre>
     */
    @RequestMapping(value="/file/{id}", method={RequestMethod.POST})
    public RestStatus SaveKnMeetingFile(KnMeetingFileDTO dto,@PathVariable("id") Long id) throws ParseException{
        KnMeetingFile kmf=BeanMapper.map(dto,KnMeetingFile.class);
        KnMeeting km=new KnMeeting();
        km.setId(id);
        kmf.setKm(km);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Long fileDate=sdf.parse(dto.getFileDate()).getTime();
        kmf.setFileDate(fileDate);
        ms.SaveKnMeetingFile(kmf);
        return new RestStatus(true);
    }
    /**
     * 获取会议评论列表
     *
     * @param id 会议摘要信息id
     *           <pre>
     *                                         接口方法：/api/v1/meeting/summary/1
     *                                         成功返回值：
     *                                         {
     *                                             "status" : true,
     *                                             "errorCode" : null,
     *                                             "errorMessage" : null,
     *                                             "detail" :  {
     *                                                 "summary" : "信息摘要"
     *                                             }
     *                                         }
     *                                         失败返回
     *                                         {
     *                                         "status" : false,
     *                                         "errorCode" : null,
     *                                         "errorMessage" : null,
     *                                         "detail" : null
     *                                         }
     *                                    </pre>
     */
    @RequestMapping(value="/summary/{id}", method={RequestMethod.GET})
    public DetailDTO<KnMeetingSummary> ReadKnMeetingSummary(@PathVariable("id") Long id){
        return new DetailDTO<>(true,ms.ReadKnMeetingSummary(id));
    }
    /**
     * 保存会议纪要
     *
     * @param kms {"summary":"信息摘要"}
     * @param id  <pre>
     *                                                         接口方法：/api/v1/meeting/summary/1
     *                                                         成功返回值：
     *                                                         {
     *                                                             "status" : true,
     *                                                             "errorCode" : null,
     *                                                             "errorMessage" : null
     *                                                         }
     *                                                         失败返回值：
     *                                                         {
     *                                                             "status" : false,
     *                                                             "errorCode" : “”,
     *                                                             "errorMessage" : null
     *                                                         }
     *                                                         </pre>
     */
    @RequestMapping(value="/summary/{id}", method={RequestMethod.POST})
    public RestStatus SaveKnMeetingSummary(KnMeetingSummary kms,@PathVariable("id") Long id){
        KnMeeting km=new KnMeeting();
        km.setId(id);
        ms.SaveKnMeetingSummary(kms);
        return new RestStatus(true);
    }
    /**
     * 会议签到
     *
     * @param id 会议id
     *           <pre>
     *                                                接口方法：/api/v1/meeting/sign/1
     *                                                成功返回值：
     *                                                        {
     *                                                            "status" : true,
     *                                                            "errorCode" : null,
     *                                                            "errorMessage" : null
     *                                                        }
     *                                                失败返回值：
     *                                                        {
     *                                                            "status" : false,
     *                                                            "errorCode" : “”,
     *                                                            "errorMessage" : null
     *                                                         }
     *                                          </pre>
     */
    @RequestMapping(value="/sign/{id}", method={RequestMethod.GET})
    public RestStatus ListKnMeetingSignIn(@PathVariable("id") Long id){
        RestStatus rs=new RestStatus();
        KnMeeting meeting=ms.ReadKnMeeting(id);
        KnMeetingRule rule=ms.ReadKnMeetingRule();//获取会议规则
        Integer signInBeforeTime=0;
        Integer signInEndTime=0;
        if(rule!=null){
            signInBeforeTime=rule.getSignInBeforeTime()!=null?rule.getSignInBeforeTime()+10:10;
            signInEndTime=rule.getSignInEndTime()!=null?rule.getSignInEndTime()+50:10;
        }
        Long startTime=new DateTime(meeting.getBeginDate()).secondOfMinute().addToCopy(-60*signInBeforeTime).getMillis();
        Long endTime=new DateTime(meeting.getEndDate()).secondOfMinute().addToCopy(60*signInEndTime+600).getMillis();
        if(System.currentTimeMillis()>=startTime&&System.currentTimeMillis()<=endTime){
            KnMeetingSignIn kmsi=new KnMeetingSignIn();
            KnMeetingSignInId kmsii=new KnMeetingSignInId();
            kmsii.setMeetingId(id);
            kmsii.setUserId(Users.id());
            KnEmployee emp=os.ReadEmp(Users.id());
            if(emp!=null){
                kmsi.setUserName(emp.getUserName()==null?"":emp.getUserName());
            }
            kmsi.setId(kmsii);
            kmsi.setSignTime(System.currentTimeMillis());
            ms.SaveKnMeetingSignIn(kmsi);
            rs.setStatus(true);
        }else{
            rs.setStatus(false);
            rs.setErrorMessage("不在签到时间内，不能签到");
        }
        return rs;
    }
    /**
     * 获取单条签到记录(判断是否签到)
     *
     * @param id 会议ID
     *           <pre>
     *                                              接口方法：/api/v1/meeting/check/1
     *                                              成功返回值：
     *                                              {
     *                                                "status" : true,
     *                                                "errorCode" : null,
     *                                                "errorMessage" : null
     *                                              }
     *                                              失败返回值：
     *                                              {
     *                                                "status" : false,
     *                                                "errorCode" : “”,
     *                                                "errorMessage" : null
     *                                              }
     *                                          </pre>
     */
    @RequestMapping(value="/sign/check/{id}", method={RequestMethod.GET})
    public RestStatus CheckKnMeetingSignIn(@PathVariable("id") Long id){
        return new RestStatus(ms.CheckSignIn(id,Users.id()));
    }
    /**
     * 获取会议签到记录
     *
     * @param id       会议id
     * @param pageNo   页码 0表示第一页，默认值为0
     * @param pageSize 每页显示数，默认为10条数据
     *                 <pre>
     *                                                                 接口方法：/api/v1/meeting/sign/list/1
     *                                                                 成功返回值：
     *                                                                 {
     *                                                                     "status" : true,
     *                                                                     "errorCode" : null,
     *                                                                     "errorMessage" : null,
     *                                                                     "list" : [ {
     *                                                                         "time" : "2014-01-01",//时间
     *                                                                         "userId" : "11",//用户id
     *                                                                         "userName" : "张三"//用户姓名
     *                                                                     }]
     *                                                                 }
     *                                                                 失败返回
     *                                                                 {
     *                                                                 "status" : false,
     *                                                                 "errorCode" : null,
     *                                                                 "errorMessage" : null,
     *                                                                 "list" : []
     *                                                                 }
     *                                                            </pre>
     */
    @RequestMapping(value="/sign/list/{id}", method={RequestMethod.GET})
    public ListDTO<KnMeetingSignInDTO> ReadKnMeetingSignIn(@PathVariable("id") Long id,@RequestParam(value="p", defaultValue="0") Integer pageNo,@RequestParam(value="s", defaultValue="5") Integer pageSize){
        List<KnMeetingSignIn> knmsis;
        boolean isModerator=ms.isModerator(id,Users.id());
        if(isModerator){
            knmsis=ms.PageKnMeetingSignIn(id,pageNo,pageSize);
        }else{
            knmsis=ms.PageKnMeetingSignIn(id,Users.id(),pageNo,pageSize);
        }
        return listKnMeetingSignInDTO(knmsis);
    }
    /**
     * 华大会议签到列表接口
     *
     * @param id       会议id
     * @param pageNo   页码 0表示第一页，默认值为0
     * @param pageSize 每页显示数，默认为10条数据
     *                 <pre>
     *                                                                 接口方法：/api/v1/meeting/oa-sign/list/1
     *                                                                 成功返回值：
     *                                                                 {
     *                                                                     "status" : true,
     *                                                                     "errorCode" : null,
     *                                                                     "errorMessage" : null,
     *                                                                     "list" : [ {
     *                                                                         "time" : "2014-01-01",//时间
     *                                                                         "userId" : "11",//用户id
     *                                                                         "userName" : "张三"//用户姓名
     *                                                                     }]
     *                                                                 }
     *                                                                 失败返回
     *                                                                 {
     *                                                                 "status" : false,
     *                                                                 "errorCode" : null,
     *                                                                 "errorMessage" : null,
     *                                                                 "list" : []
     *                                                                 }
     *                                                            </pre>
     */
    @RequestMapping(value="/oa-sign/list/{id}", method={RequestMethod.GET})
    public ListDTO<KnMeetingSignInDTO> ReadSignIn(@PathVariable("id") Long id,@RequestParam(value="p", defaultValue="0") Integer pageNo,@RequestParam(value="s", defaultValue="5") Integer pageSize){
        List<KnMeetingSignIn> knmsis=ms.PageKnMeetingSignIn(id,pageNo,pageSize);
        return listKnMeetingSignInDTO(knmsis);
    }
    /**
     * @param id 会议id
     *           <pre>
     *                                              接口方法：/api/v1/meeting/cancel/1
     *                                              成功返回值：
     *                                                       {
     *                                                        "status" : true,
     *                                                        "errorCode" : null,
     *                                                        "errorMessage" : null
     *                                                       }
     *                                              失败返回值：
     *                                                       {
     *                                                        "status" : false,
     *                                                        "errorCode" : “”,
     *                                                        "errorMessage" : null
     *                                                       }
     *                                          </pre>
     */
    @RequestMapping(value="/cancel/{id}", method={RequestMethod.GET})
    public RestStatus CancelMeeting(@PathVariable("id") Long id){
        final KnMeeting meeting=ms.ReadKnMeeting(id);
        final Long meetId=id;
        meeting.setStatusType(KnMeeting.StatusType.CANCEL);
        ms.SaveKnMeeting(meeting);
        final StringBuffer message=new StringBuffer("");
        message.append(meeting.getModerator());
        message.append("取消了 ");
        message.append(new DateTime(meeting.getBeginDate()).toString("MM-dd HH:mm"));
        message.append("-"+new DateTime(meeting.getEndDate()).toString("HH:mm"));
        message.append(" 的"+meeting.getTitle());
        new Thread(new Runnable(){
            @Override public void run(){
                ms.pushMessage(meeting,"华大基因",message.toString(),106,"106@"+meetId);
            }
        }).start();
        return new RestStatus(true);
    }
    /**
     * 获取日程记录
     *
     * @param date 日期 例如：2014-01
     *             <pre>
     *                                             接口方法：/api/v1/meeting/person/meet/2014-01
     *                                             成功返回值：
     *                                             {
     *                                             "status" : true,
     *                                             "errorCode" : null,
     *                                             "errorMessage" : null,
     *                                             "list" : [ {
     *                                                 "date" : "2014-01-01" //时间
     *                                             }]
     *                                             }
     *                                             失败返回
     *                                             {
     *                                             "status" : false,
     *                                             "errorCode" : null,
     *                                             "errorMessage" : null,
     *                                             "list" : []
     *                                             }
     *                                        </pre>
     */
    @RequestMapping(value="/person/meet/{date}", method={RequestMethod.GET})
    public ListDTO<PersonMeetingDTO> PersonMeeting(@PathVariable("date") String date){
        DateTime d=DateTime.parse(date,DateTimeFormat.forPattern("yyyy-MM"));
        Long beginDate=d.dayOfMonth().withMinimumValue().toDate().getTime();//获取月份的第一天Long
        Long endTime=d.dayOfMonth().withMaximumValue().toDate().getTime();//获取月份的最后一天Long
        List<KnMeeting> kms=ms.FindKnMeeting(beginDate,endTime,Users.id());
        List<PersonMeetingDTO> list=Lists.newArrayList();
        Map<String,PersonMeetingDTO> map=Maps.newHashMap();
        for(KnMeeting km : kms){
            PersonMeetingDTO pm=new PersonMeetingDTO();
            pm.setDate(km.getBeginDateStr());
            map.put(km.getBeginDateStr(),pm);
        }
        for(String key : map.keySet()){
            PersonMeetingDTO p=map.get(key);
            list.add(p);
        }
        return new ListDTO<>(true,list);
    }
    /**
     * 会议id生产二维码
     *
     * @param id 会议id
     *
     * @throws IOException
     */
    @RequestMapping(value="/generate-two-dimension-code/{id}", method={RequestMethod.GET})
    public void GenerateTwoDimensionCode(@PathVariable("id") Long id,HttpServletRequest request,HttpServletResponse response) throws IOException{
        response.setContentType("image/jpeg");//设置相应类型,告诉浏览器输出的内容为图片
        response.setHeader("Pragma","No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader("Expire",0);
        TwoDimensionCode handler=new TwoDimensionCode();
        handler.encoderQRCode(String.valueOf(id),response.getOutputStream());
    }
    /**
     * 检查当前用户是否造成会议室空闲使用3次的接口
     * <p/>
     * <pre>
     *          接口方法：/api/v1/meeting/rule
     *          成功返回值：
     *          {
     *              "status" : true,
     *              "errorCode" : null,
     *              "errorMessage" : null
     *          }
     *          失败返回值：
     *          {
     *              "status" : false,
     *              "errorCode" : “你已被锁定，不能申请会议”,
     *              "errorMessage" : null
     *          }
     *          </pre>
     */
    @RequestMapping(value="/rule", method={RequestMethod.GET})
    public RestStatus rule(){
        RestStatus rs;
        KnMeetingBlacklist blacklist=ms.ReadKnMeetingBlacklistByUserId(Users.id());
        Integer num;
        KnMeetingRule rule=ms.ReadKnMeetingRule();//获取会议规则
        Integer ruleNum=3;//默认申请会议空闲3次，超过锁定用户
        if(rule!=null){
            ruleNum=rule.getFreeNum()==null?3:rule.getFreeNum();
        }
        if(blacklist==null){
            num=ms.ReadMeetingRule(Users.id(),null);
            if(num>=ruleNum){
                blacklist=new KnMeetingBlacklist();
                blacklist.setLockTime(new Date().getTime());
                blacklist.setUserId(Users.id());
                KnEmployee emp=os.ReadEmp(Users.id());
                if(emp!=null){
                    blacklist.setUserName(emp.getUserName()==null?"":emp.getUserName());
                }
                blacklist.setStatus(KnMeetingBlacklist.MeetingStatus.LOCK);
                ms.SaveKnMeetingBlacklist(blacklist);
                rs=new RestStatus(false);
                rs.setErrorMessage("你已被锁定，不能申请会议");
            }else{
                rs=new RestStatus(true);
                rs.setErrorMessage("你申请会议室已空闲"+num+"次");
            }
        }else{
            if(blacklist.getStatus().equals(KnMeetingBlacklist.MeetingStatus.UNLOCK)){
                num=ms.ReadMeetingRule(Users.id(),blacklist.getLockTime());
                if(num>=3){
                    blacklist.setLockTime(new Date().getTime());
                    blacklist.setStatus(KnMeetingBlacklist.MeetingStatus.LOCK);
                    ms.SaveKnMeetingBlacklist(blacklist);
                    rs=new RestStatus(false);
                    rs.setErrorMessage("你已被锁定，不能申请会议");
                }else{
                    rs=new RestStatus(true);
                    rs.setErrorMessage("你申请会议室已空闲"+num+"次");
                }
            }else{
                rs=new RestStatus(false);
                rs.setErrorMessage("你已被锁定，不能申请会议");
            }
        }
        return rs;
    }
    /**
     * 获取除登录人员得所有员工
     *
     * @param pageNo   页码 0表示第一页，默认值为0
     * @param pageSize 每页显示数，默认为10条数据
     *                 <pre>
     *                                                                 接口方法：/api/v1/meeting/employee-list/?p=0&s=10
     *                                                                 成功返回值：
     *                                                                 {
     *                                                                     "status" : true,
     *                                                                     "errorCode" : null,
     *                                                                     "errorMessage" : null,
     *                                                                     "list" : [ {
     *                                                                         "id" : 2, //用户id
     *                                                                         "userName" : "段毅",//用户姓名
     *                                                                         "imageAddress" : null//头像地址
     *                                                                     }]
     *                                                                 }
     *                                                                 失败返回
     *                                                                 {
     *                                                                 "status" : false,
     *                                                                 "errorCode" : null,
     *                                                                 "errorMessage" : null,
     *                                                                 "list" : []
     *                                                                 }
     *                                                            </pre>
     */
    @RequestMapping(value="/employee-list", method={RequestMethod.GET})
    public ListDTO<MeetingPersonDTO> ReadEmployeeNot(@RequestParam(value="p", defaultValue="0") Integer pageNo,@RequestParam(value="s", defaultValue="10") Integer pageSize){
        List<KnEmployee> emps=os.PageEmployeeNot(Users.id(),pageNo,pageSize);
        List<MeetingPersonDTO> list=Lists.newArrayList();
        for(KnEmployee e : emps){
            MeetingPersonDTO dto=BeanMapper.map(e,MeetingPersonDTO.class);
            KnEmployeeOrganization o=os.ReadOrganization(e.getId());
            if(o!=null&&o.getId()!=null&&o.getId().getOrg()!=null){
                dto.setOrgId(o.getId().getOrg().getId());
                dto.setOrgName(o.getId().getOrg().getName());
            }
            list.add(dto);
        }
        ListDTO<MeetingPersonDTO> listDTO=new ListDTO<>(true,list);
        if(list==null||list.size()<=0){
            listDTO.setErrorMessage("无数据");
        }
        return listDTO;
    }
    /**
     * 获取所有部门
     *
     * @param pageNo   页码 0表示第一页，默认值为0
     * @param pageSize 每页显示数，默认为10条数据
     *                 <p/>
     *                 <pre>
     *                                                                 接口方法：/api/v1/meeting/org-list/?p=0&s=10
     *                                                                 成功返回值：
     *                                                                 {
     *                                                                     "status" : true,
     *                                                                     "errorCode" : null,
     *                                                                     "errorMessage" : null,
     *                                                                     "list" : [ {
     *                                                                         "id" : 1,//部门id
     *                                                                         "name" : "盈诺德"//部门名称
     *                                                                     }]
     *                                                                 }
     *                                                                 失败返回
     *                                                                 {
     *                                                                 "status" : false,
     *                                                                 "errorCode" : null,
     *                                                                 "errorMessage" : null,
     *                                                                 "list" : []
     *                                                                 }
     *                                                            </pre>
     */
    @RequestMapping(value="/org-list", method={RequestMethod.GET})
    public ListDTO<KnOrganizationDTO> ReadOrg(@RequestParam(value="p", defaultValue="0") Integer pageNo,@RequestParam(value="s", defaultValue="10") Integer pageSize){
        List<KnOrganization> orgs=os.PageKnOrganization(pageNo,pageSize);
        List<KnOrganizationDTO> list=Lists.newArrayList();
        for(KnOrganization o : orgs){
            KnOrganizationDTO dto=BeanMapper.map(o,KnOrganizationDTO.class);
            list.add(dto);
        }
        ListDTO<KnOrganizationDTO> listDTO=new ListDTO<>(true,list);
        if(list==null||list.size()<=0){
            listDTO.setErrorMessage("无数据");
        }
        return listDTO;
    }
    /**
     * 获取提前预定会议室时间。例如：1周 、2周
     *
     * @return 天数
     */
    @RequestMapping(value="/reservation", method={RequestMethod.GET})
    public DetailDTO<Integer> reservation(){
        KnMeetingRule rule=ms.ReadKnMeetingRule();//获取会议规则
        Integer num;
        if(rule!=null&&rule.getScheduleWeek()!=null){
            num=rule.getScheduleWeek()*7;
        }else{
            num=7;
        }
        return new DetailDTO<>(true,num);
    }
    private ListDTO<KnMeetingSignInDTO> listKnMeetingSignInDTO(List<KnMeetingSignIn> knmsis){
        List<KnMeetingSignInDTO> list=Lists.newArrayList();
        for(KnMeetingSignIn km : knmsis){
            KnMeetingSignInDTO dto=new KnMeetingSignInDTO();
            dto.setUserId(km.getId().getUserId());
            dto.setUserName(km.getUserName());
            dto.setTime(new DateTime(km.getSignTime()).toString("yyyy-MM-dd HH:mm"));
            list.add(dto);
        }
        ListDTO<KnMeetingSignInDTO> listDTO=new ListDTO<>(true,list);
        if(list.size()<=0||list.isEmpty()){
            listDTO.setErrorMessage("无数据");
        }
        return listDTO;
    }
    public static void main(String[] args){
        DateTime t=new DateTime(1417071600000l);
        //System.out.println(new DateTime(1417071600000l).toString("yyyy-MM-dd hh:ss"));
        System.out.println(t.secondOfMinute().addToCopy(60*15).getMillis());
        System.out.println(t.secondOfMinute().addToCopy(-60*15).getMillis());
    }
}
