package com.kingnode.xsimple.service.safety;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.common.base.Strings;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.dao.application.KnVersionInfoDao;
import com.kingnode.xsimple.dao.push.KnCertificateDao;
import com.kingnode.xsimple.dao.push.KnDeviceInfoDao;
import com.kingnode.xsimple.dao.push.KnMessageDao;
import com.kingnode.xsimple.entity.application.KnApplicationInfo;
import com.kingnode.xsimple.entity.application.KnVersionInfo;
import com.kingnode.xsimple.entity.push.KnCertificateInfo;
import com.kingnode.xsimple.entity.push.KnDeviceInfo;
import com.kingnode.xsimple.entity.push.KnPushMessageInfo;
import com.kingnode.xsimple.rest.DetailDTO;
import com.kingnode.xsimple.util.Utils;
import com.kingnode.xsimple.util.push.IosPushUtil;
import com.kingnode.xsimple.util.version.VersionNumUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author dengfeng@kingnode.com (dengfeng)
 */
@Component @Transactional(readOnly=true)
public class MessageService {

    private static Logger logger=LoggerFactory.getLogger(MessageService.class);
    private KnMessageDao messDao ;
    private KnVersionInfoDao kverDao ;
    private KnDeviceInfoDao kaciDao ;
    private KnCertificateDao knCertificateDao;

    List<Setting.WorkStatusType> workStatList = Arrays.asList(Setting.WorkStatusType.usable,Setting.WorkStatusType.test);

    @Autowired
    public void setMessDao(KnMessageDao messDao) {
        this.messDao = messDao;
    }

    @Autowired
    public void setKverDao(KnVersionInfoDao kverDao){
        this.kverDao=kverDao;
    }

    @Autowired
    public void setKaciDao(KnDeviceInfoDao kaciDao){
        this.kaciDao=kaciDao;
    }

    @Autowired
    public void setKnCertificateDao(KnCertificateDao knCertificateDao){
        this.knCertificateDao=knCertificateDao;
    }

