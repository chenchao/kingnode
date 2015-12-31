package com.kingnode.news.dto;
import com.kingnode.news.entity.KnNotice;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class KnNoticeDTO{
    private Long id;
    private String title;//标题
    private String pubTime;//发布时间
    private String pubUserName;//发布者姓名）
    private String content;//（内容）
    private String orgName;//发布者组织名称
    private String img;//（如果有图片请提供）
    private KnNotice.ReceiptType isReceipt;
    private String hasHuizhi;//（是否回执）
    private int alreadyReceipt;//已回执人数;
    private int needReceipt;//需要回执人数
    private String effectiveTime;//有效时间
    private String dept;//发布人部门
    private String isNeedReceipt;//是否需要回执  需要 返回ture 不需要 发你false
    public String getIsNeedReceipt(){
        return isNeedReceipt;
    }
    public void setIsNeedReceipt(String isNeedReceipt){
        this.isNeedReceipt=isNeedReceipt;
    }
    public String getHasHuizhi(){
        return hasHuizhi ;
    }
    public void setHasHuizhi(String hasHuizhi){
        this.hasHuizhi=hasHuizhi;
    }
    public String getDept(){
        return dept ;
    }
    public void setDept(String dept){
        this.dept=dept;
    }
    public String getEffectiveTime(){
        return effectiveTime ;
    }
    public void setEffectiveTime(String effectiveTime){
        this.effectiveTime=effectiveTime;
    }

    public int getNeedReceipt(){
        return needReceipt;
    }
    public void setNeedReceipt(int needReceipt){
        this.needReceipt=needReceipt;
    }
    public int getAlreadyReceipt(){
        return alreadyReceipt;
    }
    public void setAlreadyReceipt(int alreadyReceipt){
        this.alreadyReceipt=alreadyReceipt;
    }
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
    public String getPubTime(){
        return pubTime;
    }
    public void setPubTime(String pubTime){
        this.pubTime=pubTime;
    }
    public String getPubUserName(){
        return pubUserName;
    }
    public void setPubUserName(String pubUserName){
        this.pubUserName=pubUserName;
    }
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content=content;
    }
    public String getOrgName(){
        return orgName;
    }
    public void setOrgName(String orgName){
        this.orgName=orgName;
    }
    public String getImg(){
        return img;
    }
    public void setImg(String img){
        this.img=img;
    }
    public KnNotice.ReceiptType getIsReceipt(){
        return isReceipt;
    }
    public void setIsReceipt(KnNotice.ReceiptType isReceipt){
        this.isReceipt=isReceipt;
    }
}
