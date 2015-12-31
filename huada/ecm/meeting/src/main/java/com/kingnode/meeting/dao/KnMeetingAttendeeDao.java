package com.kingnode.meeting.dao;
import java.util.List;

import com.kingnode.meeting.entity.KnMeetingAttendee;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnMeetingAttendeeDao extends PagingAndSortingRepository<KnMeetingAttendee,Long>, JpaSpecificationExecutor<KnMeetingAttendee>{
    KnMeetingAttendee findByAttendeeIdAndKmId(Long attendeeId,Long id);
    List<KnMeetingAttendee> findByKmId(Long id);

    @Modifying
    @Query("delete from KnMeetingAttendee u where u.km.id=:id")
    void DeleteAttendeeByMeetingId(@Param(value="id")Long id);
}
