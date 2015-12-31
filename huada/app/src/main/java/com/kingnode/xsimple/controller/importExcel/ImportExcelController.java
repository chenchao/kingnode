package com.kingnode.xsimple.controller.importExcel;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Strings;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.dto.ExcelDTO;
import com.kingnode.xsimple.dto.ImportEmpDTO;
import com.kingnode.xsimple.service.excel.ImportExcelService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
/**
 * 导入设置管理
 * @author wangyifan
 */
@Controller @RequestMapping(value="/import")
public class ImportExcelController{
    private static Logger logger=LoggerFactory.getLogger(ImportExcelController.class);
    @Autowired
    private ImportExcelService importExcelService;
    /**
     * @param model 默认跳转导入用户页面
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("import-user")
    @RequestMapping(method=RequestMethod.GET)
    public String home(Model model){
        return "/importExcel/importUserList";
    }
    /**
     * @param model 跳转导入用户页面
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("import-user")
    @RequestMapping(value="user", method=RequestMethod.GET)
    public String importUserList(Model model){
        return "/importExcel/importUserList";
    }
    /**
     * 跳转导入职责页面
     *
     * @param model 设置返回信息
     *
     * @return 返回跳转链接
     */
    @RequestMapping(value="responsibility", method=RequestMethod.GET)
    public String importResponsibilityList(Model model){
        return "/importExcel/importResponList";
    }
    /**
     * 跳转导入oa用户页面
     *
     * @param model 设置返回信息
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("import-oa-user")
    @RequestMapping(value="oa-user", method=RequestMethod.GET)
    public String importOaUserList(Model model){
        return "/importExcel/importOaUserList";
    }
    /**
     * 跳转导入公司手机页面
     *
     * @param model 设置返回信息
     *
     * @return 返回跳转链接
     */
    @RequiresPermissions("import-company-phone")
    @RequestMapping(value="company-phone", method=RequestMethod.GET)
    public String importPhoneList(Model model){
        return "/importExcel/importCompanyPhoneList";
    }

    @RequiresPermissions("import-org-user")
    @RequestMapping(value="org-user", method=RequestMethod.GET)
    public String importOrgUserList(Model model){
        return "/importExcel/importOrgUserList";
    }

    /**
     * 上传execel文件,并预览信息
     *
     * @param file       execel 文件
     * @param importType 导入类型  如 用户（importType = 1）、 OA用户（importType = 5）、职责（importType = 2）、公司手机（importType = 4）
     *
     * @throws IOException
     */
    @RequestMapping(value={"/upload-excel"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}) @ResponseBody
    public Map<String,Object> upload(@RequestParam("uploadFile") MultipartFile file,HttpServletResponse response,HttpServletRequest request,String importType) throws IOException{
        response.setContentType("text/html; charset=UTF-8");
        WebApplicationContext webApplicationContext=ContextLoader.getCurrentWebApplicationContext();
        String fileExt=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1).toLowerCase();//扩展名
        String fileName=Setting.BASEADDRESS+"/"+UUID.randomUUID().toString()+"."+fileExt;
        File localFile=new File(webApplicationContext.getServletContext().getRealPath(fileName));
        if(!localFile.getParentFile().exists()){
            localFile.getParentFile().mkdirs();
        }
        file.transferTo(localFile);
        return importExcelService.GetObjInfOfExcel(localFile,fileExt,importType);
    }
    /**
     * @param importType 下载 导入模板的类型  如 用户（importType = 1）、 OA用户（importType = 5）、职责（importType = 2）、公司手机（importType = 4）
     *
     * @throws IOException
     */
    @RequestMapping(value="/down-load-excel", method=RequestMethod.GET)
    public void downLoadExcel(HttpServletResponse response,String importType) throws IOException{
        importExcelService.DownLoadExcel(importType,response);
    }
    /**
     * 保存导入用户信息
     *
     * @throws IOException
     */
    @RequestMapping(value={"/save-user-list"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}) @ResponseBody
    public Map<String,Boolean> saveUserList(@Param("name") String[] name,@Param("loginName") String[] loginName,@Param("password") String[] password,@Param("userId") String[] userId,@Param("userType") String[] userType,@Param("fromSystem") String[] fromSystem,@Param("roleName") String[] roleName,@Param("email") String[] email) throws IOException{
        return importExcelService.SaveUserList(name,loginName,userId,userType,fromSystem,roleName,email,password);
    }

    @RequestMapping(value={"/save-org-list"},method={org.springframework.web.bind.annotation.RequestMethod.POST}) @ResponseBody
    public Map<String,Boolean> saveOrgList(@Param("ids") String[] ids,@Param("names") String[] names,@Param("types") String[] types,@Param("parentIds") String[] parentIds){
        return importExcelService.SaveOrgList(ids,names,types,parentIds);
    }

    @RequestMapping(value={"/upload-save-excel"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}) @ResponseBody
    public Map<String,Object> uploadsave(@RequestParam("uploadFile") MultipartFile file,HttpServletResponse response,HttpServletRequest request,@RequestParam("importType") String importType) throws IOException{
        Map rtnMap=new HashMap();
        try{
            response.setContentType("text/html; charset=UTF-8");
            WebApplicationContext webApplicationContext=ContextLoader.getCurrentWebApplicationContext();
            String fileExt=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1).toLowerCase();//扩展名
            String fileName=Setting.BASEADDRESS+"/"+UUID.randomUUID().toString()+"."+fileExt;
            File localFile=new File(webApplicationContext.getServletContext().getRealPath(fileName));
            if(!localFile.getParentFile().exists()){
                localFile.getParentFile().mkdirs();
            }
            file.transferTo(localFile);
            //解析数据
            Map<String,Object> map=importExcelService.GetObjInfOfExcel(localFile,fileExt,importType);
            if(Strings.isNullOrEmpty(map.get("errorBack").toString())){
                //保存导入的数据
                List<ExcelDTO> orgList=(List<ExcelDTO>)map.get("objList");
                List<ImportEmpDTO> empList=(List<ImportEmpDTO>)map.get("objTList");
                Map<Long,String> orgIdMap=importExcelService.SaveOrgList(orgList);
                rtnMap=importExcelService.SaveEmpList(empList,orgIdMap);
            }else{
                rtnMap.put("stat",true);
                rtnMap.put("msg",map.get("errorBack"));
            }
        }catch(Exception ex){
            logger.error("导入失败:{}",ex);
            rtnMap.put("stat",false);
            rtnMap.put("msg","导入失败！");
        }
        return rtnMap;
    }
}
