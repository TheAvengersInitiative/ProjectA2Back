package com.a2.backend.service.impl;

import com.a2.backend.entity.Project;
import com.a2.backend.exception.ProjectWithThatTitleExistsException;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.service.ProjectService;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public Project createProject(ProjectCreateDTO projectCreateDTO) {
        // TODO: verify owner is user that created the project
        val existingProjectWithTitle= projectRepository.findByTitle(projectCreateDTO.getTitle());
        if(existingProjectWithTitle.isEmpty()){
            Project project = Project.builder()
                    .title(projectCreateDTO.getTitle())
                    .description(projectCreateDTO.getDescription())
                    .owner(projectCreateDTO.getOwner())
                    .build();
            return projectRepository.save(project);
        }

        throw new ProjectWithThatTitleExistsException(String.format("There is an existing project named %s", projectCreateDTO.getTitle()));
    }
}
