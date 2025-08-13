package com.libra_s.libraS.service;

import com.libra_s.libraS.dtos.BookStatistics;
import com.libra_s.libraS.repository.UserBookInfoRepository;
import com.libra_s.libraS.domain.enums.UserBookStatus;
import org.springframework.stereotype.Service;

import java.util.Map;

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
        
        return BookStatistics.builder()
            .totalUsers(totalUsers != null ? totalUsers.intValue() : 0)
            .averageVolume(averageVolume != null ? averageVolume.doubleValue() : 0.0)
            .usersInProgress(usersInProgress != null ? usersInProgress.intValue() : 0)
            .usersCompleted(usersCompleted != null ? usersCompleted.intValue() : 0)
            .usersNotStarted(usersNotStarted != null ? usersNotStarted.intValue() : 0)
            .averageProgress(calculateAverageProgress(bookId, totalVolumes))
            .completionRate(calculateCompletionRate(bookId, totalUsers))
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
}
