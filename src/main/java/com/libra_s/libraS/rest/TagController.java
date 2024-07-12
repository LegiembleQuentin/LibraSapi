package com.libra_s.libraS.rest;

import com.libra_s.libraS.dtos.TagDto;
import com.libra_s.libraS.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api")
@RestController
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/tags")
    public ResponseEntity<List<TagDto>> getTags() {
        List<TagDto> result = tagService.getTags();
        return ResponseEntity.ok(result);
    }
}
