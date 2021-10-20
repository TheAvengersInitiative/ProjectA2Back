package com.a2.backend.service;

import com.a2.backend.model.*;
import java.util.List;
import java.util.UUID;

public interface DiscussionService {

    CommentDTO createComment(UUID discussionId, CommentCreateDTO commentCreateDTO);

    DiscussionDTO createDiscussion(UUID projectId, DiscussionCreateDTO discussionCreateDTO);

    DiscussionDTO updateDiscussion(UUID discussionId, DiscussionUpdateDTO discussionUpdateDTO);

    DiscussionDTO getDiscussionDetails(UUID discussionID);

    void deleteDiscussion(UUID discussionID);

    CommentDTO changeCommentHighlight(UUID commentId);

    CommentDTO changeCommentHidden(UUID commentId);

    List<CommentDTO> getComments(UUID discussionId);

    void deleteComment(UUID id);
}
