package com.kingnode.xsimple.util;
import com.kingnode.xsimple.ShiroUser;
import org.apache.shiro.SecurityUtils;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class Users{
    /**
     * 取出Shiro中的当前用户LoginName.
     */
    public static String name(){
        return ShiroUser().getName();
    }
    public static Long id(){
        return ShiroUser().getId();
    }
    public static ShiroUser ShiroUser(){
        ShiroUser user=(ShiroUser)SecurityUtils.getSubject().getPrincipal();
        return user;
    }
}