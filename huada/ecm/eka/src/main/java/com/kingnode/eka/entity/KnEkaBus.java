package com.kingnode.eka.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 * 公交路线
 */
@Entity @Table(name="kn_eka_bus")
public class KnEkaBus extends AuditEntity{
    private Long ekaId;//壹卡会id
    private String title;//标题
    private String comment;//备注

    public Long getEkaId(){
        return ekaId;
    }
    public void setEkaId(Long ekaId){
        this.ekaId=ekaId;
    }
    @Column(length=200)
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    @Lob
    public String getComment(){
        return comment;
    }
    public void setComment(String comment){
        this.comment=comment;
    }
}
