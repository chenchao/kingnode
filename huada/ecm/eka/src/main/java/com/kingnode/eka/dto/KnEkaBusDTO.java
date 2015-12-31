package com.kingnode.eka.dto;
import com.kingnode.eka.entity.KnEkaCommentLog;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class KnEkaBusDTO{
    private Long ekaId;//壹卡会id
    private String title;//标题
    private String comment;//备注
    public Long getEkaId(){
        return ekaId;
    }
    public void setEkaId(Long ekaId){
        this.ekaId=ekaId;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public String getComment(){
        return comment;
    }
    public void setComment(String comment){
        this.comment=comment;
    }
}
