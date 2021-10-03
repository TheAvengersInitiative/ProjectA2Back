package com.a2.backend.service.impl;

import com.a2.backend.entity.ForumTag;
import com.a2.backend.repository.ForumTagRepository;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.service.ForumTagService;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ForumTagServiceImpl implements ForumTagService {

    private final ForumTagRepository forumTagRepository;

    private final ProjectRepository projectRepository;

    public ForumTagServiceImpl(
            ForumTagRepository forumTagRepository, ProjectRepository projectRepository) {
        this.forumTagRepository = forumTagRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public List<ForumTag> findOrCreateTag(List<String> forumTagsToAdd) {
        List<ForumTag> tagList = new ArrayList<>();

        for (String tagName : forumTagsToAdd) {
            Optional<ForumTag> optionalTag = findTagByName(tagName);

            if (optionalTag.isEmpty()) {
                tagList.add(createTag(tagName));
            } else {
                tagList.add(optionalTag.get());
            }
        }
        return tagList;
    }

    @Override
    public ForumTag createTag(String tagName) {
        return ForumTag.builder().name(tagName).build();
    }

    @Override
    public Optional<ForumTag> findTagByName(String forumTagName) {
        return forumTagRepository.findByName(forumTagName);
    }

    @Override
    public List<ForumTag> findTagsByNames(List<String> forumTagsToFind) {
        List<ForumTag> tagsFound = new ArrayList<>();

        for (String tagName : forumTagsToFind) {
            Optional<ForumTag> optionalForumTag = findTagByName(tagName);
            optionalForumTag.ifPresent(tagsFound::add);
        }
        return tagsFound;
    }

    @Override
    public List<ForumTag> getRemovedTags(List<String> updated, List<ForumTag> current) {
        List<ForumTag> removedTags = new ArrayList<>();

        for (ForumTag oldTag : current) {
            if (!updated.contains(oldTag.getName())) {
                removedTags.add(oldTag);
            }
        }
        return removedTags;
    }

    @Override
    public void deleteUnusedTags(List<ForumTag> removedTags) {
        for (ForumTag ForumTag : removedTags) {
            if (projectRepository
                    .findProjectsByTagName(ForumTag.getName().toUpperCase(Locale.ROOT))
                    .isEmpty()) {
                forumTagRepository.deleteById(ForumTag.getId());
            }
        }
    }

    @Override
    public List<ForumTag> getAllTags() {
        return forumTagRepository.findAll();
    }
}
