package com.libra_s.libraS.dtos.mapper;

import com.libra_s.libraS.domain.Book;
import com.libra_s.libraS.dtos.AdminBookDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TagMapper.class, AuthorMapper.class})
public interface AdminBookMapper {

    @Mapping(target = "name", expression = "java(entity.getNames().get(0))")
    @Mapping(target = "nbVisit", source = "nbVisit")
    AdminBookDto toAdminDto(Book entity);

    List<AdminBookDto> toAdminDtoList(List<Book> entityList);
}
