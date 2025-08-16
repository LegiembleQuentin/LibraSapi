package com.libra_s.libraS.dtos;

import com.libra_s.libraS.domain.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdminUserDto {
    private Long id;
    private String displayName;
    private String email;
    private String name;
    private String fname;
    private String profileImageUrl;
    private List<Role> roles;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime lastLoginAt;
    
    private Long totalBooks;
    private Long booksInProgress;
    private Long booksCompleted;
    private Double averageRating;
}
