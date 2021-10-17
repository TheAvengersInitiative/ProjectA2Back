package com.a2.backend.service;

import com.a2.backend.model.CommentCreateDTO;
import com.a2.backend.model.CommentDTO;
import com.a2.backend.model.DiscussionCreateDTO;
import com.a2.backend.model.DiscussionDTO;
import com.a2.backend.model.DiscussionUpdateDTO;
import java.util.UUID;

public interface DiscussionService {

    CommentDTO createComment(UUID discussionId, CommentCreateDTO commentCreateDTO);

    public DiscussionDTO createDiscussion(UUID projectId, DiscussionCreateDTO discussionCreateDTO);

    public DiscussionDTO updateDiscussion(
            UUID discussionId, DiscussionUpdateDTO discussionUpdateDTO);

    public DiscussionDTO getDiscussionDetails(UUID discussionID);
}
