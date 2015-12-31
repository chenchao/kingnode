package com.kingnode.xsimple.dto;
/**
 * @author kongjiangwei@kingnode.com
 */
public class FunctionDTO extends SimpleFunctionDTO{
    
    private String ftitle;
    private String k2;
    private String zip;
    private String zipSize;
    private String zipVersion;
    private String status;
    private String fIcon;
    private String fVersion;
    private String interfaceUrl;
    private String funckey;

    public String getZipVersion(){
        return zipVersion;
    }
    public void setZipVersion(String zipVersion){
        this.zipVersion=zipVersion;
    }
    public String getFtitle(){
        return ftitle;
    }
    public void setFtitle(String ftitle){
        this.ftitle=ftitle;
    }
    public String getK2(){
        return k2;
    }
    public void setK2(String k2){
        this.k2=k2;
    }
    public String getZip(){
        return zip;
    }
    public void setZip(String zip){
        this.zip=zip;
    }
    public String getZipSize(){
        return zipSize;
    }
    public void setZipSize(String zipSize){
        this.zipSize=zipSize;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status=status;
    }
    public String getfIcon(){
        return fIcon;
    }
    public void setfIcon(String fIcon){
        this.fIcon=fIcon;
    }
    public String getfVersion(){
        return fVersion;
    }
    public void setfVersion(String fVersion){
        this.fVersion=fVersion;
    }
    public String getInterfaceUrl(){
        return interfaceUrl;
    }
    public void setInterfaceUrl(String interfaceUrl){
        this.interfaceUrl=interfaceUrl;
    }
    public String getFunckey(){
        return funckey;
    }
    public void setFunckey(String funckey){
        this.funckey=funckey;
    }
}
