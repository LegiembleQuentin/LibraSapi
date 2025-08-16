package com.libra_s.libraS.dtos;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class BookFilterDto {
    private List<String> tags;
    private String search;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;
    
    private String author;
    private Boolean isCompleted;
    private Integer minVolumes;
    private Integer maxVolumes;
    private Double minRating;
    private Double maxRating;
}
