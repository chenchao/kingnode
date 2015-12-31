package com.kingnode.xsimple.entity.web;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * @author kongjiangwei
 */
@Entity @Table(name="kn_function_app_info")
public class KnFunctionAppInfo extends AuditEntity{
    private Long appId;
    private Long functionId;
    public Long getFunctionId(){
        return functionId;
    }
    public void setFunctionId(Long functionId){
        this.functionId=functionId;
    }
    public Long getAppId(){
        return appId;
    }
    public void setAppId(Long appId){
        this.appId=appId;
    }

}
