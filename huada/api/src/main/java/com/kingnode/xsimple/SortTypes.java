package com.kingnode.xsimple;
import java.util.Map;

import com.google.common.collect.Maps;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class SortTypes{
    private static Map<String,String> sortTypes=Maps.newLinkedHashMap();
    static{
        sortTypes.put("auto","自动");
        sortTypes.put("title","名称");
    }
    public static Map<String,String> Get(){
        return sortTypes;
    }
}
