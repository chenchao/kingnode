package com.kingnode.xsimple.util.installation;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
/**
 * .ipa文件就是一个zip压缩包, 其中包含一个info.plist文件即应用的元数据
 * 依赖的jar包：dd-plist.jar
 */
public class IpaAndroidReaderUtil{
    private static final String METADATA_FILE_NAME="info.plist";
    private static IpaAndroidReaderUtil ipaAndroidReaderUtil=null;
    private IpaAndroidReaderUtil(){
    }
    public static IpaAndroidReaderUtil getInstall(){
        if(null==ipaAndroidReaderUtil){
            synchronized(IpaAndroidReaderUtil.class){
                if(ipaAndroidReaderUtil==null){
                    ipaAndroidReaderUtil=new IpaAndroidReaderUtil();
                }
            }
        }
        return ipaAndroidReaderUtil;
    }
    /**
     * 读取IOS的安装包中的info.plist文件中的信息
     *
     * @param ipa ipa文件
     */
    public ApkPlistMetadata getMetadata(File ipa) throws IOException{
        ApkPlistMetadata metadata=null;
        ZipFile zip=new ZipFile(ipa);
        Enumeration<? extends ZipEntry> entries=zip.entries();
        while(entries.hasMoreElements()){
            ZipEntry entry=entries.nextElement();
            String entryName=entry.getName();
            if(entryName.toLowerCase().indexOf(METADATA_FILE_NAME)!=-1){
                metadata=readPlist(zip.getInputStream(entry));
                if(metadata==null||metadata.getVersionNum()==null){
                    continue;
                }
                break;
            }
        }
        return metadata;
    }
    /**
     * 获取plist文件信息
     *
     * @param plist plist文件流
     *
     * @return ApkPlistMetadata对象
     */
    private ApkPlistMetadata readPlist(InputStream plist){
        ApkPlistMetadata metadata=new ApkPlistMetadata();
        try{
            NSDictionary rootDict=(NSDictionary)PropertyListParser.parse(plist);
            metadata.setPackageName(rootDict.objectForKey("CFBundleIdentifier").toString());
            metadata.setDiscription(rootDict.objectForKey("CFBundleDisplayName").toString());
            metadata.setVersionNum(rootDict.objectForKey("CFBundleVersion").toString());
        }catch(Exception e){
            metadata=null;
        }
        return metadata;
    }
    /**
     * 读取android安装包apk包中的版本和包名的信息
     *
     * @param android android的apk包文件
     *
     * @return
     *
     * @throws IOException 文件流IO异常
     */
    public ApkPlistMetadata getAndroidData(File android) throws IOException{
        ApkPlistMetadata metadata=new ApkPlistMetadata();
        String[] str=AnalysisApk.unZip(android.toString(),null);
        metadata.setVersionNum(str[0]);
        metadata.setPackageName(str[1]);
        return metadata;
    }
}