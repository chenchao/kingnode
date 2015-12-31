package com.kingnode.xsimple.controller.platform;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.api.system.SysPropInfo;
import com.kingnode.xsimple.util.PathUtil;
import org.joda.time.DateTime;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * 平台配置
 * @author 王伟强
 */
@Controller @RequestMapping(value="platform")
public class AllocationService{
    Resource resource=new ClassPathResource("/clientInfo.properties");
    @RequestMapping(value="allocation")
    public String allocation() throws IOException{
        return "platform/allocation";
    }
    @RequestMapping(value="setup/list") @ResponseBody
    public DataTable<SysPropInfo> list(DataTable<SysPropInfo> dt) throws IOException{
        Properties props=PropertiesLoaderUtils.loadProperties(resource);
        List<SysPropInfo> sysList=Lists.newArrayList();
        if(null!=props){
            Enumeration en=props.propertyNames();
            while(en.hasMoreElements()){
                String key=(String)en.nextElement();
                String value=props.getProperty(key);
                if(!Strings.isNullOrEmpty(key)&&!Strings.isNullOrEmpty(value)){
                    SysPropInfo sysObj=new SysPropInfo();
                    sysObj.setKey(key);
                    sysObj.setValue(value);
                    sysList.add(sysObj);
                }
            }
            dt.setAaData(sysList);
        }
        return dt;
    }
    @RequestMapping(value="setup/update-properties") @ResponseBody
    public Map updateProperties(@RequestParam(value="key") String key,@RequestParam(value="value") String value,@RequestParam(value="tip") String tip) throws IOException{
        Map map=Maps.newHashMap();
        Properties props=PropertiesLoaderUtils.loadProperties(resource);
        String path=PathUtil.getRootPath()+File.separatorChar+"WEB-INF"+File.separatorChar+"classes"+File.separatorChar+"clientInfo.properties";
        StringBuffer finalKey=new StringBuffer(DateTime.now().toString("yyyy-MM-dd HH:mm:ss")+"更新的字段如下：--->");
        if(!Strings.isNullOrEmpty(key)&&!Strings.isNullOrEmpty(value)){
            props.setProperty(key,value);
            FileOutputStream fos=new FileOutputStream(path);
            finalKey.append(",'").append(key).append("'");
            props.store(fos,"Update '"+finalKey+"' value");
            fos.close();// 关闭流
        }
        if(tip.equals("1")){
            map.put("status","true");
            map.put("message","新增成功");
        }else{
            map.put("status","true");
            map.put("message","编辑成功");
        }
        return map;
    }
    private List<SysPropInfo> readFileByLines(String fileName){
        BufferedReader reader=null;
        List<SysPropInfo> sysList=new ArrayList<>();
        try{
            String tempString=null;
            reader=new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"UTF-8"));
            while((tempString=reader.readLine())!=null){
                if(!Strings.isNullOrEmpty(tempString)){
                    SysPropInfo sysObj=new SysPropInfo();
                    sysObj.setValue(tempString);
                    sysList.add(sysObj);
                }
            }
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            if(reader!=null){
                try{
                    reader.close();
                }catch(IOException e){
                }
            }
        }
        return sysList;
    }
    @RequestMapping(value="setup/show-note")
    public String showNote() throws IOException{
        return "platform/allocationNote";
    }
    @RequestMapping(value="setup/show-index") @ResponseBody
    public DataTable<SysPropInfo> showIndex(DataTable<SysPropInfo> dt) throws Exception{
        String path=PathUtil.getRootPath()+File.separatorChar+"WEB-INF"+File.separatorChar+"classes"+File.separatorChar+"/notes/clientInfo.txt";
        List<SysPropInfo> sysList=readFileByLines(path);
        dt.setAaData(sysList);
        return dt;
    }
}
