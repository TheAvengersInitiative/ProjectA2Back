package com.a2.backend.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.entity.Comment;
import com.a2.backend.entity.Discussion;
import com.a2.backend.exception.DiscussionNotFoundException;
import com.a2.backend.exception.InvalidUserException;
import com.a2.backend.exception.UserIsNotOwnerException;
import com.a2.backend.model.CommentCreateDTO;
import com.a2.backend.model.CommentUpdateDTO;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.service.DiscussionService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

public class DiscussionServiceActiveTest extends AbstractServiceTest {

    @Autowired DiscussionService discussionService;

    // Use projectRepository to find discussions
    @Autowired ProjectRepository projectRepository;

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

    @Test
    @WithMockUser("agustin.ayerza@ing.austral.edu.ar")
    void Test005_GivenValidIDAndOwnerWhenWantToDeleteADiscussionThenDeleteDiscussion() {
        Discussion discussion =
                projectRepository.findByTitle("Django").get().getDiscussions().get(0);
        assertNotNull(discussion);
        discussionService.deleteDiscussion(discussion.getId());
        assertTrue(projectRepository.findByTitle("Django").get().getDiscussions().isEmpty());
    }

    @Test
    @WithMockUser("franz.sotoleal@ing.austral.edu.ar")
    void Test006_GivenAWrongOwnerWhenWantToDeleteADiscussionThenThrowException() {
        Discussion discussion =
                projectRepository.findByTitle("Django").get().getDiscussions().get(0);
        assertNotNull(discussion);
        assertThrows(
                UserIsNotOwnerException.class,
                () -> discussionService.deleteDiscussion(discussion.getId()));
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void Test007_GivenProjectOwnerWhenWantToDeleteADiscussionInHisProjectThenDeleteThatProject() {

        assertEquals(2, projectRepository.findByTitle("Kubernetes").get().getDiscussions().size());
        Discussion discussion =
                projectRepository.findByTitle("Kubernetes").get().getDiscussions().get(0);
        assertNotNull(discussion);
        discussionService.deleteDiscussion(discussion.getId());
        assertEquals(1, projectRepository.findByTitle("Kubernetes").get().getDiscussions().size());
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void
            Test008_DiscussionServiceWithInvalidCommentIdWhenHighlightingCommentShouldThrowException() {
        assertThrows(
                DiscussionNotFoundException.class,
                () -> discussionService.changeCommentHighlight(UUID.randomUUID()));
    }

    @Test
    @WithMockUser("agustin.ayerza@ing.austral.edu.ar")
    void
            Test009_DiscussionServiceWithValidCommentIdButNotProjectOwnerWhenHighlightingCommentShouldThrowException() {

        val comment =
                projectRepository
                        .findByTitle("Kubernetes")
                        .get()
                        .getDiscussions()
                        .get(0)
                        .getComments()
                        .get(0);

        assertThrows(
                InvalidUserException.class,
                () -> discussionService.changeCommentHighlight(comment.getId()));
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void Test010_DiscussionServiceWithValidCommentIdWhenHighlightingCommentShouldUpdateComment() {

        val comment =
                projectRepository
                        .findByTitle("Kubernetes")
                        .get()
                        .getDiscussions()
                        .get(0)
                        .getComments()
                        .get(0);

        assertFalse(comment.isHidden());
        assertFalse(comment.isHighlighted());

        val commentDto = discussionService.changeCommentHighlight(comment.getId());

        assertFalse(comment.isHidden());
        assertTrue(comment.isHighlighted());
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void Test011_DiscussionServiceWithInvalidCommentIdWhenHidingCommentShouldThrowException() {
        assertThrows(
                DiscussionNotFoundException.class,
                () -> discussionService.changeCommentHidden(UUID.randomUUID()));
    }

    @Test
    @WithMockUser("agustin.ayerza@ing.austral.edu.ar")
    void
            Test012_DiscussionServiceWithValidCommentIdButNotProjectOwnerWhenHidingCommentShouldThrowException() {

        val comment =
                projectRepository
                        .findByTitle("Kubernetes")
                        .get()
                        .getDiscussions()
                        .get(0)
                        .getComments()
                        .get(0);

        assertThrows(
                InvalidUserException.class,
                () -> discussionService.changeCommentHidden(comment.getId()));
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void Test013_DiscussionServiceWithValidCommentIdWhenHidingCommentShouldUpdateComment() {

        val comment =
                projectRepository
                        .findByTitle("Kubernetes")
                        .get()
                        .getDiscussions()
                        .get(0)
                        .getComments()
                        .get(0);

        assertFalse(comment.isHidden());
        assertFalse(comment.isHighlighted());

        val commentDto = discussionService.changeCommentHidden(comment.getId());

        assertTrue(comment.isHidden());
        assertFalse(comment.isHighlighted());
    }

    @Test
    @WithMockUser("agustin.ayerza@ing.austral.edu.ar")
    void
            Test014_DiscussionServiceWithNotValidDiscussionIdWhenGettingCommentsShouldThrowException() {

        assertThrows(
                DiscussionNotFoundException.class,
                () -> discussionService.getComments(UUID.randomUUID()));
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void
            Test015_DiscussionServiceWithValidCommentIdWhenGettingCommentsAsOwnerShouldReturnListWithAllComments() {

        val discussion = projectRepository.findByTitle("Kubernetes").get().getDiscussions().get(0);

        val comments = discussionService.getComments(discussion.getId());

        assertNotNull(comments);
        assertEquals(2, comments.size());
        assertTrue(comments.get(0).getDate().isBefore(comments.get(1).getDate()));
    }

    @Test
    @WithMockUser("agustin.ayerza@ing.austral.edu.ar")
    void
            Test016_DiscussionServiceWithValidCommentIdWhenGettingCommentsAsCollaboratorShouldReturnListWithFilteredComments() {

        val discussion = projectRepository.findByTitle("Kubernetes").get().getDiscussions().get(0);

        val comments = discussionService.getComments(discussion.getId());

        assertNotNull(comments);
        assertEquals(1, comments.size());
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void
            Test017_DiscussionServiceWithValidCommentIdWhenGettingCommentsAsCollaboratorShouldReturnFilteredCommentListInOrder() {

        val discussion = projectRepository.findByTitle("Django").get().getDiscussions().get(0);

        val comments = discussionService.getComments(discussion.getId());

        assertNotNull(comments);
        assertEquals(3, comments.size());
        assertTrue(comments.get(0).getComment().contains("The method handlers for a ViewSet"));
        assertTrue(comments.get(1).getComment().contains("The ViewSet class inherits"));
        assertTrue(comments.get(2).getComment().contains("A ViewSet class is simply"));
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void Test018_DiscussionServiceWhenDeletingCommentThenItIsDeleted() {
        Discussion discussion =
                projectRepository.findByTitle("Kubernetes").get().getDiscussions().get(0);

        String commentText = "Or maybe just a reboot first...";
        assertTrue(
                discussion.getComments().stream()
                        .map(Comment::getComment)
                        .collect(Collectors.toList())
                        .contains(commentText));

        discussionService.deleteComment(discussion.getComments().get(1).getId());

        assertFalse(
                discussion.getComments().stream()
                        .map(Comment::getComment)
                        .collect(Collectors.toList())
                        .contains(commentText));
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void Test019_DiscussionServiceWithInvalidCommentIdWhenDeletingCommentThenExceptionIsThrown() {
        assertThrows(
                DiscussionNotFoundException.class,
                () -> discussionService.deleteComment(UUID.randomUUID()));
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void Test020_DiscussionServiceWithInvalidUserWhenDeletingCommentThenExceptionIsThrown() {
        Discussion discussion =
                projectRepository.findByTitle("Django").get().getDiscussions().get(0);

        assertThrows(
                InvalidUserException.class,
                () -> discussionService.deleteComment(discussion.getComments().get(0).getId()));
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void Test021_DiscussionServiceWithValidCommentIdWhenUpdatingShouldReturnUpdatedComment() {

        val comment =
                projectRepository
                        .findByTitle("Kubernetes")
                        .get()
                        .getDiscussions()
                        .get(0)
                        .getComments()
                        .get(1);

        CommentUpdateDTO commentUpdateDTO =
                CommentUpdateDTO.builder().comment("updated comment").build();

        assertEquals("Or maybe just a reboot first...", comment.getComment());
        discussionService.updateComment(comment.getId(), commentUpdateDTO);
        assertEquals("updated comment", comment.getComment());
    }
}
