package com.kingnode.xsimple.controller.application;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;

import com.google.common.base.Strings;
import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.entity.application.KnApplicationInfo;
import com.kingnode.xsimple.entity.application.KnVersionInfo;
import com.kingnode.xsimple.service.application.VersionInfoService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
/**
 * 应用版本管理
 * @author  wangyifan
 */
@Controller @RequestMapping(value="/application/version")
public class VersionInfoController{
    private static Logger logger=LoggerFactory.getLogger(VersionInfoController.class);
    @Autowired
    private VersionInfoService versionInfoService;
    /**
     * @param model 设置返回信息
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("application-version") @RequestMapping(method=RequestMethod.GET)
    public String home(Model model){
        List<KnApplicationInfo> listApp=versionInfoService.FindByTitleLike("%%");
        model.addAttribute("listApp",listApp);
        return "/application/versionList";
    }
    /**
     * 跳转新增应用版本页面
     *
     * @param model 设置返回信息
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("application-version") @RequestMapping(value="create",method=RequestMethod.GET)
    public String create(Model model){
        KnVersionInfo knVersionInfo=new KnVersionInfo();
        knVersionInfo.setImgAddress("/assets/global/img/default.jpg");
        model.addAttribute("knVersion",knVersionInfo);
        model.addAttribute("action","create");
        List<KnApplicationInfo> listApp=versionInfoService.FindByTitleLike("%%");
        model.addAttribute("listApp",listApp);
        return "/application/versionForm";
    }
    /**
     * 应用版本管理列表首页
     *
     * @param appId   应用ID
     * @param dt      页面列表信息
     * @param request 获取页面请求参数
     *
     * @return 表格信息
     */
    @RequiresPermissions("application-version") @RequestMapping(value="version-list",method=RequestMethod.POST) @ResponseBody
    public DataTable<KnVersionInfo> versionList(@RequestParam(value="appId",required=false) Long appId,DataTable<KnVersionInfo> dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        return versionInfoService.ListOfKnVerList(searchParams,dt,appId);
    }
    /**
     * 新增应用版本
     *
     * @param /file                上传文件安装包信息
     * @param imageFile            上传桌面图标信息
     * @param knVersionInfo        应用版本实体信息
     * @param optionsRadios        验证方式
     * @param //redirectAttributes 设置返回参数
     *
     * @return
     *
     * @throws IOException
     */
    @RequiresPermissions("application-version") @RequestMapping(value={"/create"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String save(@RequestParam("addressName") MultipartFile file,@RequestParam("imageFile") MultipartFile imageFile,KnVersionInfo knVersionInfo,RedirectAttributes redirectAttributes,String optionsRadios) throws IOException{
        try{
            Long appId=knVersionInfo.getApplicationInfo().getId();
            if(Strings.isNullOrEmpty(appId+"")){
                redirectAttributes.addFlashAttribute("message","应用不存在,新增失败");
                redirectAttributes.addFlashAttribute("stat",false);
                return "redirect:/application/version";
            }
            KnApplicationInfo knApplicationInfo=versionInfoService.FindKnAppInfoById(appId);
            if(null==knApplicationInfo){
                redirectAttributes.addFlashAttribute("message","应用不存在,新增失败");
                return "redirect:/application/version";
            }
            Map<String,String> map=versionInfoService.GetUploadInfoByFile(file,knVersionInfo.getType().name(),knApplicationInfo);
            String message=map.containsKey("message")?map.get("message"):"";
            if(!Strings.isNullOrEmpty(message)){//上传版本 出错,直接跳转到页面
                redirectAttributes.addFlashAttribute("message",message);
                redirectAttributes.addFlashAttribute("stat",false);
                return "redirect:/application/version";
            }
            knVersionInfo.setImgAddress(versionInfoService.GetImageAddressByFile(imageFile));
            knVersionInfo.setApplicationInfo(knApplicationInfo);
            boolean bool=versionInfoService.SaveOrUpdateKnVerInfo(knVersionInfo,map);
            if(bool){
                redirectAttributes.addFlashAttribute("stat",true);
                redirectAttributes.addFlashAttribute("message","新增成功");
            }else{
                redirectAttributes.addFlashAttribute("stat",false);
                redirectAttributes.addFlashAttribute("message","新增失败");
            }
            //开启一个线程执行,用来生成jsp以及plist文件同时下发版本更新通知
            final Map<String,String> jspMap=map;
            final KnApplicationInfo finAppInfo=knApplicationInfo;
            final KnVersionInfo finVerInfo=knVersionInfo;
            final String foptionsRadios=optionsRadios;
            Thread t=new Thread(new Runnable(){
                public void run(){
                    //生成plist文件 同时生成 jsp文件
                    try{
                        jspMap.put("optionsRadios",foptionsRadios);
                        versionInfoService.WriteToPlist(finVerInfo,jspMap,finAppInfo);
                        versionInfoService.PushNoticeNewVerInfo(finVerInfo);
                    }catch(Exception e){
                        logger.error("根据模板动态生成plist文件以及jsp文件,同时下发版本更新通知,错误信息如下：{}",e);
                    }
                }
            });
            t.start();
        }catch(Exception e){
            logger.error("更新版本错误信息 {}",e);
        }
        return "redirect:/application/version";
    }
    /**
     * 打开更新应用版本页面
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("application-version") @RequestMapping(value="update/{id}", method=RequestMethod.GET)
    public String update(@PathVariable("id") Long id,Model model){
        List<KnApplicationInfo> listApp=versionInfoService.FindByTitleLike("%%");
        model.addAttribute("listApp",listApp);
        KnVersionInfo knVersionInfo=versionInfoService.FindKnVersionInfoById(id);
        if(Strings.isNullOrEmpty(knVersionInfo.getImgAddress())){
            knVersionInfo.setImgAddress("/assets/global/img/default.jpg");
        }
        model.addAttribute("knVersion",knVersionInfo);//查询应用版本信息
        model.addAttribute("action","update");//跳转编辑的标示
        return "/application/versionForm";
    }
    /**
     * 更新应用版本信息
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("application-version") @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String update(@RequestParam("addressName") MultipartFile file,@RequestParam("imageFile") MultipartFile imageFile,KnVersionInfo knVersionInfo,RedirectAttributes redirectAttributes,String optionsRadios) throws IOException{
        try{
            Long appId=knVersionInfo.getApplicationInfo().getId();
            if(Strings.isNullOrEmpty(appId+"")){
                redirectAttributes.addFlashAttribute("message","应用不存在,更新失败");
                redirectAttributes.addFlashAttribute("stat",false);
                return "redirect:/application/version";
            }
            KnApplicationInfo knApplicationInfo=versionInfoService.FindKnAppInfoById(appId);
            if(null==knApplicationInfo){
                redirectAttributes.addFlashAttribute("message","应用不存在,更新失败");
                redirectAttributes.addFlashAttribute("stat",false);
                return "redirect:/application/version";
            }
            Map<String,String> map=versionInfoService.GetUploadInfoByFile(file,knVersionInfo.getType().name(),knApplicationInfo);
            String message=map.containsKey("message")?map.get("message"):"";
            if(!Strings.isNullOrEmpty(message)){//上传版本 出错,直接跳转到页面
                redirectAttributes.addFlashAttribute("message",message);
                redirectAttributes.addFlashAttribute("stat",false);
                return "redirect:/application/version";
            }
            knVersionInfo.setImgAddress(versionInfoService.GetImageAddressByFile(imageFile));
            knVersionInfo.setApplicationInfo(knApplicationInfo);
            boolean bool=versionInfoService.SaveOrUpdateKnVerInfo(knVersionInfo,map);
            if(bool){
                redirectAttributes.addFlashAttribute("stat",true);
                redirectAttributes.addFlashAttribute("message","更新成功");
            }else{
                redirectAttributes.addFlashAttribute("stat",false);
                redirectAttributes.addFlashAttribute("message","更新失败");
            }
            //开启一个线程执行,用来生成jsp以及plist文件同时下发版本更新通知
            final Map<String,String> jspMap=map;
            final KnApplicationInfo finAppInfo=knApplicationInfo;
            final KnVersionInfo finVerInfo=knVersionInfo;
            final String foptionsRadios=optionsRadios;
            Thread t=new Thread(new Runnable(){
                public void run(){
                    try{
                        jspMap.put("optionsRadios",foptionsRadios);
                        versionInfoService.WriteToPlist(finVerInfo,jspMap,finAppInfo);
                        versionInfoService.PushNoticeNewVerInfo(finVerInfo);
                    }catch(Exception e){
                        logger.error("根据模板动态生成plist文件以及jsp文件,同时下发版本更新通知,错误信息如下：{}",e);
                    }
                }
            });
            t.start();
        }catch(Exception e){
            logger.error("更新版本错误信息 {}",e);
        }
        return "redirect:/application/version";
    }
    /**
     * 单选删除应用版本信息
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("application-version") @RequestMapping(value="delete", method=RequestMethod.POST) @ResponseBody
    public Map<String,Boolean> delete(@RequestParam("id") Long id){
        Map<String,Boolean> map=new HashMap<String,Boolean>();
        try{
            versionInfoService.DeleteKnVersionInfoById(id);
            map.put("stat",true);
        }catch(Exception e){
            logger.error("删除应用版本错误信息：{}",e);
            map.put("stat",false);
        }
        return map;
    }
    /**
     * 全选删除应用版本信息
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("application-version") @RequestMapping(value="delete-all", method=RequestMethod.POST) @ResponseBody
    public Map<String,Boolean> deleteAll(@RequestParam("ids") List<Long> ids){
        Map<String,Boolean> map=new HashMap<String,Boolean>();
        try{
            versionInfoService.DeleteAllKnVserionInfoByIds(ids);
            map.put("stat",true);
        }catch(Exception e){
            map.put("stat",false);
            logger.error("删除应用版本错误信息：{}",e);
        }
        return map;
    }
}
