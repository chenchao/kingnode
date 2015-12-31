package com.kingnode.eka.dto;
import java.util.List;

import com.kingnode.xsimple.entity.IdEntity;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class KnEkaDTO{
    private String image;//图片
    private String name;//名称
    private String call;//电话
    private String address;//地址
    private String distance;//距离
    private String instructions;//商家说明
    private String longitude;//经度
    private String latitude;//纬度
    private Long id;

    private Long praiseNums;//被赞次数
    private Long treadNums;//被踩次数

    private String star;//星级
    private IdEntity.ActiveType isAgreement;//是否是协议

    private List<KnEkaBusDTO> busDTOs;

    private KnEkaCommentLogDTO commentLogDTO;
    public KnEkaCommentLogDTO getCommentLogDTO(){
        return commentLogDTO;
    }
    public void setCommentLogDTO(KnEkaCommentLogDTO commentLogDTO){
        this.commentLogDTO=commentLogDTO;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public String getImage(){
        return image;
    }
    public void setImage(String image){
        this.image=image;
    }
    public String getCall(){
        return call;
    }
    public void setCall(String call){
        this.call=call;
    }
    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address=address;
    }
    public String getDistance(){
        return distance;
    }
    public void setDistance(String distance){
        this.distance=distance;
    }
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
    public List<KnEkaBusDTO> getBusDTOs(){
        return busDTOs;
    }
    public void setBusDTOs(List<KnEkaBusDTO> busDTOs){
        this.busDTOs=busDTOs;
    }
    public IdEntity.ActiveType getIsAgreement(){
        return isAgreement;
    }
    public void setIsAgreement(IdEntity.ActiveType isAgreement){
        this.isAgreement=isAgreement;
    }
    public String getStar(){
        return star;
    }
    public void setStar(String star){
        this.star=star;
    }
}
