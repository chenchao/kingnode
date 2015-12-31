package com.kingnode.xsimple.service.mobile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.dao.application.KnAppScoreInfoDao;
import com.kingnode.xsimple.dao.application.KnApplicationInfoDao;
import com.kingnode.xsimple.dao.application.KnDownloadVersionInfoDao;
import com.kingnode.xsimple.dao.application.KnVersionImageInfoDao;
import com.kingnode.xsimple.dao.application.KnVersionInfoDao;
import com.kingnode.xsimple.dao.system.KnUserDao;
import com.kingnode.xsimple.dto.ScoreDto;
import com.kingnode.xsimple.dto.ScoreInfoDto;
import com.kingnode.xsimple.entity.application.KnApplicationInfo;
import com.kingnode.xsimple.entity.application.KnDownloadVersionInfo;
import com.kingnode.xsimple.entity.application.KnScoreInfo;
import com.kingnode.xsimple.entity.application.KnVersionImageInfo;
import com.kingnode.xsimple.entity.application.KnVersionInfo;
import com.kingnode.xsimple.entity.system.KnUser;
import com.kingnode.xsimple.rest.RestStatus;
import com.kingnode.xsimple.util.Utils;
import com.kingnode.xsimple.util.message.SendPhoneMsg;
import com.kingnode.xsimple.util.version.VersionNumUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * 应用信息统一入口对外提供接口
 *
 * @author wangyfian
 */
