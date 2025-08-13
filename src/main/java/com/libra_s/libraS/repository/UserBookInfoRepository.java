package com.libra_s.libraS.repository;

import com.libra_s.libraS.domain.UserBookInfo;
import com.libra_s.libraS.domain.enums.UserBookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
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
}
