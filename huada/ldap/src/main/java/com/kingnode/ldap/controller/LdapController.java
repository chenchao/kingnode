package com.kingnode.ldap.controller;
import com.kingnode.ldap.service.LdapService;
import com.kingnode.xsimple.api.common.AjaxStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller @RequestMapping(value="/system/ldap")
public class LdapController{
    @Autowired
    private LdapService ls;
    @RequestMapping(method=RequestMethod.GET) @ResponseBody
    public AjaxStatus sync(){
        ls.syncEmployee();
        return new AjaxStatus(true);
    }
}
