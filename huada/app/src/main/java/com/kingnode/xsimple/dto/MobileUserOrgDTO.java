package com.kingnode.xsimple.dto;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

import com.kingnode.xsimple.rest.RestStatus;
/**
 * @author kongjiangwei@kingnode.com
 */
@XmlRootElement(name="MobileUserOrgInfo")
public class MobileUserOrgDTO extends RestStatus{
    //获取注册信息
    private String newUrl;//最新版本的路径
    private String vf;
    private Long regTime;//最后一次登录时间
    private FullEmployeeDTO fullEmployeeDTO;//员工信息
    private List<RoleDTO> roleDTOList;
    private String kimDomain;//kim的demo地址
    private String kimHost;//kim的host地址
    private String kndCloudUrl;//云端的地址
    private List<ModuleDTO> moduleDTOList;
    private List<SimpleModuleDTO> simpleModuleDTOList;
    //检查设备是否在线
    private String deviceName;//设备名称
    private Long lastTime;//最后一次登录时间
    private String deviceType;//设备状态
    public List<ModuleDTO> getModuleDTOList(){
        return moduleDTOList;
    }
    public void setModuleDTOList(List<ModuleDTO> moduleDTOList){
        this.moduleDTOList=moduleDTOList;
    }
    public List<SimpleModuleDTO> getSimpleModuleDTOList(){
        return simpleModuleDTOList;
    }
    public void setSimpleModuleDTOList(List<SimpleModuleDTO> simpleModuleDTOList){
        this.simpleModuleDTOList=simpleModuleDTOList;
    }
    public FullEmployeeDTO getFullEmployeeDTO(){
        return fullEmployeeDTO;
    }
    public void setFullEmployeeDTO(FullEmployeeDTO fullEmployeeDTO){
        this.fullEmployeeDTO=fullEmployeeDTO;
    }
    public List<RoleDTO> getRoleDTOList(){
        return roleDTOList;
    }
    public void setRoleDTOList(List<RoleDTO> roleDTOList){
        this.roleDTOList=roleDTOList;
    }
    public String getNewUrl(){
        return newUrl;
    }
    public void setNewUrl(String newUrl){
        this.newUrl=newUrl;
    }
    public String getVf(){
        return vf;
    }
    public void setVf(String vf){
        this.vf=vf;
    }
    public Long getRegTime(){
        return regTime;
    }
    public void setRegTime(Long regTime){
        this.regTime=regTime;
    }
    public String getKimDomain(){
        return kimDomain;
    }
    public void setKimDomain(String kimDomain){
        this.kimDomain=kimDomain;
    }
    public String getKndCloudUrl(){
        return kndCloudUrl;
    }
    public void setKndCloudUrl(String kndCloudUrl){
        this.kndCloudUrl=kndCloudUrl;
    }
    public String getKimHost(){
        return kimHost;
    }
    public void setKimHost(String kimHost){
        this.kimHost=kimHost;
    }
    public String getDeviceName(){
        return deviceName;
    }
    public void setDeviceName(String deviceName){
        this.deviceName=deviceName;
    }
    public Long getLastTime(){
        return lastTime;
    }
    public void setLastTime(Long lastTime){
        this.lastTime=lastTime;
    }
    public String getDeviceType(){
        return deviceType;
    }
    public void setDeviceType(String deviceType){
        this.deviceType=deviceType;
    }
}
