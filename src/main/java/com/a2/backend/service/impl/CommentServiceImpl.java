package com.a2.backend.service.impl;

import com.a2.backend.entity.Comment;
import com.a2.backend.model.CommentCreateDTO;
import com.a2.backend.service.CommentService;
import com.a2.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentServiceImpl implements CommentService {
    private final UserService userService;

    public CommentServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Comment createComment(CommentCreateDTO commentCreateDTO) {
        return Comment.builder()
                .user(userService.getLoggedUser())
                .comment(commentCreateDTO.getComment())
                .date(LocalDateTime.now())
                .build();
    }
}
