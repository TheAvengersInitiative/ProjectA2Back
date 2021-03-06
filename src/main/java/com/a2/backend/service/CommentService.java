package com.a2.backend.service;

import com.a2.backend.entity.Comment;
import com.a2.backend.model.CommentCreateDTO;
import com.a2.backend.model.CommentUpdateDTO;
import java.util.UUID;

public interface CommentService {

    Comment createComment(CommentCreateDTO commentCreateDTO);

    Comment changeHighlight(UUID commentId);

    Comment changeHidden(UUID commentId);

    Comment deleteComment(UUID id);

    Comment updateComment(UUID commentId, CommentUpdateDTO commentUpdateDTO);
}
