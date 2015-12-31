package com.kingnode.ibeacon.rest;
import java.util.Map;

import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.ibeacon.entity.KnBluetoothChannel;
import com.kingnode.ibeacon.service.BluetoothService;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.rest.DetailDTO;
import com.kingnode.xsimple.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequestMapping({"/api/v1/ibeacon"})
public class BluetoothRestController{
    private static Logger logger=LoggerFactory.getLogger(BluetoothRestController.class);
    @Autowired
    private BluetoothService bs;
    /**
     * 根据设备号、主ID、辅ID获得一条蓝牙信息
     *
     * @param jsonparm 手机端传入参数
     *                 参数含义如下： {"channelid":"设备号","major":"主ID" ,"minor":"辅ID"}
     *                 例如： {"channelid":"b279ab239d79442b86d88734d62733aa","major":"9999999" ,"minor":"000000"}
     */
    @RequestMapping(value="/find/{jsonparm}",method={RequestMethod.GET})
    public DetailDTO<KnBluetoothChannel> findById(@PathVariable("jsonparm")String jsonparm){
        logger.info("根据设备号、主ID、辅ID获得一条蓝牙信息,接受参数如下：---->jsonparm:"+jsonparm);
        DetailDTO<KnBluetoothChannel> dd=new DetailDTO<>(false);
        dd.setErrorCode(Setting.FAIURESTAT);
        try{
            Map<String,String> jsonMap=JsonMapper.nonEmptyMapper().fromJson(jsonparm,Map.class);
            if(null!=jsonMap&&jsonMap.size()>0){
                KnBluetoothChannel bluetoothChannel=bs.findById(jsonMap);
                if(Utils.isNotNull(bluetoothChannel)){
                    dd.setStatus(true);
                    dd.setDetail(bluetoothChannel);
                    dd.setErrorCode(Setting.SUCCESSSTAT);
                }else{
                    dd.setErrorMessage("查询不到记录");
                }
            }else{
                dd.setErrorMessage("参数格式错误");
            }
        }catch(Exception e){
            dd.setErrorMessage("后台异常,稍后尝试");
        }
        return dd;
    }
}
