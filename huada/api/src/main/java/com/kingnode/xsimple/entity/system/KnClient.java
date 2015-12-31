package com.kingnode.xsimple.entity.system;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.IdEntity;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_client")
public class KnClient extends IdEntity{
    private static final long serialVersionUID=2943224462216976491L;
    private String clientName;
    private String clientId;
    private String clientSecret;
    public String getClientName(){
        return clientName;
    }
    public void setClientName(String clientName){
        this.clientName=clientName;
    }
    public String getClientId(){
        return clientId;
    }
    public void setClientId(String clientId){
        this.clientId=clientId;
    }
    public String getClientSecret(){
        return clientSecret;
    }
    public void setClientSecret(String clientSecret){
        this.clientSecret=clientSecret;
    }
}
