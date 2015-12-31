package com.kingnode.news.rest;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Lists;
import com.kingnode.diva.mapper.BeanMapper;
import com.kingnode.news.dto.KnNoticeDTO;
import com.kingnode.news.entity.KnNotice;
import com.kingnode.news.entity.KnNoticeReceipt;
import com.kingnode.news.service.NewsService;
import com.kingnode.xsimple.rest.DetailDTO;
import com.kingnode.xsimple.rest.ListDTO;
import com.kingnode.xsimple.util.Users;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * 公告管理rest接口
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
    @RestController @RequestMapping({"/api/v1/notice","/api/secure/v1/notice"})
public class NoticeRestController{
    @Autowired
    private NewsService ns;
    /**
     * 分页获取公告信息
     * 接口的请求方式: get
     * 示例：http://localhost:8080/api/v1/notice/list/1/10/admin
     * 参数：
     * @param p 第几页 从1开始
     * @param s 每页数量
     *
     * 返回：{
            "status" : true,
            "errorCode" : null,
            "errorMessage" : null,
            "list" : [ {
            "id" : 225,
            "title" : "34543543545435435",//标题
            "pubTime" : "2014-08-29 10:12:09",//发布时间
            "pubUserName" : "管理员",//发布者姓名）
            "content" : "53453453453453",//（内容）
            "orgName" : "盈诺德",//发布者组织名称
            "img" : "http://192.168.8.161:8080",//（如果有图片请提供）
            "isReceipt" : "assign",
            "hasHuizhi" : "false",//（是否回执）
            "alreadyReceipt" : 0,//已回执人数;
            "needReceipt" : 0,//需要回执人数
            "effectiveTime" : "",//有效时间
            "dept" : "移动事业部"//发布人部门
            }]
            }
     */
    @RequestMapping(value="/list", method={RequestMethod.GET})
    public ListDTO<KnNoticeDTO> List(@RequestParam(value="p") Integer p,@RequestParam(value="s") Integer s){
        String orgPath="";//要根据用户ID获取用户所在组织
        //Page<KnNotice> kns=ns.PageKnNotice(orgPath,pageNo,pageSize);
        Page<KnNotice> kns=ns.PageKnNotice(p,s);
        List<KnNoticeDTO> list=Lists.newArrayList();
        String[] orgs = ns.ReceipterMessage(Users.id());
        for(KnNotice kn : kns){
            KnNoticeDTO dto=BeanMapper.map(kn,KnNoticeDTO.class);
            dto.setPubTime(new DateTime(kn.getPubTime()).toString("yyyy-MM-dd HH:mm:ss"));
            dto.setEffectiveTime(kn.getEffectiveTime()!=null?new DateTime(kn.getEffectiveTime()).toString("yyyy-MM-dd HH:mm:ss"):"");
            dto.setContent(ns.SubString(kn.getContent(),200));
            dto.setDept(orgs[1]);
            dto.setImg(dto.getImg());
            dto.setHasHuizhi(String.valueOf(ns.HaveReceipt(dto.getId())));
            dto.setIsNeedReceipt(String.valueOf(ns.HasNeedRecipt(dto.getId())));
            list.add(dto);
        }
        ListDTO<KnNoticeDTO> dtoListDTO = new ListDTO<>(true,list);
        dtoListDTO.setErrorMessage(orgs[0]);
        return dtoListDTO;
    }
    /**
     * 获取公告详细信息
     * 接口的请求方式: get
     * 示例：http://localhost:8080/api/v1/notice/read/225
     * 参数：
     * @param id  公告id
     *
     * 返回： {
                "status" : true,
                "errorCode" : null,
                "errorMessage" : null,
                "detail" : {
                "id" : 225,
                "title" : "34543543545435435",//标题
                "pubTime" : "2014-08-29 10:12:09",//发布时间
                "pubUserName" : "管理员",//发布者姓名）
                "content" : "53453453453453",//（内容）
                "orgName" : "盈诺德",//发布者组织名称
                "img" : "http://192.168.8.161:8080",//（如果有图片请提供）
                "isReceipt" : "assign",
                "hasHuizhi" : "false",//（是否回执）
                "alreadyReceipt" : 0,//已回执人数;
                "needReceipt" : 0,//需要回执人数
                "effectiveTime" : "",//有效时间
                "dept" : "移动事业部"//发布人部门
                }
            }
     *
     */
    @RequestMapping(value="/read/{id}", method={RequestMethod.GET})
    public DetailDTO<KnNoticeDTO> Read(@PathVariable("id") Long id){
        KnNotice kn=ns.ReadKnNotice(id);
        KnNoticeDTO dto=BeanMapper.map(kn,KnNoticeDTO.class);
        dto.setPubTime(new DateTime(kn.getPubTime()).toString("yyyy-MM-dd HH:mm:ss"));
        dto.setEffectiveTime(kn.getEffectiveTime()!=null?new DateTime(kn.getEffectiveTime()).toString("yyyy-MM-dd HH:mm:ss"):"");
        dto.setImg(dto.getImg());
        dto.setHasHuizhi(String.valueOf(ns.HaveReceipt(dto.getId())));
        dto.setIsNeedReceipt(String.valueOf(ns.HasNeedRecipt(dto.getId())));
        DetailDTO dto1 = new DetailDTO<>(true,dto);
        return dto1;
    }

    /**
     * 回执
     * 接口的请求方式: POST
     * 示例：http://localhost:8080/api/v1/notice/receipt   post：id=225
     * 参数： id 公告id
     * @return 返回回执成功与否状态，true 成功，false 失败
     * 返回：{"status":true,"errorCode":null,"errorMessage":null,"detail":"true"}
     */
    @RequestMapping(value="/receipt", method={RequestMethod.POST})
    public DetailDTO<String> updateNoticeReceipt(HttpServletRequest request){
        String id =request.getParameter("id");
        KnNoticeReceipt knr = ns.SaveKnNoticeReceipt(Long.parseLong(id));
        String result = knr == null ? "false" : "true";
        return new DetailDTO<>(true,result);
    }

    /**
     * 获取某个人公告未回执的数量
     * 接口的请求方式: GET
     * 示例：http://localhost:8080/api/v1/notice/get-disabel-receipt-num/admin
     * 参数：
     * @param loginName 公告发布人账号
     *
     * 返回：{"status":true,"errorCode":null,"errorMessage":null,"detail":3}
     */
    @RequestMapping(value="/get-disabel-receipt-num/{loginName}", method={RequestMethod.GET})
    public DetailDTO<Integer> GetDisabelReceiptNum(@PathVariable("loginName") String loginName ){
        return  new DetailDTO<>(true,ns.QueryDisabelReceiptNum(loginName));
    }
}