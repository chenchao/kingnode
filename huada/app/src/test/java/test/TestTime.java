package test;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
/**
 * Created with IntelliJ IDEA
 * User: wangyifan
 * Date: 2014/7/28
 */

public class TestTime{
    @Test
    public void tset(){
        List list = new ArrayList<>();
        list.add("ss");
        list.remove(null);
        Long s = 8l;
        System.out.println(s.intValue());
        String morningStart = "17:31";
        System.out.println( morningStart.matches("\\d+:\\d+"));
        String[] timeStr=morningStart.split(":");
        //获得时分的转换的分钟
        System.out.println(Integer.parseInt(timeStr[0]));
        System.out.println(Integer.parseInt(timeStr[1]));
        Integer minutes=(Integer.parseInt(timeStr[0])*60+Integer.parseInt(timeStr[1]))-s.intValue();
        System.out.println(minutes);
        //分钟转换成时间
        int h=0, d=0;
        d=minutes%60;
        System.out.println(d);
        h=minutes/60;
        System.out.println(h);
        System.out.println(h+":"+d);
        Long a = 1329038338000l;//1411114326517
        Date date = new Date(a);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(date));
        System.out.println(System.currentTimeMillis());
        String ios7Address = "https://c.kingnode.com/pool/1411018994526/1411049245577.plist";
        String  ios7FileAddress = "https://c.kingnode.com/pool";
        ios7Address = ios7Address.replaceAll(ios7FileAddress,"");
        System.out.println(ios7Address);
    }
    @Test
    public void properties() throws IOException{
        Resource resource = new ClassPathResource("application.test.properties");
        Properties props = PropertiesLoaderUtils.loadProperties(resource);
        System.out.println(props.getProperty("mailserver.port"));
    }

}
