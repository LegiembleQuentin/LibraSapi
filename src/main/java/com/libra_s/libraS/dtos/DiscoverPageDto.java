package com.libra_s.libraS.dtos;

import lombok.Data;

import java.util.List;

@Data
public class DiscoverPageDto {
    private List<BookDto> popular;

    private List<BookDto> bestRated;

    private List<BookDto> newBooks;

    private List<BookDto> recommended;

    private List<BookDto> carrousselBooks;

    private List<BookDto> completed;

    private List<BookDto> inProgress;

    private List<BookDto> userInProgress;
}
