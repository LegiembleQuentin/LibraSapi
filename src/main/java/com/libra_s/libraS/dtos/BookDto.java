package com.libra_s.libraS.dtos;

import com.libra_s.libraS.domain.enums.UserBookStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
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

    private String imgUrl;

    private Set<TagDto> tags = new HashSet<>();

    private Set<AuthorDto> authors = new HashSet<>();

    private Set<BookDto> relatedBooks = new HashSet<>();

    private Set<BookDto> sameAuthorBooks = new HashSet<>();

    private LocalDate createdAt;

    private LocalDate modifiedAt;

    private UserBookStatus userStatus;

    private Integer userRating;

    private Integer userCurrentVolume;

    private BigDecimal userMatch;
}
