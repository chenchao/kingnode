package com.kingnode.auto.controller;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.servlet.ServletRequest;

import com.kingnode.auto.entity.KnAuto;
import com.kingnode.auto.entity.KnSetting;
import com.kingnode.auto.service.KnAutoManageService;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
 * Created by xushuangyong on 14-9-15.
 */
@Controller @RequestMapping(value="/auto/manage")
public class KnAutoManageController{
    private static final Logger logger=LoggerFactory.getLogger(KnAutoManageController.class);
    @Autowired
    private KnAutoManageService autoManageService;
    @RequestMapping(method=RequestMethod.GET)
    public String list(Model model){
        return "auto/manage";
    }
    @RequestMapping(value="setting",method=RequestMethod.GET)
    public String setting(Model model){
        KnSetting seeting=autoManageService.FetchSetting();
        model.addAttribute("setting",seeting);
        return "auto/setting";
    }
    /**
     * 打开创建车辆页面
     *
     * @param model
     *
     * @return
     */
    @RequestMapping(value="create",method=RequestMethod.GET)
    public String create(Model model){
        model.addAttribute("actionName","创建");
        model.addAttribute("atuoJson",JsonMapper.nonEmptyMapper().toJson(new HashMap<>()));
        model.addAttribute("url","/auto/manage/create");
        return "auto/autoForm";
    }
    /**
     * 创建车辆
     *
     * @param auto
     * @param redirectAttributes
     *
     * @return
     */
    @RequestMapping(value="create",method=RequestMethod.POST)
    public String create(@RequestParam("files") MultipartFile file,KnAuto auto,RedirectAttributes redirectAttributes){
        String msg="保存车辆成功!";
        try{
            if(file!=null&&file.getSize()>0){
                WebApplicationContext webApplicationContext=ContextLoader.getCurrentWebApplicationContext();
                String fileExt=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1).toLowerCase();//扩展名
                String uuid=UUID.randomUUID().toString();
                String fileName=KnAuto.getUploadPath()+uuid+"."+fileExt;
                File localFile=new File(webApplicationContext.getServletContext().getRealPath(fileName));
                if(!localFile.getParentFile().exists()){
                    localFile.getParentFile().mkdirs();
                }
                file.transferTo(localFile);
                if(localFile.length()>0){
                    auto.setPhoto(fileName);
                }
            }
        }catch(Exception e){
            // logger.error(e.getMessage());
        }
        autoManageService.SaveAuto(auto);
        redirectAttributes.addFlashAttribute("message",msg);
        return "redirect:/auto/manage";
    }
    /**
     * 打开修改车辆页面
     *
     * @param id
     * @param model
     *
     * @return
     */
    @RequestMapping(value="update/{id}",method=RequestMethod.GET)
    public String update(@PathVariable("id") Long id,Model model){
        KnAuto auto=autoManageService.ReadAuto(id);
        model.addAttribute("atuoJson",JsonMapper.nonEmptyMapper().toJson(auto));
        model.addAttribute("atuo",auto);
        model.addAttribute("actionName","修改");
        model.addAttribute("url","/auto/manage/update");
        return "auto/autoForm";
    }
    /**
     * 更新车辆
     *
     * @param auto
     * @param redirectAttributes
     *
     * @return
     */
    @RequestMapping(value="update",method=RequestMethod.POST)
    public String update(@RequestParam("files") MultipartFile file,KnAuto auto,RedirectAttributes redirectAttributes){
        String msg="修改车辆成功!";
        KnAuto oldAuto=autoManageService.ReadAuto(auto.getId());
        try{
            updateImg(file,auto,oldAuto);
        }catch(IOException io){
            logger.error(io.getMessage());
        }
        autoManageService.SaveAuto(auto);
        redirectAttributes.addFlashAttribute("message",msg);
        return "redirect:/auto/manage";
    }
    /**
     * 对车辆进行分页显示
     *
     * @param dt
     * @param request
     *
     * @return
     */
    @RequestMapping(value="page-autos",method=RequestMethod.POST) @ResponseBody
    public DataTable<KnAuto> list(DataTable<KnAuto> dt,ServletRequest request){
        Sort sort=Utils.getSort(dt,request);
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return autoManageService.PageAutos(searchParams,dt,sort);
    }
    /**
     * 删除车辆
     *
     * @param id
     *
     * @return
     */
    @RequestMapping(value="delete",method=RequestMethod.POST) @ResponseBody
    public Map delete(long id){
        Map map=new HashMap();
        try{
            autoManageService.DeleteKnAuto(id);
            map.put("success",true);
            return map;
        }catch(Exception e){
            e.printStackTrace();
        }
        map.put("success",false);
        map.put("message","");
        return map;
    }
    /**
     * 更新车辆图片
     *
     * @param file
     * @param emp
     * @param oldEmp
     *
     * @throws java.io.IOException
     */
    private void updateImg(MultipartFile file,KnAuto emp,KnAuto oldEmp) throws IOException{
        if(file!=null&&file.getSize()>0){
            WebApplicationContext webApplicationContext=ContextLoader.getCurrentWebApplicationContext();
            String fileExt=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1).toLowerCase();//扩展名
            String uuid=UUID.randomUUID().toString();
            String fileName=KnAuto.getUploadPath()+uuid+"."+fileExt;
            File localFile=new File(webApplicationContext.getServletContext().getRealPath(fileName));
            if(!localFile.getParentFile().exists()){
                localFile.getParentFile().mkdirs();
            }
            file.transferTo(localFile);
            if(localFile.length()>0){
                try{
                    emp.setPhoto(fileName);
                    //删除旧的图片文件
                    File oldLocalFile=new File(webApplicationContext.getServletContext().getRealPath(oldEmp.getPhoto()));
                    if(oldLocalFile.exists()){
                        oldLocalFile.delete();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }else{
            emp.setPhoto(oldEmp.getPhoto());
        }

    }
    /**
     * 设置车辆申请超时
     *
     * @param model
     *
     * @return
     */
    @RequestMapping(value="set-overtime",method=RequestMethod.POST)
    public String setOverTime(KnSetting setting,Model model,RedirectAttributes redirectAttributes){
        KnSetting seeting=null;
        if(setting.getMinutes()==null||setting.getMinutes()<0){
            redirectAttributes.addFlashAttribute("message","修改失败");
        }else{
            seeting=autoManageService.SaveSetting(setting);
            redirectAttributes.addFlashAttribute("message","修改成功");
        }
        model.addAttribute("setting",seeting);
        return "redirect:/auto/manage/setting";
    }
}
