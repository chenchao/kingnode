package com.kingnode.xsimple.dao.system;
import com.kingnode.xsimple.entity.system.KnIdeaFeedback;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * cici
 */
public interface KnIdeaFeedbackDao extends PagingAndSortingRepository<KnIdeaFeedback,Long>, JpaSpecificationExecutor<KnIdeaFeedback>{
}
