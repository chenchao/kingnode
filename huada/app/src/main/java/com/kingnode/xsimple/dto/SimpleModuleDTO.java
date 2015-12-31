package com.kingnode.xsimple.dto;
import java.util.List;
/**
 * @author kongjiangwei@kingnode.com
 */
public class SimpleModuleDTO{
    private Long moduleId;
    private Long mSortId;
    private List<SimpleFunctionDTO> simpleFunctionDTOList;
    public List<SimpleFunctionDTO> getSimpleFunctionDTOList(){
        return simpleFunctionDTOList;
    }
    public void setSimpleFunctionDTOList(List<SimpleFunctionDTO> simpleFunctionDTOList){
        this.simpleFunctionDTOList=simpleFunctionDTOList;
    }
    public Long getModuleId(){
        return moduleId;
    }
    public void setModuleId(Long moduleId){
        this.moduleId=moduleId;
    }
    public Long getmSortId(){
        return mSortId;
    }
    public void setmSortId(Long mSortId){
        this.mSortId=mSortId;
    }
}
