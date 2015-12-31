package com.kingnode.third.service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kingnode.third.dao.KnReponsibilityThirdDao;
import com.kingnode.third.entity.KnReponsibilityThird;
import com.kingnode.xsimple.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * 导入第三方职责设置管理
 * wangyifan
 */
@Component @Transactional(readOnly=true)
public class ImportThirdExcelService{
    private KnReponsibilityThirdDao knReponsibilityThirdDao;
    private static Logger logger=LoggerFactory.getLogger(ImportThirdExcelService.class);
    @Autowired
    public void setKnReponsibilityThirdDao(KnReponsibilityThirdDao knReponsibilityThirdDao){
        this.knReponsibilityThirdDao=knReponsibilityThirdDao;
    }
    /**
     * 批量保存职责信息
     *
     * @param responIds   职责ID数组
     * @param responNames 职责名称数组
     * @param fromSystem  来自系统数组
     *
     * @return
     */
    public Map<String,Boolean> SaveResonList(String[] responIds,String[] responNames,String[] fromSystem){
        Map<String,Boolean> backMap=new HashMap<>();
        try{
            List<KnReponsibilityThird> knReponList=(List<KnReponsibilityThird>)knReponsibilityThirdDao.findAll();
            Map<String,KnReponsibilityThird> knReponMap=new HashMap<String,KnReponsibilityThird>();
            if(null!=knReponList&&knReponList.size()>0){
                for(KnReponsibilityThird knReponObj : knReponList){
                    String responName=knReponObj.getResponsibilityName(), fromSys=knReponObj.getFromSys();
                    if(responName!=null&&fromSys!=null){
                        knReponMap.put(responName+"_"+fromSys,knReponObj);
                    }
                }
            }
            for(int i=0;i<responNames.length;i++){
                //保存职责信息 begin
                KnReponsibilityThird knRepons;
                if(fromSystem.length==0){
                    continue;
                }
                String keyTip=responNames[i]+"_"+fromSystem[i];
                if(Utils.isEmptyString(keyTip)){
                    continue;
                }
                if(knReponMap.containsKey(keyTip)){
                    knRepons=knReponMap.get(keyTip);
                }else{
                    knRepons=new KnReponsibilityThird();
                }
                knRepons.setResponsibilityId(responIds[i]);
                knRepons.setResponsibilityName(responNames.length==0?null:responNames[i]);
                knRepons.setFromSys(fromSystem.length==0?null:fromSystem[i]);
                knReponsibilityThirdDao.save(knRepons);
                knReponMap.put(keyTip,knRepons);
                //保存职责信息 end
            }
            backMap.put("stat",true);
        }catch(Exception e){
            backMap.put("stat",false);
            logger.info("批量导入职责，错误信息{}",e);
        }
        return backMap;
    }
}
