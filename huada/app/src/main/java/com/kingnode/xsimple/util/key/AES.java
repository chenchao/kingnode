package com.kingnode.xsimple.util.key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.kingnode.xsimple.util.ReadPropfileUtil;
public class AES {

    //TODO:wangyifan 属性注解
	private static final String aesKey = ReadPropfileUtil.getInstall().prop.getProperty("aesKey") ;//用户密码AES加密的key
    //TODO:wangyfian 属性注解
	private static final String ivParameter =  ReadPropfileUtil.getInstall().prop.getProperty("aesKey") ;//于AES的key一致
	/**
	 *  数据解密,解密的字符串和解密的key,key不传输则获取属性中的key值
	 * @param sSrc
	 * @param sKey
	 * @return
	 * @throws Exception
	 */
	public static String Decrypt(String sSrc, String sKey) throws Exception {
		try {
			// 判断Key是否正确
			if (sKey == null||"".equals(sKey)) {
				sKey = aesKey;
			}
			if (sKey == null) {
				return null;
			}
			// 判断Key是否为16位
			if (sKey.length() != 16) {
				return null;
			}
			try {
				byte[] raw = sKey.getBytes("ASCII");
				SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
				cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
//				byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);// 先用base64解密
				byte[] encrypted1 = hex2byte(sSrc);
				byte[] original = cipher.doFinal(encrypted1);
				return  new String(original, "utf-8");
			} catch (Exception ex) {
				return null;
			}
		} catch (Exception ex) {
			return null;
		}
	}

	// 判断Key是否正确
	/**
	 * 数据加密,加密的字符串和加密的key,key不传输则获取属性中的key值
	 */
	public static String Encrypt(String sSrc, String sKey) throws Exception {
		if (sKey == null||"".equals(sKey)) {
			sKey = aesKey;
		}
		if (sKey == null) {
			return null;
		}
		// 判断Key是否为16位
		if (sKey.length() != 16) {
			return null;
		}
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] raw = sKey.getBytes("ASCII");
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
		return  byte2hex(encrypted).toLowerCase();
//		return new BASE64Encoder().encode(encrypted);// 此处使用BASE64做转码。
	}
	/**
	 * 字符串转byte数组
	 * @param strhex
	 * @return
	 */
	public static byte[] hex2byte(String strhex) {
		if (strhex == null) {
			return null;
		}
		int l = strhex.length();
		if (l % 2 == 1) {
			return null;
		}
		byte[] b = new byte[l / 2];
		for (int i = 0; i != l / 2; i++) {
			b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2),
					16);
		}
		return b;
	}
	/**
	 * byte数组转字符串
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}
}