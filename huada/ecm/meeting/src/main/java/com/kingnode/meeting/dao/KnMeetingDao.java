package com.kingnode.meeting.dao;
import java.util.List;

import com.kingnode.meeting.entity.KnMeeting;
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
public interface KnMeetingDao extends PagingAndSortingRepository<KnMeeting,Long>, JpaSpecificationExecutor<KnMeeting>{
    @Query("select u from KnMeeting u where u.beginDate>=:beginDate and u.endDate<=:endTime and u.kmr.id=:id and u.statusType<>'CANCEL'")
    List<KnMeeting> findByDateKnMeeting(@Param("beginDate") Long beginDate,@Param("endTime") Long endTime,@Param("id") Long id);
    @Query("select u from KnMeeting u where u.id=:id and u.userId=:userId") List<KnMeeting> findByNameAndIdKnMeeting(@Param("id") Long id,@Param("userId") Long userId);
    @Query("select u from KnMeeting u,KnMeetingAttendee a where u.id=a.km.id and (u.beginDate between :beginDate and :endTime) and (u.userId=:userId or a.attendeeId=:userId) and u.statusType<>'CANCEL'")
    List<KnMeeting> findKnMeeting(@Param("beginDate") Long beginDate,@Param("endTime") Long endTime,@Param("userId") Long userId);
    @Query("select count(u) from KnMeeting u where ((u.beginDate between :beginDate and :endDate) or (u.endDate between :beginDate and :endDate)) and u.kmr.id=:id and u.statusType<>'CANCEL'")
    Long findExistMeeting(@Param("beginDate") Long beginDate,@Param("endDate") Long endDate,@Param("id") Long id);
    @Query("select u.id from KnMeeting u where u.userId=:userId and u.endDate<:currDate and u.statusType<>'CANCEL'")
    Long[] findEndMeeting(@Param(value="userId") Long userId,@Param(value="currDate") Long currDate);
    @Query("select u.id from KnMeeting u where u.userId=:userId and u.endDate between :lockDate and :currDate and u.statusType<>'CANCEL'")
    Long[] findEndMeeting(@Param(value="userId") Long userId,@Param(value="lockDate") Long lockDate,@Param(value="currDate") Long currDate);
    @Query("select distinct u from KnMeeting u left join u.kma a where u.beginDate>=:beginDate and u.endDate<=:endDate and (u.userId=:userId or a.attendeeId=:userId) order by u.beginDate asc")
    Page<KnMeeting> findMeeting(@Param(value="userId") Long userId,@Param(value="beginDate") Long beginDate,@Param(value="endDate") Long endDate,Pageable pageable);

    @Query("select u from KnMeeting u where u.beginDate>=:currDate and u.statusType<>'CANCEL' and u.signInCueStatus is null")
    List<KnMeeting> findMeetingBefore(@Param(value="currDate") Long currDate);
    @Query("select u from KnMeeting u where u.endDate<=:currDate and u.statusType<>'CANCEL' and u.signInCueEndStatus is null")
    List<KnMeeting> findMeetingAfter(@Param(value="currDate") Long currDate);
    @Query("select u from KnMeeting u where u.beginDate>=:currDate and u.statusType<>'CANCEL' and u.attendeeCueStatus is null")
    List<KnMeeting> findMeetingAttendeeCue(@Param(value="currDate") Long currDate);

}
