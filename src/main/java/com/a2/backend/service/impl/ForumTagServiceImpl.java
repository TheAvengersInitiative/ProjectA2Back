package com.a2.backend.service.impl;

import com.a2.backend.entity.ForumTag;
import com.a2.backend.repository.DiscussionRepository;
import com.a2.backend.repository.ForumTagRepository;
import com.a2.backend.service.ForumTagService;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ForumTagServiceImpl implements ForumTagService {

    private final ForumTagRepository forumTagRepository;

    private final DiscussionRepository discussionRepository;

    public ForumTagServiceImpl(
            ForumTagRepository forumTagRepository, DiscussionRepository discussionRepository) {
        this.forumTagRepository = forumTagRepository;
        this.discussionRepository = discussionRepository;
    }

    @Override
    public List<ForumTag> createTag(List<String> forumTagsToAdd) {
        List<ForumTag> tagList = new ArrayList<>();

        for (String tagName : forumTagsToAdd) {
            tagList.add(createTag(tagName));
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
            if (discussionRepository
                    .findDiscussionsByTagName(ForumTag.getName().toUpperCase(Locale.ROOT))
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
