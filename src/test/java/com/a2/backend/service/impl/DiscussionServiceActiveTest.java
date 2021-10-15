package com.a2.backend.service.impl;

import com.a2.backend.entity.Comment;
import com.a2.backend.entity.Discussion;
import com.a2.backend.exception.DiscussionNotFoundException;
import com.a2.backend.exception.InvalidUserException;
import com.a2.backend.model.CommentCreateDTO;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.service.DiscussionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DiscussionServiceActiveTest extends AbstractServiceTest {

    @Autowired
    DiscussionService discussionService;

    // Use these to find discussions
    @Autowired
    ProjectRepository projectRepository;

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void
    Test001_DiscussionServiceWithValidCommentCreateDTOWhenAddingCommentShouldUpdateCommentListOnDiscussion() {
        Discussion discussion =
                projectRepository.findByTitle("TensorFlow").get().getDiscussions().get(0);

        assertEquals(0, discussion.getComments().size());

        discussionService.createComment(
                discussion.getId(), CommentCreateDTO.builder().comment("test comment").build());

        assertEquals(1, discussion.getComments().size());

        Comment comment = discussion.getComments().get(0);
        assertEquals("test comment", comment.getComment());
        assertEquals("rodrigo.pazos@ing.austral.edu.ar", comment.getUser().getEmail());
        assertNotNull(comment.getDate());
        assertNotNull(comment.getId());
    }

    @Test
    @WithMockUser("agustin.ayerza@ing.austral.edu.ar")
    void
    Test002_DiscussionServiceWhenAddingCommentAsNeitherOwnerNorCollaboratorShouldThrowException() {
        Discussion discussion =
                projectRepository.findByTitle("TensorFlow").get().getDiscussions().get(0);

        assertThrows(
                InvalidUserException.class,
                () ->
                        discussionService.createComment(
                                discussion.getId(),
                                CommentCreateDTO.builder().comment("test comment").build()));
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void Test003_DiscussionServiceWithInvalidDiscussionIdWhenAddingCommentShouldThrowException() {
        assertThrows(
                DiscussionNotFoundException.class,
                () ->
                        discussionService.createComment(
                                UUID.randomUUID(),
                                CommentCreateDTO.builder().comment("test comment").build()));
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void
    Test004_DiscussionServiceWithValidCommentCreateDTOWhenAddingCommentThenCommentListShouldBeSortedByDate() {
        Discussion discussion =
                projectRepository.findByTitle("Kubernetes").get().getDiscussions().get(0);

        List<Comment> comments = discussion.getComments();
        assertEquals(2, comments.size());

        discussionService.createComment(
                discussion.getId(), CommentCreateDTO.builder().comment("test comment").build());

        assertEquals(3, comments.size());

        assertTrue(comments.get(0).getDate().isBefore(comments.get(1).getDate()));
        assertTrue(comments.get(1).getDate().isBefore(comments.get(2).getDate()));
    }
}
