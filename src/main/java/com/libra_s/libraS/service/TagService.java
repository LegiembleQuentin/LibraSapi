package com.libra_s.libraS.service;

import com.libra_s.libraS.domain.Tag;
import com.libra_s.libraS.dtos.TagDto;
import com.libra_s.libraS.dtos.mapper.TagMapper;
import com.libra_s.libraS.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;

    private final TagMapper tagMapper;

    public TagService(TagRepository tagRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    public List<TagDto> getTags() {
        List<Tag> tags = tagRepository.findAll();

        return tags.stream()
                .map(tagMapper::toDto)
                .collect(java.util.stream.Collectors.toList());

    }

}
