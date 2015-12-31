package com.kingnode.approval.common.util;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kingnode.approval.dto.DynamicDTO;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class GroupDtoUtil{
    /**
     * 根据DynamicDTO类型createDate属性分组
     */
    public static Map groupDynamicByDate(List<DynamicDTO> target){
        Map<String,List<DynamicDTO>> gpsMap=Maps.newHashMap();
        for(DynamicDTO dto : target){
            String key=dto.getCreateDate().substring(0,10);
            List<DynamicDTO> gpsList=gpsMap.get(key);
            if(gpsList==null){
                gpsList=Lists.newArrayList();
                gpsMap.put(key,gpsList);
            }
            gpsList.add(dto);
        }
        return gpsMap;
    }
}
