package com.kingnode.library.controller;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.validation.Valid;

import com.kingnode.diva.web.Servlets;
import com.kingnode.library.entity.KnLibraryBook;
import com.kingnode.library.entity.KnLibraryBookUnit;
import com.kingnode.library.service.LibraryService;
import com.kingnode.xsimple.api.common.DataTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller @RequestMapping(value="/library")
public class KnLibraryBookController{
    @Autowired
    private LibraryService ls;
    private final static String defaultImg="";
    private final static String searchPath="/assets/global/img/search.png";

    /**
     * 跳转到书籍管理列表页面
     * @return
     */
    @RequestMapping(value="list",method=RequestMethod.GET)
    public String home(){
        return "library/libraryList";
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    @RequestMapping(value="list",method=RequestMethod.POST) @ResponseBody
    public DataTable<KnLibraryBookUnit> list(DataTable<KnLibraryBookUnit> dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        try{
            return ls.listKnLibraryBookUnit(searchParams,dt);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 跳转到书籍详细信息界面
     * @param
     * @return
     */
    @RequestMapping(value="book-detail/{bookId}",method=RequestMethod.GET)
    public String bookDetial(@PathVariable("bookId") Long bookId,Model model){
        List<KnLibraryBookUnit> units = ls.listKnLibraryBookUnit(bookId);
        List<Integer> countList = ls.findEnabalBorrow(units);
        model.addAttribute("canCount",countList.get(0));
        model.addAttribute("bookId",bookId);
        if(units != null && units.size() > 0){
            model.addAttribute("book",units.get(0));
            model.addAttribute("count",units.size()-countList.get(1));
        }else{
            KnLibraryBookUnit unit = new KnLibraryBookUnit();
            unit.setKbl(new KnLibraryBook());
            model.addAttribute("book",unit);
            model.addAttribute("count",0);
        }
        return "library/libraryDetail";
    }

    /**
     * 根据书本id，获取对应书籍的列表
     * @param bookId
     * @return
     */
    @RequestMapping(value="detail-list",method=RequestMethod.POST) @ResponseBody
    public DataTable<KnLibraryBookUnit> listKnLibraryBookUnit(DataTable<KnLibraryBookUnit> dt,@RequestParam(value = "bookId") Long bookId,Model model){
        try{
            List list = ls.listKnLibraryBookUnit(bookId);
            dt.setiTotalDisplayRecords(list.size());
            dt.setAaData(list);
            return dt;
        }catch(Exception e){
            e.printStackTrace();
        }
        return dt;
    }

    /**
     * 跳转到保存书籍信息页面
     * @param model
     * @return
     */
    @RequestMapping(value="create",method=RequestMethod.GET)
    public String createForm(Model model){
        model.addAttribute("action","create");
        model.addAttribute("defaultImg",defaultImg);
        model.addAttribute("searchPath",searchPath);
        model.addAttribute("number",ls.bookSysNumber());
        KnLibraryBookUnit unit = new KnLibraryBookUnit();
        unit.setKbl(new KnLibraryBook());
        model.addAttribute("book",unit);
        return "library/libraryForm";
    }

    /**
     * 保存书籍信息页面
     * @param model
     * @return
     */
    @RequestMapping(value="create",method=RequestMethod.POST)
    public String create(@Valid @ModelAttribute("book") KnLibraryBookUnit book,Model model,ServletRequest request,@RequestParam(value="buyDateBook") String buyDateBook){
        String message = "创建图书信息成功.";
        try{
            ls.save(book,buyDateBook);
        }catch(Exception e){
            e.printStackTrace();
            message = book.getCode()+"此编号已经存在,请刷新后再试编辑。";
        }
        model.addAttribute("message",message);
        return "library/libraryList";
    }

    /**
     * 跳转到修改书籍信息页面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "update/{id}", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("book", ls.findKnLibraryBookUnit(id));
        model.addAttribute("action", "update");
        model.addAttribute("defaultImg",defaultImg);
        model.addAttribute("searchPath",searchPath);
        return "library/libraryForm";
    }

    /**
     * 修改书籍信息
     * @param book
     * @param
     * @return
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute("book") KnLibraryBookUnit book, Model model,@RequestParam(value="buyDateBook") String buyDateBook) {
        ls.save(book,buyDateBook);
        model.addAttribute("message","修改书籍信息成功");
        return "library/libraryList";
    }

    /**
     * 逻辑删除书籍信息
     * @param ids
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value="delete") @ResponseBody
    public String delete(@RequestParam("ids")String[] ids,  RedirectAttributes redirectAttributes,ServletRequest request) {
        String message = "删除书籍信息成功";
        try{
            ls.delete(ids);
        }catch(Exception e){
            message = e.getMessage();
            redirectAttributes.addFlashAttribute("message", message);
            return "false";
        }
        redirectAttributes.addFlashAttribute("message", message);
        return "true";
    }

    /**
     * 获取isbn号
     * @param isbn
     * @return
     */
    @RequestMapping(value="isbn/{isbn}", method=RequestMethod.GET) @ResponseBody
    public Map isbn(@PathVariable("isbn") String isbn){
        return ls.ReadBookInfoFormD(isbn);
    }
}
