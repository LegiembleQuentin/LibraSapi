package com.libra_s.libraS.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class BookDto {
    private Long id;

    private String synopsis;

    @NotNull
    private List<String> names;

    @NotNull
    private LocalDate dateStart;

    private LocalDate dateEnd;

    @NotNull
    private int nbVolume;

    private BigDecimal note;

//    private Set<TagDto> tags = new HashSet<>();

    private Set<AuthorDto> authors = new HashSet<>();

    private Set<BookDto> relatedBooks = new HashSet<>();

    private LocalDate createdAt;

    private LocalDate modifiedAt;
}