    /**
     *
     * 推送消息查询分页
     * @param dt
     * @return
     */
    public DataTable<KnPushMessageInfo> SearchList(DataTable<KnPushMessageInfo> dt,final Map<String,Object> searchParams){
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),getSort(dt));
        Page<KnPushMessageInfo> list=messDao.findAll(new Specification<KnPushMessageInfo>(){
            @Override public Predicate toPredicate(Root<KnPushMessageInfo> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                Root<KnDeviceInfo> kr=query.from(KnDeviceInfo.class);
                expressions.add(cb.equal(root.<KnDeviceInfo>get("deviceInfo"),kr.<Long>get("id")));
                if(searchParams!=null&&searchParams.size()!=0){
                    //接收者
                    if(searchParams.containsKey("LIKE_deviceInfo.loginName")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_deviceInfo.loginName").toString().trim())){
                        expressions.add(cb.like(cb.upper(kr.<String>get("loginName")),"%"+searchParams.get("LIKE_deviceInfo.loginName").toString().trim().toUpperCase()+"%"));
                    }
                    //标题
                    if(searchParams.containsKey("LIKE_title")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_title").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("title")),"%"+searchParams.get("LIKE_title").toString().trim().toUpperCase()+"%"));
                    }
                    //内容
                    if(searchParams.containsKey("LIKE_content")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_content").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("content")),"%"+searchParams.get("LIKE_content").toString().trim().toUpperCase()+"%"));
                    }
                    //消息类型
                    if(searchParams.containsKey("EQ_messType")&&!Strings.isNullOrEmpty(searchParams.get("EQ_messType").toString().trim())){
                        expressions.add(cb.equal(root.<Setting.MessageType>get("messType"),Setting.MessageType.valueOf(searchParams.get("EQ_messType").toString())));
                    }
                    //所属系统
                    if(searchParams.containsKey("LIKE_fromSys")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_fromSys").toString().trim())){
                        expressions.add(cb.equal(root.<String>get("fromSys"),searchParams.get("LIKE_fromSys").toString()));
                    }
                    //消息状态
                    if(searchParams.containsKey("EQ_msgState")&&!Strings.isNullOrEmpty(searchParams.get("EQ_msgState").toString().trim())){
                        expressions.add(cb.equal(root.<Setting.MsgState>get("msgState"),Setting.MsgState.valueOf(searchParams.get("EQ_msgState").toString())));
                    }
                    //类别
                    if(searchParams.containsKey("EQ_plateMess")&&!Strings.isNullOrEmpty(searchParams.get("EQ_plateMess").toString().trim())){
                        expressions.add(cb.equal(root.<Setting.PlateformType>get("plateMess"),Setting.PlateformType.valueOf(searchParams.get("EQ_plateMess").toString())));
                    }
                    //设备名称
                    if(searchParams.containsKey("LIKE_deviceInfo.deviceName")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_deviceInfo.deviceName").toString().trim())){
                        expressions.add(cb.like(cb.upper(kr.<String>get("deviceName")),"%"+searchParams.get("LIKE_deviceInfo.deviceName").toString().trim().toUpperCase()+"%"));
                    }
                    //接收者
                    if(searchParams.containsKey("LIKE_token")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_token").toString().trim())){
                        expressions.add(cb.like(cb.upper(kr.<String>get("deviceToken")),"%"+searchParams.get("LIKE_token").toString().trim().toUpperCase()+"%"));
                    }
                }
                return predicate;
            }
        },pageRequest);
        dt.setiTotalDisplayRecords(list.getTotalElements());
        dt.setAaData(list.getContent());
        return dt;
    }

    @Transactional(readOnly=false)
    public void DeleteMessage(List<Long> ids) throws Exception{
        List<KnPushMessageInfo> list=(List<KnPushMessageInfo>)messDao.findAll(ids);
        messDao.delete(list);
    }

    public List<KnVersionInfo> FindVerListByWorkStats(){
        return  kverDao.findVerListByWorkStats( workStatList );
    }


    public StringBuffer FindDeviceByStats(){

        StringBuffer buf = new StringBuffer() ;

        List<Setting.DeleteStatusType> delStatList = new ArrayList<>();
        delStatList.add(Setting.DeleteStatusType.nodelete);
        List<KnDeviceInfo> channelList =  kaciDao.findDeviceByStats( delStatList );

        List<String> deviceList = new ArrayList<>() ;

        boolean flag = true ;
        String devicetoken = "##" ;
        for(KnDeviceInfo info : channelList){

            devicetoken = info.getDeviceToken()  ;
            if( deviceList.contains(devicetoken) ) continue ;

            if(flag && Setting.UPPERANDROIDPHONE.equalsIgnoreCase(info.getDeviceType().name()) ){ //集合是排序好的, 第一次让android进来一次

                flag = false ;
                buf.append("<optgroup label=\""+Setting.UPPERANDROIDPHONE+"设备\">") ;
            } else if(!flag && Setting.IPHONE.equalsIgnoreCase(info.getDeviceType().name())){ //此处只会走一次

                flag = true ;
                buf.append("</optgroup>") ;
                buf.append("<optgroup label=\""+Setting.IOSPHONE+"设备\">") ;
            }
            buf.append("<option value=\""+ devicetoken+"\" >"+info.getDeviceName()+"  (设备号:"+devicetoken+")</option>" ) ;

            deviceList.add( devicetoken ) ;
        }
        if(channelList.size()>0) buf.append("</optgroup>") ;

        return buf ;
    }


    /**
     *
     * @param messageType  发送类型  systemmes("系统消息"), clearuser("清除用户"),cleardevice("清除设备"), onlineupdate("在线更新");
     * @param cliearDevice 清除设备时 所选中的设备值
     * @param appversion 在线更新时 对应的应用版本
     * @param broadcast  发送范围   U_L 在线用户(android/ios) ；U 指定用户  ； R 指定角色
     * @param channelId  指定用户发送范围时  所选中的设备id
     * @param userinfo  指定用户发送范围时  用户名
     * @param roleinfo 指定角色发送范围时  角色id
     * @param iosmessage     IOS消息内容
     * @param title  消息标题
     * @param uri   消息uri
     * @param message  消息正文内容
     */
    @Transactional(readOnly=false)
    public Map SendMessage(String messageType
            ,String cliearDevice
            ,String appversion
            ,String broadcast
            ,String channelId ,String userinfo
            ,String roleinfo ,String iosmessage ,
            String title ,String uri ,String message){
        Map map = new HashMap<>() ;
        String tip = "找不到任何符合条件的设备" ;
        String newUrl = "" ,vf="",xtype="";
        List<KnDeviceInfo> list = new ArrayList<>() ;
        try{
            if(Setting.MessageType.systemmes.name().toString().equals(messageType)||Setting.MessageType.clearuser.name().toString().equals(messageType)){
                List<Long> chanIdsList=new ArrayList<>();
                //系统消息  //清除用户
                if("U_L".equals(broadcast)){
                    //在线用户
                    list=findAllByQuery(Setting.OnlineType.online.name().toString(),null,null,null,null,null,null);
                    for(KnDeviceInfo info : list){
                        chanIdsList.add(info.getId());
                    }
                }else if("U".equals(broadcast) && !Strings.isNullOrEmpty(channelId)){
                    // 指定用户
                    String[] arr=channelId.split(",");
                    for(String str : arr){
                        chanIdsList.add(Long.parseLong(str));
                    }
                    list=findAllByQuery(null,chanIdsList,null,null,null,null,null);
                }else if("R".equals(broadcast)){
                    //指定角色
                }
                if(Setting.MessageType.clearuser.name().toString().equals(messageType)){
                    //清除用户
                    uri = Setting.CLEARCLIENT ;
                    kaciDao.updateChannState( Setting.DeleteStatusType.account ,chanIdsList);
                }
            }else if(Setting.MessageType.cleardevice.name().toString().equals(messageType)){
                //清除设备
                List<String> deviceTokenList=new ArrayList<>();
                String[] arr=cliearDevice.split(",");
                for(String str : arr){
                    deviceTokenList.add(str);
                }
                list=findAllByQuery(null,null,deviceTokenList,null,null,null,null);
                kaciDao.updateChannDeivceState(Setting.DeleteStatusType.device,deviceTokenList);
                uri = Setting.CLEARDEVICE ;
            }else if(Setting.MessageType.onlineupdate.name().toString().equals(messageType)){
                //在线更新
                KnVersionInfo knVersionInfo= null ;
                if(!Utils.isEmptyString(channelId)){
                    //单个设备在线更新
                    KnDeviceInfo knDeviceInfo = kaciDao.findOne(Long.parseLong(channelId)) ;
                    list.add( knDeviceInfo );

                    List<KnVersionInfo> knVersionInfoList= kverDao.findVerListByAppIdAndWorkStats( knDeviceInfo.getAppId() ,workStatList) ;
                    knVersionInfo= VersionNumUtil.getMaxObjectByNum(knVersionInfoList) ;

                } else{
                    //所有在线设备进行在线更新
                    knVersionInfo=kverDao.findOne(Long.parseLong(appversion));
                    list=findAllByQuery(null,null,null,knVersionInfo.getApplicationInfo().getId(),Setting.OnlineType.online.name().toString(),Setting.DeleteStatusType.nodelete.name().toString(),knVersionInfo.getWorkStatus().name().toString());
                }

                if(!Utils.isEmptyString(uri)&&(uri.contains(".apk")||uri.contains(".plist"))){ //是apk或plist两种之一时
                    message=uri;
                }else if(Utils.isNotNull(knVersionInfo)){
                    list=VersionNumUtil.getChannelByNum(list,knVersionInfo.getNum(),false);
                    message=knVersionInfo.getAddress();
                    tip = "已是最新版本";
                } else {
                    list = null ;
                    tip = "找不到相应的版本信息";
                }
                newUrl = Utils.isEmptyString(knVersionInfo.getIosHttpsAddress())?"":knVersionInfo.getIosHttpsAddress() ;
                vf = Utils.isEmptyString(knVersionInfo.getForcedUpdate())?"0":knVersionInfo.getForcedUpdate() ;
                uri= Setting.UPGRADE + vf ;
            }
            if(Utils.isEmpityCollection(list)){
                map.put("stat",false);
                map.put(Setting.MESSAGE,tip);
                return map ;
            }
            boolean isSend = false;
            boolean isIphoneSend = false ;
            list = Utils.removeDuplicate(list);
            List<KnDeviceInfo> iphoneList=new ArrayList<>();
            Set<String> apiKeyList = new HashSet<>();  //应用appkey 集合
            List<KnDeviceInfo> androidList=new ArrayList<>();
            for(KnDeviceInfo knDeviceInfo : list){
                if(Setting.PlatformType.IPHONE.name().toString().equals(knDeviceInfo.getDeviceType().name().toString())){
                    iphoneList.add(knDeviceInfo);
                    apiKeyList.add(knDeviceInfo.getApiKey());
                    isIphoneSend = true ;
                }else if(Setting.PlatformType.ANDROID.name().toString().equals(knDeviceInfo.getDeviceType().name().toString())){
                    androidList.add(knDeviceInfo);
                    isSend = true ;
                }
            }
            map.put("stat",true);
            if( isSend ){
                map = IosPushUtil.getInstall().sendAndroidMess(androidList , title , message , uri) ;
                List<String> sendMessList = null ;
                if(map.containsKey("lineMess") ){
                    sendMessList=(List<String>)map.get("lineMess");
                }
                for(KnDeviceInfo knDeviceInfo : androidList){
                    KnPushMessageInfo pushMessInfo = new KnPushMessageInfo() ;
                    pushMessInfo.setTitle(title);
                    pushMessInfo.setContent(message);
                    pushMessInfo.setUri(uri);
                    pushMessInfo.setToken(knDeviceInfo.getDeviceToken());
                    Setting.MessageType tempMessageType = Setting.MessageType.systemmes ;
                    if(Setting.CLEARCLIENT.equals(uri)){
                        tempMessageType = Setting.MessageType.clearuser ;
                    } else if(Setting.CLEARDEVICE.equals(uri)){
                        tempMessageType = Setting.MessageType.cleardevice ;
                    } else if(!"".equals(uri) && uri.contains(Setting.UPGRADE)){
                        tempMessageType = Setting.MessageType.onlineupdate ;
                    }
                    pushMessInfo.setMessType( tempMessageType );
                    pushMessInfo.setFromSys(knDeviceInfo.getFormSystem());
                    if(Utils.isNotNull(sendMessList)){
                        String pushmessname = knDeviceInfo.getPushMessname() ;
                        if(sendMessList.contains(pushmessname) || sendMessList.contains(pushmessname.toLowerCase()) || sendMessList.contains(pushmessname.toUpperCase()) ){
                            // nosend("未发送"),send("已发送"),received("已接收"),lookover("已查看");
                            pushMessInfo.setMsgState(Setting.MsgState.send );
                        } else {
                            pushMessInfo.setMsgState(Setting.MsgState.nosend);
                        }
                    } else {
                        pushMessInfo.setMsgState(Setting.MsgState.nosend);
                    }
                    pushMessInfo.setPlateMess(Setting.PlateformType.ANDROID);
                    pushMessInfo.setDeviceInfo(knDeviceInfo);
                    messDao.save(pushMessInfo);
                }
            }
            if(!Utils.isEmptyStr(iosmessage)){
                title = iosmessage ;
            }
            if( isIphoneSend ){
                IosPushUtil iosPushUtil = IosPushUtil.getInstall();
                if(null != apiKeyList && apiKeyList.size() > 0 && null != iphoneList && iphoneList.size() > 0){
                    Map<String,String> pushMap = new HashMap<>();
                    if(Setting.MessageType.onlineupdate.name().toString().equals(messageType)){
                        pushMap.put("vf", vf);
                        pushMap.put("newUrl", newUrl);
                        pushMap.put("xtype",Setting.Xtype.UPGRADE.name());
                    }else{
                        pushMap.put("sys",Utils.isEmptyString(uri)?"sys":uri);
                    }
                    List<String> iosList = new ArrayList<>();
                    for (String appkey : apiKeyList){//根据应用不同去推送消息
                        List<String> pushList = new ArrayList<>();//ios实际需要推送的设备的token集合
                        List<KnPushMessageInfo> messList = new ArrayList<KnPushMessageInfo>();//保存发送过的信息
                        for (KnDeviceInfo knDeviceInfo : iphoneList) {//循环发送多条数据,防止一个设备发送多次信息, 根据appkey来发送消息
                            String totken = knDeviceInfo.getDeviceToken(),
                                    knChanlApkey = knDeviceInfo.getApiKey(),
                                    type = knDeviceInfo.getDeviceType().name();//设备类型
                            if(appkey.equals(knChanlApkey) && Setting.VersionType.IPHONE.name().equalsIgnoreCase(type) && !iosList.contains(totken) ){
                                iosList.add(totken);
                                pushList.add(totken);
                                //保存信息 begin
                                KnPushMessageInfo  knPushMessageInfo = new KnPushMessageInfo();
                                knPushMessageInfo.setTitle(title);
                                knPushMessageInfo.setMessType(Setting.MessageType.valueOf(messageType));
                                knPushMessageInfo.setPlateMess(Setting.PlateformType.IOS);
                                knPushMessageInfo.setDeviceInfo(knDeviceInfo);
                                messList.add(knPushMessageInfo);
                                //保存信息 end
                            }
                        }

                        List<KnCertificateInfo> knCertificateInfoList = knCertificateDao.findCerListByAppkey(appkey);
                        Map<String,String> backMap = iosPushUtil.pushIosInfo(JsonMapper.nonDefaultMapper().toJson(pushMap),title,knCertificateInfoList,pushList);
                        boolean bool = false;
                        if(Setting.SUCCESSSTAT.equals(backMap.get(Setting.RESULTCODE)) ){
                            bool = true;
                        }
                        map.put(Setting.RESULTCODE,backMap.get(Setting.RESULTCODE) );
                        map.put("infiphone",backMap.get(Setting.MESSAGE) );

                        if(null != messList && messList.size() > 0){//循环将发送消息入库
                            for (KnPushMessageInfo pushMessInfo : messList) {
                                if(bool){
                                    pushMessInfo.setMsgState(Setting.MsgState.send);
                                }else{
                                    pushMessInfo.setMsgState(Setting.MsgState.nosend);
                                }
                                messDao.save(pushMessInfo);
                            }
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            map.put("stat",false);
            map.put(Setting.MESSAGE,"推送失败");
        }
        return map ;
    }


    private List<KnDeviceInfo> findAllByQuery(final String onlineStat , final List<Long> deviceIdList ,final List<String> deviceTokenList
            ,final Long appid ,final String online ,final String nodete , final String workstate){
        List<KnDeviceInfo> list = kaciDao.findAll(new Specification<KnDeviceInfo>(){
            @Override
            public Predicate toPredicate(Root<KnDeviceInfo> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                Root<KnApplicationInfo> r=cq.from(KnApplicationInfo.class);
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();

                if(!Strings.isNullOrEmpty(onlineStat)) {
                    expressions.add(cb.equal(root.<String>get("onlineStat"), onlineStat));
                }
                if(  !Utils.isEmpityCollection(deviceIdList) ) {
                    Expression<String> exp = root.get("id");
                    expressions.add( exp.in(deviceIdList) );
                }
                if( !Utils.isEmpityCollection(deviceTokenList) ) {
                    Expression<String> exp = root.get("deviceToken");
                    expressions.add( exp.in(deviceTokenList) );
                }
                if( !Utils.isEmptyString(appid))  {
                    expressions.add(cb.equal(root.<Long>get("appId") ,r.<Long>get("id")));
                    expressions.add(cb.equal(root.<Long>get("appId"), appid));
                }
                if(!Strings.isNullOrEmpty(online)) {
                    expressions.add(cb.equal(root.<String>get("onlineStat"), online));
                }
                if(!Strings.isNullOrEmpty(nodete)) {
                    expressions.add(cb.equal(root.<Setting.DeleteStatusType>get("delState"), Setting.DeleteStatusType.valueOf(nodete)));
                }
                if(!Strings.isNullOrEmpty(workstate)) {
                    expressions.add(cb.equal(root.<Setting.WorkStatusType>get("workStatus"), Setting.WorkStatusType.valueOf(workstate)));
                }
                return predicate;
            }
        }) ;
        return list ;
    }

    private Sort getSort(DataTable<KnPushMessageInfo> dt){
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        String[] column=new String[]{"deviceInfo.loginName" ,"title", "content","deviceInfo.appTitle","messType","msgState","plateMess","deviceInfo.deviceName","deviceInfo.deviceToken" ,"updateTime"} ;
        return new Sort(d,column[Integer.parseInt(dt.getiSortCol_0())-1]);
    }


    /**
     * 因ios的发送内容只有正文内容
     *
     * @param ids              KnEmployee 用户表的主键id集合
     * @param type             发送消息的类型  具体查看 Setting.MessageType  枚举
     * @param title            android的标题 对于清除用户、清除设备、在线升级 如果不填则会自己给内容
     * @param message          android消息内容 对于清除用户、清除设备、在线升级 如果不填则会自己给内容
     * @param iosmessage       IOS消息内容,对于清除用户、清除设备、在线升级 如果不填则会自己给内容
     * @param appId            用于推送版本升级消息的应用id
     * @param onlineUpdateType 用于推送版本升级消息的应用类型  只可以为: 可用版本 Setting.WorkStatusType.usable.toString() 与  测试版本 Setting.WorkStatusType.test.toString()
     * @param uri             发送普通消息跟接口消息时的特定uri供处理
     *
     * @return
     *
     * @throws RuntimeException
     */
    public DetailDTO SendMess(List<Long> ids,String type,String title,String message,String iosmessage,long appId,String onlineUpdateType,String uri){
        System.out.print("推送日志：ids:"+ids+",type:"+type+",title:"+title+",message:"+message+",iosmessage:"+iosmessage+",appId:"+appId+",onlineUpdateType:"+onlineUpdateType);
        DetailDTO dd=new DetailDTO<>(false);
        Map map=new HashMap();
        final List<String> employeeIdsList=new ArrayList<>();
        for(Long id : ids){
            employeeIdsList.add(id+"");
        }
        List<KnDeviceInfo> list=kaciDao.findAll(new Specification<KnDeviceInfo>(){
            @Override
            public Predicate toPredicate(Root<KnDeviceInfo> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                Expression<String> exp=cb.lower(root.<String>get("userId"));
                expressions.add(exp.in(employeeIdsList));
                return predicate;
            }
        });
        String broadcast="U_L";
        StringBuffer channelId=new StringBuffer();
        StringBuffer deviceToken=new StringBuffer();
        if(!Utils.isEmpityCollection(employeeIdsList)){
            broadcast="U";
            for(KnDeviceInfo knDeviceInfo : list){
                channelId.append(","+knDeviceInfo.getId());
                deviceToken.append(","+knDeviceInfo.getDeviceToken());
            }
            if(!Strings.isNullOrEmpty(channelId.toString())){
                channelId.deleteCharAt(0);
            }
            if(!Strings.isNullOrEmpty(deviceToken.toString())){
                deviceToken.deleteCharAt(0);
            }
        }
        if(Setting.MessageType.systemmes.toString().equals(type) || Setting.MessageType.intermes.toString().equals(type)){
            //系统消息(普通消息)
            map=SendMessage(Setting.MessageType.systemmes.toString(),null,null,broadcast,channelId.toString(),"","",iosmessage,title,uri,message);
        }else if(Setting.MessageType.clearuser.toString().equals(type)){
            //清除用户
            if(Utils.isEmptyStr(title)){
                title="用户清除";
                iosmessage="用户清除";
            }
            if(Utils.isEmptyStr(message)){
                message="用户清除";
            }
            map=SendMessage(Setting.MessageType.clearuser.toString(),null,null,broadcast,channelId.toString(),"","",iosmessage,title,"",message);
        }else if(Setting.MessageType.cleardevice.toString().equals(type)){
            //清除设备
            if(Utils.isEmptyStr(title)){
                title="客户端设备清除";
                iosmessage="客户端设备清除";
            }
            if(Utils.isEmptyStr(message)){
                message="客户端设备清除";
            }
            map=SendMessage(Setting.MessageType.clearuser.toString(),deviceToken.toString(),null,null,"","","",iosmessage,title,"",message);
        }else if(Setting.MessageType.onlineupdate.toString().equals(type)){
            //onlineupdate
            if(Utils.isEmptyStr(title)){
                title="新版本更新";
                iosmessage="新版本更新";
            }
            //KnVersionInfo 表中 List<Setting.WorkStatusType> workStatList = Arrays.asList(Setting.WorkStatusType.usable,Setting.WorkStatusType.test);
            List<Setting.WorkStatusType> workStatList=new ArrayList<>();
            if(Setting.WorkStatusType.usable.toString().equals(onlineUpdateType)){
                workStatList.add(Setting.WorkStatusType.usable);
            }else if(Setting.WorkStatusType.test.toString().equals(onlineUpdateType)){
                workStatList.add(Setting.WorkStatusType.test);
            }
            List<KnVersionInfo> knVersionInfoList=kverDao.findVerListByAppIdAndWorkStats(appId,workStatList);
            if(!Utils.isEmpityCollection(knVersionInfoList)){
                map=SendMessage(Setting.MessageType.onlineupdate.toString(),null,knVersionInfoList.get(0).getId()+"",null,"","","",iosmessage,title,"","");
            }
        }
        if(Setting.SUCCESSSTAT.equals(map.get(Setting.RESULTCODE))&&"true".equals(map.get("stat"))){
            dd.setStatus(true);
        }else{
            dd.setErrorCode(Setting.FAIURESTAT);
            dd.setErrorMessage("ios:"+map.get("infiphone")+" android:"+map.get(Setting.MESSAGE));
        }
        return dd;
    }

}