package com.a2.backend.service.impl;

import com.a2.backend.entity.Tag;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.repository.TagRepository;
import com.a2.backend.service.TagService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    private final ProjectRepository projectRepository;

    public TagServiceImpl(TagRepository tagRepository, ProjectRepository projectRepository) {
        this.tagRepository = tagRepository;
        this.projectRepository = projectRepository;
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

    @Override
    public List<Tag> updateTags(List<String> updatedTags, List<Tag> currentTags) {
        List<Tag> removedTags = new ArrayList<>();

        for (Tag tag : currentTags) {
            if (!updatedTags.contains(tag.getName())) {
                removedTags.add(tag);
            }
        }

        deleteUnusedTags(removedTags);
        return findOrCreateTag(updatedTags);
    }

    @Override
    public void deleteUnusedTags(List<Tag> tags) {
        for (Tag tag : tags) {
            if (projectRepository.findProjectsByTagName(tag.getName()).isEmpty()) {
                tagRepository.deleteById(tag.getId());
            }
        }
    }
}
