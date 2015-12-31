package com.kingnode.affairs.dto;
import java.util.List;

import com.kingnode.xsimple.rest.ListDTO;
/**
 * Created by xushuangyong on 14-9-24.
 */
/**
 * 返回列表对象DTO
 */
public class ReturnListDTO<T> extends ListDTO{
    //总共有多少条;
    Long totalSize=0L;
    private String message="";
    public ReturnListDTO(){
    }
    public ReturnListDTO(Boolean status,List list){
        super(status,list);
    }
    public String getMessage(){
        return message;
    }
    public void setMessage(String message){
        this.message=message;
    }
    public Long getTotalSize(){
        return totalSize;
    }
    public void setTotalSize(Long totalSize){
        this.totalSize=totalSize;
    }
}
