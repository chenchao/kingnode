package com.kingnode.xsimple.controller.safety;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.ServletRequest;

import com.google.common.base.Strings;
import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.entity.push.KnCertificateInfo;
import com.kingnode.xsimple.service.safety.CertificateService;
import com.kingnode.xsimple.util.PathUtil;
import com.kingnode.xsimple.util.Utils;
import com.kingnode.xsimple.util.file.FileUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
 * 证书管理控制器
 *
 * @author dengfeng@kingnode.com (dengfeng)
 */
@Controller @RequestMapping(value="/safety/certificate")
public class CertificateControler{
    @Autowired
    private CertificateService certificateService;
    /**
     * 跳转证书页面
     *
     * @param model
     *
     * @return
     */
    @RequiresPermissions("safety-certificate") @RequestMapping(method=RequestMethod.GET)
    public String list(Model model){
        model.addAttribute("platType",Setting.PlatformType.values());
        model.addAttribute("delStatus",Setting.DeleteStatusType.values());
        model.addAttribute("messType",Setting.MessageType.values()); // 消息类别
        model.addAttribute("msgState",Setting.MsgState.values()); // 消息状态
        model.addAttribute("platformType",Setting.PlateformType.values()); //平台类型
        model.addAttribute("workStatus",Setting.WorkStatusType.values()); //证书状态
        return "safety/certificateList";
    }
    /**
     * 证书列表
     *
     * @param dt
     * @param request
     *
     * @return
     */
    @RequiresPermissions("safety-certificate") @RequestMapping(value="search-list") @ResponseBody
    public DataTable searchList(DataTable<KnCertificateInfo> dt,ServletRequest request){
        Map<String,Object> searchParams=Servlets.getParametersStartingWith(request,"search_");
        String platformType=null;
        String certificatePwd=null;
        String workStatus=null;
        String title=null;
        for(String key : searchParams.keySet()){
            if(key.indexOf("platformType")>0){
                platformType=(String)searchParams.get(key);
            }
            if(key.indexOf("certificatePwd")>0){
                certificatePwd=(String)searchParams.get(key);
            }
            if(key.indexOf("workStatus")>0){
                workStatus=(String)searchParams.get(key);
            }
            if(key.indexOf("title")>0){
                title=(String)searchParams.get(key);
            }
        }
        return certificateService.SearchList(platformType,certificatePwd,workStatus,title,dt);
    }
    /**
     * 删除证书
     *
     * @param ids
     *
     * @return
     */
    @RequiresPermissions("safety-certificate") @RequestMapping(value="delete", method=RequestMethod.POST) @ResponseBody
    public Map delete(@RequestParam("ids") List<Long> ids){
        Map map=new HashMap();
        try{
            certificateService.Delete(ids);
            map.put("stat","true");
        }catch(Exception e){
            map.put("stat","false");
        }
        return map;
    }
    /**
     * 更新证书
     *
     * @param id
     * @param model
     * @param request
     *
     * @return
     */
    @RequiresPermissions("safety-certificate") @RequestMapping(value="update-certificate/{id}", method=RequestMethod.GET)
    public String updateCertificate(@PathVariable("id") Long id,Model model,ServletRequest request){
        KnCertificateInfo knCertificateInfo=certificateService.ReadCertificateInfo(id);
        String cerpath=knCertificateInfo.getCertificatePath();
        String loadpath="";
        if(!Utils.isEmptyString(cerpath)){
            loadpath=cerpath;
            cerpath=cerpath.substring(cerpath.lastIndexOf("/")+1);
        }
        model.addAttribute("cerpath",cerpath);
        model.addAttribute("loadpath",loadpath);
        model.addAttribute("certificate",knCertificateInfo);
        model.addAttribute("list",certificateService.GetApplicationList(null));
        model.addAttribute("workStatus",Setting.WorkStatusType.values());
        model.addAttribute("platformType",Setting.PlateformType.values());
        model.addAttribute("action","update");
        return "safety/certificateForm";
    }
    /**
     * 跳转新增证书页面
     *
     * @param model
     *
     * @return
     */
    @RequiresPermissions("safety-certificate") @RequestMapping(value="add-certificate", method=RequestMethod.GET)
    public String addCertificate(Model model){
        model.addAttribute("list",certificateService.GetApplicationList(null));
        model.addAttribute("workStatus",Setting.WorkStatusType.values());
        model.addAttribute("platformType",Setting.PlateformType.values());
        model.addAttribute("action","add");
        return "safety/certificateForm";
    }
    /**
     * 保存修改证书
     *
     * @param file
     * @param redirectAttributes
     * @param id
     * @param action
     * @param platformType
     * @param certificatePwd
     * @param packageName
     * @param isSupport
     * @param remark
     * @param workStatus
     * @param apid
     *
     * @return
     */
    @RequiresPermissions("safety-certificate") @RequestMapping(value={"/save-edit"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String saveEdit(@RequestParam("uploadFile") MultipartFile file,RedirectAttributes redirectAttributes,String id,String action,String platformType,String certificatePwd,String packageName,String isSupport,String remark,String workStatus,String apid){
        try{
            if(action.equals("add")){
                String certificatePath="";
                if(null!=file&&!file.isEmpty()){
                    certificatePath=uploadFile(file);
                }
                certificateService.Save(platformType,certificatePath,certificatePwd,packageName,isSupport,remark,workStatus,apid);
            }else if(action.equals("update")){
                String certificatePath="";
                KnCertificateInfo knCertificateInfo=certificateService.ReadCertificateInfo(Long.parseLong(id));
                if(null!=file&&!file.isEmpty()){
                    certificatePath=uploadFile(file);
                    if(!Strings.isNullOrEmpty(knCertificateInfo.getCertificatePath())){
                        FileUtil.getInstance().deleteFile(PathUtil.getRootPath()+knCertificateInfo.getCertificatePath());
                    }
                }
                certificateService.Update(knCertificateInfo,platformType,certificatePath,certificatePwd,packageName,isSupport,remark,workStatus,apid);
            }
            redirectAttributes.addFlashAttribute("message","保存修改成功");
            redirectAttributes.addFlashAttribute("messaction","1");
        }catch(Exception e){
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message","保存修改失败");
            redirectAttributes.addFlashAttribute("messaction","0");
        }
        return "redirect:/safety/certificate";
    }
    /**
     * 上传文件
     *
     * @param file
     *
     * @return
     *
     * @throws Exception
     */
    private String uploadFile(MultipartFile file) throws Exception{
        WebApplicationContext webApplicationContext=ContextLoader.getCurrentWebApplicationContext();
        String fileExt=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1).toLowerCase();//扩展名
        String certificatePath=Setting.BASEADDRESS+"/"+UUID.randomUUID().toString()+"."+fileExt;
        File localFile=new File(webApplicationContext.getServletContext().getRealPath(certificatePath));
        if(!localFile.getParentFile().exists()){
            localFile.getParentFile().mkdirs();
        }
        file.transferTo(localFile);
        return certificatePath;
    }
}
