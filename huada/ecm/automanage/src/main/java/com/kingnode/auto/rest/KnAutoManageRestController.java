package com.kingnode.auto.rest;
import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingnode.auto.dto.ApplyAutoDTO;
import com.kingnode.auto.dto.AutoBorrowParamDTO;
import com.kingnode.auto.dto.KnAutoDTO;
import com.kingnode.auto.dto.KnTrafficAccidentDTO;
import com.kingnode.auto.dto.ReturnListDTO;
import com.kingnode.auto.entity.KnAuto;
import com.kingnode.auto.entity.KnAutoBorrow;
import com.kingnode.auto.entity.KnTrafficAccident;
import com.kingnode.auto.service.KnAutoManageService;
import com.kingnode.diva.mapper.BeanMapper;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.xsimple.rest.DetailDTO;
import com.kingnode.xsimple.rest.ListDTO;
import com.kingnode.xsimple.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@RestController @RequestMapping({"/api/v1/automanage","/api/secure/v1/automanage"})
public class KnAutoManageRestController{
    private static final Logger logger=LoggerFactory.getLogger(KnAutoManageRestController.class);
    @Autowired
    private KnAutoManageService as;
    /**
     * 车辆列表
     * 接口的请求方式: post
     * 接口方法：http://localhost:8080/api/v1/automanage//auto/list/0/10
     * 参数：
     * @param pageNo   分页车辆的页码
     * @param pageSize 分页车辆每页的数量
     * @param state    APPLY,LEND,RESTORE,SCRAPPING中的一种,不填时或者无效参数是表示查询所有

                                                                          成功返回值：
                                                                          {
                                                                              "status" : true,
                                                                              "errorCode" : null,
                                                                              "errorMessage" : null,
                                                                                "totalSize":1,
                                                                              "list" :[{
                                                                                    "id":2,
                                                                                   "name":"1111",//车辆名称
                                                                                    "photo":"/upload/employee/photo/eb2cb5a5-dc5b-4f2b-ae97-b826906eb0c9.png",//车车辆图片
                                                                                    "brand":null,//品牌
                                                                                    "model":null,//型号
                                                                                    "plateNumber":"111",//车牌号码
                                                                                    "seating":10,//座位数
                                                                                    "structure":"汽车",//车型（两厢/三厢/MPV/SUV/跑车/敞篷车/旅行轿车/货车/客车/皮卡）
                                                                                    "engine":"11",//发动机(1.6L 2.0T)
                                                                                    "count":3,//出险次数
                                                                                    "totalMileage":1111,//总里程
                                                                                    "transmission":"MANUAL",//变速箱(手动/自动／手自一体)
                                                                                    "remark":"111111",//备注
                                                                                    "autoState":"RESTORE"//接车状态
                                                                                    }]
                                                                          }
                                                                          失败返回值：
                                                                          {
                                                                              "status" : false,
*                                                                              "errorCode" : “”,
*                                                                              "errorMessage" : null,
*                                                                              "list" :[]
*                                                                          }
     *                                          </pre>
     */
    @RequestMapping(value="/auto/list",method={RequestMethod.POST})
    public ReturnListDTO<KnAuto> autoList(@RequestParam(value="p",defaultValue="0") Integer pageNo,@RequestParam(value="s",defaultValue="10") Integer pageSize,@RequestParam(value="state") String state){
        KnAuto.AutoState stateObj=null;
        if(!Utils.isEmptyStr(state)){
            try{
                stateObj=KnAuto.AutoState.valueOf(state);
            }catch(Exception e){
                logger.error("state参数无效");
            }
        }
        ReturnListDTO list=new ReturnListDTO<KnAuto>();
        Page<KnAuto> page=as.ListKnAuto(stateObj,pageNo,pageSize);
        list.setList(page.getContent());
        list.setTotalSize(page.getTotalElements());
        list.setStatus(true);
        return list;
    }
    /**
     * 预定定车辆,必须考虑车子的状态
     * 接口的请求方式: post
     * 接口方法：http://localhost:8080/api/v1/automanage/auto/reserve
     * 参数:dto
     *                                                      成功返回值：
     *                                                      {
     *                                                           "status" : true,
     *                                                           "errorCode" : null,
     *                                                           "errorMessage" : null,
     *                                                            "totalSize":1,
     *                                                           "list":{
     *                                                                    "id":1,
     *                                                                    "updateTime":1412850552588,
     *                                                                    "ka":{"id":1,
     *                                                                    "name":"11111",//名称
     *                                                                    "photo":"/upload/employee/photo/b436a4a1-cf9f-47d2-b8e9-3fc8610fbed3.png","brand":null,"model":null,"plateNumber":"1111","seating":111,"structure":"汽车","engine":"1111","count":5,"totalMileage":1111,"transmission":"MANUAL","remark":"1111","autoState":"RESTORE","createTimeStr":"2014-10-08 10:16:24"},
     *                                                                    "departName":null,//部门
     *                                                                    "workNo":"",//工号
     *                                                                    "drivingNo":"",//驾驶证号
     *                                                                    "driverYear":"",//驾龄
     *                                                                    "driver":null,//驾驶员对象
     *                                                                    "userId":2,//借车人ID,对应employee id
     *                                                                    "name":"duanyi",//借车人姓名
     *                                                                    "ridingNumber":100,//乘车人数
     *                                                                    "destination":"武汉",//目的地
     *                                                                   "applicationDate":1412843831514,//预借时间
     *                                                                   "approvalDate":1412843831514,//审批时间
     *                                                                   "rejectDate":null,//拒绝时间
     *                                                                   "deselectDate":null,//取消时间
     *                                                                   "mileage":100,//预计里程
     *                                                                   "lendDate":1412843831513,//车子预定使用开始时间
     *                                                                   "restoreDate":null,//车子预定结束时间
     *                                                                   "lendMileage":null,//车子预定结束时间
     *                                                                   "restoreMileage":null,//还车里程
     *                                                                   "lendGauge":null,//起始测量
     *                                                                   "restoreGauge":null,//还车测量
     *                                                                   "lendAutoState":null,//借出车时车辆状态
     *                                                                   "restoreAutoState":null,//还车时车辆状态
     *                                                                   "shouldRestoreDate":null,//实际归还时间
     *                                                                   "driving":null,//领取行驶证
     *                                                                   "key":null,//领取钥匙
     *                                                                   "rechargeable":null,//加油卡
     *                                                                   "cause":"商务用车",//用车事由
     *                                                                   "bt":"DESELECT",//订单状态
     *                                                           }
     *                                                      }
     *                                                      失败返回值：
     *                                                      {
     *                                                          "status" : false,
     *                                                          "errorCode" : “”,
     *                                                          "errorMessage" : null,
     *                                                          "list" :[]
     *                                                      }
     *                            </pre>
     */
    @RequestMapping(value="/auto/reserve",method={RequestMethod.POST})
    public DetailDTO<KnAutoBorrow> reserveAuto(ApplyAutoDTO dto){
        //  ApplyAutoDTO dto=JsonMapper.nonEmptyMapper().fromJson(params,ApplyAutoDTO.class);
        return as.ApplyAuto(dto);
    }
    /**
     * 获取指定用户的车辆申请单
     * 接口的请求方式: get
     * 接口方法：http://localhost:8080/api/v1/automanage/auto/list-autoborrow
       参数：
         * @param pageNo    公务用车申请单的页码,起始值为1
         * @param pageSize  公务用车每页返回的数据数量大小
         * @param userId    用户Id,对应员工Id
         * @param stateList 公务用车申请单状态类型,如APPLY,APPROVAL,REJECT,DESELECT,LEND,RESTORE,OVERTIME,TIMEOUT
     *                                                                              成功返回值：
     *                                                                              {
     *                                                                                   "status" : true,
     *                                                                                   "errorCode" : null,
     *                                                                                   "errorMessage" : null,
     *                                                                                    "totalSize":1,
     *                                                                                   "list":{
     *                                                                                           "id":1,
                     *                                                                    "updateTime":1412850552588,
                     *                                                                    "ka":{"id":1,
                     *                                                                    "name":"11111",//名称
                     *                                                                    "photo":"/upload/employee/photo/b436a4a1-cf9f-47d2-b8e9-3fc8610fbed3.png","brand":null,"model":null,"plateNumber":"1111","seating":111,"structure":"汽车","engine":"1111","count":5,"totalMileage":1111,"transmission":"MANUAL","remark":"1111","autoState":"RESTORE","createTimeStr":"2014-10-08 10:16:24"},
                     *                                                                    "departName":null,//部门
                     *                                                                    "workNo":"",//工号
                     *                                                                    "drivingNo":"",//驾驶证号
                     *                                                                    "driverYear":"",//驾龄
                     *                                                                    "driver":null,//驾驶员对象
                     *                                                                    "userId":2,//借车人ID,对应employee id
                     *                                                                    "name":"duanyi",//借车人姓名
                     *                                                                    "ridingNumber":100,//乘车人数
                     *                                                                    "destination":"武汉",//目的地
                     *                                                                   "applicationDate":1412843831514,//预借时间
                     *                                                                   "approvalDate":1412843831514,//审批时间
                     *                                                                   "rejectDate":null,//拒绝时间
                     *                                                                   "deselectDate":null,//取消时间
                     *                                                                   "mileage":100,//预计里程
                     *                                                                   "lendDate":1412843831513,//车子预定使用开始时间
                     *                                                                   "restoreDate":null,//车子预定结束时间
                     *                                                                   "lendMileage":null,//车子预定结束时间
                     *                                                                   "restoreMileage":null,//还车里程
                     *                                                                   "lendGauge":null,//起始测量
                     *                                                                   "restoreGauge":null,//还车测量
                     *                                                                   "lendAutoState":null,//借出车时车辆状态
                     *                                                                   "restoreAutoState":null,//还车时车辆状态
                     *                                                                   "shouldRestoreDate":null,//实际归还时间
                     *                                                                   "driving":null,//领取行驶证
                     *                                                                   "key":null,//领取钥匙
                     *                                                                   "rechargeable":null,//加油卡
                     *                                                                   "cause":"商务用车",//用车事由
                     *                                                                   "bt":"DESELECT",//订单状态
     *                                                                                   }
     *                                                                              }
     *
     *                                                                              失败返回值：
     *                                                                              {
     *                                                                                  "status" : false,
     *                                                                                  "errorCode" : “”,
     *                                                                                  "errorMessage" : null,
     *                                                                                  "list" :[]
     *                                                                              }
     *                                                                              </pre>
     */
    @RequestMapping(value="/auto/list-autoborrow",method={RequestMethod.GET})
    public ReturnListDTO<KnAutoBorrow> listAutoBorrow(@RequestParam(value="p",defaultValue="0") Integer pageNo,@RequestParam(value="s",defaultValue="10") Integer pageSize,@RequestParam(value="userId") Long userId,@RequestParam(value="stateList") String stateList){
        ObjectMapper mapper=new ObjectMapper();
        JavaType javaType=mapper.getTypeFactory().constructParametricType(List.class,KnAutoBorrow.BorrowType.class);
        List<KnAutoBorrow.BorrowType> stateListObj=JsonMapper.nonEmptyMapper().fromJson(stateList,javaType);
        AutoBorrowParamDTO dto=new AutoBorrowParamDTO();
        dto.setPageNo(pageNo);
        dto.setPageSize(pageSize);
        dto.setUserId(userId);
        dto.setStateList(stateListObj);
        ReturnListDTO returnList=new ReturnListDTO<KnAutoBorrow>();
        Page<KnAutoBorrow> page=as.PageKnAutoBorrow(dto);
        returnList.setList(page.getContent());
        returnList.setTotalSize(page.getTotalElements());
        returnList.setStatus(true);
        return returnList;
    }
    /**
     * 退订
     * 接口的请求方式: get
     * 接口方法：http://localhost:8080/api/v1/automanage/auto/unsubscribe-auto?id=1
       参数:{"transmission":['AUTO']
     *                                                  成功返回值：
     *                                                  {
     *                                                       "status" : true,
     *                                                       "errorCode" : null,
     *                                                       "errorMessage" : null,
     *                                                      "detail":{"id":1,
     *                                                                    "updateTime":1412850552588,
     *                                                                    "ka":{"id":1,
     *                                                                    "name":"11111",//名称
     *                                                                    "photo":"/upload/employee/photo/b436a4a1-cf9f-47d2-b8e9-3fc8610fbed3.png","brand":null,"model":null,"plateNumber":"1111","seating":111,"structure":"汽车","engine":"1111","count":5,"totalMileage":1111,"transmission":"MANUAL","remark":"1111","autoState":"RESTORE","createTimeStr":"2014-10-08 10:16:24"},
     *                                                                    "departName":null,//部门
     *                                                                    "workNo":"",//工号
     *                                                                    "drivingNo":"",//驾驶证号
     *                                                                    "driverYear":"",//驾龄
     *                                                                    "driver":null,//驾驶员对象
     *                                                                    "userId":2,//借车人ID,对应employee id
     *                                                                    "name":"duanyi",//借车人姓名
     *                                                                    "ridingNumber":100,//乘车人数
     *                                                                    "destination":"武汉",//目的地
     *                                                                   "applicationDate":1412843831514,//预借时间
     *                                                                   "approvalDate":1412843831514,//审批时间
     *                                                                   "rejectDate":null,//拒绝时间
     *                                                                   "deselectDate":null,//取消时间
     *                                                                   "mileage":100,//预计里程
     *                                                                   "lendDate":1412843831513,//车子预定使用开始时间
     *                                                                   "restoreDate":null,//车子预定结束时间
     *                                                                   "lendMileage":null,//车子预定结束时间
     *                                                                   "restoreMileage":null,//还车里程
     *                                                                   "lendGauge":null,//起始测量
     *                                                                   "restoreGauge":null,//还车测量
     *                                                                   "lendAutoState":null,//借出车时车辆状态
     *                                                                   "restoreAutoState":null,//还车时车辆状态
     *                                                                   "shouldRestoreDate":null,//实际归还时间
     *                                                                   "driving":null,//领取行驶证
     *                                                                   "key":null,//领取钥匙
     *                                                                   "rechargeable":null,//加油卡
     *                                                                   "cause":"商务用车",//用车事由
     *                                                                   "bt":"DESELECT",//订单状态
     *                                                       }
     *                                                  }
     *
     *                                                  失败返回值：
     *                                                  {
     *                                                      "status" : false,
     *                                                      "errorCode" : “”,
     *                                                      "errorMessage" : null,
     *                                                      "detail" :[]
     *                                                  }
     *                                                  </pre>
     */
    @RequestMapping(value="/auto/unsubscribe-auto",method={RequestMethod.GET})
    public DetailDTO<KnAutoBorrow> unsubscribeAuto(@RequestParam(value="id") Long id){
        return as.UnsubscribeAuto(id);
    }
    /**
     * 查询能预定车辆
     * 接口的请求方式: get
     * 接口方法：http://localhost:8080/api/v1/automanage/auto/cansubscribe-list
       参数：
         * @param fromtime 开始时间
         * @param endtime  结束时间
     *                 <pre>
     *                                                                          成功返回值：
     *                                                                          {
     *                                                                               "status" : true,
     *                                                                               "errorCode" : null,
     *                                                                               "errorMessage" : null,
     *                                                                               "list" :[{
     *                                                                                     "id":2,
     *                                                                                    "name":"1111",//名称
     *                                                                                    "photo":"/upload/employee/photo/eb2cb5a5-dc5b-4f2b-ae97-b826906eb0c9.png",//车车辆图片
     *                                                                                    "brand":null,//品牌
     *                                                                                    "model":null,//型号
     *                                                                                    "plateNumber":"111",//车牌号码
     *                                                                                    "seating":10,//座位数
     *                                                                                    "structure":"汽车",//车型（两厢/三厢/MPV/SUV/跑车/敞篷车/旅行轿车/货车/客车/皮卡）
     *                                                                                    "engine":"11",//发动机(1.6L 2.0T)
     *                                                                                    "count":3,//出险次数
     *                                                                                    "totalMileage":1111,//总里程
     *                                                                                    "transmission":"MANUAL",//变速箱(手动/自动)
     *                                                                                    "remark":"111111",//备注
     *                                                                                    "autoState":"RESTORE"//接车状态 预借
     *                                                                                    }]
     *                                                                          }
     *
     *                                                                          失败返回值：
     *                                                                          {
     *                                                                              "status" : false,
     *                                                                              "errorCode" : “”,
     *                                                                              "errorMessage" : null,
     *                                                                              "list" :[]
     *                                                                          }
     *                                                                          </pre>
     */
    @RequestMapping(value="/auto/cansubscribe-list",method={RequestMethod.GET})
    public ListDTO<KnAutoDTO> CanSubscribeList(@RequestParam(value="fromtime") Long fromtime,@RequestParam(value="endtime") Long endtime,@RequestParam(value="transmissions") String transmissions){
        ObjectMapper mapper=new ObjectMapper();
        JavaType javaType=mapper.getTypeFactory().constructParametricType(List.class,KnAuto.Transmission.class);
        List<KnAuto.Transmission> transmissionsListObj=JsonMapper.nonEmptyMapper().fromJson(transmissions,javaType);
        ListDTO<KnAutoDTO> listDto=new ListDTO();
        if(endtime>fromtime){
            List<KnAuto> list=as.CanSubscribeList(fromtime,endtime,transmissionsListObj);
            List<KnAutoDTO> dtoList=BeanMapper.mapList(list,KnAutoDTO.class);
            listDto.setStatus(true);
            listDto.setList(dtoList);
        }else{
            listDto.setStatus(false);
            listDto.setErrorMessage("参数输入错误,应该fromtime<endtime");
        }
        return listDto;
    }
    /**
     * 车辆预定时间情况
     * 接口的请求方式: get
     * 接口方法：http://localhost:8080/api/v1/automanage/auto/timelist
     * 参数：
         * @param date  查询日期
         * @param p 页码
         * @param s 每页数量
         * @param transmissions 汽车变速箱类型
    成功返回值：
     *                                                                          {
     *                                                                               "status" : true,
     *                                                                               "errorCode" : null,
     *                                                                               "errorMessage" : null,
     *                                                                               "list" :[{
     *                                                                                     "id":2,
     *                                                                                    "name":"1111",//名称
     *                                                                                    "photo":"/upload/employee/photo/eb2cb5a5-dc5b-4f2b-ae97-b826906eb0c9.png",//车车辆图片
     *                                                                                    "brand":null,//品牌
     *                                                                                    "model":null,//型号
     *                                                                                    "plateNumber":"111",//车牌号码
     *                                                                                    "seating":10,//座位数
     *                                                                                    "structure":"汽车",//车型（两厢/三厢/MPV/SUV/跑车/敞篷车/旅行轿车/货车/客车/皮卡）
     *                                                                                    "engine":"11",//发动机(1.6L 2.0T)
     *                                                                                    "count":3,//出险次数
     *                                                                                    "totalMileage":1111,//总里程
     *                                                                                    "transmission":"MANUAL",//变速箱(手动/自动)
     *                                                                                    "remark":"111111",//备注
     *                                                                                    "autoState":"RESTORE"//接车状态 预借
     *                                                                                    }]
     *                                                                          }
     *
     *                                                                          失败返回值：
     *                                                                          {
     *                                                                              "status" : false,
     *                                                                              "errorCode" : “”,
     *                                                                              "errorMessage" : null,
     *                                                                              "list" :[]
     *                                                                          }
     *                                                                          </pre>
     */
    @RequestMapping(value="/auto/timelist",method={RequestMethod.GET})
    public ListDTO<KnAutoDTO> TimeList(@RequestParam(value="date") String date,@RequestParam(value="p",defaultValue="0") int p,@RequestParam(value="s",defaultValue="100") int s,@RequestParam(value="transmissions") String transmissions){
        ListDTO<KnAutoDTO> listDto=new ListDTO();
        listDto.setList(as.FindKnAutos(date,p,s,transmissions));
        listDto.setStatus(true);
        return listDto;
    }

