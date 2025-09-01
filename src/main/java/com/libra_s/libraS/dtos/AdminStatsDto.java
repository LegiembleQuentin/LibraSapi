package com.libra_s.libraS.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AdminStatsDto {
    // Utilisateurs
    private long totalUsers;
    private long newUsersDay;
    private long newUsersWeek;
    private long newUsersMonth;

    // Activité utilisateurs
    private long dau;
    private long wau;
    private long mau;

    // Livres
    private long totalBooks;
    private long newBooksDay;
    private long newBooksWeek;
    private long newBooksMonth;

    // Tops livres
    private List<BookDto> topBooksByVisits; // nbVisit
    private List<BookDto> topBooksByReaders; // lecteurs uniques
    private List<BookTrendDto> trendingBooks; // variation d'engagement (top 5)
    private List<BookDto> topBooksByRating; // note

    // Auteurs / Tags
    private List<EntityCountDto> topAuthorsByReaders; // auteur + lecteurs uniques
    private List<EntityCountDto> topTagsByReaders; // tag + lecteurs uniques
} 