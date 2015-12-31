package com.kingnode.library.dao;
import com.kingnode.library.entity.KnLibraryBook;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public interface KnLibraryBookDao extends PagingAndSortingRepository<KnLibraryBook,Long>, JpaSpecificationExecutor<KnLibraryBook>{
    KnLibraryBook findByIsbn(String isbn);
}