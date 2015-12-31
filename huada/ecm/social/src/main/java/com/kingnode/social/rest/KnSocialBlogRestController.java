package com.kingnode.social.rest;
import java.util.Date;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kingnode.diva.mapper.BeanMapper;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.social.dto.KnSocialBlogCommentDTO;
import com.kingnode.social.dto.KnSocialBlogDTO;
import com.kingnode.social.dto.KnSocialBlogSettingsDTO;
import com.kingnode.social.dto.SocialBlogUserDTO;
import com.kingnode.social.entity.KnSocialBlog;
import com.kingnode.social.entity.KnSocialBlogAt;
import com.kingnode.social.entity.KnSocialBlogAttention;
import com.kingnode.social.entity.KnSocialBlogCollect;
import com.kingnode.social.entity.KnSocialBlogComment;
import com.kingnode.social.entity.KnSocialBlogFile;
import com.kingnode.social.entity.KnSocialBlogSettings;
import com.kingnode.social.service.KnSocialBlogService;
import com.kingnode.xsimple.entity.IdEntity;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnEmployeeOrganization;
import com.kingnode.xsimple.rest.DetailDTO;
import com.kingnode.xsimple.rest.ListDTO;
import com.kingnode.xsimple.rest.RestStatus;
import com.kingnode.xsimple.service.system.OrganizationService;
import com.kingnode.xsimple.util.Users;
import com.kingnode.xsimple.util.Utils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@RestController @RequestMapping({"/api/v1/social/blog","/api/secure/v1/social/blog"})
public class KnSocialBlogRestController{
    private static Logger log = LoggerFactory.getLogger(KnSocialBlogRestController.class);

    @Autowired
    private KnSocialBlogService socialBlogService;
    @Autowired
    private OrganizationService os;
    /**
     * 首页微博列表 包含没有过期的活动
     *
     * @param pageNo   页码 0表示第一页，默认值为0
     * @param pageSize 每页显示数，默认为10条数据
     *                 <pre>
     *        接口方法：/api/v1/social/blog/home/?p=0&s=10
     *        成功返回值：
     *        {
     *        "status" : true,
     *        "errorCode" : null,
     *        "errorMessage" : null,
     *        "list" : [{
     *        "id" : 2,          //微博id
     *             "messageType" : "FORWARD",   //枚举类型： FORWARD表示此微博为转发，GENERAL表示发布的微博
     *             "messageInfo" : "2",   //微博信息
     *             "publishDate" : 2,    //发布时间
     *             "messageCollectNum" : 2,  //收藏数
     *             "messageCommentNum" : 2,  //评论数
     *             "messageTranspondNum" : 2,  //转发数
     *             "messageAgreeNum" : 2,  //赞同次数
     *             "messageReadNum" : 2,  //阅读数
     *             "userId" : 2,  //用户id
     *             "userName" : "2",  //用户姓名
     *             "attach" : [ {    //附件
     *             "name" : null,  // 附件名称
     *             "sha" : null, // sha码
     *             "fileDate" : null,  //上传文件时间
     *             "fileSize" : null,  // 文件大小
     *             "fileType" : null  //文件类型
     *        } ],
     *        "imageAddress" : null,  //头像
     *        "messageSource" : "2",  //发布来源  ，比如 ：魅族手机4G
     *        "collectStatus" : null,  //收藏状态，0未收藏、1已收藏
     *        "parentNode" : {     //转发微博的内容
     *             "id" : 1,
     *             "messageType" : "GENERAL",
     *             "messageInfo" : "1",
     *             "publishDate" : 1,
     *             "messageCollectNum" : 1,
     *             "messageCommentNum" : 1,
     *             "messageTranspondNum" : 1,
     *             "messageAgreeNum" : 1,
     *             "messageReadNum" : 1,
     *             "userId" : 1,
     *             "userName" : "1",
     *             "attach" : [ ],
     *             "imageAddress" : null,
     *             "messageSource" : "1",
     *             "collectStatus" : null,
     *             }
     *           }]
     *        }
     *        失败返回
     *        {
     *        "status" : false,
     *        "errorCode" : null,
     *        "errorMessage" : null,
     *        "list" : []
     *        }
     *        </pre>
     */
    @RequestMapping(value="/home",method={RequestMethod.GET})
    public ListDTO<KnSocialBlogDTO> SocialBlogList(@RequestParam(value="p",defaultValue="0") Integer pageNo,@RequestParam(value="s",defaultValue="10") Integer pageSize){
        Long t1 = System.currentTimeMillis();
        List<KnSocialBlog> socialBlogs=socialBlogService.PageKnSocialBlog(pageNo,pageSize);
        Long t2 = System.currentTimeMillis();
        ListDTO<KnSocialBlogDTO> dtos = listKnSocialBlogDTO(socialBlogs);
        Long t3 = System.currentTimeMillis();
        log.info("数据库查询t2-t1:"+(t2-t1)/1000+"秒");
        log.info("数据组装t3-t2:"+(t3-t2)/1000+"秒");
        log.info("总共t3-t1:"+(t3-t1)/1000+"秒");
        return dtos;
    }
    /**
     * 查询所有活动
     *
     * @param pageNo   页码 0表示第一页，默认值为0
     * @param pageSize 每页显示数，默认为10条数据
     *                 <pre>
     *        接口方法：/api/v1/social/blog/home/?p=0&s=10
     *        成功返回值：
     *        {
     *        "status" : true,
     *        "errorCode" : null,
     *        "errorMessage" : null,
     *        "list" : [{
     *        "id" : 2,          //微博id
     *             "messageType" : "FORWARD",   //枚举类型： FORWARD表示此微博为转发，GENERAL表示发布的微博
     *             "messageInfo" : "2",   //微博信息
     *             "publishDate" : 2,    //发布时间
     *             "messageCollectNum" : 2,  //收藏数
     *             "messageCommentNum" : 2,  //评论数
     *             "messageTranspondNum" : 2,  //转发数
     *             "messageAgreeNum" : 2,  //赞同次数
     *             "messageReadNum" : 2,  //阅读数
     *             "userId" : 2,  //用户id
     *             "userName" : "2",  //用户姓名
     *             "attach" : [ {    //附件
     *             "name" : null,  // 附件名称
     *             "sha" : null, // sha码
     *             "fileDate" : null,  //上传文件时间
     *             "fileSize" : null,  // 文件大小
     *             "fileType" : null  //文件类型
     *        } ],
     *        "imageAddress" : null,  //头像
     *        "messageSource" : "2",  //发布来源  ，比如 ：魅族手机4G
     *        "collectStatus" : null,  //收藏状态，0未收藏、1已收藏
     *        "parentNode" : {     //转发微博的内容
     *             "id" : 1,
     *             "messageType" : "GENERAL",
     *             "messageInfo" : "1",
     *             "publishDate" : 1,
     *             "messageCollectNum" : 1,
     *             "messageCommentNum" : 1,
     *             "messageTranspondNum" : 1,
     *             "messageAgreeNum" : 1,
     *             "messageReadNum" : 1,
     *             "userId" : 1,
     *             "userName" : "1",
     *             "attach" : [ ],
     *             "imageAddress" : null,
     *             "messageSource" : "1",
     *             "collectStatus" : null,
     *             }
     *           }]
     *        }
     *        失败返回
     *        {
     *        "status" : false,
     *        "errorCode" : null,
     *        "errorMessage" : null,
     *        "list" : []
     *        }
     *        </pre>
     */
    @RequestMapping(value="/activity",method={RequestMethod.GET})
    public ListDTO<KnSocialBlogDTO> ActivityBlogList(@RequestParam(value="p",defaultValue="0") Integer pageNo,@RequestParam(value="s",defaultValue="10") Integer pageSize){
        List<KnSocialBlog> socialBlogs=socialBlogService.ActivityBlog(pageNo,pageSize);
        return listKnSocialBlogDTO(socialBlogs);
    }


