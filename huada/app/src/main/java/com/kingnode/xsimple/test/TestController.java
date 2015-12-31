package com.kingnode.xsimple.test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 测试页面
 * wangyifan
 */
@Controller @RequestMapping(value="/testInfo")
public class TestController{
    @Autowired
    private static Logger logger=LoggerFactory.getLogger(TestController.class);
    /**
     * @param model 设置返回信息
     *
     * @return 返回跳转链接
     */
    @RequestMapping(method=RequestMethod.GET)
     public String home(Model model){
        return "/test/home";
    }

    @RequestMapping(value="/dto",method=RequestMethod.GET)
    public String testHome(Model model){
        return "/test/dto";
    }

    /**
     * @return 返回跳转链接
     */
    @RequestMapping(value="/test",method=RequestMethod.POST)
    public String test(Model model){
        return "/test/home";
    }
    /**
     * 跳转测试页面
     * @return 返回跳转链接
     */
    @RequestMapping(value="/upload",method=RequestMethod.GET)
    public String toUpload(Model model){
        return "/test/upload";
    }
}
