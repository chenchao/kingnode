package com.kingnode.xsimple.util.freemark;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Strings;
import com.kingnode.xsimple.entity.application.KnVersionInfo;
import com.kingnode.xsimple.util.PathUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
*
 * 生成IOS需要的plist文件,根据freemarker模板进行获取
 *
*/
public class WriteToPlist{
	private String freemarker = "/WEB-INF/classes/freemarker";
    private  final Logger log = LoggerFactory.getLogger(WriteToPlist.class);
    private static WriteToPlist writeToPlist = null;
    private WriteToPlist() {}

    public static WriteToPlist getInstall() {
        if (null == writeToPlist) {
            synchronized (WriteToPlist.class) {
                writeToPlist = new WriteToPlist();
            }
        }
        return writeToPlist;
    }
    /**
	 * 根据模板生成plist文件
	 * 文件中所需的信息：
	 * 1.安装包路径 2.图片路径 3.应用的包名 4.版本号 5.应用的名称
	 * @param version 版本
	 * @param packageName 包名
	 * @param address 安装包地址 ipa
	 * @param ios7Address ios7的https协议存放的地址
     * @param ios7FileAddress  //主要针对ios7的plisth和ipa信息
     * @param localUrl  访问路径信息
     * @param ios7SynCmd  //写入文件信息
     * @param synchronizePlist  //是否需要同步plist文件到c环境
     */
	public String writeToPlist(KnVersionInfo version,String address,String packageName,String ios7Address,String ios7FileAddress,String localUrl,String ios7SynCmd,String synchronizePlist) throws IOException{
        StringBuffer sbf=new StringBuffer();
        try{
            boolean flag = false;
            Map<String,String> map = new HashMap<String, String>();
            if(version!=null){
                map.put("packageName",packageName==null?"":packageName);//应用的包名
                map.put("imgAddress", version.getImgAddress()==null?"":version.getImgAddress());//图片地址
                map.put("appTitle", version.getApplicationInfo().getTitle()==null?"":version.getApplicationInfo().getTitle());//应用名称
                map.put("versionNum", version.getNum()==null?"":version.getNum());//应用版本号
                map.put("localUrl", localUrl==null?"":localUrl);
                map.put("address", address==null?"":address);//安装包地址
                String path = PathUtil.getRootPath();//获取项目的路径
                String writeFile = path + version.getAddress();//向文件中读写数据
                try{
                    flag = FreeMarker.getInstance().saveToFile("ios_plist.ftl", map, path+freemarker,writeFile);
                }catch(Exception e){}
                if(flag){
                    log.info("ios本地的plist文件生成成功");
                    sbf.append("ios本地的plist文件生成成功.");
                }else{
                    log.info("ios本地的plist文件生成失败");
                    sbf.append("ios本地的plist文件生成失败.");
                }
                //向https路径的针对ios7的plist信息
                if(!Strings.isNullOrEmpty(ios7Address)){
                    File iosWriteFile = new File(ios7FileAddress+"/"+ios7Address);
                    FileUtils.forceMkdir(iosWriteFile.getParentFile()); // 创建上传文件所在的父目录
                    //iosWriteFile.toString()
                    flag= FreeMarker.getInstance().saveToFile("ios_plist.ftl", map, path+freemarker,iosWriteFile.toString());
                    if(flag){
                        sbf.append("远程https的ios7的plist文件生成成功.");
                        log.info("ios7的plist文件生成成功");
                        if("linux".equalsIgnoreCase(System.getProperty("os.name"))){
                            try {
                                //执行赋权限操作
                                Process pro = Runtime.getRuntime().exec("/bin/chmod -R 755 "+ios7FileAddress);
                                pro.waitFor();//当前线程等待命令行执行结束
                                log.info("plist授权成功");
                                sbf.append("远程https的ios7的plist文件授权成功.");
                                pro.waitFor();
                                pro.destroy();
                            } catch (InterruptedException e) {
                                log.error("远程https的ios7的plist文件授权,错误信息，{}",e);
                            }
                            //plist文件执行成功,执行linux命令. TODO: cici . 此命令是执行文件信息同步的命令,可能上传同步的地址会有变化
                            if("true".equalsIgnoreCase(synchronizePlist)){//是否需要同步至c环境
                                if(!Strings.isNullOrEmpty(ios7SynCmd)){
                                    //									String executeOrder = "rsync -rvztopg --progress /opt/httpd_v4/htdocs/pool/   root@c.kingnode.com::pool  --password-file=/etc/rsync.pass";
                                    log.debug("ios7SynCmd-->"+ios7SynCmd);
                                    Process pro = Runtime.getRuntime().exec(ios7SynCmd);
                                    try {
                                        pro.waitFor();
                                    } catch (InterruptedException e) {
                                        log.error("plist同步,错误信息，{}",e);
                                    }
                                    log.debug("plist同步结束");
                                    pro.destroy();
                                }
                            }
                        }
                    }else{
                        sbf.append("远程https的ios7的plist文件生成失败.");
                        log.info("远程https的ios7的plist文件生成失败");
                    }
                }
            }
        }catch(Exception e){
            log.error("ios的plist文件生成失败,错误信息{}",e);
        }
		return sbf.toString();
	}
}
