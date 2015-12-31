package com.kingnode.xsimple.entity.system;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.kingnode.xsimple.entity.AuditEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
/**
 * 数据字典表
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @Cacheable(true) @Entity @Table(name="kn_sys_item")
public class KnSysItem extends AuditEntity{
    private static final long serialVersionUID=1896310466100582760L;
    private String itemId;//项目ID
    private String objId;//对象ID
    private String annexa;//说明A
    private String annexb;//说明B
    private String annexc;//说明C
    private String annexd;//说明D
    private String annexe;//说明E
    private Long seq=0L;//排序列
    private ActiveType active;//是否有效 1:有效 0无效
    @NotNull @Column(length=50)
    public String getItemId(){
        return itemId;
    }
    public void setItemId(String itemId){
        this.itemId=itemId;
    }
    @NotNull @Column(length=50)
    public String getObjId(){
        return objId;
    }
    public void setObjId(String objId){
        this.objId=objId;
    }
    @NotNull @Column(length=100)
    public String getAnnexa(){
        return annexa;
    }
    public void setAnnexa(String annexa){
        this.annexa=annexa;
    }
    @Column(length=200)
    public String getAnnexb(){
        return annexb;
    }
    public void setAnnexb(String annexb){
        this.annexb=annexb;
    }
    @Column(length=300)
    public String getAnnexc(){
        return annexc;
    }
    public void setAnnexc(String annexc){
        this.annexc=annexc;
    }
    @Column(length=400)
    public String getAnnexd(){
        return annexd;
    }
    public void setAnnexd(String annexd){
        this.annexd=annexd;
    }
    @Column(length=500)
    public String getAnnexe(){
        return annexe;
    }
    public void setAnnexe(String annexe){
        this.annexe=annexe;
    }
    @NotNull @Column(length=4)
    public Long getSeq(){
        return seq;
    }
    public void setSeq(Long seq){
        this.seq=seq;
    }
    @NotNull @Enumerated(EnumType.STRING) @Column(length=10)
    public ActiveType getActive(){
        return active;
    }
    public void setActive(ActiveType active){
        this.active=active;
    }
}
