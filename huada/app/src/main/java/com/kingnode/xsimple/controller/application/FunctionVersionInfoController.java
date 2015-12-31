package com.kingnode.xsimple.controller.application;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.google.common.base.Strings;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.entity.system.KnResource;
import com.kingnode.xsimple.entity.web.KnFunctionVersionInfo;
import com.kingnode.xsimple.service.application.ApplicationInfoService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 功能版本管理controller
 *
 * @孔江伟
 */
@Controller @RequestMapping(value="/application/function-version")
public class FunctionVersionInfoController{
    @Autowired
    private ApplicationInfoService applicationInfoService;
    @RequiresPermissions("application-function") @RequestMapping(value="find-zip-list-by-fun-id",method=RequestMethod.POST) @ResponseBody
    public List<KnFunctionVersionInfo> findZipListByFunId(@RequestParam("functionId") Long functionId){
        return applicationInfoService.FindZipListByFunId(functionId);
    }
    @RequiresPermissions("application-function") @RequestMapping(value="create-func-version/{functionId}",method=RequestMethod.GET)
    public String createFuncVersion(@PathVariable("functionId") Long functionId,Model model){
        KnFunctionVersionInfo functionVersion=new KnFunctionVersionInfo();
        functionVersion.setFunctionId(functionId);
        model.addAttribute("functionVersion",functionVersion);
        model.addAttribute("action","create");
        KnResource kr=applicationInfoService.ReadResourceInfo(functionId);
        model.addAttribute("functionName",kr.getName());
        return "application/functionVersionForm";
    }
    @RequiresPermissions("application-function") @RequestMapping(value={"/upload-file"},method={org.springframework.web.bind.annotation.RequestMethod.POST}) @ResponseBody
    public Map upload(@RequestParam("files") MultipartFile file,HttpServletResponse response,HttpServletRequest request) throws IOException{
        response.setContentType("text/html; charset=UTF-8");
        Map<String,String> hm=new HashMap<>();
        //获取时间戳
        String timexf=DateTime.now().toString("yyyyMMddHHmmss");
        String oldFileName=file.getOriginalFilename();
        WebApplicationContext webApplicationContext=ContextLoader.getCurrentWebApplicationContext();
        String fileExt=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1).toLowerCase();//扩展名
        String fileName=Setting.BASEADDRESS+"/"+timexf+"_"+oldFileName;
        File localFile=new File(webApplicationContext.getServletContext().getRealPath(fileName));
        if(!localFile.getParentFile().exists()){
            localFile.getParentFile().mkdirs();
        }
        file.transferTo(localFile);
        hm.put("path",Setting.BASEADDRESS);
        hm.put("ext",fileExt);
        hm.put("url",fileName);
        return hm;
    }
    @RequiresPermissions("application-function") @RequestMapping(value="save-func-version",method=RequestMethod.POST)
    public String saveFunc(@Valid KnFunctionVersionInfo kr,RedirectAttributes redirectAttributes) throws IOException{
        //有zip包
        if(!Strings.isNullOrEmpty(kr.getFuncZipUrl())){
            WebApplicationContext webApplicationContext=ContextLoader.getCurrentWebApplicationContext();
            File localFile=new File(webApplicationContext.getServletContext().getRealPath(kr.getFuncZipUrl()));
            if(localFile.exists()){
                //TODO 先注释掉解压功能包的部分,原先定义是用于PC端直接访问的,后期有需求再打开-cici
                /*String directoryZip=localFile.getParentFile().getAbsolutePath();
                String functionFile="function";
                //将zip包中的文件解析,查看是否有index.jsp文件,解压存放到zipIndexUrl属性中
                String tempName=localFile.getName();
                String tempFileName=tempName.substring(0,tempName.lastIndexOf("."));
                String savePath=directoryZip+"/"+functionFile+"/funUnzip"+"/"+tempFileName;
                if(UnzipFile.unZipFile(localFile,savePath)){
                    kr.setUnZipUrl(Setting.BASEADDRESS+"/"+functionFile+"/funUnzip"+"/"+tempFileName);
                }*/
                kr.setZipSize(localFile.length()+"");
            }else{
                //文件不存在,设置功能包地址为空字符串
                kr.setFuncZipUrl("");
            }
        }
        KnFunctionVersionInfo role=applicationInfoService.SaveFuncVersion(kr);
        if(role==null){
            redirectAttributes.addFlashAttribute("stat","false");
            redirectAttributes.addFlashAttribute("message","保存失败");
        }else{
            redirectAttributes.addFlashAttribute("stat","true");
            redirectAttributes.addFlashAttribute("message","保存成功");
        }
        return "redirect:/application/function";
    }
    @RequiresPermissions("application-function") @RequestMapping(value="delete-func-version",method=RequestMethod.POST) @ResponseBody
    public String deleteFuncVersion(@RequestParam("id") Long id){
        applicationInfoService.DeleteFuncVersion(id);
        return "true";
    }
    @RequiresPermissions("application-function") @RequestMapping(value="find-func-version-by-id",method=RequestMethod.POST) @ResponseBody
    public KnFunctionVersionInfo findFuncVersionById(@RequestParam("id") Long id){
        return applicationInfoService.FindFuncVersionById(id);
    }
    @RequiresPermissions("application-function") @RequestMapping(value="update-func-version-status",method=RequestMethod.POST) @ResponseBody
    public Map updateFuncVersionStatus(@RequestParam("workStatus") Setting.WorkStatusType workStatus,@RequestParam("vid") Long vid){
        KnFunctionVersionInfo info=applicationInfoService.UpdateFuncVersionStatus(workStatus,vid);
        Map map=new HashMap();
        if(info==null){
            map.put("stat",false);
            map.put("info","更新失败");
        }else{
            map.put("stat",true);
            map.put("info","更新成功");
            map.put("name",info.getWorkStatus().getTypeName());
        }
        return map;
    }
}
