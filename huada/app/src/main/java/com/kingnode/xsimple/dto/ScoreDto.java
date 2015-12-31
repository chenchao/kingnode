package com.kingnode.xsimple.dto;
import com.kingnode.xsimple.Setting;
/**
 * app评分表dto
 *
 * @author wangyifan
 */
public class ScoreDto{
    private static final long serialVersionUID=-8680175361028512930L;
    private Long id;
    private String userName;//用户名称
    private String versionNum;//用户下载的版本号
    private String appComment;//应用的评论
    private String rating;//应用的评分
    private Long createTime;//评论时间
    private Setting.VersionType versionType;
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName=userName;
    }
    public String getVersionNum(){
        return versionNum;
    }
    public void setVersionNum(String versionNum){
        this.versionNum=versionNum;
    }
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
    public Long getCreateTime(){
        return createTime;
    }
    public void setCreateTime(Long createTime){
        this.createTime=createTime;
    }
    public Setting.VersionType getVersionType(){
        return versionType;
    }
    public void setVersionType(Setting.VersionType versionType){
        this.versionType=versionType;
    }
    public ScoreDto(){
    }
    public ScoreDto(Long id,String userName,String versionNum,String appComment,String rating,Long createTime,Setting.VersionType versionType){
        this.id=id;
        this.userName=userName;
        this.versionNum=versionNum;
        this.appComment=appComment;
        this.rating=rating;
        this.createTime=createTime;
        this.versionType=versionType;
    }
    public String getVertionTypeName(){
        if(this.versionType==null){
            return "IPHONE";
        }
        return this.versionType.getM_type();
    }
}