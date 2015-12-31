package com.kingnode.meeting.dao;
import java.util.List;

import com.kingnode.meeting.entity.KnMeetingRoom;
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
public interface KnMeetingRoomDao extends PagingAndSortingRepository<KnMeetingRoom,Long>, JpaSpecificationExecutor<KnMeetingRoom>{
    @Query("select count(u) from KnMeeting u where u.beginDate<:currDate and u.endDate>:currDate and u.kmr.id=:id")
    Integer findKnMeetingCount(@Param(value="currDate") Long currDate,@Param(value="id") Long id);
    List<KnMeetingRoom> findByStatus(KnMeetingRoom.RoomStatus status,Pageable pageable);
}