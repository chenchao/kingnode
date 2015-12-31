package com.kingnode.auto.dto;
/**
 * Created by xushuangyong on 14-9-24.
 */
import com.kingnode.xsimple.rest.DetailDTO;
/**
 * 返回具体对象DTO
 */
class ReturnDetailDTO extends DetailDTO{
    //包含信息,比如报错的信息等
    private String message="";
    public String getMessage(){
        return message;
    }
    public void setMessage(String message){
        this.message=message;
    }
}
