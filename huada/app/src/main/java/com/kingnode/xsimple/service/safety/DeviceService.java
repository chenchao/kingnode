package com.kingnode.xsimple.service.safety;
import java.util.ArrayList;
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
import com.kingnode.xsimple.dao.push.KnDeviceInfoDao;
import com.kingnode.xsimple.dao.push.KnMessageDao;
import com.kingnode.xsimple.entity.push.KnDeviceInfo;
import com.kingnode.xsimple.entity.push.KnPushMessageInfo;
import com.kingnode.xsimple.util.Utils;
import com.kingnode.xsimple.util.client.HttpUtil;
import com.kingnode.xsimple.util.key.AES;
import com.kingnode.xsimple.util.push.IosPushUtil;
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
public class DeviceService{
    private KnMessageDao messDao;
    private KnDeviceInfoDao kaciDao;
    @Autowired
    public void setMessDao(KnMessageDao messDao){
        this.messDao=messDao;
    }
    @Autowired
    public void setKaciDao(KnDeviceInfoDao kaciDao){
        this.kaciDao=kaciDao;
    }
    public DataTable<KnDeviceInfo> SearchKnDeviceInfoList(DataTable<KnDeviceInfo> dt,final Map<String,Object> searchParams){
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),getSort(dt));
        Page<KnDeviceInfo> list=kaciDao.findAll(new Specification<KnDeviceInfo>(){
            @Override public Predicate toPredicate(Root<KnDeviceInfo> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(searchParams!=null&&searchParams.size()!=0){
                    //用户名
                    if(searchParams.containsKey("LIKE_loginName")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_loginName").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("loginName")),"%"+searchParams.get("LIKE_loginName").toString().trim().toUpperCase()+"%"));
                    }
                    //设备类型
                    if(searchParams.containsKey("EQ_deviceType")&&!Strings.isNullOrEmpty(searchParams.get("EQ_deviceType").toString().trim())){
                        expressions.add(cb.equal(root.<Setting.VersionType>get("deviceType"),Setting.VersionType.valueOf(searchParams.get("EQ_deviceType").toString())));
                    }
                    //账号状态
                    if(searchParams.containsKey("EQ_delState")&&!Strings.isNullOrEmpty(searchParams.get("EQ_delState").toString().trim())){
                        expressions.add(cb.equal(root.<Setting.DeleteStatusType>get("delState"),Setting.DeleteStatusType.valueOf(searchParams.get("EQ_delState").toString())));
                    }
                    //设备号
                    if(searchParams.containsKey("LIKE_deviceToken")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_deviceToken").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("deviceToken")),"%"+searchParams.get("LIKE_deviceToken").toString().trim().toUpperCase()+"%"));
                    }
                    //手机号
                    if(searchParams.containsKey("LIKE_userPhone")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_userPhone").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("userPhone")),"%"+searchParams.get("LIKE_userPhone").toString().trim().toUpperCase()+"%"));
                    }
                    //设备名称
                    if(searchParams.containsKey("LIKE_deviceName")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_deviceName").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("deviceName")),"%"+searchParams.get("LIKE_deviceName").toString().trim().toUpperCase()+"%"));
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
    public void Delete(List<Long> ids) throws Exception{
        List<KnDeviceInfo> list=(List<KnDeviceInfo>)kaciDao.findAll(ids);
        kaciDao.delete(list);
    }
    @Transactional(readOnly=false)
    public void CancelDel(List<Long> ids) throws Exception{
        List<Long> chanIdsList=new ArrayList<>();
        List<KnDeviceInfo> list=(List<KnDeviceInfo>)kaciDao.findAll(ids);
        for(KnDeviceInfo info : list){
            chanIdsList.add(info.getId());
        }
        kaciDao.updateChannState(Setting.DeleteStatusType.nodelete,chanIdsList);
    }
    @Transactional(readOnly=false)
    public void DeleteMessage(List<Long> ids) throws Exception{
        List<KnPushMessageInfo> list=(List<KnPushMessageInfo>)messDao.findAll(ids);
        messDao.delete(list);
    }
    /**
     * 查询设备下的消息
     *
     * @param deviceId 设备id
     * @param dt
     *
     * @return
     */
    public DataTable<KnPushMessageInfo> QueryMessList(DataTable<KnPushMessageInfo> dt,final Map<String,Object> searchParams,final String deviceId){
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),getMessSort(dt));
        Page<KnPushMessageInfo> list=messDao.findAll(new Specification<KnPushMessageInfo>(){
            @Override public Predicate toPredicate(Root<KnPushMessageInfo> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                Root<KnDeviceInfo> kr=query.from(KnDeviceInfo.class);
                expressions.add(cb.equal(root.<KnDeviceInfo>get("deviceInfo"),kr.<Long>get("id")));
                if(!Strings.isNullOrEmpty(deviceId)){
                    expressions.add(cb.equal(kr.<Long>get("id"),deviceId));
                }
                if(searchParams!=null&&searchParams.size()!=0){
                    //接收者
                    if(searchParams.containsKey("LIKE_deviceInfo.loginName")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_deviceInfo.loginName").toString().trim())){
                        expressions.add(cb.like(cb.upper(root.<String>get("deviceInfo.loginName")),"%"+searchParams.get("LIKE_deviceInfo.loginName").toString().trim().toUpperCase()+"%"));
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
                }
                return predicate;
            }
        },pageRequest);
        dt.setiTotalDisplayRecords(list.getTotalElements());
        dt.setAaData(list.getContent());
        return dt;
    }
    public DataTable<KnDeviceInfo> SearchOnlineList(DataTable<KnDeviceInfo> dt,final Map<String,Object> searchParams) throws Exception{
        String resultStr=HttpUtil.sendHttpUrlRequest(IosPushUtil.getInstall().androidHttpUrl+"/session/list","encod=1","post");
        //String resultStr=HttpUtil.sendHttpUrlRequest("http://180.150.179.167/push/session/list","encod=1","post");
        resultStr=AES.Decrypt(resultStr,null).toLowerCase();

        Map jm=JsonMapper.nonEmptyMapper().nonEmptyMapper().fromJson(resultStr,Map.class);
        if(jm.containsKey("hasdata") && (Boolean)jm.get("hasdata") ){
            final List<String> channelNameList=(List)jm.get("user");
            PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),new Sort(Sort.Direction.ASC,"loginName"));
            Page<KnDeviceInfo> list=kaciDao.findAll(new Specification<KnDeviceInfo>(){
                @Override public Predicate toPredicate(Root<KnDeviceInfo> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                    Predicate predicate=cb.conjunction();
                    List<Expression<Boolean>> expressions=predicate.getExpressions();

                    expressions.add( cb.equal(root.<String>get("onlineStat"),Setting.OnlineType.online.toString()) );
                    if(!Utils.isEmpityCollection(channelNameList) ) {
                        Expression<String> exp = cb.lower(root.<String>get("pushMessname"));
                        expressions.add( exp.in(channelNameList) );
                    }

                    if(searchParams!=null&&searchParams.size()!=0){
                        //接收者
                        if(searchParams.containsKey("LIKE_loginName")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_loginName").toString().trim())){
                            expressions.add(cb.like(cb.upper(root.<String>get("loginName")),"%"+searchParams.get("LIKE_loginName").toString().trim().toUpperCase()+"%"));
                        }
                        //设备类型
                        if(searchParams.containsKey("EQ_deviceType")&&!Strings.isNullOrEmpty(searchParams.get("EQ_deviceType").toString().trim())){
                            expressions.add(cb.equal(root.<Setting.VersionType>get("deviceType"),Setting.VersionType.valueOf(searchParams.get("EQ_deviceType").toString())));
                        }

                        //设备号
                        if(searchParams.containsKey("LIKE_deviceToken")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_deviceToken").toString().trim())){
                            expressions.add(cb.like(root.<String>get("deviceToken"),"%"+searchParams.get("LIKE_deviceToken")+"%"));
                        }
                        //手机号
                        if(searchParams.containsKey("LIKE_userPhone")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_userPhone").toString().trim())){
                            expressions.add(cb.equal(root.<String>get("userPhone"),searchParams.get("LIKE_userPhone").toString()));
                        }
                        //设备名称
                        if(searchParams.containsKey("LIKE_deviceName")&&!Strings.isNullOrEmpty(searchParams.get("LIKE_deviceName").toString().trim())){
                            expressions.add(cb.like(root.<String>get("deviceName"),"%"+searchParams.get("LIKE_deviceName")+"%"));
                        }
                    }
                    return predicate;
                }
            },pageRequest);
            dt.setiTotalDisplayRecords(list.getTotalElements());
            dt.setAaData(list.getContent());
        } else {
            dt.setiTotalDisplayRecords(0L);
            dt.setAaData(new ArrayList<KnDeviceInfo>());
        }
        return dt;
    }
    /**
     *
     * @param userIdList  用户ID 对应  KnEmployee  主键id
     * @return
     */
    public boolean DelteUserDevice(List<String> userIdList)throws Exception{
        boolean flag = true ;
        try{
            kaciDao.delteUserDevice(userIdList);
        }catch(Exception e){
            flag = false ;
        }finally{
            return flag ;
        }
    }
    private Sort getMessSort(DataTable<KnPushMessageInfo> dt){
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        String[] column=new String[]{"title","content","messType","fromSys","msgState","plateMess","updateTime"};
        return new Sort(d,column[Integer.parseInt(dt.getiSortCol_0())-1]);
    }
    private Sort getSort(DataTable<KnDeviceInfo> dt){
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        String[] column=new String[]{"loginName","deviceType","delState","deviceToken","userPhone","chversion","deviceName","onlineStat","appTitle","updateTime"};
        return new Sort(d,column[Integer.parseInt(dt.getiSortCol_0())-1]);
    }
}
