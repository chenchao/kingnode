package com.kingnode.meeting.dao;
import com.kingnode.meeting.entity.KnMeetingRule;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnMeetingRuleDao extends PagingAndSortingRepository<KnMeetingRule,Long>, JpaSpecificationExecutor<KnMeetingRule>{
}
