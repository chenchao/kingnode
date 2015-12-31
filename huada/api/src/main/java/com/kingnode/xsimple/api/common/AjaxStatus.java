package com.kingnode.xsimple.api.common;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class AjaxStatus{
    private Boolean success;
    private String code;
    private String message;
    public AjaxStatus(Boolean success){
        this.success=success;
    }
    public AjaxStatus(Boolean success,String message){
        this.success=success;
        this.message=message;
    }
    public Boolean isSuccess(){
        return success;
    }
    public void setSuccess(Boolean success){
        this.success=success;
    }
    public String getCode(){
        return code;
    }
    public void setCode(String code){
        this.code=code;
    }
    public String getMessage(){
        return message;
    }
    public void setMessage(String message){
        this.message=message;
    }
}
