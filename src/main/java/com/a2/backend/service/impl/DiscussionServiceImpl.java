package com.a2.backend.service.impl;

import com.a2.backend.entity.*;
import com.a2.backend.exception.DiscussionWithThatTitleExistsInProjectException;
import com.a2.backend.model.DiscussionCreateDTO;
import com.a2.backend.repository.DiscussionRepository;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.repository.TagRepository;
import com.a2.backend.service.DiscussionService;
import com.a2.backend.service.TagService;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
public class DiscussionServiceImpl implements DiscussionService {

    private final ProjectRepository projectRepository;
    private final DiscussionRepository discussionRepository;
    private final TagService tagService;
    private final TagRepository tagRepository;

    public DiscussionServiceImpl(
            ProjectRepository projectRepository,
            DiscussionRepository discussionRepository,
            TagService tagService,
            TagRepository tagRepository) {
        this.projectRepository = projectRepository;
        this.tagService = tagService;
        this.tagRepository = tagRepository;
        this.discussionRepository = discussionRepository;
    }

    @Override
    @Transactional
    public Discussion createDiscussion(UUID projectId, DiscussionCreateDTO discussionCreateDTO) {
        val existingDiscussionWithTitleInProject =
                discussionRepository.findByProjectIdAndTitle(
                        projectId, discussionCreateDTO.getTitle());
        if (existingDiscussionWithTitleInProject == null) {
            List<Tag> tags = tagService.findOrCreateTag(discussionCreateDTO.getTags());
            Discussion discussion =
                    Discussion.builder()
                            .title(discussionCreateDTO.getTitle())
                            .project(projectRepository.findById(projectId).get())
                            .tags(tags)
                            .build();
            return discussionRepository.save(discussion);
        }

        throw new DiscussionWithThatTitleExistsInProjectException(
                String.format(
                        "There is an existing discussion named %s in this project",
                        discussionCreateDTO.getTitle()));
    }
}
