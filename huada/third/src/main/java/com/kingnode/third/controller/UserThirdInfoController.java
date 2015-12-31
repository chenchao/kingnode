package com.kingnode.third.controller;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;

import com.kingnode.diva.web.Servlets;
import com.kingnode.third.entity.KnUserThirdInfo;
import com.kingnode.third.service.UserThirdInfoService;
import com.kingnode.xsimple.api.common.DataTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * @author kongjiangwei
 */
@Controller
@RequestMapping(value = "/authority/user-third")
public class UserThirdInfoController {

    @Autowired
    private UserThirdInfoService userThirdInfoService;

    @RequestMapping(method = RequestMethod.GET)
    public String home(Model model) {
        return "third/userThirdList";
    }

    @RequestMapping(value = "user-third-list", method = RequestMethod.POST)
    @ResponseBody
    public DataTable<KnUserThirdInfo> userThirdList(DataTable<KnUserThirdInfo> dt, ServletRequest request) {
        Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
        return userThirdInfoService.UserThirdList(dt, searchParams);
    }

    @RequestMapping(value = "delete-user-third", method = RequestMethod.POST)
    @ResponseBody
    public String deleteUserThird(@RequestParam("id") Long id) {
        userThirdInfoService.DeleteUserThird(id);
        return "true";
    }

    @RequestMapping(value = "delete-all-user-third", method = RequestMethod.POST)
    @ResponseBody
    public String deleteAllUserThird(@RequestParam("ids") List<Long> ids) {
        userThirdInfoService.DeleteAllUserThird(ids);
        return "true";
    }

    @RequestMapping(value = "contact-user", method = RequestMethod.POST)
    @ResponseBody
    public Map contactUser(@RequestParam("ids") List<Long> ids) {
        return userThirdInfoService.ContactUser(ids);
    }

    @RequestMapping(value = "cancel-contact-user", method = RequestMethod.POST)
    @ResponseBody
    public Map cancelContactUser(@RequestParam("id") Long id) {
        return userThirdInfoService.CancleContactUser(id);
    }

    @RequestMapping(value = "remove-contactuser", method = RequestMethod.POST)
    @ResponseBody
    public Map removeContactUser(@RequestParam("id") Long id) {
        return userThirdInfoService.RemoveContactUser(id);
    }


    @RequestMapping(value = "get-user-info", method = RequestMethod.POST)
    @ResponseBody
    public Map getUserInfo() {
        return userThirdInfoService.GetUserInfo();
    }

    @RequestMapping(value = "get-responsibility-third", method = RequestMethod.POST)
    @ResponseBody
    public Map getResponsibilityThird() {
        return userThirdInfoService.GetResponsibilityThird();
    }

    @RequestMapping(value = "get-user-responsibility-by-local", method = RequestMethod.POST)
    @ResponseBody
    public Map getUserResponsibilityByLocal() {
        return userThirdInfoService.GetUserResponsibilityByLocal();
    }

    @RequestMapping(value="add-to-user-info",method=RequestMethod.POST) @ResponseBody
    public Map addToUserInfo(@RequestParam("ids") List<Long> ids){
        return  userThirdInfoService.AddToUserInfo(ids);
    }

}
