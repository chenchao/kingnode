package com.kingnode.auto.dao;
import java.util.List;

import com.kingnode.auto.entity.KnAuto;
import com.kingnode.auto.entity.KnTrafficAccident;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * Created by xushuangyong on 14-9-29.
 */
public interface KnTrafficAccidentDao extends PagingAndSortingRepository<KnTrafficAccident,Long>, JpaSpecificationExecutor<KnTrafficAccident>{
    List<KnTrafficAccident> findByKa(KnAuto auto);
}
