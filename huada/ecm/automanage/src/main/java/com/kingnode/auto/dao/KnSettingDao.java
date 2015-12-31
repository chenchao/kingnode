package com.kingnode.auto.dao;
import com.kingnode.auto.entity.KnSetting;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * Created by xushuangyong on 14-9-18.
 */
public interface KnSettingDao extends PagingAndSortingRepository<KnSetting,Long>, JpaSpecificationExecutor<KnSetting>{
}
