package com.kingnode.regulation.controller;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.diva.web.Servlets;
import com.kingnode.regulation.bean.KnFile;
import com.kingnode.regulation.entity.KnClassification;
import com.kingnode.regulation.entity.KnRegulationFile;
import com.kingnode.regulation.service.RegulationService;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller @RequestMapping(value="/regulation/profile")
public class RegulationConttroler{
    @Autowired
    private RegulationService rs;
    @RequestMapping(method=RequestMethod.GET)
    public String list(Model model){
        return "regulation/profileList";
    }
    @RequestMapping(value="create", method=RequestMethod.GET)
    public String create(Model model){
        return "regulation/profileForm";
    }
    /**
     * 创建文件
     *
     * @param file
     * @param profile
     * @param redirectAttributes
     *
     * @return
     */
    @RequestMapping(value="create", method=RequestMethod.POST)
    public String create(@RequestParam("files") MultipartFile file,KnRegulationFile profile,RedirectAttributes redirectAttributes){
        String classStr=profile.getClassification();
        if(file!=null&&!Utils.isEmptyStr(classStr)){
            KnFile fileObject=rs.UploadFile(file);
            profile.setSavePath(fileObject.getSavePath());
            profile.setOriginalFileName(fileObject.getOriginalFileName());
            profile.setReadCount(0L);
            profile.setFileSize(file.getSize());
            profile.setFileFormat(fileObject.getFileFormat());
        }else{
            String msg="保存资料失败!";
            redirectAttributes.addFlashAttribute("message",msg);
            return "redirect:/regulation/profile";
        }
        classStr=classStr.trim();
        KnClassification kc=rs.findByClassification(classStr);
        if(kc==null){
            KnClassification newkc=new KnClassification();
            newkc.setClassification(classStr);
            rs.SaveClassification(newkc);
        }
        profile.setClassification(classStr);
        profile.setUploadTime(System.currentTimeMillis());
        rs.Save(profile);
        //knFile.isEmptyString()
        String msg="保存资料成功!";
        redirectAttributes.addFlashAttribute("message",msg);
        return "redirect:/regulation/profile";
    }
    /**
     * 删除资料
     *
     * @param ids                资料id
     * @param redirectAttributes
     *
     * @return
     */
    @RequestMapping(value="delete", method=RequestMethod.POST)
    public String delete(String ids,RedirectAttributes redirectAttributes){
        ObjectMapper mapper=new ObjectMapper();
        JavaType javaType=mapper.getTypeFactory().constructParametricType(List.class,Long.class);
        List<Long> list=JsonMapper.nonDefaultMapper().fromJson(ids,javaType);
        rs.Delete(list);
        String msg="删除资料成功!";
        redirectAttributes.addFlashAttribute("message",msg);
        return "redirect:/regulation/profile";
    }
    /**
     * 对KnRegulationFile进行分页
     *
     * @param dt
     * @param request
     *
     * @return
     */
    @RequestMapping(value="page-profilelist", method=RequestMethod.POST) @ResponseBody
    public DataTable<KnRegulationFile> list(DataTable<KnRegulationFile> dt,ServletRequest request){
        Sort sort=Utils.getSort(dt,request);
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return rs.PageListFile(searchParams,dt,sort);
    }
}


