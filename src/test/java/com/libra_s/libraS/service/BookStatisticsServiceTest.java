package com.libra_s.libraS.service;

import com.libra_s.libraS.dtos.BookStatistics;
import com.libra_s.libraS.domain.enums.UserBookStatus;
import com.libra_s.libraS.repository.UserBookInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookStatisticsServiceTest {

    @Mock
    private UserBookInfoRepository userBookInfoRepository;

    @InjectMocks
    private BookStatisticsService bookStatisticsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookStatisticsService = new BookStatisticsService(userBookInfoRepository);
    }

    @Test
    void calculateBookStatistics_basicAggregates_andDerivedMetrics() {
        Long bookId = 1L;
        int totalVolumes = 10;

        when(userBookInfoRepository.countByBookId(bookId)).thenReturn(100L);
        when(userBookInfoRepository.getAverageCurrentVolumeByBookId(bookId)).thenReturn(4.5);
        when(userBookInfoRepository.countByBookIdAndStatus(bookId, UserBookStatus.READING)).thenReturn(30L);
        when(userBookInfoRepository.countByBookIdAndStatus(bookId, UserBookStatus.COMPLETED)).thenReturn(20L);
        when(userBookInfoRepository.countByBookIdAndStatus(bookId, UserBookStatus.TO_READ)).thenReturn(50L);

        // Valeurs dépendantes du temps: on ne valide que la cohérence (>=0)
        when(userBookInfoRepository.countByBookIdAndModifiedAfter(eq(bookId), any())).thenReturn(10L, 25L);
        when(userBookInfoRepository.countByBookIdAndModifiedBetween(eq(bookId), any(), any())).thenReturn(12L, 8L);

        BookStatistics stats = bookStatisticsService.calculateBookStatistics(bookId, totalVolumes);

        assertNotNull(stats);

        // Agrégats bruts
        assertEquals(100, stats.getTotalUsers());
        assertEquals(4.5, stats.getAverageVolume());
        assertEquals(30, stats.getUsersInProgress());
        assertEquals(20, stats.getUsersCompleted());
        assertEquals(50, stats.getUsersNotStarted());

        // Dérivés
        assertEquals(0.45, stats.getAverageProgress()); // 4.5 / 10 arrondi à 2 décimales
        assertEquals(0.2, stats.getCompletionRate());   // 20 / 100 arrondi à 2 décimales

        // Champs temporels non négatifs
        assertTrue(stats.getActiveUsersLast7Days() >= 0);
        assertTrue(stats.getActiveUsersLast30Days() >= 0);
        assertTrue(stats.getActiveUsersThisMonth() >= 0);
        assertTrue(stats.getActiveUsersLastMonth() >= 0);
        assertTrue(stats.getNewReadersThisMonth() >= 0);

        // Engagement trend: valeurs mockées thisMonth=12, lastMonth=8 -> ((12-8)/8)*100 = 50.0
        assertEquals(50.0, stats.getEngagementTrend());

        verify(userBookInfoRepository, atLeastOnce()).countByBookId(bookId);
        verify(userBookInfoRepository, atLeastOnce()).getAverageCurrentVolumeByBookId(bookId);
        verify(userBookInfoRepository, atLeastOnce()).countByBookIdAndStatus(eq(bookId), any());
        verify(userBookInfoRepository, atLeastOnce()).countByBookIdAndModifiedAfter(eq(bookId), any());
        verify(userBookInfoRepository, atLeastOnce()).countByBookIdAndModifiedBetween(eq(bookId), any(), any());
    }

    @Test
    void calculateBookStatistics_handlesNulls_andZeroSafely() {
        Long bookId = 2L;

        when(userBookInfoRepository.countByBookId(bookId)).thenReturn(null);
        when(userBookInfoRepository.getAverageCurrentVolumeByBookId(bookId)).thenReturn(null);
        when(userBookInfoRepository.countByBookIdAndStatus(eq(bookId), any())).thenReturn(null);
        when(userBookInfoRepository.countByBookIdAndModifiedAfter(eq(bookId), any())).thenReturn(null);
        when(userBookInfoRepository.countByBookIdAndModifiedBetween(eq(bookId), any(), any())).thenReturn(null);

        BookStatistics stats = bookStatisticsService.calculateBookStatistics(bookId, 0);

        assertNotNull(stats);
        assertEquals(0, stats.getTotalUsers());
        assertEquals(0.0, stats.getAverageVolume());
        assertEquals(0, stats.getUsersInProgress());
        assertEquals(0, stats.getUsersCompleted());
        assertEquals(0, stats.getUsersNotStarted());
        assertEquals(0.0, stats.getAverageProgress());
        assertEquals(0.0, stats.getCompletionRate());
        assertEquals(0, stats.getActiveUsersLast7Days());
        assertEquals(0, stats.getActiveUsersLast30Days());
        assertEquals(0, stats.getActiveUsersThisMonth());
        assertEquals(0, stats.getActiveUsersLastMonth());
        assertEquals(0, stats.getNewReadersThisMonth());
        assertEquals(0.0, stats.getEngagementTrend());
    }
}


