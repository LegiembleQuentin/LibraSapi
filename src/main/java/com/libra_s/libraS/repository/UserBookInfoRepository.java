package com.libra_s.libraS.repository;

import com.libra_s.libraS.domain.UserBookInfo;
import com.libra_s.libraS.domain.enums.UserBookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.time.LocalDateTime;

@Repository
public interface UserBookInfoRepository extends JpaRepository<UserBookInfo, Long> {

    Optional<UserBookInfo> findByAppUserIdAndBookId(Long userId, Long bookId);

    List<UserBookInfo> findByAppUserId(Long userId);

    @Query("SELECT COUNT(ubi) FROM UserBookInfo ubi WHERE ubi.book.id = :bookId")
    Long countByBookId(@Param("bookId") Long bookId);

    @Query("SELECT AVG(ubi.currentVolume) FROM UserBookInfo ubi WHERE ubi.book.id = :bookId")
    Double getAverageCurrentVolumeByBookId(@Param("bookId") Long bookId);

    @Query("SELECT COUNT(ubi) FROM UserBookInfo ubi WHERE ubi.book.id = :bookId AND ubi.status = :status")
    Long countByBookIdAndStatus(@Param("bookId") Long bookId, @Param("status") UserBookStatus status);
    
    @Query("SELECT COUNT(ubi) FROM UserBookInfo ubi WHERE ubi.book.id = :bookId AND ubi.modifiedAt >= :sinceDate")
    Long countByBookIdAndModifiedAfter(@Param("bookId") Long bookId, @Param("sinceDate") LocalDateTime sinceDate);
    
    @Query("SELECT COUNT(ubi) FROM UserBookInfo ubi WHERE ubi.book.id = :bookId AND ubi.modifiedAt >= :startDate AND ubi.modifiedAt < :endDate")
    Long countByBookIdAndModifiedBetween(@Param("bookId") Long bookId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ubi.book.id FROM UserBookInfo ubi WHERE ubi.appUser.id = :userId AND ubi.book.id IN :bookIds")
    Set<Long> findBookIdsInUserLibrary(@Param("userId") Long userId, @Param("bookIds") Collection<Long> bookIds);
    
    boolean existsByAppUserIdAndBookId(Long appUserId, Long bookId);

    @Query("SELECT COUNT(DISTINCT ubi.appUser.id) FROM UserBookInfo ubi WHERE ubi.modifiedAt >= :since")
    long countDistinctActiveUsersSince(@Param("since") LocalDateTime since);

    @Query("SELECT b.id, "+
           "COUNT(DISTINCT CASE WHEN ubi.modifiedAt >= :thisMonthStart AND ubi.modifiedAt < :now THEN ubi.appUser.id END), "+
           "COUNT(DISTINCT CASE WHEN ubi.modifiedAt >= :lastMonthStart AND ubi.modifiedAt < :thisMonthStart THEN ubi.appUser.id END) " +
           "FROM UserBookInfo ubi JOIN ubi.book b " +
           "GROUP BY b.id")
    List<Object[]> aggregateDistinctActiveUsersForTrend(@Param("thisMonthStart") LocalDateTime thisMonthStart,
                                                        @Param("lastMonthStart") LocalDateTime lastMonthStart,
                                                        @Param("now") LocalDateTime now);

    @Query("SELECT b, COUNT(DISTINCT ubi.appUser.id) as readers FROM UserBookInfo ubi JOIN ubi.book b GROUP BY b ORDER BY readers DESC")
    List<Object[]> findTopBooksByReaders(Pageable pageable);
}
