package com.a2.backend.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.constants.NotificationType;
import com.a2.backend.entity.*;
import com.a2.backend.exception.DiscussionNotFoundException;
import com.a2.backend.exception.InvalidUserException;
import com.a2.backend.exception.UserIsNotOwnerException;
import com.a2.backend.model.CommentCreateDTO;
import com.a2.backend.model.CommentUpdateDTO;
import com.a2.backend.model.DiscussionCreateDTO;
import com.a2.backend.repository.NotificationRepository;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.service.DiscussionService;
import com.a2.backend.service.UserService;
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

    @Autowired NotificationRepository notificationRepository;

    @Autowired UserService userService;

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

//    @Test
//    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
//    void
//            Test004_DiscussionServiceWithValidCommentCreateDTOWhenAddingCommentThenCommentListShouldBeSortedByDate() {
//        Discussion discussion =
//                projectRepository.findByTitle("Kubernetes").get().getDiscussions().get(0);
//
//        List<Comment> comments = discussion.getComments();
//        assertEquals(2, comments.size());
//
//        discussionService.createComment(
//                discussion.getId(), CommentCreateDTO.builder().comment("test comment").build());
//
//        assertEquals(3, comments.size());
//
//        assertTrue(comments.get(0).getDate().isBefore(comments.get(1).getDate()));
//        assertTrue(comments.get(1).getDate().isBefore(comments.get(2).getDate()));
//    }

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
//        assertTrue(comments.get(0).getDate().isBefore(comments.get(1).getDate()));
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

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void
            Test022_DiscussionServiceWhenCreatingDiscussionThenOtherCollaboratorsAndOwnerAreNotified() {
        val loggedUser = userService.getLoggedUser();

        DiscussionCreateDTO discussionCreateDTO =
                DiscussionCreateDTO.builder()
                        .title("title")
                        .body("this is a discussion body")
                        .forumTags(List.of("help", "test"))
                        .build();

        val project = projectRepository.findByTitle("Node.js").get();

        User peltevis =
                project.getCollaborators().stream()
                        .filter(u -> u.getNickname().equals("Peltevis"))
                        .collect(Collectors.toList())
                        .get(0);

        val notificationsForOwnerBeforeCreatingDiscussion =
                notificationRepository.findAllByUserToNotify(project.getOwner());

        val notificationsForCollaboratorBeforeCreatingDiscussion =
                notificationRepository.findAllByUserToNotify(peltevis);

        val notificationsForDiscussionCreatorBeforeCreatingDiscussion =
                notificationRepository.findAllByUserToNotify(loggedUser);

        val discussion = discussionService.createDiscussion(project.getId(), discussionCreateDTO);

        val notificationsForOwner =
                notificationRepository.findAllByUserToNotify(project.getOwner());

        val notificationsForCollaborator = notificationRepository.findAllByUserToNotify(peltevis);

        val notificationsForDiscussionCreator =
                notificationRepository.findAllByUserToNotify(loggedUser);

        assertEquals(
                notificationsForOwnerBeforeCreatingDiscussion.size(),
                notificationsForOwner.size() - 1);

        assertEquals(
                notificationsForCollaboratorBeforeCreatingDiscussion.size(),
                notificationsForCollaborator.size() - 1);

        assertEquals(
                notificationsForDiscussionCreatorBeforeCreatingDiscussion.size(),
                notificationsForDiscussionCreator.size());

        val notificationForCollaborator =
                notificationsForCollaborator.get(notificationsForCollaborator.size() - 1);

        assertNotNull(notificationForCollaborator.getId());
        assertEquals(peltevis, notificationForCollaborator.getUserToNotify());
        assertEquals(NotificationType.DISCUSSION, notificationForCollaborator.getType());
        assertEquals(project, notificationForCollaborator.getProject());
        assertEquals(loggedUser, notificationForCollaborator.getUser());
        assertEquals(discussion.getId(), notificationForCollaborator.getDiscussion().getId());
        assertNull(notificationForCollaborator.getComment());
        assertFalse(notificationForCollaborator.isSeen());
    }

    @Test
    @WithMockUser("agustin.ayerza@ing.austral.edu.ar")
    void
            Test023_DiscussionServiceWhenCreatingCommentOnDiscussionThenProjectOwnerAndDiscussionOwnerAreNotified() {
        User loggedUser = userService.getLoggedUser();
        Project project = projectRepository.findByTitle("Kubernetes").get();
        Discussion discussion = project.getDiscussions().get(0);

        List<Notification> notificationsBeforeComment =
                notificationRepository.findAllByUserToNotify(discussion.getOwner());

        discussionService.createComment(
                discussion.getId(), CommentCreateDTO.builder().comment("test comment").build());

        Comment comment = discussion.getComments().get(discussion.getComments().size() - 1);
        assertEquals("test comment", comment.getComment());
        assertEquals(loggedUser, comment.getUser());

        List<Notification> notifications =
                notificationRepository.findAllByUserToNotify(discussion.getOwner());

        assertEquals(notificationsBeforeComment.size(), notifications.size() - 1);

        Notification notification = notifications.get(notifications.size() - 1);

        assertNotNull(notification.getId());
        assertEquals(NotificationType.COMMENT, notification.getType());
        assertEquals(project, notification.getProject());
        assertEquals(loggedUser, notification.getUser());
        assertEquals(discussion, notification.getDiscussion());
        assertEquals(comment, notification.getComment());
        assertFalse(notification.isSeen());
    }

    @Test
    @WithMockUser("agustin.ayerza@ing.austral.edu.ar")
    void
            Test024_DiscussionServiceWhenCreatingCommentOnDiscussionWhereCommentCreatorIsDiscussionAndProjectOwnerThenNoOneIsNotified() {
        User loggedUser = userService.getLoggedUser();
        Project project = projectRepository.findByTitle("Django").get();
        Discussion discussion = project.getDiscussions().get(0);

        List<Notification> notificationsBeforeCreatingComment =
                notificationRepository.findAllByUserToNotify(loggedUser);

        discussionService.createComment(
                discussion.getId(), CommentCreateDTO.builder().comment("test comment").build());

        List<Notification> notifications = notificationRepository.findAllByUserToNotify(loggedUser);

        assertEquals(loggedUser, project.getOwner());
        assertEquals(loggedUser, discussion.getOwner());
        assertEquals(notificationsBeforeCreatingComment.size(), notifications.size());
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void
            Test025_DiscussionServiceWhenCreatingDiscussionWhereDiscussionCreatorIsProjectOwnerAndThereAreNoCollaboratorsThenNoOneIsNotified() {
        User loggedUser = userService.getLoggedUser();
        Project project = projectRepository.findByTitle("Renovate").get();

        List<Notification> notificationsBeforeCreatingDiscussion =
                notificationRepository.findAllByUserToNotify(loggedUser);

        discussionService.createDiscussion(
                project.getId(),
                DiscussionCreateDTO.builder()
                        .title("discussion")
                        .body("discussion body")
                        .forumTags(List.of("help"))
                        .build());

        List<Notification> notifications = notificationRepository.findAllByUserToNotify(loggedUser);

        assertEquals(loggedUser, project.getOwner());
        assertTrue(project.getCollaborators().isEmpty());
        assertEquals(notificationsBeforeCreatingDiscussion.size(), notifications.size());
    }
}
