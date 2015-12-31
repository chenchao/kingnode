package com.kingnode.xsimple.rest;
import java.util.List;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class ListDTO<T> extends RestStatus{
    public List<T> list;
    public ListDTO(){
    }
    public ListDTO(Boolean status,List<T> list){
        super(status);
        this.list=list;
    }
    public List<T> getList(){
        return list;
    }
    public void setList(List<T> list){
        this.list=list;
    }
}