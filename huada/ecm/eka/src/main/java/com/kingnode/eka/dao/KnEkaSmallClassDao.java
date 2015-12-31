package com.kingnode.eka.dao;
import com.kingnode.eka.entity.KnEkaCategories;
import com.kingnode.eka.entity.KnEkaSmallClass;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public interface KnEkaSmallClassDao extends PagingAndSortingRepository<KnEkaSmallClass,Long>, JpaSpecificationExecutor<KnEkaSmallClass>{
    KnEkaSmallClass findByName(String name);
}