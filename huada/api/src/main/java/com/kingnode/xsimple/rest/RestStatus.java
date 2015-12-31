package com.kingnode.xsimple.rest;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class RestStatus{
    private Boolean status;
    private String errorCode;
    private String errorMessage;
    public RestStatus(){

    }
    public RestStatus(Boolean status){
        this.status=status;
    }
    public Boolean getStatus(){
        return status;
    }
    public void setStatus(Boolean status){
        this.status=status;
    }
    public String getErrorCode(){
        return errorCode;
    }
    public void setErrorCode(String errorCode){
        this.errorCode=errorCode;
    }
    public String getErrorMessage(){
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage){
        this.errorMessage=errorMessage;
    }
}
