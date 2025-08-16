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
    @Mapping(target = "isCompleted", expression = "java(entity.isCompleted())")
    AdminBookDto toAdminDto(Book entity);

    @Mapping(target = "names", expression = "java(java.util.Arrays.asList(dto.getName()))")
    @Mapping(target = "nbVisit", source = "nbVisit")
    @Mapping(target = "isCompleted", expression = "java(dto.getIsCompleted())")
    Book toEntity(AdminBookDto dto);

    List<AdminBookDto> toAdminDtoList(List<Book> entityList);
}
