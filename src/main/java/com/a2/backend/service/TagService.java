package com.a2.backend.service;

import com.a2.backend.entity.Tag;
import java.util.List;

public interface TagService {

    List<Tag> findOrCreateTag(List<String> tagsToAdd);

    Tag createTag(String tagName);
}
