package com.kingnode.health.rest;
import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.Lists;
import com.kingnode.diva.mapper.BeanMapper;
import com.kingnode.health.dto.KnEcmHealthDTO;
import com.kingnode.health.entity.KnEcmHealth;
import com.kingnode.health.entity.KnEcmHealthIcon;
import com.kingnode.health.entity.KnEcmHealthNumber;
import com.kingnode.health.entity.KnEcmHealthSet;
import com.kingnode.health.service.HealthService;
import com.kingnode.xsimple.entity.IdEntity;
import com.kingnode.xsimple.rest.DetailDTO;
import com.kingnode.xsimple.rest.ListDTO;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@RestController @RequestMapping({"/api/v1/health","/api/secure/v1/health"}) public class HealthRestController{
    private static org.slf4j.Logger log=LoggerFactory.getLogger(HealthRestController.class);
    @Autowired private HealthService hs;
    /**
     * 获取用户预约列表
     *
     * @param p 页码
     * @param s 条数
     *          <p>
     *          示例：http://192.168.15.18:8080/api/v1/health/list    post：｛"p":0,"s":10｝
     *          </p>
     *
     * @return {
     * "status" : true,
     * "errorCode" : null,
     * "errorMessage" : null,
     * "list" : [ {
     * "id" : 1,
     * "description" : "病症描述",  //病症描述
     * "waitingNumber" : "D20141222-0001",   //候诊号
     * "diagnoseResult" : "诊断结果",    //诊断结果
     * "diagnoseCost" : 90,    //诊断费用
     * "status" : "PROPOSER",    //状态(枚举) 取值:PROPOSER,WAITING,DIAGNOSE,ABANDON,OVERDUE
     * "doctor" : "张三",    //医生
     * "doctorPhone" : "12345678911",//医生电话
     * "statusStr" : "已申请",//状态(字符串) 取值:已申请,候诊,已诊断,已放弃,已逾期
     * "proposerTime" : "2014-12-22 08:10",//申请时间
     * "waitingTime" : "2014-12-22 08:10",//候诊时间
     * "diagnoseTime" : "2014-12-22 08:10" //就诊时间
     * "attachmentFlag": 1 ////附件标记,1.表示有附件
     * } ,
     * ....
     * ]}
     */
    @RequestMapping(value="/list", method={RequestMethod.POST})
    public ListDTO<KnEcmHealthDTO> HealthList(@RequestParam(value="p", defaultValue="0") Integer p,@RequestParam(value="s", defaultValue="10") Integer s){
        List<KnEcmHealthDTO> dtoList=Lists.newArrayList();
        List<KnEcmHealth> list=hs.ListKnEcmHealth(p,s);
        for(KnEcmHealth obj : list){
            KnEcmHealthDTO dto=BeanMapper.map(obj,KnEcmHealthDTO.class);
            if(obj.getHealthIcons()!=null){//设置附件标识
                dto.setAttachmentFlag(obj.getHealthIcons().size());
            }
            dtoList.add(dto);
        }
        return new ListDTO<>(true,dtoList);
    }
    /**
     * 获取用户预约信息
     *
     * @param id 预约ID
     *           <p>
     *           示例：http://192.168.15.18:8080/api/v1/health/detail    post：｛"id":1｝
     *           </p>
     *
     * @return {
     * "status" : true,
     * "errorCode" : null,
     * "errorMessage" : null,
     * "list" : [{
     * "id" : 1,
     * "description" : "病症描述",  //病症描述
     * "waitingNumber" : "D20141222-0001",   //候诊号
     * "diagnoseResult" : "诊断结果",    //诊断结果
     * "diagnoseCost" : 90,    //诊断费用
     * "status" : "PROPOSER",    //状态(枚举) 取值:PROPOSER,WAITING,DIAGNOSE,ABANDON,OVERDUE
     * "healthIcons":["/uf/2014/10/056db29440f84191b88203d488e9ba90.jpg","/uf/2014/10/056db29440f84191b88203d488e9ba90.jpg"],  //图片信息
     * "doctor" : "张三",    //医生
     * "doctorPhone" : "12345678911",//医生电话
     * "statusStr" : "已申请",//状态(字符串) 取值:已申请,候诊,已诊断,已放弃,已逾期
     * "proposerTime" : "2014-12-22 08:10",//申请时间
     * "waitingTime" : "2014-12-22 08:10",//候诊时间
     * "diagnoseTime" : "2014-12-22 08:10" //就诊时间
     * }]
     * }
     */
    @RequestMapping(value="/detail", method={RequestMethod.POST}) public ListDTO<KnEcmHealthDTO> HealthDetail(@RequestParam(value="id") Long id){
        List<KnEcmHealthDTO> dtoList=Lists.newArrayList();
        KnEcmHealth obj=hs.ReadKnEcmHealth(id);
        if(obj!=null){
            KnEcmHealthDTO dto=BeanMapper.map(obj,KnEcmHealthDTO.class);
            //保存图片信息
            List<KnEcmHealthIcon> icons=obj.getHealthIcons();
            List<String> list=Lists.newArrayList();
            if(icons!=null&&icons.size()>0){
                for(KnEcmHealthIcon icon : icons){
                    list.add(icon.getIcon());
                }
                dto.setIcons(list);
            }
            dtoList.add(dto);
        }
        return new ListDTO<>(true,dtoList);
    }
    /**
     * 预约
     *
     * @param description 描述
     * @param phone       用户电话
     * @param icons       图片集合
     *                    <p>
     *                    示例：http://192.168.15.18:8080/api/v1/health/appoint
     *                    post：｛
     *                    "description":"描述信息",
     *                    "phone":"13811111111",
     *                    "icons":[
     *                    "/uf/2014/10/056db29440f84191b88203d488e9ba90.jpg",
     *                    "/uf/2014/10/056db29440f84191b88203d488e9ba90.jpg",
     *                    ...
     *                    ]
     *                    ｝
     *                    </p>
     *
     * @return {
     * "status":true,
     * "errorCode":null,
     * "errorMessage":null,
     * "detail":"true"
     * }
     */
    @RequestMapping(value="/appoint", method={RequestMethod.POST})
    public DetailDTO<String> appoint(@RequestParam(value="description") String description,@RequestParam(value="phone") String phone,@RequestParam(value="icons") String icons){
        DetailDTO<String> detailDTO=new DetailDTO<>();
        //验证是否已在申请队列中


        KnEcmHealth health=new KnEcmHealth();
        health.setDescription(description);
        health.setPhone(phone);
        //保持图片附件
        if(StringUtils.isNotBlank(icons)&&!"[]".equals(icons)){
            icons=icons.replace("[","").replace("]","");
            String[] list=icons.split(",");
            /*ObjectMapper mapper=new ObjectMapper();
            JavaType javaType=mapper.getTypeFactory().constructParametricType(List.class,String.class);
            List<String> list=JsonMapper.nonEmptyMapper().fromJson(icons,javaType);*/
            List<KnEcmHealthIcon> iconSet=new ArrayList<KnEcmHealthIcon>();
            for(String str : list){
                if(StringUtils.isBlank(str.trim())){
                    continue;
                }
                KnEcmHealthIcon icon=new KnEcmHealthIcon();
                icon.setIcon(str);
                iconSet.add(icon);
            }
            if(iconSet.size()>0){
                health.setHealthIcons(iconSet);
            }
        }
        health.setEmployee(hs.ReadKnEmployee());
        health.setStatus(KnEcmHealth.HealthStatus.PROPOSER);
        health.setProposerDate(DateTime.now().getMillis());
        //医生及医生电话
        KnEcmHealthSet set=hs.ReadKnEcmHealthSet();
        if(set!=null){
            health.setDoctor(set.getDoctor());
            health.setDoctorPhone(set.getDoctorPhone());
        }
        hs.SaveKnEcmHealth(health);
        detailDTO.setStatus(true);
        detailDTO.setDetail((health.getId()!=null&&health.getId()>0)+"");
        return detailDTO;
    }
    /**
     * 取消预约
     *
     * @param id 预约ID
     *           <p>
     *           示例：http://192.168.12.249:8080/api/v1/health/abandon    post：｛"id":1｝
     *           </p>
     *
     * @return {
     * "status":true,
     * "errorCode":null,
     * "errorMessage":null,
     * "detail":"true"
     * }
     */
    @RequestMapping(value="/abandon", method={RequestMethod.POST}) public DetailDTO<String> abandon(@RequestParam(value="id") Long id){
        DetailDTO<String> detailDTO=new DetailDTO<>();
        KnEcmHealth health=hs.ReadKnEcmHealth(id);
        health.setStatus(KnEcmHealth.HealthStatus.ABANDON);
        hs.SaveKnEcmHealth(health);
        //设置排号对象为无效
        KnEcmHealthNumber healthNumber=hs.ReadHealthNumberByHealthId(health.getId());
        if(healthNumber!=null){
            healthNumber.setActive(IdEntity.ActiveType.DISABLE);
            hs.SaveKnEcmHealthNumber(healthNumber);
        }
        detailDTO.setStatus(true);
        detailDTO.setDetail((health.getStatus()==KnEcmHealth.HealthStatus.ABANDON)+"");
        return detailDTO;
    }
}
