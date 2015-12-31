package com.kingnode.xsimple.shiro;
import java.io.IOException;

import com.kingnode.xsimple.shiro.tag.ShiroTags;
import freemarker.template.TemplateException;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
/**
 * @author caichune@kingnode.com (cici)
 */
public class ShiroTagFreeMarkerConfigurer extends FreeMarkerConfigurer{
    @Override
    public void afterPropertiesSet() throws IOException, TemplateException{
        super.afterPropertiesSet();
        this.getConfiguration().setSharedVariable("shiro", new ShiroTags());
    }
}
