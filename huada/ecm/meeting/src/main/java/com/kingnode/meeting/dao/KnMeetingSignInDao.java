package com.kingnode.meeting.dao;
import com.kingnode.meeting.entity.KnMeetingSignIn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnMeetingSignInDao extends PagingAndSortingRepository<KnMeetingSignIn,Long>{
    Page<KnMeetingSignIn> findByIdMeetingId(Long meetingId,Pageable pageable);
    Page<KnMeetingSignIn> findByIdMeetingIdAndIdUserId(Long meetingId,Long userId,Pageable pageable);
    KnMeetingSignIn findByIdMeetingIdAndIdUserId(Long meetingId,Long userId);
    @Query("select count(u) from KnMeetingSignIn u where u.id.userId=:userId and u.id.meetingId in :ids")
    Integer findKnMeetingSignInByParam(@Param(value="userId") Long uesrId,@Param(value="ids") Long[] ids);
}