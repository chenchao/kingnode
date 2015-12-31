package com.kingnode.xsimple.util.xml;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * xml解析工具类
 */
public class XmlUtil{
    /**
     * 获取xml中子节点的字符串
     *
     * @param xml
     * @param xmlName
     *
     * @return
     */
    public static String getCenterStr(String xml,String xmlName){
        String str="";
        //获取以>{ 开头  }</ 结尾的字符串
        Pattern mpattern=Pattern.compile("(?<="+xmlName+"\\>\\s?)([\\s\\S]*?)(?=\\s?\\</"+xmlName+")");
        Matcher mmatcher=mpattern.matcher(xml);
        while(mmatcher.find()){
            str+=(mmatcher.group());
        }
        str+="";
        return str;
    }
}
