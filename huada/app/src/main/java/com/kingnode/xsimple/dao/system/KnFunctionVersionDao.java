package com.kingnode.xsimple.dao.system;
import java.util.List;

import com.kingnode.xsimple.entity.web.KnFunctionVersionInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnFunctionVersionDao extends CrudRepository<KnFunctionVersionInfo,Long>{

    @Query("select u from KnFunctionVersionInfo u where functionId = :functionId and workStatus<>'unusable' order by zipVersion desc ")
    List<KnFunctionVersionInfo> findByFunctionId(@Param("functionId") Long functionId);

    @Query("select u from KnFunctionVersionInfo u where functionId in (:functionIds)")
    List<KnFunctionVersionInfo> findByFunctionIds(@Param("functionIds") List<Long> functionIds);
}
