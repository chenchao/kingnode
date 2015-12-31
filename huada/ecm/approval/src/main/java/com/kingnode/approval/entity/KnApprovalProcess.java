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
/**
 * 审批过程信息表
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_approval_process")
public class KnApprovalProcess extends IdEntity{
    private static final long serialVersionUID=8162436627514764160L;
    private KnApproval ka;
    private Long flowId;//预制流程节点ID,来源KnFlowNode表，当存在flowid的时候说明此节点在固定流程中被重新分配人员审批了
    private Long formId;//上一流程ID
    private Long userId;//处理人ID
    private String userName;//处理人姓名
    private String comment;//处理意见
    private ResultType resultType;//结果类型
    private Set<KnApprovalFile> file;//处理人提交得文件
    private Long createTime;
    @ManyToOne @JoinColumn(nullable=false,name="approval_id") @JsonIgnore
    public KnApproval getKa(){
        return ka;
    }
    public void setKa(KnApproval ka){
        this.ka=ka;
    }
    @Column(length=13)
    public Long getFlowId(){
        return flowId;
    }
    public void setFlowId(Long flowId){
        this.flowId=flowId;
    }
    @Column(length=13)
    public Long getFormId(){
        return formId;
    }
    public void setFormId(Long formId){
        this.formId=formId;
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
    @Column(length=500)
    public String getComment(){
        return comment;
    }
    public void setComment(String comment){
        this.comment=comment;
    }
    @Column(length=20) @Enumerated(EnumType.STRING)
    public ResultType getResultType(){
        return resultType;
    }
    public void setResultType(ResultType resultType){
        this.resultType=resultType;
    }
    @OneToMany(mappedBy="kap") @Fetch(FetchMode.SUBSELECT) @Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @JsonIgnore
    public Set<KnApprovalFile> getFile(){
        return file;
    }
    public void setFile(Set<KnApprovalFile> file){
        this.file=file;
    }
    @Column(length=13)
    public Long getCreateTime(){
        return createTime;
    }
    public void setCreateTime(Long createTime){
        this.createTime=createTime;
    }
    public enum ResultType{//同意，拒绝，未查看，审批中，结束，重新发起，撤回
        agree,disagree,noView,process,end,toInitiate,revocation
    }
}
