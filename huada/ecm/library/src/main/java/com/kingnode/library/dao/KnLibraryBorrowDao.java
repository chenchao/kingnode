package com.kingnode.library.dao;
import java.util.List;

import com.kingnode.library.entity.KnLibraryBookUnit;
import com.kingnode.library.entity.KnLibraryBorrow;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@SuppressWarnings("ALL")
public interface KnLibraryBorrowDao extends PagingAndSortingRepository<KnLibraryBorrow,Long>, JpaSpecificationExecutor<KnLibraryBorrow>{
    @Query("select u  from KnLibraryBorrow u  where u.klbu.kbl.id=:bookId and u.borrowType='RESTORE' order by createTime desc")
    public List<KnLibraryBorrow> queryKnLibraryBorrows(@Param("bookId") Long bookId);

    /**
     * 获取此人已经借书列表，包括续借，已借，预约
     * @param bookId
     * @return
     */
    @Query("select u  from KnLibraryBorrow u  where u.userId=:userId and (u.borrowType in('RESERVE','LEND','RENEW'))")
    public List<KnLibraryBorrow> queryBorrowsNum(@Param("userId") Long userId);

    /**
     * 获取规定时间内没有领书的借书信息
     * @param bookId
     * @return
     */
    @Query("select u  from KnLibraryBorrow u  where u.createTime<=:createTime and (u.borrowType ='RESERVE')")
    public List<KnLibraryBorrow> queryBorrowsNoGet(@Param("createTime") Long createTime);

    /**
     * 获取规定时间内没有还书的借书信息
     * @param bookId
     * @return
     */
    @Query("select u  from KnLibraryBorrow u  where u.shouldRestoreDate is not null and u.shouldRestoreDate<=:now and u.borrowType  in('RENEW','LEND')")
    public List<KnLibraryBorrow> queryBorrowsNoReturn(@Param("now") Long now);
}