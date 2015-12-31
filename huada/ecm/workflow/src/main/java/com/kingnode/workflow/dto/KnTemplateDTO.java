package com.kingnode.workflow.dto;
import java.util.List;

import com.kingnode.workflow.entity.KnNode;
/**
 * 模板DTO
 *
 * @author huanganding
 */
public class KnTemplateDTO{
    private Long id;
    private String title; //模板名称
    private List<KnNodeDTO> nodes; //节点
    private Integer count; //提交次数
    private String type;  //模板类型
    private Long currApproveId; //当前审批人ID
    private String currApproveName; //当前审批人名称
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public List<KnNodeDTO> getNodes(){
        return nodes;
    }
    public void setNodes(List<KnNodeDTO> nodes){
        this.nodes=nodes;
    }
    public Integer getCount(){
        return count;
    }
    public void setCount(Integer count){
        this.count=count;
    }
    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type=type;
    }
    public Long getCurrApproveId(){
        return currApproveId;
    }
    public void setCurrApproveId(Long currApproveId){
        this.currApproveId=currApproveId;
    }
    public String getCurrApproveName(){
        return currApproveName;
    }
    public void setCurrApproveName(String currApproveName){
        this.currApproveName=currApproveName;
    }
}
