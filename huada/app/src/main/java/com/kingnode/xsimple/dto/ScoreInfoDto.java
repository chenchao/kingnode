package com.kingnode.xsimple.dto;
/**
 * app评分表dto
 *
 * @author wangyifan
 */
public class ScoreInfoDto{
    private String appComment;//应用的评论
    private String rating;//应用的评分
    private String plateForm;//平台区分
    private String appkey;//应用标示
    private String verType;//版本状态
    public String getAppComment(){
        return appComment;
    }
    public void setAppComment(String appComment){
        this.appComment=appComment;
    }
    public String getRating(){
        return rating;
    }
    public void setRating(String rating){
        this.rating=rating;
    }
    public String getPlateForm(){
        return plateForm;
    }
    public void setPlateForm(String plateForm){
        this.plateForm=plateForm;
    }
    public String getAppkey(){
        return appkey;
    }
    public void setAppkey(String appkey){
        this.appkey=appkey;
    }
    public String getVerType(){
        return verType;
    }
    public void setVerType(String verType){
        this.verType=verType;
    }
}