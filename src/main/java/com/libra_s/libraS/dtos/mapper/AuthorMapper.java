package com.libra_s.libraS.dtos.mapper;

import com.libra_s.libraS.domain.Author;
import com.libra_s.libraS.dtos.AuthorDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {})
public interface AuthorMapper extends EntityMapper<AuthorDto, Author> {

        @Override
        Author toEntity(AuthorDto dto);

        @Override
        AuthorDto toDto(Author entity);

        @Override
        List<Author> toEntity(List<AuthorDto> dtoList);

        @Override
        List<AuthorDto> toDto(List<Author> entityList);
}
