package com.kingnode.ibeacon.dao;
import java.util.List;

import com.kingnode.ibeacon.entity.KnBluetoothChannel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnBluetoothDeviceDao extends PagingAndSortingRepository<KnBluetoothChannel,Long>, JpaSpecificationExecutor<KnBluetoothChannel>{
    /**
     * 获取该应用下,版本的状态以及型号对应的所有的设备信息
     *
     * @param channelId 设备号  UUID 36位
     * @param major     主id
     * @param minor     辅id
     *
     * @return List<KnBluetoothChannel> 返回符合条件的设备集合列表
     */
    @Query("select b from KnBluetoothChannel b where b.channelId=:channelId and b.major=:major and b.minor=:minor")
    List<KnBluetoothChannel> findBluetoothByMajor(@Param("channelId") String channelId,@Param("major") String major,@Param("minor") String minor);
}