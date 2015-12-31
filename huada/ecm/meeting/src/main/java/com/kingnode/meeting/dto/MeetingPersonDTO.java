package com.kingnode.meeting.dto;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@XmlRootElement(name="MeetingPerson")
public class MeetingPersonDTO{
    private Long id;
    private String userName;
    private String imageAddress;
    private Long orgId;//部门id
    private String orgName;//部门名称
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
    public String getImageAddress(){
        return imageAddress;
    }
    public void setImageAddress(String imageAddress){
        this.imageAddress=imageAddress;
    }
    public Long getOrgId(){
        return orgId;
    }
    public void setOrgId(Long orgId){
        this.orgId=orgId;
    }
    public String getOrgName(){
        return orgName;
    }
    public void setOrgName(String orgName){
        this.orgName=orgName;
    }
}
