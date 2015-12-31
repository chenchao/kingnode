package com.kingnode.xsimple.util.client;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.kingnode.diva.mapper.JsonMapper;
/**
 * 访问Rest的service工具类
 *
 * @author cici
 */
public class RestServiceUtil{
    private static final String CHARSET="utf-8"; // 设置编码
    private static int httpTimeout=1000000;
    /**
     * 获取请求参数字符串
     *
     * @param serviceName 请求的服务名
     * @param ja          请求的参数
     *
     * @return 请求参数字符串
     */
    @Deprecated
    public static String getRequestString(String serviceName,Map ja,String type){
        Map<String,Object> json=new HashMap<>();
        json.put("type","rest");
        json.put("to",type);
        json.put("service",serviceName);
        json.put("data",ja);
        return JsonMapper.nonEmptyMapper().toJson(json);
    }
    @Deprecated
    public static String getStringResponse(String request,String url,boolean isPost){
        return getStringResponse(request,url,isPost,httpTimeout);
    }
    @Deprecated
    private static String getStringResponse(String request,String url,boolean isPost,int timeoutInMS){
        ByteArrayOutputStream outputStream=getResponse(request,url,isPost,timeoutInMS);
        if(outputStream!=null){
            try{
                return outputStream.toString("UTF-8");
            }catch(UnsupportedEncodingException e){
                return null;
            }
        }
        return null;
    }
    @Deprecated
    public static ByteArrayOutputStream getResponse(String request,String url,boolean isPost,int timeoutInMS){
        HttpURLConnection conn=null;
        InputStream inputStream=null;
        DataOutputStream out=null;
        try{
            URL uUrl;
            if(isPost){
                uUrl=new URL(url);
                conn=(HttpURLConnection)uUrl.openConnection();
                conn.setRequestMethod("POST");
            }else{
                if(request!=null&&!request.isEmpty()){
                    if(url.contains("?")){
                        url=url+"&"+request;
                    }else{
                        url=url+"?"+request;
                    }
                }
                uUrl=new URL(url);
                conn=(HttpURLConnection)uUrl.openConnection();
                conn.setRequestMethod("GET");
            }
            conn.setRequestProperty("Proxy-Connection","Keep-Alive");
            conn.setReadTimeout(timeoutInMS);
            conn.setConnectTimeout(timeoutInMS);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            if(isPost){
                out=new DataOutputStream(conn.getOutputStream());
                if(request!=null){
                    out.write(request.getBytes());
                }
                out.flush();
            }
            inputStream=conn.getInputStream();
            byte[] bytes=new byte[1024];
            int len=0;
            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
            while((len=inputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
            }
            return outputStream;
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(out!=null){
                try{
                    out.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            if(inputStream!=null){
                try{
                    inputStream.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            if(conn!=null){
                conn.disconnect();
            }
        }
        return null;
    }
}
