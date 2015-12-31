package com.kingnode.xsimple.entity.application;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * 存放版本信息的图片
 * @author cici
 */
@Entity @Table(name="kn_version_image_info")
public class KnVersionImageInfo extends AuditEntity{
    private static final long serialVersionUID=5175476691600765445L;
    private String imgAddress;//图片存放地址
    private Long versionId;//所属的版本的id
    private String imageSize;//图片的大小
    private Integer isort;// 图片排序
    @Column(name="img_address",length=100)
    public String getImgAddress(){
        return imgAddress;
    }
    public void setImgAddress(String imgAddress){
        this.imgAddress=imgAddress;
    }
    @Column(name="version_id",length=40)
    public Long getVersionId(){
        return versionId;
    }
    public void setVersionId(Long versionId){
        this.versionId=versionId;
    }
    @Column(name="image_size",length=20)
    public String getImageSize(){
        return imageSize;
    }
    public void setImageSize(String imageSize){
        this.imageSize=imageSize;
    }
    @Column(name="is_ort",length=7)
    public Integer getIsort(){
        return isort;
    }
    public void setIsort(Integer isort){
        this.isort=isort;
    }
}