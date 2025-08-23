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

    // Test principal : vérifie tous les calculs statistiques avec des données valides
    @Test
    void calculateBookStatistics_basicAggregates_andDerivedMetrics() {
        Long bookId = 1L;
        int totalVolumes = 10;

        when(userBookInfoRepository.countByBookId(bookId)).thenReturn(100L);
        when(userBookInfoRepository.getAverageCurrentVolumeByBookId(bookId)).thenReturn(4.5);
        when(userBookInfoRepository.countByBookIdAndStatus(bookId, UserBookStatus.READING)).thenReturn(30L);
        when(userBookInfoRepository.countByBookIdAndStatus(bookId, UserBookStatus.COMPLETED)).thenReturn(20L);
        when(userBookInfoRepository.countByBookIdAndStatus(bookId, UserBookStatus.TO_READ)).thenReturn(50L);
        when(userBookInfoRepository.countByBookIdAndModifiedAfter(eq(bookId), any())).thenReturn(10L, 25L);
        when(userBookInfoRepository.countByBookIdAndModifiedBetween(eq(bookId), any(), any())).thenReturn(12L, 8L);

        BookStatistics stats = bookStatisticsService.calculateBookStatistics(bookId, totalVolumes);

        assertNotNull(stats);
        assertEquals(100, stats.getTotalUsers());
        assertEquals(4.5, stats.getAverageVolume());
        assertEquals(30, stats.getUsersInProgress());
        assertEquals(20, stats.getUsersCompleted());
        assertEquals(50, stats.getUsersNotStarted());
        assertEquals(0.45, stats.getAverageProgress());
        assertEquals(0.2, stats.getCompletionRate());
        assertEquals(50.0, stats.getEngagementTrend());

        assertTrue(stats.getActiveUsersLast7Days() >= 0);
        assertTrue(stats.getActiveUsersLast30Days() >= 0);
        assertTrue(stats.getActiveUsersThisMonth() >= 0);
        assertTrue(stats.getActiveUsersLastMonth() >= 0);
        assertTrue(stats.getNewReadersThisMonth() >= 0);

        verify(userBookInfoRepository, atLeastOnce()).countByBookId(bookId);
        verify(userBookInfoRepository, atLeastOnce()).getAverageCurrentVolumeByBookId(bookId);
        verify(userBookInfoRepository, atLeastOnce()).countByBookIdAndStatus(eq(bookId), any());
        verify(userBookInfoRepository, atLeastOnce()).countByBookIdAndModifiedAfter(eq(bookId), any());
        verify(userBookInfoRepository, atLeastOnce()).countByBookIdAndModifiedBetween(eq(bookId), any(), any());
    }

    // Test de robustesse : vérifie le comportement avec des valeurs nulles et zéro
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

    // Test calculateAverageProgress : vérifie le calcul du pourcentage de progression moyen
    @Test
    void calculateAverageProgress_withValidData_returnsCorrectPercentage() {
        Long bookId = 3L;
        int totalVolumes = 20;

        when(userBookInfoRepository.getAverageCurrentVolumeByBookId(bookId)).thenReturn(7.5);
        when(userBookInfoRepository.countByBookId(bookId)).thenReturn(100L);
        when(userBookInfoRepository.countByBookIdAndStatus(eq(bookId), any())).thenReturn(0L);
        when(userBookInfoRepository.countByBookIdAndModifiedAfter(eq(bookId), any())).thenReturn(0L);
        when(userBookInfoRepository.countByBookIdAndModifiedBetween(eq(bookId), any(), any())).thenReturn(0L);

        BookStatistics stats = bookStatisticsService.calculateBookStatistics(bookId, totalVolumes);

        assertEquals(0.38, stats.getAverageProgress());
    }

    // Test calculateAverageProgress : cas limite avec zéro volumes (division par zéro)
    @Test
    void calculateAverageProgress_withZeroVolumes_returnsZero() {
        Long bookId = 4L;
        int totalVolumes = 0;

        when(userBookInfoRepository.getAverageCurrentVolumeByBookId(bookId)).thenReturn(5.0);
        when(userBookInfoRepository.countByBookId(bookId)).thenReturn(100L);
        when(userBookInfoRepository.countByBookIdAndStatus(eq(bookId), any())).thenReturn(0L);
        when(userBookInfoRepository.countByBookIdAndModifiedAfter(eq(bookId), any())).thenReturn(0L);
        when(userBookInfoRepository.countByBookIdAndModifiedBetween(eq(bookId), any(), any())).thenReturn(0L);

        BookStatistics stats = bookStatisticsService.calculateBookStatistics(bookId, totalVolumes);

        assertEquals(0.0, stats.getAverageProgress());
    }

    // Test calculateCompletionRate : vérifie le calcul du taux de complétion
    @Test
    void calculateCompletionRate_withValidData_returnsCorrectPercentage() {
        Long bookId = 5L;
        Long totalUsers = 150L;
        Long completedUsers = 45L;

        when(userBookInfoRepository.countByBookId(bookId)).thenReturn(totalUsers);
        when(userBookInfoRepository.getAverageCurrentVolumeByBookId(bookId)).thenReturn(3.0);
        when(userBookInfoRepository.countByBookIdAndStatus(bookId, UserBookStatus.COMPLETED)).thenReturn(completedUsers);
        when(userBookInfoRepository.countByBookIdAndStatus(bookId, UserBookStatus.READING)).thenReturn(50L);
        when(userBookInfoRepository.countByBookIdAndStatus(bookId, UserBookStatus.TO_READ)).thenReturn(55L);
        when(userBookInfoRepository.countByBookIdAndModifiedAfter(eq(bookId), any())).thenReturn(0L);
        when(userBookInfoRepository.countByBookIdAndModifiedBetween(eq(bookId), any(), any())).thenReturn(0L);

        BookStatistics stats = bookStatisticsService.calculateBookStatistics(bookId, 10);

        assertEquals(0.3, stats.getCompletionRate());
    }

    // Test calculateCompletionRate : cas limite avec zéro utilisateurs
    @Test
    void calculateCompletionRate_withZeroUsers_returnsZero() {
        Long bookId = 6L;

        when(userBookInfoRepository.countByBookId(bookId)).thenReturn(0L);
        when(userBookInfoRepository.getAverageCurrentVolumeByBookId(bookId)).thenReturn(null);
        when(userBookInfoRepository.countByBookIdAndStatus(eq(bookId), any())).thenReturn(0L);
        when(userBookInfoRepository.countByBookIdAndModifiedAfter(eq(bookId), any())).thenReturn(0L);
        when(userBookInfoRepository.countByBookIdAndModifiedBetween(eq(bookId), any(), any())).thenReturn(0L);

        BookStatistics stats = bookStatisticsService.calculateBookStatistics(bookId, 10);

        assertEquals(0.0, stats.getCompletionRate());
    }

    // Test calculateEngagementTrend : vérifie le calcul d'une tendance positive d'engagement
    @Test
    void calculateEngagementTrend_withPositiveTrend_returnsCorrectPercentage() {
        Long bookId = 7L;
        Long thisMonth = 120L;
        Long lastMonth = 80L;

        when(userBookInfoRepository.countByBookId(bookId)).thenReturn(100L);
        when(userBookInfoRepository.getAverageCurrentVolumeByBookId(bookId)).thenReturn(5.0);
        when(userBookInfoRepository.countByBookIdAndStatus(eq(bookId), any())).thenReturn(30L);
        when(userBookInfoRepository.countByBookIdAndModifiedAfter(eq(bookId), any())).thenReturn(10L);
        when(userBookInfoRepository.countByBookIdAndModifiedBetween(eq(bookId), any(), any()))
                .thenReturn(thisMonth, lastMonth);

        BookStatistics stats = bookStatisticsService.calculateBookStatistics(bookId, 10);

        assertEquals(50.0, stats.getEngagementTrend());
    }

    // Test calculateEngagementTrend : vérifie le calcul d'une tendance négative d'engagement
    @Test
    void calculateEngagementTrend_withNegativeTrend_returnsCorrectNegativePercentage() {
        Long bookId = 8L;
        Long thisMonth = 60L;
        Long lastMonth = 100L;

        when(userBookInfoRepository.countByBookId(bookId)).thenReturn(100L);
        when(userBookInfoRepository.getAverageCurrentVolumeByBookId(bookId)).thenReturn(5.0);
        when(userBookInfoRepository.countByBookIdAndStatus(eq(bookId), any())).thenReturn(30L);
        when(userBookInfoRepository.countByBookIdAndModifiedAfter(eq(bookId), any())).thenReturn(10L);
        when(userBookInfoRepository.countByBookIdAndModifiedBetween(eq(bookId), any(), any()))
                .thenReturn(thisMonth, lastMonth);

        BookStatistics stats = bookStatisticsService.calculateBookStatistics(bookId, 10);

        assertEquals(-40.0, stats.getEngagementTrend());
    }

    // Test calculateEngagementTrend : cas limite avec zéro le mois dernier (division par zéro)
    @Test
    void calculateEngagementTrend_withZeroLastMonth_returnsZero() {
        Long bookId = 9L;
        Long thisMonth = 50L;
        Long lastMonth = 0L;

        when(userBookInfoRepository.countByBookId(bookId)).thenReturn(100L);
        when(userBookInfoRepository.getAverageCurrentVolumeByBookId(bookId)).thenReturn(5.0);
        when(userBookInfoRepository.countByBookIdAndStatus(eq(bookId), any())).thenReturn(30L);
        when(userBookInfoRepository.countByBookIdAndModifiedAfter(eq(bookId), any())).thenReturn(10L);
        when(userBookInfoRepository.countByBookIdAndModifiedBetween(eq(bookId), any(), any()))
                .thenReturn(thisMonth, lastMonth);

        BookStatistics stats = bookStatisticsService.calculateBookStatistics(bookId, 10);

        assertEquals(0.0, stats.getEngagementTrend());
    }

    // Test calculateEngagementTrend : gestion des valeurs nulles (thisMonth = null traité comme 0)
    @Test
    void calculateEngagementTrend_withNullThisMonth_treatsAsZero() {
        Long bookId = 10L;
        Long thisMonth = null;
        Long lastMonth = 50L;

        when(userBookInfoRepository.countByBookId(bookId)).thenReturn(100L);
        when(userBookInfoRepository.getAverageCurrentVolumeByBookId(bookId)).thenReturn(5.0);
        when(userBookInfoRepository.countByBookIdAndStatus(eq(bookId), any())).thenReturn(30L);
        when(userBookInfoRepository.countByBookIdAndModifiedAfter(eq(bookId), any())).thenReturn(10L);
        when(userBookInfoRepository.countByBookIdAndModifiedBetween(eq(bookId), any(), any()))
                .thenReturn(thisMonth, lastMonth);

        BookStatistics stats = bookStatisticsService.calculateBookStatistics(bookId, 10);

        assertEquals(-100.0, stats.getEngagementTrend());
    }

    // Test d'arrondis précis : vérifie l'arrondi à 2 décimales pour averageProgress
    @Test
    void calculateAverageProgress_withPreciseRounding_roundsCorrectly() {
        Long bookId = 11L;
        int totalVolumes = 3;

        // 1.666... / 3 = 0.555... -> arrondi à 0.56
        when(userBookInfoRepository.getAverageCurrentVolumeByBookId(bookId)).thenReturn(1.6666666);
        when(userBookInfoRepository.countByBookId(bookId)).thenReturn(100L);
        when(userBookInfoRepository.countByBookIdAndStatus(eq(bookId), any())).thenReturn(0L);
        when(userBookInfoRepository.countByBookIdAndModifiedAfter(eq(bookId), any())).thenReturn(0L);
        when(userBookInfoRepository.countByBookIdAndModifiedBetween(eq(bookId), any(), any())).thenReturn(0L);

        BookStatistics stats = bookStatisticsService.calculateBookStatistics(bookId, totalVolumes);

        assertEquals(0.56, stats.getAverageProgress());
    }

    // Test d'arrondis précis : vérifie l'arrondi à 2 décimales pour completionRate
    @Test
    void calculateCompletionRate_withPreciseRounding_roundsCorrectly() {
        Long bookId = 12L;
        Long totalUsers = 7L;
        Long completedUsers = 1L; // 1/7 = 0.142857... -> arrondi à 0.14

        when(userBookInfoRepository.countByBookId(bookId)).thenReturn(totalUsers);
        when(userBookInfoRepository.getAverageCurrentVolumeByBookId(bookId)).thenReturn(3.0);
        when(userBookInfoRepository.countByBookIdAndStatus(bookId, UserBookStatus.COMPLETED)).thenReturn(completedUsers);
        when(userBookInfoRepository.countByBookIdAndStatus(bookId, UserBookStatus.READING)).thenReturn(3L);
        when(userBookInfoRepository.countByBookIdAndStatus(bookId, UserBookStatus.TO_READ)).thenReturn(3L);
        when(userBookInfoRepository.countByBookIdAndModifiedAfter(eq(bookId), any())).thenReturn(0L);
        when(userBookInfoRepository.countByBookIdAndModifiedBetween(eq(bookId), any(), any())).thenReturn(0L);

        BookStatistics stats = bookStatisticsService.calculateBookStatistics(bookId, 10);

        assertEquals(0.14, stats.getCompletionRate());
    }

    // Test d'arrondis précis : vérifie l'arrondi à 2 décimales pour engagementTrend
    @Test
    void calculateEngagementTrend_withPreciseRounding_roundsCorrectly() {
        Long bookId = 13L;
        Long thisMonth = 17L;
        Long lastMonth = 6L; // (17-6)/6 = 11/6 = 1.833... * 100 = 183.333... -> arrondi à 183.33

        when(userBookInfoRepository.countByBookId(bookId)).thenReturn(100L);
        when(userBookInfoRepository.getAverageCurrentVolumeByBookId(bookId)).thenReturn(5.0);
        when(userBookInfoRepository.countByBookIdAndStatus(eq(bookId), any())).thenReturn(30L);
        when(userBookInfoRepository.countByBookIdAndModifiedAfter(eq(bookId), any())).thenReturn(10L);
        when(userBookInfoRepository.countByBookIdAndModifiedBetween(eq(bookId), any(), any()))
                .thenReturn(thisMonth, lastMonth);

        BookStatistics stats = bookStatisticsService.calculateBookStatistics(bookId, 10);

        assertEquals(183.33, stats.getEngagementTrend());
    }

    // Test d'arrondis limites : vérifie l'arrondi de valeurs très petites
    @Test
    void calculateAverageProgress_withVerySmallValues_roundsCorrectly() {
        Long bookId = 14L;
        int totalVolumes = 1000;

        // 0.001 / 1000 = 0.000001 -> arrondi à 0.0
        when(userBookInfoRepository.getAverageCurrentVolumeByBookId(bookId)).thenReturn(0.001);
        when(userBookInfoRepository.countByBookId(bookId)).thenReturn(100L);
        when(userBookInfoRepository.countByBookIdAndStatus(eq(bookId), any())).thenReturn(0L);
        when(userBookInfoRepository.countByBookIdAndModifiedAfter(eq(bookId), any())).thenReturn(0L);
        when(userBookInfoRepository.countByBookIdAndModifiedBetween(eq(bookId), any(), any())).thenReturn(0L);

        BookStatistics stats = bookStatisticsService.calculateBookStatistics(bookId, totalVolumes);

        assertEquals(0.0, stats.getAverageProgress());
    }

    // Test d'arrondis limites : vérifie l'arrondi de pourcentages très proches de 1
    @Test
    void calculateCompletionRate_withAlmostCompleteRate_roundsCorrectly() {
        Long bookId = 15L;
        Long totalUsers = 1000L;
        Long completedUsers = 999L; // 999/1000 = 0.999 -> arrondi à 1.0

        when(userBookInfoRepository.countByBookId(bookId)).thenReturn(totalUsers);
        when(userBookInfoRepository.getAverageCurrentVolumeByBookId(bookId)).thenReturn(3.0);
        when(userBookInfoRepository.countByBookIdAndStatus(bookId, UserBookStatus.COMPLETED)).thenReturn(completedUsers);
        when(userBookInfoRepository.countByBookIdAndStatus(bookId, UserBookStatus.READING)).thenReturn(1L);
        when(userBookInfoRepository.countByBookIdAndStatus(bookId, UserBookStatus.TO_READ)).thenReturn(0L);
        when(userBookInfoRepository.countByBookIdAndModifiedAfter(eq(bookId), any())).thenReturn(0L);
        when(userBookInfoRepository.countByBookIdAndModifiedBetween(eq(bookId), any(), any())).thenReturn(0L);

        BookStatistics stats = bookStatisticsService.calculateBookStatistics(bookId, 10);

        assertEquals(1.0, stats.getCompletionRate());
    }
}
