package com.kingnode.library.dao;
import java.util.List;

import com.kingnode.library.entity.KnLibraryBookUnit;
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
public interface KnLibraryBookUnitDao extends PagingAndSortingRepository<KnLibraryBookUnit,Long>, JpaSpecificationExecutor<KnLibraryBookUnit>{
    @Query("select u  from KnLibraryBookUnit u  where (u.kbl.title like?1 or u.kbl.isbn like?1 or u.kbl.author like?1) and u.bookType=?2")
    public Page<KnLibraryBookUnit> findKnLibBookUnit(String sumaryInfo,KnLibraryBookUnit.BookType bookType,Pageable page);
    @Query("select u  from KnLibraryBookUnit u  order by u.createTime desc ")
    public List<KnLibraryBookUnit> queryKnLibraryBookUnit();
    public List<KnLibraryBookUnit> findByKblId(Long bookId);

    @Query("select u  from KnLibraryBookUnit u  where u.kbl.id=:bookId")
    public List<KnLibraryBookUnit> queryKnLibraryBookUnit(@Param("bookId") Long bookId);

    /**
     * 获取可借的书
     * @param bookId
     * @return
     */
    @Query("select u  from KnLibraryBookUnit u  where u.kbl.id=:bookId and u.bookType='RESTORE'")
    public List<KnLibraryBookUnit> queryBorrowsCanLend(@Param("bookId") Long bookId);


    KnLibraryBookUnit findByCode(String code);
}