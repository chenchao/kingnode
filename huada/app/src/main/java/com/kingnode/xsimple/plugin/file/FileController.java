package com.kingnode.xsimple.plugin.file;
import com.kingnode.xsimple.entity.system.KnPluginConfig;
import com.kingnode.xsimple.service.PluginConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Controller @RequestMapping({"/storage/file"})
public class FileController{
    @Autowired
    private FilePlugin fp;
    @Autowired
    private PluginConfigService pcs;
    @RequestMapping(value={"/setting"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String setting(ModelMap model){
        KnPluginConfig localPluginConfig=fp.getPluginConfig();
        model.addAttribute("pluginConfig",localPluginConfig);
        return "/storage/file";
    }
    @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String update(Integer seq){
        KnPluginConfig localPluginConfig=fp.getPluginConfig();
        localPluginConfig.setIsEnabled(true);
        localPluginConfig.setSeq(seq);
        pcs.SaveKnPluginConfig(localPluginConfig);
        return "redirect:/storage/list";
    }
}