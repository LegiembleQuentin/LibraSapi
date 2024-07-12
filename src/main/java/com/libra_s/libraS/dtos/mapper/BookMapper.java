package com.libra_s.libraS.dtos.mapper;

import com.libra_s.libraS.domain.Book;
import com.libra_s.libraS.dtos.BookDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {})
public interface BookMapper extends EntityMapper<BookDto, Book> {

    @Override
    Book toEntity(BookDto dto);

    @Override
    BookDto toDto(Book entity);

    @Override
    List<Book> toEntity(List<BookDto> dtoList);

    @Override
    List<BookDto> toDto(List<Book> entityList);
}
