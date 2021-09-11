package com.a2.backend.service.impl;

import com.a2.backend.entity.Tag;
import com.a2.backend.repository.TagRepository;
import com.a2.backend.service.TagService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



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
            Optional<Tag> optionalTag = findTag(tagName);

            if (optionalTag.isEmpty()) {
                tagList.add(createTag(tagName));
            } else {
                tagList.add(optionalTag.get());
            }
        }
        return tagList;
    }

    @Override
    public Tag createTag(String tagName) {
        return Tag.builder().name(tagName).build();
    }

    @Override
    public Optional<Tag> findTag(String tagName) {
        return tagRepository.findByName(tagName);
    }

    @Override
    public List<Tag> findTags(List<String> tagsToFind) {
        List<Tag> tagsFound = new ArrayList<>();

        for (String tagName : tagsToFind) {
            Optional<Tag> optionalTag = findTag(tagName);
            optionalTag.ifPresent(tagsFound::add);
        }
        return tagsFound;
    }
}
