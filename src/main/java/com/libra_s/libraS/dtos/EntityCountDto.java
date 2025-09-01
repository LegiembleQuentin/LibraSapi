package com.libra_s.libraS.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntityCountDto {
    private Long id;
    private String name;
    private long count;
} 