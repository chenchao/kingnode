package com.kingnode.approval.dto;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

import com.kingnode.approval.entity.KnApproval;
@XmlRootElement(name="KnApproval")
public class KnApprovalDTO{
    private Long id;
    private String title;//申请标题
    private String content;//申请内容
    private Long userId;//申请人ID
    private String userName;//申请人姓名
    private KnApproval.NodeType nodeType;//开始，进行当中，结束
    private KnFlowDTO flow;
    private KnFlowFormDTO form;//申请预制表单
    private List<KnApprovalVariablesDTO> variables;//审批表单值
    private List<KnApprovalFileDTO> file;//审批提交的文件
    private List<KnApprovalProcessDTO> process;//审批过程信息表
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content=content;
    }
    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId=userId;
    }
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName=userName;
    }
    public KnApproval.NodeType getNodeType(){
        return nodeType;
    }
    public void setNodeType(KnApproval.NodeType nodeType){
        this.nodeType=nodeType;
    }
    public KnFlowFormDTO getForm(){
        return form;
    }
    public void setForm(KnFlowFormDTO form){
        this.form=form;
    }
    public List<KnApprovalVariablesDTO> getVariables(){
        return variables;
    }
    public void setVariables(List<KnApprovalVariablesDTO> variables){
        this.variables=variables;
    }
    public List<KnApprovalFileDTO> getFile(){
        return file;
    }
    public void setFile(List<KnApprovalFileDTO> file){
        this.file=file;
    }
    public List<KnApprovalProcessDTO> getProcess(){
        return process;
    }
    public void setProcess(List<KnApprovalProcessDTO> process){
        this.process=process;
    }
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public KnFlowDTO getFlow(){
        return flow;
    }
    public void setFlow(KnFlowDTO flow){
        this.flow=flow;
    }
}