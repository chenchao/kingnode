package com.kingnode.xsimple.controller.cloud;
import java.util.List;
import java.util.Map;

import com.kingnode.xsimple.service.application.ApplicationInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * 跟云端数据有交互的应用Controller;
 * 主要有同步到云端的应用信息,
 * 从云端将数据同步到本地的信息
 */
@Controller @RequestMapping(value="/cloud/application")
public class ApplicationCloudController{
    @Autowired
    private ApplicationInfoService applicationInfoService;
    /**
     * 将本地数据同步到云端
     *
     * @param ids 应用id集合
     *
     * @return 返回是否更新成功标示
     */
    @RequestMapping(value="syn-to-cloud", method=RequestMethod.POST) @ResponseBody
    public String synToCloud(@RequestParam(value="ids", required=false) List<Long> ids,@RequestParam(value="tip", required=false) String tip){
        return applicationInfoService.SynToCloud(ids,tip);
    }
    /**
     * 将云端的数据同步到本地
     * @return 返回前台是否同步成功标示
     */
    @RequestMapping(value="syn-from-cloud", method=RequestMethod.POST) @ResponseBody
    public Map<String,String> synFromCloud(@RequestParam(value="tip", required=false) String tip){
        return applicationInfoService.SynFromCloud(tip);
    }
}
