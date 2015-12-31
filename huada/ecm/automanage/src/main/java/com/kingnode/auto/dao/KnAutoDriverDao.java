package com.kingnode.auto.dao;
import com.kingnode.auto.entity.KnAutoDriver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnAutoDriverDao extends PagingAndSortingRepository<KnAutoDriver,Long>, JpaSpecificationExecutor<KnAutoDriver>{
    /**
     * 对员工进行分页查询
     *
     * @param keyword
     * @param page
     *
     * @return
     */
    @Query(value="select driver  from KnAutoDriver driver  where  driver.name like :keyword") Page<KnAutoDriver> queryDriver(@Param("keyword") String keyword,Pageable page);
}
