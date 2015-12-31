package com.kingnode.news.rest;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
import com.kingnode.news.dto.WelfareDTO;
import com.kingnode.news.service.NewsService;
import com.kingnode.xsimple.rest.DetailDTO;
import com.kingnode.xsimple.rest.ListDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * 工会福利rest接口
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@RestController @RequestMapping({"/api/v1/content/welfare","/api/secure/v1/content/welfare"})
public class WelfareRestController{
    @Autowired
    private NewsService ns;
    /**
     * 周边福利列表
     * 接口的请求方式: get
     * 示例：http://localhost:8080/api/v1/content/welfare/list
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
        "content" : "53453453453453",//（内容）
        "img" : "http://192.168.8.161:8080",//（如果有图片请提供）
        }]
        }
     */
    @RequestMapping(value="/list", method={RequestMethod.GET})
    public ListDTO<WelfareDTO> List(@RequestParam(value="p",defaultValue="0") Integer pageNo,@RequestParam(value="s",defaultValue="5") Integer pageSize){
        return new ListDTO<>(true,ns.PageKnWelfare(pageNo,pageSize));
    }
    /**
     * 周边福利列表
     * 接口的请求方式: get
     * 示例：http://localhost:8080/api/v1/content/welfare/read/1
     * 返回：{"status":true,"errorCode":null,"errorMessage":null,"detail":"true"}
     */
    @RequestMapping(value="/read/{id}",method={RequestMethod.GET})
    public DetailDTO<WelfareDTO> Read(@PathVariable("id") Long id){
        return ns.ReadKnWelfare(id);
    }
}