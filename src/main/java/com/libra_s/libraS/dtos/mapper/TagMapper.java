package com.libra_s.libraS.dtos.mapper;

import com.libra_s.libraS.domain.Tag;
import com.libra_s.libraS.dtos.TagDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {})
public interface TagMapper extends EntityMapper<TagDto, Tag> {
    @Override
    Tag toEntity(TagDto dto);

    @Override
    TagDto toDto(Tag entity);

    @Override
    List<Tag> toEntity(List<TagDto> dtoList);

    @Override
    List<TagDto> toDto(List<Tag> entityList);
}

