package com.libra_s.libraS.dtos;

import com.libra_s.libraS.domain.enums.Role;
import lombok.Data;

import java.util.List;

@Data
public class AdminLoginResponseDto {
    private String token;
    private long expiresIn;
    private Long userId;
    private String displayName;
    private String email;
    private List<Role> roles;
} 