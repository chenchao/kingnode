package com.kingnode.third.controller;
import java.io.IOException;
import java.util.Map;

import com.kingnode.third.service.ImportThirdExcelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * 导入设置管理
 * wangyifan
 */
@Controller @RequestMapping(value="/import-third")
public class ImportThirdExcelController{
    private static Logger logger=LoggerFactory.getLogger(ImportThirdExcelController.class);
    @Autowired
    private ImportThirdExcelService importExcelService;
    /**
     * /**
     * 保存导入职责信息
     *
     * @throws java.io.IOException
     */
    @RequestMapping(value={"/save-respon-list"}, method={RequestMethod.POST}) @ResponseBody
    public Map<String,Boolean> saveResponList(@Param("responsibilityId") String[] responsibilityId,@Param("responsibilityName") String[] responsibilityName,@Param("fromSystem") String[] fromSystem) throws IOException{
        return importExcelService.SaveResonList(responsibilityId,responsibilityName,fromSystem);
    }
}
