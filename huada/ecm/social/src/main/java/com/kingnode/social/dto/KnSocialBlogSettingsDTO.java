package com.kingnode.social.dto;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@XmlRootElement(name="KnSocialBlogSettings")
public class KnSocialBlogSettingsDTO{
    private String userName;
    private String email;
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName=userName;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email=email;
    }
}
