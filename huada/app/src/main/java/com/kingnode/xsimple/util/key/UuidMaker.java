package com.kingnode.xsimple.util.key;

import java.util.UUID;
/**
 * 
 * UUID生成工具类
 * @author cici
 *
 */
public class UuidMaker {
	private static UuidMaker maker;
	public static UuidMaker getInstance(){
		if(maker==null){
			synchronized (UuidMaker.class) {
				maker = new UuidMaker();
			}
		}
		return maker;
	}
	/**
	 * 获取随机唯一性的UUID
	 * @param flag true表示去除中间的-,false表示不去除中间的-
	 * @return
	 */
	public String getUuid(boolean flag){
		String uuid = UUID.randomUUID().toString();
		if(flag){
			uuid = uuid.replaceAll("-", "");
		}
		return uuid;
	}

}
