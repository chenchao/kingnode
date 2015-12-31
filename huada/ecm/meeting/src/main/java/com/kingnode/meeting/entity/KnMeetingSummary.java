package com.kingnode.meeting.entity;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_meeting_summary") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class KnMeetingSummary extends AuditEntity{
    private static final long serialVersionUID=-9122159342965099582L;
    @Lob
    private String summary;
    public String getSummary(){
        return summary;
    }
    public void setSummary(String summary){
        this.summary=summary;
    }
}