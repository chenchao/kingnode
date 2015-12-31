package com.kingnode.news.rest;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Lists;
import com.kingnode.diva.mapper.BeanMapper;
import com.kingnode.news.dto.KnArticleCommentDTO;
import com.kingnode.news.dto.KnArticleDTO;
import com.kingnode.news.entity.KnArticle;
import com.kingnode.news.entity.KnArticleComment;
import com.kingnode.news.service.NewsService;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.rest.DetailDTO;
import com.kingnode.xsimple.rest.ListDTO;
import com.kingnode.xsimple.service.system.OrganizationService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequestMapping({"/api/v1/news","/api/secure/v1/news"})
public class NewsRestController{
    @Autowired
    private NewsService newsService;
    @Autowired
    private OrganizationService os;
    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public List<String> getArticleCategory(){
        return Collections.singletonList("<p>你好，世界！</p>");
    }
    /**
     * 新闻列表
     * 接口的请求方式: get
     * <p/>
     * 接口方法： http://localhost:8080/api/v1/news/list?p=0&s=2
     * <p/>
     * 参数：
         p页码0表示第一页
         s每页显示数，默认为10条数据
     * <pre>
     * 返回：{
     *      "status" : true,
     *      "errorCode" : null,
     *      "errorMessage" : null,
     * "list" : [ {
     * "id" : 33,  //新闻id
     * "title" : "423434",//标题
     * "cover" : "/uf/b0ae3829858742db946c28fd4277c938.png",//封面图片
     * "summary" : null,//摘要
     * "content" : "<p>432432</p>",//正文
     * "isTop" : true,//是否置顶 是否为头条
     * "hits" : 0,//浏览记录数
     * "comments" : 0,//评论数
     * "pubDate" : "2014-10-08",//发布日期
     * "effectiveTime" : "",//发布时间
     * "top" :  //头条对象
         * {
         * "id" : 33,
         * "title" : "423434",
         * "cover" : "/uf/b0ae3829858742db946c28fd4277c938.png",
         * "summary" : null,
         * "content" : "<p>432432</p>",
         * "isTop" : true,
         * "hits" : 0,
         * "comments" : 0,
         * "pubDate" : "1412697600000",
         * "effectiveTime" : null,
         * "top" : null
         * }
     * }, {
     * "id" : 1,
     * "title" : "发生地方时代",
     * "cover" : "/uf/a60fa4c91726435f818c3d886051487d.png",
     * "summary" : null,
     * "content" : "<p>发生地方的</p>",
     * "isTop" : false,
     * "hits" : 1,
     * "comments" : 0,
     * "pubDate" : "2014-10-08",
     * "effectiveTime" : "",
     * "top" : null
     * }]
     * }
     * <pre>
     */
    @RequestMapping(value="/list",method={RequestMethod.GET})
    public ListDTO<KnArticleDTO> queryKnArticleDTOs(@RequestParam(value="p") Integer pageNo,@RequestParam(value="s") Integer pageSize){
        KnArticle top=newsService.QueryNewestTopArticle();
        top.setContent("");
        Page<KnArticle> page=newsService.PageKnArticle(pageNo,pageSize,top.getId());
        List<KnArticleDTO> dtos=Lists.newArrayList();
        List<KnArticle> knArticles=page.getContent();
        KnArticleDTO topDto=BeanMapper.map(top,KnArticleDTO.class);
        topDto.setCover(topDto.getCover());
        if(knArticles!=null&&knArticles.size()>0){
            int count=0;
            for(KnArticle cle : knArticles){
                cle.setContent("");
                KnArticleDTO dto=BeanMapper.map(cle,KnArticleDTO.class);
                dto.setEffectiveTime(cle.getEffectiveTime()!=null?new DateTime(cle.getEffectiveTime()).toString("yyyy-MM-dd"):"");
                dto.setPubDate(cle.getPubDate()!=null?new DateTime(cle.getPubDate()).toString("yyyy-MM-dd"):"");
                dto.setCover(dto.getCover());
                if(count==0&&pageNo.intValue()==0){
                    dto.setTop(topDto);
                }
                KnEmployee emp = os.ReadEmp(cle.getCreateId());
                if(emp!=null){
                    dto.setPubUserName(emp.getUserName());
                }
                dtos.add(dto);
                count++;
            }
        }
        return new ListDTO<>(true,dtos);
    }
    /**
     * 获取新闻详情
     * 接口的请求方式: post方式
     * 示例： http://localhost:8080/api/v1/news/detail
     * 参数： id 新闻ID
     * 返回：{
     * "status" : true,
     * "errorCode" : null,
     * "errorMessage" : null,
     * "list" : [ {
     * "id" : 33,  //新闻id
     * "title" : "423434",//标题
     * "cover" : "/uf/b0ae3829858742db946c28fd4277c938.png",//封面图片
     * "summary" : null,//摘要
     * "content" : "<p>432432</p>",//正文
     * "isTop" : true,//是否置顶 是否为头条
     * "hits" : 0,//浏览记录数
     * "comments" : 0,//评论数
     * "pubDate" : "2014-10-08",//发布日期
     * "effectiveTime" : "",//发布时间
     * "top" : null
     * } ]
     * }
     */
    @RequestMapping(value="/detail",method={RequestMethod.POST})
    public ListDTO<KnArticleDTO> queryKnArticleDTO(@RequestParam(value="id") Long id){
        KnArticle cle=newsService.ReadKnArticle(id);
        newsService.UpdateKnArticle(cle);
        KnArticleDTO dto=BeanMapper.map(cle,KnArticleDTO.class);
        dto.setEffectiveTime(cle.getEffectiveTime()!=null?new DateTime(cle.getEffectiveTime()).toString("yyyy-MM-dd HH:mm:ss"):"");
        dto.setPubDate(cle.getCreateTime()!=null?new DateTime(cle.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"):"");
        dto.setCover(dto.getCover());
        KnEmployee emp = os.ReadEmp(cle.getCreateId());
        if(emp!=null){
            dto.setPubUserName(emp.getUserName());
        }
        List<KnArticleDTO> list=Lists.newArrayList();
        list.add(dto);
        return new ListDTO<>(true,list);
    }
    /**
     * 发表评论
     * 接口的请求方式: post方式
     * 示例：http://localhost:8080/api/v1/news/comment
     * 参数：
     * "articleId":"1", --新闻id
     * "content":"我是评论" --评论内容
     * 返回：{"status":true,"errorCode":null,"errorMessage":null,"detail":"true"}
     *
     */
    @RequestMapping(value="/comment",method={RequestMethod.POST})
    public DetailDTO<String> saveKnArticleComment(HttpServletRequest request){
        String id=request.getParameter("articleId");
        String content=request.getParameter("content");
        String pid=request.getParameter("pid");
        boolean flag=newsService.SaveKnArticleComment(id,content,pid);
        return new DetailDTO<String>(true,flag+"");
    }
    /**
     * 删除评论
     * 接口的请求方式: GET方式
     * 示例：http://localhost:8080/api/v1/news/comment/delete/1
     * 参数：
     * "id":"1", --新闻id
     * 返回：{"status":true,"errorCode":null,"errorMessage":null,"detail":"true"}
     */
    @RequestMapping(value="/comment/delete/{id}",method={RequestMethod.GET})
    public DetailDTO<String> deleteKnArticleComment(@PathVariable(value="id") String id){
        boolean flag=newsService.DeleteKnArticleComment(Long.valueOf(id));
        return new DetailDTO<String>(true,flag+"");
    }
    /**
     * 评论列表
     * 接口的请求方式: GET方式
     * 示例：http://localhost:8080/api/v1/news/comment
     * 参数： artidleId 新闻id
     * p页码 s每页记录数
     * 返回：{
     * "status" : true,
     * "errorCode" : null,
     * "errorMessage" : null,
     * "list" : [
     *  {
     *      articleId://新闻id
     *      content://评论内容
     *      name://评论人姓名
     *      title://新闻标题
     *      cTime://评论时间
     *      depart://部门
     *      img://人物头像
     *      pid://评论父id
     *      createId://评论人id
     *      pDTO:评论父元素
     *      {
     *
     *      }
     *  }
     * ]
     * }
     */
    @RequestMapping(value="/comment",method={RequestMethod.GET})
    public ListDTO<KnArticleCommentDTO> queryKnArticleCommentDTO(@RequestParam(value="articleId") Long artidleId,@RequestParam(value="p") Integer pageNo,@RequestParam(value="s") Integer pageSize){
        Page<KnArticleComment> page=newsService.PageKnArticleComment(artidleId,pageNo,pageSize);
        List<KnArticleComment> comments=page.getContent();
        List<KnArticleCommentDTO> dtos=Lists.newArrayList();
        if(comments!=null&&comments.size()>0){
            for(KnArticleComment comment : comments){
                KnArticleCommentDTO dto=BeanMapper.map(comment,KnArticleCommentDTO.class);
                dto.setcTime(comment.getCreateTime()!=null?new DateTime(comment.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"):"");
                String[] orgs=newsService.ReceipterMessage(comment.getUserId());
                dto.setDepart(orgs[1]);
                dto.setImg(orgs[2]);
                //组装父评论内容
                if(dto.getPid()!=null&&dto.getPid()>0){
                    KnArticleComment pcomment=newsService.FindKnArticleComment(dto.getPid());
                    KnArticleCommentDTO pdto=BeanMapper.map(pcomment,KnArticleCommentDTO.class);
                    pdto.setcTime(pcomment.getCreateTime()!=null?new DateTime(pcomment.getCreateTime()).toString("yyyy-MM-dd"):"");
                    dto.setpDTO(pdto);
                }
                dtos.add(dto);
            }
        }
        return new ListDTO<KnArticleCommentDTO>(true,dtos);
    }
}