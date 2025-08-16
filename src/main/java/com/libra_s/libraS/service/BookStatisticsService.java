package com.libra_s.libraS.service;

import com.libra_s.libraS.dtos.BookStatistics;
import com.libra_s.libraS.repository.UserBookInfoRepository;
import com.libra_s.libraS.domain.enums.UserBookStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class BookStatisticsService {
    
    private final UserBookInfoRepository userBookInfoRepository;
    
    public BookStatisticsService(UserBookInfoRepository userBookInfoRepository) {
        this.userBookInfoRepository = userBookInfoRepository;
    }
    
    public BookStatistics calculateBookStatistics(Long bookId, int totalVolumes) {
        Long totalUsers = userBookInfoRepository.countByBookId(bookId);
        Double averageVolume = userBookInfoRepository.getAverageCurrentVolumeByBookId(bookId);
        Long usersInProgress = userBookInfoRepository.countByBookIdAndStatus(bookId, UserBookStatus.READING);
        Long usersCompleted = userBookInfoRepository.countByBookIdAndStatus(bookId, UserBookStatus.COMPLETED);
        Long usersNotStarted = userBookInfoRepository.countByBookIdAndStatus(bookId, UserBookStatus.TO_READ);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        LocalDateTime thirtyDaysAgo = now.minusDays(30);
        LocalDateTime thisMonthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime lastMonthStart = thisMonthStart.minusMonths(1);
        LocalDateTime lastMonthEnd = thisMonthStart.minusSeconds(1);
        
        Long activeUsersLast7Days = userBookInfoRepository.countByBookIdAndModifiedAfter(bookId, sevenDaysAgo);
        Long activeUsersLast30Days = userBookInfoRepository.countByBookIdAndModifiedAfter(bookId, thirtyDaysAgo);
        Long activeUsersThisMonth = userBookInfoRepository.countByBookIdAndModifiedBetween(bookId, thisMonthStart, now.plusSeconds(1));
        Long activeUsersLastMonth = userBookInfoRepository.countByBookIdAndModifiedBetween(bookId, lastMonthStart, thisMonthStart);
        Long newReadersThisMonth = userBookInfoRepository.countByBookIdAndModifiedAfter(bookId, thisMonthStart);
        
        return BookStatistics.builder()
            .totalUsers(totalUsers != null ? totalUsers.intValue() : 0)
            .averageVolume(averageVolume != null ? averageVolume.doubleValue() : 0.0)
            .usersInProgress(usersInProgress != null ? usersInProgress.intValue() : 0)
            .usersCompleted(usersCompleted != null ? usersCompleted.intValue() : 0)
            .usersNotStarted(usersNotStarted != null ? usersNotStarted.intValue() : 0)
            .averageProgress(calculateAverageProgress(bookId, totalVolumes))
            .completionRate(calculateCompletionRate(bookId, totalUsers))
            .activeUsersLast7Days(activeUsersLast7Days != null ? activeUsersLast7Days.intValue() : 0)
            .activeUsersLast30Days(activeUsersLast30Days != null ? activeUsersLast30Days.intValue() : 0)
            .engagementTrend(calculateEngagementTrend(activeUsersThisMonth, activeUsersLastMonth))
            .activeUsersThisMonth(activeUsersThisMonth != null ? activeUsersThisMonth.intValue() : 0)
            .activeUsersLastMonth(activeUsersLastMonth != null ? activeUsersLastMonth.intValue() : 0)
            .newReadersThisMonth(newReadersThisMonth != null ? newReadersThisMonth.intValue() : 0)
            .build();
    }
    
    private double calculateAverageProgress(Long bookId, int totalVolumes) {
        if (totalVolumes <= 0) return 0.0;
        Double avgVolume = userBookInfoRepository.getAverageCurrentVolumeByBookId(bookId);
        if (avgVolume == null) return 0.0;
        return Math.round((avgVolume / totalVolumes) * 100.0) / 100.0;
    }
    
    private double calculateCompletionRate(Long bookId, Long totalUsers) {
        if (totalUsers == null || totalUsers == 0) return 0.0;
        Long completedUsers = userBookInfoRepository.countByBookIdAndStatus(bookId, UserBookStatus.COMPLETED);
        if (completedUsers == null) return 0.0;
        return Math.round(((double) completedUsers / totalUsers) * 100.0) / 100.0;
    }
    
    private double calculateEngagementTrend(Long thisMonth, Long lastMonth) {
        if (lastMonth == null || lastMonth == 0) return 0.0;
        if (thisMonth == null) thisMonth = 0L;
        
        double change = ((double) (thisMonth - lastMonth) / lastMonth) * 100;
        return Math.round(change * 100.0) / 100.0;
    }
}
