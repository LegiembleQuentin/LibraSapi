package com.libra_s.libraS.dtos;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class AuthorDto {
    private Long id;

    private String name;

    private Set<BookDto> books = new HashSet<>();
}
