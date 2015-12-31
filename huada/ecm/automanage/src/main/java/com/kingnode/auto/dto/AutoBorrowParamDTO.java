package com.kingnode.auto.dto;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

import com.kingnode.auto.entity.KnAutoBorrow;
/**
 * Created by xushuangyong on 14-9-24.
 */
@XmlRootElement(name="AutoBorrowParam")
public class AutoBorrowParamDTO{
    private Integer pageNo;
    private Integer pageSize;
    private Long userId;//查询的用户ID
    private List<KnAutoBorrow.BorrowType> stateList;
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
    public List<KnAutoBorrow.BorrowType> getStateList(){
        return stateList;
    }
    public void setStateList(List<KnAutoBorrow.BorrowType> stateList){
        this.stateList=stateList;
    }
    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId=userId;
    }
}

