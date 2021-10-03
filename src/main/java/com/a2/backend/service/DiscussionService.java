package com.a2.backend.service;

import com.a2.backend.entity.Discussion;
import com.a2.backend.model.DiscussionCreateDTO;
import java.util.UUID;

public interface DiscussionService {

    public Discussion createDiscussion(UUID projectId, DiscussionCreateDTO discussionCreateDTO);
}
