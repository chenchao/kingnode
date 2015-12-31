package com.kingnode.xsimple.plugin.file;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletContext;

import com.google.common.collect.Lists;
import com.kingnode.xsimple.api.common.FileInfo;
import com.kingnode.xsimple.plugin.StoragePlugin;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Component("filePlugin")
public class FilePlugin extends StoragePlugin implements ServletContextAware{
    private ServletContext servletContext;
    public FilePlugin(){
    }
    public void setServletContext(ServletContext servletContext){
        this.servletContext=servletContext;
    }
    public String getName(){
        return "本地文件存储";
    }
    public String getVersion(){
        return "1.0";
    }
    public String getAuthor(){
        return "xSimple";
    }
    public String getSiteUrl(){
        return "http://www.kingnode.com";
    }
    public String getInstallUrl(){
        return null;
    }
    public String getUninstallUrl(){
        return null;
    }
    public String getSettingUrl(){
        return "/file/setting";
    }
    public void upload(String path,File file,String contentType){
        File localFile=new File(this.servletContext.getRealPath(path));
        try{
            FileUtils.moveFile(file,localFile);
        }catch(IOException localIOException){
            localIOException.printStackTrace();
        }
    }
    public String getUrl(String path){
        return path;
    }
    public List<FileInfo> browser(String path){
        return Lists.newArrayList();
    }
}
