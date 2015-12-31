package com.kingnode.auto.entity;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.kingnode.xsimple.entity.AuditEntity;
/**
 * Created by xushuangyong on 14-10-14.
 */
@Entity @Table(name="kn_auto_message")
public class KnAutoMessage extends AuditEntity{
    private String content;//消息内容
    private String title;//消息标题
    private Type type;//消息类型
    private Long bId;//
    private Long sendtime;//发送时间
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content=content;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public Type getType(){
        return type;
    }
    public void setType(Type type){
        this.type=type;
    }
    public Long getbId(){
        return bId;
    }
    public void setbId(Long bId){
        this.bId=bId;
    }
    public Long getSendtime(){
        return sendtime;
    }
    public void setSendtime(Long sendtime){
        this.sendtime=sendtime;
    }
    public enum Type{
        //领车,出车,退订
        LM,CM,TM
    }
}
