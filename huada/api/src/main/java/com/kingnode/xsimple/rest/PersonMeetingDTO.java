package com.kingnode.xsimple.rest;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@XmlRootElement(name="PersonMeeting")
public class PersonMeetingDTO{
    private String date;
    public String getDate(){
        return date;
    }
    public void setDate(String date){
        this.date=date;
    }
}
