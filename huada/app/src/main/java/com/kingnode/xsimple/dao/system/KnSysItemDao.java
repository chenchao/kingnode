package com.kingnode.xsimple.dao.system;
import java.util.List;
import javax.persistence.QueryHint;

import com.kingnode.xsimple.entity.system.KnSysItem;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public interface KnSysItemDao extends PagingAndSortingRepository<KnSysItem,Long>, JpaSpecificationExecutor<KnSysItem>{
    @QueryHints({@QueryHint(name="org.hibernate.cacheable", value="true")}) List<KnSysItem> findByItemId(String itemId);
    @QueryHints({@QueryHint(name="org.hibernate.cacheable", value="true")}) KnSysItem findByObjId(String objId);
    @QueryHints({@QueryHint(name="org.hibernate.cacheable", value="true")}) KnSysItem findByItemIdAndObjId(String itemId,String objId);
    @QueryHints({@QueryHint(name="org.hibernate.cacheable", value="true")}) KnSysItem findOne(Long id);
    @QueryHints({@QueryHint(name="org.hibernate.cacheable", value="true")}) List<KnSysItem> findAll(Specification<KnSysItem> spec);
}
