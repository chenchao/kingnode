package com.kingnode.xsimple.api.system;
/**
 * 系统属性实体  --- clientInfo.properties 属性文件
 */
public class SysPropInfo{
    private static final long serialVersionUID=7829370955661684334L;
    private String key;
    private String value;
    public SysPropInfo(){
    }
    public String getKey(){
        return key;
    }
    public void setKey(String key){
        this.key=key;
    }
    public String getValue(){
        return value;
    }
    public void setValue(String value){
        this.value=value;
    }
}
