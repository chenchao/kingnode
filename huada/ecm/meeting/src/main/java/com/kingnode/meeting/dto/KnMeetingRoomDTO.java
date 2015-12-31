package com.kingnode.meeting.dto;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@XmlRootElement(name="MeetingRoom")
public class KnMeetingRoomDTO{
    private Long id;
    private String icon;//会议室图标
    private String name;//会议室名称，
    private String addr;// 会议室地址，
    private Integer num;// 可容纳人数，
    private String remark;// 会议室备注
    private List equipment;// 会议室设备码;
    private List equipmentIcon;// 会议室设备图片;
    private List<KnScheduleDTO> schedule;
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getAddr(){
        return addr;
    }
    public void setAddr(String addr){
        this.addr=addr;
    }
    public Integer getNum(){
        return num;
    }
    public void setNum(Integer num){
        this.num=num;
    }
    public String getRemark(){
        return remark;
    }
    public void setRemark(String remark){
        this.remark=remark;
    }
    public List getEquipment(){
        return equipment;
    }
    public void setEquipment(List equipment){
        this.equipment=equipment;
    }
    public List<KnScheduleDTO> getSchedule(){
        return schedule;
    }
    public void setSchedule(List<KnScheduleDTO> schedule){
        this.schedule=schedule;
    }
    public List getEquipmentIcon(){
        return equipmentIcon;
    }
    public void setEquipmentIcon(List equipmentIcon){
        this.equipmentIcon=equipmentIcon;
    }
    public String getIcon(){
        return icon;
    }
    public void setIcon(String icon){
        this.icon=icon;
    }
}
