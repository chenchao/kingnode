package com.kingnode.library.rest;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.kingnode.diva.mapper.BeanMapper;
import com.kingnode.library.dto.KnLibraryBookDTO;
import com.kingnode.library.dto.KnLibraryBookUnitDTO;
import com.kingnode.library.dto.KnLibraryBorrowDTO;
import com.kingnode.library.dto.KnLibraryRuleDTO;
import com.kingnode.library.entity.KnLibraryBook;
import com.kingnode.library.entity.KnLibraryBookUnit;
import com.kingnode.library.entity.KnLibraryBorrow;
import com.kingnode.library.entity.KnLibraryRule;
import com.kingnode.library.service.LibraryService;
import com.kingnode.xsimple.rest.DetailDTO;
import com.kingnode.xsimple.rest.ListDTO;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@RestController @RequestMapping({"/api/v1/library","/api/secure/v1/library"})
public class KnLibraryRestController{
    @Autowired
    private LibraryService ls;
    public KnLibraryRestController(){
    }
    /**
     * 借阅记录
     * 接口的请求方式: get
     * 示例：http://localhost:8080/api/v1/library/borrow/list?p=0&s=2
     * 参数 ：
     *   p页码 s每页记录数
     * 返回：
     * {
     * "status" : true,
     * "errorCode" : null,
     * "errorMessage" : null,
     * "list" : [ {
     * "id" : 161,
     * "bookCode" : "0004",//书籍编码
     * "userId" : 1,//借阅人ID
     * "name" : "管理员",//借阅人姓名
     * "reserveDate" : "1412753407522",//预借时间
     * "lendDate" : "",//借阅时间
     * "restoreDate" : "",//归还时间
     * "renewDate" : "",//续借时间
     * "shouldRestoreDate" : "",//应归还时间
     * "cancelDate" : "",//取消时间
     * "currentDate" : "2014-10-09 11:15:03",//当前时间
     * "borrowType" : "RESERVE",//结束类型
     * "klbu" : {
     * "id" : 35,
     * "klb" : {
     * "id" : 35,
     * "isbn" : "9787538585568",//书号ISBN: 9787111389187
     * "title" : "只有往前飞奔（顺丰领头人王卫）",//标题
     * "price" : 32.8,//定价: 79.00元
     * "subtitle" : "",//副标题,
     * "author" : "邢柏",//作者，可能有多个使用使用,分开
     * "pubDate" : "2014-8-30",//出版年: 2012-8
     * "tags" : "[object Object],[object Object],[object Object]",//标签
     * "image" : "http://img3.douban.com/mpic/s27411031.jpg",//封面图片
     * "binding" : "平装",//装订
     * "translator" : "",//译者
     * "pages" : 240,//页数: 440
     * "publisher" : "北方妇女儿童出版社",//出版者
     * "summary" : "王卫鲜少在公众面前露面，但所有的人都在找寻他；^^^^？",//内容简介
     * "ratingMax" : null,//最大评论分数
     * "ratingMin" : null,//最小评论分数
     * "numRaters" : null,//评论人数
     * "average" : null,//评论平均分数
     * "alt" : "http://book.douban.com/subject/11542973/",//豆瓣书地址//http://book.douban.com/subject/11542973/,
     * "douban" : "url=http://api.douban.com/v2/book/11542973",//url=http://api.douban.com/v2/book/11542973
     * "inventoryNum" : null,//库存
     * "canBorrowNum" : null,//可借数量
     * "klbs" : null
     * },
     * "code" : "0004",
     * "buyPrice" : 32.8,
     * "buyDate" : "1412092800000",
     * "bookType" : "RESERVE"
     * }
     * } ]
     * }
     */
    @RequestMapping(value="/borrow/list", method={RequestMethod.GET})
    public ListDTO<KnLibraryBorrowDTO> List(@RequestParam(value="p", defaultValue="0") Integer pageNo,@RequestParam(value="s", defaultValue="10") Integer pageSize){
        Page<KnLibraryBorrow> kns=ls.listKnLibraryBorrow("",pageNo,pageSize);
        List<KnLibraryBorrowDTO> list=Lists.newArrayList();
        if(kns!=null&&kns.getSize()>0){
            for(KnLibraryBorrow borrow : kns.getContent()){
                KnLibraryBorrowDTO borrow1=BeanMapper.map(borrow,KnLibraryBorrowDTO.class);
                KnLibraryBookUnitDTO unitDTO=BeanMapper.map(borrow.getKlbu(),KnLibraryBookUnitDTO.class);
                KnLibraryBookDTO bookDTO=BeanMapper.map(borrow.getKlbu().getKbl(),KnLibraryBookDTO.class);
                unitDTO.setKlb(bookDTO);
                borrow1.setKlbu(unitDTO);
                borrow1.setLendDate(borrow.getLendDate()!=null?new DateTime(borrow.getLendDate()).toString("yyyy-MM-dd HH:mm:ss"):"");
                borrow1.setRestoreDate(borrow.getRestoreDate()!=null?new DateTime(borrow.getRestoreDate()).toString("yyyy-MM-dd HH:mm:ss"):"");
                borrow1.setRenewDate(borrow.getRenewDate()!=null?new DateTime(borrow.getRenewDate()).toString("yyyy-MM-dd HH:mm:ss"):"");
                borrow1.setShouldRestoreDate(borrow.getShouldRestoreDate()!=null?new DateTime(borrow.getShouldRestoreDate()).toString("yyyy-MM-dd HH:mm:ss"):"");
                borrow1.setCancelDate(borrow.getCancelDate()!=null?new DateTime(borrow.getCancelDate()).toString("yyyy-MM-dd HH:mm:ss"):"");
                list.add(borrow1);
            }
        }
        return new ListDTO<>(true,list);
    }
    /**
     * 获取书籍详细信息
     * 接口的请求方式: get
     * 示例：http://localhost:8080/api/v1/library/book/35
     * 参数：bookId 书本id
     * 返回：{
     * "status" : true,
     * "errorCode" : null,
     * "errorMessage" : null,
     * "list" : [ {
     * "id" : 35,
     * "isbn" : "9787538585568",//书号ISBN: 9787111389187
     * "title" : "只有往前飞奔（顺丰领头人王卫）",//标题
     * "price" : 32.8,//定价: 79.00元
     * "subtitle" : "",//副标题,
     * "author" : "邢柏",//作者，可能有多个使用使用,分开
     * "pubDate" : "2014-8-30",//出版年: 2012-8
     * "tags" : "[object Object],[object Object],[object Object]",//标签
     * "image" : "http://img3.douban.com/mpic/s27411031.jpg",//封面图片
     * "binding" : "平装",//装订
     * "translator" : "",//译者
     * "pages" : 240,//页数: 440
     * "publisher" : "北方妇女儿童出版社",//出版者
     * "summary" : "王卫鲜少在公众面前露面，但所有的人都在找寻他；^^^^？",//内容简介
     * "ratingMax" : null,//最大评论分数
     * "ratingMin" : null,//最小评论分数
     * "numRaters" : null,//评论人数
     * "average" : null,//评论平均分数
     * "alt" : "http://book.douban.com/subject/11542973/",//豆瓣书地址//http://book.douban.com/subject/11542973/,
     * "douban" : "url=http://api.douban.com/v2/book/11542973",//url=http://api.douban.com/v2/book/11542973
     * "inventoryNum" : null,//库存
     * "canBorrowNum" : null,//可借数量
     * "klbs" : [ ]
     * } ]
     * }
     */
    @RequestMapping(value="/book/{bookId}", method={RequestMethod.GET})//TODO:修改url  detail
    public ListDTO<KnLibraryBookDTO> BookDetail(@PathVariable(value="bookId") Long bookId){
        KnLibraryBook book=ls.findKnLibraryBook(bookId);
        KnLibraryBookDTO bookDTO=BeanMapper.map(book,KnLibraryBookDTO.class);
        bookDTO.setSummary(ls.SubString(bookDTO.getSummary(),150));
        //获取库存和可借本书
        List<KnLibraryBookUnit> units=ls.listKnLibraryBookUnit(bookId);
        List<Integer> countList=ls.findEnabalBorrow(units);
        bookDTO.setCanBorrowNum(countList.get(0));
        bookDTO.setInventoryNum(units.size()-countList.get(1));
        List<KnLibraryBorrow> borrows=ls.queryKnLibraryBorrows(bookId);
        List<KnLibraryBorrowDTO> list=Lists.newArrayList();
        if(borrows!=null&&borrows.size()>0){
            for(KnLibraryBorrow borrow : borrows){
                KnLibraryBorrowDTO borrowDTO=BeanMapper.map(borrow,KnLibraryBorrowDTO.class);
                borrowDTO.setLendDate(borrow.getLendDate()!=null?new DateTime(borrow.getLendDate()).toString("yyyy-MM-dd HH:mm:ss"):"");
                borrowDTO.setRestoreDate(borrow.getRestoreDate()!=null?new DateTime(borrow.getRestoreDate()).toString("yyyy-MM-dd HH:mm:ss"):"");
                borrowDTO.setRenewDate(borrow.getRenewDate()!=null?new DateTime(borrow.getRenewDate()).toString("yyyy-MM-dd HH:mm:ss"):"");
                list.add(borrowDTO);
            }
        }
        bookDTO.setKlbs(list);
        List<KnLibraryBookDTO> bookDTOs=Lists.newArrayList();
        bookDTOs.add(bookDTO);
        return new ListDTO<>(true,bookDTOs);
    }
    /**
     * 获取书籍详细信息列表
     * 接口的请求方式: post
     * 示例：http://localhost:8080/api/v1/library/book/list   post方式 {"pageNo":0,"pageSize":1,"info":}
     * 参数：  pageNo：页码 pageSize：每页记录书 info：图书标题／作者／isbn号
     * 返回：{
     * "status" : true,
     * "errorCode" : null,
     * "errorMessage" : null,
     * "list" : [ {
     * "id" : 35,
     * "isbn" : "9787538585568",//书号ISBN: 9787111389187
     * "title" : "只有往前飞奔（顺丰领头人王卫）",//标题
     * "price" : 32.8,//定价: 79.00元
     * "subtitle" : "",//副标题,
     * "author" : "邢柏",//作者，可能有多个使用使用,分开
     * "pubDate" : "2014-8-30",//出版年: 2012-8
     * "tags" : "[object Object],[object Object],[object Object]",//标签
     * "image" : "http://img3.douban.com/mpic/s27411031.jpg",//封面图片
     * "binding" : "平装",//装订
     * "translator" : "",//译者
     * "pages" : 240,//页数: 440
     * "publisher" : "北方妇女儿童出版社",//出版者
     * "summary" : "王卫鲜少在公众面前露面，但所有的人都在找寻他；^^^^？",//内容简介
     * "ratingMax" : null,//最大评论分数
     * "ratingMin" : null,//最小评论分数
     * "numRaters" : null,//评论人数
     * "average" : null,//评论平均分数
     * "alt" : "http://book.douban.com/subject/11542973/",//豆瓣书地址//http://book.douban.com/subject/11542973/,
     * "douban" : "url=http://api.douban.com/v2/book/11542973",//url=http://api.douban.com/v2/book/11542973
     * "inventoryNum" : null,//库存
     * "canBorrowNum" : null,//可借数量
     * "klbs" : [ ]
     * } ]
     * }
     */
    @RequestMapping(value="/book/list", method={RequestMethod.POST})
    public ListDTO<KnLibraryBookDTO> BookList(@RequestParam(value="pageNo", defaultValue="0") Integer pageNo,@RequestParam(value="pageSize") Integer pageSize,@RequestParam(value="info") String info){
        List<KnLibraryBookDTO> list=Lists.newArrayList();
        Set<KnLibraryBook> books=ls.listKnLibraryBookUnit(info,pageNo,pageSize);
        if(books!=null&&books.size()>0){
            for(KnLibraryBook book : books){
                KnLibraryBookDTO bookDTO=BeanMapper.map(book,KnLibraryBookDTO.class);
                //获取库存和可借本书
                List<KnLibraryBookUnit> units=ls.listKnLibraryBookUnit(bookDTO.getId());
                List<Integer> countList=ls.findEnabalBorrow(units);
                bookDTO.setCanBorrowNum(countList.get(0));
                bookDTO.setInventoryNum(units.size()-countList.get(1));
                list.add(bookDTO);
            }
        }
        return new ListDTO<>(true,list);
    }
    /**
     * 借书申请
     * 接口的请求方式: post
     * 示例：http://localhost:8080/api/v1/library/lend   post：｛"bookId":35｝
     * 参数： bookId 书本id
     *
     * @返回：{ "status" : true,
     * "errorCode" : null,
     * "errorMessage" : null,
     * "detail" : true
     * }
     */
    @RequestMapping(value="/lend/{bookId}", method={RequestMethod.POST})
    public DetailDTO<String> LendBook(@PathVariable(value="bookId") Long bookId){
        DetailDTO<String> detailDTO=new DetailDTO<>();
        boolean flag=false;
        try{
            flag=ls.lendBook(bookId);
            if(!flag){
                detailDTO.setErrorMessage("此书已经借出。");
            }
        }catch(Exception e){
            detailDTO.setErrorCode("book1");
            detailDTO.setErrorMessage(e.getMessage());
        }
        detailDTO.setStatus(flag);
        return detailDTO;
    }
    /**
     * 续借
     * 接口的请求方式: post
     * 示例：http://localhost:8080/api/v1/library/renew   p
     * 参数： borrowId 借阅记录id
     *
     * @返回：{ "status" : true,
     * "errorCode" : null,
     * "errorMessage" : null,
     * "detail" : true
     * }
     */
    @RequestMapping(value="/renew/{borrowId}", method={RequestMethod.POST})
    public DetailDTO<String> Renew(@PathVariable(value="borrowId") Long borrowId){
        DetailDTO<String> detailDTO=new DetailDTO<>();
        boolean flag=false;
        try{
            flag=ls.renewBook(borrowId);
        }catch(Exception e){
            detailDTO.setErrorCode("book2");
            detailDTO.setErrorMessage(e.getMessage());
        }
        detailDTO.setDetail(flag+"");
        detailDTO.setStatus(flag);
        return detailDTO;
    }
    /**
     * 取消
     * 接口的请求方式: post
     * 示例：http://localhost:8080/api/v1/library/cancel
     * 参数： borrowId 借阅记录id
     *
     * @返回：{ "status" : true,
     * "errorCode" : null,
     * "errorMessage" : null,
     * "detail" : true
     * }
     */
    @RequestMapping(value="/cancel/{borrowId}", method={RequestMethod.POST})
    public DetailDTO<String> Cancel(@PathVariable(value="borrowId") Long borrowId){
        DetailDTO<String> detailDTO=new DetailDTO<>();
        boolean flag=false;
        flag=ls.cancel(borrowId);
        detailDTO.setDetail(flag+"");
        detailDTO.setStatus(flag);
        return detailDTO;
    }
    /**
     * 借阅规则
     * 接口的请求方式: get
     * 示例：http://localhost:8080/api/v1/library/rule
     * 参数：无
     * 返回：{
     * "status" : true,
     * "errorCode" : null,
     * "errorMessage" : null,
     * "detail" : {
     * "limitUnit" : 5,//限借本数
     * "borrowDay" : 10,//借阅最长多少天
     * "overdue" : 1,//逾期规则
     * "renew" : 1,//续借规则
     * "expiration" : 3//到期提醒
     * }
     * }
     */
    @RequestMapping(value="/rule", method={RequestMethod.GET})
    public DetailDTO<KnLibraryRuleDTO> Rule(){
        DetailDTO<KnLibraryRuleDTO> detailDTO=new DetailDTO<>();
        KnLibraryRule rule=ls.findKnLibraryRule();
        KnLibraryRuleDTO ruleDTO=BeanMapper.map(rule,KnLibraryRuleDTO.class);
        detailDTO.setDetail(ruleDTO);
        detailDTO.setStatus(true);
        return detailDTO;
    }
}