    /**
     * 发布活动
     *
     * @param dto {
     *            "messageType" : null, //发微博传GENERAL,转发微博 传FORWARD
     *            "messageInfo" : null, //微博内容
     *            "atUsers " : null,  //@用户id集合 例如 1#张三,2#李四 中间用逗号隔开
     *            "attach" : [ {
     *            "name" : null,//附件名称
     *            "sha" : null, //sha码
     *            "fileSize" : null,//附件大小
     *            "fileType" : null //附件类型
     *            } ],
     *            "messageSource" : null,//发布信息来源 ，比如 魅族手机
     *            "parentId" : null    //转发微博的ID
     *            }
     *            <p/>
     *            <pre>
     *            接口方法：/api/v1/social/blog/publish/activity
     *            成功返回值：
     *            {
     *                  "status" : true,
     *                  "errorCode" : null,
     *                  "errorMessage" : null
     *            }
     *            失败返回值：
     *            {
     *                  "status" : false,
     *                  "errorCode" : “操作失败”,
     *                  "errorMessage" : null
     *            }
     *         </pre> 2010-08-02 10：50
     */
    @RequestMapping(value="/publish/activity",method={RequestMethod.POST})
    public RestStatus SaveActivityBlog(KnSocialBlogDTO dto){
        socialBlogService.SaveActivity(dto);
//        if(dto.getMessageType().equals(KnSocialBlog.MessageType.FORWARD)){
//            KnSocialBlog blog=socialBlogService.ReadKnSocialBlog(dto.getParentId());
//            blog.setActive(IdEntity.ActiveType.ENABLE);
//            blog.setMessageTranspondNum(blog.getMessageTranspondNum()==null?1:blog.getMessageTranspondNum()+1);
//            socialBlogService.SaveKnSocialBlog(blog);
//        }
//        if(!Strings.isNullOrEmpty(dto.getAtUsers())){
//            saveKnSocialBlogAt(ksb.getId(),dto.getAtUsers());
//        }
        return new RestStatus(true);
    }

