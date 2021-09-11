package com.a2.backend.service;

import com.a2.backend.entity.Tag;

import java.util.List;
import java.util.Optional;

public interface TagService {

    List<Tag> findOrCreateTag(List<String> tagsToAdd);

    Tag createTag(String tagName);

    Optional<Tag> findTag(String tagName);

    List<Tag> findTags(List<String> tagsToFind);
}
