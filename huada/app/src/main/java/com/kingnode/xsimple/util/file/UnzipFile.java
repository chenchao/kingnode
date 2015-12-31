package com.kingnode.xsimple.util.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.google.common.base.Strings;
import org.apache.commons.io.FileUtils;

/**
 * 
 *    
 * 项目名称：knd_xSimple_v4   
 * 类名称：UnzipFile   
 * 类描述：   解压文件工具类
 * 创建人：cici   
 * 创建时间：2014-6-25 下午1:29:15   
 * 修改人：Thinkpad   
 * 修改时间：2014-6-25 下午1:29:15   
 * 修改备注：   
 * @version    
 *
 */
public class UnzipFile {
	private static final int buffer = 2048;

	public static void main(String[] args) {
		unZip("D:\\安装说明.zip","D:\\安装说明.zip","index.jsp");
	}
	/**
	 * 
	 * @description 解压保存文件中的某个文件地址
	 * @param path 解压的文件地址
	 * @param savePath 保存的文件地址
	 * @param fileName 解压需要保存的文件名称
	 * @author：cici   
	 * @updateTime：2014-6-25 下午1:23:21
	 */
	public static boolean unZip(String path,String savePath,String fileName) {
		boolean flag = false;
		if(Strings.isNullOrEmpty(path)||Strings.isNullOrEmpty(savePath)||Strings.isNullOrEmpty(fileName)){
			return flag;
		}
		int count = -1;
		File file = null;
		InputStream is = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			ZipFile zipFile = new ZipFile(path);
			Enumeration<?> entries = zipFile.entries();
			ZipEntry zipEntry = null;
			while (entries.hasMoreElements()) {
				zipEntry = (ZipEntry) entries.nextElement();
				if (zipEntry.isDirectory()) {

				} else {
					if(zipEntry.getName().indexOf(fileName)!=-1){
						byte buf[] = new byte[buffer];
						file = new File(savePath);
						FileUtils.forceMkdir(file.getParentFile()); // 创建上传文件所在的父目录
						is = zipFile.getInputStream(zipEntry);
						fos = new FileOutputStream(file);
						bos = new BufferedOutputStream(fos, buffer);
						while ((count = is.read(buf)) > -1) {
							bos.write(buf, 0, count);
						}
						fos.close();
						is.close();
						flag = true;
					}
				}
			}
			zipFile.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			flag = false;
		}
		return flag;
	}
	/**
	 * 
	 * @description 解压保存文件到相应的目录下
	 * @param path 解压的文件地址
	 * @param savePath 保存的文件地址
	 * @author：cici   
	 * @updateTime：2014-6-25 下午1:23:21
	 */
	public static boolean unZipFile(String path,String savePath) {
		boolean flag = false;
		if(Strings.isNullOrEmpty(path)||Strings.isNullOrEmpty(savePath)){
			return flag;
		}
		File file = new File(path);
		try {
			// 解压文件不存在时返回
			if (!file.exists()) {
				return flag;
			}
			File saveFile = new File(savePath);
//			FileUtils.forceMkdir(saveFile.getParentFile()); // 创建上传文件所在的父目录
			FileInputStream fin = new FileInputStream(file);
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry entry = null;
			while ((entry = zin.getNextEntry()) != null) {
//				if (!entry.getName().endsWith(file)) {
//					continue;
//				}
				File tmp = new File(saveFile, entry.getName());
				if (entry.isDirectory()) {
					tmp.mkdirs();
				} else {
					byte[] buff = new byte[4096];
					int len = 0;
					tmp.getParentFile().mkdirs();
					FileOutputStream fout = new FileOutputStream(tmp);
					while ((len = zin.read(buff)) != -1) {
						fout.write(buff, 0, len);
					}
					zin.closeEntry();
					fout.close();
				}
			}
			zin.close();
			flag = true;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			flag = false;
		}
		return flag;
	}
	/**
	 * 
	 * @description 解压保存文件到相应的目录下
	 * @param file 解压的文件
	 * @param savePath 保存的文件地址
	 * @author：cici   
	 * @updateTime：2014-6-25 下午1:23:21
	 */
	public static boolean unZipFile(File file,String savePath) {
		boolean flag = false;
		if(file==null||Strings.isNullOrEmpty(savePath)){
			return flag;
		}
		try {
			// 解压文件不存在时返回
			if (!file.exists()) {
				return flag;
			}
			File saveFile = new File(savePath);
//			FileUtils.forceMkdir(saveFile.getParentFile()); // 创建上传文件所在的父目录
			FileInputStream fin = new FileInputStream(file);
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry entry = null;
			while (zin!=null&&(entry = zin.getNextEntry()) != null) {
//				if (!entry.getName().endsWith(file)) {
//					continue;
//				}
				File tmp = new File(saveFile, entry.getName());
				if (entry.isDirectory()) {
					tmp.mkdirs();
				} else {
					byte[] buff = new byte[4096];
					int len = 0;
					tmp.getParentFile().mkdirs();
					FileOutputStream fout = new FileOutputStream(tmp);
					while ((len = zin.read(buff)) != -1) {
						fout.write(buff, 0, len);
					}
					zin.closeEntry();
					fout.close();
				}
			}
			zin.close();
			flag = true;
		} catch (Exception ioe) {
			ioe.printStackTrace();
			flag = false;
		}
		return flag;
	}
	/**
	 * 
	 * @description 解压保存文件中的某个文件地址
	 * @param path 解压的文件
	 * @param savePath 保存的文件地址
	 * @param fileName 解压需要保存的文件名称
	 * @author：cici   
	 * @updateTime：2014-6-25 下午1:23:21
	 */
	public static boolean unZip(File path,String savePath,String fileName) {
		boolean flag = false;
		if(path==null||Strings.isNullOrEmpty(savePath)||Strings.isNullOrEmpty(fileName)){
			return flag;
		}
		int count = -1;
		File file = null;
		InputStream is = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			ZipFile zipFile = new ZipFile(path);
			Enumeration<?> entries = zipFile.entries();
			ZipEntry zipEntry = null;
			while (entries.hasMoreElements()) {
				zipEntry = (ZipEntry) entries.nextElement();
				if (zipEntry.isDirectory()) {

				} else {
					if(zipEntry.getName().indexOf(fileName)!=-1){
						byte buf[] = new byte[buffer];
						file = new File(savePath);
						FileUtils.forceMkdir(file.getParentFile()); // 创建上传文件所在的父目录
						is = zipFile.getInputStream(zipEntry);
						fos = new FileOutputStream(file);
						bos = new BufferedOutputStream(fos, buffer);
						while ((count = is.read(buf)) > -1) {
							bos.write(buf, 0, count);
						}
						fos.close();
						is.close();
						flag = true;
					}
				}
			}
			zipFile.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			flag = false;
		}
		return flag;
	}
	public static boolean isPics(String filename) {
		boolean flag = false;
		if (filename.endsWith(".jpg") || filename.endsWith(".gif")
				|| filename.endsWith(".bmp") || filename.endsWith(".png")){
			flag = true;
		}
		return flag;
	}
}
