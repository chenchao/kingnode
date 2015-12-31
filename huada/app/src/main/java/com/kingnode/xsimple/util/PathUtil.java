package com.kingnode.xsimple.util;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
/**
 * 获取路径工具类
 * @author cici
 */
public class PathUtil{
	/**
	 * 获取项目的绝对路径
	 * @return 项目的绝对路径
	 */
	public static String getRootPath() {
		String classPath="",rootPath  = "";
		try {
			//防止有空格,%20等的出现
			classPath = URLDecoder.decode(PathUtil.class.getClassLoader().getResource("/").getPath(),"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(!"".equals(classPath)){
			//windows下
			if("\\".equals(File.separator)){
				rootPath  = classPath.substring(1,classPath.indexOf("/WEB-INF/classes"));
				rootPath = rootPath.replace("/", "\\");
			}
			//linux下
			if("/".equals(File.separator)){
				rootPath  = classPath.substring(0,classPath.indexOf("/WEB-INF/classes"));
				rootPath = rootPath.replace("\\", "/");
			}
		}
		return rootPath;
	}
	/**
	 * 获取访问者用户的ip地址
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
	    String ip = request.getHeader("x-forwarded-for");
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("Proxy-Client-IP");
	    }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("WL-Proxy-Client-IP");
	    }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getRemoteAddr();
	    }
	    return ip;
	}
}
