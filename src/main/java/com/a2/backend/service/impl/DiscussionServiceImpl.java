package com.a2.backend.service.impl;

import com.a2.backend.entity.Discussion;
import com.a2.backend.entity.ForumTag;
import com.a2.backend.entity.User;
import com.a2.backend.exception.*;
import com.a2.backend.model.CommentCreateDTO;
import com.a2.backend.model.CommentDTO;
import com.a2.backend.model.DiscussionCreateDTO;
import com.a2.backend.repository.DiscussionRepository;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.service.CommentService;
import com.a2.backend.service.DiscussionService;
import com.a2.backend.service.ForumTagService;
import com.a2.backend.service.UserService;
import lombok.val;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

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
    public Discussion createDiscussion(UUID projectId, DiscussionCreateDTO discussionCreateDTO) {
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
                            .build();
            return discussionRepository.save(discussion);
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
}
