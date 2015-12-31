package com.kingnode.xsimple.service.application;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.common.base.Strings;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.dao.application.KnApplicationInfoDao;
import com.kingnode.xsimple.dao.application.KnVersionInfoDao;
import com.kingnode.xsimple.dao.push.KnCertificateDao;
import com.kingnode.xsimple.dao.push.KnDeviceInfoDao;
import com.kingnode.xsimple.entity.application.KnApplicationInfo;
import com.kingnode.xsimple.entity.application.KnVersionInfo;
import com.kingnode.xsimple.entity.push.KnCertificateInfo;
import com.kingnode.xsimple.entity.push.KnDeviceInfo;
import com.kingnode.xsimple.util.Utils;
import com.kingnode.xsimple.util.freemark.WriteToJsp;
import com.kingnode.xsimple.util.freemark.WriteToPlist;
import com.kingnode.xsimple.util.installation.ApkPlistMetadata;
import com.kingnode.xsimple.util.installation.IpaAndroidReaderUtil;
import com.kingnode.xsimple.util.push.IosPushUtil;
import com.kingnode.xsimple.util.version.VersionNumUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
/**
 * 应用版本管理
 *
 * @wangyifan
 */
@Component @Transactional(readOnly=true)
public class VersionInfoService{
    private KnVersionInfoDao knVersionInfoDao;
    private KnApplicationInfoDao knApplicationInfoDao;
    private KnDeviceInfoDao knDeviceInfoDao;
    private KnCertificateDao knCertificateDao;
    @Value("#{commonInfo['httpsAddress']}")
    private String httpsAddress;// IOS7 连接地址
    @Value("#{commonInfo['localUrl']}")
    private String localUrl;//本地访问URL
    @Value("#{commonInfo['synchronizePlist']}")
    private String synchronizePlist;//是否需要同步到c环境 开关
    @Value("#{commonInfo['ios7FileAddress']}")
    private String ios7FileAddress;//主要针对ios7的plisth和ipa信息
    @Value("#{commonInfo['ios7SynCmd']}")
    private String ios7SynCmd;//写入文件信息
    @Value("#{commonInfo['androidHttpUrl']}")
    private String androidHttpUrl;//ANDROID 推送连接的地址
    private static Logger logger=LoggerFactory.getLogger(VersionInfoService.class);
    @Autowired
    public void setKnVersionInfoDao(KnVersionInfoDao knVersionInfoDao){
        this.knVersionInfoDao=knVersionInfoDao;
    }
    @Autowired
    public void setKnApplicationInfoDao(KnApplicationInfoDao knApplicationInfoDao){
        this.knApplicationInfoDao=knApplicationInfoDao;
    }
    @Autowired
    public void setKnChannelDao(KnDeviceInfoDao knDeviceInfoDao){
        this.knDeviceInfoDao=knDeviceInfoDao;
    }
    @Autowired
    public void setKnCertificateDao(KnCertificateDao knCertificateDao){
        this.knCertificateDao=knCertificateDao;
    }
    /**
     * 根据ids 删除多个应用版本信息
     *
     * @param ids 如 123,456
     */
    @Transactional(readOnly=false)  //  关闭只读 ,对数据库有操作
    public void DeleteAllKnVserionInfoByIds(List<Long> ids){
        List<KnVersionInfo> list=(List<KnVersionInfo>)knVersionInfoDao.findAll(ids);
        knVersionInfoDao.delete(list);
    }
    /**
     * 根据id 删除单个应用版本信息
     *
     * @param id 12313
     */
    @Transactional(readOnly=false)  //  关闭只读 ,对数据库有操作
    public void DeleteKnVersionInfoById(Long id){
        knVersionInfoDao.delete(id);
    }
    /**
     * 根据ID查询应用版本信息
     *
     * @param id 应用版本id
     *
     * @return 返回符合条件的应用信息
     */
    public KnVersionInfo FindKnVersionInfoById(Long id){
        return knVersionInfoDao.findOne(id);
    }
    /**
     * 模糊查询应用版本集合
     *
     * @param searchParams 查询条件
     * @param dt           应用 dataTable
     * @param appId        应用id
     *
     * @return 返回符合条件的应用版本集合
     */
    public DataTable<KnVersionInfo> ListOfKnVerList(Map<String,Object> searchParams,DataTable<KnVersionInfo> dt,final Long appId){
        Sort sort=getSort(dt,new String[]{"id","applicationInfo.title","type","num","workStatus","updateTime"});
        Pageable pageable=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        Page<KnVersionInfo> page=knVersionInfoDao.findAll(new Specification<KnVersionInfo>(){
            @Override
            public Predicate toPredicate(Root<KnVersionInfo> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                Root<KnApplicationInfo> r=cq.from(KnApplicationInfo.class);
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                expressions.add(cb.equal(root.<KnApplicationInfo>get("applicationInfo").get("id"),r.<Long>get("id")));
                if(!Utils.isEmptyString(appId)){
                    expressions.add(cb.equal(root.<KnApplicationInfo>get("applicationInfo").get("id"),appId));
                }
                return predicate;
            }
        },pageable);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    /**
     * 对 datatable  进行排序
     *
     * @param dt 需要排序的 DataTable
     *
     * @return 返回排序信息
     */
    private Sort getSort(DataTable dt,String[] column){
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        int index=Integer.parseInt(dt.getiSortCol_0())-1;
        return new Sort(d,column[index]);
    }
    /**
     * 根据应用名称查询应用信息集合
     *
     * @return 返回符合条件应用信息集合
     */
    public List<KnApplicationInfo> FindByTitleLike(String appName){
        return knApplicationInfoDao.findByTitleLike(appName);
    }
    /**
     * 根据应用ID查询应用
     *
     * @return 返回对应应用信息
     */
    public KnApplicationInfo FindKnAppInfoById(Long appId){
        return knApplicationInfoDao.findOne(appId);
    }
    /**
     * 保存或更新 应用版本信息
     *
     * @param knVersionInfo 应用版本信息
     *
     * @return 返回成功与否的状态 以及相关提示信息
     */
    @Transactional(readOnly=false)  //  关闭只读 ,对数据库有操作
    public boolean SaveOrUpdateKnVerInfo(KnVersionInfo knVersionInfo,Map<String,String> map){
        boolean bool=false;
        try{
            String iosAddress=map.containsKey("ios7Address")?map.get("ios7Address"):"";
            String address=map.containsKey("address")?map.get("address"):"";
            String num=map.containsKey("num")?map.get("num"):"";
            if(!Strings.isNullOrEmpty(iosAddress)){
                knVersionInfo.setIosHttpsAddress(iosAddress);
            }
            if(!Strings.isNullOrEmpty(address)){
                knVersionInfo.setAddress(address);
            }
            if(!Strings.isNullOrEmpty(num)){
                knVersionInfo.setNum(num);
            }
            knVersionInfoDao.save(knVersionInfo);
            bool=true;
        }catch(Exception e){
            logger.error("新增或是更新版本错误信息 {}",e);
        }
        return bool;
    }
    /**
     * 根据模板动态生成plist文件以及jsp文件 以及推送版本升级信息
     *
     * @param knVersionInfo 版本
     * @param map       apk 地址  ios7 地址
     *
     * @return 返回是否成功信息
     */
    public String WriteToPlist(KnVersionInfo knVersionInfo,Map<String,String> map,KnApplicationInfo finAppInfo){
        StringBuffer backMess=new StringBuffer();
        try{
            String ipaFile=map.containsKey("backPath")?map.get("backPath"):"", ios7Address=map.containsKey("ios7Address")?map.get("ios7Address"):"", packageName=map.containsKey("packageName")?map.get("packageName"):"" ,imageAddress=knVersionInfo.getAddress();
            if(Setting.VersionType.IPHONE.equals(knVersionInfo.getType())||Setting.VersionType.IPAD.equals(knVersionInfo.getType())){
                if(null!=knVersionInfo.getId()){
                    if(ios7Address==null&&imageAddress!=null){
                        try{
                            ipaFile=imageAddress.substring(0,imageAddress.lastIndexOf("."))+".ipa";
                            ios7Address=knVersionInfo.getIosHttpsAddress();
                        }catch(Exception e){
                        }
                    }
                }
                //生成plist文件
                if(!Utils.isEmptyString(imageAddress)){
                    ios7Address = ios7Address.replaceAll(httpsAddress,"");
                    backMess.append(WriteToPlist.getInstall().writeToPlist(knVersionInfo,ipaFile,packageName,ios7Address,ios7FileAddress,localUrl,ios7SynCmd,synchronizePlist));
                }
            }
            List<Setting.WorkStatusType> workStatList=new ArrayList<>();
            workStatList.add(Setting.WorkStatusType.proterotype);
            workStatList.add(Setting.WorkStatusType.usable);
            List<KnVersionInfo> knVersionInfoList=FindVerListByAppAndType(finAppInfo.getId(),workStatList); //获取该应用下的版本信息服务器中版本号最高的版本信息
            //成功jsp文件
            String optionsRadios=map.get("optionsRadios")==null?"1":map.get("optionsRadios");
            Map<String,Object> backMap=WriteToJsp.getInstall().writeToJsp(finAppInfo,optionsRadios,knVersionInfoList,localUrl);
            boolean bool=backMap.containsKey("stat")?(boolean)backMap.get("stat"):false;
            String downLoadUrl = (String)backMap.get("downLoadUrl");
            //成功且下载地址不一样才更新应用的下载地址
            if(bool&&(Strings.isNullOrEmpty(downLoadUrl)||!downLoadUrl.equals(finAppInfo.getDownLoadUrl()))){
                knApplicationInfoDao.updateDownUrlAndOptionById(finAppInfo.getId(),downLoadUrl,optionsRadios);
            }
            return backMess.toString();
        }catch(Exception e){
            logger.error("根据模板动态生成plist文件以及jsp文件,错误信息如下：{}",e);
        }
        return backMess.toString();
    }
    /**
     * 获取该应用地下的版本信息服务器中版本号最高的版本信息
     *
     * @param appId    应用ID
     * @param workStat 版本状态
     * @param dt       DataTable<KnVersion> 版本列表信息
     *
     * @return List<KnVersion> 返回符合条件的版本集合列表
     */
    private List<KnVersionInfo> getVerListByAppAndType(final String appId,final String workStat,DataTable<KnVersionInfo> dt){
        Sort sort=getSort(dt,new String[]{"id","applicationInfo.title","type","num","workStatus","createTime"});
        Pageable pageable=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        Page<KnVersionInfo> page=knVersionInfoDao.findAll(new Specification<KnVersionInfo>(){
            @Override
            public Predicate toPredicate(Root<KnVersionInfo> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                Root<KnApplicationInfo> r=cq.from(KnApplicationInfo.class);
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                expressions.add(cb.equal(root.<KnApplicationInfo>get("applicationInfo").get("id"),r.<Long>get("id")));
                if(!Strings.isNullOrEmpty(appId)){
                    expressions.add(cb.equal(root.<KnApplicationInfo>get("applicationInfo").get("id"),""+appId+""));
                }
                expressions.add(cb.equal(root.<Setting.WorkStatusType>get("workStatus"),"("+workStat+")"));
                return predicate;
            }
        },pageable);
        return page.getContent();
    }
    /**
     * 获取该应用地下的版本信息服务器中版本号最高的版本信息
     *
     * @param appId    应用ID
     * @param workStat 版本状态
     *
     * @return List<KnVersion> 返回符合条件的版本集合列表
     */
    public List<KnVersionInfo> FindVerListByAppAndType(long appId,List<Setting.WorkStatusType> workStat){
        return knVersionInfoDao.findVerListByAppIdAndWorkStats(appId,workStat);
    }
    /**
     * 新增或是编辑时,会下发版本升级通知提示
     *
     * @param finVerInfo 版本信息
     */
    public Map<String,String> PushNoticeNewVerInfo(KnVersionInfo finVerInfo){
        Map<String,String> backMap=new HashMap<>();
        try{
            long appId=finVerInfo.getApplicationInfo().getId();
            List<Setting.WorkStatusType> workStatList=new ArrayList<>();
            String version=finVerInfo.getNum(), deveType="", pushBody="有新版本,请更新", xtype=Setting.Xtype.UPGRADE.name();
            if(null==finVerInfo.getWorkStatus()){
                workStatList.add(Setting.WorkStatusType.prototype);
            }else{
                workStatList.add(finVerInfo.getWorkStatus());
            }
            if(null==finVerInfo.getType()){
                deveType=Setting.VersionType.IPHONE.name();
            }else{
                deveType=finVerInfo.getType().name();
            }
            List<KnDeviceInfo> temoChannelList=knDeviceInfoDao.findChanneListByAppAndWork(appId,workStatList,Setting.VersionType.valueOf(deveType.toUpperCase()));
            temoChannelList = handleChanneList(temoChannelList,finVerInfo.getNum());
            List<KnDeviceInfo> channelInfoList=VersionNumUtil.getChannelByNum(temoChannelList,version,false);
            IosPushUtil pushUtil=IosPushUtil.getInstall();
            if(channelInfoList!=null&&channelInfoList.size()>0){
                List<String> totkenList=new ArrayList<>();
                String appKey="";
                for(KnDeviceInfo c : channelInfoList){
                    totkenList.add(c.getDeviceToken());
                    if(appKey==""||null==appKey){
                        if(c!=null){
                            appKey=c.getApiKey();
                        }
                    }
                }
                StringBuffer backInfo=new StringBuffer();//下发通知,错误提示信息
                Map<String,String> pushMap=new HashMap<>();
                pushMap.put("xtype",xtype);
                pushMap.put("vf",Utils.isEmptyString(finVerInfo.getForcedUpdate())?"0":finVerInfo.getForcedUpdate());
                if(Setting.VersionType.IPHONE.name().equalsIgnoreCase(deveType)||Setting.VersionType.IPAD.name().equalsIgnoreCase(deveType)){
                    if(!Utils.isEmptyString(appKey)){
                        List<KnCertificateInfo> knPushPlaList=knCertificateDao.findCerListByAppkey(appKey);//获取IPHONE 推送的证书
                        try{
                            pushMap.put("newUrl",Utils.isEmptyString(finVerInfo.getIosHttpsAddress())?"":finVerInfo.getIosHttpsAddress());
                            backMap=pushUtil.pushIosInfo(JsonMapper.nonDefaultMapper().toJson(pushMap),pushBody,knPushPlaList,totkenList);
                            if(Setting.FAIURESTAT.equals(backMap.get(Setting.RESULTCODE))){
                                backInfo.append(backMap.get(Setting.MESSAGE));
                            }
                        }catch(Exception e){
                            logger.error("下发版本升级 IPHONE 通知提示,错误信息如下：{}",e);
                            backInfo.append("下发版本升级通知提示，IPHONE 推送失败");
                        }
                    }
                }else if(Setting.VersionType.ANDROID.name().equalsIgnoreCase(deveType)||Setting.VersionType.ANDROID_PAD.name().equalsIgnoreCase(deveType)){
                    try{
                        pushMap.put("newUrl",Utils.isEmptyString(finVerInfo.getAddress())?"":finVerInfo.getAddress());
                        String uriStr = Setting.UPGRADE +( (finVerInfo==null || finVerInfo.getForcedUpdate()==null)?"0":finVerInfo.getForcedUpdate());
                        backMap=pushUtil.sendAndroidMess(channelInfoList,pushBody,finVerInfo.getAddress(),uriStr);
                        if(Setting.FAIURESTAT.equals(backMap.get(Setting.RESULTCODE))){
                            backInfo.append(backMap.get(Setting.MESSAGE));
                        }
                    }catch(Exception e){
                        logger.error("下发版本升级 ANDROID 通知提示,错误信息如下：{}",e);
                        backInfo.append("下发版本升级通知提示，ANDROID 推送失败");
                    }
                }
                if(Utils.isEmptyString(backInfo)){
                    backMap.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                    backMap.put(Setting.MESSAGE,"下发版本升级通知成功");
                }else{
                    backMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    backMap.put(Setting.MESSAGE,backInfo.toString());
                }
            }else{
                backMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                backMap.put(Setting.MESSAGE,"下发版本升级通知提示，没有需要推送的设备");
            }
        }catch(Exception e){
            logger.error("下发版本升级通知提示,错误信息如下：{}",e);
            backMap.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            backMap.put(Setting.MESSAGE,"下发版本升级通知提示，推送失败");
        }
        return backMap;
    }
    /**
     * 获取应用 版本信息 的安装路径 （Android存放的地址为安装包的地址,ios存放的地址为plist的地址）
     *
     * @param file              上传版本的安装包
     * @param type              版本类型  android  与 iphone
     * @param knApplicationInfo
     *
     * @return 返回 Android存放的地址为安装包的地址,ios存放的地址为plist的地址  以及对应的版本号
     */
    public Map<String,String> GetUploadInfoByFile(MultipartFile file,String type,KnApplicationInfo knApplicationInfo){
        Map<String,String> map=new HashMap<String,String>();
        try{
            if(null!=file&&file.getSize()!=0){//上传文件不为空
                WebApplicationContext webApplicationContext=ContextLoader.getCurrentWebApplicationContext();
                String fileExt=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1).toLowerCase();//扩展名
                if(!Strings.isNullOrEmpty(fileExt)){
                    Long currentTime=System.currentTimeMillis();
                    String filePath=Setting.BASEADDRESS+"/"+currentTime, backPath=filePath+"."+fileExt; //安装包存放路径前缀  没有后缀名
                    File localFile=new File(webApplicationContext.getServletContext().getRealPath(backPath));
                    if(!localFile.getParentFile().exists()){
                        localFile.getParentFile().mkdirs();
                    }
                    file.transferTo(localFile);
                    if(type==null){
                        type=Setting.VersionType.IPHONE.name();
                    }
                    ApkPlistMetadata mdata=null;
                    if(type.indexOf(Setting.VersionType.IPHONE.name())!=-1||type.indexOf(Setting.VersionType.IPHONE.name())!=-1){//IPHONE 上传安装包
                        mdata=IpaAndroidReaderUtil.getInstall().getMetadata(localFile);
                    }else{
                        mdata=IpaAndroidReaderUtil.getInstall().getAndroidData(localFile);
                    }
                    if(null!=mdata){
                        String num=mdata.getVersionNum();//安装包中的版本号
                        String packageName = mdata.getPackageName();//安装包中的包名
                        if(Strings.isNullOrEmpty(num)){
                            map.put("message","安装包中的版本号为空,请检查后重新上传");
                            return map;
                        }
                        if(type.indexOf(Setting.VersionType.IPHONE.name())!=-1||type.indexOf(Setting.VersionType.IPHONE.name())!=-1){//IPHONE 上传安装包
                            Long createTime=knApplicationInfo.getCreateTime()==null?currentTime:knApplicationInfo.getCreateTime();
                            String ios7Address=httpsAddress+"/"+createTime+"/"+currentTime+".plist";
                            String address=Setting.BASEADDRESS+"/"+currentTime+".plist";
                            map.put("ios7Address",ios7Address);
                            map.put("address",address);//IPA
                        }else{// ANDROID 上传安装包信息
                            map.put("address",filePath+".apk");
                        }
                        map.put("packageName",packageName);
                        map.put("num",num);
                        map.put("backPath",backPath);
                    }else{
                        map.put("message","安装包解析错误,请检查安装包是否正确");
                    }
                }
            }
        }catch(Exception e){
            map.put("message","安装包解析错误,请检查安装包是否正确");
            logger.error("上传安装包 错误信息：{}",e);
        }
        return map;
    }
    /**
     * 获取上传图片文件存储的路径
     *
     * @param file 图片文件
     *
     * @return 图片文件存储的路径
     */
    public String GetImageAddressByFile(MultipartFile file){
        StringBuffer imageAddress=new StringBuffer();
        try{
            if(null!=file&&file.getSize()!=0){//上传文件不为空
                WebApplicationContext webApplicationContext=ContextLoader.getCurrentWebApplicationContext();
                String fileExt=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1).toLowerCase();//扩展名
                if(!Strings.isNullOrEmpty(fileExt)){
                    Long currentTime=System.currentTimeMillis();
                    String filePath=Setting.BASEADDRESS+"/"+currentTime, backPath=filePath+"."+fileExt; //安装包存放路径前缀  没有后缀名
                    File localFile=new File(webApplicationContext.getServletContext().getRealPath(backPath));
                    if(!localFile.getParentFile().exists()){
                        localFile.getParentFile().mkdirs();
                    }
                    file.transferTo(localFile);
                    imageAddress.append(backPath);
                }
            }
        }catch(Exception e){
            logger.error("上传图片文件存储的路径 错误信息：{}",e);
        }
        return imageAddress.toString();
    }
    /**
     * @Description: (处理版本更新时,推送信息)
     * @param: @param temoChannelList 需要推送的设备信息
     * @param: @param version 当前版本号
     * @return: List<KnDeviceInfo>    返回需要推送的设备信息
     */
    private List<KnDeviceInfo> handleChanneList(List<KnDeviceInfo> temoChannelList,String version){
        if(Utils.isEmpityCollection(temoChannelList)||Utils.isEmptyString(version)){
            return temoChannelList;
        }else{
            Map<String,ArrayList<KnDeviceInfo>> map=new HashMap<String,ArrayList<KnDeviceInfo>>();
            List<KnDeviceInfo> tempList=new ArrayList<KnDeviceInfo>();
            for(int i=0;i<temoChannelList.size();i++){
                KnDeviceInfo cObj=temoChannelList.get(i);
                if(version.equals(cObj.getChversion())){
                    tempList.add(cObj);
                }
                ArrayList<KnDeviceInfo> chanList=map.containsKey(cObj.getDeviceToken())?map.get(cObj.getDeviceToken()):new ArrayList<KnDeviceInfo>();
                chanList.add(cObj);
                map.put(cObj.getDeviceToken(),chanList);
            }
            if(Utils.isEmpityCollection(tempList)){
                return temoChannelList;
            }else{
                List<KnDeviceInfo> finaList=new ArrayList<KnDeviceInfo>();
                for(int i=0;i<tempList.size();i++){
                    KnDeviceInfo cObj=tempList.get(i);
                    if(map.containsKey(cObj.getDeviceToken())){
                        finaList.addAll(map.get(cObj.getDeviceToken()));
                    }
                }
                finaList.addAll(tempList);
                temoChannelList.removeAll(finaList);
            }
        }
        return temoChannelList;
    }
}