    @RequestMapping(value="/delete/{id}",method={RequestMethod.GET})
    public RestStatus SocialBlogDelete(@PathVariable(value="id") Long id){
        RestStatus rs=new RestStatus();
        KnSocialBlog blog=socialBlogService.ReadKnSocialBlog(id);
        if(blog.getUserId().equals(Users.id())){
            socialBlogService.DeleteKnSocialBlogAt(id);
            socialBlogService.DeleteKnSocialBlogCollect(id);
            socialBlogService.DeleteCommentByBlogId(id);
            socialBlogService.DeleteKnSocialBlogFiles(id);
            socialBlogService.DeleteKnSocialBlog(id);
            rs.setStatus(true);
        }else{
            rs.setStatus(false);
        }
        return rs;
    }
    /**
     * 查看某用户的微博
     *
     * @param userId   用户Id
     * @param pageNo   页码 0表示第一页，默认值为0
     * @param pageSize 每页显示数，默认为10条数据
     *                 <pre>
     *        接口方法：/api/v1/social/blog/list/?userId=2&p=0&s=10
     *        成功返回值：
     *        {
     *        "status" : true,
     *        "errorCode" : null,
     *        "errorMessage" : null,
     *        "list" : [{
     *        "id" : 2,          //微博id
     *             "messageType" : "FORWARD",   // FORWARD表示此微博为转发
     *             "messageInfo" : "2",   //微博信息
     *             "publishDate" : 2,    //发布时间
     *             "messageCollectNum" : 2,  //收藏数
     *             "messageCommentNum" : 2,  //评论数
     *             "messageTranspondNum" : 2,  //转发数
     *             "messageAgreeNum" : 2,  //赞同次数
     *             "messageReadNum" : 2,  //阅读数
     *             "userId" : 2,  //用户id
     *             "userName" : "2",  //用户姓名
     *             "attach" : [ {    //附件
     *             "name" : null,  // 附件名称
     *             "sha" : null, // sha码
     *             "fileDate" : null,  //上传文件时间
     *             "fileSize" : null,  // 文件大小
     *             "fileType" : null  //文件类型
     *        } ],
     *        "imageAddress" : null,  //头像
     *        "messageSource" : "2",  //发布来源  ，比如 ：魅族手机4G
     *        "collectStatus" : null,  //收藏状态，0未收藏、1已收藏
     *        "parentNode" : {     //转发微博的内容
     *             "id" : 1,
     *             "messageType" : "GENERAL",   // GENERAL表示发布的微博
     *             "messageInfo" : "1",
     *             "publishDate" : 1,
     *             "messageCollectNum" : 1,
     *             "messageCommentNum" : 1,
     *             "messageTranspondNum" : 1,
     *             "messageAgreeNum" : 1,
     *             "messageReadNum" : 1,
     *             "userId" : 1,
     *             "userName" : "1",
     *             "attach" : [ ],
     *             "imageAddress" : null,
     *             "messageSource" : "1",
     *             "collectStatus" : null,
     *             }
     *           }]
     *        }
     *        失败返回
     *        {
     *        "status" : false,
     *        "errorCode" : null,
     *        "errorMessage" : null,
     *        "list" : []
     *        }
     *        </pre>
     */
    @RequestMapping(value="list",method={RequestMethod.GET})
    public ListDTO<KnSocialBlogDTO> userSocialBlog(@RequestParam(value="userId",defaultValue="0") Long userId,@RequestParam(value="p",defaultValue="0") Integer pageNo,@RequestParam(value="s",defaultValue="10") Integer pageSize){
        if(userId==0){
            userId=Users.id();
        }
        List<KnSocialBlog> socialBlogs=socialBlogService.PageKnSocialBlogByUserId(userId,pageNo,pageSize);
        return listKnSocialBlogDTO(socialBlogs);
    }
    /**
     * 查看转发微博列表
     *
     * @param pageNo   页码 0表示第一页，默认值为0
     * @param pageSize 每页显示数，默认为10条数据
     *                 <pre>
     *        接口方法：/api/v1/social/blog/forward/?p=0&s=10
     *        成功返回值：
     *        {
     *        "status" : true,
     *        "errorCode" : null,
     *        "errorMessage" : null,
     *        "list" : [{
     *        "id" : 2,          //微博id
     *             "messageType" : "FORWARD",   // FORWARD表示此微博为转发
     *             "messageInfo" : "2",   //微博信息
     *             "publishDate" : 2,    //发布时间
     *             "messageCollectNum" : 2,  //收藏数
     *             "messageCommentNum" : 2,  //评论数
     *             "messageTranspondNum" : 2,  //转发数
     *             "messageAgreeNum" : 2,  //赞同次数
     *             "messageReadNum" : 2,  //阅读数
     *             "userId" : 2,  //用户id
     *             "userName" : "2",  //用户姓名
     *             "attach" : [ {    //附件
     *             "name" : null,  // 附件名称
     *             "sha" : null, // sha码
     *             "fileDate" : null,  //上传文件时间
     *             "fileSize" : null,  // 文件大小
     *             "fileType" : null  //文件类型
     *        } ],
     *        "imageAddress" : null,  //头像
     *        "messageSource" : "2",  //发布来源  ，比如 ：魅族手机4G
     *        "collectStatus" : null,  //收藏状态，0未收藏、1已收藏
     *        "parentNode" : {     //转发微博的内容
     *             "id" : 1,
     *             "messageType" : "GENERAL",   // GENERAL表示发布的微博
     *             "messageInfo" : "1",
     *             "publishDate" : 1,
     *             "messageCollectNum" : 1,
     *             "messageCommentNum" : 1,
     *             "messageTranspondNum" : 1,
     *             "messageAgreeNum" : 1,
     *             "messageReadNum" : 1,
     *             "userId" : 1,
     *             "userName" : "1",
     *             "attach" : [ ],
     *             "imageAddress" : null,
     *             "messageSource" : "1",
     *             "collectStatus" : null,
     *             }
     *           }]
     *        }
     *        失败返回
     *        {
     *        "status" : false,
     *        "errorCode" : null,
     *        "errorMessage" : null,
     *        "list" : []
     *        }
     *        </pre>
     */
    @RequestMapping(value="/forward",method={RequestMethod.GET})
    public ListDTO<KnSocialBlogDTO> ForwardSocialBlog(@RequestParam(value="p",defaultValue="0") Integer pageNo,@RequestParam(value="s",defaultValue="10") Integer pageSize){
        List<Long> blogIds=socialBlogService.ListBlogIds(Users.id());
        List<KnSocialBlog> socialBlogs=socialBlogService.ReadKnSocialBlogByIds(blogIds,pageNo,pageSize);
        return listKnSocialBlogDTO(socialBlogs);
    }
    /**
     * 查看@我得微博
     *
     * @param pageNo   页码 0表示第一页，默认值为0
     * @param pageSize 每页显示数，默认为10条数据
     *                 <pre>
     *        接口方法：/api/v1/social/blog/at/micro-blog/?p=0&s=10
     *        成功返回值：
     *        {
     *        "status" : true,
     *        "errorCode" : null,
     *        "errorMessage" : null,
     *        "list" : [{
     *        "id" : 2,          //微博id
     *             "messageType" : "FORWARD",   // FORWARD表示此微博为转发
     *             "messageInfo" : "2",   //微博信息
     *             "publishDate" : 2,    //发布时间
     *             "messageCollectNum" : 2,  //收藏数
     *             "messageCommentNum" : 2,  //评论数
     *             "messageTranspondNum" : 2,  //转发数
     *             "messageAgreeNum" : 2,  //赞同次数
     *             "messageReadNum" : 2,  //阅读数
     *             "userId" : 2,  //用户id
     *             "userName" : "2",  //用户姓名
     *             "attach" : [ {    //附件
     *             "name" : null,  // 附件名称
     *             "sha" : null, // sha码
     *             "fileDate" : null,  //上传文件时间
     *             "fileSize" : null,  // 文件大小
     *             "fileType" : null  //文件类型
     *        } ],
     *        "imageAddress" : null,  //头像
     *        "messageSource" : "2",  //发布来源  ，比如 ：魅族手机4G
     *        "collectStatus" : null,  //收藏状态，0未收藏、1已收藏
     *        "parentNode" : {     //转发微博的内容
     *             "id" : 1,
     *             "messageType" : "GENERAL",   // GENERAL表示发布的微博
     *             "messageInfo" : "1",
     *             "publishDate" : 1,
     *             "messageCollectNum" : 1,
     *             "messageCommentNum" : 1,
     *             "messageTranspondNum" : 1,
     *             "messageAgreeNum" : 1,
     *             "messageReadNum" : 1,
     *             "userId" : 1,
     *             "userName" : "1",
     *             "attach" : [ ],
     *             "imageAddress" : null,
     *             "messageSource" : "1",
     *             "collectStatus" : null,
     *             }
     *           }]
     *        }
     *        失败返回
     *        {
     *        "status" : false,
     *        "errorCode" : null,
     *        "errorMessage" : null,
     *        "list" : []
     *        }
     *        </pre>
     */
    @RequestMapping(value="/at",method={RequestMethod.GET})
    public ListDTO<KnSocialBlogDTO> atUserSocialBlog(@RequestParam(value="p",defaultValue="0") Integer pageNo,@RequestParam(value="s",defaultValue="10") Integer pageSize){
        List<KnSocialBlog> socialBlogs=socialBlogService.PageAtUserBlog(Users.id(),pageNo,pageSize);
        return listKnSocialBlogDTO(socialBlogs);
    }
    /**
     * 发布、转发微博
     *
     * @param dto {
     *            "messageType" : null, //发微博传GENERAL,转发微博 传FORWARD
     *            "messageInfo" : null, //微博内容
     *            "atUsers " : null,  //@用户id集合 例如 1#张三,2#李四 中间用逗号隔开
     *            "attach" : [ {
     *            "name" : null,//附件名称
     *            "sha" : null, //sha码
     *            "fileSize" : null,//附件大小
     *            "fileType" : null //附件类型
     *            } ],
     *            "messageSource" : null,//发布信息来源 ，比如 魅族手机
     *            "parentId" : null    //转发微博的ID
     *            }
     *            <p/>
     *            <pre>
     *            接口方法：/api/v1/social/blog/publish
     *            成功返回值：
     *            {
     *                  "status" : true,
     *                  "errorCode" : null,
     *                  "errorMessage" : null
     *            }
     *            失败返回值：
     *            {
     *                  "status" : false,
     *                  "errorCode" : “操作失败”,
     *                  "errorMessage" : null
     *            }
     *         </pre>
     */
    @RequestMapping(value="/publish",method={RequestMethod.POST})
    public RestStatus SaveSocialBlog(KnSocialBlogDTO dto){
        KnSocialBlog ksb=BeanMapper.map(dto,KnSocialBlog.class);
        ksb.setUserId(Users.id());
        KnEmployee emp=os.ReadEmp(ksb.getUserId());
        if(emp!=null){
            ksb.setUserName(emp.getUserName()==null?"":emp.getUserName());
        }
        ksb.setCreateTime(new Date().getTime());
        ksb.setPublishTime(new Date().getTime());
        ksb.setActive(IdEntity.ActiveType.ENABLE);
        ksb.setType(1);
        ksb=socialBlogService.SaveKnSocialBlog(ksb);
        System.out.print(dto.getAttachTemp());
        if(dto.getAttachTemp()!=null&&!dto.getAttachTemp().equals("[]")){
            KnSocialBlogFile[] files=JsonMapper.nonDefaultMapper().fromJson(dto.getAttachTemp(),KnSocialBlogFile[].class);
            for(KnSocialBlogFile file : files){
                file.setKsb(ksb);
                file.setUpdateId(1l);
                file.setCreateId(1l);
                file.setUpdateTime(System.currentTimeMillis());
                file.setCreateTime(System.currentTimeMillis());
                socialBlogService.SaveKnSocialBlogFile(file);
            }
        }
        if(dto.getMessageType().equals(KnSocialBlog.MessageType.FORWARD)){
            KnSocialBlog blog=socialBlogService.ReadKnSocialBlog(dto.getParentId());
            blog.setActive(IdEntity.ActiveType.ENABLE);
            blog.setMessageTranspondNum(blog.getMessageTranspondNum()==null?1:blog.getMessageTranspondNum()+1);
            socialBlogService.SaveKnSocialBlog(blog);
        }
        if(!Strings.isNullOrEmpty(dto.getAtUsers())){
            saveKnSocialBlogAt(ksb.getId(),dto.getAtUsers());
        }
        return new RestStatus(true);
    }
    /**
     * 查看微博详细
     *
     * @param id 微博信息ID
     *           <pre>
     * 接口方法：/api/v1/social/blog/view/{id}
     * 成功返回值：
     * {
     * "status" : true,
     * "errorCode" : null,
     * "errorMessage" : null,
     * "detail" : {
     * "id" : 2,          //微博id
     *      "messageType" : "FORWARD",   // FORWARD表示此微博为转发
     *      "messageInfo" : "2",   //微博信息
     *      "publishDate" : 2,    //发布时间
     *      "messageCollectNum" : 2,  //收藏数
     *      "messageCommentNum" : 2,  //评论数
     *      "messageTranspondNum" : 2,  //转发数
     *      "messageAgreeNum" : 2,  //赞同次数
     *      "messageReadNum" : 2,  //阅读数
     *      "userId" : 2,  //用户id
     *      "userName" : "2",  //用户姓名
     *      "attach" : [ {    //附件
     *      "name" : null,  // 附件名称
     *      "sha" : null, // sha码
     *      "fileDate" : null,  //上传文件时间
     *      "fileSize" : null,  // 文件大小
     *      "fileType" : null  //文件类型
     * } ],
     * "imageAddress" : null,  //头像
     * "messageSource" : "2",  //发布来源  ，比如 ：魅族手机4G
     * "collectStatus" : null,  //收藏状态，0未收藏、1已收藏
     * "parentNode" : {     //转发微博的内容
     *      "id" : 1,
     *      "messageType" : "GENERAL",   // GENERAL表示发布的微博
     *      "messageInfo" : "1",
     *      "publishDate" : 1,
     *      "messageCollectNum" : 1,
     *      "messageCommentNum" : 1,
     *      "messageTranspondNum" : 1,
     *      "messageAgreeNum" : 1,
     *      "messageReadNum" : 1,
     *      "userId" : 1,
     *      "userName" : "1",
     *      "attach" : [ ],
     *      "imageAddress" : null,
     *      "messageSource" : "1",
     *      "collectStatus" : null,
     *      }
     *    }
     * }
     * 失败返回
     * {
     * "status" : false,
     * "errorCode" : null,
     * "errorMessage" : null,
     * "detail" : null
     * }
     * </pre>
     */
    @RequestMapping(value="/view/{id}",method={RequestMethod.GET})
    public DetailDTO<KnSocialBlogDTO> ReadKnSocialBlog(@PathVariable(value="id") Long id){
        DetailDTO<KnSocialBlogDTO> dto=new DetailDTO<>(true);
        KnSocialBlog socialBlog=socialBlogService.ReadKnSocialBlog(id);
        if(socialBlog!=null){
            KnSocialBlogDTO socialBlogDTO=BeanMapper.map(socialBlog,KnSocialBlogDTO.class);
            if(socialBlogDTO.getUserId().equals(Users.id())){
                socialBlogDTO.setDeleteStatus(true);
            }else {
                socialBlogDTO.setDeleteStatus(false);
            }
            KnEmployee emp=os.ReadEmp(socialBlog.getUserId());
            if(emp!=null){
                socialBlogDTO.setImageAddress(emp.getImageAddress());
            }
            KnSocialBlogCollect collect=socialBlogService.ReadByUserIdAndKsbId(Users.id(),id);
            if(collect!=null){
                socialBlogDTO.setCollectStatus(1);
            }
            if(socialBlog.getMessageType().equals(KnSocialBlog.MessageType.FORWARD)){
                KnSocialBlog parentSocialBlog=socialBlogService.ReadKnSocialBlog(socialBlog.getParentId());
                KnSocialBlogDTO parentDTO=BeanMapper.map(parentSocialBlog,KnSocialBlogDTO.class);
                socialBlogDTO.setParentNode(parentDTO);
            }
            dto.setDetail(socialBlogDTO);
            socialBlog.setMessageReadNum(socialBlog.getMessageReadNum()==null?1:socialBlog.getMessageReadNum()+1);
            socialBlogService.SaveKnSocialBlog(socialBlog);
        }else{
            dto.setErrorMessage("该微博已删除");
        }
        return dto;
    }
    /**
     * 收藏微博,取消微博
     *
     * @param id 微博id
     *           <p/>
     *           <pre>
     *           接口方法：/api/v1/social/blog/favorites/{id}
     *           成功返回值：
     *           {
     *               "status" : true,
     *               "errorCode" : null,
     *               "errorMessage" : null
     *           }
     *           失败返回值：
     *           {
     *               "status" : false,
     *               "errorCode" : “操作失败”,
     *               "errorMessage" : null
     *           }
     *           </pre>
     */
    @RequestMapping(value="/favorites/{id}",method={RequestMethod.GET})
    public RestStatus SaveCollect(@PathVariable(value="id") Long id){
        Long sysUserId=Users.id();
        KnSocialBlogCollect collect=socialBlogService.ReadKnSocialBlogCollectByUserIdAndId(sysUserId,id);
        if(collect!=null){
            socialBlogService.DeleteKnSocialBlogCollect(collect);
            KnSocialBlog blog=socialBlogService.ReadKnSocialBlog(id);
            blog.setMessageCollectNum(blog.getMessageCollectNum()==null?1:blog.getMessageCollectNum()-1);
            socialBlogService.SaveKnSocialBlog(blog);
        }else{
            collect=new KnSocialBlogCollect();
            collect.setUserId(sysUserId);
            KnEmployee emp=os.ReadEmp(sysUserId);
            if(emp!=null){
                collect.setUserName(emp.getUserName()==null?"":emp.getUserName());
            }
            collect.setCreateTime(new Date().getTime());
            collect.setCollectTime(new Date().getTime());
            KnSocialBlog blog=socialBlogService.ReadKnSocialBlog(id);
            blog.setMessageCollectNum(blog.getMessageCollectNum()==null?1:blog.getMessageCollectNum()+1);
            socialBlogService.SaveKnSocialBlog(blog);
            collect.setKsb(blog);
            socialBlogService.SaveKnSocialBlogCollect(collect);
        }
        return new RestStatus(true);
    }
    /**
     * 收藏微博列表
     *
     * @param pageNo   页码 0表示第一页，默认值为0
     * @param pageSize 每页显示数，默认为10条数据
     * @param userId   用户id，默认值为0
     *                 <p/>
     *                 <pre>
     *        接口方法：/api/v1/social/blog/favorites/list/?userId=1
     *        成功返回值：
     *        {
     *        "status" : true,
     *        "errorCode" : null,
     *        "errorMessage" : null,
     *        "list" : [{
     *        "id" : 2,          //微博id
     *             "messageType" : "FORWARD",   // FORWARD表示此微博为转发
     *             "messageInfo" : "2",   //微博信息
     *             "publishDate" : 2,    //发布时间
     *             "messageCollectNum" : 2,  //收藏数
     *             "messageCommentNum" : 2,  //评论数
     *             "messageTranspondNum" : 2,  //转发数
     *             "messageAgreeNum" : 2,  //赞同次数
     *             "messageReadNum" : 2,  //阅读数
     *             "userId" : 2,  //用户id
     *             "userName" : "2",  //用户姓名
     *             "attach" : [ {    //附件
     *             "name" : null,  // 附件名称
     *             "sha" : null, // sha码
     *             "fileDate" : null,  //上传文件时间
     *             "fileSize" : null,  // 文件大小
     *             "fileType" : null  //文件类型
     *        } ],
     *        "imageAddress" : null,  //头像
     *        "messageSource" : "2",  //发布来源  ，比如 ：魅族手机4G
     *        "collectStatus" : null,  //收藏状态，0未收藏、1已收藏
     *        "parentNode" : {     //转发微博的内容
     *             "id" : 1,
     *             "messageType" : "GENERAL",   // GENERAL表示发布的微博
     *             "messageInfo" : "1",
     *             "publishDate" : 1,
     *             "messageCollectNum" : 1,
     *             "messageCommentNum" : 1,
     *             "messageTranspondNum" : 1,
     *             "messageAgreeNum" : 1,
     *             "messageReadNum" : 1,
     *             "userId" : 1,
     *             "userName" : "1",
     *             "attach" : [ ],
     *             "imageAddress" : null,
     *             "messageSource" : "1",
     *             "collectStatus" : null,
     *             }
     *           }]
     *        }
     *        失败返回
     *        {
     *        "status" : false,
     *        "errorCode" : null,
     *        "errorMessage" : null,
     *        "list" : []
     *        }
     *        </pre>
     */
    @RequestMapping(value="/favorites/list",method={RequestMethod.GET})
    public ListDTO<KnSocialBlogDTO> ReadCollect(@RequestParam(value="userId",defaultValue="0") Long userId,@RequestParam(value="p",defaultValue="0") Integer pageNo,@RequestParam(value="s",defaultValue="10") Integer pageSize){
        if(userId==0){
            userId=Users.id();
        }
        List<KnSocialBlog> socialBlogs=socialBlogService.PageCollect(userId,pageNo,pageSize);
        return listKnSocialBlogDTO(socialBlogs);
    }
    /**
     * 发表评论
     *
     * @param dto {
     *            “mId” :null, //微博id
     *            "comment" : null,  //评论内容
     *            "status" : null,   //评论状态 暂时保留未使用
     *            "pId" : null,      //回复某条评论信息的ID
     *            "atUserIds " : null,   //@用户id集合 例如 1,2 中间用逗号隔开
     *            "atUserNames " : null  //@用户姓名集合  例如 张三,李四
     *            }
     *            <pre>
     *             接口方法：/api/v1/social/blog/comment
     *             成功返回值：
     *             {
     *                  "status" : true,
     *                  "errorCode" : null,
     *                  "errorMessage" : null
     *             }
     *             失败返回值：
     *             {
     *                  "status" : false,
     *                  "errorCode" : “操作失败”,
     *                  "errorMessage" : null
     *             }
     *             </pre>
     */
    @RequestMapping(value="/comment",method={RequestMethod.POST})
    public RestStatus SaveSocialBlogComment(KnSocialBlogCommentDTO dto){
        KnSocialBlogComment ksbc=BeanMapper.map(dto,KnSocialBlogComment.class);
        ksbc.setCreateTime(new Date().getTime());
        ksbc.setCommentTime(new Date().getTime());
        Long sysUserId=Users.id();
        KnEmployee emp=os.ReadEmp(sysUserId);
        if(emp!=null){
            ksbc.setUserName(emp.getUserName()==null?"":emp.getUserName());
        }
        ksbc.setUserId(sysUserId);
        ksbc.setActive(IdEntity.ActiveType.ENABLE);
        socialBlogService.SaveKnSocialBlogComment(ksbc);
        if(!Strings.isNullOrEmpty(dto.getAtUsers())){
            saveKnSocialBlogAt(dto.getmId(),dto.getAtUsers());
        }
        if(dto.getmId()!=null){
            KnSocialBlog blog=socialBlogService.ReadKnSocialBlog(dto.getmId());
            blog.setMessageCommentNum(blog.getMessageCommentNum()==null?1:blog.getMessageCommentNum()+1);
            socialBlogService.SaveKnSocialBlog(blog);
        }
        if(dto.getpId()!=null){
            KnSocialBlogComment comment=socialBlogService.ReadKnSocialBlogComment(dto.getpId());
            comment.setReplyNum(comment.getReplyNum()==null?1:comment.getReplyNum()+1);
            comment.setActive(IdEntity.ActiveType.ENABLE);
            socialBlogService.SaveKnSocialBlogComment(comment);
        }
        return new RestStatus(true);
    }
    /**
     * 查看微博评论、查看回复评论列表
     *
     * @param id       微博Id
     * @param pId      评论Id 备注：pId可以不传，不传直接看微博评论，否则查看回复评论列表
     * @param pageNo   页码 0表示第一页，默认值为0
     * @param pageSize 每页显示数，默认为10条数据
     *                 <p/>
     *                 <pre>
     * 接口方法：/api/v1/social/blog/comment/{id}/?pId=1&p=0&s=10
     * 成功返回值：
     * {
     *      "status" : true,
     *      "errorCode" : null,
     *      "errorMessage" : null,
     *      "list" : [ {
     *             "comment" : "fdsafa",   //评论内容
     *             "status" : null,  //评论状态  备注暂时为使用
     *             "mId" : 34,  //微博id
     *             "pId" : null,   //回复评论的id
     *             "userId" : 1,   //用户id
     *             "userName" : "管理员",  //用户姓名
     *             "commentDate" : "2014-09-18 17:42:20",  //评论时间
     *             "imageAddress" : null  //用户头像
     *             "replyNum " : null  //评论回复数
     *      } ]
     *  }
     * 失败返回值：
     * {
     *     "status" : false,
     *     "errorCode" : “操作失败”,
     *     "errorMessage" : null
     * }
     * </pre>
     */
    @RequestMapping(value="/comment/{id}",method={RequestMethod.GET})
    public ListDTO<KnSocialBlogCommentDTO> comment(@PathVariable(value="id") Long id,@RequestParam(value="pId",required=false) Long pId,@RequestParam(value="p",defaultValue="0") Integer pageNo,@RequestParam(value="s",defaultValue="10") Integer pageSize){
        List<KnSocialBlogComment> comments;
        if(pId!=null){
            comments=socialBlogService.ReadKnSocialBlogCommentByMIdAndPId(id,pId,pageNo,pageSize);
        }else{
            comments=socialBlogService.ReadKnSocialBlogCommentByMId(id,pageNo,pageSize);
        }
        return listKnSocialBlogCommentDTO(comments);
    }
    /**
     * 查看其他人对我所有微博的评论
     *
     * @param pageNo   页码 0表示第一页，默认值为0
     * @param pageSize 每页显示数，默认为10条数据
     *                 <p/>
     *                 <pre>
     * 接口方法：/api/v1/social/blog/comment/list/?p=0&s=10
     * 成功返回值：
     * {
     *      "status" : true,
     *      "errorCode" : null,
     *      "errorMessage" : null,
     *      "list" : [ {
     *             "comment" : "fdsafa",   //评论内容
     *             "status" : null,  //评论状态  备注暂时为使用
     *             "mId" : 34,  //微博id
     *             "pId" : null,   //回复评论的id
     *             "userId" : 1,   //用户id
     *             "userName" : "管理员",  //用户姓名
     *             "commentDate" : "2014-09-18 17:42:20",  //评论时间
     *             "imageAddress" : null  //用户头像
     *             "replyNum " : null  //评论回复数
     *      } ]
     *  }
     * 失败返回值：
     * {
     *     "status" : false,
     *     "errorCode" : “操作失败”,
     *     "errorMessage" : null
     * }
     * </pre>
     */
    @RequestMapping(value="/comment/list",method={RequestMethod.GET})
    public ListDTO<KnSocialBlogCommentDTO> commentList(@RequestParam(value="p",defaultValue="0") Integer pageNo,@RequestParam(value="s",defaultValue="10") Integer pageSize){
        List<KnSocialBlogComment> comments=socialBlogService.PageKnSocialBlogCommentByMId(Users.id(),pageNo,pageSize);
        return listKnSocialBlogCommentDTO(comments);
    }
    /**
     * 删除评论
     *
     * @param id 评论id
     * @param mId 微博id
     */
    @RequestMapping(value="/comment/delete/{id}/{mId}",method={RequestMethod.GET})
    public RestStatus commentDelete(@PathVariable(value="id") Long id,@PathVariable(value="mId") Long mId){
        RestStatus rs=new RestStatus();
        KnSocialBlogComment comment=socialBlogService.ReadKnSocialBlogComment(id);
        if(comment.getUserId().equals(Users.id())){
            socialBlogService.DeleteKnSocialBlogComment(id);
            KnSocialBlog socialBlog=socialBlogService.ReadKnSocialBlog(mId);
            socialBlog.setMessageCommentNum(socialBlog.getMessageCommentNum()==0?0:socialBlog.getMessageCommentNum()-1);
            socialBlogService.SaveKnSocialBlog(socialBlog);
            rs.setStatus(true);
        }else{
            rs.setStatus(false);
        }
        return rs;
    }
    /**
     * 查看用户资料
     *
     * @param userId 用户Id
     *               <p/>
     *               <pre>
     *                                 接口方法：/api/v1/social/blog/user/?userId=1
     *                                 成功返回值：
     *                                 {
     *                                      "status" : true,
     *                                      "errorCode" : null,
     *                                      "errorMessage" : null,
     *                                      "detail" : {
     *                                          "userId" : 2,   //用户 Id
     *                                          "userName" : "欧阳神光",  //用户姓名
     *                                          "sex" : null,   //性别
     *                                          "email" : "",  //邮箱
     *                                          "imageAddress" : null,  //头像
     *                                          "address" : null,  //地址
     *                                          "socialBlogNum" : 1,  //微博数
     *                                          "fansNum" : 1,  //粉丝数
     *                                          "attentionNum" : 1,  //关注数
     *                                          "collectNum" : 1,  //收藏数
     *                                          "isAttention" : 1    //是否关注 备注：0表示未关注 、大于0表示已关注，大于0的值为关注信息id
     *                                      }
     *                                 }
     *                                 失败返回值：
     *                                 {
     *                                     "status" : false,
     *                                     "errorCode" : “操作失败”,
     *                                     "errorMessage" : null,
     *                                     "detail" :null
     *                                 }
     *                                 </pre>
     */
    @RequestMapping(value="/user",method={RequestMethod.GET})
    public DetailDTO<SocialBlogUserDTO> SocialBlogUserView(@RequestParam(value="userId") Long userId){
        DetailDTO<SocialBlogUserDTO> dto=new DetailDTO<>(true);
        if(userId==0){
            userId=Users.id();
        }
        KnEmployee emp=os.ReadEmp(userId);
        if(emp!=null){
            dto.setDetail(readSocialBlogUserDTO(userId,emp));
        }else{
            dto.setErrorMessage("用户不存在");
        }
        return dto;
    }
    /**
     * 查看粉丝列表
     *
     * @param userId   用户Id
     * @param pageNo   页码 0表示第一页，默认值为0
     * @param pageSize 每页显示数，默认为10条数据
     *                 <p/>
     *                 <pre>
     * 接口方法：/api/v1/social/blog/fans/{userId}/?p=0&s=10
     * 成功返回值：
     * {
     *      "status" : true,
     *      "errorCode" : null,
     *      "errorMessage" : null,
     *      "list" : [{
     *          "userId" : 2,   //用户 Id
     *          "userName" : "欧阳神光",  //用户姓名
     *          "sex" : null,   //性别
     *          "email" : "",  //邮箱
     *          "imageAddress" : null,  //头像
     *          "address" : null,  //地址
     *          "socialBlogNum" : 1,  //微博数
     *          "fansNum" : 1,  //粉丝数
     *          "attentionNum" : 1,  //关注数
     *          "collectNum" : 1,  //收藏数
     *          "isAttention" : 1    //是否关注 备注：0表示未关注 、大于0表示已关注，大于0的值为关注信息id
     *      }]
     * }
     * 失败返回值：
     * {
     *     "status" : false,
     *     "errorCode" : “操作失败”,
     *     "errorMessage" : null,
     *     "list" :[]
     * }
     * </pre>
     */
    @RequestMapping(value="/fans/{userId}",method={RequestMethod.GET})
    public ListDTO<SocialBlogUserDTO> ReadUserFans(@PathVariable(value="userId") Long userId,@RequestParam(value="p",defaultValue="0") Integer pageNo,@RequestParam(value="s",defaultValue="10") Integer pageSize){
        List<KnEmployee> emps=socialBlogService.PageUserForFans(userId,pageNo,pageSize);
        return listSocialBlogUserDTO(userId,emps);
    }
    /**
     * 查看我所关注用户得微博
     *
     * @param pageNo   页码 0表示第一页，默认值为0
     * @param pageSize 每页显示数，默认为10条数据
     *                 <pre>
     *        接口方法：/api/v1/social/blog/attention/micro-blog/?p=0&s=10
     *        成功返回值：
     *        {
     *        "status" : true,
     *        "errorCode" : null,
     *        "errorMessage" : null,
     *        "list" : [{
     *        "id" : 2,          //微博id
     *             "messageType" : "FORWARD",   // FORWARD表示此微博为转发
     *             "messageInfo" : "2",   //微博信息
     *             "publishDate" : 2,    //发布时间
     *             "messageCollectNum" : 2,  //收藏数
     *             "messageCommentNum" : 2,  //评论数
     *             "messageTranspondNum" : 2,  //转发数
     *             "messageAgreeNum" : 2,  //赞同次数
     *             "messageReadNum" : 2,  //阅读数
     *             "userId" : 2,  //用户id
     *             "userName" : "2",  //用户姓名
     *             "attach" : [ {    //附件
     *             "name" : null,  // 附件名称
     *             "sha" : null, // sha码
     *             "fileDate" : null,  //上传文件时间
     *             "fileSize" : null,  // 文件大小
     *             "fileType" : null  //文件类型
     *        } ],
     *        "imageAddress" : null,  //头像
     *        "messageSource" : "2",  //发布来源  ，比如 ：魅族手机4G
     *        "collectStatus" : null,  //收藏状态，0未收藏、1已收藏
     *        "parentNode" : {     //转发微博的内容
     *             "id" : 1,
     *             "messageType" : "GENERAL",   // GENERAL表示发布的微博
     *             "messageInfo" : "1",
     *             "publishDate" : 1,
     *             "messageCollectNum" : 1,
     *             "messageCommentNum" : 1,
     *             "messageTranspondNum" : 1,
     *             "messageAgreeNum" : 1,
     *             "messageReadNum" : 1,
     *             "userId" : 1,
     *             "userName" : "1",
     *             "attach" : [ ],
     *             "imageAddress" : null,
     *             "messageSource" : "1",
     *             "collectStatus" : null,
     *             }
     *           }]
     *        }
     *        失败返回
     *        {
     *        "status" : false,
     *        "errorCode" : null,
     *        "errorMessage" : null,
     *        "list" : []
     *        }
     *        </pre>
     */
    @RequestMapping(value="/attention",method={RequestMethod.GET})
    public ListDTO<KnSocialBlogDTO> focusUserSocialBlog(@RequestParam(value="p",defaultValue="0") Integer pageNo,@RequestParam(value="s",defaultValue="10") Integer pageSize){
        List<KnSocialBlog> socialBlogs=socialBlogService.PageFocusUserBlog(Users.id(),pageNo,pageSize);
        return listKnSocialBlogDTO(socialBlogs);
    }
    /**
     * 关注好友，取消好友关注
     *
     * @param userId 被关注好友得ID
     *               <p/>
     *               <pre>
     *                                 接口方法：/api/v1/social/blog/attention/{userId}
     *                                 成功返回值：
     *                                 {
     *                                     "status" : true,
     *                                     "errorCode" : null,
     *                                     "errorMessage" : null
     *                                 }
     *                                 失败返回值：
     *                                 {
     *                                     "status" : false,
     *                                     "errorCode" : “操作失败”,
     *                                     "errorMessage" : null
     *                                 }
     *                                 </pre>
     */
    @RequestMapping(value="/attention/{userId}",method={RequestMethod.GET})
    public RestStatus SaveAttention(@PathVariable(value="userId") Long userId){
        RestStatus rs=new RestStatus(true);
        Long sysUserId=Users.id();
        KnSocialBlogAttention attention=socialBlogService.ReadKnSocialBlogAttentionByUserIdAndFocusUserId(sysUserId,userId);
        if(attention==null){
            attention=new KnSocialBlogAttention();
            attention.setCreateTime(new Date().getTime());
            attention.setUserId(sysUserId);
            attention.setUserName(Users.name());
            attention.setAttentionTime(new Date().getTime());
            attention.setFocusUserId(userId);
            socialBlogService.SaveKnSocialBlogAttention(attention);
        }else{
            socialBlogService.DeleteKnSocialBlogAttention(attention);
        }
        return rs;
    }
    /**
     * 关注用户列表
     *
     * @param userId   用户Id
     * @param pageNo   页码 0表示第一页，默认值为0
     * @param pageSize 每页显示数，默认为10条数据
     *                 <p/>
     *                 <pre>
     * 接口方法：/api/v1/social/blog/attention/list/{userId}/?p=0&s=10
     * 成功返回值：
     * {
     *      "status" : true,
     *      "errorCode" : null,
     *      "errorMessage" : null,
     *      "list" : [{
     *          "userId" : 2,   //用户 Id
     *          "userName" : "欧阳神光",  //用户姓名
     *          "sex" : null,   //性别
     *          "email" : "",  //邮箱
     *          "imageAddress" : null,  //头像
     *          "address" : null,  //地址
     *          "socialBlogNum" : 1,  //微博数
     *          "fansNum" : 1,  //粉丝数
     *          "attentionNum" : 1,  //关注数
     *          "collectNum" : 1,  //收藏数
     *          "isAttention" : 1    //是否关注 备注：0表示未关注 、大于0表示已关注，大于0的值为关注信息id
     *      }]
     * }
     * 失败返回值：
     * {
     *     "status" : false,
     *     "errorCode" : “操作失败”,
     *     "errorMessage" : null,
     *     "list" :[]
     * }
     * </pre>
     */
    @RequestMapping(value="/attention/list/{userId}",method={RequestMethod.GET})
    public ListDTO<SocialBlogUserDTO> ReadUserAttention(@PathVariable(value="userId") Long userId,@RequestParam(value="p",defaultValue="0") Integer pageNo,@RequestParam(value="s",defaultValue="10") Integer pageSize){
        List<KnEmployee> emps=socialBlogService.PageAttentionUser(userId,pageNo,pageSize);
        return listSocialBlogUserDTO(userId,emps);
    }
    /**
     * 关注用户搜索
     *
     * @param userName 用户姓名
     * @param pageNo   页码 0表示第一页，默认值为0
     * @param pageSize 每页显示数，默认为10条数据
     *                 <p/>
     *                 <pre>
     * 接口方法：/api/v1/social/blog/attention/search/{userId}/?userName=张三&p=0&s=10
     * 成功返回值：
     * {
     *      "status" : true,
     *      "errorCode" : null,
     *      "errorMessage" : null,
     *      "list" : [{
     *          "userId" : 2,   //用户 Id
     *          "userName" : "欧阳神光",  //用户姓名
     *          "sex" : null,   //性别
     *          "email" : "",  //邮箱
     *          "imageAddress" : null,  //头像
     *          "address" : null,  //地址
     *          "socialBlogNum" : 1,  //微博数
     *          "fansNum" : 1,  //粉丝数
     *          "attentionNum" : 1,  //关注数
     *          "collectNum" : 1,  //收藏数
     *          "isAttention" : 1    //是否关注 备注：0表示未关注 、大于0表示已关注，大于0的值为关注信息id
     *      }]
     * }
     * 失败返回值：
     * {
     *     "status" : false,
     *     "errorCode" : “操作失败”,
     *     "errorMessage" : null,
     *     "list" :[]
     * }
     * </pre>
     */
    @RequestMapping(value="/attention/search",method={RequestMethod.POST})
    public ListDTO<SocialBlogUserDTO> ReadSearchUserAttention(@RequestParam(value="userName") String userName,@RequestParam(value="p",defaultValue="0") Integer pageNo,@RequestParam(value="s",defaultValue="10") Integer pageSize){
        Long sysUserId=Users.id();
        List<KnEmployee> emps=socialBlogService.PageAttentionUserSearch(sysUserId,userName,pageNo,pageSize);
        return listSocialBlogUserDTO(sysUserId,emps);
    }
    /**
     * 获取论坛邮箱配置
     *
     * @param type 枚举类型 MAILBOX,ADVICE,CONTRIBUTE
     *             <pre>
     *                  接口方法：/api/v1/social/blog/email/MAILBOX
     *                  成功返回值：
     *                  {
     *                       "status" : true,
     *                       "errorCode" : null,
     *                       "errorMessage" : null,
     *                       "detail" : {
     *                           "userName" : "tom1",
     *                           "email" : "tom1@f.cj",
     *                       }
     *                  }
     *                  失败返回
     *                  {
     *                  "status" : false,
     *                  "errorCode" : null,
     *                  "errorMessage" : null,
     *                  "detail" : null
     *                  }
     *                  </pre>
     */
    @RequestMapping(value="/email/{type}",method={RequestMethod.GET})
    public DetailDTO<KnSocialBlogSettingsDTO> ReadSettings(@PathVariable(value="type") String type){
        KnSocialBlogSettings.EmailType emailType=null;
        switch(type){
        case "MAILBOX":
            emailType=KnSocialBlogSettings.EmailType.MAILBOX;
            break;
        case "ADVICE":
            emailType=KnSocialBlogSettings.EmailType.ADVICE;
            break;
        case "CONTRIBUTE":
            emailType=KnSocialBlogSettings.EmailType.CONTRIBUTE;
            break;
        }
        KnSocialBlogSettings settings=socialBlogService.ReadKnSocialBlogSettingsByType(emailType);
        KnSocialBlogSettingsDTO dto=null;
        DetailDTO<KnSocialBlogSettingsDTO> detailDTO=new DetailDTO<>();
        if(settings!=null){
            dto=BeanMapper.map(settings,KnSocialBlogSettingsDTO.class);
            detailDTO.setDetail(dto);
            detailDTO.setStatus(true);
        }else{
            detailDTO.setDetail(null);
            detailDTO.setStatus(false);
        }
        return detailDTO;
    }
    /**
     * 获取页面显示微博DTO列表
     *
     * @param socialBlogs 微博信息列表
     */
    private ListDTO<KnSocialBlogDTO> listKnSocialBlogDTO(List<KnSocialBlog> socialBlogs){
        List<KnSocialBlogDTO> list=Lists.newArrayList();
        for(KnSocialBlog ksb : socialBlogs){
            KnEmployee emp=os.ReadEmp(ksb.getUserId());
            KnSocialBlogDTO dto=BeanMapper.map(ksb,KnSocialBlogDTO.class);
            if(dto.getUserId().equals(Users.id())){
                dto.setDeleteStatus(true);
            }else{
                dto.setDeleteStatus(false);
            }
            if(emp!=null){
                dto.setImageAddress(emp.getImageAddress());
            }
            dto.setEndTime(ksb.getEndTime()!=null?new DateTime(ksb.getEndTime()).toString("yyyy-MM-dd HH:mm"):"");
            if(ksb.getMessageType().equals(KnSocialBlog.MessageType.FORWARD)){
                KnSocialBlog socialBlog=socialBlogService.ReadKnSocialBlog(ksb.getParentId());
                KnSocialBlogDTO parentDTO=BeanMapper.map(socialBlog,KnSocialBlogDTO.class);
                dto.setParentNode(parentDTO);
            }
            KnSocialBlogCollect collect=socialBlogService.ReadByUserIdAndKsbId(Users.id(),ksb.getId());
            if(collect!=null){
                dto.setCollectStatus(1);
            }
            list.add(dto);
        }
        ListDTO<KnSocialBlogDTO> listDTO=new ListDTO<>(true,list);
        if(socialBlogs.size()==0){
            listDTO.setErrorMessage("无数据");
            listDTO.setErrorCode("001");
        }
        return listDTO;
    }
    /**
     * 获取微博用户显示DTO
     *
     * @param userId 用户Id
     * @param emp    员工实体类
     */
    private SocialBlogUserDTO readSocialBlogUserDTO(Long userId,KnEmployee emp){
        SocialBlogUserDTO user=BeanMapper.map(emp,SocialBlogUserDTO.class);
        KnEmployeeOrganization organization=os.ReadOrganization(emp.getId());
        if(organization!=null&&organization.getId().getOrg()!=null){
            user.setDepartment(organization.getId().getOrg().getName());
            user.setDepartId(organization.getId().getOrg().getId());
        }
        user.setUserId(String.valueOf(emp.getId()));
        user.setPhone(emp.getPhone());
        user.setTelephone(emp.getTelephone());
        Integer blogNum=socialBlogService.ReadKnSocialBlogCountByUserId(userId);
        if(blogNum!=null&&blogNum>0){//微博数
            user.setSocialBlogNum(blogNum);
        }
        Integer fanNum=socialBlogService.ReadKnSocialBlogAttentionCountByFocusUserId(userId);
        if(fanNum!=null&&fanNum>0){//粉丝数
            user.setFansNum(fanNum);
        }
        Integer attentionNum=socialBlogService.ReadKnSocialBlogAttentionCountByUserId(userId);
        if(attentionNum!=null&&attentionNum>0){//关注数
            user.setAttentionNum(attentionNum);
        }
        Integer collectNum=socialBlogService.ReadKnSocialBlogCollectCountByUserId(userId);
        if(collectNum!=null&&collectNum>0){//收藏数
            user.setCollectNum(collectNum);
        }
        KnSocialBlogAttention attention=socialBlogService.ReadKnSocialBlogAttentionByUserIdAndFocusUserId(Users.id(),emp.getId());
        if(attention!=null){//是否被当前用户关注过
            user.setIsAttention(1L);
        }
        return user;
    }
    /**
     * 获取微博用户显示DTO列表
     *
     * @param userId 用户Id
     * @param emps   员工实体类集合
     */
    private ListDTO<SocialBlogUserDTO> listSocialBlogUserDTO(Long userId,List<KnEmployee> emps){
        List<SocialBlogUserDTO> list=Lists.newArrayList();
        for(KnEmployee emp : emps){
            list.add(readSocialBlogUserDTO(emp.getId(),emp));
        }
        ListDTO<SocialBlogUserDTO> listDTO=new ListDTO<>(true,list);
        if(list.size()==0){
            listDTO.setErrorCode("001");
            listDTO.setErrorMessage("无数据");
        }
        return listDTO;
    }
    /**
     * 微博@功能
     *
     * @param id      微博id
     * @param atUsers 用户集合  例如： 1#张三,2#李四
     */
    private void saveKnSocialBlogAt(Long id,String atUsers){
        List<KnSocialBlogAt> list=Lists.newArrayList();
        String[] users=atUsers.split(",");
        for(int i=0;i<users.length;i++){
            String[] user=users[i].split("#");
            KnSocialBlogAt at=new KnSocialBlogAt();
            at.setCreateTime(System.currentTimeMillis());
            at.setAtTime(System.currentTimeMillis());
            at.setAtUserId(Long.parseLong(user[0]));
            at.setAtUserName(user[1]);
            at.setmId(id);
            list.add(at);
        }
        socialBlogService.SaveKnSocialBlogAt(list);
    }
    /**
     * 获取显示评论DTO列表
     */
    private ListDTO<KnSocialBlogCommentDTO> listKnSocialBlogCommentDTO(List<KnSocialBlogComment> comments){
        List<KnSocialBlogCommentDTO> list=Lists.newArrayList();
        String count = "0";
        if(!Utils.isEmpityCollection(comments)){
            count = socialBlogService.queryCount(comments.get(0).getmId())+"";
        }
        for(KnSocialBlogComment c : comments){
            KnSocialBlogCommentDTO dto=BeanMapper.map(c,KnSocialBlogCommentDTO.class);
            if(dto.getUserId().equals(Users.id())){
                dto.setDeleteStatus(true);
            }else{
                dto.setDeleteStatus(false);
            }
            KnEmployee emp=os.ReadEmp(c.getUserId());
            if(emp!=null){
                dto.setImageAddress(emp.getImageAddress());
            }
            String[] orgs = socialBlogService.ReceipterMessage(c.getUserId());
            dto.setDepart(orgs[1]);
            dto.setCommentCount(count);
            list.add(dto);
        }
        ListDTO<KnSocialBlogCommentDTO> listDTO=new ListDTO<>(true,list);
        if(list.size()==0){
            listDTO.setErrorCode("001");
            listDTO.setErrorMessage("无数据");
        }
        return listDTO;
    }
}
