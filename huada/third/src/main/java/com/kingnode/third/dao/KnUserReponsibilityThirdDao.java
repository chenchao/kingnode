package com.kingnode.third.dao;
import com.kingnode.third.entity.KnUserReponsibilityThird;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnUserReponsibilityThirdDao extends CrudRepository<KnUserReponsibilityThird,Long>{

    @Modifying
    @Query("delete from KnUserThirdInfo where fromSys=:fromSys")
    int deleteResponsibility(@Param("fromSys") String fromSys);
}
