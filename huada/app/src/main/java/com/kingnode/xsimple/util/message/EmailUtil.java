package com.kingnode.xsimple.util.message;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 邮件发送工具类,
 * 所需的jar包，mail.jar,
 *
 * @author caichune
 */
public class EmailUtil{
    //TODO:wangyifan
    private static String emailHostName="";//ReadClientProper.prop.getProperty("emailHostName");
    private static String emailAddress="";//ReadClientProper.prop.getProperty("emailAddress");
    private static String emailPwd="";//ReadClientProper.prop.getProperty("emailPwd");
    //	private static String emailFrom = ReadClientProper.prop.getProperty("emailFrom");
    private static String emailAuthor="";//ReadClientProper.prop.getProperty("emailAuthor");
    private static String emailTitle="";//ReadClientProper.prop.getProperty("emailTitle");
    private static final Logger log=LoggerFactory.getLogger(EmailUtil.class);
    /**
     * 邮件发送
     *
     * @param emailAddr 发送地址
     * @param emailName 发送昵称
     * @param msg       发送内容
     *
     * @return 是否发送的状态
     */
    public static boolean sendEmail(String emailAddr,String emailName,String msg){
        HtmlEmail email=new HtmlEmail();
        email.setHostName(emailHostName);//设置使用发电子邮件的邮件服务器  smtp.163.com  ,smtp.gmail.com,email.cimc.com
        email.setAuthentication(emailAddress,emailPwd);//email.setAuthentication("发件人信箱", "发件人信箱密码");//smtp认证的用户名和密码
        email.setCharset("UTF-8");
        StringBuffer sb=new StringBuffer();
        try{
            email.addTo(emailAddr,emailName);//email.addTo("收件人信箱", "收件人别名");
            email.setFrom(emailAddress,emailAuthor);//必须和Authentication使用的用户相同，否则失败.
            email.setSubject(emailTitle);//标题
            email.setHtmlMsg(msg);//内容
            email.send();
            sb.append("邮件发送成功,发送的内容为{发件人邮箱:").append(emailAddress).append(",收件人邮箱:").append(emailAddr).append(",发送的内容:").append(msg);
            log.info(sb.toString());
            return true;
        }catch(EmailException e){
            sb.append("邮件发送失败,发送的内容为{发件人邮箱:").append(emailAddress).append(",收件人邮箱:").append(emailAddr).append(",发送的内容:").append(msg);
            log.error(sb.toString()+",异常信息为{}",e);
            return false;
        }
    }
}
