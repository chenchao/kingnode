package com.kingnode.eka.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.google.common.collect.Lists;
import com.kingnode.diva.web.Servlets;
import com.kingnode.eka.entity.KnEka;
import com.kingnode.eka.entity.KnEkaBus;
import com.kingnode.eka.entity.KnEkaCategories;
import com.kingnode.eka.service.EkaService;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller
@RequestMapping(value="/eka")
public class EkaConttroler{
    @Autowired
    private EkaService es;


    /**
     * 返回壹卡会列表界面
     *
     * @return jsp文件
     */
    @RequestMapping(value="list", method=RequestMethod.GET)
    public String home(Model model){
        return "eka/ekaList";

    }

    /**
     * 壹卡会管理首页
     *
     * @return jsp文件
     */
    @RequestMapping(value="list", method=RequestMethod.POST)
    @ResponseBody
    public DataTable<KnEka> list(DataTable<KnEka> dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return es.PageKnEka(searchParams,dt);
    }

    /**
     * 进入壹卡会编辑页面
     */
    @RequestMapping(value="create", method=RequestMethod.GET)
    public String createForm(Model model){
        KnEka ka=new KnEka();
        model.addAttribute("eka",ka);
        model.addAttribute("action","create");
        return "eka/ekaForm";
    }

    @RequestMapping(value="create", method=RequestMethod.POST)
    public String create(@Valid KnEka ka,RedirectAttributes redirectAttributes,HttpServletRequest request){
        String[] busTitles=request.getParameterValues("busTitle");
        String[] busComments=request.getParameterValues("busComment");

        ka.setPraiseNums(0l);
        ka.setTreadNums(0l);
        es.save(ka,busTitles,busComments);
        redirectAttributes.addFlashAttribute("message","创建壹卡会成功");
        return "eka/ekaList";
    }

    @RequestMapping(value="update/{id}", method=RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id,Model model){
        KnEka eka=es.FindKnEka(id);
        model.addAttribute("eka",eka);
        model.addAttribute("action","update");
        if(eka.getSmall().getId().intValue()==EkaService.BusId.intValue()){
            List<KnEkaBus> buses=es.ListEkaBus(eka.getId());
            if(!Utils.isEmpityCollection(buses)){
                model.addAttribute("buses",buses);
            }
        }
        return "eka/ekaForm";
    }

    @RequestMapping(value="update", method=RequestMethod.POST)
    public String update(@Valid @ModelAttribute("eka") KnEka ka,RedirectAttributes redirectAttributes,HttpServletRequest request){
        String[] busTitles=request.getParameterValues("busTitle");
        String[] busComments=request.getParameterValues("busComment");
        es.save(ka,busTitles,busComments);
        redirectAttributes.addFlashAttribute("message","更新壹卡会成功");
        return "eka/ekaList";
    }

    /**
     * 删除评论信息
     */
    @RequestMapping(value="delete", method=RequestMethod.POST)
    @ResponseBody
    public String delete(@RequestParam("ids") String[] ids,RedirectAttributes redirectAttributes,ServletRequest request){
        es.deleteEka(ids);
        redirectAttributes.addFlashAttribute("message","修改书籍信息成功");
        return "true";
    }


    /**
     * 获取壹卡会大类型
     */
    @RequestMapping(value="categories", method=RequestMethod.POST)
    @ResponseBody
    public Map categories(){
        List<KnEkaCategories> list=es.FindAllCategories();
        List<KnEkaCategories> list2 =Lists.newArrayList();
        if(!Utils.isEmpityCollection(list)){
            for(KnEkaCategories categories : list){
                if(categories.getId()!=7l){
                    list2.add(categories);
                }
            }
        }
        Map map=new HashMap();
        map.put("types",list2);
        return map;
    }


    /**
     * 获取壹卡会大类型
     */
    @RequestMapping(value="importData", method=RequestMethod.POST)
    @ResponseBody
    public Map importData(@RequestParam("uploadFile") MultipartFile file,HttpServletResponse response) throws IOException{
        response.setContentType("text/html; charset=UTF-8");
        WebApplicationContext webApplicationContext=ContextLoader.getCurrentWebApplicationContext();
        String fileExt=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1).toLowerCase();//扩展名
        String fileName="/upload/"+UUID.randomUUID().toString()+"."+fileExt;
        File localFile=new File(webApplicationContext.getServletContext().getRealPath(fileName));
        if(!localFile.getParentFile().exists()){
            localFile.getParentFile().mkdirs();
        }
        file.transferTo(localFile);
        Map<String,Object> map=es.importEka(localFile,fileExt);
        return map;
    }
}
