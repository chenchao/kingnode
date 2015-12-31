package com.kingnode.xsimple.util.freemark;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 读取模版信息
 */
public final class FreeMarker{

	private static FreeMarker freeMarker ;

	private FreeMarker() {
	}

	/**
	 * 获取单例对象实例
	 * 
	 * @return FreeMarker
	 */
	public static FreeMarker getInstance() {
		if (freeMarker == null) {
			synchronized (FreeMarker.class) {
				if(freeMarker==null){
					freeMarker = new FreeMarker();
				}
			}
		}
		return freeMarker;
	}
	
	/**
	 * 使用freemarker 生成的代码
	 * @param templateName 模板的文件名称,位于模板文件夹路径下
	 * @param map 需要输出的map集合
	 * @param path 模板文件夹路径
	 * @return 模板输出的字符串,后期有需要可以当成html进行输出
	 */
	public  String getTemplateInfo(String templateName,
			Map<String, String> map,String path) {
		String xmlInfo = "";
		try {
			Configuration cfg = new Configuration();
			cfg.setDirectoryForTemplateLoading(new File(path));// 加载模版文件夹

			Template template = cfg.getTemplate(templateName);// 模版文件

			StringWriter out = new StringWriter();
			template.process(map, out);

			xmlInfo = out.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlInfo;
	}
	/**
	 * 使用freemarker 生成的代码
	 * @param templateName 模板的文件名称,位于模板文件夹路径下
	 * @param map 需要输出的map集合
	 * @param path 模板文件夹路径
	 * @param writeFile 保存的文件路径
	 * @return 模板输出的字符串,后期有需要可以当成html进行输出
	 */
	public  boolean  saveToFile(String templateName,
			Map<String, String> map,String path,String writeFile) {
		boolean flag = false;
		try {
			Configuration cfg = new Configuration();
			cfg.setDirectoryForTemplateLoading(new File(path));// 加载模版文件夹

			Template template = cfg.getTemplate(templateName,"utf-8");// 模版文件
			
			StringWriter out = new StringWriter();
			template.process(map, out);
			File file = new File(writeFile);
			file.setExecutable(true,false);//设置可执行权限  
			file.setReadable(true,false);//设置可读权限  
			file.setWritable(true,false);//设置可写权限  
			FileOutputStream fos = new FileOutputStream(file); 
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8"); 
			try {
				osw.write(out.toString());
				osw.flush();
				osw.close();
				flag = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

}
