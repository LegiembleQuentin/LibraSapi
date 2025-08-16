package com.libra_s.libraS.dtos.mapper;

import com.libra_s.libraS.domain.AppUser;
import com.libra_s.libraS.dtos.AdminUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdminUserMapper {

    @Mapping(target = "displayName", source = "displayname")
    @Mapping(target = "profileImageUrl", source = "img_url")
    @Mapping(target = "createdAt", source = "created_at")
    @Mapping(target = "modifiedAt", source = "modified_at")
    @Mapping(target = "totalBooks", ignore = true)
    @Mapping(target = "booksInProgress", ignore = true)
    @Mapping(target = "booksCompleted", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    AdminUserDto toAdminDto(AppUser entity);

    List<AdminUserDto> toAdminDtoList(List<AppUser> entityList);
}