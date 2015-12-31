package com.kingnode.xsimple.controller.platform;

import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;

import com.google.common.collect.Lists;
import com.kingnode.diva.web.Servlets;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.entity.system.KnCustomServiceInfo;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnSystemPropertieInfo;
import com.kingnode.xsimple.service.system.CustomService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 王伟强
 */
@Controller
@RequestMapping(value = "platform")
public class CustomServiceController {
    @Autowired
    private CustomService cs;

    /**
     * 跳转客服管理页面
     */
    @RequiresPermissions("platform-service")
    @RequestMapping(value = "service")
    public String home() {
        return "/platform/customServiceList";
    }

    /**
     * 客服列表
     */
    @RequiresPermissions("platform-service")
    @RequestMapping(value = "custom/list")
    @ResponseBody
    public DataTable<KnCustomServiceInfo> list(ServletRequest request, DataTable<KnCustomServiceInfo> dt) {
        Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
        dt = cs.PageCustomServiceInfos(searchParams, dt);
        return dt;
    }

    /**
     * 查询客服员工
     */
    @RequiresPermissions("platform-service")
    @RequestMapping(value = "custom/users")
    @ResponseBody
    public DataTable<KnEmployee> users(DataTable<KnEmployee> dt, ServletRequest request) {
        Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
        dt = cs.FindAllUsers(dt, searchParams);
        return dt;
    }

    /**
     * 删除客服人员列表
     */
    @RequiresPermissions("platform-service")
    @RequestMapping(value = "custom/delete")
    @ResponseBody
    public boolean delete(@RequestParam(value = "id") Long id) {
        cs.Delete(id);
        return true;
    }

    /**
     * 批量删除客服人员
     */
    @RequiresPermissions("platform-service")
    @RequestMapping(value = "custom/delete-all")
    @ResponseBody
    public boolean deleteAll(@RequestParam(value = "ids") Long[] ids) {
        cs.DeleteAll(ids);
        return true;
    }

    /**
     * 保存客服人员信息
     */
    @RequiresPermissions("platform-service")
    @RequestMapping(value = "custom/save-custom-service")
    @ResponseBody
    public boolean saveCustomService(@RequestParam(value = "ids") Long[] ids) {
        List<KnEmployee> emps = cs.FindByIds(ids);
        List<KnCustomServiceInfo> lsEntity = Lists.newArrayList();
        for (KnEmployee emp : emps) {
            List<KnCustomServiceInfo> customLs = cs.FindKnCustomServiceInfo(String.valueOf(emp.getId()), null);
            if (customLs == null || customLs.isEmpty()) {
                KnCustomServiceInfo kncs = new KnCustomServiceInfo();
                kncs.setFullName(emp.getUserName());
                kncs.setUserId(emp.getUserId());
                kncs.setFromSys(emp.getUserSystem());
                kncs.setUserType(emp.getUserType());
                lsEntity.add(kncs);
            }
        }
        cs.SaveCustomServiceInfoList(lsEntity);
        return true;
    }

    /**
     * 跳转显示系统信息
     */
    @RequiresPermissions("platform-service")
    @RequestMapping(value = "custom/system")
    @ResponseBody
    public KnSystemPropertieInfo system(KnSystemPropertieInfo sp) {
        List<KnSystemPropertieInfo> ls = cs.ReadSystemPropertieInfo();
        if (null != ls && !ls.isEmpty()) {
            sp = ls.get(0);
        }
        return sp;
    }

    /**
     * 保存系统属性信息
     */
    @RequiresPermissions("platform-service")
    @RequestMapping(value = "custom/save-system")
    @ResponseBody
    public boolean saveSystem(@RequestParam(value = "ideaId") Long ideaId, @RequestParam(value = "email") String email, @RequestParam(value = "telnum") String telnum) {
        KnSystemPropertieInfo sp = null;
        if (ideaId != null) {
            sp = cs.FindOneSystemPropertieInfo(ideaId);
        }
        if (sp == null) {
            sp = new KnSystemPropertieInfo();
        }
        sp.setEmail(email);
        sp.setTelNum(telnum);
        cs.SaveSystemPropertieInfo(sp);
        return true;
    }
}