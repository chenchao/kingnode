package com.kingnode.library.controller;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.validation.Valid;

import com.kingnode.diva.web.Servlets;
import com.kingnode.library.entity.KnLibraryBookUnit;
import com.kingnode.library.entity.KnLibraryBorrow;
import com.kingnode.library.entity.KnLibraryRule;
import com.kingnode.library.service.LibraryService;
import com.kingnode.xsimple.api.common.DataTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller @RequestMapping(value="/library")
public class KnLibraryBorrowController{
    @Autowired
    private LibraryService ls;
    private final static String defaultImg="";
    private final static String searchPath="/assets/global/img/search.png";

    /**
     * 跳转到借阅记录列表
     * @param
     * @return
     */
    @RequestMapping(value="borrow-recode")
    public String borrowRecode(){
        return "library/libraryRecode";
    }

    /**
     * 跳转到还书管理列表
     * @param
     * @return
     */
    @RequestMapping(value="return-borrow")
    public String returnBorrow(){
        return "library/libraryReturn";
    }

    /**
     * 跳转到借书管理列表
     * @param
     * @return
     */
    @RequestMapping(value="borrow-manage")
    public String listBorrow(){
        return "library/libraryBorrow";
    }

    /**
     * 跳转到借书规则界面
     * @param
     * @return
     */
    @RequestMapping(value="borrow-rule")
    public String rule(Model model){
        KnLibraryRule rule = ls.findKnLibraryRule();
        model.addAttribute("rule",rule);
        return "library/libraryRule";
    }


    /**
     * 借书管理列表
     * @param request
     * @return
     */
    @RequestMapping(value="borrow-list",method=RequestMethod.POST) @ResponseBody
    public DataTable<KnLibraryBorrow> listBorrow(DataTable<KnLibraryBorrow> dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        try{
            return ls.listKnLibraryBorrow(searchParams,dt);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 跳转到保存借书规则页面
     * @param model
     * @return
     */
    @RequestMapping(value="create-rule",method=RequestMethod.POST)
    public String createRuleForm(@Valid @ModelAttribute("rule") KnLibraryRule rule,Model model){
        KnLibraryRule ru = ls.save(rule);
        model.addAttribute("message","创建借书规则信息成功.");
        model.addAttribute("rule",ru);
        return "library/libraryRule";
    }



    /**
     * 管理员借出书籍
     * @param ids
     * @param
     * @return
     */
    @RequestMapping(value="borrow") @ResponseBody
    public String borrow(@RequestParam("ids")String[] ids){
        ls.borrow(ids);
        return "true";
    }

    /**
     * 管理员还书籍
     * @param ids
     * @param
     * @return
     */
    @RequestMapping(value="returnBook") @ResponseBody
    public String returnBook(@RequestParam("ids")String[] ids){
        ls.returnBook(ids);
        return "true";
    }

}
