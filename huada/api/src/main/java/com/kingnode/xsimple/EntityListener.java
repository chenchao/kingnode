package com.kingnode.xsimple;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.kingnode.xsimple.entity.AuditEntity;
import org.apache.shiro.SecurityUtils;
public class EntityListener{
    @PrePersist
    public void prePersist(AuditEntity ae){
        ae.setCreateTime(System.currentTimeMillis());
        ae.setUpdateTime(System.currentTimeMillis());
        ae.setCreateId(id());
        ae.setUpdateId(id());
    }
    @PreUpdate
    public void preUpdate(AuditEntity ae){
        ae.setUpdateId(id());
        ae.setUpdateTime(System.currentTimeMillis());
    }
    private Long id(){
        try{
            ShiroUser user=(ShiroUser)SecurityUtils.getSubject().getPrincipal();
            if(user!=null){
                return user.getId();
            }else{
                return 0L;
            }
        }catch(Exception e){
            return 0L;
        }
    }
}