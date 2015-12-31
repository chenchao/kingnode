package com.kingnode.meeting.entity;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kingnode.diva.utils.Collections3;
import com.kingnode.xsimple.entity.AuditEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_meeting_room") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class KnMeetingRoom extends AuditEntity{
    private static final long serialVersionUID=1982164496564428956L;
    private String name;//会议室名称，
    private String addr;// 会议室地址，
    private Integer num;// 可容纳人数，
    private Set<KnMeeting> km;// 会议;
    private Set<KnMeetingEquipment> kme;// 会议室设备码;
    private String remark;// 会议室备注
    private String icon;//会议室图标
    private RoomStatus status;
    private String useState;//会议室使用状态。使用中，空闲
    private String equipmentName="";
    @Column(length=200)
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    @Column(length=200)
    public String getAddr(){
        return addr;
    }
    public void setAddr(String addr){
        this.addr=addr;
    }
    @Column(length=20)
    public Integer getNum(){
        return num;
    }
    public void setNum(Integer num){
        this.num=num;
    }
    @Column(length=200)
    public String getRemark(){
        return remark;
    }
    public void setRemark(String remark){
        this.remark=remark;
    }
    @ManyToMany @JoinTable(name="kn_meeting_room_equipment",joinColumns={@JoinColumn(name="room_id")},inverseJoinColumns={@JoinColumn(name="equipment_id")})
    @Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @Fetch(FetchMode.SUBSELECT)
    @OrderBy("id ASC")
    public Set<KnMeetingEquipment> getKme(){
        return kme;
    }
    public void setKme(Set<KnMeetingEquipment> kme){
        this.kme=kme;
    }
    @OneToMany @Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @Fetch(FetchMode.SUBSELECT)
    public Set<KnMeeting> getKm(){
        return km;
    }
    public void setKm(Set<KnMeeting> km){
        this.km=km;
    }
    @Column(length=200)
    public String getIcon(){
        return icon;
    }
    public void setIcon(String icon){
        this.icon=icon;
    }
    @Enumerated(EnumType.STRING) @Column(length=10)
    public RoomStatus getStatus(){
        return status;
    }
    public void setStatus(RoomStatus status){
        this.status=status;
    }
    @Transient
    public String getUseState(){
        return useState;
    }
    public void setUseState(String useState){
        this.useState=useState;
    }
    @Transient
    public String getEquipmentName(){
        if(this.kme!=null&&this.kme.size()>0){
            equipmentName=Collections3.extractToList(this.kme,"name").toString();
        }
        return equipmentName;
    }
    public enum RoomStatus{//会议室状态 可用、不可用
        AVAILABLE,DISABLED
    }
}
