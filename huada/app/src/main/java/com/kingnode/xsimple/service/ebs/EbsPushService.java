package com.kingnode.xsimple.service.ebs;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(name="ebsPush",targetNamespace=WsConstants.NS)
public interface EbsPushService{
    /**
     * <推送消息>
     *
     * @param fromUser       发送者
     * @param toUser         接收者
     * @param subject         消息内容
     * @param type            消息类型 暂时为（desktop，function）
     * @param appKey          应用标示
     * @param fromSys         用户来自系统
     * @param fid             功能的短码
     * @param num             消息数目
     * @param icons           功能的图标  标示推送消息显示
     *
     * @return String  返回结果, true 为推送成功 false 为失败 以及其他信息
     *
     * @see [类、类#方法、类#成员]
     */
    public String pushInfo(@WebParam(name="fromUser") String fromUser,@WebParam(name="toUser") String toUser,@WebParam(name="subject") String subject,@WebParam(name="type") String type,@WebParam(name="appKey") String appKey,@WebParam(name="fromSys") String fromSys,@WebParam(name="fid") String fid,@WebParam(name="num") String num,@WebParam(name="icons") String icons) throws Exception;
    /**
     * <推送消息>
     *
     * @param fromUser       发送者
     * @param account         接收者账号信息
     * @param subject         消息内容
     * @param type            消息类型 暂时为（desktop，function）
     * @param appKey          应用标示
     * @param fromSys         用户来自系统
     * @param fid             功能的短码
     * @param num             消息数目
     * @param icons           功能的图标  标示推送消息显示
     *
     * @return String  返回结果, true 为推送成功 false 为失败 以及其他信息
     *
     * @see [类、类#方法、类#成员]
     */
    public String pushByAccountInfo(@WebParam(name="fromUser") String fromUser,@WebParam(name="account") String account,@WebParam(name="subject") String subject,@WebParam(name="type") String type,@WebParam(name="appKey") String appKey,@WebParam(name="fromSys") String fromSys,@WebParam(name="fid") String fid,@WebParam(name="num") String num,@WebParam(name="icons") String icons) throws Exception;
}