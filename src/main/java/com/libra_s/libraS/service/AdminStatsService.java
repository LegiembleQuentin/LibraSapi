package com.libra_s.libraS.service;

import com.libra_s.libraS.domain.Author;
import com.libra_s.libraS.domain.Book;
import com.libra_s.libraS.dtos.*;
import com.libra_s.libraS.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminStatsService {

    private final AppUserRepository appUserRepository;
    private final BookRepository bookRepository;
    private final UserBookInfoRepository userBookInfoRepository;
    private final AuthorRepository authorRepository;
    private final TagRepository tagRepository;
    private final com.libra_s.libraS.dtos.mapper.BookMapper bookMapper;

    public AdminStatsDto getStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dayAgo = now.minusDays(1);
        LocalDateTime weekAgo = now.minusWeeks(1);
        LocalDateTime monthAgo = now.minusMonths(1);

        // Utilisateurs
        long totalUsers = appUserRepository.count();
        long newUsersDay = appUserRepository.countUsersCreatedSince(dayAgo);
        long newUsersWeek = appUserRepository.countUsersCreatedSince(weekAgo);
        long newUsersMonth = appUserRepository.countUsersCreatedSince(monthAgo);

        long dau = userBookInfoRepository.countDistinctActiveUsersSince(dayAgo);
        long wau = userBookInfoRepository.countDistinctActiveUsersSince(weekAgo);
        long mau = userBookInfoRepository.countDistinctActiveUsersSince(monthAgo);

        // Livres
        long totalBooks = bookRepository.count();
        long newBooksDay = bookRepository.countBooksCreatedSince(LocalDate.now().minusDays(1));
        long newBooksWeek = bookRepository.countBooksCreatedSince(LocalDate.now().minusWeeks(1));
        long newBooksMonth = bookRepository.countBooksCreatedSince(LocalDate.now().minusMonths(1));

        // Tops livres
        List<BookDto> topBooksByVisits = bookMapper.toDto(bookRepository.findTop20ByOrderByNbVisitDesc())
            .stream().limit(5).collect(Collectors.toList());

        List<Object[]> topReadersRows = userBookInfoRepository.findTopBooksByReaders(PageRequest.of(0, 5));
        List<BookDto> topBooksByReaders = topReadersRows.stream()
            .map(row -> (Book) row[0])
            .map(bookMapper::toDto)
            .collect(Collectors.toList());

        // Trending: distinct active users MoM; ignorer livres sans activité ce mois; si lastMonth=0, trend=100* (this>0 ? 100 : 0)
        LocalDateTime thisMonthStart = now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        LocalDateTime lastMonthStart = thisMonthStart.minusMonths(1);
        List<Object[]> trendRows = userBookInfoRepository.aggregateDistinctActiveUsersForTrend(thisMonthStart, lastMonthStart, now);
        List<BookTrendDto> trendingBooks = trendRows.stream()
            .map(row -> {
                Long bookId = (Long) row[0];
                long thisCount = ((Number) row[1]).longValue();
                long lastCount = ((Number) row[2]).longValue();
                if (thisCount == 0) return null; // ignorer sans activité courante
                double trend;
                if (lastCount == 0) {
                    trend = 100.0; // nouveau mouvement ce mois-ci
                } else {
                    trend = Math.round(((double)(thisCount - lastCount) / lastCount) * 10000.0) / 100.0;
                }
                Book book = bookRepository.findById(bookId).orElse(null);
                if (book == null) return null;
                return BookTrendDto.builder()
                    .book(bookMapper.toDto(book))
                    .activeThisMonth(thisCount)
                    .activeLastMonth(lastCount)
                    .trendPercent(trend)
                    .build();
            })
            .filter(Objects::nonNull)
            .sorted(Comparator.comparingDouble(BookTrendDto::getTrendPercent).reversed())
            .limit(5)
            .collect(Collectors.toList());

        // Top notes: exclure les livres sans note
        List<BookDto> topBooksByRating = bookMapper.toDto(bookRepository.findAllByNoteNotNullOrderByNoteDesc(PageRequest.of(0, 5)));

        // Top auteurs
        List<EntityCountDto> topAuthorsByReaders = authorRepository.findTopAuthorsByReaders(PageRequest.of(0, 5))
            .stream()
            .map(row -> EntityCountDto.builder()
                .id(((Author) row[0]).getId())
                .name(((Author) row[0]).getName())
                .count(((Number) row[1]).longValue())
                .build())
            .collect(Collectors.toList());

        // Top tags
        List<EntityCountDto> topTagsByReaders = tagRepository.findTopTagsByReaders(PageRequest.of(0, 5))
            .stream()
            .map(row -> EntityCountDto.builder()
                .id(((com.libra_s.libraS.domain.Tag) row[0]).getId())
                .name(((com.libra_s.libraS.domain.Tag) row[0]).getName())
                .count(((Number) row[1]).longValue())
                .build())
            .collect(Collectors.toList());

        return AdminStatsDto.builder()
            .totalUsers(totalUsers)
            .newUsersDay(newUsersDay)
            .newUsersWeek(newUsersWeek)
            .newUsersMonth(newUsersMonth)
            .dau(dau)
            .wau(wau)
            .mau(mau)
            .totalBooks(totalBooks)
            .newBooksDay(newBooksDay)
            .newBooksWeek(newBooksWeek)
            .newBooksMonth(newBooksMonth)
            .topBooksByVisits(topBooksByVisits)
            .topBooksByReaders(topBooksByReaders)
            .trendingBooks(trendingBooks)
            .topBooksByRating(topBooksByRating)
            .topAuthorsByReaders(topAuthorsByReaders)
            .topTagsByReaders(topTagsByReaders)
            .build();
    }
} 