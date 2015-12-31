package com.kingnode.xsimple.entity;
import java.text.SimpleDateFormat;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
/**
 * JPA 基类的标识
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@MappedSuperclass
public abstract class UUIDEntity implements java.io.Serializable{
    private static final long serialVersionUID=4460663878609057486L;
    private String id;// 主键ID
    private Long createTime;// 创建时间
    private Long updateTime;// 更新时间
    private String ext1;// 扩展字段
    private String ext2;// 扩展字段
    @Id @GeneratedValue(generator="uuidHex") @GenericGenerator(name="uuidHex", strategy="uuid.hex") @Length(max=40)
    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id=id;
    }
    @Column(name="create_time")
    public Long getCreateTime(){
        return this.createTime;
    }
    public void setCreateTime(Long createTime){
        this.createTime=createTime;
    }
    @Column(name="update_time")
    public Long getUpdateTime(){
        return this.updateTime;
    }
    public void setUpdateTime(Long updateTime){
        this.updateTime=updateTime;
    }
    @Column(name="ext1",length = 500)
    public String getExt1(){
        return this.ext1;
    }
    public void setExt1(String ext1){
        this.ext1=ext1;
    }
    @Column(name="ext2",length = 500)
    public String getExt2(){
        return this.ext2;
    }
    public void setExt2(String ext2){
        this.ext2=ext2;
    }
    private Integer page;// 当前页码
    private Integer rows;// 每页显示数目
    @Transient
    public Integer getPage(){
        return page;
    }
    public void setPage(Integer page){
        this.page=page;
    }
    @Transient
    public Integer getRows(){
        return rows;
    }
    public void setRows(Integer rows){
        this.rows=rows;
    }
    /**
     * 获取创建时间的时间串 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    @Transient
    public String getCreateTimeStr(){
        if(this.createTime==null){
            return null;
        }
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(this.createTime);
    }
    /**
     * 获取创建时间的时间串 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    @Transient
    public String getCreateTimeYearStr(){
        if(this.createTime==null){
            return null;
        }
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(this.createTime);
    }
    @Transient
    public String getUpdateTimeStr(){
        if(this.updateTime==null){
            return null;
        }
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(this.updateTime);
    }
    @Transient
    public boolean isEmptyString(Object s){
        return (s==null)||(s.toString().trim().length()==0)||s.toString().trim().equalsIgnoreCase("null");
    }
}
