package com.kingnode.meeting.dao;
import com.kingnode.meeting.entity.KnMeetingBlacklist;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnMeetingBlacklistDao extends PagingAndSortingRepository<KnMeetingBlacklist,Long>, JpaSpecificationExecutor<KnMeetingBlacklist>{
    KnMeetingBlacklist findByUserId(Long userId);
}
