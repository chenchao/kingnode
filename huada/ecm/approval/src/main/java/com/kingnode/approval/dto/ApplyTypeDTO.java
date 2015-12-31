package com.kingnode.approval.dto;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@XmlRootElement(name="applyType")
public class ApplyTypeDTO{
    private Long formId;
    private String name;
    private Integer num;
    public Long getFormId(){
        return formId;
    }
    public void setFormId(Long formId){
        this.formId=formId;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public Integer getNum(){
        return num;
    }
    public void setNum(Integer num){
        this.num=num;
    }
}
