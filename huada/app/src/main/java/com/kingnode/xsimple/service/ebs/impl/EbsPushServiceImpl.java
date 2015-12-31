package com.kingnode.xsimple.service.ebs.impl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.dao.push.KnCertificateDao;
import com.kingnode.xsimple.dao.push.KnDeviceInfoDao;
import com.kingnode.xsimple.dao.push.KnMessageDao;
import com.kingnode.xsimple.dao.system.KnEmployeeDao;
import com.kingnode.xsimple.entity.push.KnCertificateInfo;
import com.kingnode.xsimple.entity.push.KnDeviceInfo;
import com.kingnode.xsimple.entity.push.KnPushMessageInfo;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.service.ebs.EbsPushService;
import com.kingnode.xsimple.service.ebs.WsConstants;
import com.kingnode.xsimple.util.Utils;
import com.kingnode.xsimple.util.push.IosPushUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
@WebService(endpointInterface="com.kingnode.xsimple.service.ebs.EbsPushService", serviceName="ebsPush", targetNamespace=WsConstants.NS) @SOAPBinding(style=SOAPBinding.Style.RPC)
@SuppressWarnings("deprecation")
public class EbsPushServiceImpl implements EbsPushService{
    private static Logger logger=LoggerFactory.getLogger(EbsPushServiceImpl.class);
    @Autowired
    private KnDeviceInfoDao knChannelInfoDao;
    @Autowired
    private KnCertificateDao knCertificateDao;
    @Autowired
    private KnEmployeeDao knEmployeeDao;
    @Autowired
    private KnMessageDao messDao ;
    public void setKnChannelInfoDao(KnDeviceInfoDao knChannelInfoDao){
        this.knChannelInfoDao=knChannelInfoDao;
    }
    @Autowired
    public void setKnCertificateDao(KnCertificateDao knCertificateDao){
        this.knCertificateDao=knCertificateDao;
    }
    @Autowired
    public void setKnEmployeeDao(KnEmployeeDao knEmployeeDao){
        this.knEmployeeDao=knEmployeeDao;
    }
    @Autowired
    public void setMessDao(KnMessageDao messDao){
        this.messDao=messDao;
    }
    /** {@inheritDoc} */
    @Override
    public String pushInfo(@WebParam(name="fromUser") String fromUser,@WebParam(name="toUser") String toUser,@WebParam(name="subject") String subject,@WebParam(name="type") String type,@WebParam(name="appKey") String appKey,@WebParam(name="fromSys") String fromSys,@WebParam(name="fid") String fid,@WebParam(name="num") String num,@WebParam(name="icons") String icons) throws Exception{
        logger.info("EBS推送,接受参数如下：--->发送者："+fromUser+"--->接受者："+toUser+"---> 消息内容："+subject+"----> 消息类型："+type+"--->应用Key："+appKey+"--->来自哪个系统："+fromSys+"--->fkey:"+fid+"--->num:"+num+"---->icons:"+icons);
        Map map=new HashMap();
        try{
            if(Utils.isEmptyString(appKey)||Utils.isEmptyString(toUser)||Utils.isEmptyString(fromSys)){
                logger.info("EBS推送 参数为空-----> 传入参数具体如下：userId："+toUser+"--->:userSystem:"+fromSys+"----->:appkey:"+appKey);
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"EBS推送 参数为空");
            }else{
                List<KnEmployee> knEmployeeList = knEmployeeDao.findKnListByUserIdAndSys(toUser,fromSys);
                if(Utils.isEmpityCollection(knEmployeeList)){
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"员工不存在");
                }else{
                    final String tempId=toUser;
                    toUser=String.valueOf(knEmployeeList.get(0).getId());
                    List<KnDeviceInfo> updateList=knChannelInfoDao.findByAppkeyAndStat(toUser,fromSys,appKey,Setting.LINE_SEPEAC,Setting.OnlineType.online.name());
                    Map pushMap=new HashMap();
                    String xtype=Setting.Xtype.EBS.name();
                    pushMap.put("fkey",fid);
                    pushMap.put("num",num);
                    pushMap.put("icons",icons);
                    pushMap.put("xtype",xtype);
                    pushMap.put("type",type);
                    String iosPushInfo=JsonMapper.nonEmptyMapper().toJson(pushMap);
                    pushMap.put("title",subject);
                    pushMap.put("to",tempId);
                    String androidInfo=JsonMapper.nonEmptyMapper().toJson(pushMap);
                    map=pushInfo(updateList,appKey,subject,androidInfo,iosPushInfo,xtype);
                }
            }
        }catch(Exception e){
            logger.error("EBS推送,错误信息{}",e);
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"网络繁忙,稍后重试");
        }
        return JsonMapper.nonEmptyMapper().toJson(map);
    }
    /**
     * 推送下线信息
     *
     * @param updateList  需要推送下线消息的设备集合
     * @param app_key     应用标示
     * @param pushBody    推送标题
     * @param androidInfo android推送的message
     * @param iosPushInfo ios 需要推送的信息
     * @param url         android 推送的类别标示
     *
     * @return
     */
    private Map pushInfo(List<KnDeviceInfo> updateList,String app_key,String pushBody,String androidInfo,String iosPushInfo,String url){
        Map backMap=new HashMap();
        try{
            logger.info("EBS需要推送下线消息的设备集合 -----> "+updateList.size());
            if(!Utils.isEmpityCollection(updateList)){
                List<String> totkenList=new ArrayList<>();
                for(KnDeviceInfo obj : updateList){
                    totkenList.add(obj.getDeviceToken());
                }
                List<KnCertificateInfo> pushList=knCertificateDao.findCerListByAppkey(app_key);
                IosPushUtil pushUtil=IosPushUtil.getInstall();
                StringBuffer backInfo=new StringBuffer();
                Map iosMap=pushUtil.pushIosInfo(iosPushInfo,pushBody,pushList,totkenList);
                logger.info("EBS推送通知信息 IPHONE 返回信息 -----> "+JsonMapper.nonEmptyMapper().toJson(iosMap));
                if(Setting.FAIURESTAT.equals(iosMap.get(Setting.RESULTCODE))){
                    boolean bool = (iosMap.get(Setting.MESSAGE)+"").indexOf("IOS推送消息时需要推送设备的集合为空")!=-1;
                    if(!bool){
                        backInfo.append(iosMap.get(Setting.MESSAGE));
                        saveMessageInfo(updateList,pushBody,false,true);
                    }
                }else if(Setting.SUCCESSSTAT.equals(iosMap.get(Setting.RESULTCODE))){
                    saveMessageInfo(updateList,pushBody,true,true);
                }
                Map androidMap=pushUtil.sendAndroidMess(updateList,pushBody,androidInfo,url);
                logger.info("EBS推送通知信息 ANDROID 返回信息 -----> "+JsonMapper.nonEmptyMapper().toJson(androidMap));
                if(Setting.FAIURESTAT.equals(androidMap.get(Setting.RESULTCODE))){
                    backInfo.append(androidMap.get(Setting.MESSAGE));
                    saveMessageInfo(updateList,pushBody,false,false);
                }else if(Setting.SUCCESSSTAT.equals(androidMap.get(Setting.RESULTCODE))){
                    saveMessageInfo(updateList,pushBody,true,false);
                }
                if(Utils.isEmptyString(backInfo)|| Setting.SUCCESSSTAT.equals(iosMap.get(Setting.RESULTCODE))||Setting.SUCCESSSTAT.equals(androidMap.get(Setting.RESULTCODE))){
                    backMap.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                    backMap.put(Setting.MESSAGE,"EBS推送成功");
                }else{
                    backMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    backMap.put(Setting.MESSAGE,backInfo.toString());
                }
            }else{
                backMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                backMap.put(Setting.MESSAGE,"在线用户不存在");
            }
        }catch(Exception e){
            logger.error("EBS推送,推送错误信息 {}",e);
            backMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            backMap.put(Setting.MESSAGE,"网络繁忙,稍后重试");
        }
        logger.info("EBS推送通知信息 最后 返回信息 -----> "+JsonMapper.nonEmptyMapper().toJson(backMap));
        return backMap;
    }

    /** {@inheritDoc} */
    @Override
    public String pushByAccountInfo(@WebParam(name="fromUser") String fromUser,@WebParam(name="account") String account,@WebParam(name="subject") String subject,@WebParam(name="type") String type,@WebParam(name="appKey") String appKey,@WebParam(name="fromSys") String fromSys,@WebParam(name="fid") String fid,@WebParam(name="num") String num,@WebParam(name="icons") String icons) throws Exception{
        logger.info("EBS推送,接受参数如下：--->发送者："+fromUser+"--->account:"+account+"---> 消息内容："+subject+"----> 消息类型："+type+"--->应用Key："+appKey+"--->来自哪个系统："+fromSys+"--->fkey:"+fid+"--->num:"+num+"---->icons:"+icons);
        Map map=new HashMap();
        try{
            if(Utils.isEmptyString(appKey)||Utils.isEmptyString(account)){
                logger.info("EBS推送 参数为空-----> 传入参数具体如下：account："+account+"----->:appkey:"+appKey);
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"EBS推送 参数为空");
            }else{
                List<KnEmployee> knEmployeeList = knEmployeeDao.findListByAccount(account);
                if(Utils.isEmpityCollection(knEmployeeList)){
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"员工不存在");
                }else{
                    List<KnDeviceInfo> updateList=knChannelInfoDao.findByAccountAndOtherInfo(account,appKey,Setting.OnlineType.online.name());
                    Map pushMap=new HashMap();
                    String xtype = Setting.Xtype.EBS.name();
                    pushMap.put("fkey",fid);
                    pushMap.put("num",num);
                    pushMap.put("icons",icons);
                    pushMap.put("xtype",xtype);
                    pushMap.put("type",type);
                    String iosPushInfo=JsonMapper.nonEmptyMapper().toJson(pushMap);
                    pushMap.put("title",subject);
                    pushMap.put("to",knEmployeeList.get(0).getId());
                    String androidInfo=JsonMapper.nonEmptyMapper().toJson(pushMap);
                    map=pushInfo(updateList,appKey,subject,androidInfo,iosPushInfo,xtype);
                }
            }
        }catch(Exception e){
            logger.error("EBS推送,错误信息{}",e);
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"网络繁忙,稍后重试");
        }
        return JsonMapper.nonEmptyMapper().toJson(map);
    }

    /**
     * 保存发送的信息
     *
     * @param updateList   推送的设备信息
     * @param pushBody     推送内容
     * @param bool         发送成功标示
     * @param iosOrAndroid ios(true) 和android(false) 标示
     */
    @Transactional(readOnly=false)
    private void saveMessageInfo(List<KnDeviceInfo> updateList,String pushBody,boolean bool,boolean iosOrAndroid){
        if(null!=updateList&&updateList.size()>0){//循环将发送消息入库
            for(KnDeviceInfo knDeviceInfo : updateList){
                KnPushMessageInfo knPushMessageInfo=new KnPushMessageInfo();
                knPushMessageInfo.setTitle(pushBody);
                knPushMessageInfo.setMessType(Setting.MessageType.intermes);
                knPushMessageInfo.setPlateMess(iosOrAndroid == true?Setting.PlateformType.IOS:Setting.PlateformType.ANDROID);
                knPushMessageInfo.setDeviceInfo(knDeviceInfo);
                knPushMessageInfo.setMsgState(bool==true?Setting.MsgState.send:Setting.MsgState.nosend);
                messDao.save(knPushMessageInfo);
            }
        }
    }
}