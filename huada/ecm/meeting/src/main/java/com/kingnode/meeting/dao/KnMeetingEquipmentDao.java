package com.kingnode.meeting.dao;
import java.util.List;

import com.kingnode.meeting.entity.KnMeetingEquipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnMeetingEquipmentDao extends PagingAndSortingRepository<KnMeetingEquipment,Long>, JpaSpecificationExecutor<KnMeetingEquipment>{
    Page<KnMeetingEquipment> findByNameLikeAndStatus(String name,KnMeetingEquipment.EquipmentStatus status,Pageable pageable);
    List<KnMeetingEquipment> findByStatus(KnMeetingEquipment.EquipmentStatus status);
}
