package com.a2.backend.service.impl;

import com.a2.backend.entity.Comment;
import com.a2.backend.entity.Discussion;
import com.a2.backend.entity.ForumTag;
import com.a2.backend.entity.User;
import com.a2.backend.exception.*;
import com.a2.backend.model.CommentCreateDTO;
import com.a2.backend.model.CommentDTO;
import com.a2.backend.model.DiscussionCreateDTO;
import com.a2.backend.model.DiscussionDTO;
import com.a2.backend.model.DiscussionUpdateDTO;
import com.a2.backend.repository.DiscussionRepository;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.service.CommentService;
import com.a2.backend.service.DiscussionService;
import com.a2.backend.service.ForumTagService;
import com.a2.backend.service.UserService;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
public class DiscussionServiceImpl implements DiscussionService {

    private final ProjectRepository projectRepository;
    private final DiscussionRepository discussionRepository;
    private final ForumTagService forumTagService;
    private final UserService userService;
    private final CommentService commentService;

    public DiscussionServiceImpl(
            ProjectRepository projectRepository,
            DiscussionRepository discussionRepository,
            ForumTagService forumTagService,
            UserService userService,
            CommentService commentService) {
        this.forumTagService = forumTagService;
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.discussionRepository = discussionRepository;
        this.commentService = commentService;
    }

    @Override
    @Transactional
    public DiscussionDTO createDiscussion(UUID projectId, DiscussionCreateDTO discussionCreateDTO) {
        User loggedUser = userService.getLoggedUser();
        val project = projectRepository.findById(projectId);
        if (!project.isPresent()) {
            throw new ProjectNotFoundException("Project not found with that ID");
        }
        if (!project.get().getCollaborators().contains(loggedUser)
                && !project.get().getOwner().getId().equals(loggedUser.getId())) {
            throw new UserIsNotCollaboratorNorOwnerException(
                    "User must be collaborator or owner to create a discussion");
        }

        val existingDiscussionWithTitleInProject =
                discussionRepository.findByProjectIdAndTitle(
                        projectId, discussionCreateDTO.getTitle());
        if (existingDiscussionWithTitleInProject == null) {
            List<ForumTag> tags =
                    forumTagService.findOrCreateTag(discussionCreateDTO.getForumTags());
            Discussion discussion =
                    Discussion.builder()
                            .title(discussionCreateDTO.getTitle())
                            .project(projectRepository.findById(projectId).get())
                            .forumTags(tags)
                            .comments(List.of())
                            .owner(loggedUser)
                            .build();
            Discussion updatedDiscussion = discussionRepository.save(discussion);

            return updatedDiscussion.toDTO();
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
                discussionRepository.findByProjectIdAndTitle(
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
        discussion.setForumTags(
                forumTagService.findOrCreateTag(discussionUpdateDTO.getForumTags()));
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
        if (!discussionRepository.getById(discussionID).getOwner().equals(loggedUser)) {
            throw new UserIsNotOwnerException(
                    String.format("User: %s is not the owner!", loggedUser.getNickname()));
        }
        if (project.isEmpty()) {
            throw new ProjectNotFoundException("Project does not exist!");
        }
        project.get().getDiscussions().remove(discussionToDelete.get());
        discussionRepository.deleteById(discussionID);
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
}
