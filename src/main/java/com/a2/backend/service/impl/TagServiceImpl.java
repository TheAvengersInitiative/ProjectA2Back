package com.a2.backend.service.impl;

import com.a2.backend.entity.Tag;
import com.a2.backend.repository.TagRepository;
import com.a2.backend.service.TagService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> findOrCreateTag(List<String> tagsToAdd) {
        List<Tag> tagList = new ArrayList<>();

        for (String tagName : tagsToAdd) {
            if (tagRepository.findByName(tagName).isEmpty()) {
                tagList.add(createTag(tagName));
            } else {
                tagList.add(tagRepository.findByName(tagName).get());
            }
        }
        return tagList;
    }

    @Override
    public Tag createTag(String tagName) {
        return Tag.builder().name(tagName).build();
    }
}
