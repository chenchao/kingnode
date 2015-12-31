package com.kingnode.eka.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_eka")
public class KnEka extends AuditEntity{
    private String image;//图片
    private String name;//名称
    private String acall;//电话
    private String address;//地址
    private Long distance;//距离
    private String instructions;//商家说明
    private String longitude;//经度
    private String latitude;//纬度
    private Long praiseNums;//被赞次数
    private Long treadNums;//被赞次数

    private KnEkaCategories categories;//一卡会  大类型
    private KnEkaSmallClass small;//一卡会  小类型
    private ActiveType isAgreement;//是否是协议
    private Long star;//星级

    @Enumerated(EnumType.STRING)
    public ActiveType getIsAgreement(){
        return isAgreement;
    }
    public void setIsAgreement(ActiveType isAgreement){
        this.isAgreement=isAgreement;
    }
    public Long getStar(){
        return star;
    }
    public void setStar(Long star){
        this.star=star;
    }
    public Long getPraiseNums(){
        return praiseNums;
    }
    public void setPraiseNums(Long praiseNums){
        this.praiseNums=praiseNums;
    }
    public Long getTreadNums(){
        return treadNums;
    }
    public void setTreadNums(Long treadNums){
        this.treadNums=treadNums;
    }
    @ManyToOne @JoinColumn(nullable=false, name="category_id") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    public KnEkaCategories getCategories(){
        return categories;
    }
    public void setCategories(KnEkaCategories categories){
        this.categories=categories;
    }
    @ManyToOne(optional=true) @JoinColumn(name="small_id") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    public KnEkaSmallClass getSmall(){
        return small;
    }
    public void setSmall(KnEkaSmallClass small){
        this.small=small;
    }
    @Column(length=100)
    public String getImage(){
        return image;
    }
    public void setImage(String image){
        this.image=image;
    }
    @Column(length=100)
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    @Column(length=50)
    public String getAcall(){
        return acall;
    }
    public void setAcall(String acall){
        this.acall=acall;
    }

    @Column(length=500)
    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address=address;
    }
    public Long getDistance(){
        return distance;
    }
    public void setDistance(Long distance){
        this.distance=distance;
    }
    @Column(length=200)
    public String getInstructions(){
        return instructions;
    }
    public void setInstructions(String instructions){
        this.instructions=instructions;
    }
    public String getLongitude(){
        return longitude;
    }
    public void setLongitude(String longitude){
        this.longitude=longitude;
    }
    public String getLatitude(){
        return latitude;
    }
    public void setLatitude(String latitude){
        this.latitude=latitude;
    }
}
