package com.kingnode.xsimple.entity.system;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Entity @Table(name="kn_position_branched_passage") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE) @Cacheable(true)
public class KnPositionBranchedPassage extends IdEntity{
    private static final long serialVersionUID=-2088138765108989320L;
    private Long posId;//岗位
    private String posCode;//岗位编码
    private String posName;//岗位名称
    private Long leaderId;//领导人ID
    private String leaderName;//领导人姓名
    private Long subordinateId;//下属人
    private String subordinateName;//下属姓名
    @Column(name="pos_id",length=13)
    public Long getPosId(){
        return posId;
    }
    public void setPosId(Long posId){
        this.posId=posId;
    }
    @Column(name="pos_code",length=10)
    public String getPosCode(){
        return posCode;
    }
    public void setPosCode(String posCode){
        this.posCode=posCode;
    }
    @Column(name="pos_name",length=100)
    public String getPosName(){
        return posName;
    }
    public void setPosName(String posName){
        this.posName=posName;
    }
    @Column(name="leader_id",length=13)
    public Long getLeaderId(){
        return leaderId;
    }
    public void setLeaderId(Long leaderId){
        this.leaderId=leaderId;
    }
    @Column(name="leader_name",length=100)
    public String getLeaderName(){
        return leaderName;
    }
    public void setLeaderName(String leaderName){
        this.leaderName=leaderName;
    }
    @Column(name="subordinate_id",length=13)
    public Long getSubordinateId(){
        return subordinateId;
    }
    public void setSubordinateId(Long subordinateId){
        this.subordinateId=subordinateId;
    }
    @Column(name="subordinate_name",length=100)
    public String getSubordinateName(){
        return subordinateName;
    }
    public void setSubordinateName(String subordinateName){
        this.subordinateName=subordinateName;
    }
}