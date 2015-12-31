package com.kingnode.xsimple.dto;
import java.util.List;
/**
 * @author kongjiangwei@kingnode.com
 */
public class ModuleDTO extends SimpleModuleDTO{
    private String mtitle;
    private String mVersion;
    private String k2;
    private List<FunctionDTO> functionDTOList;
    public String getK2(){
        return k2;
    }
    public void setK2(String k2){
        this.k2=k2;
    }
    public String getmVersion(){
        return mVersion;
    }
    public void setmVersion(String mVersion){
        this.mVersion=mVersion;
    }
    public String getMtitle(){
        return mtitle;
    }
    public void setMtitle(String mtitle){
        this.mtitle=mtitle;
    }
    public List<FunctionDTO> getFunctionDTOList(){
        return functionDTOList;
    }
    public void setFunctionDTOList(List<FunctionDTO> functionDTOList){
        this.functionDTOList=functionDTOList;
    }
}
