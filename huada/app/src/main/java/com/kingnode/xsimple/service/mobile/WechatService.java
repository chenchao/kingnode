package com.kingnode.xsimple.service.mobile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.dao.system.KnEmployeeDao;
import com.kingnode.xsimple.entity.system.KnEmployee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Component @Transactional(readOnly=true)
public class WechatService{
    private static Logger log=LoggerFactory.getLogger(WechatService.class);
    private KnEmployeeDao knEmployeeDao;
    @Autowired
    public void setKnEmployeeDao(KnEmployeeDao knEmployeeDao){
        this.knEmployeeDao=knEmployeeDao;
    }
    /**
     * *****************
     * 根据微信账号weixinId进行取消绑定操作
     *
     * @param jsonparm 参数格式示例：{"jsonparm":"{\"weixinId\":\"eefefd232\"}"}
     *
     * @return
     */
    @Transactional(readOnly=false)
    public String UnBoundToUser(String jsonparm){
        log.info("微信解绑接受参数如下----->"+jsonparm);
        String backInfo="";
        try{
            if(Strings.isNullOrEmpty(jsonparm)){
                Map<String,String> map=new HashMap<String,String>();
                map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                map.put(Setting.MESSAGE,"参数为空");
                backInfo=JsonMapper.nonEmptyMapper().toJson(map);
            }else{
                Map<String,String> jsonMap=JsonMapper.nonEmptyMapper().fromJson(jsonparm,Map.class);
                //如果有微信Id,证明是从微信端过来的用户,根据微信Id查看用户是否存在,取消绑定微信id
                //String weixinId = jsonMap.containsKey("weixinId")? Base64.getInstance().decodeString(jsonMap.get("weixinId")):"";
                String weixinId=jsonMap.containsKey("weixinId")?jsonMap.get("weixinId"):"";
                if(Strings.isNullOrEmpty(weixinId)){
                    Map<String,String> map=new HashMap<String,String>();
                    map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                    map.put(Setting.MESSAGE,"微信id或者账号为空");
                    backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                }else{
                    List<KnEmployee> userList=getInfoByWeixinId(weixinId);
                    Map<String,String> map=new HashMap<String,String>();
                    if(userList.size()!=0){
                        KnEmployee ui=userList.get(0);
                        ui.setWeixinId(null);
                        this.unBound(ui);
                        map.put(Setting.RESULTCODE,Setting.SUCCESSSTAT);
                        map.put(Setting.MESSAGE,"解绑成功");
                    }else{
                        map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
                        map.put(Setting.MESSAGE,"账户不存在");
                    }
                    backInfo=JsonMapper.nonEmptyMapper().toJson(map);
                }
            }
        }catch(Exception e){
            Map<String,String> map=new HashMap<String,String>();
            map.put(Setting.RESULTCODE,Setting.FAIURESTAT);
            map.put(Setting.MESSAGE,"解绑异常");
            backInfo=JsonMapper.nonEmptyMapper().toJson(map);
            log.info("微信解绑异常,传入的jsonparm为"+jsonparm+"\n 错误信息为:"+e);
        }finally{
            log.info("微信解绑返回参数如下----->"+backInfo);
            return backInfo;
        }
    }
    /**
     * **********************
     * 解绑微信与用户之间的绑定
     *
     * @param knEmployee 用于提供外部服务接口
     *
     * @return
     *
     * @author kongjiangwei
     */
    private KnEmployee unBound(KnEmployee knEmployee){
        return knEmployeeDao.save(knEmployee);
    }
    /**
     * *************
     * 通过weixinId获取KnEmployee信息
     *
     * @param weixinId
     *
     * @return
     */
    private List<KnEmployee> getInfoByWeixinId(String weixinId){
        return knEmployeeDao.findByWeixinId(weixinId);
    }
}
