package com.kingnode.xsimple.entity.system;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.entity.AuditEntity;
/**
 * 反馈意见表,最多只能提供10个答案
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_problem_back")
public class KnFeedProblem extends AuditEntity{
    private static final long serialVersionUID=8291013028043283936L;
    private String probContext ; //问题的内容
    private Setting.FeedProblenType feedProblenType ; //答案
    public KnFeedProblem(){

    }
    public KnFeedProblem(String probContext,Setting.FeedProblenType feedProblenType){
        this.probContext=probContext;
        this.feedProblenType=feedProblenType;
    }
    @Column(name="prob_context",length=300)
    public String getProbContext(){
        return probContext;
    }
    public void setProbContext(String probContext){
        this.probContext=probContext;
    }
    @Column(name="feed_problen_type",length=20) @Enumerated(EnumType.STRING)
    public Setting.FeedProblenType getFeedProblenType(){
        return feedProblenType;
    }
    public void setFeedProblenType(Setting.FeedProblenType feedProblenType){
        this.feedProblenType=feedProblenType;
    }


}
