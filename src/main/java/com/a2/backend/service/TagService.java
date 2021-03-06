package com.a2.backend.service;

import com.a2.backend.entity.Tag;
import java.util.List;
import java.util.Optional;

public interface TagService {

    List<Tag> findOrCreateTag(List<String> tagsToAdd);

    Tag createTag(String tagName);

    Optional<Tag> findTagByName(String tagName);

    List<Tag> findTagsByNames(List<String> tagsToFind);

    List<Tag> getRemovedTags(List<String> updated, List<Tag> current);

    void deleteUnusedTags(List<Tag> removedTags);

    List<Tag> getAllTags();
}
