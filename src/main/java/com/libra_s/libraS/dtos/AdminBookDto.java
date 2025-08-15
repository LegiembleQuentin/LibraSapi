package com.libra_s.libraS.dtos;

import com.libra_s.libraS.domain.enums.UserBookStatus;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class AdminBookDto {
    private Long id;

    private String synopsis;

    @NotNull
    private String name;

    @NotNull
    private LocalDate dateStart;

    private LocalDate dateEnd;

    @NotNull
    private int nbVolume;

    private BigDecimal note;

    private Integer nbVisit;

    private String imgUrl;

    private Boolean isCompleted;

    private Set<TagDto> tags = new HashSet<>();

    private Set<AuthorDto> authors = new HashSet<>();

    private LocalDate createdAt;

    private LocalDate modifiedAt;

    private UserBookStatus userStatus;

    private Integer userRating;

    private Integer userCurrentVolume;

    private BigDecimal userMatch;

    private Integer totalUsers;
    private Double averageVolume;
    private Integer usersInProgress;
    private Integer usersCompleted;
    private Integer usersNotStarted;
    private Double averageProgress;
    private Double completionRate;
    
    private Integer activeUsersLast7Days;
    private Integer activeUsersLast30Days;
    private Double engagementTrend;
    private Integer activeUsersThisMonth;
    private Integer activeUsersLastMonth;
    private Integer newReadersThisMonth;
}
