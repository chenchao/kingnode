package com.kingnode.approval.entity;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kingnode.xsimple.entity.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
@Entity @Table(name="kn_approval")
public class KnApproval extends IdEntity{
    private static final long serialVersionUID=7681559588965685034L;
    private String title;//申请标题
    private String content;//申请内容
    private Long userId;//申请人ID
    private String userName;//申请人姓名
    private NodeType nodeType;//开始，进行当中，结束
    private KnFlow flow;//如果是固定流程此字段不能为空
    private KnFlowForm form;//申请预制表单
    private Long createTime;//创建申请流程时间
    private Set<KnApprovalVariables> variables;//审批表单值
    private Set<KnApprovalFile> file;//审批提交的文件
    private Set<KnApprovalProcess> process;//审批过程信息表
    @Column(length=200)
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    @Column(length=2000)
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content=content;
    }
    @Column(length=13)
    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId=userId;
    }
    @Column(length=50)
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName=userName;
    }
    @Enumerated(EnumType.STRING) @Column(length=10)
    public NodeType getNodeType(){
        return nodeType;
    }
    public void setNodeType(NodeType nodeType){
        this.nodeType=nodeType;
    }
    @ManyToOne @JoinColumn(name="flow_id")
    public KnFlow getFlow(){
        return flow;
    }
    public void setFlow(KnFlow flow){
        this.flow=flow;
    }
    @ManyToOne @JoinColumn(name="form_id")
    public KnFlowForm getForm(){
        return form;
    }
    public void setForm(KnFlowForm form){
        this.form=form;
    }
    @OneToMany(mappedBy="ka") @Fetch(FetchMode.SUBSELECT) @Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @JsonIgnore
    public Set<KnApprovalVariables> getVariables(){
        return variables;
    }
    public void setVariables(Set<KnApprovalVariables> variables){
        this.variables=variables;
    }
    @OneToMany(mappedBy="ka") @Fetch(FetchMode.SUBSELECT) @Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @JsonIgnore
    public Set<KnApprovalFile> getFile(){
        return file;
    }
    public void setFile(Set<KnApprovalFile> file){
        this.file=file;
    }
    @OneToMany(mappedBy="ka") @Fetch(FetchMode.SUBSELECT) @Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @JsonIgnore
    public Set<KnApprovalProcess> getProcess(){
        return process;
    }
    public void setProcess(Set<KnApprovalProcess> process){
        this.process=process;
    }
    @Column(length=13)
    public Long getCreateTime(){
        return createTime;
    }
    public void setCreateTime(Long createTime){
        this.createTime=createTime;
    }
    public enum NodeType{
        start,process,end
    }
}