package com.a2.backend.service.impl;

import com.a2.backend.entity.Comment;
import com.a2.backend.entity.User;
import com.a2.backend.exception.CommentNotFoundException;
import com.a2.backend.exception.InvalidUserException;
import com.a2.backend.model.CommentCreateDTO;
import com.a2.backend.repository.CommentRepository;
import com.a2.backend.service.CommentService;
import com.a2.backend.service.UserService;
import lombok.val;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {
    private final UserService userService;

    private final CommentRepository commentRepository;

    public CommentServiceImpl(UserService userService, CommentRepository commentRepository) {
        this.userService = userService;
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment createComment(CommentCreateDTO commentCreateDTO) {
        return Comment.builder()
                .user(userService.getLoggedUser())
                .comment(commentCreateDTO.getComment())
                .date(LocalDateTime.now())
                .hidden(false)
                .highlighted(false)
                .build();
    }

    @Override
    public Comment changeHighlight(UUID commentId) {
        val commentOptional = commentRepository.findById(commentId);

        if (commentOptional.isEmpty()) {
            throw new CommentNotFoundException(
                    String.format("Comment with id: %s not found", commentId));
        }

        val comment = commentOptional.get();

        comment.setHighlighted(!comment.isHighlighted());

        if (comment.isHighlighted() && comment.isHidden()) {
            comment.setHidden(!comment.isHidden());
        }

        return comment;
    }

    @Override
    public Comment changeHidden(UUID commentId) {
        val commentOptional = commentRepository.findById(commentId);

        if (commentOptional.isEmpty()) {
            throw new CommentNotFoundException(
                    String.format("Comment with id: %s not found", commentId));
        }

        val comment = commentOptional.get();

        comment.setHidden(!comment.isHidden());

        if (comment.isHidden() && comment.isHighlighted()) {
            comment.setHighlighted(!comment.isHighlighted());
        }

        return comment;
    }

    @Override
    public Comment deleteComment(UUID id) {
        User loggedUser = userService.getLoggedUser();
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isEmpty())
            throw new CommentNotFoundException(String.format("Comment with id: %s not found", id));
        Comment comment = optionalComment.get();
        if (!comment.getUser().equals(loggedUser))
            throw new InvalidUserException("Only comment owners can delete comments");
        commentRepository.deleteById(id);
        return comment;
    }
}
