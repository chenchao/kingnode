package com.kingnode.xsimple.dto;
/**
 * @author kongjiangwei@kingnode.com
 */
public class SimpleFunctionDTO{
    private Long functionId;
    private Long fSortId;
    public Long getFunctionId(){
        return functionId;
    }
    public void setFunctionId(Long functionId){
        this.functionId=functionId;
    }
    public Long getfSortId(){
        return fSortId;
    }
    public void setfSortId(Long fSortId){
        this.fSortId=fSortId;
    }
}
