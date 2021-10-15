package com.a2.backend.service;

import com.a2.backend.entity.Discussion;
import com.a2.backend.model.CommentCreateDTO;
import com.a2.backend.model.CommentDTO;
import com.a2.backend.model.DiscussionCreateDTO;

import java.util.UUID;

public interface DiscussionService {

    public Discussion createDiscussion(UUID projectId, DiscussionCreateDTO discussionCreateDTO);

    CommentDTO createComment(UUID discussionId, CommentCreateDTO commentCreateDTO);
}
