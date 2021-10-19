package com.a2.backend.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.entity.Comment;
import com.a2.backend.exception.CommentNotFoundException;
import com.a2.backend.model.CommentCreateDTO;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.service.CommentService;
import com.a2.backend.service.UserService;
import java.util.UUID;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

public class CommentServiceActiveTest extends AbstractServiceTest {

    @Autowired private UserService userService;

    @Autowired private CommentService commentService;

    @Autowired private ProjectRepository projectRepository;

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void Test001_CommentServiceWhenReceivesCommentCreateDTOShouldCreateComment() {
        CommentCreateDTO commentCreateDTO = CommentCreateDTO.builder().comment("comment").build();

        Comment comment = commentService.createComment(commentCreateDTO);

        assertEquals("comment", comment.getComment());
        assertNotNull(comment.getDate());
        assertEquals(userService.getLoggedUser(), comment.getUser());
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void Test002_CommentServiceWhenReceiveNotValidCommentIdToHighlightShouldThrowException() {
        assertThrows(
                CommentNotFoundException.class,
                () -> commentService.changeHighlight(UUID.randomUUID()));
    }

    @Test
    @WithMockUser("agustin.ayerza@ing.austral.edu.ar")
    void Test003_CommentServiceWithHighlightedCommentWhenChangingHighlightShouldChangeToFalse() {
        val comment =
                projectRepository
                        .findByTitle("Django")
                        .get()
                        .getDiscussions()
                        .get(0)
                        .getComments()
                        .get(1);

        assertTrue(comment.isHighlighted());

        val updatedComment = commentService.changeHighlight(comment.getId());

        assertNotNull(updatedComment);
        assertEquals(updatedComment.getId(), comment.getId());
        assertFalse(comment.isHighlighted());
        assertEquals(comment.isHidden(), updatedComment.isHidden());
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void
            Test004_CommentServiceWithNotHighlightedCommentWhenChangingHighlightShouldChangeToTrueAndChangeHiddenWhenTrue() {
        val comment =
                projectRepository
                        .findByTitle("Kubernetes")
                        .get()
                        .getDiscussions()
                        .get(0)
                        .getComments()
                        .get(1);

        assertFalse(comment.isHighlighted());
        assertTrue(comment.isHidden());

        val updatedComment = commentService.changeHighlight(comment.getId());

        assertNotNull(updatedComment);
        assertEquals(updatedComment.getId(), comment.getId());
        assertTrue(comment.isHighlighted());
        assertFalse(comment.isHidden());
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void
            Test005_CommentServiceWithNotHighlightedCommentWhenChangingHighlightShouldChangeToTrueAndDontChangeHiddenWhenFalse() {
        val comment =
                projectRepository
                        .findByTitle("Kubernetes")
                        .get()
                        .getDiscussions()
                        .get(0)
                        .getComments()
                        .get(0);

        assertFalse(comment.isHighlighted());
        assertFalse(comment.isHidden());

        val updatedComment = commentService.changeHighlight(comment.getId());

        assertNotNull(updatedComment);
        assertEquals(updatedComment.getId(), comment.getId());
        assertTrue(comment.isHighlighted());
        assertFalse(comment.isHidden());
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void Test006_CommentServiceWhenReceiveNotValidCommentIdToHideShouldThrowException() {
        assertThrows(
                CommentNotFoundException.class,
                () -> commentService.changeHidden(UUID.randomUUID()));
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void Test007_CommentServiceWithHiddenCommentWhenChangingHiddenShouldChangeToFalse() {
        val comment =
                projectRepository
                        .findByTitle("Kubernetes")
                        .get()
                        .getDiscussions()
                        .get(0)
                        .getComments()
                        .get(1);

        assertTrue(comment.isHidden());

        val updatedComment = commentService.changeHidden(comment.getId());

        assertNotNull(updatedComment);
        assertEquals(updatedComment.getId(), comment.getId());
        assertFalse(comment.isHidden());
        assertEquals(comment.isHighlighted(), updatedComment.isHighlighted());
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void
            Test008_CommentServiceWithNotHiddenCommentWhenChangingHiddenShouldChangeToTrueAndChangeHighlightedWhenTrue() {
        val comment =
                projectRepository
                        .findByTitle("Django")
                        .get()
                        .getDiscussions()
                        .get(0)
                        .getComments()
                        .get(1);

        assertFalse(comment.isHidden());
        assertTrue(comment.isHighlighted());

        val updatedComment = commentService.changeHidden(comment.getId());

        assertNotNull(updatedComment);
        assertEquals(updatedComment.getId(), comment.getId());
        assertFalse(comment.isHighlighted());
        assertTrue(comment.isHidden());
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void
            Test009_CommentServiceWithNotHiddenCommentWhenChangingHiddenShouldChangeToTrueAndDontChangeHighlightWhenFalse() {
        val comment =
                projectRepository
                        .findByTitle("Kubernetes")
                        .get()
                        .getDiscussions()
                        .get(0)
                        .getComments()
                        .get(0);

        assertFalse(comment.isHighlighted());
        assertFalse(comment.isHidden());

        val updatedComment = commentService.changeHidden(comment.getId());

        assertNotNull(updatedComment);
        assertEquals(updatedComment.getId(), comment.getId());
        assertFalse(comment.isHighlighted());
        assertTrue(comment.isHidden());
    }
}
