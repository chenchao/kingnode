package com.kingnode.xsimple.util.client;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
/**
 * @description 发送http请求
 */
public class HttpUtil{
    @Deprecated
    public static String http(String url,Map<String,String> params){
        URL u=null;
        HttpURLConnection con=null;
        // 构建请求参数
        StringBuffer sb=new StringBuffer();
        if(params!=null){
            for(Entry<String,String> e : params.entrySet()){
                sb.append(e.getKey());
                sb.append("=");
                sb.append(e.getValue());
                sb.append("&");
            }
            sb.substring(0,sb.length()-1);
        }
        // 尝试发送请求
        try{
            u=new URL(url);
            con=(HttpURLConnection)u.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            OutputStreamWriter osw=new OutputStreamWriter(con.getOutputStream(),"UTF-8");
            osw.write(sb.toString());
            osw.flush();
            osw.close();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(con!=null){
                con.disconnect();
            }
        }
        // 读取返回内容
        StringBuffer buffer=new StringBuffer();
        try{
            BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
            String temp;
            while((temp=br.readLine())!=null){
                buffer.append(temp);
                buffer.append("\n");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return buffer.toString();
    }
    /**
     * @param httpUrl    请求的url 1、get提交则将 参数以?形式拼接在请求url后面 2、post提交时 此值为请求地址
     * @param paramValue 1、get提交时此值可以不传 2、post提交时此值为参数值 例如
     *                   username=admin&password=123456&deviceToken=bcc062f
     * @param methodType 请求类型， post为POST提交 ， get 为GET提交
     *
     * @return
     */
    @Deprecated
    public static String sendHttpUrlRequest(String httpUrl,String paramValue,String methodType) throws Exception{
        HttpURLConnection conn=null;
        if("get".equalsIgnoreCase(methodType)){
            conn=(HttpURLConnection)new URL(httpUrl).openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
        }else{
            conn=(HttpURLConnection)new URL(httpUrl).openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length",String.valueOf(paramValue.length()));
            conn.getOutputStream().write(paramValue.getBytes());
        }
        if(conn.getResponseCode()==200){
            return readIO(conn.getInputStream());
        }
        return "";
    }
    @Deprecated
    public static String readIO(InputStream in) throws IOException{
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        int len=0;
        // InputStream in = request.getInputStream() ;
        byte[] buffer=new byte[1024];
        while((len=in.read(buffer))!=-1){
            out.write(buffer,0,len);
        }
        String jsonStr=out.toString();
        in.close();
        out.close();
        return jsonStr;
    }
}