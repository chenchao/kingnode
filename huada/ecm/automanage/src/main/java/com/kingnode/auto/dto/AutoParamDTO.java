package com.kingnode.auto.dto;
import com.kingnode.auto.entity.KnAuto;
/**
 * Created by xushuangyong on 14-9-24.
 */
public class AutoParamDTO{
    private Integer pageNo;//页数
    private Integer pageSize;//要获取返回页的数量
    private KnAuto.AutoState state;
    private Long userId;//查询的用户ID
    public Integer getPageNo(){
        return pageNo;
    }
    public void setPageNo(Integer pageNo){
        this.pageNo=pageNo;
    }
    public Integer getPageSize(){
        return pageSize;
    }
    public void setPageSize(Integer pageSize){
        this.pageSize=pageSize;
    }
    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId=userId;
    }
    public KnAuto.AutoState getState(){
        return state;
    }
    public void setState(KnAuto.AutoState state){
        this.state=state;
    }
}
