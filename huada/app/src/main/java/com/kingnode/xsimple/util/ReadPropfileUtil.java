package com.kingnode.xsimple.util;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
/**
 * @author dengfeng@kingnode.com (dengfeng)
 */
public class ReadPropfileUtil{

    private static ReadPropfileUtil install = null ;
    public static Properties prop =null;

    private ReadPropfileUtil(){

    }

    static{
        try{
           prop=PropertiesLoaderUtils.loadProperties(new ClassPathResource("clientInfo.properties"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static ReadPropfileUtil getInstall(){
        if( null==install){
            install = new ReadPropfileUtil();
        }
        return install ;
    }
}
