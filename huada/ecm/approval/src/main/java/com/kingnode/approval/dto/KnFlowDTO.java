package com.kingnode.approval.dto;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name="KnFlow")
public class KnFlowDTO{
    private Long id;
    private String name;//流程名称
    private String content;//流程说明
    private List<KnFlowNodeDTO> node;//流程节点信息
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content=content;
    }
    public List<KnFlowNodeDTO> getNode(){
        return node;
    }
    public void setNode(List<KnFlowNodeDTO> node){
        this.node=node;
    }
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
}
