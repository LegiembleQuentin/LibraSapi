package com.libra_s.libraS.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookTrendDto {
    private BookDto book;
    private long activeThisMonth;
    private long activeLastMonth;
    private double trendPercent; // (this-last)/last
} 