package com.a2.backend.service.impl;

import com.a2.backend.entity.*;
import com.a2.backend.exception.DiscussionWithThatTitleExistsInProjectException;
import com.a2.backend.exception.ProjectNotFoundException;
import com.a2.backend.exception.UserIsNotCollaboratorNorOwnerException;
import com.a2.backend.model.DiscussionCreateDTO;
import com.a2.backend.repository.DiscussionRepository;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.service.DiscussionService;
import com.a2.backend.service.ForumTagService;
import com.a2.backend.service.UserService;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
public class DiscussionServiceImpl implements DiscussionService {

    private final ProjectRepository projectRepository;
    private final DiscussionRepository discussionRepository;
    private final ForumTagService forumTagService;
    private final UserService userService;

    public DiscussionServiceImpl(
            UserService userService,
            ProjectRepository projectRepository,
            DiscussionRepository discussionRepository,
            ForumTagService forumTagService) {
        this.forumTagService = forumTagService;
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.discussionRepository = discussionRepository;
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
                            .build();
            return discussionRepository.save(discussion);
        }

        throw new DiscussionWithThatTitleExistsInProjectException(
                String.format(
                        "There is an existing discussion named %s in this project",
                        discussionCreateDTO.getTitle()));
    }
}