@Component @Transactional(readOnly=true) public class ApplicationService{
    private static Logger logger=LoggerFactory.getLogger(ApplicationService.class);
    private KnApplicationInfoDao knApplicationInfoDao;
    private KnVersionInfoDao knVersionInfoDao;
    private KnVersionImageInfoDao knVersionImageInfoDao;
    private KnDownloadVersionInfoDao knDownloadVersionInfoDao;
    private KnUserDao knUserDao;
    private KnAppScoreInfoDao knAppScoreInfoDao;
    @Value("#{commonInfo['sendMsgUrl']}") private String sendMsgUrl;// 短信发送接口的请求的url地址
    @Value("#{commonInfo['sendMsgAccount']}") private String sendMsgAccount; // 短信发送接口的用户名
    @Value("#{commonInfo['sendMsgPwd']}") private String sendMsgPwd;// 短信发送接口的密码
    @Value("#{commonInfo['sendMsgID']}") private String sendMsgID;// 企业id
    @Value("#{commonInfo['sendMsgContent']}") private String sendMsgContent;// 短信发送内容
    @Autowired public void setKnApplicationInfoDao(KnApplicationInfoDao knApplicationInfoDao){
        this.knApplicationInfoDao=knApplicationInfoDao;
    }
    @Autowired public void setKnVersionInfoDao(KnVersionInfoDao knVersionInfoDao){
        this.knVersionInfoDao=knVersionInfoDao;
    }
    @Autowired public void setKnVersionImageInfoDao(KnVersionImageInfoDao knVersionImageInfoDao){
        this.knVersionImageInfoDao=knVersionImageInfoDao;
    }
    @Autowired public void setKnDownloadVersionInfoDao(KnDownloadVersionInfoDao knDownloadVersionInfoDao){
        this.knDownloadVersionInfoDao=knDownloadVersionInfoDao;
    }
    @Autowired public void setKnUserDao(KnUserDao knUserDao){
        this.knUserDao=knUserDao;
    }
    @Autowired public void setKnAppScoreInfoDao(KnAppScoreInfoDao knAppScoreInfoDao){
        this.knAppScoreInfoDao=knAppScoreInfoDao;
    }
    /**
     * 获取应用描述信息
     *
     * @param jsonMap 前台传入参数信息
     *                参数含义如下： {"xtype":"appInfo","appkey":"应用标示"}
     *                例如：        {"xtype":"appInfo","appkey":"111111111123213"}
     *
     * @return 返回 应用描述信息
     */
    public Map AppInfo(Map<String,String> jsonMap){
        Map map=new HashMap();
        try{
            String app_key=jsonMap.containsKey("appkey")?jsonMap.get("appkey"):""; //应用的标示
            if(!Utils.isEmptyString(app_key)){
                List<KnApplicationInfo> knApplicationInfoList=knApplicationInfoDao.findApplicationByAppkey(app_key);
                if(!Utils.isEmpityCollection(knApplicationInfoList)){
                    map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                    String remark=knApplicationInfoList.get(0).getRemark();
                    map.put("remark",Utils.isEmptyString(remark)?"":remark);
                }else{
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"应用为空");
                }
            }
        }catch(Exception e){
            logger.error("获取应用描述信息,错误信息 {} ",e);
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return map;
    }
    /**
     * 获取更多应用,除了当前应用，且应用状态为可用
     *
     * @param jsonMap 前台传入参数信息
     *                参数含义如下： {"xtype":"appInfo","appkey":"应用标示","appStat":"应用状态"}
     *                例如：        {"xtype":"appInfo","appkey":"111111111123213","appStat":"usable"}
     *
     * @return 返回 获取更多应用,除了当前应用，且应用状态为可用
     */
    public Map AppList(Map<String,String> jsonMap){
        Map map=new HashMap();
        try{
            String app_key=jsonMap.containsKey("appkey")?jsonMap.get("appkey"):""; //应用的标示
            String appStat=jsonMap.containsKey("appStat")?jsonMap.get("appStat"):"";//应用的状态
            List<Setting.WorkStatusType> workStatList=new ArrayList<>();
            if(Utils.isEmptyString(appStat)){
                workStatList.add(Setting.WorkStatusType.usable);
            }else{
                workStatList.add(Setting.WorkStatusType.valueOf(appStat));
            }
            List<KnApplicationInfo> knApplicationInfoList=knApplicationInfoDao.findApplicationByAppkeyAndStats(app_key,workStatList);
            if(!Utils.isEmpityCollection(knApplicationInfoList)){
                List appList=new ArrayList();
                map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                for(KnApplicationInfo obj : knApplicationInfoList){
                    appList.add(obj);
                }
                map.put("appList",appList);
            }else{
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"应用列表为空");
            }
        }catch(Exception e){
            logger.error("获取更多应用,错误信息 {} ",e);
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return map;
    }
    /**
     * 获取软件信息
     *
     * @param jsonMap 手机端传入参数
     *                参数含义如下： {"xtype":"checkVersion","appkey":"应用标示","type":"版本运行平台"}
     *                例如：        {"xtype":"checkVersion","appkey":"111111111123213","type":"android"}
     *
     * @return 软件信息
     */
    public Map VersionInfo(Map<String,String> jsonMap){
        Map map=new HashMap();
        try{
            String app_key=jsonMap.containsKey("appkey")?jsonMap.get("appkey"):""; //应用的标示
            String type=jsonMap.containsKey("type")?jsonMap.get("type"):"";//版本运行平台
            String stats=jsonMap.containsKey("stats")?jsonMap.get("stats"):"";//版本的状态
            List<Setting.VersionType> typeList=new ArrayList<>();
            if(Utils.isEmptyString(type)){
                typeList.add(Setting.VersionType.IPHONE);
            }else{
                for(Setting.WorkStatusType verType : Setting.WorkStatusType.values()){
                    if(verType.equals(type)){
                        typeList.add(Setting.VersionType.valueOf(type));
                    }
                }
                if(Utils.isEmpityCollection(typeList)){
                    typeList.add(Setting.VersionType.IPHONE);
                }
                typeList.add(Setting.VersionType.valueOf(type.toUpperCase()));
            }
            List<Setting.WorkStatusType> workStatList=new ArrayList<>();
            if(Utils.isEmptyString(stats)){
                workStatList.add(Setting.WorkStatusType.prototype);
            }else{
                for(Setting.WorkStatusType verType : Setting.WorkStatusType.values()){
                    if(verType.equals(stats)){
                        workStatList.add(Setting.WorkStatusType.valueOf(stats));
                    }
                }
                if(Utils.isEmpityCollection(workStatList)){
                    workStatList.add(Setting.WorkStatusType.prototype);
                }
            }
            List<KnVersionInfo> knVersionInfoList=knVersionInfoDao.findVerListByAppkeyAndVerType(app_key,typeList,workStatList);
            if(!Utils.isEmpityCollection(knVersionInfoList)){
                //根据版本号进行排序,降序输出,取出的第一个数据为最高版本数据
                Collections.sort(knVersionInfoList,new Comparator<KnVersionInfo>(){
                    public int compare(KnVersionInfo v1,KnVersionInfo v2){
                        return VersionNumUtil.versionCompareTo(v1.getNum(),v2.getNum());
                    }
                });
                KnVersionInfo knVersionInfo=knVersionInfoList.get(0);
                map.put("knVersion",knVersionInfo);//版本的详情
                List<KnVersionImageInfo> knVersionImageInfoList=knVersionImageInfoDao.findVerListByVerId(knVersionInfo.getId());
                List verImageList=new ArrayList();
                if(!Utils.isEmpityCollection(knVersionImageInfoList)){
                    map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                    for(KnVersionImageInfo obj : knVersionImageInfoList){
                        verImageList.add(obj);
                    }
                }
                map.put("verImageList",verImageList);//版本图片的集合
            }else{
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"软件详情列表为空");
            }
        }catch(Exception e){
            logger.error("获取软件信息,错误信息 {}",e);
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return map;
    }
    /**
     * 下载软件信息
     *
     * @param jsonMap 手机端传入参数
     *                参数含义如下： {"xtype":"downVersionInfo","channelStatus":"下载来源渠道","versionId":"应用版本的主键id","phoneNum":"手机号","email":"邮箱地址","companyName":"公司名称","uaccount":"账号","roleName":"角色名称"}
     *                例如：        {"xtype":"downVersionInfo","channelStatus":"weixin","versionId":"123123","phoneNum":"213123","email":"6464@qq.com","companyName":"深圳市xxxx","uaccount":"xiaoli","roleName":"管理员"}
     *
     * @return 是否成功信息
     */
    @Transactional(readOnly=false)  //  关闭只读 ,对数据库有操作
    public Map DownVersionInfo(Map<String,String> jsonMap){
        Map map=new HashMap();
        try{
            KnDownloadVersionInfo knDownloadVersionInfo=new KnDownloadVersionInfo();
            String versionId=jsonMap.containsKey("versionId")?jsonMap.get("versionId"):""; //应用版本的主键id
            String phoneNum=jsonMap.containsKey("phoneNum")?jsonMap.get("phoneNum"):"";//手机号
            String email=jsonMap.containsKey("email")?jsonMap.get("email"):"";//邮箱地址
            String companyName=jsonMap.containsKey("companyName")?jsonMap.get("companyName"):"";//公司名称
            String uaccount=jsonMap.containsKey("uaccount")?jsonMap.get("uaccount"):"";//账号
            String roleName=jsonMap.containsKey("roleName")?jsonMap.get("roleName"):"";//角色名称
            String channelStatus=jsonMap.containsKey("channelStatus")?jsonMap.get("channelStatus"):"";//下载来源渠道
            knDownloadVersionInfo.setVersionId(Long.valueOf(versionId));
            knDownloadVersionInfo.setCompanyName(companyName);
            knDownloadVersionInfo.setEmailAddress(email);
            knDownloadVersionInfo.setPhoneNum(phoneNum);
            knDownloadVersionInfo.setUaccount(uaccount);
            knDownloadVersionInfo.setRoleName(roleName);
            knDownloadVersionInfo.setLikeStatus(Setting.LikeStatusType.download);
            if(Utils.isEmptyString(channelStatus)){
                knDownloadVersionInfo.setChannelStatus(Setting.ChanStatusType.qrcodeImage);
            }else{
                if("qrcodeImage".equalsIgnoreCase(channelStatus)){
                    knDownloadVersionInfo.setChannelStatus(Setting.ChanStatusType.qrcodeImage);
                }else if("appstore".equalsIgnoreCase(channelStatus)){
                    knDownloadVersionInfo.setChannelStatus(Setting.ChanStatusType.appstore);
                }else if("weixin".equalsIgnoreCase(channelStatus)){
                    knDownloadVersionInfo.setChannelStatus(Setting.ChanStatusType.weixin);
                }else{
                    knDownloadVersionInfo.setChannelStatus(Setting.ChanStatusType.others);
                }
            }
            KnVersionInfo verObj=knVersionInfoDao.findOne(Long.valueOf(versionId));
            boolean bool=false;
            if(null!=verObj){
                KnApplicationInfo appObj=verObj.getApplicationInfo();
                if(null!=appObj){
                    Integer downcount=Utils.isEmptyString(appObj.getTotalDowns())?0:appObj.getTotalDowns();
                    appObj.setTotalDowns(downcount+1);
                    knApplicationInfoDao.save(appObj);
                    Integer vRating=Utils.isEmptyString(verObj.getTotalDowns())?0:verObj.getTotalDowns();
                    verObj.setTotalDowns(vRating+1);
                    knVersionInfoDao.save(verObj);
                }else{
                    bool=true;
                }
            }else{
                bool=true;
            }
            if(bool){
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"应用或版本不存在,请检查");
                return map;
            }
            knDownloadVersionInfoDao.save(knDownloadVersionInfo);
            map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
            map.put(Setting.MESSAGE,"下载信息成功");
        }catch(Exception e){
            logger.error("下载软件信息,错误信息 {} ",e);
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return map;
    }
    /**
     * 检测发送的验证码是否正确
     *
     * @param jsonMap 手机端传入参数
     *                参数含义如下： {"xtype":"checkCodeNum","codeNum":"验证码","downloadId":"下载版本id"}
     *                例如：        {"xtype":"checkCodeNum","codeNum":"9862","downloadId":"13649466667"}
     *
     * @return 验证码是否正确
     */
    public Map CheckCodeNum(Map<String,String> jsonMap){
        Map map=new HashMap();
        try{
            String versionId=jsonMap.containsKey("versionId")?jsonMap.get("versionId"):""; //应用版本的主键id
            String codeNum=jsonMap.containsKey("codeNum")?jsonMap.get("codeNum"):"";//验证码
            List<KnDownloadVersionInfo> knDownloadVersionInfoList=knDownloadVersionInfoDao.findByVerIdAndCodeNum(Long.valueOf(versionId),codeNum);
            if(Utils.isEmpityCollection(knDownloadVersionInfoList)){
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"下载信息不存在");
            }else{
                KnDownloadVersionInfo knDownloadVersionInfo=knDownloadVersionInfoList.get(0);
                if(!Utils.isEmptyString(knDownloadVersionInfo.getOutTime())){
                    Date now=new Date();
                    if(now.before(new Date(knDownloadVersionInfo.getOutTime()))){
                        map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                        map.put(Setting.MESSAGE,"验证码正确");
                    }else{
                        map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                        map.put(Setting.MESSAGE,"验证码已经过期,请重新验证");
                    }
                }else{
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"验证码已经过期,请重新验证");
                }
            }
        }catch(Exception e){
            logger.error("检测发送的验证码是否正确,错误信息{} ",e);
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return map;
    }
    /**
     * 发送验证码
     *
     * @param jsonMap 手机端传入参数
     *                参数含义如下： {"xtype":"sendCodeNum","phoneNum":"手机号码"}
     *                例如：        {"xtype":"sendCodeNum","phoneNum":"13649466667"}
     *
     * @return
     */
    public Map SendCodeNum(Map<String,String> jsonMap){
        Map map=new HashMap();
        try{
            String phoneNum=jsonMap.containsKey("phoneNum")?jsonMap.get("phoneNum"):""; //手机号码
            int randomNum=new Random().nextInt(9000)+1000;//随机生成4位数字
            boolean bool=SendPhoneMsg.getInstall().sendByPhoneNum(phoneNum,String.valueOf(randomNum),sendMsgUrl,sendMsgAccount,sendMsgContent,sendMsgID,sendMsgPwd);
            if(bool){
                List<KnDownloadVersionInfo> numCodeList=knDownloadVersionInfoDao.findListByPhone(phoneNum,Setting.LikeStatusType.codeNum);
                //设置验证过期时间,1分钟
                Calendar cd=Calendar.getInstance();
                cd.set(Calendar.MINUTE,cd.get(Calendar.MINUTE)+1);
                Long now=cd.getTimeInMillis();
                KnDownloadVersionInfo dvi=null;
                if(Utils.isEmpityCollection(numCodeList)){
                    dvi=new KnDownloadVersionInfo();
                    dvi.setCodeNum(String.valueOf(randomNum));
                    dvi.setPhoneNum(phoneNum);
                    dvi.setOutTime(now);
                    dvi.setLikeStatus(Setting.LikeStatusType.codeNum);
                }else{
                    dvi=numCodeList.get(0);
                    dvi.setCodeNum(String.valueOf(randomNum));
                    dvi.setOutTime(now);
                }
                knDownloadVersionInfoDao.save(dvi);
                map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                map.put("downloadId",dvi.getId());
                map.put(Setting.MESSAGE,"验证码发送成功,注意查收");
            }else{
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"验证码发送失败");
            }
        }catch(Exception e){
            logger.error("发送验证码,错误信息 {} ",e);
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return map;
    }
    /**
     * 检测手机以及验证码是否正确
     *
     * @param jsonMap 手机端传入参数
     *                参数含义如下： {"xtype":"checkCodeInfo","phoneNum":"手机号码","codeNum":"验证码"}
     *                例如：        {"xtype":"checkCodeInfo","phoneNum":"13649466667","codeNum":"1232"}
     *
     * @return
     */
    public Map CheckCodeInfo(Map<String,String> jsonMap){
        Map map=new HashMap();
        try{
            String phoneNum=jsonMap.containsKey("phoneNum")?jsonMap.get("phoneNum"):""; //手机号码
            String codeNum=jsonMap.containsKey("codeNum")?jsonMap.get("codeNum"):"";//验证码
            List<KnDownloadVersionInfo> numCodeList=knDownloadVersionInfoDao.findListByPhone(phoneNum,Setting.LikeStatusType.codeNum);
            if(Utils.isEmpityCollection(numCodeList)){
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"验证码已经过期,请重新获取");
            }else{
                KnDownloadVersionInfo knDownloadVersionInfo=numCodeList.get(0);
                System.out.println(knDownloadVersionInfo.getId()+"___xxxxxxxxxx");
                if(!Utils.isEmptyString(knDownloadVersionInfo.getOutTime())){
                    Date now=new Date();
                    if(now.before(new Date(knDownloadVersionInfo.getOutTime()))){
                        if(!codeNum.equals(knDownloadVersionInfo.getCodeNum())){ //验证码不正确
                            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                            map.put(Setting.MESSAGE,"验证码错误");
                        }else{
                            map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                            map.put("userId",phoneNum+"_"+codeNum);
                        }
                    }else{
                        map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                        map.put(Setting.MESSAGE,"验证码已经过期,请重新获取");
                    }
                }else{
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"验证码已经过期,请重新获取");
                }
            }
        }catch(Exception e){
            logger.error("检测手机以及验证码是否正确,错误信息 {}",e);
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return map;
    }
    /**
     * 检测用户名以及密码是否正确
     *
     * @param jsonMap 手机端传入参数
     *                参数含义如下： {"xtype":"checkCodeInfo","username":"用户名","password":"密码"}
     *                例如：        {"xtype":"checkCodeInfo","username":"13649466667","password":"1232"}
     *
     * @return
     */
    public Map CheckUserInfo(Map<String,String> jsonMap){
        Map map=new HashMap();
        try{
            String username=jsonMap.containsKey("username")?jsonMap.get("username"):""; //用户名
            String password=jsonMap.containsKey("password")?jsonMap.get("password"):"";//密码
            List<KnUser> knUserList=knUserDao.findListByLoginName(username);
            if(Utils.isEmpityCollection(knUserList)){
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"用户不存在");
            }else{
                KnUser knUser=knUserList.get(0);
                if(password.equals(password)){//TODO:WANGYIFAN
                    map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                    map.put("userId",knUser.getId()+"_"+knUser.getLoginName());
                }else{
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"用户不存在");
                }
            }
        }catch(Exception e){
            logger.error("检测用户名以及密码是否正确,错误信息 {}",e);
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"后台异常,稍后尝试");
        }
        return map;
    }

    /**
     * 应用评论信息
     *
     * @param scoreInfoDto 评论内容实体dto
     *
     * @return 返回是否评论成功标示
     */
    public RestStatus AppScore(ScoreInfoDto scoreInfoDto,Long userId,String tip){
        RestStatus obj=new RestStatus();
        String appkey=scoreInfoDto.getAppkey(), appComment=scoreInfoDto.getAppComment(), rating=scoreInfoDto.getRating(), plateForm=scoreInfoDto.getPlateForm(), verType=scoreInfoDto.getVerType();
        if(Utils.isEmptyString(appkey)){
            obj.setStatus(false);
            obj.setErrorCode(Setting.FAIURESTAT);
            obj.setErrorMessage("应用标示为空");
            return obj;
        }
        if("1".equals(tip)){
            if(Utils.isEmptyString(appComment)){
                obj.setStatus(false);
                obj.setErrorCode(Setting.FAIURESTAT);
                obj.setErrorMessage("评论内容为空");
                return obj;
            }
            if(Utils.isEmptyString(rating)){
                obj.setStatus(false);
                obj.setErrorCode(Setting.FAIURESTAT);
                obj.setErrorMessage("评分为空");
                return obj;
            }
        }
        for(Setting.VersionType versionType : Setting.VersionType.values()){
            if(versionType.name().equals(plateForm)){
                plateForm=versionType.name();
                break;
            }
        }
        for(Setting.WorkStatusType workStatusType : Setting.WorkStatusType.values()){
            if(workStatusType.name().equals(verType)){
                verType=workStatusType.name();
                break;
            }
        }
        plateForm=Utils.isEmptyString(plateForm)?Setting.VersionType.IPHONE.name():plateForm;
        verType=Utils.isEmptyString(verType)?Setting.WorkStatusType.usable.name():verType;
        List<Setting.WorkStatusType> workStatList=new ArrayList<>();
        workStatList.add(Setting.WorkStatusType.valueOf(verType));
        List<KnVersionInfo> knVersionInfoList=knVersionInfoDao.findVerListByAppkeyAndWorkStatsAndPlat(appkey,Setting.VersionType.valueOf(plateForm.toUpperCase()),workStatList);
        if(Utils.isEmpityCollection(knVersionInfoList)){
            if("1".equals(tip)){
                obj.setStatus(true);
            }else{
                obj.setStatus(false);
            }
        }else{
            if("1".equals(tip)){
                KnScoreInfo knScoreInfo=new KnScoreInfo();
                knScoreInfo.setAppComment(String.valueOf(appComment));
                knScoreInfo.setVersionId(knVersionInfoList.get(0).getId());
                knScoreInfo.setRating(String.valueOf(rating));
                knScoreInfo.setVersionType(Setting.VersionType.valueOf(String.valueOf(plateForm)));
                knScoreInfo.setUserId(userId);
                knScoreInfo.setCommentTime(System.currentTimeMillis());
                knAppScoreInfoDao.save(knScoreInfo);
                obj.setStatus(true);
            }else{
                Long versionId=knVersionInfoList.get(0).getId();
                List<ScoreDto> scoreDtoList=knAppScoreInfoDao.findListByUserIdAndOtherInfo(userId,versionId,Setting.VersionType.valueOf(plateForm));
                if(Utils.isEmpityCollection(scoreDtoList)){
                    obj.setStatus(false);
                }else{
                    Long appTime=scoreDtoList.get(0).getCreateTime();
                    if(Utils.isEmptyString(appTime)){
                        obj.setStatus(false);
                    }else{
                        Long currentTime=System.currentTimeMillis(), thirdTime=new DateTime(appTime).plusMonths(3).getMillis();
                        if(currentTime>=thirdTime){
                            obj.setStatus(true);
                        }else{
                            obj.setStatus(false);
                        }
                    }
                }
            }
        }
        return obj;
    }
}
