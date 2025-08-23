package com.libra_s.libraS.dtos.mapper;

import com.libra_s.libraS.domain.UserBookInfo;
import com.libra_s.libraS.dtos.UserBookDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserBookMapper {

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "title", expression = "java(userBookInfo.getBook().getNames().isEmpty() ? \"\" : userBookInfo.getBook().getNames().get(0))")
    @Mapping(target = "authors", expression = "java(userBookInfo.getBook().getAuthors().stream().map(a -> a.getName()).collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "imageUrl", source = "book.imgUrl")
    @Mapping(target = "userRating", source = "note")
    @Mapping(target = "totalVolumes", source = "book.nbVolume")
    @Mapping(target = "startedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    UserBookDto toUserBookDto(UserBookInfo userBookInfo);

    List<UserBookDto> toUserBookDtoList(List<UserBookInfo> userBookInfoList);
}
