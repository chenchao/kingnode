package com.kingnode.eka.dao;
import com.kingnode.eka.entity.KnEka;
import com.kingnode.eka.entity.KnEkaCategories;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public interface KnEkaCategoriesDao extends PagingAndSortingRepository<KnEkaCategories,Long>, JpaSpecificationExecutor<KnEkaCategories>{
    KnEkaCategories findByName(String name);
}