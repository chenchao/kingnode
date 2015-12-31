package com.kingnode.meeting.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_meeting_equipment") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class KnMeetingEquipment extends AuditEntity{
    private static final long serialVersionUID=-8837651634145265982L;
    public KnMeetingEquipment(Long id){
        this.id=id;
    }
    public KnMeetingEquipment(){
    }
    private String name;
    public String icon;
    private EquipmentStatus status;
    @Column(length=200)
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    @Column(length=300)
    public String getIcon(){
        return icon;
    }
    public void setIcon(String icon){
        this.icon=icon;
    }
    @Enumerated(EnumType.STRING) @Column(length=10)
    public EquipmentStatus getStatus(){
        return status;
    }
    public void setStatus(EquipmentStatus status){
        this.status=status;
    }
    public enum EquipmentStatus{//设备状态 可用、不可用
        AVAILABLE,DISABLED
    }
}