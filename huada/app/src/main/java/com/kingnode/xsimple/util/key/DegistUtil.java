package com.kingnode.xsimple.util.key;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;
/**
 * 加密算法的实现工具类
 *
 */
public class DegistUtil {
	//建议查看JDK帮助文档MessageDigest类
	/**
	 * 对数据进行加密的算法.,过程是不可逆的,是目前比较安全的算法.
	 * @param seq 要进行加密的字符串
	 * @return 已经加密过后的字符串
	 */
	public static String produceDegistCode(String seq){
		if(seq==null){
			return null;
		}
		MessageDigest md;
		try {
			//md5是不可逆的
			md = MessageDigest.getInstance("md5");
			byte[] buf = md.digest(seq.getBytes());
			//BASE64是可逆的
			BASE64Encoder encoder = new BASE64Encoder();
			String md5Str = encoder.encode(buf);
			return md5Str;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

}
