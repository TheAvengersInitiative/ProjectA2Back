package com.a2.backend.service;

import com.a2.backend.entity.ForumTag;
import java.util.List;
import java.util.Optional;

public interface ForumTagService {

    List<ForumTag> findOrCreateTag(List<String> forumTagsToAdd);

    ForumTag createTag(String tagName);

    Optional<ForumTag> findTagByName(String forumTagName);

    List<ForumTag> findTagsByNames(List<String> forumTagsToFind);

    List<ForumTag> getRemovedTags(List<String> updated, List<ForumTag> current);

    void deleteUnusedTags(List<ForumTag> removedTags);

    List<ForumTag> getAllTags();
}
