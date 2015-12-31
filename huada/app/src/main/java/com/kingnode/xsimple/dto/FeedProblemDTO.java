package com.kingnode.xsimple.dto;
import com.kingnode.xsimple.Setting;
/**
 * @author dengfeng@kingnode.com (dengfeng)
 */
public class FeedProblemDTO{
    private String probContext ; //问题的内容
    private Setting.FeedProblenType feedProblenType ; //答案
    public FeedProblemDTO(){

    }
    public FeedProblemDTO(String probContext,Setting.FeedProblenType feedProblenType){
        this.probContext=probContext;
        this.feedProblenType=feedProblenType;
    }
    public String getProbContext(){
        return probContext;
    }
    public void setProbContext(String probContext){
        this.probContext=probContext;
    }
    public Setting.FeedProblenType getFeedProblenType(){
        return feedProblenType;
    }
    public void setFeedProblenType(Setting.FeedProblenType feedProblenType){
        this.feedProblenType=feedProblenType;
    }
}
