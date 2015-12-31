package com.kingnode.regulation.dao;
import com.kingnode.regulation.entity.KnClassification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * Created by xushuangyong on 14-9-27.
 */
public interface KnClassificationDao extends PagingAndSortingRepository<KnClassification,Long>, JpaSpecificationExecutor<KnClassification>{
    KnClassification findByClassification(String classification);
}
