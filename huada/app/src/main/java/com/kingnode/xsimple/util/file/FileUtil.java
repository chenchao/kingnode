package com.kingnode.xsimple.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.kingnode.xsimple.util.PathUtil;
import org.apache.commons.io.FileUtils;
import sun.misc.BASE64Decoder;

public class FileUtil {
    private static FileUtil fileUtil;
    private FileUtil(){
    }
    public static FileUtil getInstance(){
        if(fileUtil==null){
            synchronized(FileUtil.class){
                if(fileUtil==null){
                    fileUtil = new FileUtil();
                }
            }
        }
        return fileUtil;
    }

    /**
     * 将base64编码的字符串转换成图片保存到相应的路径
     * @param imageInf 图片 字符串
     * @param path 保存的路径 只能为字符类型或者为File类型
     * @return 是否保存成功状态
     * @throws IOException IO读写错误
     */
    public static boolean saveFileToDisk(String imageInf, Object path) throws IOException {//对字节数组字符串进行Base64解码并生成图片
        if (imageInf == null) {
            //图像数据为空
            return false;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        //Base64解码
        byte[] b = decoder.decodeBuffer(imageInf);
        for (int i = 0; i < b.length; ++i) {
            if (b[i] < 0) {//调整异常数据
                b[i] += 256;
            }
        }
        //生成jpeg图片
        OutputStream out = null;
        if (path instanceof String) {
            out = new FileOutputStream((String) path);
        }
        if (path instanceof File) {
            out = new FileOutputStream((File) path);
        } else {
            return false;
        }
        out.write(b);
        out.flush();
        out.close();
        return true;
    }

    /**
     * 刪除文件或文件夾
     * @param fileAddress 文件或文件夾的完整路径
     */
    public void deleteFile(String fileAddress) {
        File file = new File(fileAddress);
        if (file.isDirectory()) {// 是一文件
            delFolder(fileAddress);
        } else if (file.exists()) {
            file.delete();// 删除数据
        }
    }

    /**
     *  删除文件夹
     * @param folderPath 文件夹完整绝对路径
     */
    public void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除空的文件夹下空的文件夹操作
     * @param list 文件夹集合
     */
    public void deleteNullFile(List<File> list) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                File temp = list.get(i);
                if (temp.isDirectory() && temp.listFiles().length <= 0) {
                    temp.delete();
                }
            }
        }
    }

    /**
     *  获取指定文件夹下删除空的文件夹下空的文件夹操作
     * @param path 文件夹完整绝对路径
     * @return 删除的状态
     */
    public void deleteNullFileDirectory(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isDirectory()) {
                if (temp.listFiles().length <= 0) {
                    temp.delete();
                } else {
                    deleteNullFileDirectory(path + "/" + tempList[i]);// 再删除空文件夹
                }
            }
        }
    }

    /**
     *  删除指定文件夹下所有文件
     * @param path 文件夹完整绝对路径
     * @return
     */
    public boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    /**
     *  获取指定文件夹下所有文件
     * @param path 文件夹完整绝对路径
     * @return 文件夹集合
     */
    public List<File> getAllFile(String path) {
        List<File> fileList = new ArrayList<File>();
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        if (!file.isDirectory()) {
            return null;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                fileList.add(temp);
            }
            if (temp.isDirectory()) {
                List<File> f = getAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                if (f != null) {
                    fileList.addAll(f);
                }
            }
        }
        return fileList;
    }
    /**
     * 文件上传的工具类
     * @param icon  要上传的文件
     * @param iconFileName 上传的文件名称
     * @author dengfeng
     * return 文件的保存位置
     */
    /*public  String  uploadFile(File icon
            , String iconFileName){
        String fileSavePath = "" ;
        try {
            if( icon!=null && icon.length()!=0){
                String realPath = ServletActionContext.getServletContext().getRealPath( AddressEntity.appLogoAddress ) ;
                fileSavePath = UuidMaker.getInstance().getUuid(false)+iconFileName.substring(iconFileName.lastIndexOf(".") ) ;
                File file = new File(realPath) ;
                if(!file.exists())file.mkdirs() ;
                FileUtils.copyFile(icon,
                        new File( file , fileSavePath) ) ;

                fileSavePath = AddressEntity.appLogoAddress+"/"+ fileSavePath ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileSavePath ;
    }*/

    /**
     *<上传文件到tomcat>
     * @param uploadFile 文件
     * @param targetDirectory 路径
     * @param uploadFileFileName 文件名字
     * @param currtTime 时间戳
     * @param tip  标示图标上传或是zip上传
     * @param version 版本号
     * @throws java.io.IOException
     * @see [类、类#方法、类#成员]
     */
    public void copyFileToTomcat(File uploadFile, String targetDirectory, String uploadFileFileName, String currtTime, String tip, String version) throws IOException {
        String realFileName = "";
        if (tip.equals("2")) {
            realFileName = currtTime + uploadFileFileName.substring(uploadFileFileName.lastIndexOf("."));
        } else {
            realFileName = currtTime + "_" + uploadFileFileName;
        }
        File target = new File(targetDirectory, realFileName);
        FileUtils.copyFile(uploadFile, target);
    }

    /**
     * 根据上传控件，保存到相应的路径路径下
     * @param savedir 父级文件夹
     * @param address 文件名称
     * @param uploadFile 上传的文件控件
     * @return
     */
    public boolean saveFile(File savedir, String address, File uploadFile) {
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        File saveFile = new File(savedir, address);//版本号加上文件名为上传的路径
        File writeFile = new File(saveFile + "/");
        boolean b = false;
        try {
            FileInputStream fis = new FileInputStream(uploadFile);
            bis = new BufferedInputStream(fis);
            FileOutputStream fos = new FileOutputStream(writeFile);
            bos = new BufferedOutputStream(fos);
            byte[] buf = new byte[2 * 1024 * 1024];
            int len = -1;
            while ((len = bis.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            b = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != bis) {
                    bis.close();
                }
                if (null != bos) {
                    bos.close();
                }
                if (!b) {
                    if (writeFile.exists()) {
                        writeFile.delete();//删除上传失败数据
                    }
                }
            } catch (IOException e) {
            }
        }
        return b;
    }

    /**
     *<获取文件的大大小>
     * @param path 文件绝对路径
     * @return 文件大小
     * @throws IOException
     * @see [类、类#方法、类#成员]
     */
    public String getFileSize(String path) throws IOException {
        String size = "0";
        try {
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            size = fis.available() + "";
        } catch (Exception e) {
        }
        return size;
    }

    /**
     * 根据文件读取文件中的字符串信息
     * @param fileName 文件
     * @return 文件中的字符串
     */
    public String readDate(File fileName) {
        // 定义一个待返回的空字符串
        StringBuffer sb = new StringBuffer();
        try {
            FileReader read = new FileReader(fileName);
            char ch[] = new char[1024];
            int d = read.read(ch);
            while (d != -1) {
                String str = new String(ch, 0, d);
                sb.append(str);
                d = read.read(ch);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 测试文件或目录是否存在  存在就返回文件,不存在返回null
     * @author dengfeng
     * @param path  文件的相对路径  例如：img/logo.jpg
     * @return
     */
    public  File  existFile(String path){
        File file = new File( PathUtil.getRootPath() + File.separatorChar + path) ;
        if(file.exists()){
            return file ;
        }else{
            return null ;
        }
    }
    /**
     * 根据浏览器的url访问文件的地址进行下载
     * @param url 访问的url
     * @param saveAddress 下载的路径地址
     * @return 保存是否成功
     */
    public boolean saveFileByUrl(String url,String saveAddress){
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        boolean b = false;
        File saveFile = new File(saveAddress);//版本号加上文件名为上传的路径
        try {
            URL resourceUrl = new URL(url);
            URLConnection conn = resourceUrl.openConnection();
            FileUtils.forceMkdir(saveFile.getParentFile()); // 创建上传文件所在的父目录
            InputStream inputStream = conn.getInputStream();
            bis = new BufferedInputStream(inputStream);
            FileOutputStream fos = new FileOutputStream(saveFile);
            bos = new BufferedOutputStream(fos);
            byte[] buf = new byte[2*1024*1024];
            int len = -1;
            while ((len = bis.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            b = true;
        } catch (Exception e) {
            b = false;
        } finally {
            try {
                if (null != bis) {
                    bis.close();
                }
                if (null != bos) {
                    bos.close();
                }
                if(!b){
                    if(saveFile.exists()){
                        saveFile.delete();//删除上传失败数据
                    }
                }
            } catch (IOException e) {
            }
        }
        return b;
    }

    /**
     *
     * @description 该目录及所有子目录下是否包含文件
     * @param path
     * @return
     * @author：kongjiangwei
     * @updateTime：May 22, 2014 5:43:08 PM
     */
    public boolean isHasFile(String path){
        String separator=File.separator;
        File file = new File(path);
        String[] tempList = file.list();
        File temp = null;
        boolean bl = false;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + separator + tempList[i]);
            }
            if (temp.isFile()) {
                bl=true;
                break;
            }else if (temp.isDirectory()) {
                bl=isHasFile(path + separator + tempList[i],bl);
            }
        }
        return bl;
    }

    /**************
     *
     * @description 是否子目录下含有文件
     * @param path
     * @param bl
     * @return
     * @author：kongjiangwei
     * @updateTime：May 19, 2014 5:54:27 PM
     */
    private boolean isHasFile(String path,boolean bl){
        String separator=File.separator;
        File file = new File(path);
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + separator + tempList[i]);
            }
            if (temp.isFile()) {
                bl=true;
                break;
            }else if (temp.isDirectory()) {
                isHasFile(path + separator + tempList[i],bl);
            }
        }
        return bl;
    }
}

