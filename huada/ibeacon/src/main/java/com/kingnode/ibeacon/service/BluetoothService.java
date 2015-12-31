package com.kingnode.ibeacon.service;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.common.base.Strings;
import com.kingnode.ibeacon.dao.KnBluetoothDeviceDao;
import com.kingnode.ibeacon.entity.KnBluetoothChannel;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * 蓝牙设备管理类
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Component @Transactional(readOnly=true)
public class BluetoothService{
    private KnBluetoothDeviceDao btdDao;
    @Autowired
    public void setBtdDao(KnBluetoothDeviceDao btdDao){
        this.btdDao=btdDao;
    }
    /**
     * @param channelId     设备号  UUID 36位
     * @param major         主id
     * @param minor         辅id
     * @param channelName   设备名
     * @param channelType   设备类型   电池 ,交流电 与 ENUM枚举  ChannelType  对应
     * @param channelStatus 状态   可用, 禁用  与 ENUM枚举 ChannelStatusType 对应
     * @param channelInfo   设备信息  中文描述
     * @param dt
     *
     * @return
     */
    public DataTable<KnBluetoothChannel> PageKnBluetoothChannel(final String channelId,final String major,final String minor,final String channelName,final String channelType,final String channelStatus,final String channelInfo,DataTable<KnBluetoothChannel> dt){
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        String[] column=new String[]{"id","channelId","major","minor","channelName","channelType","channelStatus","channelInfo","updateTime"};
        PageRequest pageable=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),new Sort(d,column[Integer.parseInt(dt.getiSortCol_0())]));
        Page<KnBluetoothChannel> page=btdDao.findAll(new Specification<KnBluetoothChannel>(){
            @Override
            public Predicate toPredicate(Root<KnBluetoothChannel> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(!Strings.isNullOrEmpty(channelId)){
                    expressions.add(cb.like(root.<String>get("channelId"),"%"+channelId+"%"));
                }
                if(!Strings.isNullOrEmpty(major)){
                    expressions.add(cb.equal(root.<String>get("major"),major));
                }
                if(!Strings.isNullOrEmpty(minor)){
                    expressions.add(cb.equal(root.<String>get("minor"),minor));
                }
                if(!Strings.isNullOrEmpty(channelName)){
                    expressions.add(cb.like(root.<String>get("channelName"),"%"+channelName+"%"));
                }
                if(!Strings.isNullOrEmpty(channelType)){
                    expressions.add(cb.equal(root.<Setting.ChannelType>get("channelType"),Setting.ChannelType.valueOf(channelType)));
                }
                if(!Strings.isNullOrEmpty(channelStatus)){
                    expressions.add(cb.equal(root.<Setting.ChannelStatusType>get("channelStatus"),Setting.ChannelStatusType.valueOf(channelStatus)));
                }
                if(!Strings.isNullOrEmpty(channelInfo)){
                    expressions.add(cb.like(root.<String>get("channelInfo"),"%"+channelInfo+"%"));
                }
                return predicate;
            }
        },pageable);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    @Transactional(readOnly=false)
    public String SaveKnBluetoothChannel(KnBluetoothChannel ka){
        List<KnBluetoothChannel> list=btdDao.findBluetoothByMajor(ka.getChannelId(),ka.getMajor(),ka.getMinor());
        if(list.isEmpty()){
            btdDao.save(ka);
            return "保存成功";
        }else{
            return "蓝牙设备已存在,设备号、主id、辅id三信息维一标识一个设备";
        }
    }
    @Transactional(readOnly=false)
    public void DeleteKnBluetoothChannel(List<Long> ids){
        for(Long id : ids){
            btdDao.delete(id);
        }
    }
    public KnBluetoothChannel ReadBluetoothInfo(Long id){
        return btdDao.findOne(id);
    }
    public KnBluetoothChannel findById(Map<String,String> jsonMap){
        KnBluetoothChannel bluetoothChannel=null;
        String channelid=jsonMap.containsKey("channelid")?jsonMap.get("channelid"):"";
        String major=jsonMap.containsKey("major")?jsonMap.get("major"):"";
        String minor=jsonMap.containsKey("minor")?jsonMap.get("minor"):"";
        List<KnBluetoothChannel> list=btdDao.findBluetoothByMajor(channelid,major,minor);
        if(!Utils.isEmpityCollection(list)){
            bluetoothChannel=list.get(0);
        }
        return bluetoothChannel;
    }
}
