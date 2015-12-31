package com.kingnode.xsimple.util.message;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.kingnode.xsimple.util.ReadPropfileUtil;
import com.kingnode.xsimple.util.Utils;
import com.kingnode.xsimple.util.xml.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*
*
 *   发送手机的验证码信息
 *
*/
public class SendPhoneMsg{
    private static SendPhoneMsg sendPhoneMsg=null;
    private final Logger log=LoggerFactory.getLogger(SendPhoneMsg.class);
    private String f_sendMsgUrl=ReadPropfileUtil.getInstall().prop.getProperty("sendMsgUrl") ;// 短信发送接口的请求的url地址
    private String f_sendMsgAccount=ReadPropfileUtil.getInstall().prop.getProperty("sendMsgAccount"); // 短信发送接口的用户名
    private String f_sendMsgPwd=ReadPropfileUtil.getInstall().prop.getProperty("sendMsgUrl");// 短信发送接口的密码
    private String f_sendMsgID=ReadPropfileUtil.getInstall().prop.getProperty("sendMsgID");// 企业id
    private String f_sendMsgContent=ReadPropfileUtil.getInstall().prop.getProperty("sendMsgContent");// 短信发送内容
    private SendPhoneMsg(){
    }
    public static SendPhoneMsg getInstall(){
        if(null==sendPhoneMsg){
            synchronized(SendPhoneMsg.class){
                if(sendPhoneMsg==null){
                    sendPhoneMsg=new SendPhoneMsg();
                }
            }
        }
        return sendPhoneMsg;
    }

    /**
     * 根据手机号和验证码信息发送短信信息
     * @param phone 手机号
     * @param codeNum  验证码
     * @return 短信发送状态,true表示发送成功,false表示发送失败
     */
    public boolean sendByPhoneNum(String phone,String codeNum,String sendMsgUrl,String sendMsgAccount,String sendMsgContent,String sendMsgID,String sendMsgPwd){
        boolean flag = false;
        if(Utils.isEmptyString(phone)||Utils.isEmptyString(codeNum)){
            return flag;
        }
        if(Utils.isEmptyString(sendMsgUrl)){
            return flag;
        }
        f_sendMsgUrl =Utils.isEmptyString(sendMsgUrl)?f_sendMsgUrl:sendMsgUrl;
        f_sendMsgID =Utils.isEmptyString(sendMsgID)?f_sendMsgID:sendMsgID;
        f_sendMsgAccount =Utils.isEmptyString(sendMsgAccount)?f_sendMsgAccount:sendMsgAccount;
        f_sendMsgPwd =Utils.isEmptyString(sendMsgPwd)?f_sendMsgPwd:sendMsgPwd;
        f_sendMsgContent =Utils.isEmptyString(sendMsgContent)?f_sendMsgUrl:sendMsgContent;
        try {
            String temp = f_sendMsgContent.replace("${codeNum}", codeNum);
            StringBuffer sbf = new StringBuffer();
            sbf.append("sname=").append(f_sendMsgAccount).append("&spwd=").append(f_sendMsgPwd).append("&scorpid=")
                   .append("&sprdid=").append(f_sendMsgID).append("&sdst=")
                    .append(phone).append("&smsg=").append(java.net.URLEncoder.encode(temp,"utf-8"));
            String result = SMS(sbf.toString(), f_sendMsgUrl);
            if(Utils.isEmptyString(result)){
                return flag;
            }
            if("0".equals(XmlUtil.getCenterStr(result,"State"))){
                flag = true;
            }
            return flag;
        } catch (Exception e) {
            flag = false;
            log.error("短信发送异常,错误信息{}",e);
        }
        return flag;
    }
    /**
     *
     * 根据发送的地址和信息发送短信信息
     * @param postData 发送的信息,包含用户名密码,短信内容,企业id等
     * @param postUrl 发送的短信接口地址
     * @return 返回的响应的字符串信息
     */
    private String SMS(String postData, String postUrl) {
        try {
            //发送POST请求
            URL url = new URL(postUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Length", String.valueOf(postData.getBytes().length));
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out.write(postData);
            out.flush();
            out.close();

            //获取响应状态
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                log.info("短信连接失败");
                return "";
            }
            //获取响应内容体
            String line, result = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            while ((line = in.readLine()) != null) {
                result += line + "\n";
            }
            in.close();
            return result;
        } catch (Exception e) {
            log.error("短信发送异常,错误信息{}",e);
            e.printStackTrace();
        }
        return "";
    }
}