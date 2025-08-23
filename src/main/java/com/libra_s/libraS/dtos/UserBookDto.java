package com.libra_s.libraS.dtos;

import com.libra_s.libraS.domain.enums.UserBookStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserBookDto {
    private Long bookId;
    private String title;
    private List<String> authors;
    private String imageUrl;
    private UserBookStatus status;
    private Double userRating;
    private Integer currentVolume;
    private Integer totalVolumes;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime modifiedAt;
}
