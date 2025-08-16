package com.libra_s.libraS.dtos;

import com.libra_s.libraS.domain.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserFilterDto {
    private String search;
    private Role role;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
}
