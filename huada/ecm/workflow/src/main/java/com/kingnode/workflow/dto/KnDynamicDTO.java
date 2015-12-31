package com.kingnode.workflow.dto;
import java.util.List;
/**
 * @author chenchao
 * 用于显示动态信息的dto
 */
public class KnDynamicDTO{
    private String date;//时间key
    private List<KnHistoryApproveDTO> hists;//动态记录集合
    public String getDate(){
        return date;
    }
    public void setDate(String date){
        this.date=date;
    }
    public List<KnHistoryApproveDTO> getHists(){
        return hists;
    }
    public void setHists(List<KnHistoryApproveDTO> hists){
        this.hists=hists;
    }
}
