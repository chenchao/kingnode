package com.kingnode.auto.dao;
import java.util.List;

import com.kingnode.auto.entity.KnAutoMessage;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * Created by xushuangyong on 14-10-14.
 */
public interface KnAutoMessageDao extends PagingAndSortingRepository<KnAutoMessage,Long>, JpaSpecificationExecutor<KnAutoMessage>{
    List<KnAutoMessage> findByBIdAndType(Long id,KnAutoMessage.Type type);
}
