package com.kingnode.affairs.dao;
import java.util.List;

import com.kingnode.affairs.entity.KnAffairsPerson;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnAffairsPersonDao extends PagingAndSortingRepository<KnAffairsPerson,Long>, JpaSpecificationExecutor<KnAffairsPerson>{
    List<KnAffairsPerson> findByDepartmentName(String departmentName);
}
