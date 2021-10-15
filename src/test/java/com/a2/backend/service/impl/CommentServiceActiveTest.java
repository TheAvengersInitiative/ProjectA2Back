package com.a2.backend.service.impl;

import com.a2.backend.entity.Comment;
import com.a2.backend.model.CommentCreateDTO;
import com.a2.backend.service.CommentService;
import com.a2.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CommentServiceActiveTest extends AbstractServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void Test001_CommentServiceWhenReceivesCommentCreateDTOShouldCreateComment() {
        CommentCreateDTO commentCreateDTO = CommentCreateDTO.builder().comment("comment").build();

        Comment comment = commentService.createComment(commentCreateDTO);

        assertEquals("comment", comment.getComment());
        assertNotNull(comment.getDate());
        assertEquals(userService.getLoggedUser(), comment.getUser());
    }
}
