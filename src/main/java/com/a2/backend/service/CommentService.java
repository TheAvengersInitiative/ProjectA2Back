package com.a2.backend.service;

import com.a2.backend.entity.Comment;
import com.a2.backend.model.CommentCreateDTO;

public interface CommentService {

    Comment createComment(CommentCreateDTO commentCreateDTO);
}