    /**
     * 根据变速箱种类获取符合条件的车辆
     * 接口的请求方式: get
     * 接口方法：http://localhost:8080/api/v1/automanage/auto/find-atuo-transmission
     * 参数：
     *      @param transmissions MANUAL,AUTO,MIX中的一种或者集中组合
     *                                                                                             成功返回值：
     *                                                                          {
     *                                                                               "status" : true,
     *                                                                               "errorCode" : null,
     *                                                                               "errorMessage" : null,
     *                                                                               "list" :[{
     *                                                                                     "id":2,
     *                                                                                    "name":"1111",//名称
     *                                                                                    "photo":"/upload/employee/photo/eb2cb5a5-dc5b-4f2b-ae97-b826906eb0c9.png",//车车辆图片
     *                                                                                    "brand":null,//品牌
     *                                                                                    "model":null,//型号
     *                                                                                    "plateNumber":"111",//车牌号码
     *                                                                                    "seating":10,//座位数
     *                                                                                    "structure":"汽车",//车型（两厢/三厢/MPV/SUV/跑车/敞篷车/旅行轿车/货车/客车/皮卡）
     *                                                                                    "engine":"11",//发动机(1.6L 2.0T)
     *                                                                                    "count":3,//出险次数
     *                                                                                    "totalMileage":1111,//总里程
     *                                                                                    "transmission":"MANUAL",//变速箱(手动/自动)
     *                                                                                    "remark":"111111",//备注
     *                                                                                    "autoState":"RESTORE"//接车状态 预借
     *                                                                                    }]
     *                                                                          }
     *
     *                                                                          失败返回值：
     *                                                                          {
     *                                                                              "status" : false,
     *                                                                              "errorCode" : “”,
     *                                                                              "errorMessage" : null,
     *                                                                              "list" :[]
     *                                                                          }
     *                                                                          </pre>
     */
    @RequestMapping(value="/auto/find-atuo-transmission",method={RequestMethod.GET})
    public ListDTO<KnAutoDTO> findAutoByransmission(@RequestParam(value="transmission") String transmissions){
        ObjectMapper mapper=new ObjectMapper();
        JavaType javaType=mapper.getTypeFactory().constructParametricType(List.class,KnAuto.Transmission.class);
        List<KnAuto.Transmission> stateListObj=JsonMapper.nonEmptyMapper().fromJson(transmissions,javaType);
        List<KnAuto> list=as.QueryByTransmission(stateListObj);
        ListDTO<KnAutoDTO> listDto=new ListDTO();
        List<KnAutoDTO> dtoList=BeanMapper.mapList(list,KnAutoDTO.class);
        listDto.setStatus(true);
        listDto.setList(dtoList);
        return listDto;
    }
    /**
     * 获取用户是否有过借车记录
     * 接口的请求方式: get
     * 接口方法：http://localhost:8080/api/v1/automanage/auto/has-autoborrow
     * 参数：
     *      @param id 对应employee Id
     */
    @RequestMapping(value="/auto/has-autoborrow",method={RequestMethod.GET})
    public DetailDTO<String> hasAutoBorrow(@RequestParam(value="id") Long id){
        DetailDTO<String> dto = new DetailDTO<String>();
        dto.setStatus(true);
        dto.setDetail(as.hasAutoBorrow(id)+"");
        return dto;
    }
    /**
     * 获取一个车辆车险列表
     * 接口的请求方式: get
     * 接口方法：http://localhost:8080/api/v1/automanage/auto/list-atuo-accident?autoId=2
       参数：
     *  @param autoId 车辆ID
     *               <pre>
     *                                                                  成功返回值：
     *                                                                  {
     *                                                                       "status" : true,
     *                                                                       "errorCode" : null,
     *                                                                       "errorMessage" : null,
     *                                                                       "list" : [{"id":67,
     *                                                                            "ka":{
     *                                                                             "id":2,
*                                                                                    "name":"1111",//名称
*                                                                                    "photo":"/upload/employee/photo/eb2cb5a5-dc5b-4f2b-ae97-b826906eb0c9.png",//车车辆图片
*                                                                                    "brand":null,//品牌
*                                                                                    "model":null,//型号
*                                                                                    "plateNumber":"111",//车牌号码
*                                                                                    "seating":10,//座位数
*                                                                                    "structure":"汽车",//车型（两厢/三厢/MPV/SUV/跑车/敞篷车/旅行轿车/货车/客车/皮卡）
*                                                                                    "engine":"11",//发动机(1.6L 2.0T)
*                                                                                    "count":3,//出险次数
*                                                                                    "totalMileage":1111,//总里程
*                                                                                    "transmission":"MANUAL",//变速箱(手动/自动)
*                                                                                    "remark":"111111",//备注
*                                                                                    "autoState":"RESTORE"//接车状态 预借
     *                                                                            },
     *                                                                            "kaId":2,//出险id
     *                                                                            "name":"11",//出险姓名
     *                                                                            "occurTime":"2014-10-23",//发生时间
     *                                                                            "occurAddress":"111",//发生地点
     *                                                                            "description":"111"//事故描述
     *                                                                            }
     *                                                                       }]
     *                                                                  }
     *                                                                  失败返回值：
     *                                                                  {
     *                                                                      "status" : true,
     *                                                                      "errorCode" : “”,
     *                                                                      "errorMessage" : null,
     *                                                                      "list" :[]
     *                                                                  }
     *                                                     </pre>
     */
    @RequestMapping(value="/auto/list-atuo-accident",method={RequestMethod.GET})
    public ListDTO<KnTrafficAccidentDTO> ListAccident(@RequestParam(value="autoId") Long autoId){
        ListDTO<KnTrafficAccidentDTO> listDto=new ListDTO();
        List<KnTrafficAccident> list=as.ListKnTrafficAccident(new Sort(Sort.Direction.DESC,"occurTime"),autoId);
        List<KnTrafficAccidentDTO> dtoList=BeanMapper.mapList(list,KnTrafficAccidentDTO.class);
        listDto.setStatus(true);
        listDto.setList(dtoList);
        return listDto;
    }
}