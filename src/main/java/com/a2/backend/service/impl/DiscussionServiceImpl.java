package com.a2.backend.service.impl;

import com.a2.backend.constants.NotificationType;
import com.a2.backend.entity.Comment;
import com.a2.backend.entity.Discussion;
import com.a2.backend.entity.ForumTag;
import com.a2.backend.entity.User;
import com.a2.backend.exception.*;
import com.a2.backend.model.*;
import com.a2.backend.repository.CommentRepository;
import com.a2.backend.repository.DiscussionRepository;
import com.a2.backend.repository.NotificationRepository;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.service.*;
import java.util.*;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DiscussionServiceImpl implements DiscussionService {

    private final ProjectRepository projectRepository;
    private final DiscussionRepository discussionRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;
    private final ForumTagService forumTagService;
    private final UserService userService;
    private final CommentService commentService;
    private final NotificationService notificationService;

    public DiscussionServiceImpl(
            ProjectRepository projectRepository,
            DiscussionRepository discussionRepository,
            CommentRepository commentRepository,
            NotificationRepository notificationRepository,
            ForumTagService forumTagService,
            UserService userService,
            CommentService commentService,
            NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.notificationRepository = notificationRepository;
        this.forumTagService = forumTagService;
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.discussionRepository = discussionRepository;
        this.commentService = commentService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public DiscussionDTO createDiscussion(UUID projectId, DiscussionCreateDTO discussionCreateDTO) {
        User loggedUser = userService.getLoggedUser();
        val project = projectRepository.findById(projectId);
        if (project.isEmpty()) {
            throw new ProjectNotFoundException("Project not found with that ID");
        }
        if (!project.get().getCollaborators().contains(loggedUser)
                && !project.get().getOwner().getId().equals(loggedUser.getId())) {
            throw new UserIsNotCollaboratorNorOwnerException(
                    "User must be collaborator or owner to create a discussion");
        }

        val existingDiscussionWithTitleInProject =
                discussionRepository.findByProject_IdAndTitleAndIsActiveIsTrue(
                        projectId, discussionCreateDTO.getTitle());
        if (existingDiscussionWithTitleInProject == null) {
            List<ForumTag> tags = forumTagService.createTag(discussionCreateDTO.getForumTags());
            Discussion discussion =
                    Discussion.builder()
                            .title(discussionCreateDTO.getTitle())
                            .project(projectRepository.findById(projectId).get())
                            .forumTags(tags)
                            .comments(List.of())
                            .body(discussionCreateDTO.getBody())
                            .owner(loggedUser)
                            .isActive(true)
                            .build();
            val discussions = project.get().getDiscussions();
            discussions.add(discussion);
            project.get().setDiscussions(discussions);
            Discussion createdDiscussion = discussionRepository.save(discussion);
            projectRepository.save(project.get());

            List<User> toNotify = new ArrayList<>(project.get().getCollaborators());
            toNotify.remove(loggedUser);
            if (loggedUser != project.get().getOwner()) toNotify.add(project.get().getOwner());
            if (!toNotify.isEmpty()) {
                for (User userToNotify : toNotify) {
                    NotificationCreateDTO notificationCreateDTO =
                            NotificationCreateDTO.builder()
                                    .type(NotificationType.DISCUSSION)
                                    .discussion(discussion)
                                    .project(project.get())
                                    .user(loggedUser)
                                    .userToNotify(userToNotify)
                                    .build();
                    notificationService.createNotification(notificationCreateDTO);
                }
            }
            return createdDiscussion.toDTO();
        }

        throw new DiscussionWithThatTitleExistsInProjectException(
                String.format(
                        "There is an existing discussion named %s in this project",
                        discussionCreateDTO.getTitle()));
    }

    @Override
    public CommentDTO createComment(UUID discussionId, CommentCreateDTO commentCreateDTO) {
        User loggedUser = userService.getLoggedUser();
        val discussionOptional = discussionRepository.findById(discussionId);

        if (discussionOptional.isEmpty()) {
            throw new DiscussionNotFoundException(
                    String.format("The discussion with id: %s does not exist!", discussionId));
        }

        val discussion = discussionOptional.get();
        val project = discussion.getProject();

        if (!project.getOwner().equals(loggedUser)
                && !project.getCollaborators().contains(loggedUser)) {
            throw new InvalidUserException(
                    "Only project owners and collaborators can submit comments");
        }

        val comments = discussionOptional.get().getComments();
        val comment = commentService.createComment(commentCreateDTO);
        comments.add(comment);

        discussion.setComments(comments);
        discussionRepository.save(discussion);

        List<User> toNotify = new ArrayList<>();
        if (loggedUser != project.getOwner()) toNotify.add(project.getOwner());
        if (loggedUser != discussion.getOwner() && discussion.getOwner() != project.getOwner())
            toNotify.add(discussion.getOwner());

        if (!toNotify.isEmpty()) {
            for (User userToNotify : toNotify) {
                NotificationCreateDTO notificationCreateDTO =
                        NotificationCreateDTO.builder()
                                .type(NotificationType.COMMENT)
                                .comment(
                                        discussion
                                                .getComments()
                                                .get(discussion.getComments().size() - 1))
                                .discussion(discussion)
                                .project(project)
                                .user(loggedUser)
                                .userToNotify(userToNotify)
                                .build();
                notificationService.createNotification(notificationCreateDTO);
            }
        }

        return comment.toDTO();
    }

    public DiscussionDTO updateDiscussion(
            UUID discussionID, DiscussionUpdateDTO discussionUpdateDTO) {
        User loggedUser = userService.getLoggedUser();

        val discussionToModifyOptional = discussionRepository.findById(discussionID);
        if (discussionToModifyOptional.isEmpty()) {
            throw new DiscussionNotFoundException(
                    String.format("Discussion with id %s does not exist!", discussionID));
        }
        if (!discussionToModifyOptional.get().getOwner().getId().equals(loggedUser.getId())) {
            throw new UserIsNotOwnerException("User must be the discussion owner to modify it");
        }

        val existingDiscussionWithTitleInProject =
                discussionRepository.findByProject_IdAndTitleAndIsActiveIsTrue(
                        discussionToModifyOptional.get().getProject().getId(),
                        discussionUpdateDTO.getTitle());

        if (existingDiscussionWithTitleInProject != null
                && !existingDiscussionWithTitleInProject
                        .getTitle()
                        .equals(discussionToModifyOptional.get().getTitle()))
            throw new DiscussionWithThatTitleExistsInProjectException(
                    String.format(
                            "There is an existing discussion named %s",
                            discussionUpdateDTO.getTitle()));

        List<ForumTag> removedForumTags =
                forumTagService.getRemovedTags(
                        discussionUpdateDTO.getForumTags(),
                        getDiscussionDetails(discussionID).getForumTags());

        val discussion = discussionToModifyOptional.get();
        discussion.setTitle(discussionUpdateDTO.getTitle());
        discussion.setBody(discussionUpdateDTO.getBody());
        discussion.setForumTags(forumTagService.createTag(discussionUpdateDTO.getForumTags()));
        Discussion updatedDiscussion = discussionRepository.save(discussion);
        forumTagService.deleteUnusedTags(removedForumTags);
        return updatedDiscussion.toDTO();
    }

    @Override
    public DiscussionDTO getDiscussionDetails(UUID discussionID) {
        return discussionRepository
                .findById(discussionID)
                .map(Discussion::toDTO)
                .orElseThrow(
                        () ->
                                new DiscussionNotFoundException(
                                        String.format(
                                                "No discussion found for id: %s", discussionID)));
    }

    @Override
    public void deleteDiscussion(UUID discussionID) {
        val loggedUser = userService.getLoggedUser();
        val discussionToDelete = discussionRepository.findById(discussionID);
        val project = projectRepository.findById(discussionToDelete.get().getProject().getId());
        if (discussionToDelete.isEmpty()) {
            throw new DiscussionNotFoundException("Discussion not found!");
        }
        if (!discussionRepository.getById(discussionID).getOwner().equals(loggedUser)
                && !project.get().getOwner().equals(loggedUser)) {
            throw new UserIsNotOwnerException(
                    String.format("User: %s is not the owner!", loggedUser.getNickname()));
        }
        if (project.isEmpty()) {
            throw new ProjectNotFoundException("Project does not exist!");
        }
        discussionToDelete.get().setActive(false);
        discussionRepository.save(discussionToDelete.get());
    }

    @Override
    public CommentDTO changeCommentHighlight(UUID commentId) {
        User loggedUser = userService.getLoggedUser();
        val discussionOptional = discussionRepository.findDiscussionByCommentId(commentId);

        if (discussionOptional.isEmpty()) {
            throw new DiscussionNotFoundException(
                    String.format("Discussion with comment id: %s not found", commentId));
        }

        val discussion = discussionOptional.get();
        val project = discussion.getProject();

        if (!project.getOwner().equals(loggedUser)) {
            throw new InvalidUserException("Only project owners can highlight comments");
        }

        val comments = discussionOptional.get().getComments();

        val updatedComment = commentService.changeHighlight(commentId);

        discussion.setComments(comments);
        discussionRepository.save(discussion);

        return updatedComment.toDTO();
    }

    @Override
    public CommentDTO changeCommentHidden(UUID commentId) {
        User loggedUser = userService.getLoggedUser();
        val discussionOptional = discussionRepository.findDiscussionByCommentId(commentId);

        if (discussionOptional.isEmpty()) {
            throw new DiscussionNotFoundException(
                    String.format("Discussion with comment id: %s not found", commentId));
        }

        val discussion = discussionOptional.get();
        val project = discussion.getProject();

        if (!project.getOwner().equals(loggedUser)) {
            throw new InvalidUserException("Only project owners can hide comments");
        }

        val comments = discussionOptional.get().getComments();

        val updatedComment = commentService.changeHidden(commentId);

        discussion.setComments(comments);
        discussionRepository.save(discussion);

        return updatedComment.toDTO();
    }

    @Override
    public List<CommentDTO> getComments(UUID discussionId) {
        User loggedUser = userService.getLoggedUser();
        val discussionOptional = discussionRepository.findById(discussionId);

        if (discussionOptional.isEmpty()) {
            throw new DiscussionNotFoundException(
                    String.format("The discussion with id: %s does not exist!", discussionId));
        }

        val discussion = discussionOptional.get();
        val project = discussion.getProject();

        List<CommentDTO> comments =
                discussionOptional.get().getComments().stream()
                        .filter(Comment::isActive)
                        .map(Comment::toDTO)
                        .collect(Collectors.toList());

        if (project.getOwner().equals(loggedUser)) {

            comments.sort(Comparator.comparing(CommentDTO::getDate));
            return comments;

        } else {
            List<CommentDTO> highlighted =
                    comments.stream()
                            .filter(CommentDTO::isHighlighted)
                            .collect(Collectors.toList());
            highlighted.sort(Comparator.comparing(CommentDTO::getDate));
            List<CommentDTO> others =
                    comments.stream()
                            .filter(c -> !c.isHighlighted() && !c.isHidden())
                            .collect(Collectors.toList());
            others.sort(Comparator.comparing(CommentDTO::getDate));

            highlighted.addAll(others);

            return highlighted;
        }
    }

    @Override
    public void deleteComment(UUID id) {
        val discussionOptional = discussionRepository.findDiscussionByCommentId(id);

        if (discussionOptional.isEmpty()) {
            throw new DiscussionNotFoundException(
                    String.format("Discussion with comment id: %s not found", id));
        }

        val discussion = discussionOptional.get();
        val comments = discussion.getComments();

        Comment comment = commentService.deleteComment(id);
        discussionRepository.save(discussion);
    }

    @Override
    public CommentDTO updateComment(UUID commentId, CommentUpdateDTO commentUpdateDTO) {
        val discussionOptional = discussionRepository.findDiscussionByCommentId(commentId);

        if (discussionOptional.isEmpty()) {
            throw new DiscussionNotFoundException(
                    String.format("Discussion with comment id: %s not found", commentId));
        }

        val discussion = discussionOptional.get();

        val comments = discussionOptional.get().getComments();

        val updatedComment = commentService.updateComment(commentId, commentUpdateDTO);

        discussion.setComments(comments);
        discussionRepository.save(discussion);

        return updatedComment.toDTO();
    }
}
