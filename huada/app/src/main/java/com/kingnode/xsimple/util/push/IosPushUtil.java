package com.kingnode.xsimple.util.push;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.entity.push.KnCertificateInfo;
import com.kingnode.xsimple.entity.push.KnDeviceInfo;
import com.kingnode.xsimple.util.PathUtil;
import com.kingnode.xsimple.util.ReadPropfileUtil;
import com.kingnode.xsimple.util.Utils;
import com.kingnode.xsimple.util.client.HttpUtil;
import com.kingnode.xsimple.util.key.AES;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServer;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import javapns.notification.PushedNotifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * IOS推送工具类
 * create by  wangyifan
 */
public class IosPushUtil{
    private static boolean official=true;//正式发布，默认是正式发布,false为开发
    private static Logger logger=LoggerFactory.getLogger(IosPushUtil.class);
    private static IosPushUtil pushUtil=null;
    public String androidHttpUrl=ReadPropfileUtil.getInstall().prop.getProperty("androidHttpUrl");
    private IosPushUtil(){
    }
    public static IosPushUtil getInstall(){
        if(null==pushUtil){
            synchronized(IosPushUtil.class){
                pushUtil=new IosPushUtil();
            }
        }
        return pushUtil;
    }
    /**
     * <一次给多个设备推送信息，包括普通消息以及版本升级信息>
     *
     * @param pushInfo   推送信息内容
     * @param pushBody   推送标题
     * @param pushList   证书路径 与 密码的集合
     * @param totkenList 需要推送设备list集合
     *
     * @return 返回推送成功是否状态 Map<String,String>  200  成功   500  失败  msg 为 相应的信息
     *
     * @throws Exception 异常信息
     */
    public Map<String,String> pushIosInfo(String pushInfo,String pushBody,List<KnCertificateInfo> pushList,List<String> totkenList) throws Exception{
        Map<String,String> backMap=new HashMap<String,String>();
        totkenList=getIosTotList(totkenList);
        if(null!=totkenList&&totkenList.size()>0){
            int failed=1;//默认推送失败
            try{
                if(!Utils.isEmptyString(pushBody)){
                    pushBody=pushBody.length()<40?pushBody:pushBody.substring(0,40);
                }else{
                    pushBody="";
                }
                PushNotificationPayload pl=getPushNotificationPayload(pushInfo,pushBody);
                if(pl.toString().length()>256){
                    logger.info("IOS推送消息时,推送信息过长，推送失败");
                    backMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    backMap.put(Setting.MESSAGE,"IOS推送消息时,推送信息过长，推送失败");
                    return backMap;
                }
                Map<String,String> pushMap=getPushKeyFile(pushList);
                String certifiPath=pushMap.containsKey("certifiPath")?pushMap.get("certifiPath"):"", finalUrl=PathUtil.getRootPath()+certifiPath;
                AppleNotificationServer server=getIosServerInfo(finalUrl,pushMap.containsKey("ceritifiPwd")?pushMap.get("ceritifiPwd"):"");
                if(server==null){
                    logger.info("IOS推送消息时,获取推送服务为空，推送失败");
                    backMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    backMap.put(Setting.MESSAGE,"IOS推送消息时,获取推送服务为空，推送失败");
                    return backMap;
                }
                PushNotificationManager manager=new PushNotificationManager();
                manager.initializeConnection(server);
                List<Device> devicelist=new ArrayList<Device>();
                for(int i=0;i<totkenList.size();i++){
                    Device device=new BasicDevice();
                    device.setToken(totkenList.get(i));
                    devicelist.add(device);
                }
                PushedNotifications notifications=manager.sendNotifications(pl,devicelist);
                manager.stopConnection();
                List<PushedNotification> failedNotifications=notifications.getFailedNotifications();
                failed=failedNotifications.size();
            }catch(Exception e){
                e.printStackTrace();
            }
            logger.info("IOS推送消息时,失败的数目为"+failed);
            if(failed==0){
                backMap.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                backMap.put(Setting.MESSAGE,"IOS推送消息时,推送成功");
                return backMap;
            }else{
                backMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                backMap.put(Setting.MESSAGE,"IOS推送消息时,推送失败");
                return backMap;
            }
        }else{
            logger.info("IOS推送消息时,需要推送设备的集合为空,直接返回,默认发送状态为false");
            backMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            backMap.put(Setting.MESSAGE,"IOS推送消息时需要推送设备的集合为空，推送失败");
            return backMap;
        }
    }
    /**
     * @Description: (根据IOS文件的路径,密码,与apple 服务器建立 连接)
     * @param: @param pathUrl 证书路径
     * @param: @param pathPwd 证书密码
     * @param: @throws KeystoreException    连接异常信息
     * @return: AppleNotificationServer    根据IOS文件的路径,密码,与apple 服务器建立 连接
     */
    private AppleNotificationServer getIosServerInfo(String pathUrl,String pathPwd) throws KeystoreException{
        try{
            if(!Utils.isEmptyString(pathPwd)&&!Utils.isEmptyString(pathUrl)){
                return new AppleNotificationServerBasicImpl(pathUrl,pathPwd,official);
            }
        }catch(Exception e){
            logger.error("IOS推送消息时,与apple服务器建立连接出现错误，{}",e);
        }
        return null;
    }
    /**
     * @Description: (处理IOS推送时,totken不足64位,直接过滤掉)
     * @param: @param totkenList 待处理的totken
     * @return: List<String> 符合推送要求的设备totken
     */
    private List<String> getIosTotList(List<String> totkenList){
        List<String> list=new ArrayList<String>();
        totkenList =  Utils.removeDuplicate(totkenList);
        if(null!=totkenList&&totkenList.size()>0){
            for(String totken : totkenList){
                if(!Utils.isEmptyString(totken)&&totken.length()==64){
                    list.add(totken);
                }
            }
        }
        return list;
    }
    /**
     * @Description: (设置推送的内容并返回)
     * @param: @param pushContent 推送内容（json形式的字符串）
     * @param: @param body 消息正文
     * @param: @throws IOException 异常信息
     * @return: PushNotificationPayload    返回IOS推送需要的文本体
     */
    private PushNotificationPayload getPushNotificationPayload(String pushContent,String body) throws Exception{
        PushNotificationPayload pl=new PushNotificationPayload();
        pl.addCustomAlertBody(body);
        List<String> stringLit=new ArrayList<String>();
        stringLit.add(pushContent);
        pl.addCustomAlertLocArgs(stringLit);
        return pl;
    }
    /**
     * @Description: (获取证书的路径以及密码)
     * @param: @param pushList 证书密码  集合
     * @return: Map<String,String>   以map形式返回证书密码以及路径
     */
    private Map<String,String> getPushKeyFile(List<KnCertificateInfo> pushList){
        Map<String,String> playMap=new HashMap<String,String>();
        String certifiPath="", ceritifiPwd="";
        try{
            if(null!=pushList&&pushList.size()>0){
                for(int i=0;i<pushList.size();i++){
                    KnCertificateInfo ps=pushList.get(i);
                    if(ps.getPlatformType().equalsIgnoreCase(Setting.VersionType.IPHONE.name())||ps.getPlatformType().equalsIgnoreCase(Setting.VersionType.IPAD.name())||ps.getPlatformType().equalsIgnoreCase(Setting.PlateformType.IOS.name())){
                        if(Setting.WorkStatusType.usable.equals(ps.getWorkStatus())){
                            certifiPath=ps.getCertificatePath();
                            ceritifiPwd=ps.getCertificatePwd();
                            break;
                        }else if(null==ps.getWorkStatus()||Utils.isEmptyString(ps.getWorkStatus().name())){
                            if(Utils.isEmptyString(ceritifiPwd)&&Utils.isEmptyString(certifiPath)){
                                certifiPath=ps.getCertificatePath();
                                ceritifiPwd=ps.getCertificatePwd();
                            }
                        }
                    }
                }
            }
        }catch(Exception e){
        }
        playMap.put("certifiPath",certifiPath);
        playMap.put("ceritifiPwd",ceritifiPwd);
        return playMap;
    }
    public Map sendAndroidMess(List<KnDeviceInfo> list,String title,String message,String uri){
        boolean isSend=false;
        Map jm=new HashMap();
        StringBuffer messUserBuff=new StringBuffer();
        try{
            List<String> channelNameList=null;
            String resultLineStr=HttpUtil.sendHttpUrlRequest(IosPushUtil.getInstall().androidHttpUrl+"/session/list","encod=1","post");
            resultLineStr=AES.Decrypt(resultLineStr,null).toLowerCase();
            Map lineMp=JsonMapper.nonEmptyMapper().nonEmptyMapper().fromJson(resultLineStr,Map.class);
            if(lineMp.containsKey("hasdata")&&(Boolean)lineMp.get("hasdata")){
                channelNameList=(List)lineMp.get("user");
            }
            List<String> sendMessList=new ArrayList<>();
            for(KnDeviceInfo knDeviceInfo : list){
                if(Setting.PlatformType.ANDROID.name().toString().equalsIgnoreCase(knDeviceInfo.getDeviceType().toString())){
                    if(Utils.isNotNull(channelNameList)){
                        String deviceName=knDeviceInfo.getPushMessname();
                        if(channelNameList.contains(deviceName.toLowerCase())||channelNameList.contains(deviceName.toUpperCase())||channelNameList.contains(deviceName)){
                            messUserBuff.append(";"+deviceName);
                            sendMessList.add(deviceName);
                            isSend=true;
                        }
                    }
                }
            }
            if(isSend){
                Map<String,String> parammap=new HashMap<>();
                parammap.put("broadcast","D");
                parammap.put("encod","2");
                parammap.put("title",AES.Encrypt(title,null));
                parammap.put("message",AES.Encrypt(message,null));
                parammap.put("uri",uri);
                String messUser=messUserBuff.toString().substring(1);
                parammap.put("messuser",AES.Encrypt(messUser,null));
                String params="jsonparm="+JsonMapper.nonEmptyMapper().toJson(parammap);
                String resultStr=HttpUtil.sendHttpUrlRequest(androidHttpUrl+"/notification/send",params,"post");
                resultStr=AES.Decrypt(resultStr,null);
                jm=JsonMapper.nonEmptyMapper().nonEmptyMapper().fromJson(resultStr,Map.class);
            }else{
                jm.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                jm.put("stat",true);
                jm.put(Setting.MESSAGE,"部分android设备不在线,推送为离线消息");
            }
            jm.put("lineMess",sendMessList);
        }catch(Exception e){
            jm.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            jm.put("stat",false);
            jm.put(Setting.MESSAGE,"无法连接到android推送服务器");
        }
        return jm;
    }
}
