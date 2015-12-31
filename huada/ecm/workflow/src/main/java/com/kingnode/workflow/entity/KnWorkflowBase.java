package com.kingnode.workflow.entity;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@MappedSuperclass
public class KnWorkflowBase extends AuditEntity{
    private static final long serialVersionUID=1639494528180821291L;
    protected Integer removeTag;//0代表有效  1代表无效
    @Column(name="remove_tag") @NotNull
    public Integer getRemoveTag(){
        if( removeTag == null ) removeTag=0;
        return removeTag;
    }
    public void setRemoveTag(int removeTag){
        this.removeTag=removeTag;
    }
}
